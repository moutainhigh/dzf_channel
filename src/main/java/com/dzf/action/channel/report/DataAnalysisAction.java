package com.dzf.action.channel.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.DataAnalysisVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.report.IDataAnalysisService;

/**
 * 加盟商
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "dataanalysis")
public class DataAnalysisAction extends BaseAction<DataAnalysisVO> {

	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IDataAnalysisService analyser;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if(pamvo == null){
				pamvo = new QryParamVO();
			}
			long total = analyser.queryTotalRow(pamvo);
			if(total > 0){
				List<DataAnalysisVO> list = analyser.query(pamvo);
				grid.setTotal(total);
				grid.setRows(list);
			}else{
				grid.setRows(new ArrayList<DataAnalysisVO>());
			}
			
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
}
