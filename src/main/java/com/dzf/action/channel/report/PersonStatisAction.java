package com.dzf.action.channel.report;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.PersonStatisVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.report.IPersonStatis;

/**
 * 加盟商人员统计
 * 
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "personStatis")
public class PersonStatisAction extends BaseAction<PersonStatisVO> {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IPersonStatis personStatis;

	/**
	 * 查询
	 */
	public void query(){
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO)DzfTypeUtils.cast(getRequest(), paramvo);
			paramvo.setUser_name(getLoginUserid());
			if(StringUtil.isEmpty(paramvo.getPk_corp())){
				paramvo.setPk_corp(getLogincorppk());
			}
			paramvo.setBegdate(new DZFDate());
			List<PersonStatisVO> list = personStatis.query(paramvo);
			grid.setTotal(Long.valueOf(0));
			grid.setRows(list);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

}
