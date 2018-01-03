package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.InvManagerService;

@Service("invManagerService")
public class InvManagerServiceImpl implements InvManagerService{

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<ChInvoiceVO> query(ChInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		int page = vo.getPage();
		int size = vo.getRows();
		sql.append("select * from (select rownum rn,ci.* from cn_invoice ci");
		sql.append(" where nvl(dr,0) = 0 ");
		if(vo.getInvstatus() == -1){
			sql.append(" and invstatus in (1,2)");
		}else{
			sql.append(" and invstatus = ?");
			sp.addParam(vo.getInvstatus());
		}
		if(!StringUtil.isEmpty(vo.getBdate())){
			sql.append(" and apptime >= ?");
			sp.addParam(vo.getBdate());
		}
		if(!StringUtil.isEmpty(vo.getEdate())){
			sql.append(" and apptime <= ?");
			sp.addParam(vo.getEdate());
		}
		if( null != vo.getCorps() && vo.getCorps().length > 0){
			String corpIdS = SqlUtil.buildSqlConditionForIn(vo.getCorps());
			sql.append(" and pk_corp  in (" + corpIdS + ")");
		}
		sql.append(" and  rownum <= ? order by ts desc ) where rn > ?");
		sp.addParam(page*size);
		sp.addParam((page-1)*size);
		List<ChInvoiceVO> list = (List<ChInvoiceVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ChInvoiceVO.class));
		
