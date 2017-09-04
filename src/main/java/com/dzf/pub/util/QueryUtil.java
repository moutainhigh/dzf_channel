package com.dzf.pub.util;

import java.util.Arrays;

import com.dzf.pub.DZFWarpException;
import com.dzf.pub.SuperVO;

public class QueryUtil {
	/**
	 * 将查询后的结果分页
	 * @param cvos
	 * @param page
	 * @param rows
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static SuperVO[] getPagedVOs(SuperVO[] cvos,int page,int rows) throws DZFWarpException{
		int beginIndex = rows * (page-1);
		int endIndex = rows*page;
		if(endIndex>=cvos.length){//防止endIndex数组越界
			endIndex=cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
	}
}
