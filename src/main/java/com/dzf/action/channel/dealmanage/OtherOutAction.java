package com.dzf.action.channel.dealmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.expfield.OtherOutExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.stock.StockOutBVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IOtherOutService;
import com.dzf.service.pub.IPubService;

/**
 * 出库单
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "otherout")
public class OtherOutAction extends BaseAction<StockOutVO>{

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IOtherOutService otherOut;
	
	@Autowired
	private IPubService pubService;
	
	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			int total = otherOut.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<StockOutVO> clist = otherOut.query(paramvo);
				grid.setRows(clist);
				grid.setMsg("查询成功!");
			}else{
				grid.setRows(new ArrayList<StockOutVO>());
				grid.setMsg("查询数据为空!");
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	
	public void queryByID() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String soutid = getRequest().getParameter("soutid");
			if (StringUtil.isEmpty(soutid)) {
				throw new BusinessException("请选择一个订单");
			}
			StockOutVO retvo = otherOut.queryByID(soutid);
			json.setSuccess(true);
			json.setRows(retvo);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	public void queryForLook() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String soutid = getRequest().getParameter("soutid");
			if (StringUtil.isEmpty(soutid)) {
				throw new BusinessException("请选择一个订单");
			}
			StockOutVO retvo = otherOut.queryForLook(soutid);
			json.setSuccess(true);
			json.setRows(retvo);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	public void queryGoodsAble() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			List<StockOutBVO> vos = otherOut.queryGoodsAble();
			json.setSuccess(true);
			json.setRows(vos);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	
	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_60);
			StockOutVO headvo = requestDealStep(getRequest());
			if (headvo == null) {
				throw new BusinessException("数据信息不能为空");
			}
			if(StringUtil.isEmpty(headvo.getPk_stockout())){//新增
				otherOut.saveNew(headvo);
			}else{
				otherOut.saveEdit(headvo);
			}
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 删除其他出库单
	 */
	public void delete() {
		Json json = new Json();
		json.setSuccess(false);
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_60);
			StockOutVO pamvo = new StockOutVO();
			pamvo = (StockOutVO) DzfTypeUtils.cast(getRequest(), pamvo);
			otherOut.delete(pamvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	public void updateCommit(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_60);
			StockOutVO vo = new StockOutVO();
			vo = (StockOutVO) DzfTypeUtils.cast(getRequest(), vo);
			vo.setVconfirmid(getLoginUserid());
			otherOut.updateCommit(vo);
			json.setSuccess(true);
			json.setRows(vo);
			json.setMsg("操作成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	public void updateCancel(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_60);
			StockOutVO vo = new StockOutVO();
			vo = (StockOutVO) DzfTypeUtils.cast(getRequest(), vo);
			vo.setVconfirmid(getLoginUserid());
			otherOut.updateCancel(vo);
			json.setSuccess(true);
			json.setRows(vo);
			json.setMsg("操作成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	private StockOutVO requestDealStep(HttpServletRequest request) {
		String head = request.getParameter("head");
		String body = request.getParameter("body");
		if(StringUtils.isEmpty(body)){
			throw new BusinessException("请至少添加一行表体");
		}
		body = body.replace("}{", "},{");
		body = "[" + body+ "]";
		JSON headjs = (JSON) JSON.parse(head);
		JSONArray array = (JSONArray) JSON.parseArray(body);
		Map<String, String> headmaping = FieldMapping.getFieldMapping(new StockOutVO());
		Map<String, String> bodymapping = FieldMapping.getFieldMapping(new StockOutBVO());
		
		StockOutVO headvo = DzfTypeUtils.cast(headjs, headmaping, StockOutVO.class, JSONConvtoJAVA.getParserConfig());
		headvo.setFathercorp(getLogincorppk());
		if(StringUtil.isEmpty(headvo.getPk_stockout())){
			headvo.setCoperatorid(getLoginUserInfo().getCuserid());
		}
		StockOutBVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, StockOutBVO[].class,
				JSONConvtoJAVA.getParserConfig());
		headvo.setChildren(bodyvos);
		return headvo;
	}
	
	/**
	 * 打印
	 */
	@SuppressWarnings("unchecked")
	public void print() {
		Json json = new Json();
        try {
            UserVO uservo = getLoginUserInfo();
    		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
    			throw new BusinessException("登陆用户错误");
    		}else if(uservo == null){
    			throw new BusinessException("登陆用户错误");
    		}
    		pubService.checkFunnode(uservo, IFunNode.CHANNEL_60);
        	String soutid =getRequest().getParameter("id");
    		if(StringUtils.isEmpty(soutid)){
    			throw new BusinessException("请选择一行数据");
    		}
    		StockOutVO headvo = otherOut.queryForLook(soutid);
    		OtherOutPrint<StockOutVO> print = new OtherOutPrint<StockOutVO>();
            print.printFileTrans(headvo);
        } catch (Exception e) {
            printErrorLog(json, log, e, "打印失败");
        }
	}
	
	/**
	 * 导出
	 */
	public void exportExcel(){
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new StockOutVO());
		StockOutVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,StockOutVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<StockOutVO> ex = new Excelexport2003<>();
		OtherOutExcelField fields = new OtherOutExcelField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String filename = fields.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
	        response.addHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
		} catch (Exception e) {
			log.error("导出失败",e);
		}  finally {
			if(toClient != null){
				try {
					toClient.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
			if(servletOutputStream != null){
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
		}
	}
	
}
