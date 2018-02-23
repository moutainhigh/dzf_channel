package com.dzf.action.channel.rebate;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.rebate.ManagerRefVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.rebate.IRebateInptService;

/**
 * 返点单录入
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/rebate")
@Action(value = "rebateinpt")
public class RebateInptAction extends BaseAction<RebateVO> {

	private static final long serialVersionUID = 9155544110565105231L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IRebateInptService rebateser;

	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				data.setFathercorp(getLogincorppk());
				data = rebateser.save(data, getLogincorppk());
				setDefaultValue(data);
				json.setRows(data);
				json.setSuccess(true);	
				json.setMsg("保存成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "保存失败");
			}
		}else {
			json.setSuccess(false);
			json.setMsg("保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 保存前设置默认值
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(RebateVO data) throws DZFWarpException{
		data.setVapproveid(getLoginUserid());
		data.setDoperatedate(new DZFDate());
		data.setTstamp(new DZFDateTime());
		data.setTs(new DZFDateTime());
		data.setDr(0);
		data.setIstatus(IStatusConstant.irebatestatus_0);//待提交
	}
	
	/**
	 * 渠道经理参照查询
	 */
	public void queryManager(){
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO)DzfTypeUtils.cast(getRequest(), paramvo);
			int page = paramvo == null ? 1: paramvo.getPage();
            int rows = paramvo == null ? 100000: paramvo.getRows();
			List<ManagerRefVO> list = rebateser.queryManagerRef(paramvo);
			if(list != null && list.size() > 0){
				ManagerRefVO[] refVOs = (ManagerRefVO[]) QueryUtil.getPagedVOs(list.toArray(new ManagerRefVO[0]), page, rows);
				grid.setRows(Arrays.asList(refVOs));
				grid.setTotal((long)(list.size()));
			}else{
			    grid.setRows(list);
			    grid.setTotal(0L);
			}
            grid.setMsg("查询成功");
            grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
}
