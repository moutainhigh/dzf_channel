package com.dzf.service.channel.matmanage.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatStockInService;
import com.dzf.service.pub.IBillCodeService;


@Service("matstockin")
public class MatStockInServiceImpl implements IMatStockInService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IBillCodeService billCode;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MaterielFileVO> queryComboBox()   throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pk_materiel ,vname  \n");
		sql.append("  FROM cn_materiel  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append(" order by doperatetime desc \n");
		return (List<MaterielFileVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(MaterielFileVO.class));
	}

	@Override
	public void saveStockIn(MaterielStockInVO data, UserVO uservo)  throws DZFWarpException {
		
		if (StringUtil.isEmpty(data.getPk_materielin())) {
			 data.setVbillcode((getMatcode(data)));
			 setDefaultValue(data,uservo);
			 
			 StringBuffer sql = new StringBuffer();
			 SQLParameter spm=new SQLParameter();
			 spm.addParam(data.getPk_materiel());
			 sql.append("   select vname,vunit \n ");
			 sql.append("         from cn_materiel \n");
			 sql.append("         where nvl(dr,0)=0 and pk_materiel =? \n");
			 MaterielFileVO mvo = (MaterielFileVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielFileVO.class));
			 if(mvo!=null){
				 data.setVname(mvo.getVname());
				 data.setVunit(mvo.getVunit());
			 }
			 
			 data=(MaterielStockInVO) singleObjectBO.insertVO("000001", data);
			 
			 //修改物料档案入库数量
			 if(!StringUtil.isEmpty(data.getPk_materiel())){
		    		MaterielFileVO mfvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, data.getPk_materiel());
			    	if(mfvo!=null && mfvo.getIntnum()!=null){
			    		mfvo.setIntnum(mfvo.getIntnum()+data.getNnum());
			    	}
			    	String[] updates = {"intnum"};
			    	singleObjectBO.update(mfvo, updates);
		    	}
		} else {
			 saveEdit(data);
		}
	}
	
    private void saveEdit(MaterielStockInVO data)  throws DZFWarpException {
    	
    	Isintnum(data);
    	//获取修改前的值
    	MaterielStockInVO mvo = (MaterielStockInVO) singleObjectBO.queryByPrimaryKey(MaterielStockInVO.class, data.getPk_materielin());
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielin(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("单据编码：" + data.getVbillcode() + ",其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_materielin(), data.getUpdatets());
			
			String[] updates = {"vmemo", "stockdate","ncost","nnum","ntotalmny" };
		    singleObjectBO.update(data, updates);
		    
		    //修改入库数量
		    MaterielFileVO vo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, data.getPk_materiel());
		    if(mvo!=null && mvo.getNnum()!=null){
		    	if(vo!=null && vo.getIntnum()!=null){
		    		Integer newintnum =  vo.getIntnum() - mvo.getNnum() + data.getNnum();
		    		vo.setIntnum(newintnum);
		    		String[] nupdatets = {"intnum"};
		    		singleObjectBO.update(vo,nupdatets );
		    	}
		    	
		    }
		    
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielin(), uuid);
		}
}

    /**
	 * 检查是否是最新数据
	 * @param pk_materielin
	 * @param updatets
	 * @return
	 */
	private MaterielStockInVO checkData(String pk_materielin, DZFDateTime updatets)  throws DZFWarpException {
		MaterielStockInVO vo = (MaterielStockInVO) singleObjectBO.queryByPrimaryKey(MaterielStockInVO.class, pk_materielin);
		if (!updatets.equals(vo.getUpdatets())) {
			throw new BusinessException("单据编码：" + vo.getVbillcode() + ",数据已发生变化;<br>");
		}
		return vo;
	}

	
	/**
	 * 获取单据编码
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private String getMatcode(MaterielStockInVO vo) throws DZFWarpException {
		String code;
		String str = "WLRK";
		MaxCodeVO mcvo = new MaxCodeVO();
		mcvo.setTbName(vo.getTableName());
		mcvo.setFieldName("vbillcode");
		mcvo.setPk_corp("000001");
		mcvo.setBillType(str);
		mcvo.setCorpIdField("pk_corp");
		mcvo.setDiflen(4);
		try {
			code = billCode.getDefaultCode(mcvo);
		} catch (Exception e) {
			throw new BusinessException("获取单据编码失败");
		}
		return code;
	}
	
	/**
	 * 设置默认值
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(MaterielStockInVO data,UserVO uservo) throws DZFWarpException {
		data.setCoperatorid(uservo.getCuserid());
		data.setDoperatetime(new DZFDateTime());
		data.setPk_corp("000001");
	}

	@Override
	public int queryTotalRow(MaterielStockInVO qvo)   throws DZFWarpException{
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	     return multBodyObjectBO.queryDataTotal(MaterielFileVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	private QrySqlSpmVO getQrySqlSpm(MaterielStockInVO pamvo)  throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("   SELECT r.pk_materielin,r.vbillcode,r.ntotalmny,r.stockdate, \n");
		sql.append("         r.vmemo,r.coperatorid,r.doperatetime, \n  ");
		sql.append("         r.vname,r.vunit,r.nnum,r.ncost \n");
		sql.append("         from  cn_materielin r \n");
		sql.append("         where nvl(r.dr,0) =0  \n");
		if (!StringUtil.isEmpty(pamvo.getPk_materiel())) {
			sql.append(" AND  r.pk_materiel = ? \n");
			spm.addParam(pamvo.getPk_materiel());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getBegindate())) {
			sql.append(" and substr(r.doperatetime,0,10) >= ? \n");
			spm.addParam(pamvo.getBegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getEnddate())) {
			sql.append(" and substr(r.doperatetime,0,10) <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" order by r.doperatetime desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielStockInVO> query(MaterielStockInVO qvo,UserVO uservo)  throws DZFWarpException {
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	        List<MaterielStockInVO> list = (List<MaterielStockInVO>) multBodyObjectBO.queryDataPage(MaterielStockInVO.class, sqpvo.getSql(),
	                sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
	       if(list!=null && list.size()>0){
	    	   for (MaterielStockInVO mvo : list) {
		            uservo = UserCache.getInstance().get(mvo.getCoperatorid(), null);
					if(uservo!=null){
						mvo.setOpername(uservo.getUser_name());
					}
				}
		        QueryDeCodeUtils.decKeyUtils(new String[] { "opername" }, list, 1);
		        return list;
	       }
	       
	       return list;
	        
	}

	@Override
	public MaterielStockInVO queryDataById(String id)  throws DZFWarpException {

		MaterielStockInVO vo = (MaterielStockInVO) singleObjectBO.queryByPrimaryKey(MaterielStockInVO.class, id);
		return vo;
	}

	@Override
	public void delete(MaterielStockInVO data)  throws DZFWarpException {
		
		IsDele(data);
		MaterielFileVO fvo = new MaterielFileVO();
		Integer num = null;
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielin(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("单据编码：" + data.getVbillcode() + ",其他用户正在操作此数据;<br>");
			}
			SQLParameter spm = new SQLParameter();
			spm.addParam(data.getPk_materielin());
			
			MaterielStockInVO msvo = (MaterielStockInVO) singleObjectBO.queryByPrimaryKey(MaterielStockInVO.class, data.getPk_materielin());
			if(msvo!=null){
				//修改入库数量
				fvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, msvo.getPk_materiel());
				if(fvo!=null){
					if(msvo.getNnum()!=null && fvo.getIntnum()!=null){
						fvo.setIntnum(fvo.getIntnum() - msvo.getNnum());
						String[] updates = {"intnum"};
				    	singleObjectBO.update(fvo, updates);
					}
				}
			}
			String sql="delete from cn_materielin where pk_materielin = ? \n";
			int i = singleObjectBO.executeUpdate(sql, spm);
			if(i == 0 && fvo!=null){
				fvo.setOutnum(num);
				throw new BusinessException("入库单删除失败");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielin(), uuid);
		}
	}
	
	

	/**
	 * 判断是否可以入库
	 * @param data
	 */
	private void Isintnum(MaterielStockInVO data)  throws DZFWarpException{
		
		if (!StringUtil.isEmpty(data.getPk_materiel())) {
    		MaterielFileVO mfvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, data.getPk_materiel());
    		MaterielStockInVO mmvo = (MaterielStockInVO) singleObjectBO.queryByPrimaryKey(MaterielStockInVO.class, data.getPk_materielin());
    		MaterielStockInVO vo = rkCount(data);
    		if(vo!=null && vo.getCount()==1){//只有一条入库单
    			if(mfvo.getOutnum()!=null){
    				if(data.getNnum()<mfvo.getOutnum()){
        				throw new BusinessException("该物料入库数量不可小于已发货数量");
        			}
    			}
    		}else{
    			Integer snum = null;//剩余的
    			if(mfvo.getIntnum()!=null && mmvo.getNnum()!=null){
    				snum = mfvo.getIntnum() - mmvo.getNnum();
    			}
    			if(data.getNnum()!=null && mfvo.getOutnum()!=null){
    			   if(snum + data.getNnum() < mfvo.getOutnum()){
    				   throw new BusinessException("该物料入库数量不可小于已发货数量");
    			   }
    			}
    		}
    	}
	}
	
	/**
	 * 判断是否可以删除
	 * @param pk_materiel
	 */
	private void IsDele(MaterielStockInVO data)  throws DZFWarpException {
		
		MaterielStockInVO msvo = (MaterielStockInVO) singleObjectBO.queryByPrimaryKey(MaterielStockInVO.class, data.getPk_materielin());
		MaterielStockInVO  svo = rkCount(msvo);
		MaterielFileVO vo = new MaterielFileVO();
		if(msvo.getPk_materiel()!=null){
			vo	= (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, msvo.getPk_materiel());
		}
		if(svo!=null && svo.getCount()!=null ){
			if(svo.getCount() == 1){//只有一条入库单，不可以删除
				if(vo.getOutnum()!=null && vo.getOutnum()>0){
					throw new BusinessException("该物料已发货，不可删除");
				}
			}else if(svo.getCount() > 1){
				if(vo!=null && msvo!=null){
					if(vo.getIntnum()!=null && msvo.getNnum()!=null){
						Integer snum = null;//剩余的数量
						snum = vo.getIntnum() - msvo.getNnum();
						if(vo.getOutnum()!=null){
							if(snum < vo.getOutnum()){
								throw new BusinessException("该物料入库数量不可小于已发货数量");
							}
						}
						
					}
				}
				
			}
		}
		
	}
	
	/**
	 * 判断入库单数量
	 * @param msvo
	 * @return
	 */
	private MaterielStockInVO rkCount(MaterielStockInVO msvo){
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if(msvo.getPk_materiel()!=null){
			spm.addParam(msvo.getPk_materiel());
		}
		sql.append("  select count(pk_materielin) count \n");
		sql.append("      from cn_materielin \n ");
		sql.append("      where nvl(dr,0) = 0  ");
		sql.append("      and pk_materiel = ?  \n ");
		MaterielStockInVO svo = (MaterielStockInVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielStockInVO.class));
	    return svo;
	}
}


