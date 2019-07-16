package com.dzf.service.channel.balance.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.balance.IRecPayDetailService;

@Service("recPayDetailService")
public class RecPayDetailServiceImpl implements IRecPayDetailService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnDetailRepVO> queryRecDetail(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySqlSpm(pamvo);
		return (List<ChnDetailRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
	}


	private QrySqlSpmVO getQrySqlSpm(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		//1：合同扣款查询；2：商品扣款查询；
		if(pamvo.getQrytype() != null && pamvo.getQrytype() == 1){
			sql.append("select dl.doperatedate,   ");
			sql.append("       dl.vmemo,   ");
			sql.append("       dl.iopertype,   ");
			sql.append("       con.ntotalmny as naccountmny,   ");
			sql.append("       con.nbookmny,   ");
			sql.append("       cncon.ideductpropor,   ");
			sql.append("       dl.nusedmny as nusedmny,   ");
			sql.append("       dl.pk_bill   ");
			sql.append("  from cn_detail dl   ");
			sql.append("  join cn_contract cncon on dl.pk_bill = cncon.pk_confrim   ");
			sql.append("  join ynt_contract con on con.pk_contract = cncon.pk_contract   ");
			sql.append(" where nvl(dl.dr, 0) = 0   ");
			sql.append("   and nvl(cncon.dr, 0) = 0   ");
			sql.append("   and nvl(con.dr, 0) = 0   ");
			sql.append("   and dl.ipaytype = 2   "); // 预付款扣款
			sql.append("   and dl.iopertype = 2   "); // 合同扣款
			sql.append("   and dl.pk_corp = ?   ");
			spm.addParam(pamvo.getPk_corp());
			sql.append("   and dl.doperatedate <= ?   ");
			spm.addParam(pamvo.getBegdate());
			sql.append(" order by dl.doperatedate desc   ");
		}else if(pamvo.getQrytype() != null && pamvo.getQrytype() == 2){
			sql.append("select dl.doperatedate,   ");
			sql.append("       dl.vmemo,   ");
			sql.append("       dl.iopertype,   ");
			sql.append("       dl.nusedmny as nusedmny,   ");
			sql.append("       dl.pk_bill   ");
			sql.append("  from cn_detail dl   ");
			sql.append(" where nvl(dl.dr, 0) = 0   ");
			sql.append("   and dl.ipaytype = 2   "); // 预付款扣款
			sql.append("   and dl.iopertype = 5   "); // 商品购买
			sql.append("   and dl.pk_corp = ?   ");
			spm.addParam(pamvo.getPk_corp());
			sql.append("   and dl.doperatedate <= ?   ");
			spm.addParam(pamvo.getBegdate());
			sql.append(" order by dl.doperatedate desc   ");
		}
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

}
