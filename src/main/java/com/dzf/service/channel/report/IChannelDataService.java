package com.dzf.service.channel.report;

import com.dzf.model.channel.report.AchievementVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IChannelDataService {
	
	public AchievementVO query(QryParamVO vo) throws DZFWarpException;

}
