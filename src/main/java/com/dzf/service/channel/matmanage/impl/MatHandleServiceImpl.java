package com.dzf.service.channel.matmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.matmanage.IMatHandleService;

@Service("mathandle")
public class MatHandleServiceImpl implements IMatHandleService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> queryComboBox()   throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT lg.pk_logistics, \n");
		sql.append("    lg.vname logname \n");
		sql.append(" FROM cn_logistics lg \n");
		sql.append("  where nvl(lg.dr,0)= 0 \n");
		List<MatOrderVO> volist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), null, new BeanListProcessor(MatOrderVO.class));
		return volist;
		
	}

}
