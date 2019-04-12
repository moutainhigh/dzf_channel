package com.dzf.pub.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDate;

public class ToolsUtil {

	public ToolsUtil(){
		
	}
	
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
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
	 * 获取两个期间的期间数
	 * @param bperiod
	 * @param eperiod
	 * @return
	 */
	public static List<String> getPeriodsBp(String bperiod, String eperiod) {
		DZFDate begindate = new DZFDate(bperiod+"-01");
		DZFDate enddate = new DZFDate(eperiod+"-01");
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
	 * 获取两个期间的季度数
	 * @param bperiod
	 * @param eperiod
	 * @return
	 */
	public static List<String> getSeasonsBp(String bperiod, String eperiod) {
		DZFDate begindate = new DZFDate(bperiod+"-01");
		DZFDate enddate = new DZFDate(eperiod+"-01");
		List<String> seasons = new ArrayList<String>();
		int nb = begindate.getYear() * 12 + begindate.getMonth();
		int ne = enddate.getYear() * 12 + enddate.getMonth();
		int year = 0;
		int month = 0;
		String period = "";
		String season = "";
		for (int i = nb; i <= ne; i++) {
			month = i % 12;
			year = i / 12;
			if (month == 0) {
				month = 12;
				year -= 1;
			}
			period = year + "-" + (month < 10 ? "0" + month : month);
			season = getSeason(period);
			if(!seasons.contains(season)){
				seasons.add(season);
			}
		}
		return seasons;
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
	 * 获取两个期间相差的月数
	 * @param bperiod
	 * @param eperiod
	 * @return
	 */
	public static Integer getCyclenum(String bperiod, String eperiod) {
		DZFDate begindate = new DZFDate(bperiod+"-01");
		DZFDate enddate = new DZFDate(eperiod+"-01");
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
	 * 整数相减
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static Integer subInteger(Integer num1, Integer num2){
		num1 = num1 == null ? 0 : num1;
		num2 = num2 == null ? 0 : num2;
		return num1 - num2;
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
    
    /**
     * 获取月末日期
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getMaxMonthDate(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateFormat.parse(date));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return dateFormat.format(calendar.getTime());
	}
    
	/**
	 * 获取查询期间的上一个期间
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
    public static String getPreviousMonth(String period) throws DZFWarpException {
		Integer year = Integer.parseInt(period.substring(0, 4));
		Integer month = Integer.parseInt(period.substring(5));
		month = month - 1;
		Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
		ca.set(year, month, 01);// 月份是从0开始的，所以11表示12月
		ca.add(Calendar.MONTH, -1); // 月份减1
		Date lastMonth = ca.getTime(); // 结果
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
		return String.valueOf(sf.format(lastMonth));
	}
    
    /**
     * 获取查询期间的上n个期间
     * @param period
     * @param num
     * @return
     * @throws DZFWarpException
     */
    public static String getPreNumsMonth(String period, Integer num) throws DZFWarpException {
		Integer year = Integer.parseInt(period.substring(0, 4));
		Integer month = Integer.parseInt(period.substring(5));
		month = month - num;
		Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
		ca.set(year, month, 01);// 月份是从0开始的，所以11表示12月
		ca.add(Calendar.MONTH, -1); // 月份减1
		Date lastMonth = ca.getTime(); // 结果
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
		return String.valueOf(sf.format(lastMonth));
	}
    
    /**
     * 获取制定日期后几个月的日期
     * @param date
     * @param num
     * @return
     * @throws ParseException
     */
    public static String getDateAfterNum(DZFDate date, Integer num) throws ParseException {
		Calendar c = Calendar.getInstance();
		c.setTime(date.toDate());
		c.add(Calendar.MONTH, num - 1);
    	return dateFormat.format(c.getTime());
    }
    
    /**
     * 获取月份所属季度
     * @param period
     * @return
     * @throws DZFWarpException
     */
    public static String getSeason(String period) throws DZFWarpException {
    	DZFDate date = new DZFDate(period+"-01");
    	Integer year = date.getYear();
    	Integer month = date.getMonth();
    	if(month <= 3){
    		return year+"-01";
    	}else if(month <= 6){
    		return year+"-02";
    	}else if(month <= 9){
    		return year+"-03";
    	}else if(month <= 12){
    		return year+"-04";
    	}
    	return "";
    }
    
	/**
	 * 获取查询期间的上几年的同一期间
	 * @param period
	 * @param nums
	 * @return
	 * @throws DZFWarpException
	 */
    public static String getPreNumsYear(String period, Integer nums) throws DZFWarpException {
		Integer year = Integer.parseInt(period.substring(0, 4));
		String month = period.substring(5);
		Integer preyear = year - nums;
		return preyear + "-" + month;
	}
    
    private static final int SQL_IN_LIST_LIMIT = 100;
    
    /**
     * SQL拼接方法
     * @param fieldname
     * @param fieldvalue
     * @return
     */
	public static String buildSqlForNotIn(final String fieldname,
			final String[] fieldvalue) {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("(" + fieldname + " NOT IN ( ");
		int len = fieldvalue.length;
		// 循环写入条件
		for (int i = 0; i < len; i++) {
			if (fieldvalue[i] != null && fieldvalue[i].trim().length() > 0) {
				sbSQL.append("'").append(fieldvalue[i].toString()).append("'");
				// 单独处理 每个取值后面的",", 对于最后一个取值后面不能添加"," 并且兼容 oracle 的 IN 254 限制。每
				// 200 个 数据 or 一次。时也不能添加","
				if (i != (fieldvalue.length - 1)
						&& !(i > 0 && (i + 1) % SQL_IN_LIST_LIMIT == 0)) {
					sbSQL.append(",");
				}
			} else {
				return null;
			}

			// 兼容 oracle 的 IN 254 限制。每 200 个 数据 or 一次。
			if (i > 0
					&& (i + 1) % SQL_IN_LIST_LIMIT == 0
					&& i != (fieldvalue.length - 1)) {
				sbSQL.append(" ) AND ").append(fieldname).append(" NOT IN ( ");
			}
		}
		sbSQL.append(" )) ");

		return sbSQL.toString();
	}
}
