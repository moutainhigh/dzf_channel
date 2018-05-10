package com.dzf.model.channel.report;

import java.util.List;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 业绩分析VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class AchievementVO extends SuperVO {

	private static final long serialVersionUID = -4268361448919814562L;
	
	@FieldAlias("sdate")
	private List<String> showdate;
	
	@FieldAlias("fir")
	private List<DZFDouble> first;
	
	@FieldAlias("sec")
	private List<DZFDouble> second;
	
	public List<String> getShowdate() {
		return showdate;
	}

	public List<DZFDouble> getFirst() {
		return first;
	}

	public List<DZFDouble> getSecond() {
		return second;
	}

	public void setShowdate(List<String> showdate) {
		this.showdate = showdate;
	}

	public void setFirst(List<DZFDouble> first) {
		this.first = first;
	}

	public void setSecond(List<DZFDouble> second) {
		this.second = second;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
