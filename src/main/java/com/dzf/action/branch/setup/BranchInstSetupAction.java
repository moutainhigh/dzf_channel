package com.dzf.action.branch.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.branch.setup.BranchInstSetupBVO;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QueryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.branch.setup.IBranchInstStepupService;
import com.dzf.service.pub.IPubService;

/**
 * 机构设置
 */
@ParentPackage("basePackage")
@Namespace("/branch")
@Action(value = "instSetupAct")
public class BranchInstSetupAction extends BaseAction<BranchInstSetupVO>{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IPubService pubser;
	
	@Autowired
	private IBranchInstStepupService branchser;
	
	/**
	 * 查询列表
	 */
	public void query(){
		/*Json json = new Json();
		try {
			QueryParamVO param = new QueryParamVO();
			param = (QueryParamVO) DzfTypeUtils.cast(getRequest(), param);
			Map<String, List> map = branchser.query(param);
			if(map==null || map.size()==0){
				json.setRows(null);
				json.setData(new ArrayList<BranchInstSetupBVO>());
				json.setMsg("查询数据为空");
			}else{
				if(map.size()>=1){
					json.setRows(map.get("0"));//第一次加载
				}
				json.setMsg("查询成功");
			    if(map.get("1")!=null && map.get("1").size()>0){
			    	int page = param.getPage();
					int rows = 1000;
					BranchInstSetupBVO[] bvolist = getPagedVOs((BranchInstSetupBVO[]) map.get("1").toArray(new BranchInstSetupBVO[0]), page, rows);
					json.setTotal((long) (map.get("1").size()));
			    	json.setData(Arrays.asList(bvolist));
			    }
			}
			json.setSuccess(true);
		}catch (Exception e) {
			json.setSuccess(false);
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);*/
		
		Grid grid = new Grid();
		try{
			QueryParamVO param = new QueryParamVO();
			param = (QueryParamVO) DzfTypeUtils.cast(getRequest(), param);
			List<BranchInstSetupVO> vosList = branchser.queryList(param);
			int page = param.getPage();
			int rows = param.getRows();
			if(vosList!=null && vosList.size()>0){
				if(vosList.get(0).getChildren()!=null){
					BranchInstSetupBVO[] bvolist = getPagedVOs((BranchInstSetupBVO[]) vosList.get(0).getChildren(), page, rows);
					grid.setRows(Arrays.asList(bvolist));
					grid.setTotal((long) vosList.get(0).getChildren().length);
				}else{
					grid.setRows(vosList);
				}
			}
            grid.setSuccess(true);
            grid.setMsg("查询成功");
		}catch (Exception e) {
			grid.setSuccess(false);
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
		
	}
	
	
	private BranchInstSetupBVO[] getPagedVOs(BranchInstSetupBVO[] cvos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= cvos.length) {// 防止endIndex数组越界
			endIndex = cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
	}



	/**
	 * 新增机构
	 */
	public void saveInst() {
		Json json = new Json();
	    try{
	    	UserVO uservo = getLoginUserInfo();
	    	pubser.checkFunnode(uservo, IFunNode.BRANCH_02);
	    	if(data == null){
	    		throw new BusinessException("数据信息不能为空");
	    	}
	    	if(StringUtil.isEmpty(data.getVname())){
	    		throw new BusinessException("机构名称不能为空");
	    	}
	    	if(StringUtil.isEmpty(data.getPk_branchset())){
	    		data.setPk_corp("000001");
	    		data.setCoperatorid(uservo.getCuserid());
	    		data.setDoperatedate(new DZFDate());
	    	}
	    	branchser.saveInst(data);
	    	json.setMsg("保存成功");
			json.setSuccess(true);
	    }catch (Exception e) {
			json.setSuccess(false);
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	
	/**
	 * 新增公司
	 */
	public void saveCorp() {
		Json json = new Json();
		try{
			UserVO uservo = getLoginUserInfo();
	    	pubser.checkFunnode(uservo, IFunNode.BRANCH_02);
	    	BranchInstSetupBVO vo = new BranchInstSetupBVO();
			vo = (BranchInstSetupBVO) DzfTypeUtils.cast(getRequest(), vo);
			if(StringUtil.isEmpty(vo.getPk_branchset())){
				throw new BusinessException("请设置机构");
			}
			if(StringUtil.isEmpty(vo.getPk_branchcorp())){
				vo.setCoperatorid(uservo.getCuserid());
				vo.setDoperatedate(new DZFDate());
				vo.setIsseal("N");
	    	}
	        branchser.saveCorp(vo);
	        json.setMsg("保存成功");
			json.setSuccess(true);
		}catch (Exception e) {
			json.setSuccess(false);
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	
	/**
	 * 校验公司名称唯一性
	 */
	public void queryCorpname(){
		Json json = new Json();
		try{
			String name = getRequest().getParameter("uname");
			Boolean b=branchser.queryCorpname(name);
			json.setSuccess(b);
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json.isSuccess());
	}
	
	/**
	 * 根据企业识别号查询公司信息
	 */
	public void queryCorpInfo() {
		Json json = new Json();
		try{
			String entnumber = getRequest().getParameter("name");
			BranchInstSetupBVO bvo = branchser.queryCorpInfo(entnumber);
			json.setRows(bvo);
			json.setMsg("查询成功");
			json.setSuccess(true);
		}catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	/**
	 * 批量更换所属机构
	 */
	public void editInst() {
		Json json = new Json();
		try{
			BranchInstSetupBVO vo = new BranchInstSetupBVO();
			vo = (BranchInstSetupBVO) DzfTypeUtils.cast(getRequest(), vo);
			String corpids = getRequest().getParameter("ids");
			if(corpids!=null){
				String[] split = corpids.split(",");
				for (String id : split) {
					vo.setPk_branchcorp(id);
					branchser.updateInst(vo);
				}
			}
			json.setMsg("更换成功");
			json.setSuccess(true);
		}catch (Exception e) {
			printErrorLog(json, log, e, "更换失败");
		}
		writeJson(json);
	}
	
	/**
	 * 封存、启用
	 */
	public void editSseal() {
		Json json = new Json();
		try{
			BranchInstSetupBVO vo = new BranchInstSetupBVO();
			vo = (BranchInstSetupBVO) DzfTypeUtils.cast(getRequest(), vo);
			branchser.updateStatus(vo);
			json.setMsg("操作成功");
			json.setSuccess(true);
		}catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
		
	}
	
	
	/**
	 * 删除公司
	 */
	public void deleteCorpById() {
		Json json = new Json();
		try{
			BranchInstSetupBVO vo = new BranchInstSetupBVO();
			vo = (BranchInstSetupBVO) DzfTypeUtils.cast(getRequest(), vo);
			branchser.deleteCorpById(vo);
			json.setMsg("删除成功");
			json.setSuccess(true);
		}catch (Exception e) {
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
		
	}
	
	/**
	 * 根据id查询机构
	 */
	public void queryById(){
		Json json = new Json();
		try{
			String id = getRequest().getParameter("id");
			String type = getRequest().getParameter("type");
			BranchInstSetupVO vo = new BranchInstSetupVO();
			BranchInstSetupBVO bvo = new BranchInstSetupBVO();
			if(type!=null && "0".equals(type)){
				vo = (BranchInstSetupVO) branchser.queryById(id,type);
				json.setRows(vo);
			}else{
				bvo = (BranchInstSetupBVO) branchser.queryById(id,type);
				json.setRows(bvo);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
		}catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	public void qryBranchs(){
        Grid grid = new Grid();
        try {
            List<ComboBoxVO> list = branchser.qryBranchs(getLogin_userid());
            if(list==null || list.size()<1){
            	list = new ArrayList<>();
            }
            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            printErrorLog(grid, log, e, "查询失败");
        }
        writeJson(grid);
	}
	
}
