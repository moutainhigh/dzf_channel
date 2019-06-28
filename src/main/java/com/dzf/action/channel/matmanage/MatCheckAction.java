package com.dzf.action.channel.matmanage;

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
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.matmanage.IMatCheckService;
import com.dzf.service.pub.IPubService;

@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "matcheck")
public class MatCheckAction extends BaseAction<MatOrderVO> {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IMatCheckService matcheck;

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
			List<MatOrderVO> mList = new ArrayList<MatOrderVO>();
			String stype = getRequest().getParameter("stype");

			MatOrderVO pamvo = new MatOrderVO();
			QryParamVO qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());

			int total = matcheck.queryTotalRow(qvo, pamvo);
			grid.setTotal((long) (total));
			if (total > 0) {
				mList = matcheck.query(qvo, pamvo, uservo);
			}

			grid.setRows(mList);
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			grid.setMsg("查询失败");
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询渠道商下拉
	 */
	public void queryComboBox() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			List<ChnAreaBVO> list = matcheck.queryComboBox(uservo);
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ChnAreaBVO>());
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
	 * 申请单审核确认
	 */
	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			String body = "";
			MatOrderBVO[] bodyVOs = null;
			MatOrderVO vo = new MatOrderVO();
			vo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), vo);

			Map<String, String> bmapping = FieldMapping.getFieldMapping(new MatOrderBVO());
			body = getRequest().getParameter("body"); // 物料数据
			if (!StringUtil.isEmpty(body)) {
				body = body.replace("}{", "},{");
				body = "[" + body + "]";
				JSONArray bodyarray = (JSONArray) JSON.parseArray(body);
				bodyVOs = DzfTypeUtils.cast(bodyarray, bmapping, MatOrderBVO[].class, JSONConvtoJAVA.getParserConfig());
			}

			if (vo != null) {
				MatOrderVO mvo = matcheck.queryById(vo.getPk_materielbill());
				if (!StringUtil.isEmpty(vo.getVreason())) {
					mvo.setVreason(vo.getVreason());
				}
				if (vo.getVstatus() != null) {
					mvo.setVstatus(vo.getVstatus());
				}
				matcheck.updateStatusById(mvo, uservo, bodyVOs);
			}
			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("操作失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}

	/**
	 * 查询申请人和审核人
	 */
	public void queryUserData() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			String mid = getRequest().getParameter("id");
			MatOrderVO vo = matcheck.queryUserData(uservo, mid);
			if (vo != null) {
				json.setRows(vo);
				json.setMsg("查询成功");
				json.setSuccess(true);
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);

	}

	/**
	 * 登录用户校验
	 * 
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误！");
		} else if (uservo == null) {
			throw new BusinessException("请先登录！");
		}
	}

}
