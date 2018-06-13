package com.dzf.action.channel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.IChnPayAuditService;

public class ChnPayAuditAction extends BaseAction<ChnPayBillVO> {

	private static final long serialVersionUID = -1475772734628751196L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChnPayAuditService payauditser;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			paramvo.setCuserid(getLoginUserid());
			int total = payauditser.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<ChnPayBillVO> clist = payauditser.query(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<ChnPayBillVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

}
