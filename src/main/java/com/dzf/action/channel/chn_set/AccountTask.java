package com.dzf.action.channel.chn_set;

import java.util.TimerTask;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dzf.service.channel.chn_set.IAccountSetService;

public class AccountTask extends TimerTask{
	
	private ServletContextEvent sce = null;

	public AccountTask(ServletContextEvent sce) {
		 this.sce = sce;
	}

	
	@Override
	public void run() {
		WebApplicationContext appctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		IAccountSetService service = (IAccountSetService) appctx.getBean("setAccount");
		service.updatedate(sce);
	}

}
