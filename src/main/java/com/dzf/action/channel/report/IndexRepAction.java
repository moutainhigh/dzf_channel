package com.dzf.action.channel.report;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.MonthBusimngVO;
import com.dzf.model.channel.report.WeekBusimngVO;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.report.IIndexRep;

/**
 * 首页
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "indexrep")
public class IndexRepAction extends BaseAction<QryParamVO> {
	
	private static final long serialVersionUID = 330196757687385517L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IIndexRep indexSer;

	/**
	 * 周业务情况查询
	 */
	public void queryBusiByWeek(){
		Json json = new Json();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO)DzfTypeUtils.cast(getRequest(), paramvo);
			if(StringUtil.isEmpty(paramvo.getPk_corp())){
				paramvo.setPk_corp(getLogincorppk());
			}
			paramvo.setCuserid(getLoginUserid());
			WeekBusimngVO busivo = indexSer.queryBusiByWeek(paramvo);
			json.setRows(busivo);
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	/**
	 * 月业务情况查询
	 */
	public void queryBusiByMonth(){
		Json json = new Json();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO)DzfTypeUtils.cast(getRequest(), paramvo);
			if(StringUtil.isEmpty(paramvo.getPk_corp())){
				paramvo.setPk_corp(getLogincorppk());
			}
			paramvo.setCuserid(getLoginUserid());
			MonthBusimngVO busivo = indexSer.queryBusiByMonth(paramvo);
			json.setRows(busivo);
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
}
