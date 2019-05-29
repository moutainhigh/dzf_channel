package com.dzf.action.branch.setup;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.branch.setup.BranchInstSetupBVO;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QueryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
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
@Action(value = "setup")
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
		Json json = new Json();
		try {
			QueryParamVO param = new QueryParamVO();
			param = (QueryParamVO) DzfTypeUtils.cast(getRequest(), param);
			Map<String, List> map = branchser.query(param);
			if(map==null || map.size()==0){
				json.setRows(null);
				json.setMsg("查询数据为空");
			}else{
				if(map.size()>1){
					json.setRows(map.get("0"));//第一次加载
				}
				json.setMsg("查询成功");
				json.setData(map.get("1"));
			}
			json.setSuccess(true);
		}catch (Exception e) {
			json.setSuccess(false);
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	
	
	
	
	/**
	 * 新增机构
	 */
	public void saveInst() {
		Json json = new Json();
	    try{
	    	UserVO uservo = getLoginUserInfo();
	    	pubser.checkFunnode(uservo, IFunNode.CHANNEL_67);
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
	    	pubser.checkFunnode(uservo, IFunNode.CHANNEL_67);
	    	BranchInstSetupBVO vo = new BranchInstSetupBVO();
			vo = (BranchInstSetupBVO) DzfTypeUtils.cast(getRequest(), vo);
			if(StringUtil.isEmpty(vo.getPk_branchset())){
				throw new BusinessException("请设置机构");
			}
			if(StringUtil.isEmpty(vo.getPk_branchcorp())){
				vo.setPk_corp("000001");
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
			String name = getRequest().getParameter("name");
			Boolean b=branchser.queryCorpname(name);
			json.setSuccess(b);
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	/**
	 * 根据企业识别号查询公司信息
	 */
	public void queryCorpInfo() {
		Json json = new Json();
		try{
			String entnumber = getRequest().getParameter("name");
			CorpVO corp = branchser.queryCorpInfo(entnumber);
			json.setRows(corp);
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
	
	
	
}
