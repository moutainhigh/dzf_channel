package com.dzf.service.channel.dealmanage.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.model.channel.dealmanage.GoodsDocVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.channel.dealmanage.MeasVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.dealmanage.IGoodsManageService;
import com.dzf.spring.SpringUtils;

@Service("goodsmanageser")
public class GoodsManageServiceImpl implements IGoodsManageService {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Integer queryTotalRow(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(GoodsVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsVO> query(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		List<GoodsVO> list = (List<GoodsVO>) multBodyObjectBO.queryDataPage(GoodsVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if(list != null && list.size() > 0){
			UserVO uservo = null;
			for(GoodsVO vo : list){
				uservo = UserCache.getInstance().get(vo.getCoperatorid(), null);
				if(uservo != null){
					vo.setVopername(uservo.getUser_name());
				}
			}
		}
		return list;
	}

	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n") ;
		sql.append("  FROM cn_goods g  \n") ; 
		sql.append(" WHERE nvl(g.dr, 0) = 0  \n") ; 
		if(pamvo.getVstatus() != null && pamvo.getVstatus() != -1){
			sql.append("   AND g.vstatus = ?  \n") ; 
			spm.addParam(pamvo.getVstatus());
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodscode())){
			sql.append("   AND g.vgoodscode like ?  \n") ; 
			spm.addParam(pamvo.getVgoodscode()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodsname())){
			sql.append("   AND g.vgoodsname like ?  \n") ; 
			spm.addParam(pamvo.getVgoodsname()+"%");
		}
		sql.append(" ORDER BY g.updatets DESC \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public GoodsVO save(GoodsVO datavo, File[] files, String[] filenames) throws DZFWarpException {
		if(StringUtil.isEmpty(datavo.getPk_goods())){
			return saveNew(datavo, files, filenames);
		}else{
			return saveEdit(datavo, files, filenames);
		}
	}
	
	/**
	 * 新增保存
	 * @param datavo
	 * @param files
	 * @param filenames
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsVO saveNew(GoodsVO datavo, File[] files, String[] filenames) throws DZFWarpException{
		datavo = (GoodsVO) singleObjectBO.saveObject(datavo.getPk_corp(), datavo);
		List<GoodsDocVO> doclist = new ArrayList<GoodsDocVO>();
		for(int i = 0; i < files.length; i++){
			String fname = System.nanoTime()  + filenames[i].substring(filenames[i].indexOf("."));
			String filepath = "";
			try {
				filepath = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).upload(files[i], filenames[i], null);
			} catch (AppException e) {
				throw new BusinessException("图片上传错误");
			}
			if(StringUtil.isEmpty(filepath)){
				throw new BusinessException("图片上传错误");
			}
			GoodsDocVO docvo = new GoodsDocVO();
			docvo.setPk_corp(datavo.getPk_corp());
			docvo.setPk_goods(datavo.getPk_goods());
			docvo.setDocName(filenames[i]);
			docvo.setDocTemp(fname);
			docvo.setVfilepath(filepath.substring(1));
			docvo.setDocOwner(datavo.getCoperatorid());
			docvo.setDocTime(String.valueOf(new Date().getTime()));
			docvo.setCoperatorid(datavo.getCoperatorid());
			docvo.setDoperatedate(new DZFDate());
			docvo.setDr(0);
			doclist.add(docvo);
		}
		if(doclist != null && doclist.size() > 0){
			singleObjectBO.insertVOArr(datavo.getPk_corp(), doclist.toArray(new GoodsDocVO[0]));
		}else{
			throw new BusinessException("图片上传错误");
		}
		return datavo;
	}
	
	/**
	 * 修改保存
	 * @param datavo
	 * @param files
	 * @param filenames
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsVO saveEdit(GoodsVO datavo, File[] files, String[] filenames) throws DZFWarpException{
		checkDataStatus(datavo);
		
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(datavo.getTableName(), datavo.getPk_goods(), uuid, 120);
			datavo = (GoodsVO) singleObjectBO.saveObject(datavo.getPk_corp(), datavo);
			List<GoodsDocVO> doclist = new ArrayList<GoodsDocVO>();
			for(int i = 0; i < files.length; i++){
				String fname = System.nanoTime()  + filenames[i].substring(filenames[i].indexOf("."));
				String filepath = "";
				try {
					filepath = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).upload(files[i], filenames[i], null);
				} catch (AppException e) {
					throw new BusinessException("图片上传错误");
				}
				if(StringUtil.isEmpty(filepath)){
					throw new BusinessException("图片上传错误");
				}
				GoodsDocVO docvo = new GoodsDocVO();
				docvo.setPk_corp(datavo.getPk_corp());
				docvo.setPk_goods(datavo.getPk_goods());
				docvo.setDocName(filenames[i]);
				docvo.setDocTemp(fname);
				docvo.setVfilepath(filepath.substring(1));
				docvo.setDocOwner(datavo.getCoperatorid());
				docvo.setDocTime(String.valueOf(new Date().getTime()));
				docvo.setCoperatorid(datavo.getCoperatorid());
				docvo.setDoperatedate(new DZFDate());
				docvo.setDr(0);
				doclist.add(docvo);
			}
			if(doclist != null && doclist.size() > 0){
				singleObjectBO.insertVOArr(datavo.getPk_corp(), doclist.toArray(new GoodsDocVO[0]));
			}else{
				throw new BusinessException("图片上传错误");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(datavo.getTableName(), datavo.getPk_goods(), uuid);
		}
		
		return datavo;
	}
	
	/**
	 * 数据状态校验
	 * @throws DZFWarpException
	 */
	private void checkDataStatus(GoodsVO datavo) throws DZFWarpException {
		GoodsVO oldvo = (GoodsVO) singleObjectBO.queryByPrimaryKey(GoodsVO.class, datavo.getPk_goods());
		if(oldvo != null){
			if(oldvo.getUpdatets().compareTo(datavo.getUpdatets()) != 0){
				throw new BusinessException("请刷新界面数据，再次尝试");
			}
		}else{
			throw new BusinessException("数据错误");
		}
	}

	@Override
	public List<ComboBoxVO> queryMeasCombox(String pk_corp) throws DZFWarpException {
		String sql = " nvl(dr,0) = 0 AND pk_corp = ? \n";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_corp);
		MeasVO[] measVOs = (MeasVO[]) singleObjectBO.queryByCondition(MeasVO.class, sql, spm);
		List<ComboBoxVO> list = new ArrayList<ComboBoxVO>();
		if(measVOs != null && measVOs.length > 0){
			ComboBoxVO boxvo = null;
			for(MeasVO vo : measVOs){
				boxvo = new ComboBoxVO();
				boxvo.setId(vo.getPk_measdoc());
				boxvo.setName(vo.getVmeasname());
				list.add(boxvo);
			}
		}
		return list;
	}

	@Override
	public MeasVO saveMeas(GoodsVO pamvo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_measdoc", pamvo.getVmeasname(), uuid, 120);
			MeasVO measvo = new MeasVO();
			measvo.setPk_corp(pamvo.getPk_corp());
			measvo.setVmeasname(pamvo.getVmeasname());
			measvo.setCoperatorid(pamvo.getCoperatorid());
			measvo.setDoperatedate(new DZFDate());
			measvo.setDr(0);
			return (MeasVO) singleObjectBO.saveObject(measvo.getPk_corp(), measvo);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_measdoc", pamvo.getVmeasname(), uuid);
		}
	}

