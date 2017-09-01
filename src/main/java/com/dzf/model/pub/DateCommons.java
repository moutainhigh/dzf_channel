package com.dzf.model.pub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.dzf.pub.lang.DZFDate;

/**
 * 
 * 日期相关公共类
 *
 */
public class DateCommons {

	/**
	 * 取两个日期间的年月：2016-03
	 * 
	 * @param minDate
	 * @param maxDate
	 * @return
	 * @throws ParseException
	 */
	public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月

		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(sdf.parse(minDate));
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		max.setTime(sdf.parse(maxDate));
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

		Calendar curr = min;
		while (curr.before(max)) {
			result.add(sdf.format(curr.getTime()));
			curr.add(Calendar.MONTH, 1);
		}
		return result;
	}

	/**
	 * 取两个日期间的年月：2016-03
	 * 
	 * @param minDate
	 * @param maxDate
	 * @return
	 * @throws ParseException
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
}
