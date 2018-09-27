package com.dzf.action.sys.sys_power;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sys_power.ButtonVO;
import com.dzf.model.channel.sys_power.RoleButtonVO;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.sys_power.IButtonPowerService;
import com.dzf.service.pub.IPubService;

@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "buttonPower")
public class ButtonPowerAction extends BaseAction<ButtonVO> {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IButtonPowerService butPower;
	
	@Autowired
	private IPubService pubser;
	
	public void queryHead() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			List<ButtonVO> vos = butPower.queryHead();
			json.setSuccess(true);
			json.setRows(vos);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	public void query() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			List<ButtonVO> vos = butPower.query();
			json.setSuccess(true);
			json.setRows(vos);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}

	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_48);
			String body = getRequest().getParameter("body"); // 第一个子表
			body = body.replace("}{", "},{");
			body = "[" + body + "]";
			JSONArray array = (JSONArray) JSON.parseArray(body);
			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new RoleButtonVO());
			RoleButtonVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, RoleButtonVO[].class,
					JSONConvtoJAVA.getParserConfig());
			butPower.save(bodyvos);
			json.setRows(data);
			json.setSuccess(true);
			json.setMsg("保存成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败!");
		}
		writeJson(json);
	}

}