package com.dzf.action.sys.sys_set;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QueryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.service.sys.sys_set.ICorpQueryService;

/**
 * 客户查询
 * @author 
 *
 */
@SuppressWarnings("rawtypes")
@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "sys_quchncorp")
public class CorpQueryAction extends BaseAction<CorpVO> {

	private static final long serialVersionUID = 4442536178302895685L;

	@Autowired
	private ICorpQueryService sys_qucorp;

	private Logger log = Logger.getLogger(this.getClass());
	

	/**
	 * 查询当前会计事务所下的所有企业客户，会计管理端业务使用
	 */
	public void queryKhRef() {
		Grid grid = new Grid();
		try {
			QueryParamVO paramvo = new QueryParamVO();
			paramvo = (QueryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if (paramvo != null && StringUtil.isEmpty(paramvo.getPk_corp())) {
				paramvo.setPk_corp(getLogincorppk());
			}
//			String isbseal = getRequest().getParameter("isbseal");//是否包含已停用(包含已停用的客户，默认不包含)
//			if(!StringUtil.isEmpty(isbseal)&&isbseal.equals("Y")){
//				paramvo.setIsbseal(new DZFBoolean(true));
//			}
			UserVO uservo = getLoginUserInfo();
			int page = paramvo == null ?1: paramvo.getPage();
			int rows = paramvo ==null? 100000: paramvo.getRows();
			CorpVO[] corpvos = sys_qucorp.queryCorpRef(paramvo, uservo);
			int len = corpvos==null?0:corpvos.length;
			if(len > 0){
				grid.setTotal((long)(len));
				//给服务机构字段赋值
				CorpVO accountVO = CorpCache.getInstance().get(null, corpvos[0].getFathercorp());
				if(accountVO != null){
					for(CorpVO corpvo : corpvos){
						corpvo.setDef1(accountVO.getUnitname());
					}
				}
				corpvos = getPagedVOs(corpvos,page,rows);
				grid.setRows(Arrays.asList(corpvos));
				grid.setSuccess(true);
				grid.setMsg("查询成功！");
			}else{
				grid.setTotal(Long.valueOf(0));
				grid.setSuccess(true);
				grid.setMsg("查询结果为空！");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 将查询后的结果分页
	 * @param cvos
	 * @param page
	 * @param rows
	 * @return
	 */
	private CorpVO[] getPagedVOs(CorpVO[] cvos,int page,int rows){
		int beginIndex = rows * (page-1);
		int endIndex = rows*page;
		if(endIndex>=cvos.length){//防止endIndex数组越界
			endIndex=cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
	}
	
}
