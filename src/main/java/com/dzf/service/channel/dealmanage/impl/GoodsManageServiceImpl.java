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
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.GoodsDocVO;
import com.dzf.model.channel.dealmanage.GoodsSpecVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.channel.dealmanage.MeasVO;
import com.dzf.model.channel.dealmanage.StockInBVO;
import com.dzf.model.channel.stock.StockNumVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IGoodsManageService;
import com.dzf.service.pub.IBillCodeService;
import com.dzf.spring.SpringUtils;

@Service("goodsmanageser")
public class GoodsManageServiceImpl implements IGoodsManageService {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IBillCodeService billCodeSer;

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
			String vstaname = "";
			String where = "";
			List<String> pklist = new ArrayList<String>();
			for(GoodsVO gvo : list){
				pklist.add(gvo.getPk_goods());
			}
			if(pklist != null && pklist.size() > 0){
				where = SqlUtil.buildSqlForIn("pk_goods", pklist.toArray(new String[0]));
			}
			List<String> gslist = queryStockGoods(where);
			for(GoodsVO vo : list){
				uservo = UserCache.getInstance().get(vo.getCoperatorid(), null);
				if(uservo != null){
					vo.setVopername(uservo.getUser_name());
				}
				switch(vo.getVstatus()){
					case 1 :
						vstaname = "已保存";
						break;
					case 2 :
						vstaname = "已发布";
						break;
					case 3 :
						vstaname = "已下架";
						break;
				}
				vo.setVstaname(vstaname);
				if(gslist != null && gslist.size() > 0){
					if(gslist.contains(vo.getPk_goods())){
						vo.setIsstockin(DZFBoolean.TRUE);
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 查询已入库的商品主键
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<String> queryStockGoods(String where) throws DZFWarpException {
		List<String> list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT pk_goods  \n") ;
		sql.append("  FROM cn_stockin_b  \n") ; 
		sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
		if(!StringUtil.isEmpty(where)){
			sql.append("   AND  ").append(where);
		}
		List<GoodsVO> glist = (List<GoodsVO>) singleObjectBO.executeQuery(sql.toString(), null, new BeanListProcessor(GoodsVO.class));
		if(glist != null && glist.size() > 0){
			for(GoodsVO gvo : glist){
				list.add(gvo.getPk_goods());
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
		sql.append("SELECT g.*, t.vname AS vgoodstypename \n") ;
		sql.append("  FROM cn_goods g  \n") ; 
		sql.append("  LEFT JOIN cn_goodstype t ON g.pk_goodstype = t.pk_goodstype \n");
		sql.append(" WHERE nvl(g.dr, 0) = 0  \n") ; 
		if(pamvo.getVstatus() != null && pamvo.getVstatus() != -1){
			sql.append("   AND g.vstatus = ?  \n") ; 
			spm.addParam(pamvo.getVstatus());
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodscode())){
			sql.append("   AND g.vgoodscode like ?  \n") ; 
			spm.addParam("%"+pamvo.getVgoodscode()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodsname())){
			sql.append("   AND g.vgoodsname like ?  \n") ; 
			spm.addParam("%"+pamvo.getVgoodsname()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getPk_goodstype())){
			sql.append(" AND g.pk_goodstype = ? \n");
			spm.addParam(pamvo.getPk_goodstype());
		}
		sql.append(" ORDER BY g.ts DESC \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public GoodsVO save(GoodsVO datavo, File[] files, String[] filenames) throws DZFWarpException {
		if(StringUtil.isEmpty(datavo.getPk_goods())){
			datavo = saveNew(datavo, files, filenames);
		}else{
			datavo = saveEdit(datavo, files, filenames);
		}
		UserVO uservo = UserCache.getInstance().get(datavo.getCoperatorid(), null);
		if(uservo != null){
			datavo.setVopername(uservo.getUser_name());
		}
		return datavo;
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
		if (StringUtil.isEmptyWithTrim(datavo.getVgoodscode())) {
			String code = getVgoodscode();
			datavo.setVgoodscode(code);
		}else{
			datavo.setVgoodscode(datavo.getVgoodscode().replaceAll(" ", ""));
		}
		if (isCoodExist(datavo)) {
			throw new BusinessException("商品编码"+datavo.getVgoodscode()+"已经存在");
		}
		datavo = (GoodsVO) singleObjectBO.saveObject(datavo.getPk_corp(), datavo);
		List<GoodsDocVO> doclist = new ArrayList<GoodsDocVO>();
		for(int i = 0; i < files.length; i++){
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
			docvo.setDocTemp(filepath.substring(filepath.lastIndexOf("/")+1));
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
	
	@SuppressWarnings("unchecked")
	private boolean isCoodExist(GoodsVO pamvo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vgoodscode from cn_goods where nvl(dr,0) = 0 ");
		if (!StringUtil.isEmptyWithTrim(pamvo.getVgoodscode())) {
			sql.append(" and vgoodscode = ? ");
			sp.addParam(pamvo.getVgoodscode());
		} else {
			throw new BusinessException("商品编码不能为空");
		}
		if (!StringUtil.isEmpty(pamvo.getPk_goods())) {
			sql.append(" and pk_goods != ? ");
			sp.addParam(pamvo.getPk_goods());
		}
		List<GoodsVO> list = (List<GoodsVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(GoodsVO.class));
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取商品编码
	 * @return
	 * @throws DZFWarpException
	 */
	private String getVgoodscode() throws DZFWarpException {
		String code;
		DZFDate date = new DZFDate();
		String year = String.valueOf(date.getYear());
		String str = "SP" + year;
		MaxCodeVO mcvo = new MaxCodeVO();
		mcvo.setTbName("cn_goods");
		mcvo.setFieldName("vgoodscode");
		mcvo.setPk_corp("000001");
		mcvo.setBillType(str);
		mcvo.setCorpIdField("pk_corp");
		mcvo.setDiflen(4);
		try{
			code = billCodeSer.getDefaultCode(mcvo);
		}catch(Exception e) {
			throw new BusinessException("获取商品编码失败");
		}
		return code;
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
		checkDataStatus(datavo, 1);
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(datavo.getTableName(), datavo.getPk_goods(), uuid, 120);
			datavo = (GoodsVO) singleObjectBO.saveObject(datavo.getPk_corp(), datavo);
			List<GoodsDocVO> doclist = new ArrayList<GoodsDocVO>();
			if(files != null && files.length > 0){
				for(int i = 0; i < files.length; i++){
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
					docvo.setDocTemp(filepath.substring(filepath.lastIndexOf("/")+1));
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
				}
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
	 * @param datavo
	 * @param type  0：修改查询；1：修改保存；2：删除；3：发布；4：下架；
	 * @throws DZFWarpException
	 */
	private void checkDataStatus(GoodsVO datavo, Integer type) throws DZFWarpException {
		GoodsVO oldvo = (GoodsVO) singleObjectBO.queryByPrimaryKey(GoodsVO.class, datavo.getPk_goods());
		if(oldvo != null){
			if(oldvo.getUpdatets().compareTo(datavo.getUpdatets()) != 0){
				throw new BusinessException("请刷新界面数据，再次尝试");
			}
			if(type == 3){
				if(oldvo.getVstatus() == IStatusConstant.IGOODSSTATUS_2){
					throw new BusinessException("商品："+oldvo.getVgoodsname()+"已经发布  ");
				}
			}else if(type == 4){
				if(oldvo.getVstatus() == IStatusConstant.IGOODSSTATUS_3){
					throw new BusinessException("商品："+oldvo.getVgoodsname()+"已经下架  ");
				}
				if(oldvo.getVstatus() == IStatusConstant.IGOODSSTATUS_1){
					throw new BusinessException("商品："+oldvo.getVgoodsname()+"未发布，不能下架  ");
				}
			}
		}else{
			throw new BusinessException("数据错误");
		}
	}

	@Override
	public List<ComboBoxVO> queryMeasCombox(String pk_corp) throws DZFWarpException {
		String sql = " nvl(dr,0) = 0 AND pk_corp = ? ORDER BY ts DESC \n";
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
			boolean flag = isMeasExist(pamvo);
			if(flag){
				throw new BusinessException("计量单位"+pamvo.getVmeasname()+"已经存在");
			}
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
	
	@SuppressWarnings("unchecked")
	private boolean isMeasExist(GoodsVO pamvo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vmeasname from cn_measdoc where nvl(dr,0) = 0 ");
		if (!StringUtil.isEmptyWithTrim(pamvo.getVmeasname())) {
			sql.append(" and vmeasname = ? ");
			sp.addParam(pamvo.getVmeasname());
		} else {
			throw new BusinessException("计量单位不能为空");
		}
		List<GoodsVO> list = (List<GoodsVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(GoodsVO.class));
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public GoodsVO queryByID(GoodsVO pamvo, Integer itype) throws DZFWarpException {
		// itype 0：修改查询；1：详情查询；
		GoodsVO oldvo = (GoodsVO) singleObjectBO.queryByPrimaryKey(GoodsVO.class, pamvo.getPk_goods());
		if(oldvo == null){
			throw new BusinessException("数据错误");
		}
		if(itype == 0){//修改查询
			if(oldvo.getVstatus() == IStatusConstant.IGOODSSTATUS_2){
				throw new BusinessException("该商品已经发布，不允许修改");
			}
		}
		String where = " pk_goods = '" + oldvo.getPk_goods()+"'";
		List<String> gslist = queryStockGoods(where);
		if(gslist != null && gslist.size() > 0){
			if(gslist.contains(oldvo.getPk_goods())){
				oldvo.setIsstockin(DZFBoolean.TRUE);
			}
		}
		
		String vstaname = "";
		switch(oldvo.getVstatus()){
			case 1 :
				vstaname = "已保存";
				break;
			case 2 :
				vstaname = "已发布";
				break;
			case 3 :
				vstaname = "已下架";
				break;
		}
		oldvo.setVstaname(vstaname);
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
		String pk_goods = "";
		try {
			GoodsDocVO oldvo = (GoodsDocVO) singleObjectBO.queryByPrimaryKey(GoodsDocVO.class, pamvo.getPk_goodsdoc());
			if(oldvo == null){
				throw new BusinessException("商品图片信息错误");
			}
			if(StringUtil.isEmpty(oldvo.getVfilepath())){
				throw new BusinessException("商品图片信息错误");
			}
			pk_goods = oldvo.getPk_goods();
			LockUtil.getInstance().tryLockKey("cn_goods", pk_goods, uuid, 120);
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
			LockUtil.getInstance().unLock_Key("cn_goods", pk_goods, uuid);
		}
	}

	@Override
	public void delete(GoodsVO pamvo) throws DZFWarpException {
		checkDataStatus(pamvo, 2);
		checkIsBeUsed(pamvo);
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(pamvo.getTableName(), pamvo.getPk_goods(), uuid, 60);
			String sql = " nvl(dr,0) = 0 AND pk_corp = ? AND pk_goods = ? ";
			SQLParameter spm = new SQLParameter();
			spm.addParam(pamvo.getPk_corp());
			spm.addParam(pamvo.getPk_goods());
			GoodsDocVO[] docVOs = (GoodsDocVO[]) singleObjectBO.queryByCondition(GoodsDocVO.class, sql, spm);
			if(docVOs != null && docVOs.length > 0){
				//1、删除商品图片
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
				//2、删除商品图片信息
				sql = " DELETE FROM cn_goodsdoc WHERE pk_corp = ? AND pk_goods = ? ";
				spm = new SQLParameter();
				spm.addParam(pamvo.getPk_corp());
				spm.addParam(pamvo.getPk_goods());
				int res = singleObjectBO.executeUpdate(sql, spm);
				if(res == 0){
					throw new BusinessException("商品图片信息删除失败");
				}
			}
			//3、删除商品规格型号
			sql = " DELETE FROM cn_goodsspec WHERE pk_corp = ? AND pk_goods = ? ";
			spm = new SQLParameter();
			spm.addParam(pamvo.getPk_corp());
			spm.addParam(pamvo.getPk_goods());
			int res = singleObjectBO.executeUpdate(sql, spm);
			
			//4、删除商品信息
			sql = " DELETE FROM cn_goods WHERE pk_corp = ? AND pk_goods = ? ";
			spm = new SQLParameter();
			spm.addParam(pamvo.getPk_corp());
			spm.addParam(pamvo.getPk_goods());
			res = singleObjectBO.executeUpdate(sql, spm);
			if(res == 0){
				throw new BusinessException("商品信息删除失败");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(pamvo.getTableName(), pamvo.getPk_goods(), uuid);
		}
	}
	
	/**
	 * 检查商品是否被
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	private void checkIsBeUsed(GoodsVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_goods  \n") ;
		sql.append("  FROM cn_stockin_b  \n") ; 
		sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
		sql.append("   AND pk_goods = ?  \n");
		spm.addParam(pamvo.getPk_goods());
		sql.append("   AND pk_corp = ?  \n");
		spm.addParam(pamvo.getPk_corp());
		boolean flag = singleObjectBO.isExists(pamvo.getPk_corp(), sql.toString(), spm);
		if(flag){
			throw new BusinessException("商品"+pamvo.getVgoodsname()+"已经被入库单引用，不允许删除");
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

	@Override
	public GoodsVO updateData(GoodsVO pamvo, Integer itype) throws DZFWarpException {
		if (itype == 1) {
			checkDataStatus(pamvo, 3);
		} else if (itype == 2) {
			checkDataStatus(pamvo, 4);
		}

		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(pamvo.getTableName(), pamvo.getPk_goods(), uuid, 60);
			List<String> strlist = new ArrayList<String>();
			strlist.add("vstatus");
			if(itype == 1){//发布
				CheckStockNum(pamvo);
				pamvo.setVstatus(IStatusConstant.IGOODSSTATUS_2);
				pamvo.setUpdatets(new DZFDateTime());
				pamvo.setDpublishdate(new DZFDate());
				pamvo.setDoffdate(null);
				strlist.add("dpublishdate");
				strlist.add("doffdate");
			}else if(itype == 2){//下架
				pamvo.setVstatus(IStatusConstant.IGOODSSTATUS_3);
				pamvo.setUpdatets(new DZFDateTime());
				pamvo.setDoffdate(new DZFDate());
				strlist.add("doffdate");
			}
			singleObjectBO.update(pamvo, strlist.toArray(new String[0]));
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(pamvo.getTableName(), pamvo.getPk_goods(), uuid);
		}
		return null;
	}
	
	/**
	 * 发布前可售卖数量检查
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void CheckStockNum(GoodsVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT sum(nvl(istocknum,0) - nvl(isellnum,0)) AS istocknum \n");
		sql.append("  FROM cn_stocknum \n");
		sql.append(" WHERE nvl(dr, 0) = 0 \n");
		sql.append("   AND pk_goods = ? \n");
		sql.append("   GROUP BY pk_goods \n");
		spm.addParam(pamvo.getPk_goods());
		List<StockNumVO> list = (List<StockNumVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(StockNumVO.class));
		if(list != null && list.size() > 0){
			if(list.get(0).getIstocknum() == 0){
				throw new BusinessException("商品"+pamvo.getVgoodsname()+"可售卖数量不足，请补充库存后，再发布！");
			}
		}else{
			throw new BusinessException("商品"+pamvo.getVgoodsname()+"可售卖数量不足，请补充库存后，再发布！");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsBoxVO> queryComboBox() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT c.pk_goodsspec AS id,  \n") ;
		sql.append("       s.vgoodsname ||' '|| '(' || c.invspec || c.invtype || ')' AS name,  \n") ; 
		sql.append("       c.pk_goods,  \n") ; 
		sql.append("       c.invspec,  \n") ; 
		sql.append("       c.invtype  \n") ; 
		sql.append("  FROM cn_goodsspec c  \n") ; 
		sql.append("  LEFT JOIN cn_goods s ON c.pk_goods = s.pk_goods  \n") ; 
		sql.append(" WHERE nvl(c.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(s.dr, 0) = 0 \n");
		return (List<GoodsBoxVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(GoodsBoxVO.class));
	}

	@Override
	public GoodsVO saveSet(List<GoodsSpecVO> blist, String pk_corp) throws DZFWarpException {
		if(blist == null || blist.size() == 0){
			throw new BusinessException("操作数据不能为空");
		}
		String pk_goods = blist.get(0).getPk_goods();
		
//		DZFDouble nprice = DZFDouble.ZERO_DBL;
//		int i = 0;
//		for(GoodsSpecVO svo : blist){
//			if(svo.getDr() == null || (svo.getDr() != null && svo.getDr() == 0)){
//				if(i == 0){
//					nprice = svo.getNprice();
//				}else if(nprice.compareTo(svo.getNprice()) > 0){
//					nprice = svo.getNprice();
//				}
//				i++;
//			}
//		}
		GoodsVO gvo = new GoodsVO();
		gvo.setPk_goods(pk_goods);
		
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(gvo.getTableName(), gvo.getPk_goods(), uuid, 120);
			
			gvo = queryByID(gvo, 1);
//			gvo.setNprice(nprice);
			gvo.setChildren(blist.toArray(new GoodsSpecVO[0]));
			gvo = (GoodsVO) singleObjectBO.saveObject(pk_corp, gvo);
			String vstaname = "";
			switch(gvo.getVstatus()){
				case 1 :
					vstaname = "已保存";
					break;
				case 2 :
					vstaname = "已发布";
					break;
				case 3 :
					vstaname = "已下架";
					break;
			}
			gvo.setVstaname(vstaname);
			return gvo;
			
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(gvo.getTableName(), gvo.getPk_goods(), uuid);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsSpecVO> queryGoodsSet(String pk_goods) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n");
		sql.append("  FROM cn_goodsspec  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_goods = ? \n");
		spm.addParam(pk_goods);
		List<GoodsSpecVO> retlist = (List<GoodsSpecVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(GoodsSpecVO.class));
		if(retlist != null && retlist.size() > 0){
			List<String> pklist = queryBeUsedPk(pk_goods);
			for(GoodsSpecVO cvo : retlist){
				if(pklist.contains(cvo.getPk_goodsspec())){
					cvo.setIsbeused(DZFBoolean.TRUE);
				}
			}
		}
		return retlist;
	}
	
	/**
	 * 查询被入库单子表引用的规格主键
	 * @param pk_goods
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<String> queryBeUsedPk(String pk_goods) throws DZFWarpException {
		List<String> list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT pk_goodsspec  \n");
		sql.append("  FROM cn_stockin_b  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_goods = ? \n");
		spm.addParam(pk_goods);
		List<StockInBVO> stlist = (List<StockInBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(StockInBVO.class));
		if (stlist != null && stlist.size() > 0) {
			for (StockInBVO bvo : stlist) {
				list.add(bvo.getPk_goodsspec());
			}
		}
		return list;
	}
	
}
