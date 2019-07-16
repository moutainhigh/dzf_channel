package com.dzf.service.channel.invoice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.invoice.IBillingQueryService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("billingQueryServiceImpl")
public class BillingQueryServiceImpl implements IBillingQueryService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IPubService pubser;

	private final static String tablename = "cn_invoice";
	
	@Autowired
	private IUserService userServiceImpl;

	@SuppressWarnings("unchecked")
	@Override
	public List<BillingInvoiceVO> query(BillingInvoiceVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select account.pk_corp,   ");
		sql.append("       account.innercode as corpcode,   ");
		sql.append("       account.unitname as corpname,   ");
		sql.append("       account.vprovince,   ");
		sql.append("       account.citycounty as vprovname,   ");
		sql.append("       SUM(CASE WHEN t.ipaytype = 2 AND t.iopertype = 2 THEN nvl(t.nusedmny,0)   ");
		sql.append("       			ELSE 0 END) AS debitconmny,   ");
		sql.append("       SUM(CASE WHEN t.ipaytype = 2 AND t.iopertype = 5 THEN nvl(t.nusedmny,0)   ");
		sql.append("       			ELSE 0 END) AS debitbuymny   ");
		sql.append("  from bd_account account   ");
		sql.append("  left join cn_detail t on account.pk_corp = t.pk_corp   ");
		sql.append("                            and nvl(t.dr, 0) = 0   ");
		// 付款类型  1：加盟费；2：预付款；3：返点；
		sql.append("                            and t.ipaytype = 2   ");
		// 操作类型  1：付款单付款；2：合同扣款；3：返点单确认；4：退款单审核；5：商品购买；																
		sql.append("                            and t.iopertype in (2, 5)   ");

		if (!StringUtil.isEmpty(paramvo.getBdate())) {
			sql.append(" and t.doperatedate <= ?");
			spm.addParam(paramvo.getBdate());
		}
		//sql.append(" where account.ischannel = 'Y'  ");
		sql.append(" where " + QueryUtil.getWhereSql());
		
		
		if (null != paramvo.getCorps() && paramvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
			sql.append(" and account.pk_corp  in (" + corpIdS + ")");
		}

		if (!StringUtil.isEmpty(paramvo.getVmanager())) {
			String qrysql = getQrySql(paramvo.getVmanager(), IStatusConstant.IQUDAO);
			if(!StringUtil.isEmpty(qrysql)){
				sql.append(qrysql);
			}
		}
		
		if(!StringUtil.isEmpty(paramvo.getVoperater())){
			String qrysql = getQrySql(paramvo.getVoperater(), IStatusConstant.IYUNYING);
			if(!StringUtil.isEmpty(qrysql)){
				sql.append(qrysql);
			}
		}

		String where = pubser.makeCondition(paramvo.getCuserid(), paramvo.getAreaname(), IStatusConstant.IYUNYING);
		if (where != null && !where.equals("alldata")) {
			sql.append(where);
		} else if (where == null) {
			return new ArrayList<BillingInvoiceVO>();
		}
		sql.append(" group by account.pk_corp,   ");
		sql.append("          account.innercode,   ");
		sql.append("          account.unitname,   ");
		sql.append("          account.vprovince,   ");
		sql.append("          account.citycounty   ");

		List<BillingInvoiceVO> list = (List<BillingInvoiceVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BillingInvoiceVO.class));
		
		if (list != null && list.size() > 0) {
			return setShowData(paramvo, list);
		}
		return list;
	}
	
	/**
	 * 设置显示数据
	 * @param pamvo
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<BillingInvoiceVO> setShowData(BillingInvoiceVO pamvo, List<BillingInvoiceVO> list) throws DZFWarpException {
		HashMap<String, BillingInvoiceVO> map = queryInvoiceMny(pamvo);
		Map<Integer, String> areaMap = pubser.getAreaMap(pamvo.getAreaname(), 3);
		List<BillingInvoiceVO> retlist = new ArrayList<BillingInvoiceVO>();
		QueryDeCodeUtils.decKeyUtils(new String[] { "corpname" }, list, 2);
		
		Map<String, UserVO> marmap = pubser.getManagerMap(1);// 渠道经理
		Map<String, UserVO> opermap = pubser.getManagerMap(3);// 渠道运营
		
		UserVO uservo = null;
		for (BillingInvoiceVO bvo : list) {
			if (areaMap != null && !areaMap.isEmpty()) {
				String area = areaMap.get(bvo.getVprovince());
				if (!StringUtil.isEmpty(area)) {
					bvo.setAreaname(area);
				}
			}
			if (map != null && !map.isEmpty()) {
				BillingInvoiceVO binvo = map.get(bvo.getPk_corp());
				if (binvo != null) {
					bvo.setBillconmny(CommonUtil.getDZFDouble(binvo.getBillconmny()));// 累计合同开票金额
					bvo.setNoticketmny(SafeCompute.sub(bvo.getDebitconmny(), bvo.getBillconmny()));// 合同扣款未开票金额

					bvo.setBillbuymny(CommonUtil.getDZFDouble(binvo.getBillbuymny()));// 累计商品开票金额
					bvo.setNotbuymny(SafeCompute.sub(bvo.getDebitbuymny(), bvo.getBillbuymny()));// 商品购买未开票金额
				}

			}
			
			if (marmap != null && !marmap.isEmpty()) {
				uservo = marmap.get(bvo.getPk_corp());
				if (uservo != null) {
					bvo.setVmanager(uservo.getUser_name());// 渠道经理
				}
			}
			if (opermap != null && !opermap.isEmpty()) {
				uservo = opermap.get(bvo.getPk_corp());
				if (uservo != null) {
					bvo.setVoperater(uservo.getUser_name());// 渠道运营
				}
			}
			
			if (!StringUtil.isEmpty(pamvo.getCorpname())) {
				if (bvo.getCorpcode().indexOf(pamvo.getCorpname()) != -1
						|| bvo.getCorpname().indexOf(pamvo.getCorpname()) != -1) {
					retlist.add(bvo);
				}
			}
		}
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			return retlist;
		}
		return list;
	}

	/**
	 * 获取查询条件
	 * 
	 * @param cuserid
	 * @param qrytype   1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	public String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		String[] corps = pubser.getManagerCorp(cuserid, qrytype);
		if (corps != null && corps.length > 0) {
			String where = SqlUtil.buildSqlForIn(" t.pk_corp", corps);
			sql.append(" AND ").append(where);
		} else {
			sql.append(" AND t.pk_corp is null   ");
		}
		return sql.toString();
	}

	/**
	 * 查询已开票金额
	 * 
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, BillingInvoiceVO> queryInvoiceMny(BillingInvoiceVO vo) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select a.pk_corp,   ");
		// sql.append(" sum(nvl(i.invprice, 0)) as billtotalmny,");
		sql.append("       sum(decode(i.isourcetype, 1, nvl(i.invprice, 0), 0)) AS billconmny,   ");// 累计合同开票金额
		sql.append("       sum(decode(i.isourcetype, 2, nvl(i.invprice, 0), 0)) AS billbuymny   ");// 累计商品开票金额
		sql.append("  from bd_account a   ");
		sql.append("  left join cn_invoice i on i.pk_corp = a.pk_corp   ");
		sql.append("                              and nvl(i.dr, 0) = 0   ");
		sql.append("                              and i.invstatus in (1, 2, 3)   ");// 发票状态
																					// 0：待提交
																					// ；1：待开票；2：已开票；3：开票失败；
		sql.append("                              and i.isourcetype in (1, 2)   ");// 发票来源类型
																					// 1：合同扣款开票；
																					// 2：订单扣款开票；
		sql.append("                              and i.apptime <= ?   ");
		spm.addParam(new DZFDate());
		sql.append(" where a.ischannel = 'Y'   ");
		if (null != vo.getCorps() && vo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(vo.getCorps());
			sql.append(" and a.pk_corp  in (" + corpIdS + ")");
		}
		sql.append(" group by a.pk_corp   ");
		List<BillingInvoiceVO> list = (List<BillingInvoiceVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BillingInvoiceVO.class));
		HashMap<String, BillingInvoiceVO> map = new HashMap<>();
		if (list != null && list.size() > 0) {
			for (BillingInvoiceVO bvo : list) {
				map.put(bvo.getPk_corp(), bvo);
			}
		}
		return map;

	}

	@Override
	public void insertBilling(BillingInvoiceVO vo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(tablename, vo.getPk_corp(), uuid, 60);
			if (vo.getNoticketmny().compareTo(DZFDouble.ZERO_DBL) <= 0) {
				throw new BusinessException("未开票金额必须大于0");
			}
			DZFDouble invmny = queryInvoiceMny(vo.getPk_corp());// 累计合同开票金额
			DZFDouble umny = CommonUtil.getDZFDouble(vo.getDebitconmny());// 累计合同扣款金额
			DZFDouble invprice = new DZFDouble(vo.getNoticketmny());// 合同扣款未开票金额
			if (invprice.compareTo(umny.sub(invmny)) > 0) {
				StringBuffer msg = new StringBuffer();
				msg.append("你本次要开票的金额").append(invprice.setScale(2, DZFDouble.ROUND_HALF_UP)).append("元大于可开票金额")
						.append(umny.sub(invmny).setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，请刷新数据。");
				throw new BusinessException(msg.toString());
			}
			AccountVO avo = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, vo.getPk_corp());
			if (StringUtil.isEmpty(avo.getTaxcode())) {
				throw new BusinessException("开票信息【税号】为空。");
			}

			saveChinvoice(vo, avo);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(tablename, vo.getPk_corp(), uuid);
		}
	}

	/**
	 * 发票保存
	 * 
	 * @param vo
	 * @param avo
	 * @throws DZFWarpException
	 */
	private void saveChinvoice(BillingInvoiceVO vo, AccountVO avo) throws DZFWarpException {
		if (vo.getNoticketmny().compareTo(new DZFDouble(10000)) > 0) {
			List<ChInvoiceVO> list = new ArrayList<ChInvoiceVO>();
			list = getChinvoiceList(vo, avo, list);
			singleObjectBO.insertVOArr(vo.getPk_corp(), list.toArray(new ChInvoiceVO[0]));
		} else {
			ChInvoiceVO cvo = getChinvoiceVO(vo, avo, vo.getNoticketmny());
			singleObjectBO.saveObject(vo.getPk_corp(), cvo);
		}
	}

	/**
	 * 当开票金额大于 1万时，递归组装小于等于 10万的多条发票
	 * 
	 * @param vo
	 * @param avo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ChInvoiceVO> getChinvoiceList(BillingInvoiceVO vo, AccountVO avo, List<ChInvoiceVO> list)
			throws DZFWarpException {
		ChInvoiceVO cvo = null;
		if (vo.getNoticketmny().compareTo(new DZFDouble(100000)) > 0) {
			cvo = getChinvoiceVO(vo, avo, new DZFDouble(100000));
			list.add(cvo);
			DZFDouble noticketmny = SafeCompute.sub(vo.getNoticketmny(), new DZFDouble(100000));
			vo.setNoticketmny(noticketmny);
			getChinvoiceList(vo, avo, list);
		} else if (vo.getNoticketmny().compareTo(new DZFDouble(100000)) < 0
				&& vo.getNoticketmny().compareTo(new DZFDouble(0)) > 0) {
			cvo = getChinvoiceVO(vo, avo, vo.getNoticketmny());
			list.add(cvo);
			vo.setNoticketmny(DZFDouble.ZERO_DBL);
		}
		return list;
	}

	/**
	 * 构造开票vo
	 * 
	 * @param vo
	 * @param avo
	 * @param noticketmny
	 * @return
	 * @throws DZFWarpException
	 */
	private ChInvoiceVO getChinvoiceVO(BillingInvoiceVO vo, AccountVO avo, DZFDouble noticketmny)
			throws DZFWarpException {
		ChInvoiceVO cvo = new ChInvoiceVO();
		cvo.setPk_corp(vo.getPk_corp());
		cvo.setCorpname(vo.getCorpname());
		cvo.setInvnature(0);// 发票性质
		cvo.setTaxnum(avo.getTaxcode());// 税号
		cvo.setInvprice(noticketmny);// 开票金额
		cvo.setInvtype(avo.getInvtype() == null ? 2 : avo.getInvtype());// 发票类型
		cvo.setCorpaddr(avo.getPostaddr());// 公司地址
		cvo.setInvphone(CodeUtils1.deCode(avo.getPhone1()));
		cvo.setBankcode(avo.getVbankcode());// 开户账户
		cvo.setBankname(avo.getVbankname());// 开户行
		cvo.setEmail(avo.getEmail1());// 邮箱
		cvo.setApptime(new DZFDate().toString());// 申请日期
		cvo.setInvstatus(1);// 状态
		cvo.setIpaytype(0);
		cvo.setInvcorp(2);
		cvo.setRusername(avo.getLinkman2());
		cvo.setIsourcetype(1);// 发票来源类型 1：合同扣款开票； 2：商品扣款开票；
		return cvo;
	}

	/**
	 * 查询累计合同开票金额
	 * 
	 * @param vo
	 */
	private DZFDouble queryInvoiceMny(String pk_corp) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select    ");
		sql.append("       sum(nvl(invprice, 0)) as billconmny   ");// 累计合同开票金额
		sql.append("  from cn_invoice   ");
		sql.append(" where nvl(dr,0) = 0   ");
		// 发票状态 0：待提交 ；1：待开票；2：已开票；3：开票失败；
		sql.append("   and invstatus in (1, 2, 3)   ");
		sql.append("   and apptime <= ?   ");
		sql.append("   and pk_corp = ?   ");
		sql.append("   and isourcetype = 1   ");
		spm.addParam(new DZFDate());
		spm.addParam(pk_corp);
		sql.append(" group by pk_corp ");
		Object obj = singleObjectBO.executeQuery(sql.toString(), spm, new ColumnProcessor());
		return obj == null ? DZFDouble.ZERO_DBL : new DZFDouble(obj.toString());
	}

}
