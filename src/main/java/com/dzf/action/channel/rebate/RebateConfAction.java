package com.dzf.action.channel.rebate;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.rebate.IRebateConfService;

/**
 * 返点单确认
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/rebate")
@Action(value = "rebateconf")
public class RebateConfAction extends BaseAction<RebateVO> {

	private static final long serialVersionUID = 5761681623723616215L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IRebateConfService confser;
	
	/**
	 * 确认/驳回
	 */
	public void updateConf() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				String opertype = getRequest().getParameter("opertype");
				Integer iopertype = null;
				if(opertype != null){
					iopertype = Integer.parseInt(opertype);
				}
				data.setVconfirmid(getLoginUserid());
				data.setTconfirmtime(new DZFDateTime());
				data = confser.updateConf(data, getLogincorppk(), iopertype);
				json.setRows(data);
				json.setSuccess(true);	
				json.setMsg("确认成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "确认失败");
			}
		}else {
			json.setSuccess(false);
			json.setMsg("确认失败");
		}
		writeJson(json);
	}
	
	/**
	 * 取消确认
	 */
	public void updateCanc() {
		Json json = new Json();
		try {
			String data = getRequest().getParameter("data"); // 审核数据
			if (StringUtil.isEmpty(data)) {
				throw new BusinessException("数据不能为空");
			}
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new RebateVO());
			RebateVO[] rabateVOs = DzfTypeUtils.cast(arrayJson, headmaping, RebateVO[].class,
					JSONConvtoJAVA.getParserConfig()); // 合同表体
			rabateVOs[0].setVconfirmid(getLoginUserid());
			rabateVOs[0].setTconfirmtime(new DZFDateTime());
			String opertype = getRequest().getParameter("opertype");
			Integer iopertype = null;
			if(opertype != null){
				iopertype = Integer.parseInt(opertype);
			}
			RebateVO retvo = confser.updateConf(rabateVOs[0], getLogincorppk(), iopertype);
			json.setRows(retvo);
			json.setSuccess(true);	
			json.setMsg("取消确认成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "取消确认失败");
		}
		writeJson(json);
	}
	
}
