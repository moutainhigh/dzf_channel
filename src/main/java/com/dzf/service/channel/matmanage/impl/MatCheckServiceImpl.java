package com.dzf.service.channel.matmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatCheckService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("matcheck")
public class MatCheckServiceImpl implements IMatCheckService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IUserService userser;

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnAreaBVO> queryComboBox(UserVO uservo)   throws DZFWarpException{
		
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append(" SELECT distinct  b.userid,a.ts \n");
		corpsql.append(" FROM cn_chnarea_b b \n");
		corpsql.append(" left join cn_chnarea a on \n");
		corpsql.append(" a.pk_chnarea = b.pk_chnarea \n");
		corpsql.append("  where nvl(a.dr,0)= 0 and \n");
		corpsql.append("  nvl(b.dr,0)= 0 and \n");
		corpsql.append("  a.userid = ? \n");
		corpsql.append(" order by a.ts desc");
		sp.addParam(uservo.getCuserid());
		List<ChnAreaBVO> bvolist = (List<ChnAreaBVO>) singleObjectBO.executeQuery(corpsql.toString(), sp, new BeanListProcessor(ChnAreaBVO.class));
		HashMap<String, UserVO> map = userser.queryUserMap(uservo.getPk_corp(), false);
		if(bvolist!=null && bvolist.size()>0){
			for (ChnAreaBVO bvo : bvolist) {
				//uservo = UserCache.getInstance().get(bvo.getUserid(), null);
				  uservo = userser.queryUserJmVOByID(bvo.getUserid());
				if(uservo != null){
					bvo.setUsername(uservo.getUser_name());
				}
			}
			return bvolist;
		}
		
		return null;
	}

	@Override
	public void updateStatusById(MatOrderVO data,UserVO uservo,MatOrderBVO[] bvos)   throws DZFWarpException{
		
		String uuid = UUID.randomUUID().toString();
        try {
        	boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>");
			}
			
			checkData(data.getPk_materielbill(), data.getUpdatets());
			//修改申请通过数
			String[] updatets = {"succnum"};
			if(bvos!=null && bvos.length>0){
				for (MatOrderBVO bvo : bvos) {
					if(data.getVstatus()==2){//审核通过
						if(bvo.getSuccnum()==null){
							bvo.setSuccnum(0);
						}
						bvo.setSuccnum(bvo.getSuccnum()+bvo.getApplynum());
					}
					singleObjectBO.update(bvo, updatets);
				}
			}
			
			data.setAuditerid(uservo.getCuserid());
			data.setAuditdate(new DZFDate());
			String[] updates ={ "vstatus","vreason","auditerid","auditdate"};
			singleObjectBO.update(data, updates);
			
			if(data.getVstatus()==1){//反审核后
				data.setAuditdate(null);//清除之前的审核人信息
				data.setAuditerid(null);
				String[] nupdatets ={ "auditerid","auditdate"};
				singleObjectBO.update(data, nupdatets);
				
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielbill(), uuid);
		}
	}

	
	@Override
	public MatOrderVO queryById(String pk_materielbill)  throws DZFWarpException {
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_materielbill);
		sql.append("  select vcontcode,updatets,pk_materielbill \n");
		sql.append("       from cn_materielbill \n");
		sql.append("     where nvl(dr,0) = 0 \n");
		sql.append("     and pk_materielbill = ? \n");
		
		MatOrderVO vo=(MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MatOrderVO.class));
		return vo;
	}
	
	 /**
 	 * 检查是否是最新数据
 	 * @param pk_materiel
 	 * @param updatets
 	 * @return
 	 */
 	private MatOrderVO checkData(String pk_materielbill, DZFDateTime updatets)  throws DZFWarpException {
 		MatOrderVO vo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, pk_materielbill);
 		if (!updatets.equals(vo.getUpdatets())) {
 			throw new BusinessException("合同编号：" + vo.getVcontcode() + ",数据已发生变化;<br>");
 		}
 		return vo;
 	}

	@Override
	public MatOrderVO queryUserData(UserVO uservo, String mid) {
		
	    MatOrderVO mvo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, mid);
	    //uservo = UserCache.getInstance().get(uservo.getCuserid(), null);
	      uservo = userser.queryUserJmVOByID(uservo.getCuserid());
		mvo.setAudname(uservo.getUser_name());//审核人
		//uservo = UserCache.getInstance().get(mvo.getCoperatorid(), null);
		uservo = userser.queryUserJmVOByID(mvo.getCoperatorid());
		mvo.setApplyname(uservo.getUser_name());//申请人
		QueryDeCodeUtils.decKeyUtil(new String[] { "applyname","audname" }, mvo, 1);
		return mvo;
	}
	
}

	