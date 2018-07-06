package com.dzf.action.channel.refund;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.refund.RefundBillVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.service.channel.refund.IRefundBillService;
import com.dzf.service.pub.IPubService;

/**
 * 付款单
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/refund")
@Action(value = "refundbill")
public class RefundBillAction extends BaseAction<RefundBillVO> {

	private static final long serialVersionUID = -7404412877485478067L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IRefundBillService refundser;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
		    QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			int total = refundser.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<RefundBillVO> list = refundser.query(paramvo);
				grid.setRows(list);
			}else{
				grid.setRows(new ArrayList<RefundBillVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				setDefaultValue(data);
				data = refundser.save(data, getLogincorppk());
				json.setRows(data);
				json.setSuccess(true);	
				json.setMsg("保存成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "保存失败");
			}
		}else {
			json.setSuccess(false);
			json.setMsg("保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 保存前设置默认值
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(RefundBillVO data) throws DZFWarpException{
		if(StringUtil.isEmpty(data.getPk_refund())){
			data.setFathercorp(getLogincorppk());
			data.setCoperatorid(getLoginUserid());
			data.setDoperatedate(new DZFDate());
			data.setTs(new DZFDateTime());
			data.setDr(0);
			data.setIstatus(IStatusConstant.IREFUNDSTATUS_0);//待确认
			if(!StringUtil.isEmpty(data.getVbillcode())){
				data.setVbillcode(data.getVbillcode().trim());
			}
		}
		data.setUpdatets(new DZFDateTime());
	}
	
	/**
	 * 查询返点相关金额
	 */
	public void queryRefundMny() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				RefundBillVO refvo = refundser.queryRefundMny(data);
				json.setSuccess(true);
				json.setRows(refvo);
				json.setMsg("操作成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "操作失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("操作数据为空");
		}
		writeJson(json);
	}
	
	/**
	 * 保存前校验
	 */
	public void checkBeforeSave() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				RefundBillVO refvo = refundser.checkBeforeSave(data);
				json.setSuccess(true);
				json.setRows(refvo);
				json.setMsg("操作成功");
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
