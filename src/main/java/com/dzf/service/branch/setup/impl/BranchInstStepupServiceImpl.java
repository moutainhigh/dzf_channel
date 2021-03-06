package com.dzf.service.branch.setup.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.model.branch.setup.BranchInstSetupBVO;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QueryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.branch.setup.IBranchInstStepupService;


@Service("serbranchinst")
public class BranchInstStepupServiceImpl implements IBranchInstStepupService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public void saveInst(BranchInstSetupVO data) throws DZFWarpException{
		
		if(StringUtil.isEmpty(data.getPk_branchset())){
			//新增机构设置
			checkIsAddInst(data,0);
			singleObjectBO.insertVO("000001",data);
		}else{
			checkIsAddInst(data,1);
			saveEdit(data);
		}
	}

	/**
	 * 校验是否能新增机构
	 * @param data
	 * @param i 
	 */
	private void checkIsAddInst(BranchInstSetupVO data, Integer type) {
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm=new SQLParameter();
		spm.addParam(data.getVname());
		sql.append("select   ");
		sql.append("  pk_branchset   ");
		sql.append("  from br_branchset   ");
		sql.append("  where nvl(dr,0) = 0 and   ");
		if(type!=null && "1".equals(type.toString())){//修改机构
			sql.append(" vname = ? and pk_branchset!= ?   ");
			spm.addParam(data.getPk_branchset());
		}else{
			sql.append(" vname = ?   ");
		}
		
		BranchInstSetupVO bvo = (BranchInstSetupVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(BranchInstSetupVO.class));
		if(bvo!=null){
			throw new BusinessException("此机构名称已存在");
		}
	}


	private void saveEdit(BranchInstSetupVO data) throws DZFWarpException{
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_branchset(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_branchset(),data.getUpdatets(),"inst");
			singleObjectBO.update(data, new String[]{"vname"});
			
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_branchset(), uuid);
		}
	}
	
	

	@Override
	public void saveCorp(BranchInstSetupBVO vo) {
		if(StringUtil.isEmpty(vo.getPk_branchcorp())){
			checkIsAdd(vo);
			//新增公司
			singleObjectBO.insertVO("000001",vo);
		}else{
			saveEditCorp(vo);
		}
	}
	
	/**
	 * 校验企业识别号，公司名称
	 * @param vo
	 */
	private void checkIsAdd(BranchInstSetupBVO vo) {
		
		StringBuffer esql = new StringBuffer();
		SQLParameter espm=new SQLParameter();
		espm.addParam(vo.getVname());
		esql.append("  select def12 vname   ");
		esql.append("    from bd_corp   ");
		esql.append("    where nvl(dr,0) = 0 and   ");
		esql.append("    isaccountcorp = 'Y' and   ");
		esql.append("    def12 = ?   ");
		CorpVO corp = (CorpVO) singleObjectBO.executeQuery(esql.toString(), espm, new BeanProcessor(CorpVO.class));
		if(corp==null){
			throw new BusinessException("此企业识别号不存在");
		}
		
		if(StringUtil.isEmpty(vo.getUnitname())){
			throw new BusinessException("公司名称不能为空");
		}
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm=new SQLParameter();
		spm.addParam(vo.getPk_corp());
		sql.append("select   ");
		sql.append("  pk_corp   ");
		sql.append("  from br_branchcorp   ");
		sql.append("  where nvl(dr,0) = 0 and   ");
		sql.append("  pk_corp = ?   ");
		
		BranchInstSetupBVO bvo = (BranchInstSetupBVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(BranchInstSetupBVO.class));
		if(bvo!=null){
			throw new BusinessException("此公司名称已存在");
		}
	}


	private void saveEditCorp(BranchInstSetupBVO data) {
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_branchcorp(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("企业识别号："+data.getVname()+"其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_branchcorp(),data.getUpdatets(),"corp");
			singleObjectBO.update(data, new String[]{"vname","linkman","phone","isseal","vmemo"});
			
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_branchcorp(), uuid);
		}		
	}


	/**
 	 * 检查是否是最新数据
 	 * @param pk_id
 	 * @param updatets
	 * @param name 
 	 * @return
 	 */
 	private void checkData(String pk_id, DZFDateTime updatets, String name)  throws DZFWarpException {
 		BranchInstSetupVO vo = new BranchInstSetupVO();
 		BranchInstSetupBVO bvo = new BranchInstSetupBVO();
 		DZFDateTime newupdatets= new DZFDateTime();
 		if(name!=null && "inst".equals(name)){
 			vo = (BranchInstSetupVO) singleObjectBO.queryByPrimaryKey(BranchInstSetupVO.class, pk_id);
 			newupdatets = vo.getUpdatets();
 		}else{
 			bvo = (BranchInstSetupBVO) singleObjectBO.queryByPrimaryKey(BranchInstSetupBVO.class, pk_id);
 			newupdatets = bvo.getUpdatets();
 		}
 		if (!updatets.equals(newupdatets)) {
 			throw new BusinessException("当前数据已发生变化;<br>");
 		}
 	}


	@Override
	public Boolean queryCorpname(String name) {
		String sql="select * from br_branchcorp where nvl(dr,0)=0 and unitname=? ";
		SQLParameter spm=new SQLParameter();
		spm.addParam(name);
		BranchInstSetupBVO vo= (BranchInstSetupBVO) singleObjectBO.executeQuery(sql, spm, new BeanProcessor(BranchInstSetupBVO.class));
		if(vo==null){
			return true;
	    }
		return false;
	}


	@Override
	public BranchInstSetupBVO queryCorpInfo(String entnumber) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm=new SQLParameter();
		spm.addParam(entnumber);
		sql.append(" select  ");
		sql.append("    pk_corp,def12 vname,unitname,linkman2 linkman,phone1 phone   ");
		sql.append("    from bd_corp   ");
		sql.append("    where nvl(dr,0)=0 and def12 is not null   ");
		sql.append("    and def12 =?   ");
		BranchInstSetupBVO corpvo = (BranchInstSetupBVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(BranchInstSetupBVO.class));
		if(corpvo!=null){
			QueryDeCodeUtils.decKeyUtil(new String[] { "unitname", "phone" }, corpvo, 1);
		}
		return corpvo;
	}


	@Override
	public void updateInst(BranchInstSetupBVO data) {
		String uuid = UUID.randomUUID().toString();
		BranchInstSetupBVO bvo = (BranchInstSetupBVO) singleObjectBO.queryByPrimaryKey(BranchInstSetupBVO.class, data.getPk_branchcorp());
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_branchcorp(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("企业识别号："+bvo.getVname()+"其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_branchcorp(), bvo.getUpdatets(), "corp");
			singleObjectBO.update(data, new String[]{"pk_branchset"});
			
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_branchcorp(), uuid);
		}	
	}


	@Override
	public void updateStatus(BranchInstSetupBVO data) {
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_branchcorp(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("企业识别号："+data.getVname()+"其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_branchcorp(), data.getUpdatets(), "corp");
			
			if(!StringUtil.isEmpty(data.getIsseal()) &&
					"Y".equals(data.getIsseal())){
				data.setIsseal("N");
			}else if(!StringUtil.isEmpty(data.getIsseal()) &&
					"N".equals(data.getIsseal())){
				data.setIsseal("Y");
			}
			singleObjectBO.update(data, new String[]{"isseal"});
			
			
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_branchcorp(), uuid);
		}	
	}


	@Override
	public void deleteCorpById(BranchInstSetupBVO data) {
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_branchcorp(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("企业识别号："+data.getVname()+"其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_branchcorp(), data.getUpdatets(), "corp");
			String sql = "delete from br_branchcorp where pk_branchcorp = ? ";
			SQLParameter spm = new SQLParameter();
			spm.addParam(data.getPk_branchcorp());
			singleObjectBO.executeUpdate(sql, spm);
			
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_branchcorp(), uuid);
		}	
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<BranchInstSetupVO> queryList(QueryParamVO param) {
		StringBuffer ssql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		BranchInstSetupVO vo = new BranchInstSetupVO();
		String sql ="select * from br_branchset where nvl(dr,0) = 0 order by ts desc";
		List<BranchInstSetupVO> list = (List<BranchInstSetupVO>) singleObjectBO.executeQuery(sql, null, new BeanListProcessor(BranchInstSetupVO.class));
		if(param.getQrytype()!=null && param.getQrytype()==0){//第一次加载  先显示机构名称
			return list;
		}else{
			if(!StringUtil.isEmpty(param.getPk_currency())){
				spm.addParam(param.getPk_currency());
			}else{
				spm.addParam(list.get(0).getPk_branchset());
			}
			ssql.append(" select   ");
			ssql.append("   bc.pk_branchcorp,bc.vname,  ");
			ssql.append("   bc.linkman,bc.phone,bc.unitname,  ");
			ssql.append("   bc.isseal,bc.vmemo,bc.updatets   ");
			ssql.append("   from br_branchset bs   ");
			ssql.append("   right join br_branchcorp bc on   ");
			ssql.append("   bs.pk_branchset = bc.pk_branchset   ");
			ssql.append("   where nvl(bs.dr,0) = 0 and   ");
			ssql.append("   nvl(bc.dr,0) = 0 and   ");
			ssql.append("   bs.pk_branchset = ?   ");
			List<BranchInstSetupBVO> bvolist = (List<BranchInstSetupBVO>) singleObjectBO.executeQuery(ssql.toString(), spm, new BeanListProcessor(BranchInstSetupBVO.class));
			List<BranchInstSetupVO> newlist = new ArrayList<BranchInstSetupVO>();
			newlist.add(vo);
			BranchInstSetupBVO[] b = new BranchInstSetupBVO[bvolist.size()];
			BranchInstSetupBVO[] bvos = (BranchInstSetupBVO[]) bvolist.toArray(b);
			vo.setChildren(bvos);
			return newlist;
		}
	}
	

	@Override
	public Map<String, List> query(QueryParamVO param) {
		
		StringBuffer ssql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		Map<String, List> map = new HashMap<String, List>();
		if(param.getQrytype()!=null && param.getQrytype()==0){//第一次加载
			String sql ="select * from br_branchset where nvl(dr,0) = 0 order by ts desc";
			List<BranchInstSetupBVO> list = (List<BranchInstSetupBVO>) singleObjectBO.executeQuery(sql, null, new BeanListProcessor(BranchInstSetupBVO.class));
			if(list!=null && list.size()>0){
				map.put("0", list);
				spm.addParam(list.get(0).getPk_branchset());
			}else{
				return null;
			}
		}else{
			spm.addParam(param.getPk_currency());//主键id
		}
		
		ssql.append(" select   ");
		ssql.append("   bc.pk_branchcorp,bc.vname,  ");
		ssql.append("   bc.linkman,bc.phone,bc.unitname,  ");
		ssql.append("   bc.isseal,bc.vmemo,bc.updatets   ");
		ssql.append("   from br_branchset bs   ");
		ssql.append("   right join br_branchcorp bc on   ");
		ssql.append("   bs.pk_branchset = bc.pk_branchset   ");
		ssql.append("   where nvl(bs.dr,0) = 0 and   ");
		ssql.append("   nvl(bc.dr,0) = 0 and   ");
		ssql.append("   bs.pk_branchset = ?   ");
		List<BranchInstSetupBVO> bvolist = (List<BranchInstSetupBVO>) singleObjectBO.executeQuery(ssql.toString(), spm, new BeanListProcessor(BranchInstSetupBVO.class));
		if(bvolist!=null && bvolist.size()>0){
			map.put("1", bvolist);
		}
		
		
		return map;
	}


	@Override
	public Object queryById(String id,String type) {
		if(type!=null && "0".equals(type)){
			return (BranchInstSetupVO) singleObjectBO.queryByPrimaryKey(BranchInstSetupVO.class, id);
		}else{
			return (BranchInstSetupBVO) singleObjectBO.queryByPrimaryKey(BranchInstSetupBVO.class, id);
		}
		
	}
	
	@Override
	public List<ComboBoxVO> qryBranchs(String cuserid) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter param = new SQLParameter();
        sql.append("select bs.pk_branchset id , bs.vname name  ");
        sql.append("  from br_user_branch ub ");
        sql.append("  left join br_branchset bs on ub.pk_branchset = bs.pk_branchset ");
        sql.append(" where nvl(ub.dr, 0) = 0 ");
        sql.append("   and nvl(bs.dr, 0) = 0 ");
        sql.append("   and ub.cuserid = ? ");
    	param.addParam(cuserid);
        return (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), param, new BeanListProcessor(ComboBoxVO.class));
    }

	


}
