package com.dzf.service.channel.rebate.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.rebate.ManagerRefVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.pub.WorkflowVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.rebate.IRebateInputService;
import com.dzf.service.pub.IBillCodeService;
import com.dzf.service.pub.IPubService;

@Service("rebateinptser")
public class RebateInputServiceImpl implements IRebateInputService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IPubService pubser;

	@Autowired
	private IBillCodeService billCodeSer;

	@SuppressWarnings("unchecked")
	@Override
	public List<RebateVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(paramvo);
		if (qryvo == null) {
			return null;
		}
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(RebateVO.class));
		if (list != null && list.size() > 0) {
			return setShowData(paramvo, list);
		}
		return list;
	}
	
	/**
	 * 设置显示名称
	 * @param paramvo
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<RebateVO> setShowData(QryParamVO paramvo, List<RebateVO> list) throws DZFWarpException {
		Map<String, String> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		Map<String, String> opermap = pubser.getManagerMap(IStatusConstant.IYUNYING);// 渠道运营
		Map<Integer, String> areamap = pubser.getAreaMap(paramvo.getAreaname(), IStatusConstant.IYUNYING);// 大区
		if (!StringUtil.isEmpty(paramvo.getCorpname())) {
			List<RebateVO> retlist = new ArrayList<RebateVO>();
			CorpVO corpvo = null;
			for (RebateVO vo : list) {
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (corpvo != null && !StringUtil.isEmpty(corpvo.getUnitname())
						&& corpvo.getUnitname().indexOf(paramvo.getCorpname()) != -1) {
					setShowSingle(vo, marmap, areamap, opermap);
					retlist.add(vo);
				}
			}
			return retlist;
		}
		setShowInfo(list, marmap, areamap, opermap);
		return list;
	}

	/**
	 * 设置多条显示名称
	 * @param list
	 * @param marmap
	 * @param areamap
	 * @throws DZFWarpException
	 */
	private void setShowInfo(List<RebateVO> list, Map<String, String> marmap, Map<Integer, String> areamap,
			Map<String, String> opermap) throws DZFWarpException {
		for (RebateVO vo : list) {
			setShowSingle(vo, marmap, areamap, opermap);
		}
	}

	/**
	 * 设置单条显示名称
	 * @param vo
	 * @param marmap
	 * @param areamap
	 * @param opermap
	 * @throws DZFWarpException
	 */
	private void setShowSingle(RebateVO vo, Map<String, String> marmap, Map<Integer, String> areamap,
			Map<String, String> opermap) throws DZFWarpException {
		CorpVO corpvo = qryCorpInfo(vo.getPk_corp());
		if (corpvo != null) {
			if (corpvo.getVprovince() != null) {
				vo.setVprovname(corpvo.getCitycounty());
				if (marmap != null && !marmap.isEmpty()) {
					String cuserid = marmap.get(vo.getPk_corp());
					UserVO uservo = UserCache.getInstance().get(cuserid, null);
					if (uservo != null) {
						vo.setVmanagername(uservo.getUser_name());// 渠道经理
					}
				}
				if (opermap != null && !opermap.isEmpty()) {
					String cuserid = opermap.get(vo.getPk_corp());
					UserVO uservo = UserCache.getInstance().get(cuserid, null);
					if (uservo != null) {
						vo.setVoperater(uservo.getUser_name());// 渠道运营
					}
				}
				if (areamap != null && !areamap.isEmpty()) {
					vo.setAreaname(areamap.get(corpvo.getVprovince()));// 大区
				}
			}
			vo.setCorpcode(corpvo.getInnercode());
			vo.setCorpname(corpvo.getUnitname());
		}
		String period = "";
		if (!StringUtil.isEmpty(vo.getVyear())) {
			period = vo.getVyear() + "-";
		}
		if (vo.getIseason() != null) {
			period = period + getSeasonName(vo.getIseason());
		}
		vo.setVperiod(period);
	}

	/**
	 * 获取季度名称
	 * 
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
			name = "第二季度";
			break;
		case 3:
			name = "第三季度";
			break;
		case 4:
			name = "第四季度";
			break;
		}
		return name;
	}

	/**
	 * 查询会计公司信息
	 * 
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private CorpVO qryCorpInfo(String pk_corp) throws DZFWarpException {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if (corpvo != null) {
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
	 * 
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQrySql(QryParamVO paramvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.*,ba.vprovince FROM cn_rebate t \n");
		sql.append(" LEFT JOIN bd_account ba on t.pk_corp = ba.pk_corp ");
		sql.append(" WHERE nvl(t.dr, 0) = 0 and nvl(ba.dr,0)=0 \n");
		String condition = pubser.makeCondition(paramvo.getCuserid(), paramvo.getAreaname(), IStatusConstant.IYUNYING);
		if (condition != null && !condition.equals("alldata")) {
			sql.append(condition);
		} else if (condition == null) {
			return null;
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() != -1) {
			sql.append("   AND t.istatus = ? \n");
			spm.addParam(paramvo.getQrytype());
		} else {
			if (!StringUtil.isEmpty(paramvo.getVyear())) {
				sql.append("   AND t.vyear = ? \n");
				spm.addParam(paramvo.getVyear());
			}
			if (paramvo.getIseason() != null && paramvo.getIseason() != -1) {
				sql.append("   AND t.iseason = ? \n");
				spm.addParam(paramvo.getIseason());
			}
			if (paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() != -1) {
				if (paramvo.getVdeductstatus() == -2) {
					sql.append("   AND t.istatus in (?,?,?) \n");
					spm.addParam(IStatusConstant.IREBATESTATUS_1);
					spm.addParam(IStatusConstant.IREBATESTATUS_2);
					spm.addParam(IStatusConstant.IREBATESTATUS_3);
				} else if (paramvo.getVdeductstatus() == -3) {
					sql.append("   AND t.istatus in (?,?) \n");
					spm.addParam(IStatusConstant.IREBATESTATUS_2);
					spm.addParam(IStatusConstant.IREBATESTATUS_3);
				} else {
					sql.append("   AND t.istatus = ? \n");
					spm.addParam(paramvo.getVdeductstatus());
				}
			}
			// if(!StringUtil.isEmpty(paramvo.getCuserid())){
			// String[] corps = pubser.getManagerCorp(paramvo.getCuserid());
			// if(corps != null && corps.length > 0){
			// String where = SqlUtil.buildSqlForIn("pk_corp", corps);
			// sql.append(" AND ").append(where);
			// }else{
			// sql.append(" AND pk_corp is null \n") ;
			// }
			// }
			if (!StringUtil.isEmpty(paramvo.getPk_corp())) {
				String[] corps = paramvo.getPk_corp().split(",");
				String where = SqlUtil.buildSqlForIn("t.pk_corp", corps);
				sql.append(" AND ").append(where);
			}
			if (!StringUtil.isEmpty(paramvo.getPk_bill())) {
				sql.append("   AND t.pk_rebate = ? \n");
				spm.addParam(paramvo.getPk_bill());
			}
			if (!StringUtil.isEmpty(paramvo.getVqrysql())) {// 渠道经理过滤
				sql.append(paramvo.getVqrysql());
			}
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public RebateVO save(RebateVO data, String pk_corp) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			chcekBeforeSave(data);
			if (StringUtil.isEmpty(data.getVbillcode())) {
				String vbillcode = getVbillcode();
				data.setVbillcode(vbillcode);
				checkCodeOnly(data);// 返点单单号唯一性校验
			}
			LockUtil.getInstance().tryLockKey(data.getTableName(),
					data.getPk_corp() + "" + data.getVyear() + "" + data.getIseason(), uuid, 10);
			RebateVO retvo = (RebateVO) singleObjectBO.saveObject(pk_corp, data);
			if (retvo != null) {
				retvo.setVprovince(data.getVprovince());
				Map<String, String> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
				Map<String, String> opermap = pubser.getManagerMap(IStatusConstant.IYUNYING);// 渠道运营
				Map<Integer, String> areaMap = pubser.getAreaMap(null, IStatusConstant.IYUNYING);// 大区
				setShowSingle(retvo, marmap, areaMap, opermap);
			}
			return retvo;
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(),
					data.getPk_corp() + "" + data.getVyear() + "" + data.getIseason(), uuid);
		}
	}

	/**
	 * 获取单据编码
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private String getVbillcode() throws DZFWarpException {
		String code;
		DZFDate date = new DZFDate();
		String year = String.valueOf(date.getYear());
		String str = year + date.getStrMonth() + date.getStrDay();
		MaxCodeVO mcvo = new MaxCodeVO();
		mcvo.setTbName("cn_rebate");
		mcvo.setFieldName("vbillcode");
		mcvo.setPk_corp("000001");
		mcvo.setBillType(str);
		mcvo.setCorpIdField("pk_corp");
		mcvo.setDiflen(4);
		try {
			code = billCodeSer.getDefaultCode(mcvo);
		} catch (Exception e) {
			throw new BusinessException("获取单据编码失败");
		}
		return code;
	}

	/**
	 * 保存前校验
	 * 
	 * @param data
	 * @throws DZFWarpException
	 */
	private void chcekBeforeSave(RebateVO data) throws DZFWarpException {
		if (!StringUtil.isEmpty(data.getVbillcode())) {
			checkCodeOnly(data);// 返点单单号唯一性校验
		}
		checkDataOnly(data);// 返点信息年-季度唯一性校验
		checkRebateMny(data);// 返点金额校验
	}

	/**
	 * 校验返点相关金额是否正确
	 * 
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void checkRebateMny(RebateVO vo) throws DZFWarpException {
		if (CommonUtil.getDZFDouble(vo.getNbasemny()).compareTo(DZFDouble.ZERO_DBL) == 0) {
			throw new BusinessException("返点基数必须大于0");
		}
		if (CommonUtil.getDZFDouble(vo.getNrebatemny()).compareTo(DZFDouble.ZERO_DBL) == 0) {
			throw new BusinessException("返点金额必须大于0");
		}
		RebateVO mnyvo = queryDebateMny(vo);
		if (mnyvo != null) {
			if (vo.getNdebitmny() != null && vo.getNdebitmny().compareTo(mnyvo.getNdebitmny()) != 0) {
				throw new BusinessException("扣款金额计算错误");
			}
			if (vo.getNdebitmny() != null && vo.getNbasemny() != null
					&& vo.getNbasemny().compareTo(vo.getNdebitmny()) > 0) {
				throw new BusinessException("返点基数不能大于扣款金额");
			}
			if (vo.getNbasemny() != null && vo.getNrebatemny() != null
					&& vo.getNrebatemny().compareTo(vo.getNbasemny()) > 0) {
				throw new BusinessException("返点金额不能大于返点基数");
			}
		} else {
			throw new BusinessException("扣款金额计算错误");
		}
	}

	/**
	 * 返点单号唯一性校验
	 * 
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
			throw new BusinessException("返点单号：" + vo.getVbillcode() + "已经在系统中存在");
		}
	}

	/**
	 * 返点信息年-季度唯一性校验
	 * 
	 * @param vo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public void checkDataOnly(RebateVO vo) throws DZFWarpException {
		if (StringUtil.isEmpty(vo.getVyear())) {
			throw new BusinessException("所属年不能为空");
		}
		if (vo.getIseason() == null) {
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
			if (corpvo != null) {
				corpname = corpvo.getUnitname();
			}
			throw new BusinessException("加盟商：" + corpname + vo.getVyear() + "年第" + vo.getIseason() + "季度返点单，已经在系统中存在");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ManagerRefVO> queryManagerRef(QryParamVO pamvo) throws DZFWarpException {
		List<ManagerRefVO> retlist = new ArrayList<ManagerRefVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		//只有渠道经理参照，此字段才传值，用于过滤当前登陆人负责的渠道经理
		if(!StringUtil.isEmpty(pamvo.getCuserid())){
			Integer power = pubser.getAreaPower(pamvo.getCuserid());
			if(power != null){
				if(power == -1){
					return new ArrayList<ManagerRefVO>();
				}else if(power == 1){
					sql.append("SELECT DISTINCT userid  \n") ;
					sql.append("  FROM cn_chnarea_b  \n") ; 
					sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
					//1：渠道经理；2：培训师；3：渠道运营；
					sql.append("   AND nvl(type, 0) = ?  \n");
					spm.addParam(pamvo.getQrytype());
				}else if(power == 2){
					sql.append("SELECT b.userid  \n") ;
					sql.append("  FROM cn_chnarea_b b  \n") ; 
					sql.append("  LEFT JOIN cn_chnarea a ON b.pk_chnarea = a.pk_chnarea  \n") ; 
					sql.append(" WHERE nvl(b.dr, 0) = 0  \n") ; 
					sql.append("   AND nvl(a.dr, 0) = 0  \n") ; 
					sql.append("   AND b.type = ?  \n") ; 
					sql.append("   AND a.userid = ?  \n");
					spm.addParam(pamvo.getQrytype());
					spm.addParam(pamvo.getCuserid());
				}else if(power == 3){
					sql.append("SELECT DISTINCT userid  \n") ;
					sql.append("  FROM cn_chnarea_b  \n") ; 
					sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
					//1：渠道经理；2：培训师；3：渠道运营；
					sql.append("   AND nvl(type, 0) = ?  \n");
					spm.addParam(pamvo.getQrytype());
					sql.append("   AND userid = ?  \n");
					spm.addParam(pamvo.getCuserid());
				}
			}
		}else{
			sql.append("SELECT DISTINCT userid  \n") ;
			sql.append("  FROM cn_chnarea_b  \n") ; 
			sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
			//1：渠道经理；2：培训师；3：渠道运营；
			sql.append("   AND nvl(type, 0) = ?  \n");
			spm.addParam(pamvo.getQrytype());
		}
		
		List<ChnAreaBVO> list = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaBVO.class));
		if (list != null && list.size() > 0) {
			UserVO uservo = null;
			ManagerRefVO refvo = null;
			for (ChnAreaBVO vo : list) {
				if (!StringUtil.isEmpty(vo.getUserid())) {
					refvo = new ManagerRefVO();
					refvo.setCuserid(vo.getUserid());
					uservo = UserCache.getInstance().get(vo.getUserid(), null);
					if (uservo != null) {
						refvo.setUsercode(uservo.getUser_code());
						refvo.setUsername(uservo.getUser_name());
					}
					if (!StringUtil.isEmpty(pamvo.getUser_code())) {
						if (!StringUtil.isEmpty(refvo.getUsercode()) || !StringUtil.isEmpty(refvo.getUsername())) {
							if ((!StringUtil.isEmpty(refvo.getUsercode())
									&& refvo.getUsercode().indexOf(pamvo.getUser_code()) != -1)
									|| (!StringUtil.isEmpty(refvo.getUsername())
											&& refvo.getUsername().indexOf(pamvo.getUser_code()) != -1)) {
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
		if (data.getIstatus() != null && data.getIstatus() != IStatusConstant.IREBATESTATUS_0
				&& data.getIstatus() != IStatusConstant.IREBATESTATUS_4) {
			throw new BusinessException("返点单：" + data.getVbillcode() + "状态不为待提交或已驳回，不能删除");
		}
		String errmsg = checkData(data);
		if (!StringUtil.isEmpty(errmsg)) {
			throw new BusinessException(errmsg);
		}
		String sql = " DELETE FROM cn_rebate WHERE pk_rebate = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(data.getPk_rebate());
		singleObjectBO.executeUpdate(sql, spm);
	}

	/**
	 * 数据校验
	 * 
	 * @param data
	 * @throws DZFWarpException
	 */
	@Override
	public String checkData(RebateVO data) throws DZFWarpException {
		String errmsg = "";
		RebateVO oldvo = (RebateVO) singleObjectBO.queryByPrimaryKey(RebateVO.class, data.getPk_rebate());
		if (oldvo != null) {
			if (oldvo.getDr() != null && oldvo.getDr() == 1) {
				errmsg = "返点单：" + data.getVbillcode() + "已经被删除";
			}
			if (data.getTstamp() == null || oldvo.getTstamp() == null) {
				errmsg = "返点单：" + data.getVbillcode() + "数据错误";
			}
			if (data.getTstamp().compareTo(oldvo.getTstamp()) != 0) {
				errmsg = "返点单：" + data.getVbillcode() + "数据发生变化，请重新查询后再次尝试";
			}
		} else {
			errmsg = "返点单：" + data.getVbillcode() + "数据错误";
		}
		return errmsg;
	}

	/**
	 * 获取查询期间最终预付款金额 = 扣款日期在所属期间的预付款扣款 - 变更日期在所属期间的预付款退款
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RebateVO queryDebateMny(RebateVO data) throws DZFWarpException {
		RebateVO retvo = null;
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT SUM(nvl(t.ndeductmny,0)) AS ndebitmny, \n");
		sql.append("  SUM(nvl(t.ndeductmny,0)) AS nbasemny, \n");
		// 合同数量去掉补提单合同数
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus,0) != 5 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS icontractnum  \n");
		sql.append("  FROM cn_contract t \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(ct.dr, 0) = 0 \n");
		sql.append("   AND nvl(ct.isncust, 'N') = 'N' \n");
		sql.append("   AND t.vdeductstatus in (?, ?, ?) \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
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
		if (list != null && list.size() > 0) {
			retvo = list.get(0);
		} else {
			retvo = new RebateVO();
			retvo.setNdebitmny(DZFDouble.ZERO_DBL);
			retvo.setNbasemny(DZFDouble.ZERO_DBL);
			retvo.setIcontractnum(0);
		}
		RebateVO backvo = queryRetMny(data);
		if (backvo != null) {
			retvo.setNdebitmny(SafeCompute.add(retvo.getNdebitmny(), backvo.getNdebitmny()));
			retvo.setNbasemny(SafeCompute.add(retvo.getNbasemny(), backvo.getNbasemny()));
			retvo.setIcontractnum(countConNum(retvo.getIcontractnum(), backvo.getIcontractnum()));
		}
		return retvo;
	}

	/**
	 * 合同数量计算
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private Integer countConNum(Integer num1, Integer num2) throws DZFWarpException {
		num1 = num1 == null ? 0 : num1;
		num2 = num2 == null ? 0 : num2;
		return num1 - num2;
	}

	/**
	 * 获取查询期间终止合同退款金额
	 * 
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private RebateVO queryRetMny(RebateVO data) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT SUM(nvl(ct.nsubdeductmny, 0)) AS ndebitmny,  \n");
		sql.append("       SUM(nvl(ct.nsubdeductmny, 0)) AS nbasemny,  \n");
		sql.append("       SUM(case ct.vstatus  \n");
		sql.append("             when 10 then  \n");
		sql.append("              1  \n");
		sql.append("             else  \n");
		sql.append("              0  \n");
		sql.append("           end) AS icontractnum  \n");
		sql.append("  FROM cn_contract ct  \n");
		sql.append(" INNER JOIN ynt_contract t ON ct.pk_contract = t.pk_contract  \n");
		sql.append(" WHERE nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.isncust, 'N') = 'N'  \n");
		sql.append("   AND ct.vdeductstatus in (?, ?) \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		sql.append("   AND ct.pk_corp = ? \n");
		spm.addParam(data.getPk_corp());
		List<String> pliat = getDebatePeriod(data);
		if (pliat != null && pliat.size() > 0) {
			String where = SqlUtil.buildSqlForIn("SUBSTR(ct.dchangetime,1,7)", pliat.toArray(new String[0]));
			sql.append(" AND ").append(where).append(" \n");
		} else {
			throw new BusinessException("返点单所属年、所属季度不能为空");
		}
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RebateVO.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			RebateVO retvo = new RebateVO();
			retvo.setNdebitmny(DZFDouble.ZERO_DBL);
			retvo.setNbasemny(DZFDouble.ZERO_DBL);
			retvo.setIcontractnum(0);
			return retvo;
		}
	}

	/**
	 * 获取查询期间
	 * 
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	private List<String> getDebatePeriod(RebateVO data) throws DZFWarpException {
		List<String> pliat = new ArrayList<String>();
		String year = data.getVyear();
		switch (data.getIseason()) {
		case 1:
			pliat.add(year + "-01");
			pliat.add(year + "-02");
			pliat.add(year + "-03");
			break;
		case 2:
			pliat.add(year + "-04");
			pliat.add(year + "-05");
			pliat.add(year + "-06");
			break;
		case 3:
			pliat.add(year + "-07");
			pliat.add(year + "-08");
			pliat.add(year + "-09");
			break;
		case 4:
			pliat.add(year + "-10");
			pliat.add(year + "-11");
			pliat.add(year + "-12");
			break;
		}
		return pliat;
	}

	@Override
	public RebateVO[] saveCommit(RebateVO[] bateVOs) throws DZFWarpException {
		List<RebateVO> uplist = new ArrayList<RebateVO>();
		if (bateVOs != null && bateVOs.length > 0) {
			String errmsg = "";
			for (RebateVO vo : bateVOs) {
				if (!vo.getIstatus().equals(IStatusConstant.IREBATESTATUS_0)) {
					vo.setVerrmsg("返点单：" + vo.getVbillcode() + "状态不为待提交");
					continue;
				}
				errmsg = checkData(vo);
				if (!StringUtil.isEmpty(errmsg)) {
					vo.setVerrmsg(errmsg);
					continue;
				}
				vo.setIstatus(IStatusConstant.IREBATESTATUS_1);
				vo.setTstamp(new DZFDateTime());
				uplist.add(vo);
			}
		}
		if (uplist != null && uplist.size() > 0) {
			singleObjectBO.updateAry(uplist.toArray(new RebateVO[0]), new String[] { "istatus", "tstamp" });
		}
		return bateVOs;
	}

	@Override
	public RebateVO queryById(RebateVO data, Integer opertype) throws DZFWarpException {
		String errmsg = "";
		RebateVO oldvo = (RebateVO) singleObjectBO.queryByPrimaryKey(RebateVO.class, data.getPk_rebate());
		if (oldvo != null) {
			if (oldvo.getDr() != null && oldvo.getDr() == 1) {
				errmsg = "返点单：" + data.getVbillcode() + "已经被删除";
			}
			if (data.getTstamp() == null || oldvo.getTstamp() == null) {
				errmsg = "返点单：" + data.getVbillcode() + "数据错误";
			}
			if (opertype == 1) {
				// if(data.getTstamp().compareTo(oldvo.getTstamp()) != 0){
				// errmsg = "返点单："+data.getVbillcode()+"数据发生变化，请重新查询后再次尝试";
				// }
				if (oldvo.getIstatus() != null && oldvo.getIstatus() != IStatusConstant.IREBATESTATUS_0
						&& oldvo.getIstatus() != IStatusConstant.IREBATESTATUS_4) {
					errmsg = "返点单：" + data.getVbillcode() + "状态不为待提交或已驳回状态，不能修改，请重新查询后再次尝试";
				}
			}
		} else {
			errmsg = "返点单：" + data.getVbillcode() + "数据错误";
		}
		if (!StringUtil.isEmpty(errmsg)) {
			throw new BusinessException(errmsg);
		}
		String sql = " nvl(dr,0) = 0 AND pk_bill = ? ORDER BY TS DESC ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(oldvo.getPk_rebate());
		WorkflowVO[] flowVOs = (WorkflowVO[]) singleObjectBO.queryByCondition(WorkflowVO.class, sql, spm);
		if (flowVOs != null && flowVOs.length > 0) {
			oldvo.setChildren(flowVOs);
		}
		// 展示赋值
		CorpVO corpvo = CorpCache.getInstance().get(null, oldvo.getPk_corp());
		if (corpvo != null) {
			oldvo.setCorpname(corpvo.getUnitname());
			oldvo.setVprovince(corpvo.getVprovince());
		}
		UserVO uservo = UserCache.getInstance().get(oldvo.getCoperatorid(), null);
		if (uservo != null) {
			oldvo.setVoperatorname(uservo.getUser_name());
		}
		if (oldvo.getIstatus() != null) {
			String vstatusname = "";
			// 0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回；
			switch (oldvo.getIstatus()) {
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
		oldvo.setVshowdate(oldvo.getVyear() + "-0" + oldvo.getIseason());
		return oldvo;
	}

	@Override
	public String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		String[] corps = pubser.getManagerCorp(cuserid, qrytype);
		if (corps != null && corps.length > 0) {
			String where = SqlUtil.buildSqlForIn(" t.pk_corp ", corps);
			sql.append(" AND ").append(where);
		} else {
			sql.append(" AND t.pk_corp is null \n");
		}
		return sql.toString();
	}

}
