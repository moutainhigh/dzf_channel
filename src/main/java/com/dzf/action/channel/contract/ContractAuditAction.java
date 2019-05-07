package com.dzf.action.channel.contract;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.contract.ChangeApplyVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.contract.IContractAuditService;
import com.dzf.service.pub.IPubService;

/**
 * 渠道合同审核
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/contract")
@Action(value = "contractaudit")
public class ContractAuditAction extends BaseAction<ChangeApplyVO> {

	private static final long serialVersionUID = -2630663628940589385L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IContractAuditService auditser;

	@Autowired
	private IPubService pubser;

	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			checkUser();
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_66);
			ChangeApplyVO pamvo = new ChangeApplyVO();
			pamvo = (ChangeApplyVO) DzfTypeUtils.cast(getRequest(), new ChangeApplyVO());
			UserVO uservo = getLoginUserInfo();
			int total = auditser.queryTotalNum(pamvo, uservo);
			grid.setTotal((long) (total));
			if (total > 0) {
				List<ChangeApplyVO> list = auditser.query(pamvo, uservo);
				grid.setRows(list);
			} else {
				grid.setRows(new ArrayList<ChangeApplyVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询待审核人员
	 */
	public void queryAuditer() {
		Grid grid = new Grid();
		try {
			checkUser();
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_66);
			ChangeApplyVO pamvo = new ChangeApplyVO();
			pamvo = (ChangeApplyVO) DzfTypeUtils.cast(getRequest(), new ChangeApplyVO());
			UserVO uservo = getLoginUserInfo();
			List<ComboBoxVO> list = auditser.queryAuditer(pamvo, uservo);
			grid.setRows(list);
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 通过主键查询申请信息
	 */
	public void queryById() {
		Json json = new Json();
		try {
			checkUser();
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_66);
			ChangeApplyVO pamvo = new ChangeApplyVO();
			pamvo = (ChangeApplyVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			UserVO uservo = getLoginUserInfo();
			ChangeApplyVO datavo = auditser.queryById(pamvo, uservo);
			json.setRows(datavo);
			json.setSuccess(true);
			json.setMsg("操作成功");
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
			checkUser();
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_66);
			ChangeApplyVO pamvo = new ChangeApplyVO();
			pamvo = (ChangeApplyVO) DzfTypeUtils.cast(getRequest(), pamvo);
			UserVO uservo = getLoginUserInfo();
			pamvo = auditser.queryById(pamvo, uservo);
			boolean isexists = true;
			String fpath = "";
			if (pamvo != null) {
				fpath = pamvo.getVfilepath();
			}
			File afile = new File(fpath);
			if (!afile.exists()) {
				isexists = false;
			}
			if (isexists) {
				String path = getRequest().getSession().getServletContext().getRealPath("/");
				String typeiconpath = path + "images" + File.separator + "typeicon" + File.separator;
				if (fpath.toLowerCase().lastIndexOf(".pdf") > 0) {
					/*
					 * typeiconpath += "pdf.jpg"; afile = new
					 * File(typeiconpath);
					 */
				} else if (fpath.toLowerCase().lastIndexOf(".doc") > 0) {
					typeiconpath += "word.jpg";
					afile = new File(typeiconpath);
				} else if (fpath.toLowerCase().lastIndexOf(".xls") > 0) {
					typeiconpath += "excel.jpg";
					afile = new File(typeiconpath);
				} else if (fpath.toLowerCase().lastIndexOf(".ppt") > 0) {
					typeiconpath += "powerpoint.jpg";
					afile = new File(typeiconpath);
				} else if (fpath.toLowerCase().lastIndexOf(".zip") > 0 || fpath.toLowerCase().lastIndexOf(".rar") > 0) {
					typeiconpath += "zip.jpg";
					afile = new File(typeiconpath);
				} else {

				}
				os = getResponse().getOutputStream();
				is = new FileInputStream(afile);
				IOUtils.copy(is, os);
			}
		} catch (Exception e) {
			log.info(e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				log.info(e);
			}
		}
	}

	/**
	 * 变更审核保存
	 */
	public void updateChange() {
		Json json = new Json();
		try {
			checkUser();
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_66);
			if (data == null) {
				throw new BusinessException("数据不能为空");
			}
			auditser.updateChange(data, getLoginUserInfo());
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
	public void bathconfrim() {
		Json json = new Json();
		try {
			checkUser();
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_66);
			String data = getRequest().getParameter("data"); // 审核数据
			if (StringUtil.isEmpty(data)) {
				throw new BusinessException("数据不能为空");
			}

			String audit = getRequest().getParameter("audit");
			JSON auditjs = (JSON) JSON.parse(audit);
			Map<String, String> maping = FieldMapping.getFieldMapping(new ChangeApplyVO());
			ChangeApplyVO pamvo = DzfTypeUtils.cast(auditjs, maping, ChangeApplyVO.class,
					JSONConvtoJAVA.getParserConfig());

			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			ChangeApplyVO[] applyVOs = DzfTypeUtils.cast(arrayJson, maping, ChangeApplyVO[].class,
					JSONConvtoJAVA.getParserConfig());

			if (pamvo.getIopertype() != null && pamvo.getIopertype() == 2) {// 驳回
				if (StringUtil.isEmpty(pamvo.getVconfreason())) {
					throw new BusinessException("驳回原因不能为空");
				}
			}
			
			checkBeforeBatchAudit(applyVOs);

			int rignum = 0;
			int errnum = 0;
			List<ChangeApplyVO> rightlist = new ArrayList<ChangeApplyVO>();
			StringBuffer errmsg = new StringBuffer();
			if (applyVOs != null && applyVOs.length > 0) {
				for (ChangeApplyVO datavo : applyVOs) {
					try {
						datavo.setIopertype(pamvo.getIopertype());
						datavo.setVauditer(pamvo.getVauditer());
						datavo.setVconfreason(pamvo.getVconfreason());
						auditser.updateChange(datavo, uservo);
						rignum++;
						rightlist.add(datavo);
					} catch (Exception e) {
						errnum++;
						errmsg.append(e.getMessage()).append("<br>");
					}
				}
			}
			json.setSuccess(true);
			if (rignum > 0 && rignum == applyVOs.length) {
				json.setRows(Arrays.asList(applyVOs));
				json.setMsg("成功" + rignum + "条");
			} else if (errnum > 0) {
				json.setMsg("成功" + rignum + "条，失败" + errnum + "条，失败原因：" + errmsg.toString());
				json.setStatus(-1);
				if (rignum > 0) {
					json.setRows(rightlist);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 批量审核前校验
	 * @param applyVOs
	 * @throws DZFWarpException
	 */
	private void checkBeforeBatchAudit(ChangeApplyVO[] applyVOs) throws DZFWarpException {
		List<Integer> typelist = new ArrayList<Integer>();
		List<Integer> statlist = new ArrayList<Integer>();
		for(int i = 0; i <applyVOs.length; i++){
			if(i == 0){
				if(applyVOs[i].getIchangetype() != null && applyVOs[i].getIchangetype() == 3){
					typelist.add(applyVOs[i].getIchangetype());
				}else{
					typelist.add(1);
				}
				statlist.add(applyVOs[i].getIapplystatus());
			}else{
				if(applyVOs[i].getIchangetype() != null && applyVOs[i].getIchangetype() == 3){
					if(!typelist.contains(applyVOs[i].getIchangetype())){
						throw new BusinessException("请选择同一申请类型数据");
					}
				}else{
					if(!typelist.contains(1)){
						throw new BusinessException("请选择同一申请类型数据");
					}
				}
				if(!statlist.contains(applyVOs[i].getIapplystatus())){
					throw new BusinessException("请选择同一处理状态数据");
				}
			}
		}
	}

	/**
	 * 登录用户校验
	 */
	private void checkUser() {
		UserVO uservo = getLoginUserInfo();
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误");
		} else if (uservo == null) {
			throw new BusinessException("登陆用户错误");
		}
	}

}
