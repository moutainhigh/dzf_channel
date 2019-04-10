package com.dzf.action.channel.contract;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.contract.ChangeApplyVO;
import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.contract.IContractAuditService;

/**
 * 渠道合同审核
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/contract")
@Action(value = "contractaudit")
public class ContractAuditAction extends BaseAction<ChangeApplyVO> {

	private static final long serialVersionUID = -2630663628940589385L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IContractAuditService auditser;

	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			ChangeApplyVO pamvo = new ChangeApplyVO();
			pamvo = (ChangeApplyVO) DzfTypeUtils.cast(getRequest(), new ChangeApplyVO());
			UserVO uservo = getLoginUserInfo();
			int total = auditser.queryTotalNum(pamvo, uservo);
			grid.setTotal((long) (total));
			if (total > 0) {
				List<ChangeApplyVO> list = auditser.query(pamvo, uservo);
				grid.setRows(list);
			} else {
				grid.setRows(new ArrayList<ContractConfrimVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

}
