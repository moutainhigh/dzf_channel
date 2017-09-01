package com.dzf.model.pub;

import java.math.BigDecimal;

import com.dzf.pub.lang.DZFDouble;

/**
 * 
 * 
 *
 */
public class CommonUtil {


	/**
	 * 
	 * 功能描述:根据一个对象的值得到DZFDouble的值，如果为空，返回零 参数：Object value 对象值
	 * 
	 * @param value
	 * @return
	 */
	public static DZFDouble getDZFDouble(Object value) {
		if (value == null || value.toString().trim().equals("")) {
			return DZFDouble.ZERO_DBL;
		} else if (value instanceof DZFDouble) {
			return (DZFDouble) value;
		} else if (value instanceof BigDecimal) {
			return new DZFDouble((BigDecimal) value);
		} else {
			return new DZFDouble(value.toString().trim());
		}
	}
	
	/**
	 * 
	 * 功能描述:根据一个对象的值得到DZFDouble的值，如果为空，返回零 参数：Object value 对象值
	 * 
	 * @param value
	 * @return
	 */
	public static Integer getInteger(Object value) {
		if (value == null || value.toString().trim().equals("")) {
			return 0;
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else {
			return Integer.valueOf(value.toString());
		}
	}
}
