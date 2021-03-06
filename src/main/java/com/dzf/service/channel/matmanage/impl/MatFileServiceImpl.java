package com.dzf.service.channel.matmanage.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.matmanage.IMatFileService;
import com.dzf.service.pub.IBillCodeService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("matfile")
public class MatFileServiceImpl implements IMatFileService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IBillCodeService billCodeSer;
	
	@Autowired
    private IUserService userser;
	
	@Override
	public Boolean queryMatName(String name)  throws DZFWarpException{
		String sql="select * from cn_materiel where nvl(dr,0)=0 and vname=? ";
		SQLParameter spm=new SQLParameter();
		spm.addParam(name);
		MaterielFileVO vo= (MaterielFileVO) singleObjectBO.executeQuery(sql, spm, new BeanProcessor(MaterielFileVO.class));
		if(vo==null){
			return true;
	    }
		return false;
	 }

	@Override
	public void saveMatFile(MaterielFileVO data,UserVO uservo)  throws DZFWarpException{
		if (StringUtil.isEmpty(data.getPk_materiel())) {
			if(data.getIsappl()==null){
				data.setIsappl(0);
			}
			data.setVcode((getMatcode(data)));
			setDefaultValue(data,uservo);
			data = (MaterielFileVO) singleObjectBO.insertVO("000001", data);
		} else {
			saveEdit(data);
		}
	}
	
	private void saveEdit(MaterielFileVO data)   throws DZFWarpException{
		
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materiel(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("物料编号：" + data.getVcode() + ",其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_materiel(), data.getUpdatets());
			
			String[] updates = {"vname", "vunit", "isappl" };
		    singleObjectBO.update(data, updates);
		    
		    //修改的物料信息同步到物料订单中
		    String sql ="select * from cn_materielbill_b "
		    +"where nvl(dr,0) = 0 and pk_materiel = ? ";
		    SQLParameter spm = new SQLParameter();
		    spm.addParam(data.getPk_materiel());
		    List<MatOrderBVO> voList = (List<MatOrderBVO>) singleObjectBO.executeQuery(sql, spm, new BeanListProcessor(MatOrderBVO.class));
		    if(voList!=null && voList.size()>0){
		    	String[] nupdates = {"vname", "vunit" };
		    	for (MatOrderBVO vo : voList) {
		    		vo.setVname(data.getVname());
		    		vo.setVunit(data.getVunit());
		    		singleObjectBO.update(vo,nupdates);
				}
		    }
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materiel(), uuid);
		}
}

	/**
	 * 检查是否是最新数据
	 * @param pk_materiel
	 * @param updatets
	 * @return
	 */
	private MaterielFileVO checkData(String pk_materiel, DZFDateTime updatets)   throws DZFWarpException{
		MaterielFileVO vo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, pk_materiel);
		if (!updatets.equals(vo.getUpdatets())) {
			throw new BusinessException("物料编号：" + vo.getVcode() + ",数据已发生变化;<br>");
		}
		return vo;
	}

	/**
	 * 获取物料编码
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private String getMatcode(MaterielFileVO vo) throws DZFWarpException {
		String code;
		String str = "CGWL";
		MaxCodeVO mcvo = new MaxCodeVO();
		mcvo.setTbName(vo.getTableName());
		mcvo.setFieldName("vcode");
		mcvo.setPk_corp("000001");
		mcvo.setBillType(str);
		mcvo.setCorpIdField("pk_corp");
		mcvo.setDiflen(4);
		try {
			code = billCodeSer.getDefaultCode(mcvo);
		} catch (Exception e) {
			throw new BusinessException("获取物料编码失败");
		}
		return code;
	}
	
	/**
	 * 设置默认值
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(MaterielFileVO data,UserVO uservo) throws DZFWarpException {
		data.setCoperatorid(uservo.getCuserid());
		data.setDoperatetime(new DZFDateTime());
		data.setPk_corp("000001");
		data.setOutnum(0);//默认发货数量为0
		data.setIntnum(0);//默认入库数量为0
		data.setIsseal(IStatusConstant.ISSEAL_1);//封存状态：默认为启用
	}

	@Override
	public int queryTotalRow(MaterielFileVO qvo)  throws DZFWarpException{
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	     return multBodyObjectBO.queryDataTotal(MaterielFileVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielFileVO> query(MaterielFileVO qvo,UserVO uservo)  throws DZFWarpException{
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	        List<MaterielFileVO> list = (List<MaterielFileVO>) multBodyObjectBO.queryDataPage(MaterielFileVO.class, sqpvo.getSql(),
	                sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
	        HashMap<String, UserVO> map = userser.queryUserMap(uservo.getPk_corp(), true);
	       if(list!=null && list.size()>0){
	    	   for (MaterielFileVO mvo : list) {
	    		      uservo = map.get(mvo.getCoperatorid());
					if(uservo!=null){
						mvo.setApplyname(uservo.getUser_name());
					}
				}
		        QueryDeCodeUtils.decKeyUtils(new String[] { "applyname" }, list, 1);  
	       }
	       return list;
	}
	
	/**
	 * 获取查询条件
	 * 
	 * @param pamvo
	 * @param uservo 
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(MaterielFileVO pamvo) throws DZFWarpException {
		
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("   SELECT pk_materiel,vcode,vname,vunit,isappl,isseal,coperatorid,doperatetime    ");
		sql.append("         from  cn_materiel   ");
		sql.append("         where nvl(dr,0) =0   ");
		if (!StringUtil.isEmpty(pamvo.getVname())) {
			sql.append(" AND vname like ? ");
			spm.addParam("%" + pamvo.getVname() + "%");
		}
		if (pamvo.getIsseal()!=null && pamvo.getIsseal()!=0) {
			sql.append("   AND isseal = ?   ");
			spm.addParam(pamvo.getIsseal());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getBegindate())) {
			sql.append(" and substr(doperatetime,0,10) >= ? ");
			spm.addParam(pamvo.getBegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getEnddate())) {
			sql.append(" and substr(doperatetime,0,10) <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" order by doperatetime desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
		
	}

	@Override
	public void updateStatus(MaterielFileVO mvo,Integer type)   throws DZFWarpException{
		if(type==1){
			checkIsOperY(mvo.getIsseal(),mvo.getVcode(),1,"已启用");
		}
		if(type==2){
			checkIsOperY(mvo.getIsseal(),mvo.getVcode(),2,"已封存");
		}
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(mvo.getTableName(), mvo.getPk_materiel(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("物料编号：" + mvo.getVcode() + ",其他用户正在操作此数据;<br>");
			}
			if(type==2){
				mvo.setIsseal(IStatusConstant.ISSEAL_2);//启用变为封存
			}
			if(type==1){
				mvo.setIsseal(IStatusConstant.ISSEAL_1);//封存变启用
			}
			singleObjectBO.update(mvo, new String[] { "isseal" });
  
		  }catch (Exception e) {
			  if (e instanceof BusinessException)
					throw new BusinessException(e.getMessage());
				else
					throw new WiseRunException(e);
		 }finally {
				LockUtil.getInstance().unLock_Key(mvo.getTableName(), mvo.getPk_materiel(), uuid);
			}
				
	}
	
	/**
	 * 判断封存状态
	 * @param sseal
	 * @param code
	 * @param type 
	 * @param msg
	 * @throws DZFWarpException
	 */
	private void checkIsOperY(Integer sseal, String code, Integer type, String msg) throws DZFWarpException{
			if (sseal == null) {
				throw new BusinessException("物料编号：" + code + "是错误数据");
			}else{
				if(type==1){
				    if (sseal == 1) {
						throw new BusinessException("物料编号：" + code + msg);
					} 
				}
				if(type==2){
				    if (sseal == 2) {
						throw new BusinessException("物料编号：" + code + msg);
					} 
				}
			}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielFileVO> querySsealById(String ids)  throws DZFWarpException {
		
		List<String> idList = Arrays.asList(ids.split(","));
		String condition = SqlUtil.buildSqlForIn("pk_materiel ",idList.toArray(new String[idList.size()]));
		String sql="SELECT isseal,vcode,pk_materiel,updatets from cn_materiel where "+condition;
		List<MaterielFileVO> voList = (List<MaterielFileVO>) singleObjectBO.executeQuery(sql, null, new BeanListProcessor(MaterielFileVO.class));
		return voList;
	}

	@Override
	public MaterielFileVO queryDataById(String id)  throws DZFWarpException {
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(  "select pk_materiel,vname,vunit,isappl,updatets    ");
		sql.append(  "   from cn_materiel   ");
		sql.append(  "   where nvl(dr,0)=0 and pk_materiel =?   ");
		spm.addParam(id);
		return (MaterielFileVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielFileVO.class));
	}

    private MaterielStockInVO queryIsRk(String id)  throws DZFWarpException {
		
		 StringBuffer sql = new StringBuffer();
		 SQLParameter spm = new SQLParameter();
		 spm.addParam(id);
		 sql.append("   select distinct l.vcode    ");
		 sql.append("         from cn_materielin m   ");
		 sql.append("         left join cn_materiel l on   ");
		 sql.append("         m.pk_materiel=l.pk_materiel   ");
		 sql.append("         where nvl(l.dr,0)=0 and nvl(m.dr,0)=0    ");
		 sql.append("         and l.pk_materiel = ?   ");
		 
		 return (MaterielStockInVO) singleObjectBO.executeQuery(sql.toString(),spm, new BeanProcessor(MaterielStockInVO.class));
	}

	@Override
	public void deleteWl(MaterielFileVO qryvo)  throws DZFWarpException {
		
		MaterielStockInVO vo = queryIsRk(qryvo.getPk_materiel());
		if(vo!=null){
			throw new BusinessException("物料编号："+vo.getVcode()+"已有入库单，请删除入库单后重试<br/>");
		}else{
			String uuid = UUID.randomUUID().toString();
			try {
				LockUtil.getInstance().tryLockKey(qryvo.getTableName(), qryvo.getPk_materiel(), uuid, 60);
				SQLParameter spm = new SQLParameter();
		    	spm.addParam(qryvo.getPk_materiel());
				String sql = " DELETE FROM cn_materiel WHERE pk_materiel = ? ";
				singleObjectBO.executeUpdate(sql, spm);
			}catch (Exception e) {
				if (e instanceof BusinessException)
					throw new BusinessException(e.getMessage());
				else
					throw new WiseRunException(e);
			} finally {
				LockUtil.getInstance().unLock_Key(qryvo.getTableName(), qryvo.getPk_materiel(), uuid);
			}
			
		}
		
	}


}