package com.dzf.service.sys.sys_set;

import com.dzf.model.pub.QueryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

/**
 * 公司目录的service类
 *
 */
public interface ICorpQueryService {
	
	
	public CorpVO[] queryCorpRef(QueryParamVO queryvo,UserVO uservo) throws DZFWarpException;
	/**
	 * 	查询公司数据总条数
	 * @param queryvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO[] queryCorpTotal(QueryParamVO queryvo,UserVO uservo) throws DZFWarpException;
	
	
}
