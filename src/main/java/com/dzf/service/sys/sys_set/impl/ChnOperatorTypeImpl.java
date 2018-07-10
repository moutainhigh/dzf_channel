package com.dzf.service.sys.sys_set.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.service.gl.gl_bdset.IOperatorType;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.spring.SpringUtils;

@Service("sys_ope_type")
public class ChnOperatorTypeImpl implements IOperatorType {

	@Override
	public List<LogRecordEnum> getLogEnum(CorpVO cpvo) throws DZFWarpException {

		List<LogRecordEnum> records = new ArrayList<>();

		for (LogRecordEnum enumtemp : LogRecordEnum.getChnSysEnum()) {
			records.add(enumtemp);
		}
		
		return records;
	}

	@Override
	public List<UserVO> getListUservo(String pk_corp) throws DZFWarpException {
		StringBuffer qrysql = new StringBuffer();

		SQLParameter sp = new SQLParameter();

		qrysql.append("select sm.cuserid ,sm.user_name ,sm.user_code ");
		qrysql.append(" from sm_user sm ");
		qrysql.append(" where sm.pk_corp = ? and xsstyle = 6");
		qrysql.append(" order by sm.user_code ");

		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");

		sp.addParam(pk_corp);
		List<UserVO> listuvos = (List<UserVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(UserVO.class));
		
		listuvos = (List<UserVO>) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name"}, listuvos, 1);

		return listuvos;
	}
}
