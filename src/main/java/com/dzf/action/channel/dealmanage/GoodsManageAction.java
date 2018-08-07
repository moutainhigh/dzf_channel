package com.dzf.action.channel.dealmanage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.dealmanage.IGoodsManageService;

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
	
	/**
	 * 查询方法
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

}
