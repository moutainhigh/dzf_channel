package com.dzf.service.channel.branch;

import com.dzf.model.channel.branch.BranchInstSetupBVO;
import com.dzf.model.channel.branch.BranchInstSetupVO;
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

}
