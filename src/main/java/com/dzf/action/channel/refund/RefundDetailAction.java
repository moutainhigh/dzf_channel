package com.dzf.action.channel.refund;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.dzf.action.channel.expfield.RefundDetailExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.refund.RefundDetailVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.InOutUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.refund.IRefundDetailService;
import com.dzf.service.pub.IPubService;

/**
 * 退款明细查询
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/refund")
@Action(value = "refunddetail")
public class RefundDetailAction extends BaseAction<RefundDetailVO> {

	private static final long serialVersionUID = 1711382364069761257L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IPubService pubser;
	
	@Autowired
	private IRefundDetailService refdetailser;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_49);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
		    QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if(pamvo == null){
				pamvo = new QryParamVO();
			}
			pamvo.setCuserid(getLoginUserid());
			List<RefundDetailVO> list = refdetailser.query(pamvo);
			int page = pamvo.getPage();
			int rows = pamvo.getRows();
			int len = list == null ? 0 : list.size();
			if (len > 0) {
				grid.setTotal((long) (len));
				grid.setRows(Arrays.asList(QueryUtil.getPagedVOs(list.toArray(new RefundDetailVO[0]), page, rows)));
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			} else {
				grid.setTotal(Long.valueOf(0));
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询结果为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询明细
	 */
	public void queryDetail() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_49);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
		    QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if(pamvo == null){
				pamvo = new QryParamVO();
			}
			pamvo.setCuserid(getLoginUserid());
			List<RefundDetailVO> clist = refdetailser.queryDetail(pamvo);
			grid.setRows(clist);
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
	public void onExport(){
        String strlist =getRequest().getParameter("strlist");
        String qj = getRequest().getParameter("qj");
        if(StringUtil.isEmpty(strlist)){
            throw new BusinessException("导出数据不能为空!");
        }   
        JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
        Map<String, String> mapping = FieldMapping.getFieldMapping(new RefundDetailVO());
        RefundDetailVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,RefundDetailVO[].class, JSONConvtoJAVA.getParserConfig());
        ArrayList<RefundDetailVO> explist = new ArrayList<RefundDetailVO>();
        for(RefundDetailVO vo : expVOs){
            explist.add(vo);
        }
        HttpServletResponse response = getResponse();
        Excelexport2003<RefundDetailVO> ex = new Excelexport2003<RefundDetailVO>();
        RefundDetailExcelField fields = new RefundDetailExcelField();
        fields.setVos(explist.toArray(new RefundDetailVO[0]));;
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
            InOutUtils.close(toClient, "退款明细查询关闭输出流");
            InOutUtils.close(servletOutputStream, "退款明细查询关闭输入流");
        }
	}
}
