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
}
