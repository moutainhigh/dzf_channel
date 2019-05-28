package com.dzf.service.channel.branch.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.model.channel.branch.BranchInstSetupBVO;
import com.dzf.model.channel.branch.BranchInstSetupVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.branch.IBranchInstStepupService;


@Service("serbranch")
public class BranchInstStepupServiceImpl implements IBranchInstStepupService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public void saveInst(BranchInstSetupVO data) throws DZFWarpException{
		if(StringUtil.isEmpty(data.getPk_branchset())){
			//新增机构设置
			singleObjectBO.insertVO("000001",data);
		}else{
			saveEdit(data);
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
			//新增公司
			singleObjectBO.insertVO("000001",vo);
		}else{
			saveEditCorp(vo);
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
		String sql="select * from br_branchcorp where nvl(dr,0)=0 and vname=? ";
		SQLParameter spm=new SQLParameter();
		spm.addParam(name);
		BranchInstSetupBVO vo= (BranchInstSetupBVO) singleObjectBO.executeQuery(sql, spm, new BeanProcessor(BranchInstSetupBVO.class));
		if(vo==null){
			return true;
	    }
		return false;
	}


	@Override
	public CorpVO queryCorpInfo(String entnumber) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm=new SQLParameter();
		spm.addParam(entnumber);
		sql.append(" select\n");
		sql.append("    pk_corp,def12,unitname,linkman2,phone1 \n");
		sql.append("    from bd_account \n");
		sql.append("    where nvl(dr,0)=0 and def12 is not null \n");
		sql.append("    and def12 =? \n");
		CorpVO corpvo = (CorpVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(CorpVO.class));
		if(corpvo!=null){
			QueryDeCodeUtils.decKeyUtil(new String[] { "unitname", "phone1" }, corpvo, 1);
		}
		return corpvo;
	}


	@Override
	public void updateInst(BranchInstSetupBVO data) {
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_branchcorp(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("企业识别号："+data.getVname()+"其他用户正在操作此数据;<br>");
			}
			checkData(data.getPk_branchcorp(), data.getUpdatets(), "corp");
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


}
