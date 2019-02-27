package com.dzf.action.channel.dealmanage;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.dealmanage.IStockSumService;

@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "stocksum")
public class StockSumAction extends BaseAction<StockOutInMVO>{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IStockSumService stocksum;

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
			StockOutInMVO qvo = new StockOutInMVO();
			qvo = (StockOutInMVO) DzfTypeUtils.cast(getRequest(), qvo);
			List<StockOutInMVO> clist = stocksum.query(qvo);
			grid.setTotal((long)(clist.size()));
			grid.setRows(clist);
			grid.setMsg("查询成功!");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
}
