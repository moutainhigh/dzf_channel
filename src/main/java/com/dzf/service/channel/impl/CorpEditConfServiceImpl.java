package com.dzf.service.channel.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.CorpNameEVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.ICorpEditConfService;
import com.dzf.service.pub.IPubService;

@Service("corpeditconfser")
public class CorpEditConfServiceImpl implements ICorpEditConfService {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
    @Autowired
    private IPubService pubService;

	@Override
	public Integer queryTotalRow(QryParamVO paramvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo,uservo);
		return multBodyObjectBO.queryDataTotal(CorpNameEVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpNameEVO> query(QryParamVO paramvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo,uservo);
		List<CorpNameEVO> list = (List<CorpNameEVO>) multBodyObjectBO.queryDataPage(CorpNameEVO.class, 
					sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if(list != null && list.size() > 0){
			CorpVO corpvo = null;
			Map<Integer, String> areaMap = pubService.getAreaMap(paramvo.getAreaname(),3);
			for(CorpNameEVO vo : list){
				if(areaMap!=null && !areaMap.isEmpty()){
					String area = areaMap.get(vo.getVprovince());
					if(!StringUtil.isEmpty(area)){
						vo.setAreaname(area);
					}
				}
				corpvo = CorpCache.getInstance().get(null, vo.getFathercorp());
				if(corpvo != null){
					vo.setFathername(corpvo.getUnitname());
				}
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(corpvo != null){
					vo.setInnercode(corpvo.getInnercode());
				}
				vo.setVoldname(CodeUtils1.deCode(vo.getVoldname()));
				vo.setVnewname(CodeUtils1.deCode(vo.getVnewname()));
			}
		}
		return list;
	}
	
	/**
	 * 获取查询条件
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO paramvo, UserVO sd) throws DZFWarpException{
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" SELECT a.*,ba.vprovince FROM cn_corpnameedit a \n") ;
		sql.append(" LEFT JOIN bd_account ba on a.fathercorp=ba.pk_corp ");
		sql.append(" WHERE nvl(a.dr,0) = 0 and nvl(ba.dr,0) = 0 ");
    	if(!StringUtil.isEmpty(paramvo.getVqrysql())){
    		sql.append(paramvo.getVqrysql());
    	}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
		    String[] strs = paramvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND a.fathercorp in (").append(inSql).append(")");
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append("   AND a.istatus = ? \n");
			spm.addParam(paramvo.getQrytype());
		}else{
			sql.append("   AND a.istatus != ? \n");
			spm.addParam(IStatusConstant.ICORPEDITSTATUS_0);
		}
		if(paramvo.getBegdate() != null){
			sql.append("   AND substr(a.vsubmittime,1,10) >= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		if(paramvo.getEnddate() != null){
			sql.append("   AND substr(a.vsubmittime,1,10) <= ? \n");
			spm.addParam(paramvo.getEnddate());
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 更新数据
	 * @param datavo
	 * @param uservo
	 * @param opertype
	 * @param vreason
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpNameEVO updateData(CorpNameEVO datavo, UserVO uservo, int opertype, String vreason)
			throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(datavo.getTableName(), datavo.getPk_corpnameedit(),uuid, 60);
			checkBeforeAudit(datavo);
			if (opertype == 2) {
				CorpVO corpvo = CorpCache.getInstance().get(null, datavo.getPk_corp());
				if(corpvo != null){
					corpvo.setUnitdistinction(CodeUtils1.enCode(corpvo.getUnitname()));//原客户名称
					corpvo.setUnitname(CodeUtils1.enCode(datavo.getVnewname()));
					singleObjectBO.update(corpvo, new String[] { "unitname", "unitdistinction" });
					CorpCache.getInstance().remove(datavo.getPk_corp());
				} else {
					String errmsg = "原客户名称" + CodeUtils1.deCode(datavo.getVoldname()) + "数据错误";
					datavo.setVerrmsg(errmsg);
				} 
				datavo.setIstatus(IStatusConstant.ICORPEDITSTATUS_2);
				datavo.setVapprovenote(null);
			} else if (opertype == 3) {
				datavo.setIstatus(IStatusConstant.ICORPEDITSTATUS_3);
				datavo.setVapprovenote(vreason);
			}
			if (uservo != null) {
				datavo.setVapproveid(uservo.getCuserid());
				datavo.setVapprovename(uservo.getUser_name());
			}
			datavo.setTapprovetime(new DZFDateTime());
			singleObjectBO.update(datavo, new String[] { "istatus", "vapprovenote", "vapproveid",
					"vapprovename", "tapprovetime" });
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(datavo.getTableName(), datavo.getPk_corpnameedit(),uuid);
		}
		return datavo;
	}
	
	/**
	 * 审核前校验
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private void checkBeforeAudit(CorpNameEVO paramvo) throws DZFWarpException {
		//1、时间戳校验
		CorpNameEVO oldvo = (CorpNameEVO) singleObjectBO.queryByPrimaryKey(CorpNameEVO.class, paramvo.getPk_corpnameedit());
		if(oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)){
			throw new BusinessException("原客户名称"+CodeUtils1.deCode(paramvo.getVoldname())+"数据已经发生变化，请刷新界面数据后，再次尝试");
		}
		if(oldvo.getUpdatets() != null && oldvo.getUpdatets().compareTo(paramvo.getUpdatets()) != 0){
			throw new BusinessException("原客户名称"+CodeUtils1.deCode(paramvo.getVoldname())+"数据已经发生变化，请刷新界面数据后，再次尝试");
		}
		if(paramvo.getIstatus() != null && paramvo.getIstatus() != IStatusConstant.ICORPEDITSTATUS_1){
			throw new BusinessException("原客户名称"+CodeUtils1.deCode(paramvo.getVoldname())+"数据不为待审核");
		}
	}

	@Override
	public CorpNameEVO queryByID(String pk_corpnameedit) throws DZFWarpException {
		return (CorpNameEVO) singleObjectBO.queryByPrimaryKey(CorpNameEVO.class, pk_corpnameedit);
	}
	
}
