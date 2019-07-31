package com.dzf.action.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.IChannelService;

@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/corp")
@Action(value = "channel")
public class ChannelAction extends BaseAction<UserVO> {
	
	@Autowired
	private IChannelService channel;

	private Logger log = Logger.getLogger(this.getClass());

	public void querySmall() {
		Grid grid = new Grid();
		try {
			UserVO uservo = checkLoginUser();
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if(paramvo == null){
				paramvo = new QryParamVO();
			}
			paramvo.setCuserid(uservo.getCuserid());
			List<CorpVO> list = channel.querySmall(paramvo);
			if (list != null && list.size() > 0) {
				CorpVO[] corpvos =  (CorpVO[]) QueryUtil.getPagedVOs(list.toArray(new CorpVO[0]), paramvo.getPage(), paramvo.getRows());
				grid.setRows(Arrays.asList(corpvos));
				grid.setTotal((long) (list.size()));
			} else {
				grid.setRows(list);
				grid.setTotal(0L);
			}
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询渠道商(包含演示加盟商)
	 */
	public void queryChannel() {
		Grid grid = new Grid();
		try {
			checkLoginUser();
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if(paramvo == null){
				paramvo = new ChInvoiceVO();
			}
			paramvo.setEmail(getLoginUserid());
			List<CorpVO> list = channel.queryChannel(paramvo);
			if (list != null && list.size() > 0) {
				CorpVO[] corpvos = list.toArray(new CorpVO[0]);
				corpvos = (CorpVO[]) QueryUtil.getPagedVOs(corpvos, paramvo.getPage(), paramvo.getRows());
				grid.setRows(Arrays.asList(corpvos));
				grid.setTotal((long) (list.size()));
			} else {
				grid.setRows(list);
				grid.setTotal(0L);
			}
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 多选加盟商参照
	 */
	public void qryMultiChannel() {
		Grid grid = new Grid();
		try {
			checkLoginUser();
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			List<CorpVO> list = channel.qryMultiChannel(paramvo);
			if (list != null && list.size() > 0) {
				grid.setRows(list);
				grid.setTotal((long) (list.size()));
			} else {
				grid.setRows(new ArrayList<CorpVO>());
				grid.setTotal(0L);
			}
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 登录用户校验
	 * @return
	 */
	private UserVO checkLoginUser(){
		UserVO uservo = getLoginUserInfo();
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误");
		} else if (uservo == null) {
			throw new BusinessException("登陆用户错误");
		}
		return uservo;
	}

}