		if(list == null || list.size() == 0){
			return list;
		}else{
			for(ChInvoiceVO cvo :list){
				if(!StringUtil.isEmpty(cvo.getInvperson())){
					cvo.setIperson(queryUserName(cvo.getInvperson()));
				}
			}
			return list;
		}
	}
	
	private String queryUserName(String userid){
		UserVO uvo = (UserVO)singleObjectBO.queryVOByID(userid, UserVO.class);
		return CodeUtils1.deCode(uvo.getUser_name());
	}
	
	@Override
	public Integer queryTotalRow(ChInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) from cn_invoice where nvl(dr,0) = 0 ");
		if(vo.getCorps() != null && vo.getCorps().length > 0){
			String corps = SqlUtil.buildSqlConditionForIn(vo.getCorps());
			sql.append(" and pk_corp in (" +corps+ ")");
		}
		if(vo.getInvstatus() == -1){
			sql.append(" and invstatus in (1,2)");
		}else{
			sql.append(" and invstatus = ?");
			sp.addParam(vo.getInvstatus());
		}
		if(!StringUtil.isEmpty(vo.getBdate())){
			sql.append(" and apptime >= ?");
			sp.addParam(vo.getBdate());
		}
		if(!StringUtil.isEmpty(vo.getEdate())){
			sql.append(" and apptime <= ?");
			sp.addParam(vo.getEdate());
		}
		sql.append(" order by ts desc ");
		String total = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor()).toString();
		return Integer.valueOf(total);
	}

	@Override
	public Integer queryChTotalRow(ChInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) from bd_corp where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y'");
		sql.append("  and nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N'");
		if(!StringUtil.isEmpty(vo.getCorpcode())){
			sql.append(" and instr(innercode,?) > 0");
			sp.addParam(vo.getCorpcode());
		}
		String total = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor()).toString();
		return Integer.valueOf(total);
	}

	@Override
	public List<CorpVO> queryChannel(ChInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
//		int page = vo.getPage();
//		int size = vo.getRows();
//		sql.append("select pk_corp,unitname,innercode from (select pk_corp,unitname,innercode,rownum rn from bd_corp ");
//		sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' and nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N' ");
//		if(!StringUtil.isEmpty(vo.getCorpcode())){
//			sql.append(" and instr(innercode,?) > 0");
//			sp.addParam(vo.getCorpcode());
//		}
//		sql.append(" and rownum <= ?)");
//		sql.append(" where rn > ?");
//		sql.append(" order by innercode ");
		sql.append("select pk_corp,unitname,innercode from bd_corp ");
        sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' ");
        sql.append(" and nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N' ");
//        if(!StringUtil.isEmpty(vo.getCorpcode())){
//            sql.append(" and instr(innercode,?) > 0");
//            sp.addParam(vo.getCorpcode());
//        }
        sql.append(" order by innercode ");
//		sp.addParam(page*size);
//		sp.addParam((page-1)*size);
		List<CorpVO> list = (List<CorpVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(CorpVO.class));
		
		if(list != null && list.size() > 0){
		    encodeCorpVO(list);
		    List<CorpVO> rList = new ArrayList<>();
		    if(!StringUtil.isEmpty(vo.getCorpcode())){
		        for(CorpVO cvo : list){
		            if(cvo.getUnitname().contains(vo.getCorpcode()) || cvo.getInnercode().contains(vo.getCorpcode())){
		                rList.add(cvo);
		            }
		        }
		        return rList;
		    }
		}
		return list;
	}
	
	private List<CorpVO> encodeCorpVO(List<CorpVO> vos){
		for(CorpVO vo : vos){
			vo.setUnitname(CodeUtils1.deCode(vo.getUnitname()));
		}
		return vos;
	}
	
	@Override
	public List<ChInvoiceVO> onBilling(String[] pk_invoices, String userid) throws DZFWarpException {
		//
		if(pk_invoices == null || pk_invoices.length == 0){
			throw new BusinessException("请选择发票！");
		}
		List<ChInvoiceVO> lists = new ArrayList<ChInvoiceVO>(); 
		List<ChInvoiceVO> listError = new ArrayList<ChInvoiceVO>(); 
		HashMap<String, DZFDouble> mapUse = queryUsedMny();
		for(String pk_invoice : pk_invoices){
			ChInvoiceVO vo = queryByPk(pk_invoice);
			DZFDouble umny = CommonUtil.getDZFDouble(mapUse.get(vo.getPk_corp()));
			DZFDouble invmny = queryInvoiceMny(vo.getPk_corp());;
			if(vo.getInvstatus() != 1){
			    vo.setMsg("要确认开票的单据不是待开票状态");
			    listError.add(vo);
				continue;
			}
			DZFDouble invprice = new DZFDouble(vo.getInvprice());
            if(invprice.compareTo(umny.sub(invmny)) > 0){
                StringBuffer msg = new StringBuffer();
                msg.append("你本次要确认开票的金额").append(invprice.setScale(2, DZFDouble.ROUND_HALF_UP))
                    .append("元大于可开票金额").append(umny.sub(invmny).setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，请确认。");
                vo.setMsg(msg.toString());
                listError.add(vo);
                continue;
            }
			lists.add(vo);
			vo.setInvperson(userid);
			updateTicketPrice(vo);
			updateInvoice(vo);
		}
//		int success = lists.size();
//		for(ChInvoiceVO vo : lists){
//		    vo.setInvperson(userid);
//            updateTicketPrice(vo);
//		}
//		updateInvoice(lists.toArray(new ChInvoiceVO[0]));
		return listError;
	}
	
	/**
	 * 查询累计扣款
	 * @param pk_invoices
	 * @return
	 * @throws DZFWarpException
	 */
	public HashMap<String, DZFDouble> queryUsedMny() throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append(" select a.pk_corp,");
        sql.append(" sum(nvl(detail.nusedmny,0)) as debittotalmny ");
        sql.append(" from bd_account a");
        sql.append(" left join cn_detail detail on a.pk_corp = detail.pk_corp and detail.iopertype = 2 and nvl(detail.dr,0) = 0 and detail.doperatedate <= ?");
        sp.addParam(new DZFDate());
        sql.append(" where a.ischannel = 'Y'  ");
        sql.append(" group by a.pk_corp");
        List<BillingInvoiceVO> list = (List<BillingInvoiceVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(BillingInvoiceVO.class));
        HashMap<String, DZFDouble> map = new HashMap<>();
        if(list != null && list.size() > 0){
            for(BillingInvoiceVO bvo : list){
                map.put(bvo.getPk_corp(), bvo.getDebittotalmny() == null ? DZFDouble.ZERO_DBL : bvo.getDebittotalmny());
            }
        }
        return map;
    }
	
	/**
     * 查询已开票金额
     * @param vo
     */
    private DZFDouble queryInvoiceMny(String pk_corp){
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append(" select sum(nvl(invprice,0)) as billtotalmny ");
        sql.append(" from cn_invoice  where invstatus = 2 ");
        sql.append(" and apptime <= ? and pk_corp = ?"); 
        sql.append(" and nvl(dr,0) = 0");
        sp.addParam(new DZFDate());
        sp.addParam(pk_corp);
        sql.append(" group by pk_corp ");
        Object obj = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor());
        return obj == null ? DZFDouble.ZERO_DBL : new DZFDouble(obj.toString());
    }


	
	/**
	 * 根据主键查询
	 * @param pk_invoice
	 * @return
	 */
	private ChInvoiceVO queryByPk(String pk_invoice){
		return (ChInvoiceVO)singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, pk_invoice);
	}
	
	/**
	 * 更新开票金额
	 */
	private void updateTicketPrice(ChInvoiceVO vo){
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getInvprice());
		sp.addParam(vo.getPk_corp());
		if(vo.getIpaytype() == 0){
			sp.addParam(2);
		}
		sql.append("update cn_balance set nticketmny = nvl(nticketmny,0) + ? ");
		sql.append("where nvl(dr,0)=0 and pk_corp = ? and ipaytype = ?");
		singleObjectBO.executeUpdate(sql.toString(), sp);
	}
	
	/**
	 * 更新发票
	 */
	private void updateInvoice(ChInvoiceVO[] vos){
		DZFDate time = new DZFDate();
		for(ChInvoiceVO vo : vos){
			vo.setInvtime(time.toString());
			vo.setInvstatus(2);
		}
		singleObjectBO.updateAry(vos, new String[]{"invtime","invperson","invstatus"});
	}
	
	private void updateInvoice(ChInvoiceVO vo){
        DZFDate time = new DZFDate();
        vo.setInvtime(time.toString());
        vo.setInvstatus(2);
        singleObjectBO.update(vo, new String[]{"invtime","invperson","invstatus"});
    }


    @Override
    public void delete(ChInvoiceVO vo) throws DZFWarpException {
        ChInvoiceVO chvo = (ChInvoiceVO)singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, vo.getPk_invoice());
        if(chvo != null){
            if(chvo.getInvcorp() != 2){
                throw new BusinessException("加盟商提交的开票申请不能删除。");
            }
            if(chvo.getInvstatus() == 2 ){
                throw new BusinessException("发票状态为【已开票】，不能删除！");
            }
            singleObjectBO.deleteObject(vo);
        }
    }

    @Override
    public ChInvoiceVO queryTotalPrice(String pk_corp, int ipaytype, String invprice) throws DZFWarpException {
        ChInvoiceVO returnVo = new ChInvoiceVO();
        DZFDouble addPrice = queryAddPrice(pk_corp, ipaytype, invprice);
        DZFDouble ticketPrice = queryTicketPrice(pk_corp, ipaytype);
        DZFDouble nticketmny = ticketPrice.sub(addPrice);
        
        returnVo.setNticketmny(String.valueOf(nticketmny));
        return returnVo;
    }
    
    /**
     * 查询已添加的开票金额总额
     */
    private DZFDouble queryAddPrice(String pk_corp, int ipaytype, String invprice){

        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select nvl(sum(nvl(invprice,0)),0) price from cn_invoice where nvl(dr,0) = 0 and pk_corp = ? and invstatus ！= 2 and ipaytype = ?");
        sp.addParam(pk_corp);
        sp.addParam(ipaytype);
        String price = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("price")).toString();
        DZFDouble nprice = new DZFDouble(price);
        if(!StringUtil.isEmpty(invprice)){
            nprice = nprice.sub(new DZFDouble(invprice));
        }
        return nprice;
    }
    
    private DZFDouble queryTicketPrice(String pk_corp, int ipaytype){
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select nvl(sum(nvl(nusedmny,0))-sum(nvl(nticketmny,0)),0) as nticketmny from cn_balance ");
        sql.append("where nvl(dr,0)=0 and pk_corp = ? and ipaytype = ?");
        sp.addParam(pk_corp);
        if(ipaytype == 0){
            ipaytype = 2;
        }
        sp.addParam(ipaytype);
        String nticketmny = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("nticketmny")).toString();
        DZFDouble price = new DZFDouble(nticketmny);
        return price;
    }

    @Override
    public void save(ChInvoiceVO vo) throws DZFWarpException {
        ChInvoiceVO ovo = (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, vo.getPk_invoice());
        if(StringUtil.isEmpty(vo.getPk_corp())){
            if(ovo == null){
                throw new BusinessException("数据已被删除，请刷新重新操作。");
            }
            vo.setPk_corp(ovo.getPk_corp());
        }
        if(ovo.getInvstatus() == 2){
            throw new BusinessException("已开票，不允许修改。");
        }
        checkInvPrice(vo);
        String[] fieldNames = new String[]{"taxnum","invprice","invtype","corpaddr","invphone","bankname","bankcode","email","vmome"};
        singleObjectBO.update(vo, fieldNames);
    }
    
    /**
     * 校验开票金额（>0 && <=可开票金额）
     */
    private void checkInvPrice(ChInvoiceVO vo){
        if(new DZFDouble(vo.getInvprice()).sub(new DZFDouble(0)).doubleValue() <= 0){
            throw new BusinessException("开票金额小于0！");
        }
        DZFDouble addPrice = queryAddPrice(vo.getPk_corp(), vo.getIpaytype(), vo.getTempprice());
        DZFDouble ticketPrice = queryTicketPrice(vo.getPk_corp(), vo.getIpaytype());
        DZFDouble invPrice = new DZFDouble(vo.getInvprice());
        DZFDouble nticketmny = ticketPrice.sub(addPrice).sub(invPrice);
        if(nticketmny.doubleValue() < 0){
            throw new BusinessException("开票金额不可以大于可开票金额，请重新填写");
        }
    }
    
}
