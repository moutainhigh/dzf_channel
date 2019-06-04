package com.dzf.action.channel;

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
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if(paramvo == null){
				paramvo = new QryParamVO();
			}
			paramvo.setCuserid(uservo.getCuserid());
			int page = paramvo.getPage();
			int rows = paramvo.getRows();
			List<CorpVO> list = channel.querySmall(paramvo);
			if (list != null && list.size() > 0) {
				CorpVO[] corpvos = getPagedVOs(list.toArray(new CorpVO[0]), page, rows);
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
	 * 查询渠道商
	 */
	public void queryChannel() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO) DzfTypeUtils.cast(getRequest(), paramvo);
			int page = paramvo == null ? 1 : paramvo.getPage();
			int rows = paramvo == null ? 100000 : paramvo.getRows();
			if(paramvo != null){
				paramvo.setEmail(getLoginUserid());
			}
			List<CorpVO> list = channel.queryChannel(paramvo);
			if (list != null && list.size() > 0) {
				CorpVO[] corpvos = list.toArray(new CorpVO[0]);
				corpvos = (CorpVO[]) QueryUtil.getPagedVOs(corpvos, page, rows);
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
	
	private CorpVO[] getPagedVOs(CorpVO[] cvos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= cvos.length) {// 防止endIndex数组越界
			endIndex = cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
	}

}