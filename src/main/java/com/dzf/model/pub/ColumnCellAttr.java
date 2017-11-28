package com.dzf.model.pub;

/**
 * 
 * 表头每个字段的属性
 * 
 * @author zhangj
 * 
 */
public class ColumnCellAttr {

	private String columname;
	private String key;
	private Integer colspan;
	private Integer rowspan;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getColumname() {
		return columname;
	}

	public void setColumname(String columname) {
		this.columname = columname;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

}
