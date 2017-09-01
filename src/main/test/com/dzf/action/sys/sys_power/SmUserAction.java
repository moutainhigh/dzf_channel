package com.dzf.action.sys.sys_power;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.action.sys.sys_set.PinyinUtil;
import com.dzf.model.pub.DZFSessionVO;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.LoginLogVo;
import com.dzf.model.sys.sys_power.SysFunNodeVO;
import com.dzf.model.sys.sys_power.UserLoginVo;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.owner.pub.DesUtils;
import com.dzf.owner.pub.JVMCache;
import com.dzf.pub.BusinessException;
import com.dzf.pub.CheckPwdSecurity;
import com.dzf.pub.CommonPassword;
import com.dzf.pub.CommonUtil;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfSessionContext;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.SSOServerUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.FunNodeCache;
import com.dzf.pub.cache.LoginCache;
import com.dzf.pub.cache.NodeUrlConst;
import com.dzf.pub.cache.SessionCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.cache.UserRolCache;
import com.dzf.pub.framework.rsa.Encode;
import com.dzf.pub.framework.rsa.RSACoderUtils;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.session.DzfCookieTool;
import com.dzf.pub.session.DzfSessionTool;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.RSAUtils;
import com.dzf.service.gl.gl_bdset.IWorkMachineService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.sys.sys_power.ISysFunnodeService;
import com.dzf.service.sys.sys_power.IUserService;

@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "sm_user")
public class SmUserAction extends BaseAction<UserVO>{

	private Logger log = Logger.getLogger(this.getClass());
	private IUserService userService = null;
	private ISysFunnodeService sysFunnodeService = null;
	private IWorkMachineService workMachineService =null;
	
	
	public IWorkMachineService getWorkMachineService() {
		return workMachineService;
	}
	@Autowired
	public void setWorkMachineService(IWorkMachineService workMachineService) {
		this.workMachineService = workMachineService;
	}
	public IUserService getUserService() {
		return userService;
	}
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	
	public ISysFunnodeService getSysFunnodeService() {
		return sysFunnodeService;
	}
	
	@Autowired
	public void setSysFunnodeService(ISysFunnodeService sysFunnodeService) {
		this.sysFunnodeService = sysFunnodeService;
	}
	
	private void addRedisSessionAndCookie(String userid, Json json) throws BusinessException {

		getSession().setAttribute(IGlobalConstants.remote_address, getRequest().getRemoteAddr());
		
		//写cookie到客户端
		DzfCookieTool.writeCookie(getSession(), getRequest(), getResponse());
		
		//session同步至redis服务器
		SessionCache.getInstance().addSession(getSession());

		
		String service = getRequest().getParameter("service");
		
		if (StringUtil.isEmptyWithTrim(service) == false) {
			
			HttpSession hs = getSession();
			if (hs.getAttribute(IGlobalConstants.login_token) == null)
			{
				RSACoderUtils.createToken(getSession());
			}
			

			
			String ticket = userid + System.currentTimeMillis();
			String encryptTicket = SSOServerUtils.encryptByPublicKey(ticket);
			
			DZFSessionVO sessionvo = DzfSessionTool.createSession(getSession());

			SSOServerUtils.putTicket(ticket, sessionvo);


			StringBuilder url = new StringBuilder();
			url.append(service);
			if (0 <= service.indexOf("?")) {
				url.append("&");
			} else {
				url.append("?");
			}
			url.append("t=").append(encryptTicket);
			json.setHead(url.toString());

		}
	}
	
