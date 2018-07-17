package com.dzf.action.channel;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.PackageDefVO;
import com.dzf.model.packagedef.PackageQryVO;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.IPackageDefService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 服务套餐定义
 *
 */
@ParentPackage("basePackage")
@Namespace("/channel")
@Action(value = "packageDef")
public class PackageDefAction extends BaseAction<PackageDefVO> {

	private static final long serialVersionUID = 2887542271004704969L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IPackageDefService packageDefImpl;
	
	@Autowired
	private IPubService pubser;

	/**
	 * 查询方法
	 */
	public void query() {
		Json grid = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			PackageQryVO paramvo = (PackageQryVO) DzfTypeUtils.cast(getRequest(), new PackageQryVO());
			PackageDefVO[] vos = packageDefImpl.query(paramvo);
			grid.setRows(vos);
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 */
	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_6);

			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new PackageDefVO());
			JSONObject jsonObj = (JSONObject) JSON.parse(getRequest().getParameter("submitData"));

			PackageDefVO[] insertData = DzfTypeUtils.cast(jsonObj.get("newRows"), bodymapping, PackageDefVO[].class,
					JSONConvtoJAVA.getParserConfig());
			PackageDefVO[] delData = DzfTypeUtils.cast(jsonObj.get("deleteRows"), bodymapping, PackageDefVO[].class,
					JSONConvtoJAVA.getParserConfig());
			PackageDefVO[] updateData = DzfTypeUtils.cast(jsonObj.get("updateRows"), bodymapping, PackageDefVO[].class,
					JSONConvtoJAVA.getParserConfig());

			setDefaultValue(insertData, updateData);
			packageDefImpl.save(getLogincorppk(), insertData, delData, updateData);
			// json.setRows(data);
			json.setSuccess(true);
			json.setMsg("保存成功");
			PackageDefVO logVO=null;
			StringBuffer buf=new StringBuffer();
			if(insertData!=null && insertData.length>0){
				logVO=insertData[0];
				buf.append("新增服务套餐：");
				buf.append(logVO.getVtaxpayertype()).append("、月服务费");
				buf.append(logVO.getNmonthmny().setScale(2, DZFDouble.ROUND_HALF_UP)).append("、收费周期");
				buf.append(logVO.getIcashcycle()).append("、合同周期").append(logVO.getIcontcycle());
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), buf.toString(), ISysConstants.SYS_3);
			}else if(updateData!=null && updateData.length>0){
				logVO=updateData[0];
				buf.append("修改服务套餐：");
				buf.append(logVO.getVtaxpayertype()).append("、月服务费");
				buf.append(logVO.getNmonthmny().setScale(2, DZFDouble.ROUND_HALF_UP)).append("、收费周期");
				buf.append(logVO.getIcashcycle()).append("、合同周期").append(logVO.getIcontcycle());
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), buf.toString(), ISysConstants.SYS_3);
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败！");
		}
		writeJson(json);
	}

	private void setDefaultValue(PackageDefVO[] insertData, PackageDefVO[] updateData) {
		if (insertData != null && insertData.length > 0) {
			for (PackageDefVO data : insertData) {
				data.setDr(0);
				data.setCoperatorid(getLoginUserid());
				data.setDoperatedate(new DZFDate());
				data.setVstatus(1);
				data.setCoperatorname(getLoginUserInfo().getUser_name());
			}
		}
	}

	public void delete() {
		Json json = new Json();
		String pStr = getRequest().getParameter("deldata");
		if (StringUtil.isEmpty(pStr)) {
			return;
		}
		JSONArray jsonArray = (JSONArray) JSON.parseArray(pStr);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new PackageDefVO());
		PackageDefVO[] defvos = DzfTypeUtils.cast(jsonArray, mapping, PackageDefVO[].class,
				JSONConvtoJAVA.getParserConfig());
		if (defvos != null && defvos.length > 0) {
			try {
				UserVO uservo = getLoginUserInfo();
				if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
					throw new BusinessException("登陆用户错误");
				} else if (uservo == null) {
					throw new BusinessException("登陆用户错误");
				}
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_6);
				packageDefImpl.delete(defvos);
				json.setSuccess(true);
				json.setRows(defvos);
				json.setMsg("删除成功");
				if(defvos!=null && defvos.length==1){
					PackageDefVO logVO=defvos[0];
					StringBuffer buf=new StringBuffer();
					buf.append("删除服务套餐：");
					buf.append(logVO.getVtaxpayertype()).append("、月服务费");
					buf.append(logVO.getNmonthmny().setScale(2, DZFDouble.ROUND_HALF_UP)).append("、收费周期");
					buf.append(logVO.getIcashcycle()).append("、合同周期").append(logVO.getIcontcycle());
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), buf.toString(), ISysConstants.SYS_3);
				}else if(defvos!=null && defvos.length>1){
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), "删除服务套餐"+defvos.length+"个", ISysConstants.SYS_3);
				}
			} catch (Exception e) {
				printErrorLog(json, log, e, "删除失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("删除失败");
		}
		writeJson(json);
	}

	public void updatePublish() {
		Json json = new Json();
		String pStr = getRequest().getParameter("datas");
		if (StringUtil.isEmpty(pStr)) {
			return;
		}
		JSONArray jsonArray = (JSONArray) JSON.parseArray(pStr);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new PackageDefVO());
		PackageDefVO[] defvos = DzfTypeUtils.cast(jsonArray, mapping, PackageDefVO[].class,
				JSONConvtoJAVA.getParserConfig());
		if (defvos != null && defvos.length > 0) {
			try {
				UserVO uservo = getLoginUserInfo();
				if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
					throw new BusinessException("登陆用户错误");
				} else if (uservo == null) {
					throw new BusinessException("登陆用户错误");
				}
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_6);
				packageDefImpl.updatePublish(defvos);
				json.setSuccess(true);
				json.setRows(defvos);
				json.setMsg("发布成功");
				if(defvos!=null && defvos.length==1){
					PackageDefVO logVO=defvos[0];
					StringBuffer buf=new StringBuffer();
					buf.append("发布服务套餐：");
					buf.append(logVO.getVtaxpayertype()).append("、月服务费");
					buf.append(logVO.getNmonthmny().setScale(2, DZFDouble.ROUND_HALF_UP)).append("、收费周期");
					buf.append(logVO.getIcashcycle()).append("、合同周期").append(logVO.getIcontcycle());
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), buf.toString(), ISysConstants.SYS_3);
				}else if(defvos!=null && defvos.length>1){
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), "发布服务套餐"+defvos.length+"个", ISysConstants.SYS_3);
				}
			} catch (Exception e) {
				printErrorLog(json, log, e, "发布失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("发布失败");
		}
		writeJson(json);
	}

	public void updateOff() {
		Json json = new Json();
		String pStr = getRequest().getParameter("datas");
		if (StringUtil.isEmpty(pStr)) {
			return;
		}
		JSONArray jsonArray = (JSONArray) JSON.parseArray(pStr);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new PackageDefVO());
		PackageDefVO[] defvos = DzfTypeUtils.cast(jsonArray, mapping, PackageDefVO[].class,
				JSONConvtoJAVA.getParserConfig());
		if (defvos != null && defvos.length > 0) {
			try {
				UserVO uservo = getLoginUserInfo();
				if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
					throw new BusinessException("登陆用户错误");
				} else if (uservo == null) {
					throw new BusinessException("登陆用户错误");
				}
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_6);
				packageDefImpl.updateOff(defvos);
				json.setSuccess(true);
				json.setRows(defvos);
				json.setMsg("下架成功");
				if(defvos!=null && defvos.length==1){
					PackageDefVO logVO=defvos[0];
					StringBuffer buf=new StringBuffer();
					buf.append("下架服务套餐：");
					buf.append(logVO.getVtaxpayertype()).append("、月服务费");
					buf.append(logVO.getNmonthmny().setScale(2, DZFDouble.ROUND_HALF_UP)).append("、收费周期");
					buf.append(logVO.getIcashcycle()).append("、合同周期").append(logVO.getIcontcycle());
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), buf.toString(), ISysConstants.SYS_3);
				}else if(defvos!=null && defvos.length>1){
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_6.getValue(), "下架服务套餐"+defvos.length+"个", ISysConstants.SYS_3);
				}
			} catch (Exception e) {
				printErrorLog(json, log, e, "下架失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("下架失败");
		}
		writeJson(json);
	}
}
