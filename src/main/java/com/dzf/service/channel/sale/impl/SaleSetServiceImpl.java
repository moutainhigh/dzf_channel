package com.dzf.service.channel.sale.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.sale.SaleSetVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.UserCache;
import com.dzf.service.channel.sale.ISaleSetService;

@Service("sale_set")
public class SaleSetServiceImpl implements ISaleSetService{

	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Override
	public SaleSetVO query(SaleSetVO vo) throws DZFWarpException {
		String pk_corp = vo.getPk_corp();
		if(StringUtil.isEmptyWithTrim(pk_corp)){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" nvl(dr,0) = 0 and pk_corp = ? ");
		sp.addParam(pk_corp);
		SaleSetVO[] vos = (SaleSetVO[])singleObjectBO.queryByCondition(SaleSetVO.class, sql.toString(), sp);
		if(vos!=null&&vos.length>0){
			if(!StringUtil.isEmpty(vo.getLastmodifypsnid())){
				UserVO uvo =  UserCache.getInstance().get(vo.getLastmodifypsnid(), null);
				vos[0].setLastmodifypsn(uvo.getUser_name());
			}
			return vos[0];
		}
		return null;
	}

	@Override
	public void save(SaleSetVO vo) throws DZFWarpException {
		if(StringUtil.isEmpty(vo.getPk_saleset())){
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		}else{
			singleObjectBO.update(vo, new String[]{"isfirecovery","firstnum","isserecovery",
					"secondnum","isthrecovery","thirdnum","isreceive","receivenum","releasenum","classifyfir",
					"classifysec","classifythi","classifyfou","classifyfif","lastmodifypsnid","lastmodifydate"});
		}
	}
	
	@Override
	public List<SaleSetVO> queryHistory(SaleSetVO vo){
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();//l.iopetype,l.vopemsg,
		sql.append(" select l.doperatedate lastmodifydate,l.cuserid lastmodifypsnid from ynt_logrecord l ");
		sql.append(" where l.vopemsg=? and l.pk_corp=? and nvl(l.dr,0)=0 order by l.ts desc");
		sp.addParam("销售管理规则设置");
		sp.addParam(vo.getPk_corp());
		List<SaleSetVO> list = (List<SaleSetVO>) singleObjectBO.executeQuery(sql.toString(),sp,new BeanListProcessor(SaleSetVO.class));
		UserVO uvo=null;
		if(list!=null&&list.size()>0){
			for (SaleSetVO saleSetVO : list) {
				if(!StringUtil.isEmpty(saleSetVO.getLastmodifypsnid())){
					uvo =  UserCache.getInstance().get(saleSetVO.getLastmodifypsnid(), null);
					saleSetVO.setLastmodifypsn(uvo.getUser_name());
				}
			}
			return list;
		}else{
			return null;
		}
	}
}
