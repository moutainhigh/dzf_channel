package com.dzf.action.channel.report;

import java.io.BufferedOutputStream;
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
import com.dzf.action.channel.expfield.GoodsSalesAnalysisExeclField;
import com.dzf.action.pub.BaseAction;
import com.dzf.dao.jdbc.framework.util.InOutUtil;
import com.dzf.model.channel.report.GoodsSalesAnalysisVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.report.IGoodsSalesAnalysisService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 加盟商商品销售分析
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "goodssalesanalysis")
public class GoodsSalesAnalysisAction extends BaseAction<GoodsSalesAnalysisVO> {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IPubService pubser;

	@Autowired
	private IGoodsSalesAnalysisService analysisSer;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_63);
			checkUser(uservo);
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			List<GoodsSalesAnalysisVO> list = analysisSer.query(pamvo);
			if(list != null && list.size() > 0){
				grid.setRows(list);
			}else{
				grid.setRows(new ArrayList<GoodsSalesAnalysisVO>());
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 导出
	 */
	public void onExport() {
		String strlist = getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new GoodsSalesAnalysisVO());
		GoodsSalesAnalysisVO[] expVOs = DzfTypeUtils.cast(exparray, mapping, GoodsSalesAnalysisVO[].class,
				JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<GoodsSalesAnalysisVO> ex = new Excelexport2003<GoodsSalesAnalysisVO>();
		GoodsSalesAnalysisExeclField fields = new GoodsSalesAnalysisExeclField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String filename = fields.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "导出合同列表", ISysConstants.SYS_3);
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
		    InOutUtil.close(toClient, "加盟商商品销售分析关闭输出流");
			InOutUtil.close(servletOutputStream, "加盟商商品销售分析关闭输入流");
		}
	}

	/**
	 * 登录用户校验
	 * 
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误");
		} else if (uservo == null) {
			throw new BusinessException("登陆用户错误");
		}
	}
}
