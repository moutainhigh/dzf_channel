package com.dzf.action.channel.dealmanage;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.stock.GoodsTypeVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.constant.IFunNode;
import com.dzf.service.channel.dealmanage.IGoodsTypeService;
import com.dzf.service.pub.IPubService;

/**
 * 商品分类
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "goodstype")
public class GoodsTypeAction extends BaseAction<GoodsTypeVO> {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IGoodsTypeService  typeService;
	
	@Autowired
	private IPubService pubService;
	
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
			List<GoodsTypeVO> clist = typeService.query();
			grid.setRows(clist);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	
	/**
	 * 保存计量单位
	 */
	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_51);
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			data.setPk_corp(getLogincorppk());
			data.setCoperatorid(getLoginUserid());
			typeService.save(data);
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	
	/**
	 * 删除商品
	 */
	public void delete() {
		Json json = new Json();
		json.setSuccess(false);
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_51);
			GoodsTypeVO pamvo = new GoodsTypeVO();
			pamvo = (GoodsTypeVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());
			typeService.delete(pamvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	
}
