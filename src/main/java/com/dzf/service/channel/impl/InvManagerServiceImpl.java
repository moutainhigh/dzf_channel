package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.ChInvoiceVO;
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
		int page = vo.getPage();
		int size = vo.getRows();
		sql.append("select pk_corp,unitname,innercode from (select pk_corp,unitname,innercode,rownum rn from bd_corp ");
		sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' and nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N' ");
		if(!StringUtil.isEmpty(vo.getCorpcode())){
			sql.append(" and instr(innercode,?) > 0");
			sp.addParam(vo.getCorpcode());
		}
		sql.append(" and rownum <= ?)");
		sql.append(" where rn > ?");
		sp.addParam(page*size);
		sp.addParam((page-1)*size);
		List<CorpVO> list = (List<CorpVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(CorpVO.class));
		if(list == null || list.size() == 0){
			return list;
		}else{
			return encodeCorpVO(list);
		}
	}
	
	private List<CorpVO> encodeCorpVO(List<CorpVO> vos){
		for(CorpVO vo : vos){
			vo.setUnitname(CodeUtils1.deCode(vo.getUnitname()));
		}
		return vos;
	}

	@Override
	public int onBilling(String[] pk_invoices, String userid) throws DZFWarpException {
		//
		if(pk_invoices == null || pk_invoices.length == 0){
			throw new BusinessException("请选择发票！");
		}
		List<ChInvoiceVO> lists = new ArrayList<ChInvoiceVO>(); 
		for(String pk_invoice : pk_invoices){
			ChInvoiceVO vo = queryByPk(pk_invoice);
			if(vo.getInvstatus() != 1){
				continue;
			}
			lists.add(vo);
		}
		int success = lists.size();
		for(ChInvoiceVO vo : lists){
			vo.setInvperson(userid);
			updateTicketPrice(vo);
		}
		updateInvoice(lists.toArray(new ChInvoiceVO[0]));
		return success;
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
        checkTaxnum(vo.getTaxnum(), vo.getPk_invoice());
        checkInvPrice(vo);
        String[] fieldNames = new String[]{"taxnum","invprice","invtype","corpaddr","invphone","bankname","bankcode","email"};
        singleObjectBO.update(vo, fieldNames);
    }
    
    /**
     * 校验开票金额（>0 && <=可开票金额）
     */
    private void checkInvPrice(ChInvoiceVO vo){
        if(new DZFDouble(vo.getInvprice()).sub(new DZFDouble(0)).doubleValue() <= 0){
            throw new BusinessException("开票金额小于0！");
        }
        DZFDouble addPrice = queryAddPrice(vo.getPk_corp(), vo.getIpaytype(), vo.getTempPrice());
        DZFDouble ticketPrice = queryTicketPrice(vo.getPk_corp(), vo.getIpaytype());
        DZFDouble invPrice = new DZFDouble(vo.getInvprice());
        DZFDouble nticketmny = ticketPrice.sub(addPrice).sub(invPrice);
        if(nticketmny.doubleValue() < 0){
            throw new BusinessException("开票金额不可以大于可开票金额，请重新填写");
        }
    }
    
    /**
     * 校验税号不能重复
     * @param taxNum
     */
    private void checkTaxnum(String taxNum, String pk){
        StringBuffer condition = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        condition.append("nvl(dr,0) = 0");
        if(!StringUtil.isEmpty(pk)){
            condition.append(" and pk_invoice != ?");
            sp.addParam(pk);
        }
        ChInvoiceVO[] voArry = (ChInvoiceVO[])singleObjectBO.queryByCondition(ChInvoiceVO.class, condition.toString(), sp);
        if(voArry != null && voArry.length > 0){
            for(ChInvoiceVO vo : voArry){
                if(vo.getTaxnum().equals(taxNum)){
                    throw new BusinessException("税号不能重复！");
                }
            }
        }
    }
}
