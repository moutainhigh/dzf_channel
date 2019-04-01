package com.dzf.action.channel.dealmanage;

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
import com.alibaba.fastjson.JSONObject;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.GoodsPackageVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IGoodsPackageService;
import com.dzf.service.pub.IPubService;

/**
 * 商品套餐
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "goodspackage")
public class GoodsPackageAction extends BaseAction<GoodsPackageVO> {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IPubService pubser;

	@Autowired
	private IGoodsPackageService packageser;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);

			GoodsPackageVO pamvo = (GoodsPackageVO) DzfTypeUtils.cast(getRequest(), new GoodsPackageVO());
			List<GoodsPackageVO> list = packageser.query(pamvo);
			if(list != null && list.size() > 0){
				grid.setRows(list);
			}else{
				grid.setRows(new ArrayList<GoodsPackageVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);

			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new GoodsPackageVO());
			JSONObject jsonObj = (JSONObject) JSON.parse(getRequest().getParameter("submitData"));

			GoodsPackageVO[] addData = DzfTypeUtils.cast(jsonObj.get("newRows"), bodymapping, GoodsPackageVO[].class,
					JSONConvtoJAVA.getParserConfig());
			GoodsPackageVO[] delData = DzfTypeUtils.cast(jsonObj.get("deleteRows"), bodymapping, GoodsPackageVO[].class,
					JSONConvtoJAVA.getParserConfig());
			GoodsPackageVO[] updData = DzfTypeUtils.cast(jsonObj.get("updateRows"), bodymapping, GoodsPackageVO[].class,
					JSONConvtoJAVA.getParserConfig());
			setDefaultValue(addData);
			packageser.save(getLogincorppk(), addData, delData, updData);
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}

	/**
	 * 设置默认值
	 * 
	 * @param addData
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(GoodsPackageVO[] addData) throws DZFWarpException {
		if (addData != null && addData.length > 0) {
			for (GoodsPackageVO vo : addData) {
				vo.setPk_corp(getLogincorppk());
				vo.setDr(0);
				vo.setCoperatorid(getLogin_userid());
				vo.setDoperatedate(new DZFDate());
				vo.setVstatus(1);
			}
		}
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);
			packageser.delete(data);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
	}
	
	/**
	 * 更新操作数据（发布、下架）
	 */
	public void operateData() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);
			GoodsPackageVO[] datas = getOperData();
			String opertype = getRequest().getParameter("opertype");
			
			StringBuffer retmsg = new StringBuffer();
			StringBuffer errmsg = new StringBuffer();
			int rightnum = 0;
			int errornum = 0;
			for(GoodsPackageVO vo : datas){
				try {
					packageser.updateData(vo, opertype);
					rightnum ++;
				} catch (Exception e) {
					errornum ++;
					errmsg.append(e.getMessage()).append("<br>");
				}
			}
			retmsg.append("成功").append(rightnum).append("条");
			if(errornum > 0){
				retmsg.append("，失败").append(errornum).append("条");
				retmsg.append("，失败原因：").append(errmsg);
			}
			json.setSuccess(true);
			json.setMsg(retmsg.toString());
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
	}
	
	/**
	 * 获取操作数据
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsPackageVO[] getOperData() throws DZFWarpException {
		String datas = getRequest().getParameter("datas");
		if (StringUtil.isEmpty(datas)) {
			throw new BusinessException("操作数据不为空");
		}
		datas = datas.replace("}{", "},{");
		datas = "[" + datas + "]";
		JSONArray jsonArray = (JSONArray) JSON.parseArray(datas);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new GoodsPackageVO());
		return DzfTypeUtils.cast(jsonArray, mapping, GoodsPackageVO[].class,
				JSONConvtoJAVA.getParserConfig());
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
