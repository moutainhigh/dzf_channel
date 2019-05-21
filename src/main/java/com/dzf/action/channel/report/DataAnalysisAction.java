package com.dzf.action.channel.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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
import com.dzf.action.pub.BaseAction;
import com.dzf.dao.jdbc.framework.util.InOutUtil;
import com.dzf.model.channel.report.DataAnalysisVO;
import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.report.IDataAnalysisService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.report.ExportExcel;

/**
 * 加盟商
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "dataanalysis")
public class DataAnalysisAction extends BaseAction<DataAnalysisVO> {

	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IDataAnalysisService analyser;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if(pamvo == null){
				pamvo = new QryParamVO();
			}
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_72);
			long total = analyser.queryTotalRow(pamvo);
			if(total > 0){
				List<DataAnalysisVO> list = analyser.query(pamvo);
				grid.setTotal(total);
				grid.setRows(list);
			}else{
				grid.setRows(new ArrayList<DataAnalysisVO>());
			}
			
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 导出
	 */
	public void onExport() {
		QryParamVO pamvo = new QryParamVO();
		pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
		if(pamvo == null){
			pamvo = new QryParamVO();
		}
		pamvo.setRows(1000000);
		
		DataAnalysisVO[] expVOs = null;
		if(!StringUtil.isEmpty(pamvo.getVbillcode())){
			JSONArray exparray = (JSONArray) JSON.parseArray(pamvo.getVbillcode());
			Map<String, String> mapping = FieldMapping.getFieldMapping(new DataAnalysisVO());
			expVOs = DzfTypeUtils.cast(exparray, mapping, DataAnalysisVO[].class,
					JSONConvtoJAVA.getParserConfig());
		}else{
			List<DataAnalysisVO> clist = analyser.query(pamvo);
			
			if(clist != null && clist.size() > 0){
				expVOs = clist.toArray(new DataAnalysisVO[0]);
			}else{
				return;
			}
		}
		String hblcols = getRequest().getParameter("hblcols");
		JSONArray hblcolsarray = (JSONArray) JSON.parseArray(hblcols);//合并列信息
		
		String cols = getRequest().getParameter("cols");
		JSONArray colsarray = (JSONArray) JSON.parseArray(cols);//除冻结列之外，导出字段编码
		
		//1、导出字段名称
		List<String> exptitlist = new ArrayList<String>();
		exptitlist.add("大区");
		exptitlist.add("省市");
		exptitlist.add("加盟商");
		exptitlist.add("加盟日期");
		exptitlist.add("解约日期");
		
		//2、导出字段编码
		List<String> expfieidlist = new ArrayList<String>();
		//3、合并列字段名称
		List<String> hbltitlist = new ArrayList<String>();
		//4、合并列字段下标
		List<Integer> hblindexlist = new ArrayList<Integer>();
		//5、合并行字段名称
		List<String> hbhtitlist = new ArrayList<String>();
		hbhtitlist.add("大区");
		hbhtitlist.add("省市");
		hbhtitlist.add("加盟商");
		hbhtitlist.add("加盟日期");
		hbhtitlist.add("解约日期");

		hbhtitlist.add("存量合同");
		hbhtitlist.add("0扣款(非存量)合同");
		hbhtitlist.add("非存量合同数");

		hbhtitlist.add("合同代账费");
		hbhtitlist.add("账本费");
		hbhtitlist.add("保证金");
		hbhtitlist.add("预存款余额");
		hbhtitlist.add("返点余额");
		hbhtitlist.add("合同扣款");
		hbhtitlist.add("商品购买");
		hbhtitlist.add("总用户数");
		//6、合并行字段下标
		Integer[] hbhindexs = new Integer[]{0,1,2,3,4,11,12,13,14,15,16,17,18,19,20,21};
		//7、字符集合
		List<String> strslist = new ArrayList<String>();
		strslist.add("corpname");
		strslist.add("areaname");
		strslist.add("vprovname");
		strslist.add("djoindate");
		strslist.add("drelievedate");
		strslist.add("ismcustnum");
		strslist.add("igecustnum");
		strslist.add("ismstocknum");
		strslist.add("igestocknum");
		strslist.add("ismnstocknum");
		strslist.add("igenstocknum");
		strslist.add("istockconnum");
		strslist.add("izeroconnum");
		strslist.add("instockconnum");
		
		strslist.add("isumcustnum");
		
		strslist.add("ssumnum");
		//8、金额集合
		List<String> mnylist = new ArrayList<String>();
		mnylist.add("ntotalmny");
		mnylist.add("naccountmny");
		mnylist.add("nbookmny");
		mnylist.add("ndepositmny");
		mnylist.add("npaymentmny");
		mnylist.add("nrebatemny");
		mnylist.add("ndeductmny");
		mnylist.add("ngoodsbuymny");
		
		Map<String, String> field = null;
		
		expfieidlist.add("corpname");
		expfieidlist.add("areaname");
		expfieidlist.add("vprovname");
		expfieidlist.add("djoindate");
		expfieidlist.add("drelievedate");
		
		expfieidlist.add("ismcustnum");
		expfieidlist.add("igecustnum");
		expfieidlist.add("ismstocknum");
		expfieidlist.add("igestocknum");
		expfieidlist.add("ismnstocknum");
		expfieidlist.add("igenstocknum");
		
		expfieidlist.add("istockconnum");
		expfieidlist.add("izeroconnum");
		expfieidlist.add("instockconnum");
		
		expfieidlist.add("naccountmny");
		expfieidlist.add("nbookmny");
		expfieidlist.add("ndepositmny");
		expfieidlist.add("npaymentmny");
		expfieidlist.add("nrebatemny");
		expfieidlist.add("ndeductmny");
		expfieidlist.add("ngoodsbuymny");
		
		expfieidlist.add("isumcustnum");
		
		for(int i = 0; i < colsarray.size(); i++){

			if(i >= 2 && i < 8){
				if(i % 2 == 0){
					exptitlist.add("小规模");
				}else{
					exptitlist.add("一般人");
				}
			}
		}
		
		exptitlist.add("存量合同");
		exptitlist.add("0扣款(非存量)合同");
		exptitlist.add("非存量合同数");
		
		exptitlist.add("合同代账费");
		exptitlist.add("账本费");
		exptitlist.add("保证金");
		exptitlist.add("预存款余额");
		exptitlist.add("返点余额");
		exptitlist.add("合同扣款");
		exptitlist.add("商品购买");
		exptitlist.add("总用户数");
		
		int j = 3;
		for(int i = 0; i < hblcolsarray.size(); i++){
			field = (Map<String, String>) hblcolsarray.get(i);
			if("2".equals(String.valueOf(field.get("colspan")))){
				hbltitlist.add(String.valueOf(field.get("title")));
				hblindexlist.add(i+j);
				j++;
			}
		}
		
		
		ExportExcel<DeductAnalysisVO> ex = new ExportExcel<DeductAnalysisVO>();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String date = DateUtils.getDate(new Date());
			String fileName = null;
			String userAgent = getRequest().getHeader("user-agent");  
            if (!StringUtil.isEmpty(userAgent) && ( userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0   
                    || userAgent.indexOf("Safari") >= 0 ) ) {  
                fileName= new String(("加盟商数据分析").getBytes(), "ISO8859-1");  
            } else {  
                fileName=URLEncoder.encode("加盟商数据分析","UTF8"); //其他浏览器  
            }  
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName//new String("加盟商数据分析".getBytes("UTF-8"),"ISO8859-1")
					+ new String(date+".xls"));
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.expSjfxExcel("加盟商数据分析", exptitlist, expfieidlist, hbltitlist, hblindexlist, hbhtitlist,
					hbhindexs, expVOs, toClient, "",strslist,mnylist);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (IOException e) {
			log.error(e);
		} finally {
		    InOutUtil.close(toClient, "收款登记导出全部关闭输出流");
		    InOutUtil.close(servletOutputStream, "收款登记导出全部关闭输入流");
		}
	}
	
}
