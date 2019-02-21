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
import com.dzf.action.channel.expfield.ChnPayAuditExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.dealmanage.StockSumVO;
import com.dzf.model.channel.dealmanage.StockSumVO;
import com.dzf.model.channel.stock.GoodsNumVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IStockSumService;
import com.dzf.service.pub.LogRecordEnum;

@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "stocksum")
public class StockSumAction extends BaseAction<StockSumVO>{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IStockSumService stocksum;

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
			StockSumVO qvo = new StockSumVO();
			qvo = (StockSumVO) DzfTypeUtils.cast(getRequest(), qvo);
			int total = stocksum.queryTotalRow(qvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<StockSumVO> clist = stocksum.query(qvo);
				grid.setRows(clist);
				grid.setMsg("查询成功!");
			}else{
				grid.setRows(new ArrayList<StockSumVO>());
				grid.setMsg("查询数据为空!");
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
}
