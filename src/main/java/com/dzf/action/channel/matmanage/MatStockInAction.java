package com.dzf.action.channel.matmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.expfield.MatStockinExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.matmanage.IMatStockInService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;


@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "matstockin")
public class MatStockInAction extends BaseAction<MaterielStockInVO> {

private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private  IMatStockInService matstockin;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MaterielStockInVO pamvo = new MaterielStockInVO();
			pamvo = (MaterielStockInVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			int total = matstockin.queryTotalRow(pamvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<MaterielStockInVO> mList = matstockin.query(pamvo,uservo);
				grid.setRows(mList);
				grid.setMsg("查询成功");
			}else{
				grid.setRows(new ArrayList<MaterielStockInVO>());
				grid.setMsg("查询数据为空");
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			grid.setMsg("查询失败");
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询所有物料
	 */
	public void queryComboBox(){
		Grid grid = new Grid();
		try {
			List<MaterielFileVO> list = matstockin.queryComboBox();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<MaterielFileVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 新增入库单
	 */
	public void save() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_69);
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			matstockin.saveStockIn(data,uservo);
			json.setMsg("保存成功");
			json.setSuccess(true);
		}catch (Exception e) {
			json.setMsg("保存失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_69);
			MaterielStockInVO pamvo = new MaterielStockInVO();
			pamvo = (MaterielStockInVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			matstockin.delete(pamvo);
			json.setMsg("删除成功");
			json.setSuccess(true);
		}catch (Exception e) {
			json.setMsg("删除失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
	}
	
	
	/**
	 * 编辑回显
	 */
	public void queryById() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MaterielStockInVO vo = new MaterielStockInVO();
			String id = getRequest().getParameter("id");
			if(!StringUtil.isEmpty(id)){
			    vo=matstockin.queryDataById(id);
			}
			json.setRows(vo);
			json.setMsg("查询成功");
			json.setSuccess(true);
		}catch (Exception e) {
			json.setMsg("查询失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
		
	}
	
	/**
	 * 导出
	 */
	public void exportAuditExcel() {
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new MaterielStockInVO());
		MaterielStockInVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,MaterielStockInVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<MaterielStockInVO> ex = new Excelexport2003<>();
		MatStockinExcelField fields = new MatStockinExcelField();
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
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "导出物料入库单", ISysConstants.SYS_3);
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
	
	
	/**
	 * 登录用户校验
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
			throw new BusinessException("登陆用户错误！");
		}else if(uservo == null){
			throw new BusinessException("请先登录！");
		}
	}

	
	
}
