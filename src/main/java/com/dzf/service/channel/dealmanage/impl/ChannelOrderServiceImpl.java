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
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.dealmanage.GoodsBillBVO;
import com.dzf.model.channel.dealmanage.GoodsBillSVO;
import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.service.channel.dealmanage.IChannelOrderService;

@Service("channelorderser")
public class ChannelOrderServiceImpl implements IChannelOrderService {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Integer queryTotalRow(GoodsBillVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(GoodsBillVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsBillVO> query(GoodsBillVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		List<GoodsBillVO> list = (List<GoodsBillVO>) multBodyObjectBO.queryDataPage(GoodsBillVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if(list != null && list.size() > 0){
			setShowValue(list);
		}
		return list;
	}
	
	/**
	 * 设置显示名称
	 * @throws DZFWarpException
	 */
	private void setShowValue(List<GoodsBillVO> list) throws DZFWarpException {
		CorpVO corpvo = null;
		for(GoodsBillVO bvo : list){
			corpvo = CorpCache.getInstance().get(null, bvo.getPk_corp());
			if(corpvo != null){
				bvo.setCorpcode(corpvo.getInnercode());
				bvo.setCorpname(corpvo.getUnitname());
			}
		}
	}
	
	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(GoodsBillVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT l.pk_goodsbill,  \n") ;
		sql.append("       l.vbillcode,  \n") ; 
		sql.append("       l.vreceivername,  \n") ; 
		sql.append("       l.phone,  \n") ; 
		sql.append("       l.vzipcode,  \n") ; 
		sql.append("       l.vreceiveaddress,  \n") ; 
		sql.append("       l.pk_corp,  \n") ; 
		sql.append("       l.ndedsummny,  \n") ; 
		sql.append("       l.ndeductmny,  \n") ; 
		sql.append("       l.ndedrebamny,  \n") ; 
		sql.append("       l.vstatus,  \n") ; 
		sql.append("       t.logisticsunit,  \n") ; 
		sql.append("       t.fastcode,  \n") ; 
		sql.append("       s.doperatetime AS dsubmittime  \n") ; 
		sql.append("  FROM cn_goodsbill l  \n") ; 
		sql.append("  LEFT JOIN cn_goodsbill_s s ON l.pk_goodsbill = s.pk_goodsbill  \n") ; 
		sql.append("                            AND s.vstatus = 0  \n") ; 
		sql.append("  LEFT JOIN cn_goodsbill_s t ON l.pk_goodsbill = t.pk_goodsbill  \n") ; 
		sql.append("                            AND t.vstatus = 2  \n") ; 
		sql.append(" WHERE nvl(l.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(s.dr, 0) = 0  \n") ; 
		if(!StringUtil.isEmpty(pamvo.getVbillcode())){
			sql.append("   AND l.vbillcode = ?  \n") ; 
			spm.addParam(pamvo.getVbillcode());
		}
		if(!StringUtil.isEmpty(pamvo.getPk_corp())){
			sql.append("   AND l.pk_corp = ?  \n") ; 
			spm.addParam(pamvo.getPk_corp());
		}
		if(pamvo.getVstatus() != null && pamvo.getVstatus() != -1){
			sql.append("   AND l.vstatus = ? \n");
			spm.addParam(pamvo.getVstatus());
		}
		if(!StringUtil.isEmpty(pamvo.getPk_goodsbill())){
			sql.append(" AND l.pk_goodsbill = ? ");
			spm.addParam(pamvo.getPk_goodsbill());
		}
		sql.append(" ORDER BY s.doperatetime DESC");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	/**
	 * 
	 * @param pamvo
	 * @param type   1：确认；2：取消订单；3：商品发货；
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public GoodsBillVO updateData(GoodsBillVO pamvo, Integer type, String cuserid) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(pamvo.getTableName(), pamvo.getPk_goodsbill(), uuid, 120);
			
			checkData(pamvo, type);
			if(type != null && type == 1){
				return updateConfirm(pamvo, cuserid);
			}else if(type != null && type == 2){
				return updateCancel(pamvo, cuserid);
			}else if(type != null && type == 3){
				return updateSetOut(pamvo, cuserid);
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
	 * 更新商品发货信息
	 * @param pamvo
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsBillVO updateSetOut(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		pamvo.setVstatus(IStatusConstant.IORDERSTATUS_2);//已发货
		pamvo.setUpdatets(new DZFDateTime());
		singleObjectBO.update(pamvo, new String[]{"vstatus", "updatets"});
		
		//订单购买详情
		GoodsBillSVO bsvo = new GoodsBillSVO();
		bsvo.setPk_goodsbill(pamvo.getPk_goodsbill());
		bsvo.setPk_corp(pamvo.getPk_corp());
		bsvo.setVsaction(IStatusConstant.IORDERACTION_2);
		bsvo.setVstatus(IStatusConstant.IORDERSTATUS_2);
		bsvo.setVsdescribe(IStatusConstant.IORDERDESCRIBE_2);//状态描述
		bsvo.setCoperatorid(cuserid);
		bsvo.setDoperatedate(new DZFDate());
		bsvo.setDoperatetime(new DZFDateTime());
		bsvo.setLogisticsunit(pamvo.getLogisticsunit());//物流公司
		bsvo.setFastcode(pamvo.getFastcode());//物流单号
		singleObjectBO.saveObject(bsvo.getPk_corp(), bsvo);
		return pamvo;
	}
	
	/**
	 * 更新取消订单数据
	 * @param pamvo
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsBillVO updateCancel(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		pamvo.setVstatus(IStatusConstant.IORDERSTATUS_4);//已取消
		pamvo.setUpdatets(new DZFDateTime());
		singleObjectBO.update(pamvo, new String[]{"vstatus", "updatets"});
		
		//订单购买详情
		GoodsBillSVO bsvo = new GoodsBillSVO();
		bsvo.setPk_goodsbill(pamvo.getPk_goodsbill());
		bsvo.setPk_corp(pamvo.getPk_corp());
		bsvo.setVsaction(IStatusConstant.IORDERACTION_4);
		bsvo.setVstatus(IStatusConstant.IORDERSTATUS_4);
		bsvo.setVsdescribe(IStatusConstant.IORDERDESCRIBE_4);//状态描述
		bsvo.setCoperatorid(cuserid);
		bsvo.setDoperatedate(new DZFDate());
		bsvo.setDoperatetime(new DZFDateTime());
		bsvo.setVnote(pamvo.getVrejereason());//处理说明
		singleObjectBO.saveObject(bsvo.getPk_corp(), bsvo);
		return pamvo;
	}
	
	/**
	 * 更新确认数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsBillVO updateConfirm(GoodsBillVO pamvo, String cuserid) throws DZFWarpException {
		Map<String, ChnBalanceVO> map = getBanlanceMap(pamvo);
		if(CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL) != 0){//返点扣款
			ChnBalanceVO balvo = map.get("rebate");
			String uuid = UUID.randomUUID().toString();
			try {
				LockUtil.getInstance().tryLockKey("cn_balance",
						balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_3, uuid, 120);
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
					detvo.setVmemo("商品购买："+pamvo.getVbillcode());
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
				LockUtil.getInstance().unLock_Key("cn_balance",
						balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_3, uuid);
			}
		}
		if(CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) != 0){//预付款扣款
			ChnBalanceVO balvo = map.get("payment");
			String uuid = UUID.randomUUID().toString();
			try {
				LockUtil.getInstance().tryLockKey("cn_balance",
						balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_2, uuid, 120);
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
					detvo.setVmemo("商品购买："+pamvo.getVbillcode());
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
				LockUtil.getInstance().unLock_Key("cn_balance",
						balvo.getPk_corp() + "" + IStatusConstant.IPAYTYPE_2, uuid);
			}
		}
		pamvo.setVstatus(IStatusConstant.IORDERSTATUS_1);//待发货
		pamvo.setUpdatets(new DZFDateTime());
		singleObjectBO.update(pamvo, new String[]{"vstatus", "updatets"});
		
		//订单购买详情
		GoodsBillSVO bsvo = new GoodsBillSVO();
		bsvo.setPk_goodsbill(pamvo.getPk_goodsbill());
		bsvo.setPk_corp(pamvo.getPk_corp());
		bsvo.setVsaction(IStatusConstant.IORDERACTION_1);
		bsvo.setVstatus(IStatusConstant.IORDERSTATUS_1);
		bsvo.setVsdescribe(IStatusConstant.IORDERDESCRIBE_1);//状态描述
		bsvo.setCoperatorid(cuserid);
		bsvo.setDoperatedate(new DZFDate());
		bsvo.setDoperatetime(new DZFDateTime());
		bsvo.setVnote("");//处理说明
		singleObjectBO.saveObject(bsvo.getPk_corp(), bsvo);
		return pamvo;
	}
	
	/**
	 * 获取余额信息
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, ChnBalanceVO> getBanlanceMap(GoodsBillVO pamvo) throws DZFWarpException {
		Map<String, ChnBalanceVO> map = new HashMap<String, ChnBalanceVO>();
		String yesql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype in (?,?) ";
		SQLParameter yespm = new SQLParameter();
		yespm.addParam(pamvo.getPk_corp());
		yespm.addParam(IStatusConstant.IPAYTYPE_3);
		yespm.addParam(IStatusConstant.IPAYTYPE_2);
		ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, yesql, yespm);
		String corpname = "";
		CorpVO corpvo = CorpCache.getInstance().get(null, pamvo.getPk_corp());
		if(corpvo != null){
			corpname = corpvo.getUnitname();
		}
		if (balVOs != null && balVOs.length > 0) {
			DZFDouble balance = DZFDouble.ZERO_DBL;
			for(ChnBalanceVO balvo : balVOs){
				balance = SafeCompute.sub(balvo.getNpaymny(), balvo.getNusedmny());
				if(balvo.getIpaytype() != null && balvo.getIpaytype() == IStatusConstant.IPAYTYPE_3){
					if(CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(DZFDouble.ZERO_DBL) != 0){
						if(CommonUtil.getDZFDouble(pamvo.getNdedrebamny()).compareTo(balance) > 0){
							throw new BusinessException("确认失败！加盟商"+corpname+"账户返点余额不足");
						}
						map.put("rebate", balvo);
					}
				}else if(balvo.getIpaytype() != null && balvo.getIpaytype() == IStatusConstant.IPAYTYPE_2){
					if(CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(DZFDouble.ZERO_DBL) != 0){
						if(CommonUtil.getDZFDouble(pamvo.getNdeductmny()).compareTo(balance) > 0){
							throw new BusinessException("确认失败！加盟商"+corpname+"账户预付款余额不足");
						}
						map.put("payment", balvo);
					}
				}
			}
		} else {
			throw new BusinessException("确认失败！加盟商"+corpname+"账户余额不足");
		}
		
		return map;
	}
	
	
	/**
	 * 操作前数据校验
	 * @param pamvo
	 * @param type   1：确认；2：取消订单；3：商品发货；
	 * @throws DZFWarpException
	 */
	private void checkData(GoodsBillVO pamvo, Integer type) throws DZFWarpException {
		GoodsBillVO oldvo = (GoodsBillVO) singleObjectBO.queryByPrimaryKey(GoodsBillVO.class, pamvo.getPk_goodsbill());
		if(oldvo != null){
			if(type != null && (type == 1 || type == 2) ){
				if(oldvo.getVstatus() != null && oldvo.getVstatus() != 0){
					throw new BusinessException("订单：【"+pamvo.getVbillcode()+"】状态不为待确认");
				}
			}else if(type != null && type == 3){
				if(oldvo.getVstatus() != null && oldvo.getVstatus() != 1){
					throw new BusinessException("订单：【"+pamvo.getVbillcode()+"】状态不为待发货");
				}
			}
		}else{
			throw new BusinessException("订单：【"+pamvo.getVbillcode()+"】数据错误");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public GoodsBillVO qryOrderDet(GoodsBillVO pamvo) throws DZFWarpException {
		GoodsBillVO gvo = null;
		List<GoodsBillVO> list = query(pamvo); 
		if(list != null && list.size() > 0){
			gvo = list.get(0);
		}else{
			throw new BusinessException("商品订单信息错误");
		}
		//查询订单流程详情
		String qsql = " nvl(dr,0) = 0 AND pk_goodsbill = ? ORDER BY ts ASC";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pamvo.getPk_goodsbill());
		GoodsBillSVO[] sVOs = (GoodsBillSVO[]) singleObjectBO.queryByCondition(GoodsBillSVO.class, qsql, spm);
		if(sVOs != null && sVOs.length > 0){
			gvo.setDetail(sVOs);
		}
		//查询订单商品详情
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
		if(blist != null && blist.size() > 0){
			gvo.setGoods(blist.toArray(new GoodsBillBVO[0]));
		}
		return gvo;
	}

}
