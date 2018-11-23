package com.dzf.action.channel.dealmanage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.channel.stock.StockOutBVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IStockOutService;
import com.dzf.service.pub.IPubService;

/**
 * 出库单
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "stockout")
public class StockOutAction extends BaseAction<StockOutVO>{

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
			String bills = getRequest().getParameter("bills");
			if (StringUtil.isEmpty(corpid)) {
				throw new BusinessException("请选择一个加盟商");
			}
			 List<StockOutBVO> vos = stockOut.queryOrders(corpid,bills);
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
			StockOutVO vo = new StockOutVO();
			vo = (StockOutVO) DzfTypeUtils.cast(getRequest(), vo);
			vo.setVconfirmid(getLoginUserid());
			stockOut.updateCommit(vo);
			json.setSuccess(true);
			json.setRows(vo);
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
			String head = getRequest().getParameter("head");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new StockOutVO());
			StockOutVO headvo = DzfTypeUtils.cast(headjs, headmaping, StockOutVO.class, JSONConvtoJAVA.getParserConfig());
			headvo.setLogisticsunit(getRequest().getParameter("logunit"));
			headvo.setPk_logistics(getRequest().getParameter("logid"));
			headvo.setFastcode(getRequest().getParameter("fcode"));
			headvo.setVdeliverid(getLoginUserid());
//			headvo.setFathercorp(getLogincorppk());
			stockOut.updateDeliver(headvo);
			json.setSuccess(true);
			json.setRows(headvo);
			json.setMsg("操作成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	private StockOutVO requestDealStep(HttpServletRequest request) {
		String head = request.getParameter("head");
		String body = request.getParameter("body");
		if(StringUtils.isEmpty(body)){
			throw new BusinessException("");
		}
		body = body.replace("}{", "},{");
		body = "[" + body+ "]";
		JSON headjs = (JSON) JSON.parse(head);
		JSONArray array = (JSONArray) JSON.parseArray(body);
		Map<String, String> headmaping = FieldMapping.getFieldMapping(new StockOutVO());
		Map<String, String> bodymapping = FieldMapping.getFieldMapping(new StockOutBVO());
		
		StockOutVO headvo = DzfTypeUtils.cast(headjs, headmaping, StockOutVO.class, JSONConvtoJAVA.getParserConfig());
		headvo.setFathercorp(getLogincorppk());
		if(StringUtil.isEmpty(headvo.getPk_stockout())){
			headvo.setCoperatorid(getLoginUserInfo().getCuserid());
		}
		StockOutBVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, StockOutBVO[].class,
				JSONConvtoJAVA.getParserConfig());
		for (StockOutBVO stockOutBVO : bodyvos) {
			if(!StringUtil.isEmpty(headvo.getPk_stockout())){
				stockOutBVO.setPk_stockout(headvo.getPk_stockout());
			}
			stockOutBVO.setFathercorp(headvo.getFathercorp());
			stockOutBVO.setPk_corp(headvo.getPk_corp());
			stockOutBVO.setPk_warehouse(IStatusConstant.CK_ID);//仓库主键
		}
		headvo.setTableVO("cn_stockout_b", bodyvos);
		return headvo;
	}
	
	/**
	 * 打印
	 */
	@SuppressWarnings("unchecked")
	public void print() {
		Json json = new Json();
        try {
            UserVO uservo = getLoginUserInfo();
    		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
    			throw new BusinessException("登陆用户错误");
    		}else if(uservo == null){
    			throw new BusinessException("登陆用户错误");
    		}
    		pubService.checkFunnode(uservo, IFunNode.CHANNEL_52);
        	String soutid =getRequest().getParameter("id");
    		if(StringUtils.isEmpty(soutid)){
    			throw new BusinessException("请选择一行数据");
    		}
    		StockOutVO headvo = stockOut.queryForPrint(soutid);
            StockOutPrint<StockOutVO> print = new StockOutPrint<StockOutVO>();
            print.printFileTrans(headvo);
        } catch (Exception e) {
            printErrorLog(json, log, e, "打印失败");
        }
	}
	
	/**
	 * 查询有订单的加盟商数据
	 */
	public void queryChannel() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			List<ComboBoxVO> list = stockOut.queryChannel();
			if(list != null && list.size() > 0){
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}else{
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	public void queryLogist() {
		Grid grid = new Grid();
		try {
			List<ComboBoxVO> list = stockOut.queryLogist();
			if(list != null && list.size() > 0){
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}else{
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	
}
