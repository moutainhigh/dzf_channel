package com.dzf.service.channel.invoice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.invoice.IBillingQueryService;

@Service("billingQueryServiceImpl")
public class BillingQueryServiceImpl implements IBillingQueryService{

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<BillingInvoiceVO> query(BillingInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select a.pk_corp,a.innercode as corpcode,a.unitname as corpname,");
		sql.append(" sum(nvl(detail.nusedmny,0)) as debittotalmny,sum(nvl(invoice.invprice,0)) as billtotalmny ");
		sql.append(" from bd_account a");
        sql.append(" left join cn_detail detail on a.pk_corp = detail.pk_corp and detail.iopertype = 2");
        if(!StringUtil.isEmpty(vo.getBdate())){
            sql.append(" and detail.doperatedate <= ?"); 
            sp.addParam(vo.getBdate());
        }
        sql.append(" left join cn_invoice invoice on invoice.pk_corp = a.pk_corp and (invoice.invstatus = 2 or invoice.invstatus = 1)");
        if(!StringUtil.isEmpty(vo.getBdate())){
            sql.append(" and invoice.invtime <= ?"); 
            sp.addParam(vo.getBdate());
        }
        sql.append(" where a.ischannel = 'Y' and nvl(detail.dr,0) = 0 and nvl(invoice.dr,0) = 0");
        if( null != vo.getCorps() && vo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(vo.getCorps());
            sql.append(" and a.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" group by a.pk_corp,a.innercode ,a.unitname");
		List<BillingInvoiceVO> list = (List<BillingInvoiceVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(BillingInvoiceVO.class));
		if(list != null && list.size() > 0){
		    for(BillingInvoiceVO bvo : list){
		        bvo.setNoticketmny(CommonUtil.getDZFDouble(bvo.getDebittotalmny()).sub(CommonUtil.getDZFDouble(bvo.getBilltotalmny())));
		    }
		}
		return list;
	}

    @Override
    public void insertBilling(BillingInvoiceVO vo) throws DZFWarpException {
        if(vo.getNoticketmny().compareTo(DZFDouble.ZERO_DBL) <= 0){
            throw new BusinessException("未开票金额必须大于0");
        }
        AccountVO avo = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, vo.getPk_corp());
        if(StringUtil.isEmpty(avo.getTaxcode())){
            throw new BusinessException("开票信息【税号】为空。");
        }
        ChInvoiceVO cvo = new ChInvoiceVO();
        cvo.setPk_corp(vo.getPk_corp());
        cvo.setCorpname(vo.getCorpname());
        cvo.setInvnature(0);//发票性质
        cvo.setTaxnum(avo.getTaxcode());//税号
        cvo.setInvprice(vo.getNoticketmny().toString());//开票金额
        cvo.setInvtype(avo.getInvtype() == null ? 2 : avo.getInvtype());//发票类型
        cvo.setCorpaddr(avo.getPostaddr());//公司地址
        cvo.setInvphone(CodeUtils1.deCode(avo.getPhone1()));
        cvo.setBankcode(avo.getVbankcode());//开户账户
        cvo.setBankname(avo.getVbankname());//开户行
        cvo.setEmail(avo.getEmail1());//邮箱
        cvo.setApptime(new DZFDate().toString());//申请日期
        cvo.setInvstatus(1);//状态
        cvo.setIpaytype(0);
        cvo.setInvcorp(2);
        singleObjectBO.saveObject(vo.getPk_corp(), cvo);
    }
}
