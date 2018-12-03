package com.dzf.action.channel.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.AchievementVO;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.constant.IFunNode;
import com.dzf.service.channel.report.IChannelDataService;
import com.dzf.service.channel.report.IChannelStatisService;
import com.dzf.service.pub.IPubService;

/**
 * 渠道数据统计
 * 
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "channelData")
public class ChannelDataAction extends BaseAction<AchievementVO> {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChannelDataService chnData;
	
	@Autowired
	private IPubService pubser;

	/**
	 * 查询
	 */
	public void query(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_57);
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO)DzfTypeUtils.cast(getRequest(), paramvo);
			AchievementVO vo = chnData.query(paramvo);
			json.setRows(vo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}

}