	@Override
	public GoodsVO queryByID(GoodsVO pamvo) throws DZFWarpException {
		GoodsVO oldvo = (GoodsVO) singleObjectBO.queryByPrimaryKey(GoodsVO.class, pamvo.getPk_goods());
		if(oldvo == null){
			throw new BusinessException("数据错误");
		}
		return oldvo;
	}

	@Override
	public GoodsDocVO[] getAttatches(GoodsVO pamvo) throws DZFWarpException {
		String sql = " nvl(dr,0) = 0 AND pk_corp = ? AND pk_goods = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pamvo.getPk_corp());
		spm.addParam(pamvo.getPk_goods());
		return (GoodsDocVO[]) singleObjectBO.queryByCondition(GoodsDocVO.class, sql, spm);
	}

	@Override
	public void deleteFile(GoodsDocVO pamvo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(pamvo.getTableName(), pamvo.getPk_goodsdoc(), uuid, 120);
			GoodsDocVO oldvo = (GoodsDocVO) singleObjectBO.queryByPrimaryKey(GoodsDocVO.class, pamvo.getPk_goodsdoc());
			if(oldvo == null){
				throw new BusinessException("商品图片信息错误");
			}
			if(StringUtil.isEmpty(oldvo.getVfilepath())){
				throw new BusinessException("商品图片信息错误");
			}
			try {
				((FastDfsUtil) SpringUtils.getBean("connectionPool")).deleteFile(oldvo.getVfilepath());
			} catch (AppException e) {
				throw new BusinessException("商品图片删除失败");
			}
			String sql = " delete from cn_goodsdoc where pk_goodsdoc = ? and pk_corp = ? ";
			SQLParameter spm = new SQLParameter();
			spm.addParam(pamvo.getPk_goodsdoc());
			spm.addParam(pamvo.getPk_corp());
			int res = singleObjectBO.executeUpdate(sql, spm);
			if(res != 1){
				throw new BusinessException("商品图片删除失败");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(pamvo.getTableName(), pamvo.getPk_goodsdoc(), uuid);
		}
	}

