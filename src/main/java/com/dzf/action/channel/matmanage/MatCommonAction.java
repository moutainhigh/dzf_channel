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
import com.dzf.action.channel.expfield.MatFileExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.matmanage.IMatCommonService;
import com.dzf.service.pub.LogRecordEnum;

@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "matcomm")
public class MatCommonAction extends BaseAction<MatOrderVO>{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IMatCommonService matcomm;


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
			List<MaterielFileVO> list = matcomm.queryMatFile(pamvo,uservo);
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
	 * 查询物料信息
	 */
	public void queryNumber() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO pamvo = new MatOrderVO();
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			List<MatOrderBVO> bvos = matcomm.queryNumber(pamvo);
			if (bvos == null || bvos.size() == 0) {// 还没有申请，查询所有启用的物料
				List<MaterielFileVO> mvos = matcomm.queryMat();
				if (mvos != null && mvos.size() > 0) {
					for (MaterielFileVO mvo : mvos) {
						mvo.setApplynum(0);
						mvo.setOutnum(0);
					}
				}

				json.setRows(mvos);
			} else {
				json.setRows(bvos);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	/**
	 * 查询所有的省份
	 */
	public void queryAllProvince() {
		Grid grid = new Grid();
		try {
			List<MatOrderVO> list = matcomm.queryAllProvince();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<MatOrderVO>());
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
	 * 根据省份查询市
	 */
	public void queryCityByProId() {
		Json json = new Json();
		try {
			String pid = getRequest().getParameter("provinceid");
			if (!StringUtil.isEmpty(pid)) {
				List<MatOrderVO> list = matcomm.queryCityByProId(Integer.parseInt(pid));
				if (list == null || list.size() == 0) {
					json.setRows(new ArrayList<MatOrderVO>());
					json.setSuccess(true);
					json.setMsg("查询数据为空");
				} else {
					json.setRows(list);
					json.setSuccess(true);
					json.setMsg("查询成功");
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);

	}
	
	/**
	 * 根据市查询区县
	 */
	public void queryAreaByCid() {
		Json json = new Json();
		try {
			String cid = getRequest().getParameter("cityid");
			if (!StringUtil.isEmpty(cid)) {
				List<MatOrderVO> list = matcomm.queryAreaByCid(Integer.parseInt(cid));
				if (list == null || list.size() == 0) {
					json.setRows(new ArrayList<MatOrderVO>());
					json.setSuccess(true);
					json.setMsg("查询数据为空");
				} else {
					json.setRows(list);
					json.setSuccess(true);
					json.setMsg("查询成功");
				}
			}

		} catch (Exception e) {
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
