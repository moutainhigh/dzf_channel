package com.dzf.service.channel.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.sys.sys_power.CorpDocVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.DZfcommonTools;
import com.dzf.service.channel.IChannelCustomerApprove;

@Service("sys_channel_approveserv")
public class ChannelCustomerApproveImpl implements IChannelCustomerApprove {
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public void processApprove(CorpVO[] corps, String user)
			throws DZFWarpException {
	    if(corps != null && corps.length > 0){
	        for (CorpVO corp : corps) {
	            corp.setApprove_user(user);
	            corp.setApprove_time(new DZFDateTime());
	        }
	        singleObjectBO.updateAry(corps, new String[] { "approve_status",
	                "approve_comment", "approve_user", "approve_time" });
	    }
	}

	@Override
	public void processAbandonApprove(CorpVO[] corps) throws DZFWarpException {
	    if(corps != null && corps.length > 0){
	        for (CorpVO corp : corps) {
	            corp.setApprove_comment(null);
	            corp.setApprove_time(null);
	            corp.setApprove_user(null);
	            corp.setApprove_status(0);
	        }
	        singleObjectBO.updateAry(corps, new String[] { "approve_status",
	                "approve_comment", "approve_user", "approve_time" });
	    }
	}

	@Override
	public List<CorpVO> queryChannelBusiness(String logincorp, String location)
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(logincorp);
		sp.addParam(logincorp);
		StringBuilder sb = new StringBuilder();
		sb.append("select * from bd_corp ")
				.append(" where (pk_corp = ? or fathercorp = ?) ")
				.append(" and nvl(dr,0) = 0  and ischannel = 'Y' ");
		if (!StringUtil.isEmpty(location)) {
			sp.addParam(location);
			sb.append(" and citycounty = ? ");
		}
		sb.append("  order by fathercorp, pk_corp ");
		List<CorpVO> list = (List) singleObjectBO.executeQuery(sb.toString(),
				sp, new BeanListProcessor(CorpVO.class));
		return list;
	}

	@Override
	public List<CorpVO> queryChannelCustomer(String account_corp, String bdate,
			String edate, String status) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuilder sb = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();

		sb.append(" select a.pk_corp, a.innercode,a.unitname,a.createdate,a.ischannel,a.ishasaccount,")
		        .append(" a.approve_status,a.approve_comment,a.approve_user,a.approve_time,")
		        .append(" u.user_name as approve_user_name,")
				.append(" t.tradename as indusname, acc.unitname as foreignname from bd_corp a ")
				.append(" left join sm_user u on a.approve_user = u.cuserid")
				.append(" left join ynt_bd_trade t on a.industry = t.pk_trade ")
				.append(" left join bd_account acc on a.fathercorp = acc.pk_corp ");

		whereSql.append(" where nvl(a.dr,0) =0  ")
			.append(" and nvl(a.isaccountcorp, 'N') = 'N' and a.isformal = 'Y' ")
			.append(" and nvl(a.isseal,'N') = 'N' and a.ischannel = 'Y' ");
		if (!StringUtil.isEmpty(account_corp)) {
			whereSql.append(" and a.fathercorp = ? ");
			sp.addParam(account_corp);
		}
		if (!StringUtil.isEmpty(bdate)) {
			whereSql.append(" and a.createdate >= ? ");
			sp.addParam(bdate);
		}
		if (!StringUtil.isEmpty(edate)) {
			whereSql.append(" and a.createdate <= ? ");
			sp.addParam(edate);
		}
		if (!StringUtil.isEmpty(status)) {
			whereSql.append(" and a.approve_status = ? ");
			sp.addParam(Integer.valueOf(status));
		}
		sb.append(whereSql);
		sb.append(" order by a.foreignname, a.innercode ");
		List<CorpVO> list = (List) singleObjectBO.executeQuery(sb.toString(),
				sp, new BeanListProcessor(CorpVO.class));

//		sb = new StringBuilder();
//		sb.append("select pk_doc, pk_corp, docname, vfilepath from ynt_corpdoc")
//				.append(" where nvl(dr, 0)=0 and pk_corp in ")
//				.append("(select pk_corp from bd_corp a ").append(whereSql)
//				.append(")");
//		List<CorpDocVO> docList = (List) singleObjectBO.executeQuery(
//				sb.toString(), sp, new BeanListProcessor(CorpDocVO.class));
//
//		Map<String, List<CorpDocVO>> docMap = DZfcommonTools.hashlizeObject(
//				docList, new String[] { "pk_corp" });
//		for (CorpVO corp : list) {
//			if (docMap.containsKey(corp.getPk_corp())) {
//				corp.setCorpDocVos(docMap.get(corp.getPk_corp()).toArray(
//						new CorpDocVO[0]));
//			}
//		}
		return list;
	}

	@Override
	public CorpDocVO queryCorpDocByID(String pk_doc) throws DZFWarpException {
		return (CorpDocVO) singleObjectBO.queryVOByID(pk_doc, CorpDocVO.class);
	}

}
