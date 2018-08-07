package com.dzf.service.channel.balance.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.balance.IRecPayDetailService;

@Service("recPayDetailService")
public class RecPayDetailServiceImpl implements IRecPayDetailService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private MultBodyObjectBO multBodyObjectBO;
    
    @Override
    public List<ChnDetailRepVO> queryRecDetail(QryParamVO paramvo) throws DZFWarpException {
        String sql = getSql();
        SQLParameter params = new SQLParameter();
        params.addParam(paramvo.getPk_corp());
        params.addParam(paramvo.getBegdate());
        return (List<ChnDetailRepVO>)singleObjectBO.executeQuery(sql, params, new BeanListProcessor(ChnDetailRepVO.class));
        
//        return (List<ChnDetailRepVO>)multBodyObjectBO.queryDataPage(ChnDetailRepVO.class, sql, params, paramvo.getPage(), paramvo.getRows(), null);
    }
    
    @Override
    public int queryRecDetailTotal(QryParamVO paramvo) throws DZFWarpException {
        String sql = getSql();
        SQLParameter params = new SQLParameter();
        params.addParam(paramvo.getPk_corp());
        params.addParam(paramvo.getBegdate());
        return multBodyObjectBO.queryDataTotal(ChnDetailRepVO.class,sql, params);
    }
    
    private String getSql(){
        StringBuffer str = new StringBuffer();
        str.append("select dl.doperatedate,dl.vmemo,con.ntotalmny as naccountmny,con.nbookmny, ");
        str.append(" cncon.ideductpropor,dl.nusedmny as nusedmny,dl.pk_bill ");
        str.append(" from cn_detail dl");
        str.append(" join cn_contract cncon on dl.pk_bill = cncon.pk_confrim");
        str.append(" join ynt_contract con on con.pk_contract = cncon.pk_contract");
        str.append(" where nvl(dl.dr,0) = 0 and nvl(cncon.dr,0) = 0 and nvl(con.dr,0) = 0 ");
        str.append(" and nvl(dl.iopertype,1) = 2 and dl.ipaytype = 2");
        str.append(" and dl.pk_corp = ? and dl.doperatedate <= ?");
        str.append(" order by  dl.doperatedate desc");
        return str.toString();
    }
}
