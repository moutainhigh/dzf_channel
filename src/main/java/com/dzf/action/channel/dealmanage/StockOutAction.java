package com.dzf.action.channel.dealmanage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IStockOutService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.report.PrintUtil;

/**
 * 出库单
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "stockout")
public class StockOutAction extends PrintUtil<StockOutVO>{

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IStockOutService stockOut;
	
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
			int total = stockOut.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<StockOutVO> clist = stockOut.query(paramvo);
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
			StockOutVO retvo = stockOut.queryByID(soutid);
			json.setSuccess(true);
			json.setRows(retvo);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	public void queryOrders() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String corpid = getRequest().getParameter("corpid");
			if (StringUtil.isEmpty(corpid)) {
				throw new BusinessException("请选择一个加盟商");
			}
			 List<StockOutBVO> vos = stockOut.queryOrders(corpid);
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
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_52);
			StockOutVO headvo = requestDealStep(getRequest());
			if (headvo == null) {
				throw new BusinessException("数据信息不能为空");
			}
			if(StringUtil.isEmpty(headvo.getPk_stockout())){
				headvo.setFathercorp(getLogincorppk());
				headvo.setCoperatorid(getLoginUserid());
				stockOut.saveNew(headvo);
			}else{
				stockOut.saveEdit(headvo);
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
	 * 删除商品
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
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_52);
			StockOutVO pamvo = new StockOutVO();
			pamvo = (StockOutVO) DzfTypeUtils.cast(getRequest(), pamvo);
			stockOut.delete(pamvo);
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
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_52);
			String head = getRequest().getParameter("head");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new StockOutVO());
			StockOutVO headvo = DzfTypeUtils.cast(headjs, headmaping, StockOutVO.class, JSONConvtoJAVA.getParserConfig());
			headvo.setLogisticsunit(getRequest().getParameter("logunit"));
			headvo.setFastcode(getRequest().getParameter("fcode"));
			headvo.setVconfirmid(getLoginUserid());
			
			stockOut.updateCommit(headvo);
			json.setSuccess(true);
			json.setRows(headvo);
			json.setMsg("操作成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	public void updateDeliver(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_52);
			StockOutVO vo = new StockOutVO();
			vo = (StockOutVO) DzfTypeUtils.cast(getRequest(), vo);
			vo.setVdeliverid(getLoginUserid());
			stockOut.updateDeliver(vo);
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
		body = body.replace("}{", "},{");
		body = "[" + body+ "]";
		JSON headjs = (JSON) JSON.parse(head);
		JSONArray array = (JSONArray) JSON.parseArray(body);
		Map<String, String> headmaping = FieldMapping.getFieldMapping(new StockOutVO());
		Map<String, String> bodymapping = FieldMapping.getFieldMapping(new StockOutBVO());
		
		StockOutVO headvo = DzfTypeUtils.cast(headjs, headmaping, StockOutVO.class, JSONConvtoJAVA.getParserConfig());
		if(StringUtil.isEmpty(headvo.getPk_stockout())){
			headvo.setCoperatorid(getLoginUserInfo().getCuserid());
			headvo.setFathercorp(getLogincorppk());
		}
		StockOutBVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, StockOutBVO[].class,
				JSONConvtoJAVA.getParserConfig());
		headvo.setTableVO("cn_stockout_b", bodyvos);
		return headvo;
	}
	
	/**
	 * 打印
	 */
	@SuppressWarnings("unchecked")
	public void print() {
		try{
			String strlist =getRequest().getParameter("strlist");
			if(strlist==null){
				throw new BusinessException("打印数据不能为空!");
			}
			String columns =getRequest().getParameter("columns");
			JSONArray array = (JSONArray) JSON.parseArray(strlist);
			JSONArray headlist = (JSONArray) JSON.parseArray(columns);
			List<String> heads = new ArrayList<String>();
			List<String> fieldslist = new ArrayList<String>();
			setIscross(DZFBoolean.TRUE);
			Map<String, String> name = null;
			int[] widths =new  int[]{};
			int len=headlist.size();
			for (int i = 0 ; i< len; i ++) {
				 name=(Map<String, String>) headlist.get(i);
				 heads.add(name.get("title"));
				 fieldslist.add(name.get("field"));
				 widths =ArrayUtils.addAll(widths, new int[] {3}); 
			}
			String[] fields= (String[]) fieldslist.toArray(new String[fieldslist.size()]);		
			
			//字符类型字段(取界面元素id)
			List<String> list = new ArrayList<String>();
			list.add("vccode");
			list.add("corpkco");
			list.add("corpkna");
			list.add("ddate");
			list.add("istypename");
			list.add("chgcycle");
			list.add("vremth");
			printMultiColumn(array, "出库单", heads, fields, widths, 20, list,null);		
		}catch(Exception e){
			log.error("打印失败",e);		
		}		
	}
	
}
