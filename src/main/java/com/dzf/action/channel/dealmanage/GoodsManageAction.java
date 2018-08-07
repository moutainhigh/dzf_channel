package com.dzf.action.channel.dealmanage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.dealmanage.IGoodsManageService;
import com.dzf.service.pub.IPubService;

/**
 * 商品管理
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "goodsmanage")
public class GoodsManageAction extends BaseAction<GoodsVO> {

	private static final long serialVersionUID = 3652362465205566316L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IGoodsManageService manser;
	
	@Autowired
	private IPubService pubser;
	
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
			GoodsVO paramvo = (GoodsVO) DzfTypeUtils.cast(getRequest(), new GoodsVO());
			int total = manser.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<GoodsVO> clist = manser.query(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<GoodsVO>());
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
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_41);
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			File[] files = ((MultiPartRequestWrapper) getRequest()).getFiles("imageFile");
			String[] filenames = ((MultiPartRequestWrapper) getRequest()).getFileNames("imageFile");
			if (files == null || files.length == 0) {
				throw new BusinessException("商品图片不能为空");
			}
			if (StringUtil.isEmpty(data.getPk_corp())) {
				data.setPk_corp(getLogincorppk());
			}
			setDefaultValue(data);
			GoodsVO returnvo = manser.save(data, files, filenames);
			json.setSuccess(true);
			json.setRows(returnvo);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 设置默认值
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(GoodsVO data) throws DZFWarpException {
		if(StringUtil.isEmpty(data.getPk_goods())){
			data.setCoperatorid(getLoginUserid());
			data.setDoperatedate(new DZFDate());
		}
	}

}
