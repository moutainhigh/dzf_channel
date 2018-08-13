package com.dzf.service.channel.dealmanage.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.dealmanage.IChannelOrderService;

@Service("channelorderser")
public class ChannelOrderServiceImpl implements IChannelOrderService {

	@Override
	public Integer queryTotalRow(GoodsBillVO pamvo) throws DZFWarpException {
		return null;
	}

	@Override
	public List<GoodsBillVO> query(GoodsBillVO pamvo) throws DZFWarpException {
		return null;
	}
	
	private QrySqlSpmVO getQryCondition(GoodsBillVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		
		return null;
	}

}
