package com.dzf.service.channel.sys_power.impl;

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
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.sys_power.DeductRateLogVO;
import com.dzf.model.channel.sys_power.DeductRateVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.sys_power.IDeductRateService;
import com.dzf.service.pub.IPubService;

@Service("deductrateser")
public class DeductRateServiceImpl implements IDeductRateService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IPubService pubService;

	@Override
	public Integer queryTotalRow(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(DeductRateVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeductRateVO> query(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(pamvo);
		List<DeductRateVO> list = (List<DeductRateVO>) multBodyObjectBO.queryDataPage(DeductRateVO.class, sqpvo.getSql(),
				sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		Map<Integer, String> areaMap = pubService.getAreaMap(pamvo.getAreaname(),1);
		Map<String, UserVO> marmap = pubService.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if (list != null && list.size() > 0) {
			setShowName(list,areaMap,marmap);
		}
		
		return list;
	}
	
	/**
	 * 设置显示名称
	 * @param list
	 * @param areaMap 
	 * @param marmap 
	 * @throws DZFWarpException
	 */
	private void setShowName(List<DeductRateVO> list, Map<Integer, String> areaMap, Map<String, UserVO> marmap) throws DZFWarpException {
		UserVO uservo = null;
		CorpVO deduvo = null;
		for(DeductRateVO rvo : list){
			deduvo = CorpCache.getInstance().get(null, rvo.getPk_corp());
			uservo = marmap.get(rvo.getPk_corp());
			rvo.setCorpname(CodeUtils1.deCode(rvo.getCorpname()));
			if(StringUtil.isEmpty(rvo.getPk_deductrate())){
				//1：普通加盟商；2：金牌加盟商；
				if(rvo.getChanneltype() != null && rvo.getChanneltype() == 1){
					rvo.setInewrate(10);
					rvo.setIrenewrate(10);
				}else if(rvo.getChanneltype() != null && rvo.getChanneltype() == 2){
					rvo.setInewrate(10);
					rvo.setIrenewrate(10);
				}
			}
			if (deduvo != null) {
				rvo.setVprovname(deduvo.getCitycounty());
			}
			if(areaMap.containsKey(rvo.getVprovince())){
				rvo.setAreaname(areaMap.get(rvo.getVprovince()));
			}
			if (uservo != null) {
				rvo.setVmanagername(uservo.getUser_name());// 渠道经理
			}
			rvo.setLastmodifypsn(CodeUtils1.deCode(rvo.getLastmodifypsn()));
		}
	}
	
	/**
	 * 获取查询条件
	 * 
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT account.pk_corp,    ");
		sql.append("       account.fathercorp,    ");
		sql.append("       account.innercode AS corpcode,    ");
		sql.append("       account.unitname AS corpname,    ");
		sql.append("       account.channeltype,    ");
		sql.append("       account.vprovince,     ");
		sql.append("       d.pk_deductrate,    "); 
		sql.append("       d.inewrate,    "); 
		sql.append("       d.irenewrate,    "); 
		sql.append("       d.lastmodifypsnid,    "); 
		sql.append("       u.user_name lastmodifypsn,    "); 
		sql.append("       d.lastmodifydate    ") ; 
		sql.append("  FROM bd_account account    ");
		sql.append("  LEFT JOIN cn_deductrate d ON account.pk_corp = d.pk_corp    ");
		sql.append("  LEFT JOIN sm_user u ON d.lastmodifypsnid = u.cuserid    ") ; 
		sql.append(" WHERE ").append(QueryUtil.getWhereSql());
		sql.append("   AND nvl(d.dr, 0) = 0    "); 
		sql.append("   AND account.fathercorp = '000001'    ");
		sql.append("   AND (account.drelievedate IS NULL OR account.drelievedate > ?)");
		spm.addParam(String.valueOf(new DZFDate()));
		sql.append(" ORDER BY account.innercode ASC");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeductRateVO> queryAllData(QryParamVO pamvo) throws DZFWarpException {
		List<DeductRateVO> retlist = new ArrayList<DeductRateVO>();
		QrySqlSpmVO sqpvo = getQrySqlSpm(pamvo);
		List<DeductRateVO> list = (List<DeductRateVO>) singleObjectBO.executeQuery(sqpvo.getSql(), sqpvo.getSpm(),
				new BeanListProcessor(DeductRateVO.class));
		Map<Integer, String> areaMap = pubService.getAreaMap(pamvo.getAreaname(),1);
		Map<String, UserVO> marmap = pubService.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if(list != null && list.size() > 0){
			for(DeductRateVO rvo : list){
				rvo.setCorpname(CodeUtils1.deCode(rvo.getCorpname()));
				if(StringUtil.isEmpty(rvo.getPk_deductrate())){
					//1：普通加盟商；2：金牌加盟商；
					if(rvo.getChanneltype() != null && rvo.getChanneltype() == 1){
						rvo.setInewrate(10);
						rvo.setIrenewrate(10);
					}else if(rvo.getChanneltype() != null && rvo.getChanneltype() == 2){
						rvo.setInewrate(10);
						rvo.setIrenewrate(10);
					}
				}
				if(rvo.getCorpcode().indexOf(pamvo.getCorpcode()) != -1 
						|| rvo.getCorpname().indexOf(pamvo.getCorpcode()) != -1){
					retlist.add(rvo);
				}
			}
		}
		if (retlist != null && retlist.size() > 0) {
			setShowName(retlist,areaMap,marmap);
		}
		return retlist;
	}

	@Override
	public DeductRateVO saveImport(DeductRateVO ratevo, Map<String, String> map, UserVO uservo) throws DZFWarpException {
		String fathercorp = uservo.getPk_corp(); 	
		String cuserid = uservo.getCuserid();
		String cusername = uservo.getUser_name();
		if (map != null && !map.isEmpty()) {
			String pk_corp = map.get(ratevo.getCorpcode());
			if (!StringUtil.isEmpty(pk_corp)) {
				DeductRateVO oldvo = queryDeductByField("pk_corp", pk_corp);
				if(oldvo == null){
					ratevo.setPk_corp(pk_corp);
					ratevo.setCoperatorid(cuserid);
					ratevo.setDoperatedate(new DZFDateTime());
					ratevo.setLastmodifypsnid(cuserid);
					ratevo.setLastmodifydate(new DZFDateTime());
					ratevo.setFathercorp(fathercorp);
					ratevo.setDr(0);
					ratevo = saveData(ratevo, fathercorp, cuserid);
					ratevo.setLastmodifypsn(cusername);
					setShowName(ratevo);
					return ratevo;
				}else{
					oldvo.setInewrate(ratevo.getInewrate());
					oldvo.setIrenewrate(ratevo.getIrenewrate());
					oldvo.setLastmodifypsnid(cuserid);
					oldvo.setLastmodifydate(new DZFDateTime());
					oldvo.setFathercorp(fathercorp);
					oldvo = saveData(oldvo, fathercorp, cuserid);
					ratevo.setLastmodifypsn(cusername);
					setShowName(oldvo);
					return oldvo;
				}
			} else {
				throw new BusinessException("加盟商" + ratevo.getCorpcode() + "信息错误");
			}
		} else {
			throw new BusinessException("加盟商信息错误");
		}
	}
	
	/**
	 * 设置显示名称
	 * @param ratevo
	 * @throws DZFWarpException
	 */
	private void setShowName(DeductRateVO ratevo) throws DZFWarpException {
		CorpVO corpvo = CorpCache.getInstance().get(null, ratevo.getPk_corp());
		if(corpvo != null){
			ratevo.setCorpcode(corpvo.getInnercode());
			ratevo.setCorpname(corpvo.getUnitname());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DeductRateVO queryDeductByField(String field, String value) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *    ");
		sql.append("  FROM cn_deductrate    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		sql.append("   AND ").append(field).append(" = ?   ");
		spm.addParam(value);
		List<DeductRateVO> list = (List<DeductRateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(DeductRateVO.class));
		if(list != null && list.size() > 0){
			return list.get(0);
		}else if(list != null && list.size() > 1){
			throw new BusinessException("扣款率错误");
		}
		return null;
	}

	@Override
	public DeductRateVO save(DeductRateVO ratevo, String fathercorp, String cuserid) throws DZFWarpException {
		if(StringUtil.isEmpty(ratevo.getPk_deductrate())){//主键为空，从数据库重新查询，防止存储重复数据
			DeductRateVO oldvo = queryDeductByField("pk_corp", ratevo.getPk_corp());
			if(oldvo == null){
				ratevo.setFathercorp(fathercorp);
				ratevo.setCoperatorid(cuserid);
				ratevo.setDoperatedate(new DZFDateTime());
				ratevo.setDr(0);
				ratevo.setLastmodifypsnid(cuserid);
				ratevo.setLastmodifydate(new DZFDateTime());
				return saveData(ratevo, fathercorp, cuserid);
			}else{
				oldvo.setInewrate(ratevo.getInewrate());
				oldvo.setIrenewrate(ratevo.getIrenewrate());
				oldvo.setLastmodifypsnid(cuserid);
				oldvo.setLastmodifydate(new DZFDateTime());
				return saveData(ratevo, fathercorp, cuserid);
			}
		}else{
			ratevo.setLastmodifypsnid(cuserid);
			ratevo.setLastmodifydate(new DZFDateTime());
			return saveData(ratevo, fathercorp, cuserid);
		}
	}

	/**
	 * 保存数据
	 * @param ratevo
	 * @param fathercorp
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private DeductRateVO saveData(DeductRateVO ratevo, String fathercorp, String cuserid) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(ratevo.getTableName(), ratevo.getPk_corp(), uuid, 60);
			ratevo = (DeductRateVO) singleObjectBO.saveObject(fathercorp, ratevo);
			saveLog(ratevo, cuserid);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(ratevo.getTableName(), ratevo.getPk_corp(), uuid);
		}
		return ratevo;
	}
	
	/**
	 * 保存操作日志
	 * @param ratevo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void saveLog(DeductRateVO ratevo, String cuserid) throws DZFWarpException {
		DeductRateLogVO logvo = new DeductRateLogVO();
		logvo.setInewrate(ratevo.getInewrate());
		logvo.setIrenewrate(ratevo.getIrenewrate());
		logvo.setPk_deductrate(ratevo.getPk_deductrate());
		logvo.setFathercorp(ratevo.getFathercorp());
		logvo.setPk_corp(ratevo.getPk_corp());
		logvo.setCoperatorid(cuserid);
		logvo.setDoperatedate(new DZFDateTime());
		logvo.setDr(0);
		singleObjectBO.saveObject(logvo.getFathercorp(), logvo);
	}
	
	/**
	 * 保存前校验
	 * @param ratevo
	 * @throws DZFWarpException
	 */
	private void checkBeforeSave(DeductRateVO ratevo) throws DZFWarpException {
		DeductRateVO oldvo = queryDeductByField("pk_deductrate", ratevo.getPk_deductrate());
		if(oldvo.getUpdatets().compareTo(ratevo.getUpdatets()) != 0){
			throw new BusinessException("加盟商"+ratevo.getCorpname()+"数据发生变化，请刷新界面数据后，再次尝试");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryCorpMap() throws DZFWarpException {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT pk_corp, innercode   ");
		sql.append("  FROM bd_account acc    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		sql.append("   AND nvl(ischannel, 'N') = 'Y'    ");
		sql.append("   AND (drelievedate IS NULL OR drelievedate > ?)   ");
		spm.addParam(String.valueOf(new DZFDate()));
		List<AccountVO> list = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountVO.class));
		if(list != null && list.size() > 0){
			for(AccountVO vo : list){
				map.put(vo.getInnercode(), vo.getPk_corp());
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeductRateLogVO> queryLog(String fathercorp, String pk_deductrate) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT g.inewrate,g.irenewrate,g.coperatorid,g.doperatedate,u.user_name coperator   ");
		sql.append("  FROM cn_deductratelog g   ");
		sql.append("  LEFT JOIN sm_user u ON g.coperatorid = u.cuserid    ") ; 
		sql.append(" WHERE nvl(g.dr, 0) = 0    ");
		sql.append("   AND g.fathercorp = ?    ");
		spm.addParam(fathercorp);
		sql.append("   AND g.pk_deductrate = ?   ");
		spm.addParam(pk_deductrate);
		sql.append(" ORDER BY g.ts DESC");
		List<DeductRateLogVO> list = (List<DeductRateLogVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(DeductRateLogVO.class));
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"coperator"}, list, 1);
		}
		return list;
	}

}
