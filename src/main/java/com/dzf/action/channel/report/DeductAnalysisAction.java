package com.dzf.action.channel.report;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.report.IDeductAnalysis;

/**
 * 加盟商扣款分析
 * @author zy
 *
 */
@SuppressWarnings({ "rawtypes" })
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "deductanalysis")
public class DeductAnalysisAction extends BaseAction{

	private static final long serialVersionUID = -1632075355173038501L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IDeductAnalysis analyser;

	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			List<DeductAnalysisVO> vos = analyser.query(paramvo);
			grid.setRows(vos);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
}
