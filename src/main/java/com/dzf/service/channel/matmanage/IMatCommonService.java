package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IMatCommonService {
 
	/**
	 * 物料审核过滤 大区负责人只能看见自己负责的加盟商
	 * @param vcorp 
	 * @param vpro 
	 */
	public List<ChnAreaBVO> queryPro(UserVO uservo, String stype, String vpro, String vcorp) throws DZFWarpException;
	
}
