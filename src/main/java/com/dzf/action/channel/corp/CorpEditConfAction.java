package com.dzf.action.channel.corp;

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
import com.dzf.model.channel.CorpNameEVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.ICorpEditConfService;

/**
 * 客户名称修改审核
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/corp")
@Action(value = "corpeditconf")
public class CorpEditConfAction extends BaseAction<CorpNameEVO> {

	private static final long serialVersionUID = -856333864149848124L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ICorpEditConfService confser;
	
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
		    QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			int total = confser.queryTotalRow(paramvo, getLoginUserInfo());
			json.setTotal((long)(total));
			if(total > 0){
				List<CorpNameEVO> clist = confser.query(paramvo, uservo);
				json.setRows(clist);
			}else{
				json.setRows(new ArrayList<CorpNameEVO>());
			}
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	
	/**
	 * 审核
	 */
	public void audit() {
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
			String type = getRequest().getParameter("type"); 
			String vreason = getRequest().getParameter("vreason"); //驳回原因
			int opertype = Integer.valueOf(type);//操作类型   2：审核通过； 3：拒绝审核；
			if(opertype == 3 && StringUtil.isEmpty(vreason)){
				throw new BusinessException("驳回原因不能为空");
			}
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new CorpNameEVO());
			CorpNameEVO[] billVOs = DzfTypeUtils.cast(arrayJson, headmaping, CorpNameEVO[].class,
					JSONConvtoJAVA.getParserConfig()); 
			int rignum = 0;
			int errnum = 0;
			StringBuffer errmsg = new StringBuffer();
			List<CorpNameEVO> rightlist = new ArrayList<CorpNameEVO>();
			for(CorpNameEVO billvo : billVOs){
				if(billvo == null){
					log.info("客户名称修改-获取操作数据为空");
				}
				billvo = confser.updateAudit(billvo, uservo, opertype, vreason);
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
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if (StringUtil.isEmpty(paramvo.getPk_bill())) {
				throw new BusinessException("主键为空");
			}
			CorpNameEVO vo = confser.queryByID(paramvo.getPk_bill());
			boolean isexists = true;
			if (vo == null) {
				isexists = false;
			}
			String fpath = "";
			if(vo != null){
				fpath = vo.getVurl();
			}
			File afile = new File(fpath);
			if (!afile.exists()) {
				isexists = false;
			}
			if (isexists) {
				String path = getRequest().getSession().getServletContext().getRealPath("/");
				String typeiconpath = path + "images" + File.separator + "typeicon" + File.separator;
				OutputStream os = getResponse().getOutputStream();
				is = new FileInputStream(afile);
				IOUtils.copy(is, os);
			}
		} catch (Exception e) {

		} finally {
			if (is != null) {
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
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if (StringUtil.isEmpty(paramvo.getPk_bill())) {
				throw new BusinessException("主键为空");
			}
			CorpNameEVO vo = confser.queryByID(paramvo.getPk_bill());
			fileis = new FileInputStream(vo.getVurl());
			getResponse().setContentType("application/octet-stream");
			String formattedName = URLEncoder.encode(vo.getFilename(), "UTF-8");
			getResponse().addHeader("Content-Disposition", "attachment;filename=" + 
					new String(vo.getFilename().getBytes("UTF-8"), "ISO8859-1") + ";filename*=UTF-8''" + formattedName);
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