	@Override
	public void delete(GoodsVO pamvo) throws DZFWarpException {
		checkDataStatus(pamvo);
		String sql = " nvl(dr,0) = 0 AND pk_corp = ? AND pk_goods = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pamvo.getPk_corp());
		spm.addParam(pamvo.getPk_goods());
		GoodsDocVO[] docVOs = (GoodsDocVO[]) singleObjectBO.queryByCondition(GoodsDocVO.class, sql, spm);
		if(docVOs != null && docVOs.length > 0){
			for(GoodsDocVO docvo : docVOs){
				if(StringUtil.isEmpty(docvo.getVfilepath())){
					throw new BusinessException("商品图片错误");
				}
				try {
					((FastDfsUtil) SpringUtils.getBean("connectionPool")).deleteFile(docvo.getVfilepath());
				} catch (AppException e) {
					throw new BusinessException("商品图片删除失败");
				}
			}
			sql = " DELETE FROM cn_goodsdoc WHERE pk_corp = ? AND pk_goods = ? ";
			spm = new SQLParameter();
			spm.addParam(pamvo.getPk_corp());
			spm.addParam(pamvo.getPk_goods());
			int res = singleObjectBO.executeUpdate(sql, spm);
			if(res == 0){
				throw new BusinessException("商品图片信息删除失败");
			}
		}
		sql = " DELETE FROM cn_goods WHERE pk_corp = ? AND pk_goods = ? ";
		spm = new SQLParameter();
		spm.addParam(pamvo.getPk_corp());
		spm.addParam(pamvo.getPk_goods());
		int res = singleObjectBO.executeUpdate(sql, spm);
		if(res == 0){
			throw new BusinessException("商品信息删除失败");
		}
	}

	@Override
	public GoodsDocVO queryGoodsDocById(GoodsDocVO pamvo) throws DZFWarpException {
		GoodsDocVO docvo = (GoodsDocVO) singleObjectBO.queryByPrimaryKey(GoodsDocVO.class, pamvo.getPk_goodsdoc());
		if(docvo == null){
			throw new BusinessException("商品图片信息查询错误");
		}
		return docvo;
	}
	
}
