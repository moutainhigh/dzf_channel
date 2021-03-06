package com.dzf.action.channel.chn_set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.chn_set.IChnAreaService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 渠道区域划分或培训区域划分
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/chn_set")
@Action(value = "chnarea")
public class ChnAreaAction extends BaseAction<ChnAreaVO> {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChnAreaService chnarea;
	
	@Autowired
	private IPubService pubser;

	/**
	 * 保存主表数据
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
			ChnAreaVO headvo = requestDealStep(getRequest(),uservo);
			String operate="";
			if(StringUtil.isEmpty(headvo.getPk_chnarea())){
				operate="新增";
			}else{
				operate="修改";
			}
			headvo = chnarea.save(headvo);
			json.setSuccess(true);
			json.setRows(headvo);
			json.setMsg("保存成功!");
			Integer logValue=null;
			if(headvo.getType()==1){
				logValue=LogRecordEnum.OPE_CHANNEL_QDQYHF.getValue();
				operate+="渠道区域:"+headvo.getAreacode()+" "+headvo.getAreaname();
			}else if(headvo.getType()==2){
				logValue=LogRecordEnum.OPE_CHANNEL_28.getValue();
				operate+="培训区域:"+headvo.getAreacode()+" "+headvo.getAreaname();
			}else{
				logValue=LogRecordEnum.OPE_CHANNEL_35.getValue();
				operate+="运营区域:"+headvo.getAreacode()+" "+headvo.getAreaname();
			}
			writeLogRecord(logValue, operate, ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}

	private ChnAreaVO requestDealStep(HttpServletRequest request,UserVO uservo) {
		String head = request.getParameter("head");
		String body = request.getParameter("body"); // 子表
		body = body.replace("}{", "},{");
		body = "[" + body + "]";
		JSON headjs = (JSON) JSON.parse(head);
		JSONArray array = (JSONArray) JSON.parseArray(body);
		Map<String, String> headmaping = FieldMapping.getFieldMapping(new ChnAreaVO());
		Map<String, String> bodymapping = FieldMapping.getFieldMapping(new ChnAreaBVO());

		ChnAreaVO headvo = DzfTypeUtils.cast(headjs, headmaping, ChnAreaVO.class, JSONConvtoJAVA.getParserConfig());
		if(headvo.getType()==1){
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_15);
		}else if(headvo.getType()==2){
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_28);
		}else{
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_35);
		}
		String pk_corp = headvo.getPk_corp();
		if (StringUtil.isEmpty(pk_corp)) {
			pk_corp = uservo.getPk_corp();
			headvo.setPk_corp(pk_corp);
		}
		if (StringUtil.isEmpty(headvo.getPk_chnarea())) {
			headvo.setDoperatedate(new DZFDate());
			headvo.setCoperatorid(uservo.getCuserid());
			headvo.setTs(new DZFDateTime());
		}
		ChnAreaBVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, ChnAreaBVO[].class,
				JSONConvtoJAVA.getParserConfig());
		HashMap<String, String> map1 = new HashMap<>();
		HashMap<String, String> map2 = new HashMap<>();
		List<ChnAreaBVO> list = new ArrayList<ChnAreaBVO>();
		for (ChnAreaBVO chnAreaBVO : bodyvos) {
			if (!chnAreaBVO.getIsCharge().booleanValue() && StringUtil.isEmpty(chnAreaBVO.getPk_corp())) {
				throw new BusinessException("省市负责人为否时，加盟商为必输项");
			}
			if (!StringUtil.isEmpty(chnAreaBVO.getUserid())) {
				String id1 = chnAreaBVO.getVprovince() + chnAreaBVO.getUserid();
				if (map1.containsKey(id1)) {
					if (headvo.getType() == 1) {
						throw new BusinessException("负责地区+渠道经理重复,请重新输入");
					} else if (headvo.getType() == 2) {
						throw new BusinessException("负责地区+培训师重复,请重新输入");
					} else {
						throw new BusinessException("负责地区+渠道运营重复,请重新输入");
					}
				} else {
					map1.put(id1, "value");
				}
			}
			if (chnAreaBVO.getIsCharge().booleanValue()) {
				String id2 = chnAreaBVO.getVprovince() + chnAreaBVO.getIsCharge().toString();
				if (map2.containsKey(id2)) {
					throw new BusinessException("一个地区只能有一个负责人");
				} else {
					map2.put(id2, "value");
				}
			}
			chnAreaBVO.setType(headvo.getType());
			if (StringUtil.isEmpty(chnAreaBVO.getPk_corp()) || !chnAreaBVO.getPk_corp().contains(",")) {
				list.add(chnAreaBVO);
			} else {
				String[] corps = chnAreaBVO.getPk_corp().split(",");
				for (String corp : corps) {
					ChnAreaBVO clone = (ChnAreaBVO) chnAreaBVO.clone();
					clone.setPk_corp(corp);
					list.add(clone);
				}
			}
		}
		headvo.setTableVO("cn_chnarea_b", list.toArray(new ChnAreaBVO[0]));
		return headvo;
	}

	/**
	 * 删除主子表数据
	 */
	public void delete() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String type = getRequest().getParameter("type");
			if("1".equals(type)){
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_15);
			}else if("2".equals(type)){
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_28);
			}else if("3".equals(type)){
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_35);
			}
			String pk_area = getRequest().getParameter("pk_area");
			if (StringUtil.isEmpty(pk_area)) {
				throw new BusinessException("主键为空");
			}
			String acode = getRequest().getParameter("acode");
			String aname = getRequest().getParameter("aname");
			String pk_corp = uservo.getPk_corp();
			chnarea.delete(pk_area, pk_corp);
			json.setSuccess(true);
			json.setRows(data);
			json.setMsg("删除成功!");
			String operate=":";
			if(!StringUtil.isEmpty(acode)){
				operate+=acode+" ";
			}
			if(!StringUtil.isEmpty(aname)){
				operate+=aname;
			}
			if("1".equals(type)){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_QDQYHF.getValue(), "删除渠道区域"+operate, ISysConstants.SYS_3);
			}else if("2".equals(type)){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_28.getValue(), "删除培训区域"+operate, ISysConstants.SYS_3);
			}else if("3".equals(type)){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_35.getValue(), "删除运营区域"+operate, ISysConstants.SYS_3);
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
	}

	/**
	 * 查询主表数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			ChnAreaVO qvo = new ChnAreaVO();
			qvo = (ChnAreaVO) DzfTypeUtils.cast(getRequest(), qvo);
			String pk_corp = getLogincorppk();
			if (StringUtil.isEmpty(qvo.getPk_corp())) {
				qvo.setPk_corp(pk_corp);
			}
			ChnAreaVO[] vos = chnarea.query(qvo);
			if (vos == null || vos.length == 0) {
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setMsg("查询数据为空!");
			} else {
				grid.setRows(Arrays.asList(vos));
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 通过主表主键查询主子表数据
	 */
	public void queryByPrimaryKey() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String pk_area = getRequest().getParameter("pk_area");
			if (StringUtil.isEmpty(pk_area)) {
				throw new BusinessException("主键为空");
			}
			ChnAreaVO vo = chnarea.queryByPrimaryKey(pk_area);
			if (vo != null) {
				grid.setRows(Arrays.asList(vo));
			}
			grid.setSuccess(true);
			grid.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询加盟商总经理
	 */
	public void queryManager() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String pk_corp = uservo.getPk_corp();
			String username = chnarea.queryManager(pk_corp);
			json.setRows(username);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			json.setSuccess(false);
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}

	/**
	 * 查询省市，过滤掉那些已经使用的
	 */
	public void queryComboxArea() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String pk_area = getRequest().getParameter("pk_area");
			String type = getRequest().getParameter("type");
			ArrayList list = chnarea.queryComboxArea(pk_area, type);
			if (list == null || list.size() == 0) {
				grid.setRows(null);
				grid.setMsg("查询数据为空!");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询大区（下拉选项）
	 */
	public void queryArea() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO qvo = new QryParamVO();
			qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setCuserid(uservo.getCuserid());
			List<ComboBoxVO> vos = chnarea.queryArea(qvo);
			if (vos == null || vos.size() == 0) {
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空!");
			} else {
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询省（市）（下拉选项）
	 */
	public void queryProvince() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO qvo = new QryParamVO();
			qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setCuserid(uservo.getCuserid());
			List<ComboBoxVO> vos = chnarea.queryProvince(qvo);
			if (vos == null || vos.size() == 0) {
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空!");
			} else {
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询省级别 人（下拉选项）
	 * 
	 */
	public void queryTrainer() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO qvo = new QryParamVO();
			qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setCuserid(uservo.getCuserid());
			List<ComboBoxVO> vos = chnarea.queryTrainer(qvo);
			if (vos == null || vos.size() == 0) {
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空!");
			} else {
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

}