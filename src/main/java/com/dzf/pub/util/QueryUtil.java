package com.dzf.pub.util;

import java.util.Arrays;

import com.dzf.pub.DZFWarpException;
import com.dzf.pub.SuperVO;

public class QueryUtil {
	/**
	 * 将查询后的结果分页
	 * 
	 * @param cvos
	 * @param page
	 * @param rows
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static SuperVO[] getPagedVOs(SuperVO[] cvos, int page, int rows) throws DZFWarpException {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= cvos.length) {// 防止endIndex数组越界
			endIndex = cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
	}
	
	/**
	 * 代账机构过滤掉演示加盟商
	 * @author gejw
	 * @time 下午2:41:57
	 * @return
	 */
	public static String getWhereSql(){
	    return " nvl(account.dr,0) = 0 and nvl(account.channeltype,-1) != 9 and account.ischannel = 'Y' and account.isaccountcorp = 'Y' "; 
	}
}
