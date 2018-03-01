package com.dzf.service.channel.rebate.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.rebate.ManagerRefVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.pub.WorkflowVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.rebate.IRebateInputService;
import com.dzf.service.pub.IPubService;

@Service("rebateinptser")
public class RebateInputServiceImpl implements IRebateInputService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IPubService pubser;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RebateVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(paramvo);
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(RebateVO.class));
		if(list != null && list.size() > 0){
			if(!StringUtil.isEmpty(paramvo.getCorpname())){
				List<RebateVO> retlist = new ArrayList<RebateVO>();
				CorpVO corpvo = null;
				for(RebateVO vo : list){
					corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
					if(corpvo != null && !StringUtil.isEmpty(corpvo.getUnitname()) 
							&& corpvo.getUnitname().indexOf(paramvo.getCorpname()) != -1){
						retlist.add(vo);
					}
				}
				if(retlist != null && retlist.size() > 0){
					setShowInfo(retlist);
				}
				return retlist;
			}
			setShowInfo(list);
		}
		return list;
	}
	
	/**
	 * 设置返点相关展示信息
	 * @param list
	 * @throws DZFWarpException
	 */
	private void setShowInfo(List<RebateVO> list) throws DZFWarpException{
		Map<Integer, String> areamap = pubser.queryAreaMap("1");
		Map<String, ChnAreaVO> lareamap = pubser.queryLargeArea();
		for(RebateVO vo : list){
			setShowData(vo, areamap, lareamap);
		}
	}
	
	/**
	 * 设置单个返点单展示信息
	 * @throws DZFWarpException
	 */
	private void setShowData(RebateVO vo, Map<Integer, String> areamap, Map<String, ChnAreaVO> lareamap)
			throws DZFWarpException {
		CorpVO corpvo = qryCorpInfo(vo.getPk_corp());
		if (corpvo != null) {
			if (corpvo.getVprovince() != null) {
				if (areamap != null && !areamap.isEmpty()) {
					vo.setVprovname(areamap.get(corpvo.getVprovince()));
				}
				if (lareamap != null && !lareamap.isEmpty()) {
					ChnAreaVO lareavo = lareamap.get(String.valueOf(corpvo.getVprovince()));
					if (lareavo != null) {
						vo.setVareaname(lareavo.getAreaname());
						if (!StringUtil.isEmpty(lareavo.getUserid())) {
							UserVO uservo = UserCache.getInstance().get(lareavo.getUserid(), null);
							if (uservo != null) {
								vo.setVmanagername(uservo.getUser_name());
							}
						}
					}
				}
			}
			vo.setCorpcode(corpvo.getInnercode());
			vo.setCorpname(corpvo.getUnitname());
		}
		String period = "";
		if(!StringUtil.isEmpty(vo.getVyear())){
			period = vo.getVyear()+"-";
		}
		if(vo.getIseason() != null){
			period = period + getSeasonName(vo.getIseason());
		}
		vo.setVperiod(period);
	}
	
	/**
	 * 获取季度名称
	 * @return
	 * @throws DZFWarpException
	 */
	private String getSeasonName(Integer season) throws DZFWarpException {
		String name = "";
		switch (season) {
			case 1:
				name = "第一季度";
				break;
			case 2:
				name = "第一季度";
				break;
			case 3:
				name = "第一季度";
				break;
			case 4:
				name = "第一季度";
				break;
		}
		return name;
	}
	
	/**
	 * 查询会计公司信息
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private CorpVO qryCorpInfo(String pk_corp) throws DZFWarpException {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if(corpvo != null){
			corpvo.setLegalbodycode(CodeUtils1.deCode(corpvo.getLegalbodycode()));
			corpvo.setPhone1(CodeUtils1.deCode(corpvo.getPhone1()));
			corpvo.setPhone2(CodeUtils1.deCode(corpvo.getPhone2()));
			corpvo.setUnitname(CodeUtils1.deCode(corpvo.getUnitname()));
			corpvo.setUnitshortname(CodeUtils1.deCode(corpvo.getUnitshortname()));
		}
		return corpvo;
	}
	
	/**
	 * 获取查询条件及参数
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQrySql(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT * \n") ;
		sql.append("  FROM cn_rebate \n") ; 
		sql.append(" WHERE nvl(dr, 0) = 0 \n") ; 
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append("   AND istatus = ? \n") ; 
			spm.addParam(paramvo.getQrytype());
		}else{
			if(!StringUtil.isEmpty(paramvo.getVyear())){
				sql.append("   AND vyear = ? \n") ; 
				spm.addParam(paramvo.getVyear());
			}
			if(paramvo.getIseason() != null){
				sql.append("   AND iseason = ? \n") ; 
				spm.addParam(paramvo.getIseason());
			}
			if(paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() != -1){
				sql.append("   AND istatus = ? \n") ; 
				spm.addParam(paramvo.getVdeductstatus());
			}
			if(!StringUtil.isEmpty(paramvo.getCuserid())){
				sql.append("SELECT t.pk_corp \n") ;
				sql.append("  FROM bd_account t \n") ; 
				sql.append(" WHERE nvl(t.dr, 0) = 0 \n") ; 
				sql.append("   AND t.vprovince IN (SELECT b.vprovince \n") ; 
				sql.append("                         FROM cn_chnarea_b b \n") ; 
				sql.append("                        WHERE nvl(b.dr, 0) = 0 \n") ; 
				String[] users = paramvo.getCuserid().split(",");
				String where = SqlUtil.buildSqlForIn("b.userid", users);
				if(!StringUtil.isEmpty(where)){
					sql.append(" AND ").append(where);
				}
				sql.append("                                 )");
			}
			if(!StringUtil.isEmpty(paramvo.getPk_corp())){
				String[] corps = paramvo.getPk_corp().split(",");
				String where = SqlUtil.buildSqlForIn("pk_corp", corps);
				sql.append(" AND ").append(where);
			}
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public RebateVO save(RebateVO data, String pk_corp) throws DZFWarpException {
		try {
			chcekBeforeSave(data);
			if(StringUtil.isEmpty(data.getVbillcode())){
				String vbillcode = pubser.queryCode("cn_rebate");
				data.setVbillcode(vbillcode);
			}
			LockUtil.getInstance().tryLockKey(data.getTableName(), data.getPk_corp()+""+data.getVyear()+""+data.getIseason(), 10);
			RebateVO retvo =  (RebateVO) singleObjectBO.saveObject(pk_corp, data);
			if(retvo != null){
				Map<Integer, String> areamap = pubser.queryAreaMap("1");
				Map<String, ChnAreaVO> lareamap = pubser.queryLargeArea();
				setShowData(retvo, areamap, lareamap);
			}
			return retvo;
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_corp()+""+data.getVyear()+""+data.getIseason());
		}
	}
	
	/**
	 * 保存前校验
	 * @param data
	 * @throws DZFWarpException
	 */
	private void chcekBeforeSave(RebateVO data) throws DZFWarpException{
		if(!StringUtil.isEmpty(data.getVbillcode())){
			checkCodeOnly(data);//返点单单号唯一性校验
		}
		checkDataOnly(data);//返点信息年-季度唯一性校验
		checkRebateMny(data);//返点金额校验
	}
	
	/**
	 * 校验返点相关金额是否正确
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void checkRebateMny(RebateVO vo) throws DZFWarpException {
		String errmsg = "";
		RebateVO mnyvo = queryDebateMny(vo);
		if(mnyvo != null){
			if(vo.getNdebitmny() != null && vo.getNdebitmny().compareTo(mnyvo.getNdebitmny()) != 0){
				errmsg = "扣款金额计算错误";
			}
			if(vo.getNdebitmny() != null && vo.getNbasemny() != null 
					&& vo.getNbasemny().compareTo(vo.getNdebitmny()) > 0){
				errmsg = "返点基数不能大于扣款金额";
			}
			if(vo.getNbasemny() != null && vo.getNrebatemny() != null
					&& vo.getNrebatemny().compareTo(vo.getNbasemny()) > 0){
				errmsg = "返点金额不能大于返点基数";
			}
		}else{
			errmsg = "扣款金额计算错误";
		}
		if(!StringUtil.isEmpty(errmsg)){
			throw new BusinessException(errmsg);
		}
	}
	
	/**
	 * 返点单号唯一性校验
	 * @param vo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public void checkCodeOnly(RebateVO vo) throws DZFWarpException {
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vbillcode from cn_rebate where nvl(dr,0) = 0 ");
		sql.append(" and vbillcode = ? ");
		spm.addParam(vo.getVbillcode());
		if (!StringUtil.isEmpty(vo.getPk_rebate())) {
			sql.append(" and pk_rebate != ? ");
			spm.addParam(vo.getPk_rebate());
		}
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RebateVO.class));
		if (list != null && list.size() > 0) {
			throw new BusinessException("返点单号："+vo.getVbillcode()+"已经在系统中存在");
		}
	}
	
	/**
	 * 返点信息年-季度唯一性校验
	 * @param vo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public void checkDataOnly(RebateVO vo) throws DZFWarpException {
		if(StringUtil.isEmpty(vo.getVyear())){
			throw new BusinessException("所属年不能为空");
		}
		if(vo.getIseason() == null){
			throw new BusinessException("所属季度不能为空");
		}
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vbillcode from cn_rebate where nvl(dr,0) = 0 ");
		if (!StringUtil.isEmptyWithTrim(vo.getPk_corp())) {
			sql.append(" and pk_corp = ? ");
			spm.addParam(vo.getPk_corp());
		} else {
			throw new BusinessException("加盟商信息不能为空");
		}
		sql.append(" and vyear = ? and iseason = ? ");
		spm.addParam(vo.getVyear());
		spm.addParam(vo.getIseason());
		if (!StringUtil.isEmpty(vo.getPk_rebate())) {
			sql.append(" and pk_rebate != ? ");
			spm.addParam(vo.getPk_rebate());
		}
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RebateVO.class));
		if (list != null && list.size() > 0) {
			String corpname = "";
			CorpVO corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if(corpvo != null){
				corpname = corpvo.getUnitname();
			}
			throw new BusinessException("加盟商："+corpname+vo.getVyear()+"年"+vo.getIseason()+"季度返点单，已经在系统中存在");
		}
	}

	@Override
	public List<ManagerRefVO> queryManagerRef(QryParamVO paramvo) throws DZFWarpException {
		List<ManagerRefVO> retlist = new ArrayList<ManagerRefVO>();
		ChnAreaBVO[] areaVOs = (ChnAreaBVO[]) singleObjectBO.queryByCondition(ChnAreaBVO.class, " nvl(dr,0) = 0 ",
				null);
		if (areaVOs != null && areaVOs.length > 0) {
			UserVO uservo = null;
			ManagerRefVO refvo = null;
			for (ChnAreaBVO vo : areaVOs) {
				if (!StringUtil.isEmpty(vo.getUserid())) {
					refvo = new ManagerRefVO();
					refvo.setCuserid(vo.getUserid());
					uservo = UserCache.getInstance().get(vo.getUserid(), null);
					if (uservo != null) {
						refvo.setUsercode(uservo.getUser_code());
						refvo.setUsername(uservo.getUser_name());
					}
					if (!StringUtil.isEmpty(paramvo.getUser_code())) {
						if (!StringUtil.isEmpty(refvo.getUsercode()) || !StringUtil.isEmpty(refvo.getUsername())) {
							if ((!StringUtil.isEmpty(refvo.getUsercode())
									&& refvo.getUsercode().indexOf(paramvo.getUser_code()) != -1)
									|| (!StringUtil.isEmpty(refvo.getUsername())
											&& refvo.getUsername().indexOf(paramvo.getUser_code()) != -1)) {
								retlist.add(refvo);
							}
						}
					} else {
						retlist.add(refvo);
					}
				}
			}
		}
		return retlist;
	}

	@Override
	public void delete(RebateVO data) throws DZFWarpException {
		if(data.getIstatus() != null && data.getIstatus() != IStatusConstant.IREBATESTATUS_0){
			throw new BusinessException("返点单："+data.getVbillcode()+"状态不为待提交，不能删除");
		}
		String errmsg = checkData(data);
		if(!StringUtil.isEmpty(errmsg)){
			throw new BusinessException(errmsg);
		}
		String sql = " UPDATE cn_rebate SET dr = 1, tstamp = ? WHERE nvl(dr,0) = 0 AND pk_rebate = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(new DZFDateTime());
		spm.addParam(data.getPk_rebate());
		singleObjectBO.executeUpdate(sql, spm);
	}
	
	/**
	 * 数据校验
	 * @param data
	 * @throws DZFWarpException
	 */
	@Override
	public String checkData(RebateVO data) throws DZFWarpException {
		String errmsg = "";
		RebateVO oldvo = (RebateVO) singleObjectBO.queryByPrimaryKey(RebateVO.class, data.getPk_rebate());
		if(oldvo != null){
			if(oldvo.getDr() != null && oldvo.getDr() == 1){
				errmsg = "返点单："+data.getVbillcode()+"已经被删除";
			}
			if(data.getTstamp() == null || oldvo.getTstamp() == null){
				errmsg = "返点单："+data.getVbillcode()+"数据错误";
			}
			if(data.getTstamp().compareTo(oldvo.getTstamp()) != 0){
				errmsg = "返点单："+data.getVbillcode()+"数据发生变化，请重新查询后再次尝试";
			}
		}else{
			errmsg = "返点单："+data.getVbillcode()+"数据错误";
		}
		return errmsg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RebateVO queryDebateMny(RebateVO data) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT SUM(nvl(t.ndeductmny,0) + nvl(t.nsubdeductmny,0)) AS ndebitmny, \n");
		sql.append("  SUM(nvl(t.ndeductmny,0) + nvl(t.nsubdeductmny,0)) AS nbasemny \n");
		sql.append("  FROM cn_contract t \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.isncust, 'N') = 'N' \n");
		sql.append("   AND t.vdeductstatus in (?, ?) \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		sql.append("   AND t.pk_corp = ? \n");
		spm.addParam(data.getPk_corp());
		List<String> pliat = getDebatePeriod(data);
		if (pliat != null && pliat.size() > 0) {
			String where = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata,1,7)", pliat.toArray(new String[0]));
			sql.append(" AND ").append(where);
		} else {
			throw new BusinessException("返点单所属年、所属季度不能为空");
		}
		sql.append("   GROUP BY t.pk_corp \n");
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RebateVO.class));
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			RebateVO retvo = new RebateVO();
			retvo.setNdebitmny(DZFDouble.ZERO_DBL);
			retvo.setNbasemny(DZFDouble.ZERO_DBL);
			return retvo;
		}
	}
	
	/**
	 * 获取查询期间
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	private List<String> getDebatePeriod(RebateVO data) throws DZFWarpException {
		List<String> pliat = new ArrayList<String>();
		String year = data.getVyear();
		switch(data.getIseason()){
			case 1:
				pliat.add(year+"-01");
				pliat.add(year+"-02");
				pliat.add(year+"-03");
				break;
			case 2:
				pliat.add(year+"-04");
				pliat.add(year+"-05");
				pliat.add(year+"-06");
				break;
			case 3:
				pliat.add(year+"-07");
				pliat.add(year+"-08");
				pliat.add(year+"-09");
				break;
			case 4:
				pliat.add(year+"-10");
				pliat.add(year+"-11");
				pliat.add(year+"-12");
				break;
		}
		return pliat;
	}

	@Override
	public RebateVO[] saveCommit(RebateVO[] bateVOs) throws DZFWarpException {
		List<RebateVO> uplist = new ArrayList<RebateVO>();
		if(bateVOs != null && bateVOs.length > 0){
			String errmsg = "";
			for(RebateVO vo : bateVOs){
				if(!vo.getIstatus().equals(IStatusConstant.IREBATESTATUS_0)){
					vo.setVerrmsg("返点单："+vo.getVbillcode()+"状态不为待提交");
					continue;
				}
				errmsg = checkData(vo);
				if(!StringUtil.isEmpty(errmsg)){
					vo.setVerrmsg(errmsg);
					continue;
				}
				vo.setIstatus(IStatusConstant.IREBATESTATUS_1);
				vo.setTstamp(new DZFDateTime());
				uplist.add(vo);
			}
		}
		if(uplist != null && uplist.size() > 0){
			singleObjectBO.updateAry(uplist.toArray(new RebateVO[0]), new String[]{"istatus","tstamp"});
		}
		return bateVOs;
	}

	@Override
	public RebateVO queryById(RebateVO data, Integer opertype) throws DZFWarpException {
		String errmsg = "";
		RebateVO oldvo = (RebateVO) singleObjectBO.queryByPrimaryKey(RebateVO.class, data.getPk_rebate());
		if(oldvo != null){
			if(oldvo.getDr() != null && oldvo.getDr() == 1){
				errmsg = "返点单："+data.getVbillcode()+"已经被删除";
			}
			if(data.getTstamp() == null || oldvo.getTstamp() == null){
				errmsg = "返点单："+data.getVbillcode()+"数据错误";
			}
			if(opertype == 1){
//				if(data.getTstamp().compareTo(oldvo.getTstamp()) != 0){
//					errmsg = "返点单："+data.getVbillcode()+"数据发生变化，请重新查询后再次尝试";
//				}
				if(oldvo.getIstatus() != IStatusConstant.IREBATESTATUS_0){
					errmsg = "返点单："+data.getVbillcode()+"状态不为待提交状态，不能修改，请重新查询后再次尝试";
				}
			}
		}else{
			errmsg = "返点单："+data.getVbillcode()+"数据错误";
		}
		if(!StringUtil.isEmpty(errmsg)){
			throw new BusinessException(errmsg);
		}
		String sql = " nvl(dr,0) = 0 AND pk_bill = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(oldvo.getPk_rebate());
		WorkflowVO[] flowVOs = (WorkflowVO[]) singleObjectBO.queryByCondition(WorkflowVO.class, sql, spm);
		if(flowVOs != null && flowVOs.length > 0){
			oldvo.setChildren(flowVOs);
		}
		//展示赋值
		CorpVO corpvo = CorpCache.getInstance().get(null, oldvo.getPk_corp());
		if(corpvo != null){
			oldvo.setCorpname(corpvo.getUnitname());
		}
		UserVO uservo = UserCache.getInstance().get(oldvo.getCoperatorid(), null);
		if(uservo != null){
			oldvo.setVoperatorname(uservo.getUser_name());
		}
		if(oldvo.getIstatus() != null){
			String vstatusname = "";
			//0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回；
			switch(oldvo.getIstatus()){
				case 0:
					vstatusname = "待提交";
					break;
				case 1:
					vstatusname = "待确认";
					break;
				case 2:
					vstatusname = "待审批";
					break;
				case 3:
					vstatusname = "审批通过";
					break;
				case 4:
					vstatusname = "已驳回";
					break;
			}
			oldvo.setVstatusname(vstatusname);
		}
		if(oldvo.getIseason() != null){
			String vseasonname = "";
			switch(oldvo.getIseason()){
				case 1:
					vseasonname = "第一季度";
					break;
				case 2:
					vseasonname = "第二季度";
					break;
				case 3:
					vseasonname = "第三季度";
					break;
				case 4:
					vseasonname = "第四季度";
					break;
			}
			oldvo.setVseasonname(vseasonname);
		}
		return oldvo;
	}

}
