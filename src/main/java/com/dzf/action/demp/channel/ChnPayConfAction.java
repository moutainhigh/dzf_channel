package com.dzf.action.demp.channel;

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
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
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
}
