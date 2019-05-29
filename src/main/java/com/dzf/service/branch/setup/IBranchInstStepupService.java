package com.dzf.service.branch.setup;

import java.util.List;
import java.util.Map;

import com.dzf.model.branch.setup.BranchInstSetupBVO;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.pub.QueryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;

public interface IBranchInstStepupService {

	/**
	 * 新增机构设置
	 * @param data
	 */
	void saveInst(BranchInstSetupVO data);

	/**
	 * 新增机构下的公司
	 * @param vo
	 */
	void saveCorp(BranchInstSetupBVO vo);

	/**
	 * 校验公司名称唯一性
	 * @param name
	 * @return
	 */
	Boolean queryCorpname(String name);

	/**
	 * 根据企业识别号查询公司信息
	 * @param entnumber
	 * @return
	 */
	CorpVO queryCorpInfo(String entnumber);

	/**
	 * 批量更换公司所属机构
	 * @param vo
	 */
	void updateInst(BranchInstSetupBVO vo);

	/**
	 * 封存、启用
	 * @param vo
	 */
	void updateStatus(BranchInstSetupBVO vo);

	/**
	 * 删除公司
	 * @param vo
	 */
	void deleteCorpById(BranchInstSetupBVO vo);

	/**
	 * 列表查询
	 * @param param 
	 * @return
	 */
	Map<String, List> query(QueryParamVO param);

	/**
	 * 根据id查询机构
	 * @param id
	 * @param type 
	 * @return
	 */
	Object queryById(String id, String type);

}
