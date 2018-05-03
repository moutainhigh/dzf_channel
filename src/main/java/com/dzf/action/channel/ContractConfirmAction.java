package com.dzf.action.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.demp.contract.ContractDocVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.IContractConfirm;

/**
 * 合同确认
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/contract")
@Action(value = "contractconf")
public class ContractConfirmAction extends BaseAction<ContractConfrimVO> {

	private static final long serialVersionUID = 8503727157432036048L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IContractConfirm contractconfser;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
		    QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			List<ContractConfrimVO> clist = contractconfser.query(paramvo);
			int page = paramvo == null ? 1 : paramvo.getPage();
			int rows = paramvo == null ? 10000 : paramvo.getRows();
			int len = clist == null ? 0 : clist.size();
			if(len > 0){
				grid.setTotal((long) (len));
				ContractConfrimVO[] conVOs = clist.toArray(new ContractConfrimVO[0]);
				conVOs = (ContractConfrimVO[]) QueryUtil.getPagedVOs(conVOs, page, rows);
				grid.setRows(Arrays.asList(conVOs));
				grid.setSuccess(true);
				grid.setMsg("操作成功");
			}else{
				grid.setTotal(Long.valueOf(0));
				grid.setRows(new ArrayList<ContractConfrimVO>());
				grid.setMsg("操作结果为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询待扣款数据
	 */
	public void queryDeductData(){
		Json json = new Json();
		try {
			ContractConfrimVO qryvo = (ContractConfrimVO) DzfTypeUtils.cast(getRequest(), new ContractConfrimVO());
			ContractConfrimVO retvo = contractconfser.queryDebitData(qryvo);
			json.setRows(retvo);
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
	public void updateDeductData(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String type = getRequest().getParameter("opertype");
			Integer opertype = Integer.parseInt(type);
			String head = getRequest().getParameter("head");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new ContractConfrimVO());
			ContractConfrimVO paramvo = new ContractConfrimVO();
			paramvo = DzfTypeUtils.cast(headjs, headmaping, ContractConfrimVO.class, JSONConvtoJAVA.getParserConfig());
			if(paramvo == null){
				log.info("单个审核-获取审核数据为空");
			}
			ContractConfrimVO retvo = contractconfser.updateDeductData(paramvo, opertype, getLoginUserid());
			json.setRows(retvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 批量审核
	 */
	public void bathconfrim(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String contract = getRequest().getParameter("contract"); // 审核数据
			if(StringUtil.isEmpty(contract)){
				throw new BusinessException("数据不能为空");
			}
			String type = getRequest().getParameter("opertype");
			int opertype = Integer.parseInt(type);//2：审核通过；3： 
			
			String head = getRequest().getParameter("head");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new ContractConfrimVO());
			ContractConfrimVO paramvo = DzfTypeUtils.cast(headjs, headmaping, ContractConfrimVO.class, JSONConvtoJAVA.getParserConfig());
			
			contract = contract.replace("}{", "},{");
			contract = "[" + contract + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(contract);
			Map<String, String> contmaping = FieldMapping.getFieldMapping(new ContractConfrimVO());
			ContractConfrimVO[] confrimVOs = DzfTypeUtils.cast(arrayJson, contmaping, ContractConfrimVO[].class,
					JSONConvtoJAVA.getParserConfig());
			int rignum = 0;
			int errnum = 0;
			List<ContractConfrimVO> rightlist = new ArrayList<ContractConfrimVO>();
			Map<String, String> packmap = contractconfser.queryPackageMap();
			StringBuffer errmsg = new StringBuffer();
			for(ContractConfrimVO confvo : confrimVOs){
				if(confvo == null){
					log.info("批量审核-获取审核数据为空");
				}else{
					confvo = contractconfser.updateBathDeductData(confvo, paramvo, opertype, getLoginUserid(), packmap);
				}
				if(!StringUtil.isEmpty(confvo.getVerrmsg() )){
					errnum++;
					errmsg.append(confvo.getVerrmsg()).append("<br>");
				}else{
					rignum++;
					rightlist.add(confvo);
				}
			}
			json.setSuccess(true);
			if(rignum > 0 && rignum == confrimVOs.length){
				json.setRows(Arrays.asList(confrimVOs));
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
	 * 获取附件列表
	 */
	public void getAttaches(){
		Json json = new Json();
		json.setSuccess(false);
		try {
			ContractDocVO paramvo = new ContractDocVO();
			paramvo = (ContractDocVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if(StringUtil.isEmpty(paramvo.getPk_corp())){
				paramvo.setPk_corp(getLogincorppk());
			}
			ContractDocVO[] resvos = contractconfser.getAttatches(paramvo);
			if(resvos!=null){
				json.setRows(Arrays.asList(resvos));
			}else{
				json.setRows(0);
			}			
			json.setSuccess(true);
			json.setMsg("获取附件成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "获取附件失败");
		}
		writeJson(json);
	}
	
	/**
	 * 获取附件显示图片
	 */
	public void getAttachImage(){
		
		InputStream is = null;
		try {
			ContractDocVO paramvo = new ContractDocVO();
			paramvo = (ContractDocVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if(StringUtil.isEmpty(paramvo.getPk_corp())){
				paramvo.setPk_corp(getLogincorppk());
			}
			ContractDocVO[] resvos = contractconfser.getAttatches(paramvo);
			boolean isexists = true;
			if(resvos==null||resvos[0]==null){
				isexists = false;
			}
			 String fpath = resvos[0].getVfilepath();
			 File afile = new File(fpath);
			 if(!afile.exists()){
				 isexists = false;
			 }
			 if(isexists){
				 String path = getRequest().getSession().getServletContext().getRealPath("/");
				 String typeiconpath = path + "images" + File.separator + "typeicon" + File.separator;
				 if(fpath.toLowerCase().lastIndexOf(".pdf")>0){
				/*	 typeiconpath += "pdf.jpg";
					 afile = new File(typeiconpath);*/
				 }else if(fpath.toLowerCase().lastIndexOf(".doc")>0){
					 typeiconpath += "word.jpg";
					 afile = new File(typeiconpath);
				 }else if(fpath.toLowerCase().lastIndexOf(".xls")>0){
					 typeiconpath += "excel.jpg";
					 afile = new File(typeiconpath);
				 }else if(fpath.toLowerCase().lastIndexOf(".ppt")>0){
					 typeiconpath += "powerpoint.jpg";
					 afile = new File(typeiconpath);
				 }else if(fpath.toLowerCase().lastIndexOf(".zip")>0
						 ||fpath.toLowerCase().lastIndexOf(".rar")>0){
					 
					 typeiconpath += "zip.jpg";
					 afile = new File(typeiconpath);
					 
				 }else{
					 
				 }
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
	 * 变更保存
	 */
	public void saveChange() {
		Json json = new Json();
		if (data != null) {
			File[] files = ((MultiPartRequestWrapper) getRequest()).getFiles("imageFile");
			String[] filenames = ((MultiPartRequestWrapper) getRequest()).getFileNames("imageFile");
			try {
				if(files == null || files.length == 0){
					throw new BusinessException("变更附件不能为空");
				}
				ContractConfrimVO retvo = contractconfser.saveChange(data, getLoginUserid(), files, filenames);
				json.setRows(retvo);
				json.setSuccess(true);	
				json.setMsg("变更成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "变更失败");
			}
		}else{
			json.setSuccess(false);
			json.setMsg("变更失败");
		}
		writeJson(json);
	}

	/**
	 * 查询加盟商合同详情信息
	 */
	public void queryInfoById() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				ContractConfrimVO retvo = contractconfser.queryInfoById(data);
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
