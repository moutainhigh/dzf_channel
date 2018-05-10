package com.dzf.service.channel.report.impl;

import org.springframework.stereotype.Service;

import com.dzf.model.channel.report.AchievementVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.report.IAchievementService;

@Service("achievementser")
public class AchievementServiceImpl implements IAchievementService {

	@Override
	public AchievementVO queryLine(QryParamVO paramvo) throws DZFWarpException {
		return null;
	}

	@Override
	public AchievementVO queryChart(QryParamVO paramvo) throws DZFWarpException {
		return null;
	}

}
