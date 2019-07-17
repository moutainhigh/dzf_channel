package com.dzf.action.listener;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import com.dzf.action.task.AccoScheduledTask;
import com.dzf.action.task.ConnectionConst;


/**
 * 
 * 账务设置类任务监听 需要再WEB-XML中配置
 */
public class AccountProcListenter extends HttpServlet implements ServletContextListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Timer timer = null;
	
	/**
	 * 监听开始销毁
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		timer.cancel();
		sce.getServletContext().log("定时器销毁");
	}

	/**
	 * 监听开始执行
	 */
	public void contextInitialized(ServletContextEvent sce) {

		//执行天任务 比如说今天8点38分执行天任务

        Calendar calender_schedule = Calendar.getInstance();//当前日历类并设置定时时间
        calender_schedule.setTime(new Date());
        calender_schedule.set(Calendar.HOUR_OF_DAY,12);
        calender_schedule.set(Calendar.MINUTE, 15);
        calender_schedule.set(Calendar.SECOND, 0);
        calender_schedule.set(Calendar.MILLISECOND, 0);
        
		 //传入任务的时间  和类型即可
		 AccoScheduledTask.executeTask(sce,calender_schedule,ConnectionConst.TASKTYPE_DAILY);
    
	}
}
