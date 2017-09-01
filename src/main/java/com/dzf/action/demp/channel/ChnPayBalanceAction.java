package com.dzf.action.demp.channel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.demp.channel.ChnBalanceVO;
import com.dzf.model.demp.channel.ChnDetailVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.demp.channel.IChnPayBalanceService;

/**
 * 付款余额
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/chnpay")
@Action(value = "chnpaybalance")
public class ChnPayBalanceAction extends BaseAction<ChnBalanceVO> {

	private static final long serialVersionUID = -750029782321770916L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChnPayBalanceService paybalanSer;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			int total = paybalanSer.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<ChnBalanceVO> clist = paybalanSer.query(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<ChnBalanceVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 明细查询方法
	 */
	public void queryDetail() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			int total = paybalanSer.queryDetailTotal(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<ChnDetailVO> clist = paybalanSer.queryDetail(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<ChnDetailVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
}
