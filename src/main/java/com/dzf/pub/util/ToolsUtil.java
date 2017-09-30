package com.dzf.pub.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	/**
	 * 整数相加
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static Integer addInteger(Integer num1, Integer num2){
		num1 = num1 == null ? 0 : num1;
		num2 = num2 == null ? 0 : num2;
		return num1 + num2;
	}
	
    /** 
     * 根据日期计算所在周的上下界 
     *  
     * @param time 
     */  
    public static Map<String, DZFDate> getWeekDate(Date time) {  
        Map<String, DZFDate> map = new HashMap<String, DZFDate>();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(time);  
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天  
        if (1 == dayWeek) {  
            cal.add(Calendar.DAY_OF_MONTH, -1);  
        }  
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
        int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天  
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值  
        String begin = sdf.format(cal.getTime());  
        cal.add(Calendar.DATE, 6);  
        String end = sdf.format(cal.getTime());  
  
        map.put("begin", DZFDate.getDate(begin));  
        map.put("end", DZFDate.getDate(end));  
  
        return map;  
    }
}
