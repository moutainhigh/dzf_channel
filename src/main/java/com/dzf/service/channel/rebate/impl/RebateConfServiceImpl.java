package com.dzf.service.channel.rebate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
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
	public RebateVO updateConf(RebateVO data) throws DZFWarpException {
		// 时间校验
		String errmsg = rebateser.checkData(data);
		if (StringUtil.isEmpty(errmsg)) {
			throw new BusinessException(errmsg);
		}
		if (data.getIstatus() != IStatusConstant.IREBATESTATUS_1) {
			throw new BusinessException("返点单号" + data.getVbillcode() + "不为待确认态");
		}
		if (data.getIstatus() != null && data.getIstatus() == IStatusConstant.IREBATESTATUS_4) {
			if (StringUtil.isEmpty(data.getVconfirmnote())) {
				throw new BusinessException("确认说明不能为空");
			}
		}
		data.setTconfirmtime(new DZFDateTime());
		data.setTstamp(new DZFDateTime());
		try {
			LockUtil.getInstance().tryLockKey(data.getTableName(), data.getPk_rebate(), 15);
			singleObjectBO.update(data,
					new String[] { "istatus", "vconfirmnote", "vconfirmid", "tconfirmtime", "tstamp" });
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_rebate());
		}
		return data;
	}

}
