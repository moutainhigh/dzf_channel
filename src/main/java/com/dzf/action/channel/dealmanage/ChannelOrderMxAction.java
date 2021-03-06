package com.dzf.action.channel.dealmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.expfield.ChannelOrderMxAuditExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.GoodsBillMxVO;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IChannelOrderMxService;

/**
 * 加盟商订单
 * @author yy
 *
 */
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "channelordermx")
public class ChannelOrderMxAction extends BaseAction<GoodsBillMxVO> {

	private static final long serialVersionUID = -3903910761366206337L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChannelOrderMxService orderser;

	/**
	 * 查询明细O
	 */
	public void querymx() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			GoodsBillMxVO paramvo = (GoodsBillMxVO) DzfTypeUtils.cast(getRequest(), new GoodsBillMxVO());
			int total = orderser.queryTotalRowMx(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<GoodsBillMxVO> clist = orderser.querymx(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<GoodsBillMxVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询商品下拉
	 */
	public void queryComboBox() {
		Grid grid = new Grid();
		try {
			List<GoodsBoxVO> list = orderser.queryComboBox();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<GoodsBoxVO>());
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
	 * 订单明细表导出
	 */
	public void exportAuditExcel(){
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new GoodsBillMxVO());
		GoodsBillMxVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,GoodsBillMxVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<GoodsBillMxVO> ex = new Excelexport2003<>();
		ChannelOrderMxAuditExcelField fields = new ChannelOrderMxAuditExcelField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String filename = fields.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
	        response.addHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
		} catch (Exception e) {
			log.error("导出失败",e);
		}  finally {
			if(toClient != null){
				try {
					toClient.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
			if(servletOutputStream != null){
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
		}
	}
	
}
