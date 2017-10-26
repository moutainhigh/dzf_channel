package com.dzf.action.channel;

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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpDocVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
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
	 * 收款确认、取消确认
	 */
	public void operate() {
		Json json = new Json();
		try {
			String data = getRequest().getParameter("data"); // 操作数据
			if(StringUtil.isEmpty(data)){
				throw new BusinessException("数据不能为空");
			}
			String type = getRequest().getParameter("type"); // 操作类型
			Integer opertype = Integer.valueOf(type);
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new ChnPayBillVO());
			ChnPayBillVO[] billVOs = DzfTypeUtils.cast(arrayJson, headmaping, ChnPayBillVO[].class,
					JSONConvtoJAVA.getParserConfig()); 
			ChnPayBillVO[] retVOs = payconfSer.operate(billVOs, opertype, getLoginUserid());
			if(retVOs != null && retVOs.length > 0){
				int rignum = 0;
				int errnum = 0;
				List<ChnPayBillVO> rightlist = new ArrayList<ChnPayBillVO>();
				for(ChnPayBillVO retvo : retVOs){
					if(StringUtil.isEmpty(retvo.getVerrmsg() )){
						rignum++;
						rightlist.add(retvo);
					}else{
						errnum++;
					}
				}
				json.setSuccess(true);
				if(rignum > 0 && rignum == retVOs.length){
					json.setRows(retVOs);
					json.setMsg("成功"+rignum+"条");
				}else if(errnum > 0){
					json.setMsg("成功"+rignum+"条，失败"+errnum+"条，");
					json.setStatus(-1);
					if(rignum > 0){
						json.setRows(rightlist.toArray(new ChnPayBillVO[0]));
					}
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
//	/**
//	 * 查询单个数据
//	 */
//	public void queryByID(){
//		Json json = new Json();
//		try {
//			ChnPayBillVO chn = new ChnPayBillVO();
//			String pk_corp = getLogincorppk();
//			chn = (ChnPayBillVO) DzfTypeUtils.cast(getRequest(), chn);
//			if(StringUtil.isEmpty(chn.getPk_paybill())){
//				throw new BusinessException("主键为空");
//			}
//			if (StringUtil.isEmpty(chn.getPk_corp())){
//				chn.setPk_corp(pk_corp);
//			}
//			ChnPayBillVO recust = payconfSer.queryByID(chn.getPk_paybill());
//			json.setSuccess(true);
//			json.setRows(recust);
//			json.setMsg("查询成功!");
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "查询失败");
//		}
//		writeJson(json);
//	}
	
	/**
	 * 获取附件显示图片
	 */
	public void getAttachImage(){
		InputStream is = null;
		try {
			ChnPayBillVO vo = new ChnPayBillVO();
			vo = (ChnPayBillVO) DzfTypeUtils.cast(getRequest(), vo);
			if(StringUtil.isEmpty(vo.getPk_paybill())){
				throw new BusinessException("主键为空");
			}
			vo = payconfSer.queryByID(vo.getPk_paybill());
			boolean isexists = true;
			if(vo==null){
				isexists = false;
			}
			 String fpath = vo.getVfilepath();
			 File afile = new File(fpath);
			 if(!afile.exists()){
				 isexists = false;
			 }
			 if(isexists){
				 String path = getRequest().getSession().getServletContext().getRealPath("/");
				 String typeiconpath = path + "images" + File.separator + "typeicon" + File.separator;
				 OutputStream os = getResponse().getOutputStream();
				 is = new FileInputStream(afile);
				 IOUtils.copy(is, os);
			 }
		} catch (Exception e) {
			
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * 文件下载
	 */
	public void downFile() {
		FileInputStream fileis = null;
		Json json = new Json();
		try {
			String billid = getRequest().getParameter("billid");
			ChnPayBillVO vo = payconfSer.queryByID(billid);
			fileis = new FileInputStream(vo.getVfilepath());
			getResponse().setContentType("application/octet-stream");
			//getResponse().addHeader("Content-Disposition", "attachment;filename=" + new String(docvo.getDocName().getBytes("UTF-8"), "ISO8859-1"));
			String formattedName = URLEncoder.encode(vo.getDocName(), "UTF-8");
			getResponse().addHeader("Content-Disposition", "attachment;filename=" + 
					new String(vo.getDocName().getBytes("UTF-8"), "ISO8859-1") + ";filename*=UTF-8''" + formattedName);
			ServletOutputStream out = getResponse().getOutputStream();
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
		}
	}
}
