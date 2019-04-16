package com.dzf.action.channel.report;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.LogisticRepVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.report.ILogisticRepService;
import com.dzf.service.pub.IPubService;

/**
 * 快递统计表
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "logistic")
public class LogisticRepAction extends BaseAction<LogisticRepVO>{

	@Autowired
	private ILogisticRepService logistic;
	
	@Autowired
	private IPubService pubService;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询商品数据
	 */
	public void queryGoods() {
		Grid grid = new Grid();
		try {
			QryParamVO qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			UserVO uservo = getLoginUserInfo();
			List<LogisticRepVO> retlist = new ArrayList<>();
			String powSql = pubService.makeCondition(uservo.getCuserid(), qvo.getAreaname(), 3);
			if (powSql != null && !powSql.equals("alldata")) {
				qvo.setVqrysql(powSql);
				retlist = logistic.queryGoods(qvo);
			} else if (powSql != null) {
				retlist = logistic.queryGoods(qvo);
			}
			grid.setRows(retlist);
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询物料数据
	 */
	public void queryMateriel() {
		Grid grid = new Grid();
		try {
			QryParamVO qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			UserVO uservo = getLoginUserInfo();
			List<LogisticRepVO> retlist = new ArrayList<>();
			String powSql = pubService.makeCondition(uservo.getCuserid(), qvo.getAreaname(), 3);
			if (powSql != null && !powSql.equals("alldata")) {
				qvo.setVqrysql(powSql);
				retlist = logistic.queryMateriel(qvo);
			} else if (powSql != null) {
				retlist = logistic.queryMateriel(qvo);
			}
			grid.setRows(retlist);
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询商品表头
	 */
	public void qryGoodsHead() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			List<ComboBoxVO> list = logistic.qryGoodsHead();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ComboBoxVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询物料表头
	 */
	public void qryMaterHead() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			List<ComboBoxVO> list = logistic.qryMaterHead();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ComboBoxVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
}
