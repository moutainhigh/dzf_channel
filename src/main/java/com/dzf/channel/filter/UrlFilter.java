package com.dzf.channel.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.NodeUrlConst;
import com.dzf.pub.cache.ServletRequestCache;
import com.dzf.pub.framework.rsa.RSACoderUtils;
import com.dzf.pub.framework.rsa.XwwyAppRSACoderUtils;
/**
 * @author   
 * 
 */
public class UrlFilter implements Filter {
	//
//    private static final ThreadLocal<ServletRequest> tlCurrentRequest = new ThreadLocal<ServletRequest>();
//
//    public static ThreadLocal<ServletRequest> getTlCurrentRequest() {
//        return tlCurrentRequest;
//    }  

	private FilterConfig filterConfig;
	
	private String errorPage;

	public UrlFilter() {
		super();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws 
	IOException, ServletException {
			boolean needClearTl = false;
			try {
				ServletRequestCache.getInstance().getThreadLocal().set(request);
	    		//	getTlCurrentRequest().set(request);
//	    			needClearTl = true;
			} finally {
				if(needClearTl) {				
					try {
						ServletRequestCache.getInstance().getThreadLocal().remove();
				//		getTlCurrentRequest().remove();
					} catch (Exception e) {
					}
				}
			}
		
			if(request != null)
		   	request.setCharacterEncoding("UTF-8");
		   	response.setContentType("text/html;charset=UTF-8");
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			HttpSession session = getSession(request, false);
			
			if(session == null){
				session = req.getSession();
			}
			String url = req.getRequestURI();
			if( req!=null && (url.endsWith(".css") || url.endsWith("500.html") || url.endsWith("404.html") || url.endsWith(".js") || url.endsWith(".xlsx") || url.endsWith(".xls")   || url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif") || url.endsWith(".doc"))){
				filterChain.doFilter(request, response);
	        	return;
	        }
			//数据库监控
			if(url.contains("/druid/") && 
					(url.endsWith(".html") || url.endsWith("druid/submitLogin") || url.endsWith(".json"))){
				filterChain.doFilter(request, response);
	        	return;
			}
			//会计公司注册.....zpm增加
			if(url.endsWith("/sys/sm_user!getLogin.action") || url.endsWith("/au/image.jsp") || url.endsWith("/login.jsp") ){
				filterChain.doFilter(request, response);
	        	return;
			}
			//二维码
			if ((url.endsWith(".eot")) || (url.endsWith(".svg"))
			    		|| (url.endsWith(".ttf")) || (url.endsWith(".woff"))) {
			        filterChain.doFilter(request, response);
			        return;
			 }
			 
//			if(url.endsWith("qrcheck!doQRCodeCheck.action") || url.endsWith("qrcheck!doQRLogin.action")){
//				filterChain.doFilter(request, response);
//				return;
//			}
			//webservice hzp add
			if(url.contains("/services/YscsService")){
		        filterChain.doFilter(request, response);
		        return;	
			}
			
			if(url.contains("/hessian")){
				filterChain.doFilter(request, response);
	        	return;
			}
			// 小薇无忧
	        if (url != null) {
	            if (url.endsWith("/xwwy_app/busidata!dealData.action")) {
	                String operate = request.getParameter("operate");
	                if (StringUtil.isEmpty(operate)) {
	                    session.setAttribute("errorMsg", "操作状态为空。");
	                    return;
	                }
	                if (Integer.parseInt(operate) == 1 || Integer.parseInt(operate) == 7 ||Integer.parseInt(operate)==9) {
	                    filterChain.doFilter(request, response);
	                    return;
	                } else {
	                    String token = request.getParameter("token");
	                    String xwwy_sesssionid = request.getParameter("xwwy_sessionid");
	                    String userid = request.getParameter("userid");
	                    String corp_id = request.getParameter("corp_id");
	                    
	                    session.setAttribute(IGlobalConstants.login_user, userid);
	                    session.setAttribute(IGlobalConstants.login_corp, corp_id);
	                    session.setAttribute("xwwy_sessionid", xwwy_sesssionid);
	                    session.setAttribute(IGlobalConstants.login_token, token);
	                    
	                    if (StringUtil.isEmptyWithTrim(token) == false) {
	                        try {
	                            if (session != null) {
	                                boolean b = XwwyAppRSACoderUtils.validateToken(session);
	                                if (b) {
	                                    filterChain.doFilter(request, response);
	                                }else{
	                                	response.getWriter().write("{\"sessiontimeout\":true}");
	            	                    return;
	                                }
	                            }
	                        } catch (Exception e) {
	                            session.setAttribute("errorMsg", e.getMessage());
	                            response.getWriter().write("{\"sessiontimeout\":true}");
	                            return;
	                        }
	                    }
	                }
	            }else if(url.endsWith("/xwwy_app/upload!uploadFile.action")){
	            	filterChain.doFilter(request, response);
                    return;
	            }
	        }

			String contextPath = req.getContextPath();
			if(contextPath.equals("/")){
				contextPath = "";
			}
		    if(!url.equals(contextPath+"/sys/sm_user!channelLogin.action") && !url.equals(contextPath+"/")  && !url.equals(contextPath+"/index.jsp") 
		    		&& !url.equals(contextPath+"/bingtu.html")&& !url.equals(contextPath+"/sys/sm_user!logout.action")
		    		&& !url.equals(contextPath+"/sys/sm_user!updatePsw.action") && !url.equals(contextPath+"/sys/sm_user!updatePwdLogin.action")
		    		&& !url.equals(contextPath+"/help.html") && !url.equals(contextPath+"/files/manage-help.docx")
		    		&& !url.endsWith("/searchPsw.jsp")
		    		&& !url.equals(contextPath+"/st/searchPsw!getPswBack.action")
		    		&& !url.equals(contextPath+"/st/searchPsw!sandYZcode.action")
		    		&& !url.equals(contextPath+"/selcomp.jsp")
		    		&& !url.equals(contextPath+"/sys/sm_user!gsQueryAdmin.action")
		    		&& !url.equals(contextPath+"/sys/sm_user!gsSelectAdmin.action")
		    		&& !url.equals(contextPath+"/ref/select_area.jsp")
		    		&& !url.equals(contextPath+"/area/areasearch!query.action")
		    		&& !url.equals(contextPath + "/sys/sys_corpskinact!modifyKsin.action")
		    		&& !url.endsWith(contextPath + "/app/loginqr!getQRCode2.action")
					&& !url.endsWith(contextPath + "/app/loginqr!longConnCheck2.action")
//					&& !url.contains(contextPath + "/app/loginqr!photologin.action")
//					&& !url.contains(contextPath + "/app/loginqr!doQRlogin.action")
		    		){
		    	Set<String> powerMap = (Set<String>)session.getAttribute(IGlobalConstants.POWER_MAP);
				boolean b=false;
				if(powerMap != null && (isHavePower(powerMap, url.replace(contextPath, "")))){
					try{
						b = RSACoderUtils.validateToken(session);
					}catch(Exception e){
//						session.setAttribute("errorMsg", "无权操作,请联系管理员!");
//						req.getRequestDispatcher("/error_kj.jsp").forward(req,res);
//	   				 	return;
						req.getRequestDispatcher("/login.jsp").forward(req,res);
						return;
					}
				//powerMap.(url.replace(contextPath, "")) == null || !powerMap.get(url.replace(contextPath, ""))){
				}
				if(!b){
					if(url.endsWith(".action")){
						res.getWriter().write("{\"success\":false,\"msg\":\"无权操作,请联系管理员\"}");
						res.getWriter().flush();
						res.getWriter().close();
						return;	
					}else{
						session.setAttribute("errorMsg", "无权操作,请联系管理员!");
						req.getRequestDispatcher("/error_kj.jsp").forward(req,res);
	   				 	return;
					}
				}
			}
	        filterChain.doFilter(request, response);
			return;
	}
	
	 public static HttpSession getSession(ServletRequest request, boolean create)
	  {
	    if ((request instanceof HttpServletRequest)) {
	      HttpServletRequest req = (HttpServletRequest)request;
	      HttpSession session = req.getSession(create);
	      return session;
	    }
	    return null;
	  }
	

	public void destroy() {
		this.filterConfig = null;
	}
	
	/**
	 * 检查是否有权限
	 * @param powerMap
	 * @param url
	 * @return
	 */
	private boolean isHavePower(Set<String> powerMap,String url){
		boolean returnBoolean =false;
		Iterator<String> iterator=powerMap.iterator();
		while(iterator.hasNext()){
			String powerUrl=NodeUrlConst.getInstance().getUrlMap().get(iterator.next());
			if(powerUrl!=null){
				returnBoolean=powerUrl.contains(url);
				if(returnBoolean){
					return returnBoolean;
				}
			}
		}
		return returnBoolean;
	}
}