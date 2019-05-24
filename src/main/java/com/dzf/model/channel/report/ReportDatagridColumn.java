package com.dzf.model.channel.report;

import com.dzf.model.sys.sys_power.DatagridColumn;

/**
 * grid表格动态属性
 * @author zy
 *
 */
public class ReportDatagridColumn extends DatagridColumn {

	private String formatter;
	
	private Integer rowspan;
	
	private Integer colspan;
	
	private String halign;

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public String getHalign() {
		return halign;
	}

	public void setHalign(String halign) {
		this.halign = halign;
	}
}