	//会计管理端登录
	public void login2() {
 		getRequest().getSession().removeAttribute(IGlobalConstants.logout_msg);
		Json json = new Json();
		LoginLogVo loginLogVo = getLoginVo("kj_admin");
		String password = null;//data.getUser_password();
		String usercode = null;
		try{
			password = data.getUser_password();
			password=RSAUtils.decryptStringByJs(password);
			usercode = RSAUtils.decryptStringByJs(data.getUser_code());
			data.setUser_code(usercode);
		}catch(Exception e){
			getSession().removeAttribute("rand");
			log.error(e);
			json.setSuccess(false);
			json.setStatus(-200);
			json.setMsg("用户名、密码或验证码错误!");
			writeJson(json);
			return;
		}
		try {
			String date =  getRequest().getParameter("date");
			String clientid = getRequest().getParameter("clientid");
			if(!checkUserInfo()) return;
			data.setUser_code(data.getUser_code().trim());
			UserLoginVo userLoginVo = LoginCache.getUserVo(data.getUser_code());
			UserVO userVo = userService.loginByCode(data);
		//	UserVO userVo = userService.queryById("10|eA31000000000000Y");
			
			loginLogVo.setPk_user(data.getUser_code());
			
			if(userVo == null){ 
				getSession().removeAttribute("rand");
				json.setMsg("用户名、密码或验证码错误!");
				loginLogVo.setMemo("密码错误");
			}else if(!userVo.getUser_password().equals(new Encode().encode(password))){
				getSession().removeAttribute("rand");
				LoginCache.loginFail(userVo.getUser_code());
				userLoginVo = LoginCache.getUserVo(data.getUser_code());
				json.setStatus(userLoginVo.getNumber());
				if(userLoginVo.getNumber()>=IGlobalConstants.lock_fail_login){
					json.setMsg("提示：您的账号已被锁定! "+(IGlobalConstants.lock_login_min-(new Date().getTime() - userLoginVo.getLogin_date().getTime())/(60*1000))+"分钟之后解锁  !");
					loginLogVo.setMemo("被锁定");
					userService.loginLog(loginLogVo);
					writeJson(json);
					return;
				}
//				json.setMsg("用户名或密码错误!失败" + (IGlobalConstants.lock_fail_login - userLoginVo.getNumber()) + "次后将被锁定！");
				json.setMsg("用户名、密码或验证码错误!");
				loginLogVo.setMemo("密码错误");
			}else{
				LoginCache.ClearUserVo(userVo.getUser_code());
				json.setStatus(userLoginVo.getNumber());
				loginLogVo.setPk_user(userVo.getPrimaryKey());
				if(IDefaultValue.DefaultGroup.equals(userVo.getPk_corp()) || (userVo.getBappuser() != null && userVo.getBappuser().booleanValue())){
					getSession().removeAttribute("rand");
					json.setMsg("无权操作!");
					loginLogVo.setMemo("无权操作");
				} else if(userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()){
					getSession().removeAttribute("rand");
					json.setMsg("当前用户被锁定，请联系管理员!");
					loginLogVo.setMemo("锁定");
				}
				//  2016-04-27  去掉   管理权限通过权限分配控制
//				else if(userVo.getIsmanager() == null || !userVo.getIsmanager().booleanValue()){
//					getSession().removeAttribute("rand");
//					json.setMsg("无管理权限!");
//					loginLogVo.setMemo("无管理权限");
//				}
				
				else if(userVo.getAble_time() == null || userVo.getAble_time().compareTo(new DZFDate()) > 0){
					getSession().removeAttribute("rand");
					json.setMsg("用户还未生效!");
					loginLogVo.setMemo("未生效");
				}else{
					loginLogVo.setMemo("登陆成功");
					StringBuffer sf = new StringBuffer();
					UserCache.getInstance().remove(userVo.getCuserid());
					if(!checkUserPWD(password,sf)){
						loginLogVo.setLoginstatus(1);
						loginLogVo.setPk_corp(userVo.getPk_corp());
						loginLogVo.setPk_user(userVo.getPrimaryKey());
						loginLogVo.setMemo("登陆成功");
						
						getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
						getSession().setAttribute(IGlobalConstants.login_date, date);
						
						json.setSuccess(false);
						json.setStatus(-200);
						if(sf.length()>0){
							json.setMsg(sf.toString());
						}else{
							json.setMsg("抱歉，您的密码过于简单，请您修改密码后再登陆系统！");
						}
						loginLogVo.setMemo("密码简单");
						userService.loginLog(loginLogVo);
						writeJson(json);
						return;
					}
//					if(DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()) != null && !DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()).getId().equals(getSession().getId())){
					if (isOtherClientOnline(userVo.getPrimaryKey(), IGlobalConstants.ADMIN_KJ,clientid))
					{
						String force = getRequest().getParameter("f");
					
						if(force != null && force.equals("1")){
							try{
								LoginLogVo loginOldVo = getLoginVo("kj_admin");
								loginOldVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
								loginOldVo.setLogouttype(2);
								loginOldVo.setPk_user(loginLogVo.getPk_user());
//								loginOldVo.setLoginsession(DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()).getId());
								getUserService().logoutLog(loginOldVo);
							}catch(Exception e){
								log.error(e);
							}
							loginLogVo.setMemo("强制登陆");
//							DzfSessionContext.getInstance().DelUserSessionByPkUser(userVo.getPrimaryKey(),false);
						}else{
							//重新把验证码放入缓存
							String uuid = (String)getSession().getAttribute(IGlobalConstants.uuid);
							SessionCache.getInstance().addVerify(uuid, getRequest().getParameter("verify"));
							
							json.setSuccess(false);
							json.setStatus(-100);
							json.setMsg("当前用户已经登陆，是否强制退出？");
							loginLogVo.setMemo("已登陆");
							userService.loginLog(loginLogVo);
							writeJson(json);
							return;
						}
					}
					List<SysFunNodeVO> lnode = sysFunnodeService.querySysnodeByUser(userVo, IGlobalConstants.ADMIN_KJ);
//					Set<String> dzfMap = new HashSet<String>();
//					dzfMap.add("/");
//					if(lnode != null && lnode.size()>0){
//						for(SysFunNodeVO nodeVo:lnode){
//							if(nodeVo.getNodeurl()!=null&&nodeVo.getNodeurl().trim().length()>0){
//								String[] urlAddr = nodeVo.getNodeurl().split(",");
//								if(urlAddr != null && urlAddr.length > 0){
//									for(String url : urlAddr){
//										//System.out.println(url);
//										dzfMap.add(url);
//									}
//								}
//							}
//						}
//					}
					//登录缓存节点URL，改为缓存index
					Set<Integer> dzfMap = new HashSet<Integer>();
					if (lnode != null && lnode.size() > 0) {
						for (SysFunNodeVO nodeVo : lnode) {
							if (nodeVo.getFile_dir() != null && nodeVo.getFile_dir().trim().length() > 0) {
								dzfMap.add(NodeUrlConst.getInstance().getIndexMap().get(nodeVo.getPrimaryKey()));
							}
						}
					}
					getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
//					getSession().setAttribute(IGlobalConstants.login_corp,userVo.getPk_corp());//etPk_corp
					json.setSuccess(true);
					json.setStatus(200);
					json.setRows(userVo);
					json.setMsg("登陆成功!");
					loginLogVo.setLoginstatus(1);
					loginLogVo.setPk_user(userVo.getPrimaryKey());
					loginLogVo.setPk_corp(userVo.getPk_corp());
					CorpCache.getInstance().remove(userVo.getPk_corp());
					getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
					getSession().setAttribute(IGlobalConstants.login_date, date);
//					DzfSessionContext.getInstance().AddUserSession(getSession());

					RSACoderUtils.createToken(getSession());
					
					getSession().setAttribute(IGlobalConstants.appid, IGlobalConstants.ADMIN_KJ);	//会计管理端

					//未选择公司的部分登录成功，也要写session信息和cookie，否则登录不能支持分布式
					addRedisSessionAndCookie(userVo.getPrimaryKey(), json);		
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		if(json.isSuccess()){
			json.setHead(getRequest().getContextPath()+"/selcomp.jsp?appid=" + IGlobalConstants.ADMIN_KJ);
		}
		userService.loginLog(loginLogVo);
		writeJson(json);
	}
	
	private LoginLogVo getLoginVo(String project){
			LoginLogVo loginLogVo =  new LoginLogVo();
			try{
				loginLogVo.setLogindate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
				loginLogVo.setLoginsession(getSession().getId());
				loginLogVo.setLoginstatus(0);
				loginLogVo.setProject_name(project);
				loginLogVo.setLoginip(CommonUtil.getIpAddr(getRequest()));
			}catch(Exception e){
				log.error(e);
			}
			return loginLogVo;
	}
	

	
	 public void getLogin() {
			/*Json json = new Json();
			if(data == null || data.getUser_code() == null || data.getUser_code().trim().length() == 0 ){
				json.setMsg("用户名不能为空!");
				json.setSuccess(false);
				writeJson(json);
				return;
			}
			UserVO userVo = userService.loginByCode(data);
			json.setSuccess(true);
			if(userVo != null){
				LoginCache.ClearUserVoByTime(userVo.getUser_code());
				UserLoginVo vo = LoginCache.getUserVo(userVo.getUser_code());
				if(vo.getNumber()>=IGlobalConstants.lock_fail_login){
					json.setStatus(7);
					json.setMsg("提示：您的账号已被锁定! "+(IGlobalConstants.lock_login_min-(new Date().getTime() - vo.getLogin_date().getTime())/(60*1000))+"分钟之后解锁  !");
				}else if(vo.getNumber()>=IGlobalConstants.verify_fail_login){
					json.setStatus(4);
				}else{
					json.setStatus(1);
				}
			}else{
				json.setStatus(0);
			}
			writeJson(json);*/
		}
	 
	private boolean isOtherClientOnline(String pk_user, String appid,String clientid)
	{
		boolean isOtherOnline = false
				;
		DZFSessionVO session_userid = SessionCache.getInstance().getByUserID(pk_user, appid,clientid);
		if (session_userid != null)
		{
			String uuid = null;
			String token = DzfCookieTool.getToken(getRequest());
			if (token != null)
			{
				String realtoken = DzfCookieTool.getRealToken(token);
				if (realtoken != null)
				{
					String[] sa = realtoken.split(",");
					uuid = sa[0];
				}
			}
			if (uuid == null || session_userid.getUuid().equals(uuid) == false)	//当前用户没建立uuid，或者已建立的uuid与网络已登录的不一致，则其它用户在线。
			{
				isOtherOnline = true;
			}
		}
		return isOtherOnline;
	}
	//财务核算端登录
	public void login() {
 		getRequest().getSession().removeAttribute(IGlobalConstants.logout_msg);
		Json json = new Json();
		LoginLogVo loginLogVo = getLoginVo("dzf_kj");
		json.setSuccess(false);


		String password = null;//data.getUser_password();
		String usercode = null;
		try{
			password = data.getUser_password();
			password=RSAUtils.decryptStringByJs(password);
			usercode = RSAUtils.decryptStringByJs(data.getUser_code());
			data.setUser_code(usercode);
			
		}catch(Exception e){
			getSession().removeAttribute("rand");
			log.error(e);
			json.setSuccess(false);
			json.setStatus(-200);
			json.setMsg("用户名、密码或验证码错误!");
			writeJson(json);
			return;
		}
		
		try {
			String date =  getRequest().getParameter("date");
			String clientid =  getRequest().getParameter("clientid");
			if(!checkUserInfo()) return;
			data.setUser_code(data.getUser_code().trim());
			UserVO userVo = userService.loginByCode(data);
			UserLoginVo userLoginVo = LoginCache.getUserVo(data.getUser_code());
			loginLogVo.setPk_user(data != null && data.getUser_code() != null ? data.getUser_code() : "");
//			sp.addParam(new Encode().encode(vo.getUser_password()));

			if(userVo == null){
				getSession().removeAttribute("rand");
				json.setMsg("用户名、密码或验证码错误!");
				loginLogVo.setMemo("密码错误");
			}else if(!userVo.getUser_password().equals(new Encode().encode(password))){
				getSession().removeAttribute("rand");
				LoginCache.loginFail(userVo.getUser_code());
				userLoginVo = LoginCache.getUserVo(data.getUser_code());
				json.setStatus(userLoginVo.getNumber());
				if(userLoginVo.getNumber()>=IGlobalConstants.lock_fail_login){
					json.setMsg("提示：您的账号已被锁定! "+(IGlobalConstants.lock_login_min-(new Date().getTime() - userLoginVo.getLogin_date().getTime())/(60*1000))+"分钟之后解锁  !");
					loginLogVo.setMemo("被锁定");
					userService.loginLog(loginLogVo);
					writeJson(json);
					return;
				}
//				json.setMsg("用户名或密码错误!失败" + (IGlobalConstants.lock_fail_login - userLoginVo.getNumber()) + "次后将被锁定！");
				json.setMsg("用户名、密码或验证码错误!");
				loginLogVo.setMemo("密码错误");
			}else{
				LoginCache.ClearUserVo(userVo.getUser_code());
				json.setStatus(userLoginVo.getNumber());
				if(IDefaultValue.DefaultGroup.equals(userVo.getPk_corp()) || (userVo.getBappuser() != null && userVo.getBappuser().booleanValue())){
					getSession().removeAttribute("rand");
					json.setMsg("无权操作!");
					loginLogVo.setMemo("无权操作");
				}else if(userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()){
					getSession().removeAttribute("rand");
					json.setMsg("当前用户被锁定，请联系管理员!");
					loginLogVo.setMemo("锁定");
				}/*else if(userVo.getAble_time() == null || userVo.getAble_time().compareTo(new DZFDate(date)) > 0){
					json.setMsg("用户还未生效!");
					loginLogVo.setMemo("未生效");
				}*/else{
					String mac =  getRequest().getParameter("mac");
					String macMsg=workMachineService.checkMac(userVo.getCuserid(),mac,userVo.getPk_corp());
					if(macMsg!=null&&macMsg.equals("success")){
						UserCache.getInstance().remove(userVo.getCuserid());
						json.setSuccess(true);
						json.setRows(userVo);
						json.setMsg("登陆成功!");
						loginLogVo.setLoginstatus(1);
						loginLogVo.setPk_corp(userVo.getPk_corp());
						loginLogVo.setPk_user(userVo.getPrimaryKey());
						loginLogVo.setMemo("登陆成功");
						StringBuffer sf = new StringBuffer();
						if( !checkUserPWD(password,sf)){
							getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
							getSession().setAttribute(IGlobalConstants.login_date, date);
							json.setSuccess(false);
							json.setStatus(-200);
							if(sf.length()>0){
								json.setMsg(sf.toString());
							}else{
								json.setMsg("抱歉，您的密码过于简单，请您修改密码后再登陆系统！");
							}
							loginLogVo.setMemo("密码简单");
							userService.loginLog(loginLogVo);
							writeJson(json);
							return;
						}
						if(isOtherClientOnline(userVo.getPrimaryKey(), IGlobalConstants.DZF_KJ,clientid)){
							String force = getRequest().getParameter("f");

							if(force != null && force.equals("1")){
								try{
									LoginLogVo loginOldVo = getLoginVo("kj_admin");
									loginOldVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
									loginOldVo.setLogouttype(2);
									loginOldVo.setPk_user(loginLogVo.getPk_user());
									loginOldVo.setLoginsession(SessionCache.getInstance().getByUserID(userVo.getPrimaryKey(), IGlobalConstants.DZF_KJ,clientid).getSessionid());
									getUserService().logoutLog(loginOldVo);
								}catch(Exception e){
									log.error(e);
								}
								loginLogVo.setMemo("强制登陆");
//								DzfSessionContext.getInstance().DelUserSessionByPkUser(userVo.getPrimaryKey(),false);
							}else{
								//重新把验证码放入缓存
								String uuid = (String)getSession().getAttribute(IGlobalConstants.uuid);
								SessionCache.getInstance().addVerify(uuid, getRequest().getParameter("verify"));
								
								json.setSuccess(false);
								json.setStatus(-100);
								json.setMsg("当前用户已经登陆，是否强制退出？");
								loginLogVo.setMemo("已登陆");
								userService.loginLog(loginLogVo);
								writeJson(json);
								return;
							}
						}
						getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
						getSession().setAttribute(IGlobalConstants.login_date, date);
//						DzfSessionContext.getInstance().AddUserSession(getSession());
						
						getSession().setAttribute(IGlobalConstants.appid, IGlobalConstants.DZF_KJ); //会计核算端
						
						//未选择公司的部分登录成功，也要写session信息和cookie，否则登录不能支持分布式
						addRedisSessionAndCookie(userVo.getPrimaryKey(), json);
						
					}else{
						getSession().removeAttribute("rand");
						if(macMsg==null){
							json.setMsg("该用户请访问【"+getRequest().getContextPath()+"/loginMac.jsp】登录!");
						}else{
							json.setMsg(macMsg);
						}
						loginLogVo.setMemo("mac");
					}
				}
			}
		} catch (Exception e) {
			getSession().removeAttribute("rand");
			log.error(e);
			json.setSuccess(false);
			json.setStatus(-200);
			json.setMsg("登陆失败！");
		}
		if(json.isSuccess()){
			json.setHead(getRequest().getContextPath()+"/selcomp.jsp?appid=" + IGlobalConstants.DZF_KJ);
		}
		writeJson(json);
		userService.loginLog(loginLogVo);
	}
	private boolean checkUserInfo() {
		Json json = new Json();
		if(data == null || data.getUser_code() == null || data.getUser_code().trim().length() == 0 ){
			getSession().removeAttribute("rand");
			json.setMsg("用户名、密码或验证码错误!");
			writeJson(json);
			return false;
		}
		LoginCache.ClearUserVoByTime(data.getUser_code());
		UserLoginVo uservo = LoginCache.getUserVo(data.getUser_code());
		json.setStatus(uservo.getNumber());
		
		String verify = getRequest().getParameter("verify");
		//rand 从redis读取 2016-07-27
		String rand = SessionCache.getInstance().getVerify((String)getSession().getAttribute(IGlobalConstants.uuid));
		if (rand == null)
		{
			rand = (String)getSession().getAttribute("rand");
		}

//		if(((String)getSession().getAttribute("rand")) != null){
//			String rand = ((String)getSession().getAttribute("rand")).toLowerCase();
		if (rand != null)
		{
			if ( verify==null ||!rand.toLowerCase().equals(verify.toLowerCase()))  {
				getSession().removeAttribute("rand");
				json.setMsg("用户名、密码或验证码错误!");
				writeJson(json);
				return false;
			}	
		}else{
			getSession().removeAttribute("rand");
			json.setMsg("用户名、密码或验证码错误!");
			json.setStatus(uservo.getNumber());
			writeJson(json);
			return false;
			}
		
		//输入密码错误超过三次锁定
		if(uservo.getNumber()>=IGlobalConstants.lock_fail_login){
			getSession().removeAttribute("rand");
			json.setMsg("提示：您的账号已被锁定! "+(IGlobalConstants.lock_login_min-(new Date().getTime() - uservo.getLogin_date().getTime())/(60*1000))+"分钟之后解锁  !");
			writeJson(json);
			return false;
		}/*else if(uservo.getNumber() >=IGlobalConstants.verify_fail_login){
			
			}*/
			
		/*
		 * 登录就要求输验证码
		*/
/*		if(uservo.getNumber()>= 0 ){
			String verify = getRequest().getParameter("verify");
			if(((String)getSession().getAttribute("rand")) != null){
				String rand = ((String)getSession().getAttribute("rand")).toLowerCase();
				if ( verify==null ||!rand.equals(verify.toLowerCase()))  {
					json.setMsg("提示：验证码不正确! ");
					writeJson(json);
					return false;
				}	
			}else{
				json.setMsg("提示：验证码不正确! ");
				json.setStatus(uservo.getNumber());
				writeJson(json);
				return false;
				}
		}*/

		if(data.getUser_password() == null || data.getUser_password().trim().length() == 0){
			getSession().removeAttribute("rand");
			json.setMsg("用户名、密码或验证码错误!");
			writeJson(json);
			return false;
		}
		return true;
	}
	
	//集团管理端登录
	public void dzfLogin() {
 		getRequest().getSession().removeAttribute(IGlobalConstants.logout_msg);
		Json json = new Json();
		json.setSuccess(false);
		LoginLogVo loginLogVo = getLoginVo("dzf_admin");
		loginLogVo.setPk_corp(IDefaultValue.DefaultGroup);
		String password = null;//data.getUser_password();
		try{
			password = data.getUser_password();
			password=RSAUtils.decryptStringByJs(password);
			data.setUser_code(RSAUtils.decryptStringByJs(data.getUser_code()));
		}catch(Exception e){
			getSession().removeAttribute("rand");
			log.error(e);
			json.setSuccess(false);
			json.setStatus(-200);
			json.setMsg("用户名、密码或验证码错误!");
			writeJson(json);
			return;
		}
		try {
			String date =  getRequest().getParameter("date");
			String clientid =  getRequest().getParameter("clientid");
			if(!checkUserInfo()) return;
//			UserVO userVo = userService.login(data);
			data.setUser_code(data.getUser_code().trim());
			UserVO userVo = userService.loginByCode(data);
			UserLoginVo userLoginVo = LoginCache.getUserVo(data.getUser_code());
			loginLogVo.setPk_user(data.getUser_code());
		//	UserVO userVo = userService.queryById("10|eA31000000000000Y");
//			password=RSAUtils.decryptStringByJs(password);
			
			if(userVo == null){
				getSession().removeAttribute("rand");
				json.setMsg("用户名、密码或验证码错误!");
				loginLogVo.setMemo("密码错误");
			}else if(!userVo.getUser_password().equals(new Encode().encode(password))){
				getSession().removeAttribute("rand");
				loginLogVo.setPk_user(userVo.getPrimaryKey());
				LoginCache.loginFail(userVo.getUser_code());
				userLoginVo = LoginCache.getUserVo(data.getUser_code());
				json.setStatus(userLoginVo.getNumber());
				if(userLoginVo.getNumber()>=IGlobalConstants.lock_fail_login){
					json.setMsg("提示：您的账号已被锁定! "+(IGlobalConstants.lock_login_min-(new Date().getTime() - userLoginVo.getLogin_date().getTime())/(60*1000))+"分钟之后解锁  !");
					loginLogVo.setMemo("被锁定");
					userService.loginLog(loginLogVo);
					writeJson(json);
					return;
				}
//				json.setMsg("用户名或密码错误!失败" + (IGlobalConstants.lock_fail_login - userLoginVo.getNumber()) + "次后将被锁定！");
				json.setMsg("用户名、密码或验证码错误!");
				loginLogVo.setMemo("密码错误");
			}else{
				loginLogVo.setPk_user(userVo.getPrimaryKey());
				LoginCache.ClearUserVo(userVo.getUser_code());
				json.setStatus(userLoginVo.getNumber());
				if(userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()){
					getSession().removeAttribute("rand");
					json.setMsg("当前用户被锁定，请联系管理员!");
					loginLogVo.setMemo("锁定");
				}else{
//					CorpVO corpVo =CorpCache.getInstance().get(userVo.getPrimaryKey(), IDefaultValue.DefaultGroup);// userService.querypowercorpById(id);
					/*if(corpVo == null){
						json.setMsg("公司不存在!");
					}*/if(! IDefaultValue.DefaultGroup.equals(userVo.getPk_corp())){
						getSession().removeAttribute("rand");
						json.setMsg("无权操作!");
						loginLogVo.setMemo("无权操作");
					}else if(userVo.getAble_time() == null || userVo.getAble_time().compareTo(new DZFDate()) > 0){
						getSession().removeAttribute("rand");
						json.setMsg("用户还未生效!");
						loginLogVo.setMemo("用户未生效");
					}else{
						loginLogVo.setMemo("登陆成功");
						if(isOtherClientOnline(userVo.getPrimaryKey(), IGlobalConstants.SYS_DZF,clientid)){
							String force = getRequest().getParameter("f");
					
							if(force != null && force.equals("1")){
								try{
									LoginLogVo loginOldVo = getLoginVo("kj_admin");
									loginOldVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
									loginOldVo.setLogouttype(2);
									loginOldVo.setPk_user(loginLogVo.getPk_user());
//									loginOldVo.setLoginsession(DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()).getId());
									getUserService().logoutLog(loginOldVo);
								}catch(Exception e){
									log.error(e);
								}
								loginLogVo.setMemo("强制登陆");
//								DzfSessionContext.getInstance().DelUserSessionByPkUser(userVo.getPrimaryKey(),false);
							}else{
								//重新把验证码放入缓存
								String uuid = (String)getSession().getAttribute(IGlobalConstants.uuid);
								SessionCache.getInstance().addVerify(uuid, getRequest().getParameter("verify"));
								
								json.setSuccess(false);
								json.setStatus(-100);
								json.setMsg("当前用户已经登陆，是否强制退出？");
								loginLogVo.setMemo("已登陆");
								userService.loginLog(loginLogVo);
								writeJson(json);
								return;
							}
						}
						List<SysFunNodeVO> lnode = sysFunnodeService.querySysnodeByUser(userVo,
								IGlobalConstants.SYS_DZF);
						// 登录缓存节点URL，改为缓存index
						Set<Integer> dzfMap = new HashSet<Integer>();
						if (lnode != null && lnode.size() > 0) {
							for (SysFunNodeVO nodeVo : lnode) {
								if (nodeVo.getFile_dir() != null && nodeVo.getFile_dir().trim().length() > 0) {
									dzfMap.add(NodeUrlConst.getInstance().getIndexMap().get(nodeVo.getPrimaryKey()));
								}
							}
						}
						getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
						json.setSuccess(true);
						json.setMsg("登陆成功!");
						userVo.setUser_name(CodeUtils1.deCode(userVo.getUser_name()));
						json.setRows(userVo);
						loginLogVo.setLoginstatus(1);
						CorpCache.getInstance().remove(userVo.getPk_corp());
						UserCache.getInstance().remove(userVo.getPrimaryKey());
						getSession().setAttribute(IGlobalConstants.login_corp,userVo.getPk_corp());//etPk_corp
						getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
						getSession().setAttribute(IGlobalConstants.login_date, date);
//						DzfSessionContext.getInstance().AddUserSession(getSession());
						RSACoderUtils.createToken(getSession());
						
						getSession().setAttribute(IGlobalConstants.appid, IGlobalConstants.SYS_DZF); //集团管理端
						//写客户端cookie和redis缓存
						addRedisSessionAndCookie(userVo.getPrimaryKey(), json);					//集团管理端不选择公司
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
			json.setMsg("登陆失败!");
		}
		if(json.isSuccess()){//如果登录成功
			writeLogRecord(LogRecordEnum.OPE_JITUAN_SYS.getValue(),"系统登录成功",ISysConstants.SYS_0);
		}
		writeJson(json);
		userService.loginLog(loginLogVo);
	}
	
	public void gsQuery() {
		Json json = new Json();
		try {
			UserVO userVo=getLoginUserInfo();
			if(userVo == null){
				json.setMsg("请先登陆！");
			}else{
				if(userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()){
					json.setMsg("当前用户被锁定，请联系管理员!");
				}else{
					List<CorpVO> list = userService.queryPowerCorpKj(userVo);
					if (list != null && list.size() > 0) {
						String pyfirstcomb = null;
						Map<String, String> syMap = userService.queryCorpSyByUser(userVo.getCuserid());
						String currentYear = new DZFDate().toString().substring(0, 4);
						for (CorpVO corpVO : list) {
							try {
								corpVO.setUnitname(CodeUtils1.deCode(corpVO.getUnitname()));
								
								pyfirstcomb = PinyinUtil.getFirstSpell(corpVO.getUnitname())
										+ PinyinUtil.getPinYin(corpVO.getUnitname());
								corpVO.setPyfirstcomb(pyfirstcomb);//客户名称拼音首字母
								
								String syPeriod = syMap
										.get(corpVO.getPk_corp());
								String accPeriod = syPeriod == null ? DateUtils
										.getPeriod(corpVO.getBegindate())
										: new DZFDate(DateUtils.getNextMonth(
												new DZFDate(syPeriod + "-01").getMillis()))
												.toString().substring(0, 7);
								corpVO.setAccountProgress((currentYear
										.equals(accPeriod.substring(0, 4)) ? ""
										: accPeriod.substring(0, 4) + "年")
										+ accPeriod.substring(5, 7) + "月");
							} catch (Exception e) {
								log.error(e);
							}
						}
					}
					json.setSuccess(true);
					json.setRows(list);
					json.setMsg("登陆成功!");
				}
			}
			writeJson(json,new CorpVO());
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void gsSelect() {
		String id = (String) getRequest().getAttribute("id");
		Json json = new Json();
		try {
			UserVO userVo=getLoginUserInfo();
			if(userVo == null){
				json.setMsg("请先登陆！");
			}else{
				if(userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()){
					json.setMsg("当前用户被锁定，请联系管理员!");
				}else{
					DZFDate optDate = null;
					try{
						optDate = new DZFDate((String)getSession().getAttribute(IGlobalConstants.login_date));
					}catch(Exception e){
						log.error(e);
						json.setSuccess(false);
						json.setMsg("请先登陆，选择日期!");
						writeJson(json);
					}
					CorpVO corpVo =CorpCache.getInstance().get(userVo.getPrimaryKey(), id,true);// userService.querypowercorpById(id);
					Set<String> corps=  userService.querypowercorpSet(userVo);
					if(!corps.contains(corpVo.getPk_corp())){
						json.setMsg("公司不存在!");
					} else {
						if(corpVo == null){
							json.setMsg("公司不存在!");
						}else if(corpVo.getBegindate() == null){
							json.setMsg("请初始化公司开账日期!");
						}/*else if(corpVo.getBegindate().compareTo(optDate) > 0){
							json.setMsg("登陆日期不能小于开账日期!");
						}*/else if(corpVo.getBegindate().compareTo(optDate) > 0){
							json.setMsg("登陆日期不能小于开账日期!");
						}else if(userVo.getDisable_time() != null && userVo.getDisable_time().compareTo(optDate) < 0){
							json.setMsg("登陆日期不能大于用户失效日期!");
						}else{
							List<SysFunNodeVO> lfunnode = sysFunnodeService.querySysnodeByUserAndCorp(userVo, corpVo, IGlobalConstants.DZF_KJ);
							Set<Integer> dzfMap = new HashSet<Integer>();
							if (lfunnode != null && lfunnode.size() > 0) {
								for (SysFunNodeVO nodeVo : lfunnode) {
									if (nodeVo.getFile_dir() != null && nodeVo.getFile_dir().trim().length() > 0) {
										dzfMap.add(NodeUrlConst.getInstance().getIndexMap().get(nodeVo.getPrimaryKey()));
									}
								}
							}
							getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
							json.setSuccess(true);
							LoginLogVo loginLogVo = getLoginVo("dzf_kj");
							loginLogVo.setLoginstatus(1);
							loginLogVo.setPk_corp(corpVo.getPk_corp());
							loginLogVo.setPk_user(userVo.getCuserid());
							loginLogVo.setMemo("选公司");
							userService.loginLog(loginLogVo);
							json.setMsg("选择公司!");
							json.setRows(corpVo);
							CorpCache.getInstance().remove(corpVo.getPk_corp());
							getSession().setAttribute(IGlobalConstants.login_corp,corpVo.getPk_corp());//etPk_corp
							RSACoderUtils.createToken(getSession());
							
							//核算端选择公司成功后，加redis缓存和cookie ztc 2016-06-28
							addRedisSessionAndCookie(userVo.getCuserid(), json);
							
						}
					}
					if(data != null && data.getUser_code() != null && data.getUser_password() != null){
						String password = data.getUser_password();
						password=RSAUtils.decryptStringByJs(password);
						String usercode = RSAUtils.decryptStringByJs(data.getUser_code());
						StringBuffer sf = new StringBuffer();
						CheckPwdSecurity.checkUserPWD2(usercode, password,sf);
						
						String service = getRequest().getParameter("service");
						
						if (StringUtil.isEmptyWithTrim(service) == false) {
							if(sf.length()>0)
								json.setMsg("1");
						}
						else
						{
							if(sf.length()>0)
								json.setHead("密码不安全，请及时修改！");
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		if(json.isSuccess()){//如果登录成功
			writeLogRecord(LogRecordEnum.OPE_KJ_SYS.getValue(),"系统登录成功",ISysConstants.SYS_2);
		}
		writeJson(json);
	}
	//快速切换
	public void ksqh() {
		String date = getRequest().getParameter("date");
		String id = getRequest().getParameter("corp_id"); 
		Json json = new Json();
		try {
			UserVO userVo=getLoginUserInfo();
			if(userVo == null){
				json.setMsg("请重新登陆!");
			}else{
				Set<String> corps=  userService.querypowercorpSet(userVo);
				if(!corps.contains(id)){
					json.setMsg("对不起，您无操作权限！");
					json.setSuccess(false);
					writeJson(json);
					return;
				}
				DZFDate optDate = null;
				try{
					optDate = new DZFDate(date);
				}catch(Exception e){
					log.error(e);
					json.setSuccess(false);
					json.setMsg("请先登陆，选择日期!");
					writeJson(json);
				}
				if(userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()){
					json.setMsg("当前用户被锁定，请联系管理员!");
				}else{
					CorpVO corpVo =CorpCache.getInstance().get(userVo.getPrimaryKey(), id);// userService.querypowercorpById(id);
					json.setStatus(-200);
					json.setSuccess(false);
					if(corpVo == null){
						json.setMsg("公司不存在!");
					}else if(corpVo.getBegindate() == null){
						json.setMsg("请初始化公司开账日期!");
					}else if(corpVo.getBegindate().after(optDate)){
						json.setMsg("登陆日期不能小于开账日期!");
					}else if(userVo.getDisable_time() != null && userVo.getDisable_time().compareTo(optDate) < 0){
						json.setMsg("登陆日期不能大于用户失效日期!");
					}else{
						List<SysFunNodeVO> lfunnode = sysFunnodeService.querySysnodeByUserAndCorp(userVo, corpVo, IGlobalConstants.DZF_KJ);
						Set<Integer> dzfMap = new HashSet<Integer>();
						if (lfunnode != null && lfunnode.size() > 0) {
							for (SysFunNodeVO nodeVo : lfunnode) {
								if (nodeVo.getFile_dir() != null && nodeVo.getFile_dir().trim().length() > 0) {
									dzfMap.add(NodeUrlConst.getInstance().getIndexMap().get(nodeVo.getPrimaryKey()));
								}
							}
						}
						getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
						json.setStatus(200);
						json.setSuccess(true);
//						json.setMsg("切换成功!");
						json.setRows(corpVo);
						CorpCache.getInstance().remove(corpVo.getPk_corp());
						getSession().setAttribute(IGlobalConstants.login_corp,corpVo.getPk_corp());//etPk_corp
						getSession().setAttribute(IGlobalConstants.login_date, date);
						json.setMsg(date);
						RSACoderUtils.createToken(getSession());
						
						//redis 更新session信息 ztc 2016-07-18
						SessionCache.getInstance().addSession(getSession());
					}
				}
			}
			writeJson(json,new CorpVO());
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void logout() throws ServletException, IOException{
		String pk_user = (String)getSession().getAttribute(IGlobalConstants.login_user);
		String uuid = (String)getSession().getAttribute(IGlobalConstants.uuid);
		String appid = (String)getSession().getAttribute(IGlobalConstants.appid);
		String clientid = (String)getSession().getAttribute(IGlobalConstants.clientid);
		//s删除redis缓存
		SessionCache.getInstance().removeByUUID(uuid, pk_user, appid, getSession().getMaxInactiveInterval(),clientid);

		//清理httpsession
		DzfSessionTool.clearSession(getSession());
		//删除cookie
		DzfCookieTool.deleteCookie(getRequest(), getResponse());
		
//		getSession().removeAttribute(IGlobalConstants.login_corp);
//		getSession().removeAttribute(IGlobalConstants.login_date);
//		getSession().removeAttribute(IGlobalConstants.login_user);
//		getSession().removeAttribute(IGlobalConstants.POWER_MAP); 
//		DzfSessionContext.getInstance().DelUserSessionByPkUser(pk_user,true);
		if(pk_user != null){
			LoginLogVo loginLogVo = getLoginVo("kj_admin");
			loginLogVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			loginLogVo.setLogouttype(1);
			loginLogVo.setPk_user(pk_user);
			getUserService().logoutLog(loginLogVo);
		}
		
		getResponse().sendRedirect(getRequest().getContextPath()+"/login.jsp"); 
		return;

	}
	
	
	public void getAuthAccessPage(){
		Json json = new Json();
		try{
		UserVO user = getLoginUserInfo();
		CorpVO corp = getLoginCorpInfo();
		
		String path =getRequest().getContextPath();
		
		SysFunNodeVO[] bodyvos = getUserService().getAuthAccessPage(user, corp,path);
		
		json.setStatus(200);
		json.setSuccess(true);
		json.setMsg("初始化成");
		json.setData(bodyvos);
		
		}catch(Exception e){
			json.setStatus(-200);
			json.setSuccess(false);
			json.setMsg("初始化权限失败");
			log.error(e);
		}
		writeJson(json);
		
	}
	
	
	public void checkPageAuth(){
		Json json = new Json();
		json.setStatus(-200);
		json.setSuccess(false);
		json.setMsg("没有改内容权限，请联系管理员!");
		try{
		UserVO user = getLoginUserInfo();
		CorpVO corp = getLoginCorpInfo();
		
		String chkpageid = getRequest().getParameter("pageid");
		
		ArrayList<String> pages = UserRolCache.getInstance().get(user.getCuserid(), corp.getPk_corp(),true);
		
		if(pages!=null&&pages.size()>0&&pages.contains(chkpageid)){
			SysFunNodeVO nodevo =FunNodeCache.getInstance().get(null,chkpageid);
			if(nodevo!=null&&nodevo.getFile_dir()!=null){
				if(nodevo.getFile_dir()!=null&&nodevo.getFile_dir().length()>0){
					json.setStatus(200);
					json.setSuccess(true);
					json.setMsg("初始化成");
					json.setData(nodevo.getFile_dir());
				}else{
					json.setStatus(-200);
					json.setSuccess(false);
					json.setMsg("内容正在建设中!");
					json.setData(nodevo.getFile_dir());
				}
			}
		}
		
		}catch(Exception e){
			json.setStatus(-200);
			json.setSuccess(false);
			json.setMsg("初始化页面失败");
			log.error(e);
		}
		writeJson(json);
	}
	
	//登录成功之后，用户口令更换密码
	public void updatePsw(){
		Json json = new Json();
		UserVO userVo = getLoginUserInfo();
		if(userVo == null){
			json.setMsg("请先登陆");
			json.setStatus(-200);
			json.setSuccess(false);
			writeJson(json);
			return;
		}
		UserVO oldUser = userService.queryById(userVo.getPrimaryKey());
		if(data == null || oldUser == null){
			json.setMsg("未找到用户信息！");
			json.setStatus(-200);
			json.setSuccess(false);
			writeJson(json);
			return;
		}
		json.setStatus(-200);
		json.setSuccess(false);
		String psw2 = getRequest().getParameter("psw2");
		psw2=RSAUtils.decryptStringByJs(psw2);
		String psw3 = getRequest().getParameter("psw3");
		psw3=RSAUtils.decryptStringByJs(psw3);
		try{
			
			String password = data.getUser_password();
			password=RSAUtils.decryptStringByJs(password);
			
//			if(data.getUser_password() == null || data.getUser_password().trim().length()== 0 || !(new Encode().encode(data.getUser_password().trim())).equals(oldUser.getUser_password())){
			if(data.getUser_password() == null || data.getUser_password().trim().length()== 0 || !(new Encode().encode(password)).equals(oldUser.getUser_password())){
				json.setMsg("输入初始密码错误！");
			}else if(psw2 != null && psw3 != null && psw2.trim().length() > 0 && psw3.trim().length() > 0 ){
				if(psw2.equals(password)){
					json.setMsg("旧密码和新密码不能一致！");
				}else if(psw2.equals(psw3)){
					oldUser.setUser_password(psw2);
					//修改后密码校验
					StringBuffer sf = new StringBuffer();
					boolean flag = checkUserPWD(psw2,sf);
					if(!flag){
						json.setMsg(sf.toString());
						json.setSuccess(false);
					}else{
						if(data.getPhone() != null && !"".equals(data.getPhone()) && data.getUser_mail() != null && !"".equals(data.getUser_mail())){
							oldUser.setPhone(data.getPhone());
							oldUser.setUser_mail(data.getUser_mail());
						}
						userService.update(oldUser);
						json.setMsg("修改成功!");
						json.setSuccess(true);
						json.setStatus(200);
//						getSession().removeAttribute(IGlobalConstants.login_user);
//						getSession().removeAttribute(IGlobalConstants.login_date);
						
						String pk_user = (String)getSession().getAttribute(IGlobalConstants.login_user);
						String uuid = (String)getSession().getAttribute(IGlobalConstants.uuid);
						String appid = (String)getSession().getAttribute(IGlobalConstants.appid);
						String clientid = (String)getSession().getAttribute(IGlobalConstants.clientid);
						//s删除redis缓存
						SessionCache.getInstance().removeByUUID(uuid, pk_user, appid, getSession().getMaxInactiveInterval(),clientid);

						//清理httpsession
						DzfSessionTool.clearSession(getSession());
						//删除cookie
						DzfCookieTool.deleteCookie(getRequest(), getResponse());
						
					}
				}else{
					json.setMsg("两次输入密码不一致，请检查！");
				}
			}else{
				json.setMsg("请输入密码信息！");
			}
		}catch(Exception e){
			if(e instanceof BusinessException){
				json.setMsg(e.getMessage());
			}else{
				json.setMsg("操作失败");
				log.error("操作失败",e);
			}
			json.setSuccess(false);
		}
		writeJson(json);
	}
	
	//登录更换密码
	public void updatePwdLogin(){
		Json json = new Json();
		//此处改成查询出来,cache 有时候取不出来
		String usrid = getLoginUserid();
		if(StringUtil.isEmpty(usrid)){
			json.setMsg("请先登陆");
			json.setStatus(-200);
			json.setSuccess(false);
			writeJson(json);
			return;
		}
		UserVO oldUser = userService.queryById(usrid);
		if(data == null || data.getUser_code() == null){
			json.setMsg("未找到用户信息！");
			json.setStatus(-200);
			json.setSuccess(false);
			writeJson(json);
			return;
		}
		try{
			data.setUser_code(RSAUtils.decryptStringByJs(data.getUser_code()));
		}catch(Exception e){
			log.error(e);
			json.setMsg("未找到用户信息！");
			json.setStatus(-200);
			json.setSuccess(false);
			writeJson(json);
			return;
		}
		
//		UserVO userVo = new UserVO();
//		userVo.setUser_code(data.getUser_code());
//		userVo = userService.loginByCode(userVo);
//		if(userVo != null && userVo.getPrimaryKey() != null){
//			oldUser = userService.queryById(userVo.getPrimaryKey());
//		}
		if(data == null || oldUser == null){
			json.setMsg("未找到用户信息！");
			json.setStatus(-200);
			json.setSuccess(false);
			writeJson(json);
			return;
		}
		json.setStatus(-200);
		json.setSuccess(false);
//		String psw2 = getRequest().getParameter("psw2");
//		String psw3 = getRequest().getParameter("psw3");
		
		String psw2 = getRequest().getParameter("psw2");
		psw2=RSAUtils.decryptStringByJs(psw2);
		String psw3 = getRequest().getParameter("psw3");
		psw3=RSAUtils.decryptStringByJs(psw3);
		
		try{
			
			String password = data.getUser_password();
			password=RSAUtils.decryptStringByJs(password);
			
//			if(data.getUser_password() == null || data.getUser_password().trim().length()== 0 || !(new Encode().encode(data.getUser_password().trim())).equals(oldUser.getUser_password())){
			if(data.getUser_password() == null || data.getUser_password().trim().length()== 0 || !(new Encode().encode(password)).equals(oldUser.getUser_password())){
				
				
				json.setMsg("输入初始密码错误！");
			}else if(psw2 != null && psw3 != null && psw2.trim().length() > 0 && psw3.trim().length() > 0 ){
				if(psw2.equals(psw3)){
					oldUser.setUser_password(psw2);
					//修改后密码校验
					StringBuffer sf = new StringBuffer();
					boolean flag = checkUserPWD(psw2,sf);
					if(!flag){
						json.setMsg(sf.toString());
						json.setSuccess(false);
					}else{
						userService.update(oldUser);
						json.setMsg("修改成功!");
						json.setSuccess(true);
						json.setStatus(200);
						getSession().removeAttribute(IGlobalConstants.login_user);
						getSession().removeAttribute(IGlobalConstants.login_date);
					}
				}else{
					json.setMsg("两次输入密码不一致，请检查！");
				}
			}else{
				json.setMsg("请输入密码信息！");
			}
		}catch(Exception e){
			if(e instanceof BusinessException){
				json.setMsg("失败," + e.getMessage());
			}else{
				json.setMsg("更新失败！");
			}
			log.error(e);
		}
		writeJson(json);
	}
	
	private boolean checkUserPWD(String pwd,StringBuffer eInfo){
		if (pwd.length() < 8){
			eInfo.append("密码长度不能小于8\n");
			return  false;
		}
		if (!pwd.matches(".*([0-9]+.*[A-Za-z]+|[A-Za-z]+.*[0-9]+).*")){
			eInfo.append("密码必须含有数字、字母\n");
			return  false;
		}
		//判断是否为初始化密码
		if(Arrays.asList(CommonPassword.INIT_PASSWORD).contains(pwd)){
			eInfo.append("密码为初始化密码!\n");
			return  false;
		}
		String regEx = "[~!@#$%^&*()<>?+=]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(pwd);
		if (!m.find()){
			eInfo.append("密码必须含有特殊字符\n");
			return  false;
		}
		return true;
	}
	
	// 会计工厂登录
		public void loginForFactory() {
			getRequest().getSession().removeAttribute(IGlobalConstants.logout_msg);
			Json json = new Json();
			LoginLogVo loginLogVo = getLoginVo("dzf_factory");
			String password = null;// data.getUser_password();
			String usercode = null;
			try {
				password = data.getUser_password();
				password = RSAUtils.decryptStringByJs(password);
				usercode = RSAUtils.decryptStringByJs(data.getUser_code());
				data.setUser_code(usercode);
			} catch (Exception e) {
				log.error(e);
				json.setSuccess(false);
				json.setStatus(-200);
				json.setMsg("用户名或密码错误！");
				writeJson(json);
				return;
			}
			try {
				String date = getRequest().getParameter("date");
				String clientid = getRequest().getParameter("clientid");
				if (!checkUserInfo())
					return;
				data.setUser_code(data.getUser_code().trim());
				UserLoginVo userLoginVo = LoginCache.getUserVo(data.getUser_code());
				UserVO userVo = userService.loginByCode(data);
				// UserVO userVo = userService.queryById("10|eA31000000000000Y");

				loginLogVo.setPk_user(data.getUser_code());

				if (userVo == null) {
					json.setMsg("用户名或密码错误!");
					loginLogVo.setMemo("密码错误");
				} else if (!userVo.getUser_password().equals(new Encode().encode(password))) {
					LoginCache.loginFail(userVo.getUser_code());
					userLoginVo = LoginCache.getUserVo(data.getUser_code());
					json.setStatus(userLoginVo.getNumber());
					if (userLoginVo.getNumber() >= IGlobalConstants.lock_fail_login) {
						json.setMsg("提示：您的账号已被锁定! " + (IGlobalConstants.lock_login_min - (new Date().getTime() - userLoginVo.getLogin_date().getTime()) / (60 * 1000)) + "分钟之后解锁  !");
						loginLogVo.setMemo("被锁定");
						userService.loginLog(loginLogVo);
						writeJson(json);
						return;
					}
					json.setMsg("用户名或密码错误!失败" + (IGlobalConstants.lock_fail_login - userLoginVo.getNumber()) + "次后将被锁定！");
					loginLogVo.setMemo("密码错误");
				} else {
					String mac = getRequest().getParameter("mac");
					String macMsg = workMachineService.checkMac(userVo.getCuserid(), mac, userVo.getPk_corp());
					if (macMsg != null && macMsg.equals("success")) {
						LoginCache.ClearUserVo(userVo.getUser_code());
						json.setStatus(userLoginVo.getNumber());
						loginLogVo.setPk_user(userVo.getPrimaryKey());
						if (IDefaultValue.DefaultGroup.equals(userVo.getPk_corp()) || (userVo.getBappuser() != null && userVo.getBappuser().booleanValue())) {
							json.setMsg("无权操作!");
							loginLogVo.setMemo("无权操作");
						} else if (userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()) {
							json.setMsg("当前用户被锁定，请联系管理员!");
							loginLogVo.setMemo("锁定");
						}  else if (userVo.getAble_time() == null || userVo.getAble_time().compareTo(new DZFDate()) > 0) {
							json.setMsg("用户还未生效!");
							loginLogVo.setMemo("未生效");
						} else if(userVo.getDisable_time() != null && userVo.getDisable_time().compareTo(new DZFDate()) < 0){
							json.setMsg("登陆日期不能大于用户失效日期!");
						}else {
							loginLogVo.setMemo("登陆成功");
							StringBuffer sf = new StringBuffer();
							if( !checkUserPWD(password,sf)){
								getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
								getSession().setAttribute(IGlobalConstants.login_date, date);
								json.setSuccess(false);
								json.setStatus(-200);
								if(sf.length()>0){
									json.setMsg(sf.toString());
								}else{
									json.setMsg("抱歉，您的密码过于简单，请您修改密码后再登陆系统！");
								}
								loginLogVo.setMemo("密码简单");
								userService.loginLog(loginLogVo);
								writeJson(json);
								return;
							}
							if(isOtherClientOnline(userVo.getPrimaryKey(), IGlobalConstants.DZF_FACTORY,clientid)){
								String force = getRequest().getParameter("f");

								if (force != null && force.equals("1")) {
									try {
										LoginLogVo loginOldVo = getLoginVo("dzf_factory");
										loginOldVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										loginOldVo.setLogouttype(2);
										loginOldVo.setPk_user(loginLogVo.getPk_user());
//										loginOldVo.setLoginsession(DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()).getId());
										getUserService().logoutLog(loginOldVo);
									} catch (Exception e) {
										log.error(e);
									}
									loginLogVo.setMemo("强制登陆");
//									DzfSessionContext.getInstance().DelUserSessionByPkUser(userVo.getPrimaryKey(), false);
								} else {
									//重新把验证码放入缓存
									String uuid = (String)getSession().getAttribute(IGlobalConstants.uuid);
									SessionCache.getInstance().addVerify(uuid, getRequest().getParameter("verify"));
									
									json.setSuccess(false);
									json.setStatus(-100);
									json.setMsg("当前用户已经登陆，是否强制退出？");
									loginLogVo.setMemo("已登陆");
									userService.loginLog(loginLogVo);
									writeJson(json);
									return;
								}
							}
							List<SysFunNodeVO> lnode = sysFunnodeService.querySysnodeByUser(userVo, IGlobalConstants.DZF_FACTORY);
							Set<Integer> dzfMap = new HashSet<Integer>();
							if (lnode != null && lnode.size() > 0) {
								for (SysFunNodeVO nodeVo : lnode) {
									if (nodeVo.getFile_dir() != null && nodeVo.getFile_dir().trim().length() > 0) {
										dzfMap.add(NodeUrlConst.getInstance().getIndexMap().get(nodeVo.getPrimaryKey()));
									}
								}
							}
							getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
							getSession().setAttribute(IGlobalConstants.login_corp, userVo.getPk_corp());// etPk_corp
							json.setSuccess(true);
							json.setStatus(200);
							json.setRows(userVo);
							json.setMsg("登陆成功!");
							loginLogVo.setLoginstatus(1);
							loginLogVo.setPk_user(userVo.getPrimaryKey());
							loginLogVo.setPk_corp(userVo.getPk_corp());
							CorpCache.getInstance().remove(userVo.getPk_corp());
							getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
							getSession().setAttribute(IGlobalConstants.login_date, date);
//							DzfSessionContext.getInstance().AddUserSession(getSession());

							RSACoderUtils.createToken(getSession());
							
							getSession().setAttribute(IGlobalConstants.appid, IGlobalConstants.DZF_FACTORY);	//会计工厂
							//写客户端cookie和redis缓存
							addRedisSessionAndCookie(userVo.getPrimaryKey(), json);
						}
					} else {
						if (macMsg == null) {
							json.setMsg("该用户请访问【" + getRequest().getContextPath() + "/loginMac.jsp】登录!");
						} else {
							json.setMsg(macMsg);
						}
						loginLogVo.setMemo("mac");
					}
				}
				writeJson(json);
			} catch (Exception e) {
				log.error(e);
			}
			userService.loginLog(loginLogVo);
		}
		
		
	public void gsQueryAdmin() {
		Json json = new Json();
		try {
			UserVO userVo = getLoginUserInfo();
			if (userVo == null) {
				json.setMsg("用户名或密码错误!");
			} else {
				if (userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()) {
					json.setMsg("当前用户被锁定，请联系管理员!");
				} else {
					List<CorpVO> list = userService.queryPowerCorpAdmin(userVo);
					if (list != null && list.size() > 0) {
						for (CorpVO corpVO : list) {
							try {
								corpVO.setUnitname(CodeUtils1.deCode(corpVO.getUnitname()));
							} catch (Exception e) {
								log.error(e);
							}
						}
					}
					json.setSuccess(true);
					json.setRows(list);
					json.setMsg("登陆成功!");
				}
			}
			writeJson(json, new CorpVO());
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void gsSelectAdmin() {
		String id = (String) getRequest().getAttribute("id");
		Json json = new Json();
		try {
			UserVO userVo = getLoginUserInfo();
			if (userVo == null) {
				json.setMsg("用户名或密码错误!");
			} else {
				if (userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()) {
					json.setMsg("当前用户被锁定，请联系管理员!");
				} else {
					DZFDate optDate = null;
					try {
						optDate = new DZFDate(getLoginDate());
					} catch (Exception e) {
						log.error(e);
						json.setSuccess(false);
						json.setMsg("请先登陆，选择日期!");
						writeJson(json);
					}
					CorpVO corpVo = CorpCache.getInstance().get(userVo.getPrimaryKey(), id, true);// userService.querypowercorpById(id);
					Set<String> corps = userService.queryPowerCorpAdSet(userVo);

					if (corpVo == null || corps == null || corps.size() == 0 || !corps.contains(corpVo.getPk_corp())) {
						json.setMsg("公司不存在!");
					} else if (corpVo.getBegindate() == null) {
						json.setMsg("请初始化公司开账日期!");
					} else if (corpVo.getBegindate().compareTo(optDate) > 0) {
						json.setMsg("登陆日期不能小于开账日期!");
					} else if (userVo.getDisable_time() != null && userVo.getDisable_time().compareTo(optDate) < 0) {
						json.setMsg("登陆日期不能大于用户失效日期!");
					} else {
						List<SysFunNodeVO> lfunnode = sysFunnodeService.querySysnodeByUserAndCorp(userVo, corpVo,
								IGlobalConstants.ADMIN_KJ);
						Set<Integer> dzfMap = new HashSet<Integer>();
						if (lfunnode != null && lfunnode.size() > 0) {
							for (SysFunNodeVO nodeVo : lfunnode) {
								if (nodeVo.getFile_dir() != null && nodeVo.getFile_dir().trim().length() > 0) {
									dzfMap.add(NodeUrlConst.getInstance().getIndexMap().get(nodeVo.getPrimaryKey()));
								}
							}
						}
						getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
						json.setSuccess(true);
						LoginLogVo loginLogVo = getLoginVo(IGlobalConstants.ADMIN_KJ);
						loginLogVo.setLoginstatus(1);
						loginLogVo.setPk_corp(corpVo.getPk_corp());
						loginLogVo.setPk_user(userVo.getCuserid());
						loginLogVo.setMemo("选公司");
						userService.loginLog(loginLogVo);
						json.setMsg("选择公司!");
						json.setRows(corpVo);
						CorpCache.getInstance().remove(corpVo.getPk_corp());
						getSession().setAttribute(IGlobalConstants.login_corp, corpVo.getPk_corp());// etPk_corp
						RSACoderUtils.createToken(getSession());
						//写客户端cookie和redis缓存信息
						addRedisSessionAndCookie(userVo.getCuserid(), json);
					}
				}
			}
			writeJson(json);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	// 企业主登录，手机注册用户登录PC
		public void loginForOwner() {
			getRequest().getSession().removeAttribute(IGlobalConstants.logout_msg);
			Json json = new Json();
			LoginLogVo loginLogVo = getLoginVo(IGlobalConstants.DZF_OWNER);
			json.setSuccess(false);

			String password = null;
			String usercode = null;
			try {
				password = data.getUser_password();
				password = RSAUtils.decryptStringByJs(password);
				usercode = RSAUtils.decryptStringByJs(data.getUser_code());
				data.setUser_code(usercode);

			} catch (Exception e) {
				getSession().removeAttribute("rand");
				log.error(e);
				json.setSuccess(false);
				json.setStatus(-200);
				json.setMsg("用户名、密码或验证码错误!");
				writeJson(json);
				return;
			}

			try {
				String date = new DZFDate().toString();
				if (!checkUserInfo())
					return;
				data.setUser_code(data.getUser_code().trim());
				UserVO userVo = userService.loginByOwner(data);
				UserLoginVo userLoginVo = LoginCache.getUserVo(data.getUser_code());
				loginLogVo.setPk_user(data != null && data.getUser_code() != null ? data.getUser_code() : "");

				if (userVo == null) {
					getSession().removeAttribute("rand");
					json.setMsg("用户名、密码或验证码错误!");
					loginLogVo.setMemo("密码错误");
				} else if (!userVo.getUser_password().equals(new Encode().encode(password))) {
					getSession().removeAttribute("rand");
					LoginCache.loginFail(userVo.getUser_code());
					userLoginVo = LoginCache.getUserVo(data.getUser_code());
					json.setStatus(userLoginVo.getNumber());
					if (userLoginVo.getNumber() >= IGlobalConstants.lock_fail_login) {
						json.setMsg("提示：您的账号已被锁定! " + (IGlobalConstants.lock_login_min - (new Date().getTime() - userLoginVo.getLogin_date().getTime()) / (60 * 1000)) + "分钟之后解锁  !");
						loginLogVo.setMemo("被锁定");
						userService.loginLog(loginLogVo);
						writeJson(json);
						return;
					}
					json.setMsg("用户名、密码或验证码错误!");
					loginLogVo.setMemo("密码错误");
				} else {
					LoginCache.ClearUserVo(userVo.getUser_code());
					json.setStatus(userLoginVo.getNumber());
					if (IDefaultValue.DefaultGroup.equals(userVo.getPk_corp())) {
						getSession().removeAttribute("rand");
						json.setMsg("无权操作!");
						loginLogVo.setMemo("无权操作");
					} else if (userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()) {
						getSession().removeAttribute("rand");
						json.setMsg("当前用户被锁定，请联系管理员!");
						loginLogVo.setMemo("锁定");
					} else {

						UserCache.getInstance().remove(userVo.getCuserid());
						json.setSuccess(true);
						json.setRows(userVo);
						json.setMsg("登陆成功!");
						loginLogVo.setLoginstatus(1);
						loginLogVo.setPk_corp(userVo.getPk_corp());
						loginLogVo.setPk_user(userVo.getPrimaryKey());
						loginLogVo.setMemo("登陆成功");
						if (DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()) != null && !DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()).getId().equals(getSession().getId())) {
							String force = getRequest().getParameter("f");

							if (force != null && force.equals("1")) {
								try {
									LoginLogVo loginOldVo = getLoginVo(IGlobalConstants.DZF_OWNER);
									loginOldVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
									loginOldVo.setLogouttype(2);
									loginOldVo.setPk_user(loginLogVo.getPk_user());
									loginOldVo.setLoginsession(DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()).getId());
									getUserService().logoutLog(loginOldVo);
								} catch (Exception e) {
									log.error(e);
								}
								loginLogVo.setMemo("强制登陆");
								DzfSessionContext.getInstance().DelUserSessionByPkUser(userVo.getPrimaryKey(), false);
							} else {
								json.setSuccess(false);
								json.setStatus(-100);
								json.setMsg("当前用户已经登陆，是否强制退出？");
								loginLogVo.setMemo("已登陆");
								userService.loginLog(loginLogVo);
								writeJson(json);
								return;
							}
						}
						getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
						getSession().setAttribute(IGlobalConstants.login_date, date);
						getSession().setAttribute(IGlobalConstants.appid, IGlobalConstants.DZF_OWNER);
						DzfSessionContext.getInstance().AddUserSession(getSession());
					}
				}
			} catch (Exception e) {
				getSession().removeAttribute("rand");
				log.error(e);
				json.setSuccess(false);
				json.setStatus(-200);
				json.setMsg("登陆失败！");
			}
			if (json.isSuccess()) {
				json.setHead(getRequest().getContextPath() + "/selcomp.jsp");
			}
			writeJson(json);
			userService.loginLog(loginLogVo);
		}

		// 企业主登录后写cookie,不走周天成的cookie
		private void addOwnerCookie(String userid, String pk_corp, Json json) throws DZFWarpException {
			HttpServletResponse response = getResponse();
			String callUrl = getRequest().getParameter("callUrl");
			String ticket = null;
			try {
				DesUtils des = new DesUtils();
				String ticketValue = des.encrypt(userid + pk_corp);
				Cookie cookie = new Cookie("ownersso", ticketValue);
				cookie.setPath("/");
				response.addCookie(cookie);
				if (StringUtil.isEmpty(callUrl) && !callUrl.equals("null")) {
					long time = System.currentTimeMillis();
					String strIn = userid + pk_corp + time;
					ticket = des.encrypt(strIn);

					JVMCache.TICKET_AND_NAME.put(ticket, ticketValue);
					StringBuilder url = new StringBuilder();
					url.append(callUrl);
					if (0 <= callUrl.indexOf("?")) {
						url.append("&");
					} else {
						url.append("?");
					}
					url.append("ticket=").append(ticket);
					json.setHead(url.toString());
				}
			} catch (Exception e) {
				throw new WiseRunException(e);
			}
		}
		
		//企业主查询登录用户有权限的公司
		public void gsQueryForOwner() {
			Json json = new Json();
			try {
				UserVO userVo = getLoginUserInfo();
				if (userVo == null) {
					json.setMsg("请先登陆！");
				} else {
					if (userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()) {
						json.setMsg("当前用户被锁定，请联系管理员!");
					} else {
						List<CorpVO> list = userService.queryPowerCorpByOwner(userVo);
						if (list != null && list.size() > 0) {
							for (CorpVO corpVO : list) {
								try {
									corpVO.setUnitname(CodeUtils1.deCode(corpVO.getUnitname()));
								} catch (Exception e) {
									log.error(e);
								}
							}
						}
						json.setSuccess(true);
						json.setRows(list);
						json.setMsg("登陆成功!");
					}
				}
				writeJson(json, new CorpVO());
			} catch (Exception e) {
				getSession().removeAttribute("rand");
				log.error(e);
				json.setSuccess(false);
				json.setStatus(-200);
				json.setMsg(e.getMessage());
				writeJson(json);
			}
		}
		//企业主选择公司
		public void gsSelectForOwner() {
			String id = (String) getRequest().getAttribute("id");
			Json json = new Json();
			try {
				UserVO userVo = getLoginUserInfo();
				if (userVo == null) {
					json.setMsg("请先登陆！");
				} else {
					if (userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()) {
						json.setMsg("当前用户被锁定，请联系管理员!");
					} else {
						DZFDate optDate = null;
						try {
							optDate = new DZFDate((String) getSession().getAttribute(IGlobalConstants.login_date));
						} catch (Exception e) {
							log.error(e);
							json.setSuccess(false);
							json.setMsg("请先登陆，选择日期!");
							writeJson(json);
						}
						CorpVO corpVo = CorpCache.getInstance().get(userVo.getPrimaryKey(), id, true);// userService.querypowercorpById(id);
						Set<String> corps = userService.queryPowerCorpOwnerSet(userVo);
						if (!corps.contains(corpVo.getPk_corp())) {
							json.setMsg("公司不存在!");
						} else {
							if (corpVo == null) {
								json.setMsg("公司不存在!");
							} else if (corpVo.getBegindate() == null) {
								json.setMsg("请初始化公司开账日期!");
							} else if (corpVo.getBegindate().compareTo(optDate) > 0) {
								json.setMsg("登陆日期不能小于开账日期!");
							} else if (userVo.getDisable_time() != null && userVo.getDisable_time().compareTo(optDate) < 0) {
								json.setMsg("登陆日期不能大于用户失效日期!");
							} else {
								Set<Integer> dzfMap = new HashSet<Integer>();
								getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
								json.setSuccess(true);
								LoginLogVo loginLogVo = getLoginVo(IGlobalConstants.DZF_OWNER);
								loginLogVo.setLoginstatus(1);
								loginLogVo.setPk_corp(corpVo.getPk_corp());
								loginLogVo.setPk_user(userVo.getCuserid());
								loginLogVo.setMemo("选公司");
								userService.loginLog(loginLogVo);
								json.setMsg("选择公司!");
								json.setRows(corpVo);
								CorpCache.getInstance().remove(corpVo.getPk_corp());
								getSession().setAttribute(IGlobalConstants.login_corp, corpVo.getPk_corp());// etPk_corp
								RSACoderUtils.createToken(getSession());
								addOwnerCookie(userVo.getPrimaryKey(), corpVo.getPk_corp(), json);
							}
						}
					}
				}
			} catch (Exception e) {
				log.error(e);
			}
			writeJson(json);
		}

		//企业主给手机号发送验证码
		public void sendVerify() {
			String phone = getRequest().getParameter("phone");
			StringBuffer url = getRequest().getRequestURL();
			Json json = new Json();
			try {
				if (StringUtil.isEmpty(phone) || !checkPhone(phone)) {
					json.setSuccess(false);
					json.setMsg("手机号有误");
				} else {
					getSession().setAttribute("verifyPhone", phone + "," + userService.sendVerify(phone,url.toString()));
					getSession().setAttribute("verifyPhoneTime", new Date().getTime());
					json.setSuccess(true);
					json.setMsg("验证码发送成功");
				}
			} catch (Exception e) {
				printErrorLog(json, log, e, null);
			}
			writeJson(json);
		}
		//企业主
		private boolean checkPhone(String phone) {
			return Pattern.matches("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$", phone);
		}
		//企业主提交验证码
		public void commitByVerify() {
			String verify = getRequest().getParameter("verify");
			Json json = new Json();
			if (verify == null || verify.length() != 6) {
				json.setSuccess(false);
				json.setMsg("验证码有误");
			} else {
				String phoneInfo = (String) getSession().getAttribute("verifyPhone");
				String[] obj = phoneInfo.split(",");
				String trueVerify = obj[1];
				if (!trueVerify.equals(verify)) {
					json.setSuccess(false);
					json.setMsg("验证码有误");
				} else {
					long trueTime = (long) getSession().getAttribute("verifyPhoneTime");
					long time = new Date().getTime() - trueTime;
					if (time / 1000 / 60 >= 10) {
						json.setSuccess(false);
						json.setMsg("验证码已经过期,请重新获取验证码");
					} else {
						json.setSuccess(true);
					}
				}
			}
			writeJson(json);
		}

		//企业主修改密码
		public void updatePswForOwner() {
			Json json = new Json();
			String psw2 = getRequest().getParameter("psw2");
			psw2 = RSAUtils.decryptStringByJs(psw2);
			String psw3 = getRequest().getParameter("psw3");
			psw3 = RSAUtils.decryptStringByJs(psw3);
			if (StringUtil.isEmpty(psw2) || StringUtil.isEmpty(psw3)) {
				json.setSuccess(false);
				json.setMsg("密码不能为空");
			}
			if (!psw2.equals(psw3)) {
				json.setSuccess(false);
				json.setMsg("两次输入的密码不一致");
			}
			String phoneInfo = (String) getSession().getAttribute("verifyPhone");
			String[] obj = phoneInfo.split(",");
			String phone = obj[0];
			UserVO user = new UserVO();
			user.setUser_code(phone);
			user = userService.loginByCode(user);
			if (user == null) {
				json.setSuccess(false);
				json.setMsg("手机号不是大账房手机用户");
			} else {
				user.setUser_password(psw2);
				try {
					userService.updateOwnerPass(user);
					json.setMsg("密码修改成功!");
					json.setSuccess(true);
				} catch (Exception e) {
					if (e instanceof BusinessException) {
						json.setMsg(e.getMessage());
					} else {
						json.setMsg("操作失败");
						log.error("操作失败", e);
					}
					json.setSuccess(false);
				}
			}
			writeJson(json);
		}
		//企业主退出登录
		public void logoutForOwner() throws ServletException, IOException {
			String pk_user = (String) getSession().getAttribute(IGlobalConstants.login_user);
			getSession().removeAttribute(IGlobalConstants.login_corp);
			getSession().removeAttribute(IGlobalConstants.login_date);
			getSession().removeAttribute(IGlobalConstants.login_user);
			getSession().removeAttribute(IGlobalConstants.POWER_MAP);
			DzfSessionContext.getInstance().DelUserSessionByPkUser(pk_user, true);
			deleteOwnerCookie(getRequest(), getResponse());
			if (pk_user != null) {
				LoginLogVo loginLogVo = getLoginVo(IGlobalConstants.DZF_OWNER);
				loginLogVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
				loginLogVo.setLogouttype(1);
				loginLogVo.setPk_user(pk_user);
				getUserService().logoutLog(loginLogVo);
			}
			getResponse().sendRedirect(getRequest().getContextPath() + "/login.jsp");
			return;
		}

		//企业主退出后清cookie
		public void deleteOwnerCookie(ServletRequest request, ServletResponse response) {
			String contentPath = ((HttpServletRequest) request).getContextPath() + "/";
			Cookie[] cookies = ((HttpServletRequest) request).getCookies();
			for (Cookie cookie : cookies) {
				if ("ownersso".equals(cookie.getName())) {
					cookie.getValue();
					cookie.setMaxAge(0);
					cookie.setPath("/");
					((HttpServletResponse) response).addCookie(cookie);
				}
			}
		}
		
		/**
		 * 渠道管理用户登陆
		 */
		public void channelLogin() {
	 		getRequest().getSession().removeAttribute(IGlobalConstants.logout_msg);
			Json json = new Json();
			json.setSuccess(false);
			LoginLogVo loginLogVo = getLoginVo("dzf_channel");
			loginLogVo.setPk_corp(IDefaultValue.DefaultGroup);
			String password = null;//data.getUser_password();
			try{
				password = data.getUser_password();
				password=RSAUtils.decryptStringByJs(password);
				data.setUser_code(RSAUtils.decryptStringByJs(data.getUser_code()));
			}catch(Exception e){
				getSession().removeAttribute("rand");
				log.error(e);
				json.setSuccess(false);
				json.setStatus(-200);
				json.setMsg("用户名、密码或验证码错误!");
				writeJson(json);
				return;
			}
			try {
				String date =  getRequest().getParameter("date");
				String clientid =  getRequest().getParameter("clientid");
				if(!checkUserInfo()) return;
//				UserVO userVo = userService.login(data);
				data.setUser_code(data.getUser_code().trim());
				UserVO userVo = userService.loginByCode(data);
				UserLoginVo userLoginVo = LoginCache.getUserVo(data.getUser_code());
				loginLogVo.setPk_user(data.getUser_code());
			//	UserVO userVo = userService.queryById("10|eA31000000000000Y");
//				password=RSAUtils.decryptStringByJs(password);
				
				if(userVo == null){
					getSession().removeAttribute("rand");
					json.setMsg("用户名、密码或验证码错误!");
					loginLogVo.setMemo("密码错误");
				}else if(!userVo.getUser_password().equals(new Encode().encode(password))){
					getSession().removeAttribute("rand");
					loginLogVo.setPk_user(userVo.getPrimaryKey());
					LoginCache.loginFail(userVo.getUser_code());
					userLoginVo = LoginCache.getUserVo(data.getUser_code());
					json.setStatus(userLoginVo.getNumber());
					if(userLoginVo.getNumber()>=IGlobalConstants.lock_fail_login){
						json.setMsg("提示：您的账号已被锁定! "+(IGlobalConstants.lock_login_min-(new Date().getTime() - userLoginVo.getLogin_date().getTime())/(60*1000))+"分钟之后解锁  !");
						loginLogVo.setMemo("被锁定");
						userService.loginLog(loginLogVo);
						writeJson(json);
						return;
					}
//					json.setMsg("用户名或密码错误!失败" + (IGlobalConstants.lock_fail_login - userLoginVo.getNumber()) + "次后将被锁定！");
					json.setMsg("用户名、密码或验证码错误!");
					loginLogVo.setMemo("密码错误");
				}else{
					loginLogVo.setPk_user(userVo.getPrimaryKey());
					LoginCache.ClearUserVo(userVo.getUser_code());
					json.setStatus(userLoginVo.getNumber());
					if(userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()){
						getSession().removeAttribute("rand");
						json.setMsg("当前用户被锁定，请联系管理员!");
						loginLogVo.setMemo("锁定");
					}else{
//						CorpVO corpVo =CorpCache.getInstance().get(userVo.getPrimaryKey(), IDefaultValue.DefaultGroup);// userService.querypowercorpById(id);
						/*if(corpVo == null){
							json.setMsg("公司不存在!");
						}*/if(! IDefaultValue.DefaultGroup.equals(userVo.getPk_corp())){
							getSession().removeAttribute("rand");
							json.setMsg("无权操作!");
							loginLogVo.setMemo("无权操作");
						}else if(userVo.getAble_time() == null || userVo.getAble_time().compareTo(new DZFDate()) > 0){
							getSession().removeAttribute("rand");
							json.setMsg("用户还未生效!");
							loginLogVo.setMemo("用户未生效");
						}else{
							loginLogVo.setMemo("登陆成功");						
							if(isOtherClientOnline(userVo.getPrimaryKey(), IGlobalConstants.SYS_DZF,clientid)){
								String force = getRequest().getParameter("f");
						
								if(force != null && force.equals("1")){
									try{
										LoginLogVo loginOldVo = getLoginVo("dzf_channel");
										loginOldVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
										loginOldVo.setLogouttype(2);
										loginOldVo.setPk_user(loginLogVo.getPk_user());
//										loginOldVo.setLoginsession(DzfSessionContext.getInstance().getSessionByPkUser(userVo.getPrimaryKey()).getId());
										getUserService().logoutLog(loginOldVo);
									}catch(Exception e){
										log.error(e);
									}
									loginLogVo.setMemo("强制登陆");
//									DzfSessionContext.getInstance().DelUserSessionByPkUser(userVo.getPrimaryKey(),false);
								}else{
									//重新把验证码放入缓存
									String uuid = (String)getSession().getAttribute(IGlobalConstants.uuid);
									SessionCache.getInstance().addVerify(uuid, getRequest().getParameter("verify"));
									
									json.setSuccess(false);
									json.setStatus(-100);
									json.setMsg("当前用户已经登陆，是否强制退出？");
									loginLogVo.setMemo("已登陆");
									userService.loginLog(loginLogVo);
									writeJson(json);
									return;
								}
							}
							List<SysFunNodeVO> lnode = sysFunnodeService.querySysnodeByUser(userVo,
									IGlobalConstants.SYS_DZF);
							// 登录缓存节点URL，改为缓存index
							Set<Integer> dzfMap = new HashSet<Integer>();
							if (lnode != null && lnode.size() > 0) {
								for (SysFunNodeVO nodeVo : lnode) {
									if (nodeVo.getFile_dir() != null && nodeVo.getFile_dir().trim().length() > 0) {
										dzfMap.add(NodeUrlConst.getInstance().getIndexMap().get(nodeVo.getPrimaryKey()));
									}
								}
							}
							getSession().setAttribute(IGlobalConstants.POWER_MAP, dzfMap);
							json.setSuccess(true);
							json.setMsg("登陆成功!");
							userVo.setUser_name(CodeUtils1.deCode(userVo.getUser_name()));
							json.setRows(userVo);
							loginLogVo.setLoginstatus(1);
							CorpCache.getInstance().remove(userVo.getPk_corp());
							UserCache.getInstance().remove(userVo.getPrimaryKey());
							getSession().setAttribute(IGlobalConstants.login_corp,userVo.getPk_corp());//etPk_corp
							getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
							getSession().setAttribute(IGlobalConstants.login_date, date);
//							DzfSessionContext.getInstance().AddUserSession(getSession());
							RSACoderUtils.createToken(getSession());
							
							getSession().setAttribute(IGlobalConstants.appid, IGlobalConstants.SYS_DZF); //集团管理端
							//写客户端cookie和redis缓存
							addRedisSessionAndCookie(userVo.getPrimaryKey(), json);					//集团管理端不选择公司
						}
					}
				}
			} catch (Exception e) {
				log.error(e);
				json.setMsg("登陆失败!");
			}
			if(json.isSuccess()){//如果登录成功
				writeLogRecord(LogRecordEnum.OPE_JITUAN_SYS.getValue(),"系统登录成功",ISysConstants.SYS_0);
			}
			writeJson(json);
			userService.loginLog(loginLogVo);
		}
}
