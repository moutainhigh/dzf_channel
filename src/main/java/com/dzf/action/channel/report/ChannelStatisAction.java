package com.dzf.action.channel.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.constant.IFunNode;
import com.dzf.service.channel.report.IChannelStatisService;
import com.dzf.service.pub.IPubService;

/**
 * 渠道业绩统计
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "channelStatis")
public class ChannelStatisAction extends BaseAction<ManagerVO> {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChannelStatisService chnStatis;
	
	@Autowired
	private IPubService pubser;

	/**
	 * 查询
	 */
	public void query(){
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_56);
			ManagerVO qvo = new ManagerVO();
			qvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setCuserid(uservo.getCuserid());
			List<ManagerVO> list = chnStatis.query(qvo);
			if(list==null||list.size()==0){
				grid.setRows(new ArrayList<ManagerVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}	
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 明细查询方法
	 */
	public void queryDetail() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_56);
			ManagerVO paramvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), new ManagerVO());
			List<ManagerVO> clist = chnStatis.queryDetail(paramvo);
			grid.setRows(clist);
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

}
