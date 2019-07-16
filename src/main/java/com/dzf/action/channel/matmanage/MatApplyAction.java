package com.dzf.action.channel.matmanage;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.matmanage.IMatApplyService;
import com.dzf.service.pub.IPubService;

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
			List<MatOrderVO> mList = new ArrayList<MatOrderVO>();
			//String stype = getRequest().getParameter("stype");

			MatOrderVO pamvo = new MatOrderVO();
			QryParamVO qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());

			int total = matapply.queryTotalRow(qvo, pamvo);
			grid.setTotal((long) (total));
			if (total > 0) {
				mList = matapply.query(qvo, pamvo, uservo);
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
	 * 查询登录人
	 */
	public void queryUserData() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO vo = matapply.queryUserData(uservo);
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
	 * 根据加盟商查询申请单信息
	 */
	public void showDataByCorp() {
		Json json = new Json();
		try {
			String corpid = getRequest().getParameter("fcorp");
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
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			String stype = getRequest().getParameter("stype");//提示后保存
			String kind = getRequest().getParameter("kind");//区分是否需要校验

			pubser.checkFunnode(uservo, IFunNode.CHANNEL_70);
			MatOrderVO vo = new MatOrderVO();
			vo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), vo);
			Map<String, String> bmapping = FieldMapping.getFieldMapping(new MatOrderBVO());
			String body = getRequest().getParameter("body"); // 物料数据
			String message = "";
			if (body != null) {
				body = body.replace("}{", "},{");
				body = "[" + body + "]";
				JSONArray bodyarray = (JSONArray) JSON.parseArray(body);
				MatOrderBVO[] bodyVOs = DzfTypeUtils.cast(bodyarray, bmapping, MatOrderBVO[].class,
						JSONConvtoJAVA.getParserConfig());

				if (bodyVOs == null || bodyVOs.length == 0) {
					throw new BusinessException("物料数据不能为空");
				}
				message = matapply.saveApply(vo, uservo, bodyVOs, stype, kind);
			} else {
				matapply.editSave(vo);
			}

			if (!StringUtil.isEmpty(message)) {// 需要提示信息
				json.setMsg("提示");
				json.setRows(message);
			} else {
				json.setMsg("保存成功");
				json.setSuccess(true);
			}
		} catch (Exception e) {
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
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO vo = new MatOrderVO();
			vo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), vo);
			String id = getRequest().getParameter("id");
			if (!StringUtil.isEmpty(id)) {
				vo = matapply.queryDataById(vo, id, uservo);
			}
			if (!StringUtil.isEmpty(vo.getMessage())) {
				json.setMsg("提示");
				json.setRows(vo);
			} else {
				json.setRows(vo);
				json.setMsg("查询成功");
				json.setSuccess(true);
			}
		} catch (Exception e) {
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
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO pamvo = new MatOrderVO();
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			matapply.delete(pamvo);
			json.setMsg("删除成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("删除失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);

	}

	/**
	 * 查询符合条件的加盟商
	 */
	public void queryChannel() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			List<CorpVO> list = matapply.queryChannel(uservo);
			if (list != null && list.size() > 0) {
				CorpVO[] corpvos = getPagedVOs(list.toArray(new CorpVO[0]), page, rows);
				grid.setRows(Arrays.asList(corpvos));
				grid.setTotal((long) (list.size()));
			} else {
				grid.setRows(list);
				grid.setTotal(0L);
			}
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	private CorpVO[] getPagedVOs(CorpVO[] cvos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= cvos.length) {// 防止endIndex数组越界
			endIndex = cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
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
