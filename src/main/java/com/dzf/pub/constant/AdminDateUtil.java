
package com.dzf.pub.constant;

import java.util.Calendar;

import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.DateUtils;

/**
 * 服务器时间
 * @author dzf
 *
 */
public class AdminDateUtil {
	
//    private static Calendar CALENDAR = Calendar.getInstance();
    
    /**
     * 服务器时间：年-月-日
     * @return
     */
    public static String getServerDate(){
        return new DZFDate().toString();
    }
    
    /**
     * 服务器时间：年-月-日  时分秒
     * @return
     */
    public static String getServerDateTime(){
        return new DZFDateTime().toString();
    }
    
    /**
     * 服务器时间：期间
     * @return
     */
    public static String getPeriod(){
        return DateUtils.getPeriod( new DZFDate());
    }
    
    public static String getPreviousPeriod(){
        long millis = DateUtils.getPreviousYear(new DZFDate().getMillis());
        DZFDate ddate = new DZFDate(millis);
        return DateUtils.getPeriod(ddate);
    }
    /**
     * 服务器时间：前一个月、  年-月-日
     * @return
     */
    public static String getPreviousDate(){
        long millis = DateUtils.getPreviousMonth(new DZFDate().getMillis());
        return new DZFDate(millis).toString();
    }
    
    /**
     * 服务器时间：后一个月、  年-月-日
     * @return
     */
    public static String getNextDate(){
        long millis = DateUtils.getNextMonth(new DZFDate().getMillis());
        return new DZFDate(millis).toString();
    }
    
    /**
     * 服务器时间：年
     * @return
     */
    public static String getYear(){
        return String.valueOf(new DZFDate().getYear());
    }
    
    /**
     * 服务器时间：月
     * @return
     */
    public static int getMonth(){
        return new DZFDate().getMonth();
    }
    
    public static String getPreviousYear(){
        long millis = DateUtils.getPreviousYear(new DZFDate().getMillis());
        return new DZFDate(millis).toString();
    }
    
    /**
     * 取当前日期往前推N个月
     * @param n
     * @return
     */
     public static String getPreviousNMonth(int n) {
         long millis = incrementMonth(new DZFDate().getMillis(), -1*n);
         return new DZFDate(millis).toString();
     }
     
     /**
      * 时间计算
      * @param date
      * @param increment
      * @return
      */
     private static long incrementMonth(long date, int increment) {
         Calendar calendar = Calendar.getInstance();
         synchronized (calendar) {
             calendar.setTimeInMillis(date);
             calendar.add(Calendar.MONTH, increment);
             return calendar.getTimeInMillis();
         }
     }
     
     /**
      * 取当前月份向后推N个月
      * @param n
      * @return
      */
      public static String getNextNPeriod(int n) {
          long millis = incrementMonth(new DZFDate().getMillis(), +1*n);
          DZFDate date =  new DZFDate(millis);
          return date.getYear() + "-" + date.getStrMonth();
      }
}
