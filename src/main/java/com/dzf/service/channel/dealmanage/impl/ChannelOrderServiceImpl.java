package com.dzf.service.channel.dealmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.dealmanage.GoodsBillBVO;
import com.dzf.model.channel.dealmanage.GoodsBillSVO;
import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.model.channel.stock.StockNumVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.ICarryOverService;
import com.dzf.service.channel.dealmanage.IChannelOrderService;

@Service("channelorderser")
public class ChannelOrderServiceImpl implements IChannelOrderService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private ICarryOverService carryover;

	@Override
	public Integer queryTotalRow(GoodsBillVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(GoodsBillVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsBillVO> query(GoodsBillVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(pamvo);
		List<GoodsBillVO> list = (List<GoodsBillVO>) multBodyObjectBO.queryDataPage(GoodsBillVO.class, sqpvo.getSql(),
				sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if (list != null && list.size() > 0) {
			setShowValue(list);
		}
		return list;
	}

	/**
	 * 设置显示名称
	 * 
	 * @throws DZFWarpException
	 */
	private void setShowValue(List<GoodsBillVO> list) throws DZFWarpException {
		CorpVO corpvo = null;
		for (GoodsBillVO bvo : list) {
			corpvo = CorpCache.getInstance().get(null, bvo.getPk_corp());
			if (corpvo != null) {
				bvo.setCorpcode(corpvo.getInnercode());
				bvo.setCorpname(corpvo.getUnitname());
			}
		}
	}

	/**
	 * 获取查询条件
	 * 
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(GoodsBillVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT l.pk_goodsbill,  \n");
		sql.append("       l.vbillcode,  \n");
		sql.append("       l.vreceivername,  \n");
		sql.append("       l.phone,  \n");
		sql.append("       l.vzipcode,  \n");
		sql.append("       l.vreceiveaddress,  \n");
		sql.append("       l.pk_corp,  \n");
		sql.append("       l.ndedsummny,  \n");
		sql.append("       l.ndeductmny,  \n");
		sql.append("       l.ndedrebamny,  \n");
		sql.append("       l.vstatus,  \n");
		sql.append("       nvl(l.vtistatus,1) AS vtistatus, \n");
		sql.append("       l.updatets,  \n");
		sql.append("       t.logisticsunit,  \n");
		sql.append("       t.fastcode,  \n");
		sql.append("       s.doperatetime AS dsubmittime,  \n");
		sql.append("       u.doperatedate AS dconfdate  \n");
		sql.append("  FROM cn_goodsbill l  \n");
		sql.append("  LEFT JOIN cn_goodsbill_s s ON l.pk_goodsbill = s.pk_goodsbill  \n");
		sql.append("                            AND s.vstatus = 0  \n");
		sql.append("  LEFT JOIN cn_goodsbill_s t ON l.pk_goodsbill = t.pk_goodsbill  \n");
		sql.append("                            AND t.vstatus = 2  \n");
		sql.append("  LEFT JOIN cn_goodsbill_s u ON l.pk_goodsbill = u.pk_goodsbill  \n");
		sql.append("                            AND u.vstatus = 1  \n");
		sql.append(" WHERE nvl(l.dr, 0) = 0  \n");
		sql.append("   AND nvl(s.dr, 0) = 0  \n");
		sql.append("   AND nvl(u.dr, 0) = 0  \n");
		if (!StringUtil.isEmpty(pamvo.getVbillcode())) {
			sql.append("   AND l.vbillcode = ?  \n");
			spm.addParam(pamvo.getVbillcode());
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			String[] strs = pamvo.getPk_corp().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND l.pk_corp in (").append(inSql).append(")");
		}
		if (pamvo.getVstatus() != null && pamvo.getVstatus() != -1) {//订单状态，界面快速查询
			sql.append("   AND l.vstatus = ? \n");
			spm.addParam(pamvo.getVstatus());
		}
		if(!StringUtil.isEmpty(pamvo.getLogisticsunit())){//订单状态，下拉多选查询
			String[] strs = pamvo.getLogisticsunit().split(",");
			String where = SqlUtil.buildSqlForIn("l.vstatus", strs);
			sql.append(" AND ").append(where);
		}
		if (pamvo.getVtistatus() != null && pamvo.getVtistatus() != -1) {//开票状态
			sql.append("   AND nvl(l.vtistatus,1) = ? \n");
			spm.addParam(pamvo.getVtistatus());
		}
		if (!StringUtil.isEmpty(pamvo.getPk_goodsbill())) {
			sql.append(" AND l.pk_goodsbill = ? ");
			spm.addParam(pamvo.getPk_goodsbill());
		}
		if (pamvo.getBegdate() != null) {
			sql.append(" AND s.doperatedate >= ?  \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND s.doperatedate <= ?  \n");
			spm.addParam(pamvo.getEnddate());
		}
		if (pamvo.getBbegdate() != null) {
			sql.append(" AND u.doperatedate >= ?  \n");
			spm.addParam(pamvo.getBbegdate());
		}
		if (pamvo.getEenddate() != null) {
			sql.append(" AND u.doperatedate <= ?  \n");
			spm.addParam(pamvo.getEenddate());
		}
		sql.append(" ORDER BY s.doperatetime DESC");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	/**
	 * 
	 * @param pamvo
	 * @param type
	 *            1：确认；2：取消订单；3：取消确认；4：发票申请；
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public GoodsBillVO updateData(GoodsBillVO pamvo, Integer type, String cuserid) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(pamvo.getTableName(), pamvo.getPk_goodsbill(), uuid, 60);
			checkData(pamvo, type);
			if (type != null) {
				if (type == 1) {
					return updateConfirm(pamvo, cuserid);
				} else if (type == 2) {
					return updateCancel(pamvo, cuserid);
				} else if (type == 3) {
					return updateCancelConf(pamvo, cuserid);
				} else if(type == 4){
					saveBillData(pamvo, cuserid);
				}
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(pamvo.getTableName(), pamvo.getPk_goodsbill(), uuid);
		}
		return pamvo;
	}
	
	/**
	 * 发票申请
	 * @param pamvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void saveBillData(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		AccountVO accvo = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, pamvo.getPk_corp());
		if(accvo == null){
			throw new BusinessException("会计贵公司信息错误！");
		}
		if (StringUtil.isEmpty(accvo.getTaxcode())) {
			throw new BusinessException("开票信息【税号】为空。");
		}
		ChInvoiceVO cvo = new ChInvoiceVO();
		cvo.setPk_corp(pamvo.getPk_corp());
		cvo.setInvprice(pamvo.getNdedsummny());// 开票金额 = 订单扣款总金额
		cvo.setInvnature(0);// 发票性质
		cvo.setCorpname(CodeUtils1.deCode(accvo.getUnitname()));
		cvo.setTaxnum(accvo.getTaxcode());// 税号
		cvo.setInvtype(accvo.getInvtype() == null ? 2 : accvo.getInvtype());// 发票类型
		cvo.setCorpaddr(accvo.getPostaddr());// 公司地址
		cvo.setInvphone(CodeUtils1.deCode(accvo.getPhone1()));
		cvo.setBankcode(accvo.getVbankcode());// 开户账户
		cvo.setBankname(accvo.getVbankname());// 开户行
		cvo.setEmail(accvo.getEmail1());// 邮箱
		cvo.setApptime(new DZFDate().toString());// 申请日期
		cvo.setInvstatus(1);// 状态
		cvo.setIpaytype(2);//付款类型  0：预付款；   1：加盟费；2：预付款+发点
		cvo.setInvcorp(2);
		cvo.setRusername(accvo.getLinkman2());
		cvo.setIsourcetype(2);//发票来源类型  1：合同扣款开票； 2：商品扣款开票；
		cvo.setPk_source(pamvo.getPk_goodsbill());
		singleObjectBO.saveObject(pamvo.getPk_corp(), cvo);
		
		pamvo.setVtistatus(2);//已开票
		singleObjectBO.update(pamvo, new String[] { "vtistatus" });
	}

	/**
	 * 更新取消订单数据
	 * 
	 * @param pamvo
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsBillVO updateCancel(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		pamvo.setVstatus(IStatusConstant.IORDERSTATUS_4);// 已取消
		pamvo.setUpdatets(new DZFDateTime());
		singleObjectBO.update(pamvo, new String[] { "vstatus" });

		// 订单购买详情
		GoodsBillSVO bsvo = new GoodsBillSVO();
		bsvo.setPk_goodsbill(pamvo.getPk_goodsbill());
		bsvo.setPk_corp(pamvo.getPk_corp());
		bsvo.setVsaction(IStatusConstant.IORDERACTION_4);
		bsvo.setVstatus(IStatusConstant.IORDERSTATUS_4);
		bsvo.setVsdescribe(IStatusConstant.IORDERDESCRIBE_4);// 状态描述
		bsvo.setCoperatorid(cuserid);
		bsvo.setDoperatedate(new DZFDate());
		bsvo.setDoperatetime(new DZFDateTime());
		bsvo.setVnote(pamvo.getVrejereason());// 处理说明
		singleObjectBO.saveObject(bsvo.getPk_corp(), bsvo);

		// 释放购买量
		updateStockNum(pamvo);

		return pamvo;
	}

	/**
	 * 释放库存的购买量
	 * 
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	private void updateStockNum(GoodsBillVO pamvo) throws DZFWarpException {
		String sql = " nvl(dr,0) = 0 AND pk_goodsbill = ? AND pk_corp = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pamvo.getPk_goodsbill());
		spm.addParam(pamvo.getPk_corp());
		GoodsBillBVO[] bVOs = (GoodsBillBVO[]) singleObjectBO.queryByCondition(GoodsBillBVO.class, sql, spm);
		if (bVOs != null && bVOs.length > 0) {
			for (GoodsBillBVO bvo : bVOs) {
				updateStockOffNum(bvo);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateStockOffNum(GoodsBillBVO bvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n");
		sql.append("  FROM cn_stocknum  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_corp = ?  \n");
		sql.append("   AND pk_warehouse = ?  \n");
		sql.append("   AND pk_goods = ?  \n");
		sql.append("   AND pk_goodsspec = ? \n");
		spm.addParam("000001");
		spm.addParam(IStatusConstant.CK_ID);// 大账房默认库存
		spm.addParam(bvo.getPk_goods());
		spm.addParam(bvo.getPk_goodsspec());
		List<StockNumVO> list = (List<StockNumVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(StockNumVO.class));
		if (list != null && list.size() > 0) {
			StockNumVO numvo = list.get(0);
			updateStock(numvo, bvo);
		} else if (list != null && list.size() > 1) {
			throw new BusinessException("库存商品数量错误");
		}
	}

	/**
	 * 更新库存
	 * 
	 * @param numvo
	 * @param bvo
	 * @throws DZFWarpException
	 */
	private void updateStock(StockNumVO numvo, GoodsBillBVO bvo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(numvo.getTableName(), numvo.getPk_goods() + "" + numvo.getPk_goodsspec(),
					uuid, 60);
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();

			sql.append("UPDATE cn_stocknum \n");
			sql.append("   SET isellnum = nvl(isellnum,0) - ?  \n");
			spm.addParam(bvo.getAmount());
			sql.append(" WHERE nvl(dr,0) = 0 \n");
			sql.append("   AND pk_corp = ? \n");
			spm.addParam(numvo.getPk_corp());
			sql.append("   AND pk_stocknum = ?  \n");
			spm.addParam(numvo.getPk_stocknum());
			int res = singleObjectBO.executeUpdate(sql.toString(), spm);
			if (res != 1) {
				throw new BusinessException("入库单数量更新错误");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(numvo.getTableName(), numvo.getPk_goods() + "" + numvo.getPk_goodsspec(),
					uuid);
		}
	}

	/**
	 * 更新取消确认数据
	 * 
	 * @param pamvo
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsBillVO updateCancelConf(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		carryover.checkIsOper(new DZFDateTime(pamvo.getDconfdate().getMillis()),2);
		checkStockOut(pamvo);
		Map<String, ChnBalanceVO> map = getBanlanceMap(pamvo, 2);
		if (CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL) > 0) {// 返点扣款
			updateRebBanlanceSub(pamvo, map, cuserid);
		} else if (CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL) < 0) {
			throw new BusinessException("返点扣款错误");
		}
		if (CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) > 0) {// 预付款扣款
			updateDedBanlanceSub(pamvo, map, cuserid);
		} else if (CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) < 0) {
			throw new BusinessException("预付款扣款错误");
		}
		pamvo.setVstatus(IStatusConstant.IORDERSTATUS_0);// 待确认
		singleObjectBO.update(pamvo, new String[] { "vstatus" });

		saveOperDetailSub(pamvo, cuserid);
		return pamvo;
	}
	
	/**
	 * 取消确认前，订单商品出库状态校验
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void checkStockOut(GoodsBillVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.* \n");
		sql.append("  FROM cn_stockout t \n");
		sql.append("  LEFT JOIN cn_stockout_b b ON t.pk_stockout = b.pk_stockout \n");
		sql.append(" INNER JOIN cn_goodsbill_b g ON b.pk_goodsbill_b = g.pk_goodsbill_b \n");
		sql.append("  lEFT JOIN cn_goodsbill l ON g.pk_goodsbill = l.pk_goodsbill \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(b.dr, 0) = 0 \n");
		sql.append("   AND nvl(g.dr, 0) = 0 \n");
		sql.append("   AND nvl(l.dr, 0) = 0 \n");
		sql.append("   AND l.pk_goodsbill = ? \n");
		spm.addParam(pamvo.getPk_goodsbill());
		List<StockOutVO> list = (List<StockOutVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(StockOutVO.class));
		if(list != null && list.size() > 0){
			for(StockOutVO vo : list){
				if(vo.getVstatus() != null && vo.getVstatus() != 0){
					throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】已有商品出库，不允许取消确认；");
				}
			}
		}
	}

	/**
	 * 更新确认数据
	 * 
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsBillVO updateConfirm(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		carryover.checkIsOper(new DZFDateTime(pamvo.getDconfdate().getMillis()),1);
		Map<String, ChnBalanceVO> map = getBanlanceMap(pamvo, 1);
		if (CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL) > 0) {// 返点扣款
			updateRebBanlanceAdd(pamvo, map, cuserid);
		} else if (CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL) < 0) {
			throw new BusinessException("返点扣款错误");
		}
		if (CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) > 0) {// 预付款扣款
			updateDedBanlanceAdd(pamvo, map, cuserid);
		} else if (CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) < 0) {
			throw new BusinessException("预付款扣款错误");
		}
		pamvo.setVstatus(IStatusConstant.IORDERSTATUS_1);// 待发货
		pamvo.setUpdatets(new DZFDateTime());
		singleObjectBO.update(pamvo, new String[] { "vstatus" });

		saveOperDetailAdd(pamvo, cuserid);
		return pamvo;
	}

	/**
	 * 保存操作详情
	 * 
	 * @param pamvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void saveOperDetailSub(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		// 订单取消确认详情
//		GoodsBillSVO bsvo = new GoodsBillSVO();
//		bsvo.setPk_goodsbill(pamvo.getPk_goodsbill());
//		bsvo.setPk_corp(pamvo.getPk_corp());
//		bsvo.setVsaction(IStatusConstant.IORDERACTION_5);
//		bsvo.setVstatus(IStatusConstant.IORDERSTATUS_0);
//		bsvo.setVsdescribe(IStatusConstant.IORDERDESCRIBE_5);// 状态描述
//		bsvo.setCoperatorid(cuserid);
//		bsvo.setDoperatedate(new DZFDate());
//		bsvo.setDoperatetime(new DZFDateTime());
//		bsvo.setVnote("");// 处理说明
//		singleObjectBO.saveObject(bsvo.getPk_corp(), bsvo);
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("DELETE FROM cn_goodsbill_s \n") ;
		sql.append(" WHERE pk_goodsbill = ? \n") ; 
		spm.addParam(pamvo.getPk_goodsbill());
		sql.append("   AND pk_corp = ? \n") ; 
		spm.addParam(pamvo.getPk_corp());
		sql.append("   AND vsaction = ? \n") ; 
		spm.addParam(IStatusConstant.IORDERACTION_1);
		sql.append("   AND vstatus = ? \n") ; 
		spm.addParam(IStatusConstant.IORDERSTATUS_1);
		sql.append("   AND vsdescribe = ? \n");
		spm.addParam(IStatusConstant.IORDERDESCRIBE_1);
		int res = singleObjectBO.executeUpdate(sql.toString(), spm);
		if(res != 1){
			throw new BusinessException("商品订单操作详情记录错误");
		}
	}

	/**
	 * 保存操作详情
	 * 
	 * @param pamvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void saveOperDetailAdd(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		// 订单购买详情
		GoodsBillSVO bsvo = new GoodsBillSVO();
		bsvo.setPk_goodsbill(pamvo.getPk_goodsbill());
		bsvo.setPk_corp(pamvo.getPk_corp());
		bsvo.setVsaction(IStatusConstant.IORDERACTION_1);
		bsvo.setVstatus(IStatusConstant.IORDERSTATUS_1);
		bsvo.setVsdescribe(IStatusConstant.IORDERDESCRIBE_1);// 状态描述
		bsvo.setCoperatorid(cuserid);
		bsvo.setDoperatedate(new DZFDate());
		bsvo.setDoperatetime(new DZFDateTime());
		bsvo.setVnote("");// 处理说明
		singleObjectBO.saveObject(bsvo.getPk_corp(), bsvo);
	}

	/**
	 * 更新预付款余额（退款）
	 * 
	 * @param pamvo
	 * @param map
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateDedBanlanceSub(GoodsBillVO pamvo, Map<String, ChnBalanceVO> map, String cuserid)
			throws DZFWarpException {
		ChnBalanceVO balvo = map.get("payment");
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_2, uuid,
					120);
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			sql.append("UPDATE cn_balance l  \n");
			sql.append("   SET l.nusedmny = nvl(l.nusedmny,0) - ?  \n");
			spm.addParam(pamvo.getNdeductmny());
			sql.append(" WHERE nvl(l.dr,0) = 0 AND l.ipaytype = ?  \n");
			spm.addParam(IStatusConstant.IPAYTYPE_2);
			sql.append("   AND l.pk_corp = ?  \n");
			spm.addParam(pamvo.getPk_corp());
			int res = singleObjectBO.executeUpdate(sql.toString(), spm);
			if (res == 1) {
				sql = new StringBuffer();
				spm = new SQLParameter();
				sql.append("DELETE FROM cn_detail  \n");
				sql.append(" WHERE nvl(dr, 0) = 0  \n");
				sql.append("   AND pk_corp = ?  \n");
				spm.addParam(pamvo.getPk_corp());
				sql.append("   AND ipaytype = ?  \n");
				spm.addParam(IStatusConstant.IPAYTYPE_2);// 预付款
				sql.append("   AND pk_bill = ?  \n");
				spm.addParam(pamvo.getPk_goodsbill());
				sql.append("   AND iopertype = ?  \n");
				spm.addParam(IStatusConstant.IDETAILTYPE_5);// 商品购买
				res = singleObjectBO.executeUpdate(sql.toString(), spm);
				if (res != 1) {
					throw new BusinessException("付款余额表-明细表更新错误");
				}
			} else {
				throw new BusinessException("付款余额表-预付款余额不足");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_2, uuid);
		}
	}

	/**
	 * 更新预付款余额（扣款）
	 * 
	 * @param pamvo
	 * @param map
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateDedBanlanceAdd(GoodsBillVO pamvo, Map<String, ChnBalanceVO> map, String cuserid)
			throws DZFWarpException {
		ChnBalanceVO balvo = map.get("payment");
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_2, uuid,
					120);
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			sql.append("UPDATE cn_balance l  \n");
			sql.append("   SET l.nusedmny = nvl(l.nusedmny,0) + ?  \n");
			spm.addParam(pamvo.getNdeductmny());
			sql.append(" WHERE nvl(l.dr,0) = 0 AND l.ipaytype = ?  \n");
			spm.addParam(IStatusConstant.IPAYTYPE_2);
			sql.append("   AND l.pk_corp = ?  \n");
			spm.addParam(pamvo.getPk_corp());
			sql.append("   and nvl(npaymny,0) - nvl(l.nusedmny, 0) >= ? \n");
			spm.addParam(pamvo.getNdeductmny());
			int res = singleObjectBO.executeUpdate(sql.toString(), spm);
			if (res == 1) {
				ChnDetailVO detvo = new ChnDetailVO();
				detvo.setPk_corp(pamvo.getPk_corp());
				detvo.setNusedmny(pamvo.getNdeductmny());
				detvo.setIpaytype(IStatusConstant.IPAYTYPE_2);// 预付款
				detvo.setPk_bill(pamvo.getPk_goodsbill());
				detvo.setVmemo("商品购买：" + pamvo.getVbillcode());
				detvo.setCoperatorid(cuserid);
				detvo.setDoperatedate(new DZFDate());
				detvo.setDr(0);
				detvo.setIopertype(IStatusConstant.IDETAILTYPE_5);// 商品购买
				singleObjectBO.saveObject("000001", detvo);
			} else {
				throw new BusinessException("付款余额表-预付款余额不足");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_2, uuid);
		}
	}

	/**
	 * 更新返点余额（退款）
	 * 
	 * @param pamvo
	 * @param map
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateRebBanlanceSub(GoodsBillVO pamvo, Map<String, ChnBalanceVO> map, String cuserid)
			throws DZFWarpException {
		ChnBalanceVO balvo = map.get("rebate");
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_3, uuid,
					120);
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			sql.append("UPDATE cn_balance l  \n");
			sql.append("   SET l.nusedmny = nvl(l.nusedmny,0) - ?  \n");
			spm.addParam(pamvo.getNdedrebamny());
			sql.append(" WHERE nvl(l.dr,0) = 0 AND l.ipaytype = ?  \n");
			spm.addParam(IStatusConstant.IPAYTYPE_3);
			sql.append("   AND l.pk_corp = ?  \n");
			spm.addParam(pamvo.getPk_corp());
			int res = singleObjectBO.executeUpdate(sql.toString(), spm);
			if (res == 1) {
				sql = new StringBuffer();
				spm = new SQLParameter();
				sql.append("DELETE FROM cn_detail  \n");
				sql.append(" WHERE nvl(dr, 0) = 0  \n");
				sql.append("   AND pk_corp = ?  \n");
				spm.addParam(pamvo.getPk_corp());
				sql.append("   AND ipaytype = ?  \n");
				spm.addParam(IStatusConstant.IPAYTYPE_3);// 返点款
				sql.append("   AND pk_bill = ?  \n");
				spm.addParam(pamvo.getPk_goodsbill());
				sql.append("   AND iopertype = ?  \n");
				spm.addParam(IStatusConstant.IDETAILTYPE_5);// 商品购买
				res = singleObjectBO.executeUpdate(sql.toString(), spm);
				if (res != 1) {
					throw new BusinessException("付款余额表-明细表更新错误");
				}
			} else {
				throw new BusinessException("付款余额表-返点款余额不足");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_3, uuid);
		}
	}

	/**
	 * 更新返点余额（扣款）
	 * 
	 * @param pamvo
	 * @param map
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateRebBanlanceAdd(GoodsBillVO pamvo, Map<String, ChnBalanceVO> map, String cuserid)
			throws DZFWarpException {
		ChnBalanceVO balvo = map.get("rebate");
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_3, uuid,
					120);
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			sql.append("UPDATE cn_balance l  \n");
			sql.append("   SET l.nusedmny = nvl(l.nusedmny,0) + ?  \n");
			spm.addParam(pamvo.getNdedrebamny());
			sql.append(" WHERE nvl(l.dr,0) = 0 AND l.ipaytype = ?  \n");
			spm.addParam(IStatusConstant.IPAYTYPE_3);
			sql.append("   AND l.pk_corp = ?  \n");
			spm.addParam(pamvo.getPk_corp());
			sql.append("   and nvl(npaymny,0) - nvl(l.nusedmny, 0) >= ? \n");
			spm.addParam(pamvo.getNdedrebamny());
			int res = singleObjectBO.executeUpdate(sql.toString(), spm);
			if (res == 1) {
				ChnDetailVO detvo = new ChnDetailVO();
				detvo.setPk_corp(pamvo.getPk_corp());
				detvo.setNusedmny(pamvo.getNdedrebamny());
				detvo.setIpaytype(IStatusConstant.IPAYTYPE_3);// 返点款
				detvo.setPk_bill(pamvo.getPk_goodsbill());
				detvo.setVmemo("商品购买：" + pamvo.getVbillcode());
				detvo.setCoperatorid(cuserid);
				detvo.setDoperatedate(new DZFDate());
				detvo.setDr(0);
				detvo.setIopertype(IStatusConstant.IDETAILTYPE_5);// 商品购买
				singleObjectBO.saveObject("000001", detvo);
			} else {
				throw new BusinessException("付款余额表-返点款余额不足");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_balance", balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_3, uuid);
		}
	}

	/**
	 * 获取余额信息
	 * @param pamvo
	 * @param checktype  1:扣款；2：退款；
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, ChnBalanceVO> getBanlanceMap(GoodsBillVO pamvo, Integer checktype) throws DZFWarpException {
		Map<String, ChnBalanceVO> map = new HashMap<String, ChnBalanceVO>();
		String yesql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype in (?,?) ";
		SQLParameter yespm = new SQLParameter();
		yespm.addParam(pamvo.getPk_corp());
		yespm.addParam(IStatusConstant.IPAYTYPE_3);
		yespm.addParam(IStatusConstant.IPAYTYPE_2);
		ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, yesql, yespm);
		String corpname = "";
		CorpVO corpvo = CorpCache.getInstance().get(null, pamvo.getPk_corp());
		if (corpvo != null) {
			corpname = corpvo.getUnitname();
		}
		if (balVOs != null && balVOs.length > 0) {
			DZFDouble balance = DZFDouble.ZERO_DBL;
			for (ChnBalanceVO balvo : balVOs) {
				balance = SafeCompute.sub(balvo.getNpaymny(), balvo.getNusedmny());
				if (balvo.getIpaytype() != null && balvo.getIpaytype() == IStatusConstant.IPAYTYPE_3) {
					if (CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL) != 0) {
						if(checktype == 1){
							if (CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(balance) > 0) {
								throw new BusinessException("确认失败！加盟商" + corpname + "账户返点余额不足");
							}
						}
						map.put("rebate", balvo);
					}
				} else if (balvo.getIpaytype() != null && balvo.getIpaytype() == IStatusConstant.IPAYTYPE_2) {
					if (CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) != 0) {
						if(checktype == 1){
							if (CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(balance) > 0) {
								throw new BusinessException("确认失败！加盟商" + corpname + "账户预付款余额不足");
							}
						}
						map.put("payment", balvo);
					}
				}
			}
		} else {
			throw new BusinessException("确认失败！加盟商" + corpname + "账户余额不足");
		}

		return map;
	}

	/**
	 * 操作前数据校验
	 * 
	 * @param pamvo
	 * @param type
	 *            1：确认；2：取消订单；3：取消确认；4：发票申请；
	 * @throws DZFWarpException
	 */
	private void checkData(GoodsBillVO pamvo, Integer type) throws DZFWarpException {
		GoodsBillVO oldvo = (GoodsBillVO) singleObjectBO.queryByPrimaryKey(GoodsBillVO.class, pamvo.getPk_goodsbill());
		if (oldvo != null) {
			if (pamvo.getUpdatets().compareTo(oldvo.getUpdatets()) != 0) {
				throw new BusinessException("界面数据发生变化，请刷新后再次尝试");
			}
			if(type != null){
				if (type == 1 || type == 2) {
					if (oldvo.getVstatus() != null && oldvo.getVstatus() != 0) {
						throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】状态不为待确认；");
					}
				} else if (type == 3) {
					if (oldvo.getVstatus() != null && oldvo.getVstatus() != 1) {
						throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】状态不为待发货；");
					}
					if(oldvo.getVtistatus() != null && oldvo.getVtistatus() == 2){
						throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】开票状态为已开票，不允许取消确认；");
					}
				} else if(type == 4){
					if(oldvo.getVtistatus() != null && oldvo.getVtistatus() == 2){
						throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】开票状态为已开票，不允许再次开票；");
					}
					if (oldvo.getVstatus() != null && oldvo.getVstatus() == 0) {
						throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】状态为待确认，不能开票；");
					}
					if (oldvo.getVstatus() != null && oldvo.getVstatus() == 4) {
						throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】状态为已取消，不能开票；");
					}
				}
			} else {
				throw new BusinessException("操作类型不能为空");
			}
		} else {
			throw new BusinessException("订单：【" + pamvo.getVbillcode() + "】数据错误");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public GoodsBillVO qryOrderDet(GoodsBillVO pamvo) throws DZFWarpException {
		GoodsBillVO gvo = null;
		List<GoodsBillVO> list = query(pamvo);
		if (list != null && list.size() > 0) {
			gvo = list.get(0);
		} else {
			throw new BusinessException("商品订单信息错误");
		}
		// 查询订单流程详情
		String qsql = " nvl(dr,0) = 0 AND pk_goodsbill = ? ORDER BY ts ASC";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pamvo.getPk_goodsbill());
		GoodsBillSVO[] sVOs = (GoodsBillSVO[]) singleObjectBO.queryByCondition(GoodsBillSVO.class, qsql, spm);
		if (sVOs != null && sVOs.length > 0) {
			gvo.setDetail(sVOs);
		}
		// 查询订单商品详情
		StringBuffer sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("select b.*,ss.pk_goodsdoc,ss.vnote,ss.vfilepath ");
		sql.append("  from cn_goodsbill_b b ");
		sql.append("  left join (select c.pk_goods pk_goods,c.pk_goodsdoc,s.vnote,c.vfilepath ");
		sql.append("               from cn_goodsdoc c ");
		sql.append("              inner join cn_goods s on s.pk_goods = c.pk_goods ");
		sql.append("              where c.docTime in ");
		sql.append("                    (select min(docTime) from cn_goodsdoc group by pk_goods)) ss ");
		sql.append("   on b.pk_goods = ss.pk_goods ");
		sql.append("   where nvl(b.dr,0) = 0 and b.pk_goodsbill = ? ");
		spm.addParam(pamvo.getPk_goodsbill());
		List<GoodsBillBVO> blist = (List<GoodsBillBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(GoodsBillBVO.class));
		if (blist != null && blist.size() > 0) {
			gvo.setGoods(blist.toArray(new GoodsBillBVO[0]));
		}
		return gvo;
	}

}
