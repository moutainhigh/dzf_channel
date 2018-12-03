package com.dzf.action.sys.sys_power;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.SaleSetVO;
import com.dzf.model.channel.sys_power.DeductRateLogVO;
import com.dzf.model.channel.sys_power.DeductRateVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.ExportDeductRateExcel;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.sys_power.IDeductRateService;
import com.dzf.service.pub.IPubService;

/**
 * 扣款率设置Action
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "deductrate")
public class DeductRateAction extends BaseAction<DeductRateVO> {

	private static final long serialVersionUID = -8259647148929099004L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IDeductRateService rateser;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());
			
			//加盟商名称或编码过滤，采用代码分页
			if(!StringUtil.isEmpty(pamvo.getCorpcode())){
				List<DeductRateVO> retlist = rateser.queryAllData(pamvo);
				int page = pamvo == null ? 1 : pamvo.getPage();
				int rows = pamvo == null ? 10000 : pamvo.getRows();
				int len = retlist == null ? 0 : retlist.size();
				if (len > 0) {
					grid.setTotal((long) (len));
					SuperVO[] pageVOs = QueryUtil.getPagedVOs(retlist.toArray(new DeductRateVO[0]), page, rows);
					grid.setRows(Arrays.asList(pageVOs));
					grid.setSuccess(true);
					grid.setMsg("查询成功");
				} else {
					grid.setTotal(Long.valueOf(0));
					grid.setRows(new ArrayList<DeductRateVO>());
					grid.setSuccess(true);
					grid.setMsg("查询结果为空");
				}
			}else{//非加盟商名称或编码过滤，采用数据库分页
				int total = rateser.queryTotalRow(pamvo);
				grid.setTotal((long)(total));
				if(total > 0){
					List<DeductRateVO> clist = rateser.query(pamvo);
					grid.setRows(clist);
					grid.setMsg("查询成功");
				}else{
					grid.setRows(new ArrayList<DeductRateVO>());
					grid.setMsg("查询数据为空");
				}
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 保存
	 */
	public void save(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_55);
			
			String head = getRequest().getParameter("data");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new DeductRateVO());
			DeductRateVO hvo = DzfTypeUtils.cast(headjs, headmaping, DeductRateVO.class, JSONConvtoJAVA.getParserConfig());
			
			hvo = rateser.save(hvo, getLogincorppk(), getLoginUserid());
			json.setSuccess(true);
			json.setRows(hvo);
			json.setMsg("保存成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 批量设置-保存
	 */
	public void saveBatchSet(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_55);
			
			String head = getRequest().getParameter("data");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> mapping = FieldMapping.getFieldMapping(new DeductRateVO());
			DeductRateVO pamvo = DzfTypeUtils.cast(headjs, mapping, DeductRateVO.class, JSONConvtoJAVA.getParserConfig());
			
			String body = getRequest().getParameter("body"); // 设置数据
			body = body.replace("}{", "},{");
			body = "[" + body + "]";
			JSONArray bodyarray = (JSONArray) JSON.parseArray(body);
			DeductRateVO[] rateVOs = DzfTypeUtils.cast(bodyarray, mapping, DeductRateVO[].class,
					JSONConvtoJAVA.getParserConfig());
			if (rateVOs == null || rateVOs.length == 0) {
				throw new BusinessException("批量设置数据不能为空");
			}
			
			
			int rignum = 0;
			int errnum = 0;
			List<DeductRateVO> rightlist = new ArrayList<DeductRateVO>();
			StringBuffer errmsg = new StringBuffer();
			for(DeductRateVO ratevo : rateVOs){
				try {
					ratevo.setInewrate(pamvo.getInewrate());//新增
					ratevo.setIrenewrate(pamvo.getIrenewrate());//续费
					ratevo = rateser.save(ratevo, getLogincorppk(), getLogin_userid());
					rignum++;
					rightlist.add(ratevo);
				} catch (Exception e) {
					errnum++;
					errmsg.append(e.getMessage()).append("<br>");
				}
			}
			
			json.setSuccess(true);
			if (rignum > 0 && rignum == rateVOs.length) {
				json.setRows(rightlist);
				json.setMsg("成功" + rignum + "条");
			} else if (errnum > 0) {
				json.setMsg("成功" + rignum + "条，失败" + errnum + "条，失败原因：" + errmsg.toString());
				json.setStatus(-1);
				if (rignum > 0) {
					json.setRows(rightlist);
				}
			}
			
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 查询变更记录
	 */
	public void queryLog() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			List<DeductRateLogVO> list = rateser.queryLog(getLogincorppk(), pamvo.getPk_bill());
			grid.setSuccess(true);
			grid.setMsg("查询成功");
			grid.setRows(list);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * Excel导出方法
	 */
	public void exportExcel() {
		UserVO uservo = getLoginUserInfo();
		checkUser(uservo);
		
		String strlist = getRequest().getParameter("strlist");
		if (strlist == null) {
			return;
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new DeductRateVO());
		DeductRateVO[] rateVOs = DzfTypeUtils.cast(exparray, mapping, DeductRateVO[].class,
				JSONConvtoJAVA.getParserConfig());
		ArrayList<DeductRateVO> explist = new ArrayList<DeductRateVO>();
		for (DeductRateVO vo : rateVOs) {
			explist.add(vo);
		}

		HttpServletResponse response = getResponse();

		ExportDeductRateExcel<DeductRateVO> ex = new ExportDeductRateExcel<DeductRateVO>();
		Map<String, String> map = getExpFieldMap();
		String[] enFields = new String[map.size()];
		String[] cnFields = new String[map.size()];
		// 填充普通字段数组
		int count = 0;
		for (Entry<String, String> entry : map.entrySet()) {
			enFields[count] = entry.getKey();
			cnFields[count] = entry.getValue();
			count++;
		}
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			byte[] length = ex.exportExcel("扣款率设置", cnFields, enFields, explist, toClient);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
			toClient.flush();
			servletOutputStream.flush();
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
			if (toClient != null) {
				try {
					toClient.close();
				} catch (IOException e) {
					log.error("导出失败", e);
				}
			}
			if (servletOutputStream != null) {
				try {
					servletOutputStream.close();
				} catch (IOException e) {
					log.error("导出失败", e);
				}
			}
		}
	}
	
	/**
	 * 获取导出列
	 * @return
	 */
	public Map<String, String> getExpFieldMap(){
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("corpcode", "加盟商编码");
		map.put("corpname", "加盟商名称");
		map.put("inewrate", "新增");		
		map.put("irenewrate", "续费");
		return map;
	}
	
	/**
	 * 登录用户校验
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
			throw new BusinessException("登陆用户错误");
		}else if(uservo == null){
			throw new BusinessException("登陆用户错误");
		}
	}

}
