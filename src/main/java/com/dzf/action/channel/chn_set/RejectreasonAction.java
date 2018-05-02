package com.dzf.action.channel.chn_set;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.RejectreasonVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.chn_set.impl.IRejectreasonService;

/**
 * 合同审批设置
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/chn_set")
@Action(value = "rejectreason")
public class RejectreasonAction extends BaseAction<RejectreasonVO> {

	private static final long serialVersionUID = 4198508246490206214L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	IRejectreasonService rejectser;

	/**
	 * 查询
	 */
	public void query() {
		Grid json = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO vo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if (StringUtil.isEmptyWithTrim(vo.getPk_corp())) {
				vo.setPk_corp(getLoginCorpInfo().getPk_corp());
			}
			int total = rejectser.queryTotalRow(vo, uservo);
			json.setTotal((long)(total));
			if(total > 0){
				List<RejectreasonVO> clist = rejectser.query(vo, uservo);
				json.setRows(clist);
			}else{
				json.setRows(new ArrayList<RejectreasonVO>());
			}
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				setDefaultValue(data);
				data = rejectser.save(data, getLogincorppk());
				json.setRows(data);
				json.setSuccess(true);	
				json.setMsg("保存成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "保存失败");
			}
		}else {
			json.setSuccess(false);
			json.setMsg("保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 保存前设置默认值
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(RejectreasonVO data) throws DZFWarpException {
		if(StringUtil.isEmpty(data.getPk_rejectreason())){
			data.setCoperatorid(getLoginUserid());
			data.setDoperatedate(new DZFDate());
			data.setPk_corp(getLogincorppk());
			data.setDr(0);
		}else{
			data.setLastmodifypsnid(getLoginUserid());
		}
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				rejectser.delete(data);
				json.setSuccess(true);
				json.setMsg("操作成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "操作失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("操作数据为空");
		}
		writeJson(json);
	}
	
	/**
	 * 通过主键查询信息
	 */
	public void queryById() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				RejectreasonVO retvo = rejectser.queryById(data);
				json.setSuccess(true);
				json.setRows(retvo);
			} catch (Exception e) {
				printErrorLog(json, log, e, "操作失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("操作数据为空");
		}
		writeJson(json);
	}
}
