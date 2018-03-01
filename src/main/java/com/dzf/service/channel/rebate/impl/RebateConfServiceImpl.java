package com.dzf.service.channel.rebate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.WorkflowVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.rebate.IRebateConfService;
import com.dzf.service.channel.rebate.IRebateInputService;

@Service("rebateconfser")
public class RebateConfServiceImpl implements IRebateConfService{
	
	@Autowired
	private IRebateInputService rebateser;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public RebateVO updateConf(RebateVO data, String pk_corp, Integer opertype) throws DZFWarpException {
		// 时间校验
		String errmsg = rebateser.checkData(data);
		if (!StringUtil.isEmpty(errmsg)) {
			throw new BusinessException(errmsg);
		}
		if (data.getIstatus() != null && 
				data.getIstatus() != IStatusConstant.IREBATESTATUS_1) {
			throw new BusinessException("返点单号" + data.getVbillcode() + "不为待确认态");
		}
		if (opertype != null && opertype == IStatusConstant.IREBATEOPERTYPE_1) {
			if (StringUtil.isEmpty(data.getVconfirmnote())) {
				throw new BusinessException("驳回说明不能为空");
			}
			data.setIstatus(IStatusConstant.IREBATESTATUS_0);
		}else if(opertype != null && opertype == IStatusConstant.IREBATEOPERTYPE_2){
			data.setIstatus(IStatusConstant.IREBATESTATUS_2);
		}else if(opertype != null && opertype == IStatusConstant.IREBATEOPERTYPE_3){
			data.setIstatus(IStatusConstant.IREBATESTATUS_1);
		}
		data.setTconfirmtime(new DZFDateTime());
		data.setTstamp(new DZFDateTime());
		try {
			LockUtil.getInstance().tryLockKey(data.getTableName(), data.getPk_rebate(), 15);
			//1、更新相关确认信息
			singleObjectBO.update(data,
					new String[] { "istatus", "vconfirmnote", "vconfirmid", "tconfirmtime", "tstamp" });
			//2、记录审批历史
			WorkflowVO flowvo = getFlowInfo(data, pk_corp, opertype);
			if(flowvo != null){
				singleObjectBO.saveObject(pk_corp, flowvo);
			}
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_rebate());
		}
		return data;
	}
	
	/**
	 * 获取审批历史
	 * @param data
	 * @param pk_corp
	 * @param opertype
	 * @return
	 * @throws DZFWarpException
	 */
	private WorkflowVO getFlowInfo(RebateVO data, String pk_corp, Integer opertype) throws DZFWarpException{
		UserVO uservo = null;
		WorkflowVO flowvo = new WorkflowVO();
		flowvo.setPk_corp(pk_corp);
		flowvo.setPk_corpk(data.getPk_corp());
		flowvo.setPk_bill(data.getPk_rebate());
		flowvo.setVcode(data.getVbillcode());
		flowvo.setIsdeal(DZFBoolean.TRUE);
		String userid = data.getVconfirmid();//确认人
		flowvo.setSenderman(userid);
		uservo = UserCache.getInstance().get(userid, null);
		flowvo.setDsenddate(new DZFDate());
		flowvo.setDsendtime(new DZFDateTime());
		flowvo.setDealman(userid);
		flowvo.setDdealdate(new DZFDate());
		flowvo.setDdealtime(new DZFDateTime());
		if(uservo != null){
			flowvo.setSendmanname(uservo.getUser_name());
			flowvo.setDealmanname(uservo.getUser_name());
		}
		flowvo.setCoperatorid(userid);
		flowvo.setDoperatedate(new DZFDate());
		flowvo.setDr(0);
		flowvo.setVbilltype(IStatusConstant.IBILLTYPE_FD01);//返点单确认
		flowvo.setVapprovenote(data.getVconfirmnote());//确认说明or驳回说明
		if (opertype != null && opertype == IStatusConstant.IREBATEOPERTYPE_1) {
			flowvo.setVstatusnote("驳回修改");
		}else if(opertype != null && opertype == IStatusConstant.IREBATEOPERTYPE_2){
			flowvo.setVstatusnote("确认通过");
		}else if(opertype != null && opertype == IStatusConstant.IREBATEOPERTYPE_3){
			flowvo.setVstatusnote("取消确认");
		}
		return flowvo;
	}

}
