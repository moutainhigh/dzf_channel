package com.dzf.action.channel.matmanage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.matmanage.IMatCheckService;
import com.dzf.service.pub.IPubService;

@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "matcheck")
public class MatCheckAction extends BaseAction<MatOrderVO> {

private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IMatCheckService matcheck;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询渠道商下拉
	 */
	public void queryComboBox() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			List<ChnAreaBVO> list = matcheck.queryComboBox(uservo);
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ChnAreaBVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	
	 /**
     * 申请单审核确认
     */
    public void save () {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO vo = new MatOrderVO();
			vo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), vo);
			if(vo!=null){
				 MatOrderVO mvo=matcheck.queryById(vo.getPk_materielbill());
				 if(!StringUtil.isEmpty(vo.getVreason())){
					 mvo.setVreason(vo.getVreason());
				 }
				 if(vo.getVstatus()!=null){
				     mvo.setVstatus(vo.getVstatus());
				 }
				 matcheck.updateStatusById(mvo,uservo);
			}
			json.setMsg("审核成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("审核失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "审核失败");
		}
		writeJson(json);
	}
    
    
	/**
	 * 登录用户校验
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
			throw new BusinessException("登陆用户错误！");
		}else if(uservo == null){
			throw new BusinessException("请先登录！");
		}
	}
	
	
	
}
