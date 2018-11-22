package com.dzf.action.channel.dealmanage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.StockInBVO;
import com.dzf.model.channel.dealmanage.StockInVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.channel.stock.SupplierVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IStockInService;
import com.dzf.service.pub.IPubService;

/**
 * 入库单
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "stockin")
public class StockInAction extends BaseAction<StockInVO> {

	private static final long serialVersionUID = 1647952408841301452L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IStockInService stockinser;
	
	@Autowired
	private IPubService pubser;
	
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
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());
			int total = stockinser.queryTotalRow(pamvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<StockInVO> clist = stockinser.query(pamvo);
				grid.setRows(clist);
				grid.setMsg("查询成功");
			}else{
				grid.setRows(new ArrayList<StockInVO>());
				grid.setMsg("查询数据为空");
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询供应商
	 */
	public void querySupplierRef() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			List<SupplierVO> list = stockinser.querySupplierRef(pamvo);
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ChnAreaVO>());
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
	 * 保存
	 */
	public void save(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_50);
			
			String head = getRequest().getParameter("head");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new StockInVO());
			StockInVO hvo = DzfTypeUtils.cast(headjs, headmaping, StockInVO.class, JSONConvtoJAVA.getParserConfig());
			SetDefaultValue(hvo);
			
			Map<String, String> bmapping = FieldMapping.getFieldMapping(new StockInBVO());
			String body = getRequest().getParameter("body"); // 界面数据
			body = body.replace("}{", "},{");
			body = "[" + body + "]";
			JSONArray bodyarray = (JSONArray) JSON.parseArray(body);
			StockInBVO[] bodyVOs = DzfTypeUtils.cast(bodyarray, bmapping, StockInBVO[].class,
					JSONConvtoJAVA.getParserConfig());
			if (bodyVOs == null || bodyVOs.length == 0) {
				throw new BusinessException("表体数据不能为空");
			}
			
			List<StockInBVO> blist = getDataList(bmapping);
			if(blist != null && blist.size() > 0){
				hvo.setChildren(blist.toArray(new StockInBVO[0]));
			}
			hvo = stockinser.save(hvo, getLogin_corp(), bodyVOs);
			json.setSuccess(true);
			json.setRows(hvo);
			json.setMsg("保存成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 新增设置默认值
	 * @param hvo
	 * @throws DZFWarpException
	 */
	private void SetDefaultValue(StockInVO hvo) throws DZFWarpException{
		if(StringUtil.isEmpty(hvo.getPk_stockin())){
			hvo.setPk_corp(getLogincorppk());
			hvo.setCoperatorid(getLogin_userid());
			hvo.setDoperatedate(new DZFDateTime());
			hvo.setDr(0);
			hvo.setVstatus(IStatusConstant.ISTOCKINSTATUS_1);
		}
	}
	
	/**
	 * 获取操作数据
	 * @return
	 */
	private List<StockInBVO> getDataList(Map<String, String> bmapping) {
		List<StockInBVO> list = new ArrayList<StockInBVO>();
		
		String addfile = getRequest().getParameter("adddata"); // 新增数据

		StockInBVO[] addBVOs = null;
		StockInBVO[] updBVOs = null;
		StockInBVO[] delBVOs = null;
		if (!StringUtil.isEmpty(addfile)) {
			addfile = addfile.replace("}{", "},{");
			addfile = "[" + addfile + "]";
			JSONArray addarray = (JSONArray) JSON.parseArray(addfile);
			addBVOs = DzfTypeUtils.cast(addarray, bmapping, StockInBVO[].class, JSONConvtoJAVA.getParserConfig());
			if (addBVOs != null && addBVOs.length > 0) {
				for (StockInBVO bvo : addBVOs) {
					bvo.setPk_corp(getLogin_corp());
					bvo.setDr(0);
					bvo.setPk_warehouse(IStatusConstant.CK_ID);//仓库主键
					if(StringUtil.isEmpty(bvo.getPk_goods())){
						throw new BusinessException("商品主键不能为空");
					}
					if(StringUtil.isEmpty(bvo.getPk_goodsspec())){
						throw new BusinessException("商品规格主键不能为空");
					}
					list.add(bvo);
				}
			}
		}
		String updfile = getRequest().getParameter("upddata"); // 更新数据
		if (!StringUtil.isEmpty(updfile)) {
			updfile = updfile.replace("}{", "},{");
			updfile = "[" + updfile + "]";
			JSONArray updarray = (JSONArray) JSON.parseArray(updfile);
			updBVOs = DzfTypeUtils.cast(updarray, bmapping, StockInBVO[].class, JSONConvtoJAVA.getParserConfig());
			if(updBVOs != null && updBVOs.length > 0){
				list.addAll(Arrays.asList(updBVOs));
			}
		}
		String delfile = getRequest().getParameter("deldata"); // 删除数据
		if (!StringUtil.isEmpty(delfile)) {
			delfile = delfile.replace("}{", "},{");
			delfile = "[" + delfile + "]";
			JSONArray delarray = (JSONArray) JSON.parseArray(delfile);
			delBVOs = DzfTypeUtils.cast(delarray, bmapping, StockInBVO[].class, JSONConvtoJAVA.getParserConfig());
			if(delBVOs != null && delBVOs.length > 0){
				for(StockInBVO bvo : delBVOs){
					bvo.setDr(1);
					list.add(bvo);
				}
			}
		}
		
		return list;
	}

	
	/**
	 * 通过主表信息查询主子表信息
	 */
	public void queryById() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			
			StockInVO qryvo = (StockInVO) DzfTypeUtils.cast(getRequest(), new StockInVO());
			StockInVO retvo = stockinser.queryById(qryvo.getPk_stockin(), getLogincorppk(), 1);
			json.setRows(retvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 删除入库单
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
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_50);
			
			StockInVO pamvo = new StockInVO();
			pamvo = (StockInVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			stockinser.delete(pamvo);
			json.setSuccess(true);
			json.setMsg("删除入库单成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除入库单失败");
		}
		writeJson(json);
	}
	
	/**
	 * 确认入库
	 */
	public void confirmData() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_50);
			String data = getRequest().getParameter("data");
			if (StringUtil.isEmpty(data)) {
				throw new BusinessException("数据不能为空");
			}
			
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> custmaping = FieldMapping.getFieldMapping(new StockInVO());
			StockInVO[] stockinVOs = DzfTypeUtils.cast(arrayJson, custmaping, StockInVO[].class,
					JSONConvtoJAVA.getParserConfig());
			
			List<StockInVO> rightlist = new ArrayList<StockInVO>();
			int rignum = 0;
			int errnum = 0;
			StringBuffer errmsg = new StringBuffer();
			if (stockinVOs != null && stockinVOs.length > 0) {
				for (StockInVO vo : stockinVOs) {
					try {
						vo = stockinser.updateconfirm(vo, getLoginUserid());
						rightlist.add(vo);
						rignum++;
					} catch (Exception e) {
						errnum++;
						errmsg.append(e.getMessage()).append("<br>");
					}
				}
			}
			json.setSuccess(true);
			if (rignum > 0 && rignum == stockinVOs.length) {
				json.setRows(Arrays.asList(stockinVOs));
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
	 * 保存供应商
	 */
	public void saveSupplier() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_50);
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			data.setPk_corp(getLogincorppk());
			data.setCoperatorid(getLoginUserid());
			stockinser.saveSupplier(data);
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
}
