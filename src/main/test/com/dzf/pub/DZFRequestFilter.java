package com.dzf.pub;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.pub.DZFSessionVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.cache.ServletRequestCache;
import com.dzf.pub.cache.SessionCache;
import com.dzf.pub.framework.rsa.RSACoderUtils;
import com.dzf.pub.httpclient.HttpClientUtil;

import com.dzf.pub.session.DzfCookieTool;
import com.dzf.pub.session.DzfSessionTool;
import com.dzf.pub.util.RSAUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * @author   
 * 
 */
public class DZFRequestFilter implements Filter {
	//
//    private static final ThreadLocal<ServletRequest> tlCurrentRequest = new ThreadLocal<ServletRequest>();
//
//    public static ThreadLocal<ServletRequest> getTlCurrentRequest() {
//        return tlCurrentRequest;
//    }  

	private FilterConfig filterConfig;
	
	private String errorPage;

	public DZFRequestFilter() {
		super();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}
	private boolean isForbiddenRedirect(String url)
	{
		return url.endsWith("sys/xjr_sync!sync.action") || url.endsWith("xwwy_app/busidata!dealData.action") 
				|| url.endsWith("xwwy_app/upload!uploadFile.action") || url.contains("wbx/invoice!inputExpBill.action")
				|| url.contains("hessian/voucherService") || url.contains("/services/YscsService")
				|| url.contains("hessian/riscservice")
				|| url.contains("hessian/cwgyInfoService" )
				|| url.contains("hessian/filtransService")
				|| url.endsWith("/taxrpt/taxDeclarAction!updateDeclareStatusJs.action")//江苏报税，回写状态接口。
				;
	}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws 
	IOException, ServletException {
			boolean needClearTl = false;
			try {
				ServletRequestCache.getInstance().getThreadLocal().set(request);
	    //			getTlCurrentRequest().set(request);
//	    			needClearTl = true;
			} finally {
				if(needClearTl) {				
					try {
						ServletRequestCache.getInstance().getThreadLocal().remove();
//						getTlCurrentRequest().remove();
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
				
				RSAPublicKey publicKey = RSAUtils.getDefaultPublicKey();
				
				String modulus=new String(Hex.encodeHex(publicKey.getModulus().toByteArray()));
				String exponent=new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray()));
				session.setAttribute("MODULUS",modulus);
				session.setAttribute("EXPONENT",exponent);
				
			}
			
			//session修改开始		
			String longurl = req.getRequestURL().toString();
			
			String token = DzfCookieTool.getToken(request);
			String uuid_cookie = DzfCookieTool.getUUIDByCookie(req);
			String ticket = request.getParameter("t");
			String forceReadRedis = request.getParameter("rds");
			String ssoserverflag = request.getParameter("ssoserver");
			String pk_user = null;		
			String clientid = null;
			String errorRetMsg = null;	//错误返回提示信息
			
			String appid = null;
			
			//检查是否有统一登录服务器配置
			String[] ssoservercfg = DzfServerProperty.getInstance().getDzfServerCfg();
			if (ssoservercfg == null)
			{
				errorRetMsg = "请配置服务器属性文件dzfserver.properties";
				return;
			}
			String ssoserver = ssoservercfg[0];
			appid = ssoservercfg[1];
			String loginjsp = ssoservercfg[2];
			boolean useSSOServer = StringUtil.isEmpty(ssoservercfg[3]) ? false : Boolean.valueOf(ssoservercfg[3]);
			if (useSSOServer)
			{
				if (StringUtil.isEmpty(ssoserverflag) == false && ssoserverflag.toLowerCase().trim().equals("n"))//判断传入参数是否禁止
				{
					useSSOServer = false;
				}
				if (useSSOServer)	//判断禁用单点服务器域名是否包含当前地址
				{
					String forbiddenaddress = ssoservercfg[4];
					if (StringUtil.isEmptyWithTrim(forbiddenaddress) == false)
					{
						String[] addresses = forbiddenaddress.split(",");
						for (String address : addresses)
						{
							if (longurl.contains(address))
							{
								useSSOServer = false;
								break;
							}
						}
					}
				}
			}

			if (longurl.contains("loginMac.jsp"))
			{
				loginjsp = "loginMac.jsp";
			}
				
			try 
			{
				boolean bDeleteSession = false;
				boolean bDeleteCookie = false;
				
				

						//从cookie中获得
				String sessionUser = (String)session.getAttribute(IGlobalConstants.login_user);
				
				if (StringUtil.isEmptyWithTrim(ticket))				//票换用户信息过程不能走下面代码
				{
					if (token == null)
					{
						//可能是登录过程，客户端无cookie
						if (uuid_cookie == null)
						{
							DzfCookieTool.writeCookie_UUID(session, req, res);
						}
						else
						{
							session.setAttribute(IGlobalConstants.uuid, uuid_cookie); //恢复uuid
						}
					}
					else
					{
						String realtoken = DzfCookieTool.getRealToken(token);
						if (StringUtil.isEmptyWithTrim(realtoken) == false)
						{
							String[] sa = realtoken.split(",");
							String strUUID = sa[0];
							pk_user = sa[1];
							String appid_intoken = sa[2];
							
							session.setAttribute(IGlobalConstants.uuid, strUUID);
	
							if(sa.length==4){//即含有clientid
								clientid = sa[3];
							}
							//从redis服务器检查此用户是否有在其它客户端在线，也就是当前用户是否被踢
							DZFSessionVO sessionvo_ByUserid = null;
							sessionvo_ByUserid = SessionCache.getInstance().getByUserID(pk_user, appid,clientid);
							 

							
							if (sessionvo_ByUserid != null)
							{
								if (strUUID.equals(sessionvo_ByUserid.getUuid()))
								{
									if (StringUtil.isEmptyWithTrim(sessionUser))
									{
										//把redis缓存信息重新赋值给httpsession
										DzfSessionTool.fillValueToHttpSession(sessionvo_ByUserid, session);
			
//										DzfSessionContext.getInstance().AddUserSession(session);
										RSACoderUtils.createToken(session);
									}
									else if (sessionUser.equals(pk_user) == false)
									{
										//这个分支进入的几率很低，当分布式tomcat集群的jsession重复了，服务器跳转后可能会出现
										bDeleteCookie = true;
										bDeleteSession = true;
									}
									else
									{
										//是否强制从redis恢复session
										if (StringUtil.isEmpty(forceReadRedis) == false)
										{
											DZFSessionVO sessionvo_ByUUID = SessionCache.getInstance().getByUUID(strUUID, appid);
											
											if (sessionvo_ByUUID.getPk_user().equals(pk_user) == false)
											{
												//把登录的原用户信息从redis服务器清除
												SessionCache.getInstance().removeByUUID(strUUID, pk_user, appid, session.getMaxInactiveInterval(),clientid);
											}
											DzfSessionTool.fillValueToHttpSession(sessionvo_ByUUID, session);
											//重新生成token
											RSACoderUtils.createToken(session);
											//新token 更新回redis
											sessionvo_ByUUID.setToken((String)session.getAttribute(IGlobalConstants.login_token));
											SessionCache.getInstance().addSession(session);
											DzfCookieTool.writeCookie(session, request, response);
											
										}
										//一切正常的交互，不需做什么工作，向redis服务器同步session信息在定时服务中运行，不能在每次客户端请求都做。
									}
								}
								else
								{
									//此用户在其他客户端登录了
									bDeleteCookie = true;
									bDeleteSession = true;
									session.setAttribute(IGlobalConstants.logout_msg,"被其它用户强制退出！");

									if (session.getAttribute(IGlobalConstants.login_user) != null)
									{
										DzfSessionTool.clearSession(session);
									}
									DzfCookieTool.deleteCookie(request, response);

									res.sendRedirect(req.getContextPath() + "/login.jsp");
									return;

								}
							}
							else if (StringUtil.isEmptyWithTrim(sessionUser) == false)
							{
								//cookie存在，httpseesion也存在，但redis缓存没有了，只要cookie中的用户与session中的用户一致，即可继续使用
								if (sessionUser.equals(pk_user))
								{

									//!!!--- redis缓存没了不再重建，可能是服务器重启，或者网络断开，或者相同用户在其他机器登录后又退出且ngix又跳转，总之，不能轻易在此给用户回复缓存。
									//httpsession 重新缓存到redis
									//SessionCache.getInstance().addSession(session);
									
								}
								else
								{
									//这种情况不太可能出现, 
									bDeleteCookie = true;
									bDeleteSession = true;
								}
							}
							else
							{
								//redis服务器没有session信息，tomcat中也没有session信息，但有客户端cookie里面的token，说明是过期了，需要删除
								bDeleteCookie = true;
								bDeleteSession = true;
								
								DzfCookieTool.deleteCookie(request, response);
								
								if (longurl.endsWith(".action"))
								{
									errorRetMsg = "{\"success\":false,\"msg\":\"会话已过期，请重新登录\",\"status\":-300}";
								}
								else
								{
									res.sendRedirect(req.getContextPath() + "/login.jsp"); 
								}
								return;
								
							}
						}
						else
						{
							//没有成功解出真正token内容，可能是客户端无cookie，或cookie已陈旧，与服务器不匹配，
							bDeleteCookie = true;
							bDeleteSession = true;
						}
					}
					if (bDeleteSession)
					{
						if (session.getAttribute(IGlobalConstants.login_user) != null)
						{
							DzfSessionTool.clearSession(session);
						}
					}
					if (bDeleteCookie)
					{
						DzfCookieTool.deleteCookie(request, response);
					}
		
				}
				//session修改开始结束
				
				
			
				//SSOServer跳转
				if (isForbiddenRedirect(longurl) == false)	//小巨人同步接口不能跳转
				{
					String contextpath = req.getContextPath();
					
					String pk_user_session = (String)session.getAttribute(IGlobalConstants.login_user);
					
					String qz = request.getParameter("qz");	//提示信息
	
					if (StringUtil.isEmptyWithTrim(pk_user_session) || StringUtil.isEmptyWithTrim(ticket) == false)
					{
						if (useSSOServer)	//没有login.jsp则是通过ssoserver统一登录
						{
							if (StringUtil.isEmptyWithTrim(ticket) == false)
							{
								boolean isSuccess = false;
								DZFSessionVO ticketobj = null;
								
								String ssoserver_url = ssoserver + "TicketServlet";
	          
				            	Map<String, String> map = new HashMap<String, String>();
				            	map.put("ticket", ticket);
				            	
				                try {
				                	String ticketobjstr = new HttpClientUtil().doPostEntity(ssoserver_url, map, "utf-8");
				                	if (StringUtil.isEmptyWithTrim(ticketobjstr) == false) {
				                		ticketobj = new ObjectMapper().readValue(ticketobjstr, DZFSessionVO.class);
				                		if (StringUtil.isEmptyWithTrim(ticketobj.getPk_user()) == false)
				                		{
				                			isSuccess = true;
				                		}
				                	}
					               
				                } catch (Exception e) {
				                	//filter 写不写log？
				                }
				                if (isSuccess)
				                {
				                	pk_user = ticketobj.getPk_user();
	
									DzfSessionTool.fillValueToHttpSession(ticketobj, session);
									
									
									RSACoderUtils.createToken(session);
									//写登录成功信息到客户端
									DzfCookieTool.writeCookie(session, request, response);
									//UUID更新为ssoserver的值
									DzfCookieTool.writeCookie_UUID(session, request, response);
									
									res.sendRedirect(longurl + (qz == null ? "" : "?qz=" + qz));

									return;
	
				                }
				                else
				                {
				                	errorRetMsg = "无权操作,请联系管理员";
							
									bDeleteCookie = true;	
									bDeleteSession = true;
			
				                }
							}
							else
							{
								longurl = (longurl.startsWith("https://") || req.getServerPort() == 443 ? "https://" : "http://") 
										+ req.getServerName() + (req.getServerPort() == 80 || req.getServerPort() == 443 ? "" : ":" + String.valueOf(req.getServerPort())) 
										+ (StringUtil.isEmptyWithTrim(contextpath) ? "/login.jsp" : contextpath.trim() + "/login.jsp");
								String encoderURL = URLEncoder.encode(longurl, "UTF-8");
								//没有用户，也没有ticket
								//跳转至ssoserver用户登录
								StringBuffer newurl = new StringBuffer();
								newurl.append(ssoserver);
								newurl.append(loginjsp);
								newurl.append("?service=");
								newurl.append(encoderURL);
								newurl.append("&appid=");
								newurl.append(appid);
								res.sendRedirect(newurl.toString());
								
							}
							return;
						}
						else
						{
							//开发调试过程有login.jsp, 保留原交互
						}
					}
					else
					{
						
						//用户已经登陆成功，如果有ticket后缀，则跳转一下，删掉t=xxx长串
						//或者 	//服务器中没有login.jsp 是通过ssoserver统一登录，但如果请求中有login.jsp，会报404错误。
						if (StringUtil.isEmptyWithTrim(ticket) == false || useSSOServer && longurl.indexOf("/login.jsp") >= 0)
						{

							res.sendRedirect(req.getContextPath() + "/" + (qz == null ? "" : "?qz=" + qz));
							return;
						}
						
					}
				}
			}
			catch (Exception e)
			{
				//filter写日志否？
			}
			finally {
				
				if (errorRetMsg != null)
				{
//					res.getWriter().write("{\"success\":false,\"msg\":\"" + errorRetMsg + "\",\"status\":-300}");
					res.getWriter().write(errorRetMsg);
					res.getWriter().flush();
					res.getWriter().close();
					return;
				}
			}

			////ssoserver跳转 结束

			
			String url = req.getRequestURI();
			if( req!=null && (url.endsWith("DzfApplet.jar")||url.endsWith("fileupload.jar") || url.endsWith(".css") || url.endsWith(".js")  || url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif"))){
				filterChain.doFilter(request, response);
	        	return;
	        }
			//数据库监控
/*			if(url.contains("/druid/") && 
					(url.endsWith(".html") || url.endsWith("druid/submitLogin") || url.endsWith(".json"))){
				filterChain.doFilter(request, response);
	        	return;
			}*/
			//会计公司注册.....zpm增加
			if(url.endsWith("/wbx/invoice!inputExpBill.action") || url.endsWith("/404.html") || url.endsWith("/500.html") || url.endsWith("/au/image.jsp") || url.endsWith("/do/dz_registered.jsp") || url.endsWith("/do/forgot_password.jsp") || url.endsWith("/do/results.jsp")
					|| url.endsWith("register_act!sendMessage.action") || url.endsWith("register_act!saveRegistered.action") ){
				filterChain.doFilter(request, response);
	        	return;
			}
			//小巨人同步
			if(url.endsWith("/sys/xjr_sync!sync.action")){
				filterChain.doFilter(request, response);
	        	return;
			}
			//会计工厂登录
			if(url.endsWith("/loginMac.jsp")){
				filterChain.doFilter(request, response);
	        	return;
			}
			//小薇无忧app登陆
			if(url.contains("/xwwy_app/busidata!dealData.action") || url.endsWith("xwwy_app/upload!uploadFile.action")){
				filterChain.doFilter(request, response);
	        	return;
			}
			//增加中服直接登录
			if(url.endsWith("/sys/auto_user!autologin.action") || url.endsWith("/zonefulogin.jsp") || url.endsWith("/bindzflogin.jsp")){
				filterChain.doFilter(request, response);
	        	return;
			}
			//易世财税
			if(url.contains("/services/YscsService"))
			{
				filterChain.doFilter(request, response);
	        	return;
			}
			//山东天南星
			if(url.contains("/tnx/service")){
				filterChain.doFilter(request, response);
	        	return;
			}
			//管理平台调用在线会计的凭证保存接口
			if(url.contains("/hessian")){
				filterChain.doFilter(request, response);
	        	return;
			}
			//一键报税登录
			if(url.endsWith("/taxrpt/fastTaxAction!bslogin.action")){
				filterChain.doFilter(request, response);
	        	return;
			}

			//江苏报税，回写状态接口。------
			if(url.endsWith("/taxrpt/taxDeclarAction!updateDeclareStatusJs.action")){
				filterChain.doFilter(request, response);
	        	return;
			}
			
			//扫码登录begin		    
		    if ((url.endsWith("/css/font/font_1451959379_8626566.eot")) 
		    		|| (url.endsWith("/css/font/font_1451959379_8626566.svg"))
		    		|| (url.endsWith("/css/font/font_1451959379_8626566.ttf")) 
		    		|| (url.endsWith("/css/font/font_1451959379_8626566.woff"))) {
		        filterChain.doFilter(request, response);
		        return;
		    }
		    //websocket---字体通过
		    if(url.endsWith("layui/font/iconfont.woff")
		    		|| url.endsWith("layui/font/iconfont.ttf")){
		    	 filterChain.doFilter(request, response);
			     return;
		    }

			if (url.endsWith("/app/loginqr!getQRCode.action") || url.endsWith("/app/loginqr!getQRCode2.action")
					|| url.endsWith("/app/loginqr!longConnCheck.action") || url.endsWith("/app/loginqr!longConnCheck2.action")){
				filterChain.doFilter(request, response);
				return;
			}
			//扫码登录end
			
		    if(session!=null){
		    	String userid = (String)session.getAttribute(IGlobalConstants.login_user);
		    	String corp=(String) session.getAttribute(IGlobalConstants.login_corp);
		    	
		    	CorpVO corpVo =null;
/*		    	if(StringUtil.isEmptyWithTrim(corp)==false)
		    		corpVo=CorpCache.getInstance().get(userid,corp ) ;//(CorpVO)session.getAttribute(IGlobalConstants.login_corp);
*/		    	
		    	
		    	if(! url.endsWith("sm_user!logout.action") &&  ! url.endsWith("register_act!saveRegistered.action") && !url.endsWith("sm_user!updatePwdLogin.action") && ! url.endsWith("forgot_password.jsp") && ! url.endsWith("dz_registered.jsp") 
		    			&& (userid == null || (corp == null && !url.endsWith("/selcomp.jsp") && !url.endsWith("/sys/sm_user!gsSelect.action") && !url.endsWith("/sys/sm_user!gsQuery.action") && !url.endsWith("/sys/sm_user!gsSelectAdmin.action") && !url.endsWith("/sys/sm_user!gsQueryAdmin.action") && !url.endsWith("/taxrpt/fastTaxAction!getClientList.action"))) 
		    			&& !url.endsWith("/sys/sm_user!login.action") && !url.endsWith("/sys/sm_user!getLogin.action") && !url.endsWith("/sys/sm_user!loginForFactory.action") &&!url.endsWith("/fct/fct_statistics!query.action")&& !url.endsWith("/sys/sm_user!login2.action") && !url.endsWith("/sys/sm_user!dzfLogin.action") && !url.endsWith("/searchPsw.jsp") && !url.endsWith("/st/searchPsw!getPswBack.action") 
		    			&& !url.endsWith("/st/searchPsw!sandYZcode.action") && !url.endsWith("/sys/sm_user!channelLogin.action") ){
	    			if (StringUtil.isEmpty(token) == false)
	    			{
	    				DzfCookieTool.deleteCookie(request, response);
	    				
	    			}
	    			if (StringUtil.isEmpty(uuid_cookie) == false)
	    			{
	    				SessionCache.getInstance().removeByUUID(uuid_cookie, pk_user, appid, session.getMaxInactiveInterval(),clientid);
	    			}
	    			DzfSessionTool.clearSession(session);
		    		if(url.endsWith(".action")){
			    		String message = (String)session.getAttribute(IGlobalConstants.logout_msg);
			    		if(message == null) message = "请先登陆!";
		    			String json = "{\"msg\": \"" + message + "\",\"rows\": [],\"success\": false,\"total\": 0}";
		    			response.setContentType("text/html;charset=utf-8");
		    			response.getWriter().write(json);
//			    		session.setAttribute("errorMsg", message);
//						req.getRequestDispatcher("/error_kj.jsp").forward(req,res);
	   				 	return;
			    	}
			    	if (isForbiddenRedirect(longurl) == false && useSSOServer) {
			    		String contextpath = req.getContextPath();
			    		longurl = (longurl.startsWith("https://") || req.getServerPort() == 443 ? "https://" : "http://") 
			    					+ req.getServerName() 
			    					+ (req.getServerPort() == 80 || req.getServerPort() == 443 ? "" : ":" + String.valueOf(req.getServerPort())) 
			    					+ (StringUtil.isEmptyWithTrim(contextpath) ? "/login.jsp" : contextpath.trim() + "/login.jsp");
			    		String encoderURL = URLEncoder.encode(longurl, "UTF-8");
						//没有用户，也没有ticket
						//跳转至ssoserver用户登录
						StringBuffer newurl = new StringBuffer();
						newurl.append(ssoserver);
						newurl.append(loginjsp);
						newurl.append("?service=");
						newurl.append(encoderURL);
						newurl.append("&appid=");
						newurl.append(appid);
						res.sendRedirect(newurl.toString());
			    	}
			    	else
			    	{
			    		req.getRequestDispatcher("/login.jsp").forward(req,res);
			    	}
   				 	return;
		    	}
/*boolean b=false;
try{
	b = RSACoderUtils.validateToken(session);
}catch(Exception e){
	session.setAttribute("errorMsg", "无权操作,请联系管理员!");
//	req.getRequestDispatcher("/error_kj.jsp").forward(req,res);
	 	return;
}*/
//				String path =req.getServletPath();
//		    	if(userid!=null&&corpVo!=null&&!path.endsWith("/index.jsp")){
//		    		if(!checkPageAuth( (HttpServletRequest)request,userid,corpVo.getPk_corp())){
//		    			request.getRequestDispatcher(errorPage).forward(request, response);//璺宠浆鍒颁俊鎭彁绀洪〉闈紒锛�
//		    		}
//		    	}
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
	
	private Boolean checkPageAuth(HttpServletRequest request,String userid,String pk_corp){
		
		String path = request.getRequestURI();
//		String path = request.getServletPath().substring(0, request.getServletPath().indexOf("!"));
		
		String sql = new String("SELECT 1 FROM SM_POWER_FUNC POWER "+
				" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE "+
				" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE "+
				" WHERE ROL.CUSERID =? AND ROL.PK_CORP=?  AND FILE_DIR = ? AND NVL(POWER.DR,0)=0 AND NVL(ROL.DR,0)=0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(userid);
		sp.addParam(pk_corp);
		sp.addParam(path);
		
		WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
		SingleObjectBO sbo = wc.getBean(SingleObjectBO.class);
		
		String c = (String)sbo.executeQuery(sql, sp, new ColumnProcessor());
		
		return c!=null;
	}
	
}
