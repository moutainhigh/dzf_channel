package com.dzf.action.channel.matmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.matmanage.IMatApplyService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportExcel;
import com.sun.tools.javac.code.Type.ForAll;

import oracle.net.aso.i;

/**
 * 物料申请
 */
@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "matapply")
public class MatApplyAction extends BaseAction<MatOrderVO> {

    private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IMatApplyService matapply;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO pamvo = new MatOrderVO();
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());
			int total = matapply.queryTotalRow(pamvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<MatOrderVO> mList = matapply.query(pamvo,uservo);
				grid.setRows(mList);
				grid.setMsg("查询成功");
			}/*else{
				List<MaterielFileVO> mvos=matapply.queryMatFile();
				for (MaterielFileVO mvo : mvos) {
					mvo.setApplynum(0);
					mvo.setOutnum(0);
				}
				grid.setRows(mvos);
				grid.setSuccess(true);
			}*/
			grid.setSuccess(true);
		} catch (Exception e) {
			grid.setMsg("查询失败");
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询物料信息
	 */
	public void queryNumber() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO pamvo = new MatOrderVO();
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			List<MatOrderBVO> bvos=matapply.queryNumber(pamvo);
			if(bvos==null || bvos.size()==0){//还没有申请，查询所有启用的物料
				List<MaterielFileVO> mvos=matapply.queryMatFile();
				for (MaterielFileVO mvo : mvos) {
					mvo.setApplynum(0);
					mvo.setOutnum(0);
				}
				json.setRows(mvos);
			}else{
				json.setRows(bvos);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
		}catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	/**
	 * 查询所有的省份
	 */
	public void queryAllProvince() {
		Grid grid = new Grid();
		try {
			List<MatOrderVO> list = matapply.queryAllProvince();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<MatOrderVO>());
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
	 * 根据省份查询市
	 */
	public void queryCityByProId() {
		Json json = new Json();
		try {
			String pid =getRequest().getParameter("provinceid");
			if (!StringUtil.isEmpty(pid)) {
				List<MatOrderVO> list = matapply.queryCityByProId(Integer.parseInt(pid));
				if (list == null || list.size() == 0) {
					json.setRows(new ArrayList<MatOrderVO>());
					json.setSuccess(true);
					json.setMsg("查询数据为空");
				} else {
					json.setRows(list);
					json.setSuccess(true);
					json.setMsg("查询成功");
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
		
	}
	
	
	/**
	 * 根据市查询区县
	 */
	public void queryAreaByCid() {
		Json json = new Json();
		try {
			String cid =getRequest().getParameter("cityid");
			if (!StringUtil.isEmpty(cid)) {
				List<MatOrderVO> list = matapply.queryAreaByCid(Integer.parseInt(cid));
				if (list == null || list.size() == 0) {
					json.setRows(new ArrayList<MatOrderVO>());
					json.setSuccess(true);
					json.setMsg("查询数据为空");
				} else {
					json.setRows(list);
					json.setSuccess(true);
					json.setMsg("查询成功");
				}
			}
			
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
		
	}
	
	/**
	 * 根据加盟商查询申请单信息
	 */
	public void showDataByCorp() {
		Json json = new Json();
		try {
			String corpid =getRequest().getParameter("fcorp");
			if (!StringUtil.isEmpty(corpid)) {
				MatOrderVO mvo = matapply.showDataByCorp(corpid);
				if (mvo == null) {
					json.setSuccess(true);
					json.setMsg("查询数据为空");
				} else {
					json.setRows(mvo);
					json.setSuccess(true);
					json.setMsg("查询成功");
				}
			}
			
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
		
	}
	
	/**
	 * 新增物料申请单
	 */
	public void save() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			String type = getRequest().getParameter("type");
			String stype = getRequest().getParameter("stype");
			if("1".equals(type)){
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_68);
			}else{
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_70);
			}
			MatOrderVO vo = new MatOrderVO();
			vo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), vo);
			
			Map<String, String> bmapping = FieldMapping.getFieldMapping(new MatOrderBVO());
			String body = getRequest().getParameter("body"); // 物料数据
			body = body.replace("}{", "},{");
			body = "[" + body + "]";
			JSONArray bodyarray = (JSONArray) JSON.parseArray(body);
			MatOrderBVO[] bodyVOs = DzfTypeUtils.cast(bodyarray, bmapping, MatOrderBVO[].class,
					JSONConvtoJAVA.getParserConfig());
			
			if (bodyVOs == null || bodyVOs.length == 0) {
				throw new BusinessException("物料数据不能为空");
			}
			String message = matapply.saveApply(vo,uservo,bodyVOs,type,stype);
			
			if(!StringUtil.isEmpty(message)){//需要提示信息
				json.setMsg("提示");
				json.setRows(message);
			}else{
				json.setMsg("保存成功");
				json.setSuccess(true);
			}
		}catch (Exception e) {
			json.setMsg("保存失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 编辑回显
	 */
	public void queryById() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO vo = new MatOrderVO();
			String id = getRequest().getParameter("id");
			String type = getRequest().getParameter("type");
			String stype = getRequest().getParameter("stype");
			if(!StringUtil.isEmpty(id)){
			    vo=matapply.queryDataById(id,uservo,type,stype);
			}
			if(!StringUtil.isEmpty(vo.getMessage())){
				json.setMsg("提示");
				json.setRows(vo.getMessage());
			}else{
				json.setRows(vo);
				json.setMsg("查询成功");
				json.setSuccess(true);
			}
			
		}catch (Exception e) {
			json.setMsg("查询失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
		
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO pamvo = new MatOrderVO();
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			matapply.delete(pamvo);
			json.setMsg("删除成功");
			json.setSuccess(true);
		}catch (Exception e) {
			json.setMsg("删除失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
		
	}
	
	/**
	 * 物料申请表导出
	 */
	public void exportAuditExcel() {
		// 获取需要导出数据
		String strlist = getRequest().getParameter("strlist");
		if (StringUtil.isEmpty(strlist)) {
			return;
		}
		JSONArray array = (JSONArray) JSON.parseArray(strlist);
		
		String hblcols = getRequest().getParameter("hblcols");
		JSONArray hblcolsarray = (JSONArray) JSON.parseArray(hblcols);//title+field
		/*JSONArray hblcolsarray = new JSONArray();
		for (int i=5;i<hblarray.size()-8;i++) {
			hblcolsarray.add(hblarray.get(i));
		}*/
		
		
		String cols = getRequest().getParameter("cols");
		JSONArray colsarray = (JSONArray) JSON.parseArray(cols);//字段编码
		
		//1、导出字段名称
		List<String> exptitlist = new ArrayList<String>();//大区——申请，实发...发货时间
		exptitlist.add("大区");
		exptitlist.add("渠道经理");
		exptitlist.add("省/市");
		exptitlist.add("加盟商");
		exptitlist.add("合同编号");
		
		//2、导出字段编码
		List<String> expfieidlist = new ArrayList<String>();//aname...applynum1...——>adate
		//3、title名称
		List<String> hbltitlist = new ArrayList<String>();//[档案袋/个, 发票盒/盒, 易拉宝/个, 合同档案/份, 收货信息, 快递信息]
		//4、title字段下标
		List<Integer> hblindexlist = new ArrayList<Integer>();
		//5、除了物料信息之外的字段名称
		List<String> hbhtitlist = new ArrayList<String>();
		List<String> hbhtitlist2 = new ArrayList<String>();
		hbhtitlist.add("大区");
		hbhtitlist.add("渠道经理");
		hbhtitlist.add("省/市");
		hbhtitlist.add("加盟商");
		hbhtitlist.add("合同编号");
		
		hbhtitlist2.add("备注");
		hbhtitlist2.add("状态");
		hbhtitlist2.add("驳回原因");	
		hbhtitlist2.add("录入时间");
		hbhtitlist2.add("申请人");
		hbhtitlist2.add("申请时间");
		Integer[] hbhindexs = new Integer[]{0,1,2,3,4};
		Integer[] hbhindexs2 = new Integer[]{13,14,15,16,17};
		//7、字符集合
		List<String> strslist = new ArrayList<String>();
		strslist.add("aname");
		strslist.add("uname");
		strslist.add("pname");
		strslist.add("corpname");
		strslist.add("code");
		//8、物料名称+单位集合
		List<String> nalist = new ArrayList<String>();//wlname...applynum1...adate
		nalist.add("wlname");
		nalist.add("unit");
		
		//9、合并列集合
		List<String> tlist = new ArrayList<String>();
		tlist.add("");
		
		Map<String, String> field = null;
		
		for(int i = 7; i < hblcolsarray.size()-6; i++){
			field = (Map<String, String>) hblcolsarray.get(i);
			hbltitlist.add(String.valueOf(field.get("title")));
			
            if(String.valueOf(field.get("title")).contains("/") 
            		&& !"省/市".equals(String.valueOf(field.get("title")))){
            	//strslist.add(String.valueOf(colsarray.get(i)));
				exptitlist.add("申请");
				//nalist.add(String.valueOf(colsarray.get(i)));
				exptitlist.add("实发");
            }
           
		}
		int n = 5;
		for(int j=0;j<hbltitlist.size();j++){
			if(j>=0 && j<= hbltitlist.size()-2){
				hblindexlist.add(n);
			}
			else if(j==hbltitlist.size()-1){
				hblindexlist.add(n+1);
			}
			n=n+2;
		}
		
		exptitlist.add("收货人");
		exptitlist.add("联系电话");
		exptitlist.add("地址");
		exptitlist.add("快递公司");
		exptitlist.add("金额");
		exptitlist.add("单号");
		exptitlist.add("发货时间");
		
		
		
		for(int i = 2; i < colsarray.size(); i++){
			expfieidlist.add(String.valueOf(colsarray.get(i)));
			//strslist.add(String.valueOf(colsarray.get(i)));
			//nalist.add(String.valueOf(colsarray.get(i)));
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
                fileName= new String(("物料申请表").getBytes(), "ISO8859-1");  
            } else {  
                fileName=URLEncoder.encode("物料申请表","UTF8"); //其他浏览器  
            }  
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName
					+ new String(date+".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.expMatApply("物料申请表", exptitlist,expfieidlist, hbltitlist, hblindexlist, hbhtitlist,hbhtitlist2,
					hbhindexs, hbhindexs2,array, toClient, "",strslist,nalist);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (IOException e) {
			log.error(e);
		} finally {
			if (toClient != null) {
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
			if (servletOutputStream != null) {
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	
	
	/**
	 * 登录用户校验
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
			throw new BusinessException("登陆用户错误！");
		}else if(uservo == null){
			throw new BusinessException("请先登录！");
		}
	}

}
