package com.dzf.action.channel.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.FinanceDealStateRepVO;
import com.dzf.model.channel.report.FinanceDetailVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.report.IAccountQryService;
import com.dzf.service.channel.report.IFinanceDealStateRep;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 财务处理分析
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "financedealstaterep")
public class FinanceDealStateRepAction extends BaseAction<FinanceDealStateRepVO> {

	private static final long serialVersionUID = 995793827523865854L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IFinanceDealStateRep financeServ;

	@Autowired
	private IAccountQryService iaccqryser;
	
	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if (paramvo != null) {
				paramvo.setUser_name(getLoginUserid());
				paramvo.setBegdate(new DZFDate());
			}
			if (paramvo != null && StringUtil.isEmpty(paramvo.getPk_corp())) {
				paramvo.setPk_corp(getLogincorppk());
			}
			List<FinanceDealStateRepVO> list = financeServ.query(paramvo);
			int page = paramvo == null ? 1 : paramvo.getPage();
			int rows = paramvo == null ? 10000 : paramvo.getRows();
			int len = list == null ? 0 : list.size();
			if (len > 0) {
				grid.setTotal((long) (len));
				SuperVO[] pageVOs = QueryUtil.getPagedVOs(list.toArray(new FinanceDealStateRepVO[0]), page, rows);
				grid.setRows(Arrays.asList(pageVOs));
				grid.setSuccess(true);
				grid.setMsg("查询成功");
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_8.getValue(), "财务处理分析查询成功", ISysConstants.SYS_3);
			} else {
				grid.setTotal(Long.valueOf(0));
				grid.setRows(new ArrayList<FinanceDealStateRepVO>());
				grid.setSuccess(true);
				grid.setMsg("查询结果为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询
	 */
	public void queryDetail() {
		Grid grid = new Grid();
		try {
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (isPageByBase(pamvo)) {
				int total = iaccqryser.queryTotalRow(pamvo, getLoginUserInfo());
				if (total > 0) {
					List<FinanceDetailVO> detlist = iaccqryser.query(pamvo, getLoginUserInfo());
					grid.setRows(detlist);
				} else {
					grid.setRows(new ArrayList<FinanceDetailVO>());
				}
				grid.setTotal((long) (total));
			}else{
				List<FinanceDetailVO> detlist = iaccqryser.queryAllData(pamvo, getLoginUserInfo());
				int len = detlist == null ? 0 : detlist.size();
				if (len > 0) {
					grid.setTotal((long) (len));
					SuperVO[] pageVOs = QueryUtil.getPagedVOs(detlist.toArray(new FinanceDetailVO[0]), page, rows);
					grid.setRows(Arrays.asList(pageVOs));
				} else {
					grid.setTotal(Long.valueOf(0));
					grid.setRows(new ArrayList<FinanceDetailVO>());
				}
			}

			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}

		writeJson(grid);
	}
	
	/**
	 * 是否数据库分页查询
	 * @return
	 * @throws DZFWarpException
	 */
	private boolean isPageByBase(QryParamVO pamvo) throws DZFWarpException {
		// 记账状态、账务检查
		if (StringUtil.isEmpty(pamvo.getCorpkname()) && "全部".equals(pamvo.getVmanager())
				&& "全部".equals(pamvo.getVbillcode())) {
			return true;
		}
		return false;
	}

}
