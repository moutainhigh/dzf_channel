package com.dzf.action.channel.matmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.dzf.action.channel.expfield.MatFileExcelField;
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
import com.dzf.service.channel.matmanage.IMatFileService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;


/**
 * 物料档案
 */
@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "matfile")
public class MatFileAction extends BaseAction<MaterielFileVO> {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IMatFileService matFile;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 校验物料名称唯一性
	 */
	public void queryMatname(){
		Json json = new Json();
		try{
			String name = getRequest().getParameter("wlname");
			Boolean b=matFile.queryMatName(name);
			json.setSuccess(b);
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json.isSuccess());
		
	}
	
	/**
	 * 查询数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MaterielFileVO pamvo = new MaterielFileVO();
			pamvo = (MaterielFileVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());
			int total = matFile.queryTotalRow(pamvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<MaterielFileVO> mList = matFile.query(pamvo,uservo);
				grid.setRows(mList);
				grid.setMsg("查询成功");
			}else{
				grid.setRows(new ArrayList<MaterielFileVO>());
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
	 * 封存/启用
	 */
	public void ToSseal() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			String ids = getRequest().getParameter("ids");
			String type = getRequest().getParameter("type");
			if (StringUtil.isEmpty(ids)) {
				throw new BusinessException("请选择物料");
			}
			StringBuffer errmsg = new StringBuffer();
			int errNum = 0;
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_67);
			List<MaterielFileVO> vos=matFile.querySsealById(ids);
			for (MaterielFileVO mvo : vos) {
				try{
					matFile.updateStatus(mvo,Integer.parseInt(type));
				}catch (Exception e) {
					errNum++;
					errmsg.append(e.getMessage()).append("<br>");
				}
			}
			if(errNum==0){
				json.setMsg("操作成功!");
				json.setSuccess(true);
			}else{
				json.setMsg("操作成功" +(vos.size()-errNum) + "条，失败" + errNum + "条，失败原因：" + errmsg.toString());
			    json.setSuccess(false);
			}
		} catch (Exception e) {
			json.setMsg("操作失败");
			printErrorLog(json, log, e, "操作失败");
	}
		writeJson(json);
}
	
	/**
	 * 新增物料
	 */
	public void save() {
		Json json = new Json();
		String vname = "";
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_67);
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			if(!StringUtil.isEmpty(data.getVname())){
				if(data.getVname().contains(",")){
					vname = data.getVname().replace(",", "").trim();
					data.setVname(vname);
				}
			}
			matFile.saveMatFile(data,uservo);
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
	 * 编辑回显
	 */
	public void queryById() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MaterielFileVO vo = new MaterielFileVO();
			String id = getRequest().getParameter("id");
			if(!StringUtil.isEmpty(id)){
			    vo=matFile.queryDataById(id);
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
	 * 查询是否已有入库单
	 */
	public void queryIsRk() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			String ids = getRequest().getParameter("ids");
			if(StringUtil.isEmpty(ids)){
				throw new BusinessException("请选择要删除的物料！");
			}
			StringBuffer errmsg = new StringBuffer();
			List<MaterielStockInVO> bvosList=matFile.queryIsRk(ids);
			if(bvosList==null || bvosList.size()==0){
				 json.setSuccess(true);
			}else {
				 for (MaterielStockInVO bvo : bvosList) {
				    	errmsg.append("物料编号："+bvo.getVcode()+"已有入库单，请删除入库单后重试<br/>");
						json.setMsg(errmsg.toString());
					}
			}
		}catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 删除物料
	 */
	public void delete() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MaterielFileVO vo = new MaterielFileVO();
			
			String wl = getRequest().getParameter("wl");
			wl = wl.replace("}{", "},{");
			wl = "[" + wl + "]";
			JSONArray array = (JSONArray) JSON.parseArray(wl);
			Map<String, String> map = FieldMapping.getFieldMapping(new MaterielFileVO());
			MaterielFileVO[] mvos = DzfTypeUtils.cast(array, map, MaterielFileVO[].class, JSONConvtoJAVA.getParserConfig());
			
			List<MaterielFileVO> mvoList = Arrays.asList(mvos);
			for (MaterielFileVO mvo : mvoList) {
				matFile.deleteWl(mvo);
			}
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
	 * 选择物料
	 */
	public void queryMatFile() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MaterielFileVO pamvo = new MaterielFileVO();
			pamvo = (MaterielFileVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			List<MaterielFileVO> list = matFile.queryMatFile(pamvo,uservo);
			if(list!=null && list.size()>0){
				grid.setRows(list);
				grid.setTotal((long) (list.size()));
				grid.setMsg("查询成功！");
				
			}else{
				grid.setMsg("查询数据为空！");
				grid.setRows(new ArrayList<MaterielFileVO>());
				grid.setTotal((long)0);
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
		
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
		Map<String, String> mapping = FieldMapping.getFieldMapping(new MaterielFileVO());
		MaterielFileVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,MaterielFileVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<MaterielFileVO> ex = new Excelexport2003<>();
		MatFileExcelField fields = new MatFileExcelField();
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
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "导出物料档案", ISysConstants.SYS_3);
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
