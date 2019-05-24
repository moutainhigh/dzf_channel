package com.dzf.model.channel.report;

import java.util.List;

import com.dzf.model.sys.sys_power.DataGrid;

/**
 * 报表返回grid
 * @author zy
 *
 */
public class ReportDataGrid extends DataGrid {
	
	private static final long serialVersionUID = 1L;
	
	private List<ReportDatagridColumn> hbcolumns;
	
	private String sumdata;

	public String getSumdata() {
		return sumdata;
	}

	public void setSumdata(String sumdata) {
		this.sumdata = sumdata;
	}

	public List<ReportDatagridColumn> getHbcolumns() {
		return hbcolumns;
	}

	public void setHbcolumns(List<ReportDatagridColumn> hbcolumns) {
		this.hbcolumns = hbcolumns;
	}
}
