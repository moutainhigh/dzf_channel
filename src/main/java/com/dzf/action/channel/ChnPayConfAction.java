package com.dzf.action.channel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.expfield.ChnPayAuditExcelField;
import com.dzf.action.channel.expfield.ChnPayBillExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.IChnPayConfService;

/**
 * 付款单确认
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/chnpay")
@Action(value = "chnpayconf")
public class ChnPayConfAction extends BaseAction<ChnPayBillVO>{

	private static final long serialVersionUID = 2887542271004704969L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChnPayConfService payconfSer;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			paramvo.setCuserid(getLoginUserid());
			int total = payconfSer.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<ChnPayBillVO> clist = payconfSer.query(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<ChnPayBillVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 收款确认、确认驳回、取消确认
	 */
	public void operate() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String data = getRequest().getParameter("data"); // 操作数据
			if(StringUtil.isEmpty(data)){
				throw new BusinessException("数据不能为空");
			}
			String type = getRequest().getParameter("type"); // 操作类型
			String vreason = getRequest().getParameter("vreason"); // 操作类型
			Integer opertype = Integer.valueOf(type);
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new ChnPayBillVO());
			ChnPayBillVO[] billVOs = DzfTypeUtils.cast(arrayJson, headmaping, ChnPayBillVO[].class,
					JSONConvtoJAVA.getParserConfig()); 
			int rignum = 0;
			int errnum = 0;
			StringBuffer errmsg = new StringBuffer();
			List<ChnPayBillVO> rightlist = new ArrayList<ChnPayBillVO>();
			for(ChnPayBillVO billvo : billVOs){
				if(billvo == null){
					log.info("付款单-获取操作数据为空");
				}
				billvo = payconfSer.updateOperate(billvo, opertype, getLoginUserid(), vreason);
				if(!StringUtil.isEmpty(billvo.getVerrmsg())){
					errnum ++;
					errmsg.append(billvo.getVerrmsg()).append("<br>");
				}else{
					rignum ++;
					rightlist.add(billvo);
				}
			}
			json.setSuccess(true);
			if(rignum > 0 && rignum == billVOs.length){
				json.setRows(rightlist);
				json.setMsg("成功"+rignum+"条");
			}else if(errnum > 0){
				json.setMsg("成功"+rignum+"条，失败"+errnum+"条，失败原因："	+ errmsg.toString());
				json.setStatus(-1);
				if(rignum > 0){
					json.setRows(rightlist);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 获取附件显示图片
	 */
	public void getAttachImage() {
		InputStream is = null;
		OutputStream os = null;
		try {
			ChnPayBillVO vo = new ChnPayBillVO();
			vo = (ChnPayBillVO) DzfTypeUtils.cast(getRequest(), vo);
			if (StringUtil.isEmpty(vo.getPk_paybill())) {
				throw new BusinessException("主键为空");
			}
			vo = payconfSer.queryByID(vo.getPk_paybill());
			boolean isexists = true;
			if (vo == null) {
				isexists = false;
			}
			String fpath = "";
			if (vo != null) {
				fpath = vo.getVfilepath();
			}
			File afile = new File(fpath);
			if (!afile.exists()) {
				isexists = false;
			}
			if (isexists) {
				// String path =
				// getRequest().getSession().getServletContext().getRealPath("/");
				// String typeiconpath = path + "images" + File.separator +
				// "typeicon" + File.separator;
				os = getResponse().getOutputStream();
				is = new FileInputStream(afile);
				IOUtils.copy(is, os);
			}
		} catch (Exception e) {

		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}
	
	/**
	 * 文件下载
	 */
	public void downFile() {
		FileInputStream fileis = null;
		ServletOutputStream out = null;
		Json json = new Json();
		try {
			String billid = getRequest().getParameter("billid");
			ChnPayBillVO vo = payconfSer.queryByID(billid);
			fileis = new FileInputStream(vo.getVfilepath());
			getResponse().setContentType("application/octet-stream");
			String formattedName = URLEncoder.encode(vo.getDocName(), "UTF-8");
			getResponse().addHeader("Content-Disposition", "attachment;filename=" + 
					new String(vo.getDocName().getBytes("UTF-8"), "ISO8859-1") + ";filename*=UTF-8''" + formattedName);
			out = getResponse().getOutputStream();
			byte[] buf = new byte[4 * 1024]; // 4K buffer
			int bytesRead;
			while ((bytesRead = fileis.read(buf)) != -1) {
				out.write(buf, 0, bytesRead);
			}
			out.flush();
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				json.setMsg(e.getMessage());
			} else {
				json.setMsg("文件下载失败");
			}
			log.error("文件下载失败", e);
			json.setSuccess(false);
		} finally {
			if (fileis != null) {
				try {
					fileis.close();
				} catch (IOException e) {
					log.error("关闭流失败", e);
				}
			}
			if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("关闭流失败", e);
                }
            }
		}
	}
	
	/**
	 * 付款单确认导出
	 */
	public void exportExcel(){
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new ChnPayBillVO());
		ChnPayBillVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,ChnPayBillVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<ChnPayBillVO> ex = new Excelexport2003<>();
		ChnPayBillExcelField fields = new ChnPayBillExcelField();
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
