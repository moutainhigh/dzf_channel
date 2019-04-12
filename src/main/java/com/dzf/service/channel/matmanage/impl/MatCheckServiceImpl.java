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
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatCheckService;
import com.dzf.service.pub.IPubService;
import com.sun.org.apache.regexp.internal.recompile;

@Service("matcheck")
public class MatCheckServiceImpl implements IMatCheckService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IPubService pubser;

	@Override
	public List<ChnAreaBVO> queryComboBox(UserVO uservo) {
		
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append("SELECT a.pk_chnarea, \n");
		corpsql.append("  b.pk_chnarea_b,b.userid \n");
		corpsql.append(" FROM cn_chnarea_b b \n");
		corpsql.append(" left join cn_chnarea a on \n");
		corpsql.append(" a.pk_chnarea = b.pk_chnarea \n");
		corpsql.append("  where nvl(a.dr,0)= 0 and \n");
		corpsql.append("  nvl(b.dr,0)= 0 and \n");
		corpsql.append("  a.userid = ? \n");
		corpsql.append(" order by a.ts desc");
		sp.addParam(uservo.getCuserid());
		List<ChnAreaBVO> bvolist = (List<ChnAreaBVO>) singleObjectBO.executeQuery(corpsql.toString(), sp, new BeanListProcessor(ChnAreaBVO.class));
		if(bvolist!=null && bvolist.size()>0){
			for (ChnAreaBVO bvo : bvolist) {
				uservo = UserCache.getInstance().get(bvo.getUserid(), null);
				if(uservo != null){
					bvo.setUsername(uservo.getUser_name());
				}
			}
			return bvolist;
		}
		
		return null;
	}

	@Override
	public void updateStatusById(MatOrderVO data,UserVO uservo) {
		
		String uuid = UUID.randomUUID().toString();
        try {
        	boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid, 60);
			String message;
			if (!lockKey) {
				message = "合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>";
				throw new BusinessException(message);
			}
			
			MatOrderVO checkData = checkData(data.getPk_materielbill(), data.getUpdatets());
			
			data.setAuditerid(uservo.getCuserid());
			data.setAuditdate(new DZFDate());
			String[] updates ={ "vstatus","vreason","auditerid","auditdate"};
			singleObjectBO.update(data, updates);
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
	public MatOrderVO queryById(String pk_materielbill) {
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_materielbill);
		sql.append("  select vcontcode,updatets,pk_materielbill \n");
		sql.append("       from cn_materielbill \n");
		sql.append("     where nvl(dr,0) = 0 \n");
		sql.append("     and pk_materielbill = ? \n");
		
		MatOrderVO vo=(MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MatOrderVO.class));
		if(vo!=null){
			return vo;
		}
		return null;
	}
	
	 /**
 	 * 检查是否是最新数据
 	 * @param pk_materiel
 	 * @param updatets
 	 * @return
 	 */
 	private MatOrderVO checkData(String pk_materielbill, DZFDateTime updatets) {
 		MatOrderVO vo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, pk_materielbill);
 		if (!updatets.equals(vo.getUpdatets())) {
 			throw new BusinessException("合同编号：" + vo.getVcontcode() + ",数据已发生变化;<br>");
 		}
 		return vo;
 	}
	
	
}

	