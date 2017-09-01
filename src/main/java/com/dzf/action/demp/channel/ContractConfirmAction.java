package com.dzf.action.demp.channel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.service.channel.IContractConfirm;

/**
 * 合同确认
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/contract")
@Action(value = "contractconf")
public class ContractConfirmAction extends BaseAction<ContractConfrimVO> {

	private static final long serialVersionUID = 8503727157432036048L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IContractConfirm contractconfser;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			int total = contractconfser.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<ContractConfrimVO> clist = contractconfser.query(paramvo);
				grid.setRows(clist);
			}else{
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
