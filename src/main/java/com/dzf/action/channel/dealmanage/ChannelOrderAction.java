package com.dzf.action.channel.dealmanage;

import java.util.ArrayList;
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
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.model.channel.invoice.ChInvoiceBVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IChannelOrderService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 加盟商订单
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "channelorder")
public class ChannelOrderAction extends BaseAction<GoodsBillVO> {

	private static final long serialVersionUID = -3903910761366206337L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChannelOrderService orderser;
	
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
			GoodsBillVO paramvo = (GoodsBillVO) DzfTypeUtils.cast(getRequest(), new GoodsBillVO());
			int total = orderser.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<GoodsBillVO> clist = orderser.query(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<GoodsBillVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 操作数据（确认、取消订单、取消确认、发票申请）
	 */
	public void operData() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_45);
			String data = getRequest().getParameter("data");
			if (StringUtil.isEmpty(data)) {
				throw new BusinessException("数据不能为空");
			}
			String type = getRequest().getParameter("type");
			Integer itype = Integer.parseInt(type);
			Map<String, String> maping = FieldMapping.getFieldMapping(new GoodsBillVO());
			if(itype == 1 || itype == 3 || itype == 4){//1：确认订单；3：取消确认；4：发票申请；
				//单条数据
				JSON djson = (JSON) JSON.parse(data);
				GoodsBillVO ordervo = DzfTypeUtils.cast(djson, maping, GoodsBillVO.class,
						JSONConvtoJAVA.getParserConfig());
				orderser.updateData(ordervo, itype, getLoginUserid());
				
				if(itype == 1 || itype == 3){
					StringBuffer opername = new StringBuffer();
					if (itype == 1) {
						opername.append("确认订单：");
					}else if(itype == 3){
						opername.append("取消确认：");
					}
					opername.append(ordervo.getVbillcode());
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_43.getValue(),  opername.toString(), ISysConstants.SYS_3);
				}
				json.setMsg("操作成功");
			}else if(itype == 2){//2：取消订单；
				//多条数据
				data = data.replace("}{", "},{");
				data = "[" + data + "]";
				JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
				GoodsBillVO[] bVOs = DzfTypeUtils.cast(arrayJson, maping, GoodsBillVO[].class,
						JSONConvtoJAVA.getParserConfig());
				int rightnum = 0;
				int errornum = 0;
				StringBuffer errmsg = new StringBuffer();
				for(GoodsBillVO bvo : bVOs){
					try {
						orderser.updateData(bvo, itype, getLoginUserid());
						rightnum ++;
					} catch (Exception e) {
						errmsg.append(e.getMessage());
						errornum ++;
					}
				}
				
				if(errornum > 0){
					json.setMsg("成功" + rightnum + "条，失败" + errornum + "条，失败原因：" + errmsg.toString());
					json.setStatus(-1);
				}else{
					json.setMsg("成功" + rightnum + "条");
				}
			}
			json.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 查询订单详情
	 */
	public void qryOrderDet() {
		Json json = new Json();
		try {
			GoodsBillVO pamvo = (GoodsBillVO) DzfTypeUtils.cast(getRequest(), new GoodsBillVO());
			if (StringUtil.isEmptyWithTrim(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLoginCorpInfo().getPk_corp());
			}
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			GoodsBillVO detvo = orderser.qryOrderDet(pamvo);
			json.setRows(detvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 查询订单发票表信息
	 */
	public void queryInvoiceInfo() {
		Json json = new Json();
		try {
			GoodsBillVO pamvo = (GoodsBillVO) DzfTypeUtils.cast(getRequest(), new GoodsBillVO());
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			ChInvoiceVO detvo = orderser.queryInvoiceInfo(pamvo);
			json.setData(detvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 保存订单发票信息
	 */
	public void saveInvoice() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_45);
			
			String head = getRequest().getParameter("head");
			if (StringUtil.isEmpty(head)) {
				throw new BusinessException("发票表头数据不能为空");
			}
			Map<String, String> hmaping = FieldMapping.getFieldMapping(new ChInvoiceVO());
			JSON hjson = (JSON) JSON.parse(head);
			ChInvoiceVO hvo = DzfTypeUtils.cast(hjson, hmaping, ChInvoiceVO.class,
					JSONConvtoJAVA.getParserConfig());
			
			Map<String, String> bmaping = FieldMapping.getFieldMapping(new ChInvoiceBVO());
			String body = getRequest().getParameter("body");
			JSONArray arrayJson = (JSONArray) JSON.parseArray(body);
			ChInvoiceBVO[] bVOs = DzfTypeUtils.cast(arrayJson, bmaping, ChInvoiceBVO[].class,
					JSONConvtoJAVA.getParserConfig());
			
			hvo.setChildren(bVOs);
			orderser.saveInvoice(hvo, getLoginUserid());
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
}
