package com.dzf.pub.util;

import java.util.ArrayList;
import java.util.List;

import com.dzf.pub.lang.DZFDate;

public class ToolsUtil {

	public ToolsUtil(){
		
	}
	/**
	 * 获取两个日期之间的期间数
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	public static List<String> getPeriods(DZFDate begindate, DZFDate enddate) {
		List<String> vec_periods = new ArrayList<String>();
		int nb = begindate.getYear() * 12 + begindate.getMonth();
		int ne = enddate.getYear() * 12 + enddate.getMonth();
		int year = 0;
		int month = 0;
		for (int i = nb; i <= ne; i++) {
			month = i % 12;
			year = i / 12;
			if (month == 0) {
				month = 12;
				year -= 1;
			}
			vec_periods.add(year + "-" + (month < 10 ? "0" + month : month));
		}
		return vec_periods;
	}
	
	/**
	 * 获取两个日期之间相差的月数
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	public static Integer getCyclenum(DZFDate begindate, DZFDate enddate) {
		List<String> vec_periods = new ArrayList<String>();
		int nb = begindate.getYear() * 12 + begindate.getMonth();
		int ne = enddate.getYear() * 12 + enddate.getMonth();
		int year = 0;
		int month = 0;
		for (int i = nb; i <= ne; i++) {
			month = i % 12;
			year = i / 12;
			if (month == 0) {
				month = 12;
				year -= 1;
			}
			vec_periods.add(year + "-" + (month < 10 ? "0" + month : month));
		}
		if(vec_periods != null && vec_periods.size() > 0){
			return vec_periods.size();
		}
		return 0;
	}
}
