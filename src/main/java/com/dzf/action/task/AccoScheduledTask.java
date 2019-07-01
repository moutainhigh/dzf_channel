package com.dzf.action.task;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;

import com.dzf.action.channel.chn_set.AccountTask;

public class AccoScheduledTask {

	private static ScheduledThreadPoolExecutor stpe = null;
	
	public static void executeTask(ServletContextEvent sce,Calendar calender_schedule, int taskType) {
		// 构造一个ScheduledThreadPoolExecutor对象，并且设置它的容量为1个
		stpe = new ScheduledThreadPoolExecutor(1);
		AccountTask accotask = new AccountTask(sce);//禁用记账服务
		long delay = 0;

		Date date_now = new Date();
		Calendar calendar_now = Calendar.getInstance();// 当前日历类并设置当前时间
		calendar_now.setTime(date_now);
		long now = calendar_now.getTimeInMillis();// 获取当前时间的毫秒值

		long shedule = calender_schedule.getTimeInMillis();
		long period = 0;// 间隔（多少毫秒后第一次执行）
		long oneday = 24L * 60 * 60 * 1000;// 一天多少毫秒

		if (taskType == ConnectionConst.TASKTYPE_DAILY) {//天任务
			period = oneday;
			delay = shedule - now;
			if (delay < 0){// 如果延时为负（说明已经超过了执行的时间 加一天后再执行）
				calender_schedule.set(Calendar.DAY_OF_MONTH, calender_schedule.get(Calendar.DAY_OF_MONTH) + 1);
				shedule = calender_schedule.getTimeInMillis();
				delay = shedule - now;
			}
		}
		
        stpe.scheduleAtFixedRate(accotask, delay, period, TimeUnit.MILLISECONDS);
	}

	public static ScheduledThreadPoolExecutor getStpe() {
		return stpe;
	}

	public static void setStpe(ScheduledThreadPoolExecutor stpe) {
		AccoScheduledTask.stpe = stpe;
	}

	

}
