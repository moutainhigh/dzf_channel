package com.dzf.action.branch.reportmanage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.branch.reportmanage.CorpDataVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.service.branch.reportmanage.ISaleCorpDataService;

/**
 * 销售客户明细
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/branch")
@Action(value = "salecorpdataact")
public class SaleCorpDataAction extends BaseAction<CorpDataVO> {
	
	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ISaleCorpDataService salecorpser;
	
	/**
	 * 查询
	 */
	public void query(){
		Grid grid = new Grid();
		try {
			checkUser();
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if(pamvo == null){
				pamvo = new QryParamVO();
			}
			if(StringUtil.isEmpty(pamvo.getCuserid())){
				pamvo.setCuserid(getLoginUserid());
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null){
				pamvo.setUser_name(uservo.getUser_name());
			}
			if(StringUtil.isEmpty(pamvo.getCorpkname())){
				long total = salecorpser.queryTotal(pamvo);
				if(total > 0){
					List<CorpDataVO> list = salecorpser.query(pamvo);
					grid.setRows(list);
				}else{
					grid.setRows(new ArrayList<CorpDataVO>());
				}
				grid.setTotal(total);
			}else{
//				List<CorpDataVO> list = corpser.queryAll(pamvo);
//				if(list != null && list.size() > 0){
//					grid.setRows(list);
//					grid.setTotal((long) list.size());
//				}else{
//					grid.setRows(new ArrayList<CorpDataVO>());
//					grid.setTotal((long) 0);
//				}
			}
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询会计公司下拉
	 */
	public void queryAccount() {
		Grid grid = new Grid();
		try {
			checkUser();
			List<ComboBoxVO> list = salecorpser.queryAccount(getLoginUserid());
			grid.setRows(list);
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 登录用户验证
	 * 
	 * @throws Exception
	 */
	private void checkUser() throws Exception {
		UserVO uservo = getLoginUserInfo();
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误");
		} else if (uservo == null) {
			throw new BusinessException("登陆用户错误");
		}
	}
}
