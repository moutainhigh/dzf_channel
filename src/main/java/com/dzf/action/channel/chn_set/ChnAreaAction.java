package com.dzf.action.channel.chn_set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.chn_set.IChnAreaService;

/**
 * 渠道区域划分
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/chn_set")
@Action(value = "chnarea")
public class ChnAreaAction extends BaseAction<ChnAreaVO> {

	@Autowired
	private IChnAreaService chnarea;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 保存主表数据
	 */
	public void save() {
		Json json = new Json();
		try {
			ChnAreaVO headvo = requestDealStep(getRequest());
			headvo=chnarea.save(headvo);
			json.setSuccess(true);
			json.setRows(headvo);
			json.setMsg("保存成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	
	private ChnAreaVO requestDealStep(HttpServletRequest request) {
		String head = request.getParameter("head");
		String body1 = request.getParameter("body1"); // 第一个子表
		String body1d = request.getParameter("body1d");
		body1 = body1.replace("}{", "},{");
		body1 = "[" + body1 + "]";
		body1d = body1d.replace("}{", "},{");
		body1d = "[" + body1d + "]";
		JSON headjs = (JSON) JSON.parse(head);
		JSONArray array1 = (JSONArray) JSON.parseArray(body1);
		JSONArray array1d = (JSONArray) JSON.parseArray(body1d);
		Map<String, String> headmaping = FieldMapping.getFieldMapping(new ChnAreaVO());
		Map<String, String> body1mapping = FieldMapping.getFieldMapping(new ChnAreaBVO());
		
		ChnAreaVO headvo = DzfTypeUtils.cast(headjs, headmaping, ChnAreaVO.class, JSONConvtoJAVA.getParserConfig());
		String pk_corp = headvo.getPk_corp();
		if (StringUtil.isEmpty(pk_corp)) {
			pk_corp = getLoginCorpInfo().getPk_corp();
			headvo.setPk_corp(pk_corp);
		}
		if(StringUtil.isEmpty(headvo.getPk_chnarea())){
			headvo.setDoperatedate(new DZFDate());
			headvo.setCoperatorid(getLoginUserInfo().getCuserid());
			headvo.setTs(new DZFDateTime());
		}
		ChnAreaBVO[] bodyvos1 = DzfTypeUtils.cast(array1, body1mapping, ChnAreaBVO[].class,
				JSONConvtoJAVA.getParserConfig());
		ChnAreaBVO[] bodyvos1d = DzfTypeUtils.cast(array1d, body1mapping, ChnAreaBVO[].class,
				JSONConvtoJAVA.getParserConfig());
		List<ChnAreaBVO> list1 = new ArrayList<ChnAreaBVO>();
		for (ChnAreaBVO dealstepB1VO : bodyvos1) {
			dealstepB1VO.setDr(0);
			dealstepB1VO.setPk_corp(pk_corp);
			list1.add(dealstepB1VO);
		}
		for (ChnAreaBVO dealstepB1VO : bodyvos1d) {
			dealstepB1VO.setDr(1);
			dealstepB1VO.setPk_corp(pk_corp);
			list1.add(dealstepB1VO);
		}
		headvo.setTableVO("cn_chnarea_b", list1.toArray(new ChnAreaBVO[0]));
		return headvo;
	}

	/**
	 * 删除主子表数据
	 */
	public void delete() {
		Json json = new Json();
		try {
			String pk_area = getRequest().getParameter("pk_area");//办理步骤ID
			if(StringUtil.isEmpty(pk_area)){
				throw new BusinessException("主键为空");
			}
			String pk_corp = getLoginCorpInfo().getPk_corp();
			chnarea.delete(pk_area,pk_corp);
			json.setSuccess(true);
			json.setRows(data);
			json.setMsg("删除成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
	}


	/**
	 * 查询主表数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			ChnAreaVO qvo = new ChnAreaVO();
			qvo = (ChnAreaVO) DzfTypeUtils.cast(getRequest(), qvo);
			String pk_corp = getLogincorppk();
			if (StringUtil.isEmpty(qvo.getPk_corp())) {
				qvo.setPk_corp(pk_corp);
			}
			ChnAreaVO[] vos = chnarea.query(qvo);
			if(vos==null||vos.length==0){
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(Arrays.asList(vos));
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 通过主表主键查询主子表数据
	 */
	public void queryByPrimaryKey(){
		Grid grid = new Grid();
		try {
			String pk_area = getRequest().getParameter("pk_area");//办理步骤ID
			if(StringUtil.isEmpty(pk_area)){
				throw new BusinessException("主键为空");
			}
			ChnAreaVO vo = chnarea.queryByPrimaryKey(pk_area,getLogincorppk());
			if (vo != null) {
				grid.setRows(Arrays.asList(vo));
			}
			grid.setSuccess(true);
			grid.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

}
