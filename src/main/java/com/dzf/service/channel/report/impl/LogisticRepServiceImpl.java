package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.report.LogisticRepVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.ILogisticRepService;
import com.dzf.service.pub.IPubService;

@Service("rep_logistic")
public class LogisticRepServiceImpl implements ILogisticRepService{
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO = null;
	
    @Autowired
    private IPubService pubService = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LogisticRepVO> queryGoods(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQryGoods(pamvo);
		List<LogisticRepVO> list = (List<LogisticRepVO>) multBodyObjectBO.queryDataPage(LogisticRepVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		Map<String, String> opermap = pubService.getManagerMap(3);// 渠道运营
		Map<Integer, String> areaMap = pubService.getAreaMap(pamvo.getAreaname(), 3);//大区
		Map<String,List<ComboBoxVO>> comMap = getGoodsMap(pamvo);
		UserVO uvo;
		String getId;
		List<ComboBoxVO> getList;
		for (LogisticRepVO logisticRepVO : list) {
			QueryDeCodeUtils.decKeyUtil(new String[] { "corpname" }, logisticRepVO, 2);
			if (areaMap != null && !areaMap.isEmpty()) {
				getId= areaMap.get(logisticRepVO.getVprovince());
				if (!StringUtil.isEmpty(getId)) {
					logisticRepVO.setAreaname(getId);
				}
			}
			if (opermap != null && !opermap.isEmpty()) {
				getId = opermap.get(logisticRepVO.getPk_corp());
				if (!StringUtil.isEmpty(getId)) {
					uvo = UserCache.getInstance().get(getId, null);
					if (uvo != null) {
						logisticRepVO.setVoperater(uvo.getUser_name());// 渠道运营
					}
				}
			}
			if(comMap != null && !comMap.isEmpty()){
				getList = comMap.get(logisticRepVO.getPk_id());
				if (getList !=null && getList.size()>0) {
					logisticRepVO.setChildren(getList.toArray(new ComboBoxVO[getList.size()]));// 渠道运营
				}
			}
		}
		return list;
	}
	
	/**
	 * 获取出库单，商品map
	 * @param pamvo
	 * @return
	 */
	private Map<String,List<ComboBoxVO>> getGoodsMap(QryParamVO pamvo) {
		Map<String, List<ComboBoxVO>> map = new HashMap<String, List<ComboBoxVO>>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select g.vgoodsname name, sum(b.nnum) code,b.pk_stockout id");
		sql.append("  from cn_stockout_b b ");
		sql.append("  left join cn_stockout out on b.pk_stockout = out.pk_stockout ");
		sql.append("  left join bd_account ba on out.pk_corp = ba.pk_corp  ");
		sql.append("  left join cn_goods g on b.pk_goods = g.pk_goods ");
		sql.append(" where nvl(b.dr, 0) = 0 ");
		sql.append("   and nvl(out.dr, 0) = 0 ");
		sql.append("   and out.vstatus = 2 ");
		if(pamvo.getBegdate()!=null){
			sql.append("and substr(ddelivertime,0,10)>=? ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate()!=null){
			sql.append("and substr(ddelivertime,0,10)<= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (pamvo.getCorps() != null && pamvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(pamvo.getCorps());
			sql.append(" and out.pk_corp  in (" + corpIdS + ")");
		}
		if(!StringUtil.isEmpty(pamvo.getUser_code())){
			sql.append("and fastcode like ? ");
			spm.addParam("%"+pamvo.getUser_code()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVqrysql())){
			sql.append(pamvo.getVqrysql());
		}
		sql.append(" group by b.pk_goods, b.pk_stockout, g.vgoodsname ");
		List<ComboBoxVO> list = (List<ComboBoxVO>)singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(ComboBoxVO.class));
		List<ComboBoxVO> setList= new ArrayList<>();
		for (ComboBoxVO comboBoxVO : list) {
			if(!map.isEmpty() && map.containsKey(comboBoxVO.getId())){
				setList= new ArrayList<>();
				setList = map.get(comboBoxVO.getId());
				setList.add(comboBoxVO);
			}else{
				setList= new ArrayList<>();
				setList.add(comboBoxVO);
				map.put(comboBoxVO.getId(), setList);
			}
		}
		return map;
	}

	@Override
	public Integer qryGoodsTotal(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQryGoods(pamvo);
		return multBodyObjectBO.queryDataTotal(StockOutVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}
	
	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQryGoods(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select ba.pk_corp, ");
		sql.append("       ba.unitname corpname, ");
		sql.append("       ba.vprovince, ");
		sql.append("       pk_stockout,pk_id ");
		sql.append("       out.vreceivername, ");
		sql.append("       out.phone, ");
		sql.append("       out.vreceiveaddress, ");
		sql.append("       log.vname logisticsunit, ");
		sql.append("       fastcost, ");
		sql.append("       fastcode, ");
		sql.append("       ddelivertime, ");
		sql.append("       vmemo ");
		sql.append("  from cn_stockout out ");
		sql.append("  left join bd_account ba on out.pk_corp = ba.pk_corp ");
		sql.append("  left join cn_logistics log on out.pk_logistics = log.pk_logistics ");
		sql.append(" where nvl(out.dr, 0) = 0 ");
		sql.append("   and nvl(log.dr, 0) = 0 ");
		sql.append("   and out.vstatus = 2 ");
		if(pamvo.getBegdate()!=null){
			sql.append("and substr(ddelivertime,0,10)>=? ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate()!=null){
			sql.append("and substr(ddelivertime,0,10)<= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (pamvo.getCorps() != null && pamvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(pamvo.getCorps());
			sql.append(" and out.pk_corp  in (" + corpIdS + ")");
		}
		if(!StringUtil.isEmpty(pamvo.getUser_code())){
			sql.append("and fastcode like ? ");
			spm.addParam("%"+pamvo.getUser_code()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVqrysql())){
			sql.append(pamvo.getVqrysql());
		}
		sql.append(" order by ddelivertime desc ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LogisticRepVO> queryMateriel(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQryMateriel(pamvo);
		List<LogisticRepVO> list = (List<LogisticRepVO>) multBodyObjectBO.queryDataPage(LogisticRepVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		Map<String, String> opermap = pubService.getManagerMap(3);// 渠道运营
		Map<Integer, String> areaMap = pubService.getAreaMap(pamvo.getAreaname(), 3);//大区
		Map<String, List<ComboBoxVO>> matmap = getMaterielMap(pamvo);
		UserVO uvo;
		String getId;
		List<ComboBoxVO> getList;
		for (LogisticRepVO logisticRepVO : list) {
			QueryDeCodeUtils.decKeyUtil(new String[] { "corpname" }, logisticRepVO, 2);
			if (areaMap != null && !areaMap.isEmpty()) {
				getId= areaMap.get(logisticRepVO.getVprovince());
				if (!StringUtil.isEmpty(getId)) {
					logisticRepVO.setAreaname(getId);
				}
			}
			if (opermap != null && !opermap.isEmpty()) {
				getId = opermap.get(logisticRepVO.getPk_corp());
				if (!StringUtil.isEmpty(getId)) {
					uvo = UserCache.getInstance().get(getId, null);
					if (uvo != null) {
						logisticRepVO.setVoperater(uvo.getUser_name());// 渠道运营
					}
				}
			}
			if(matmap != null && !matmap.isEmpty()){
				getList = matmap.get(logisticRepVO.getPk_id());
				if (getList !=null && getList.size()>0) {
					logisticRepVO.setChildren(getList.toArray(new ComboBoxVO[getList.size()]));// 渠道运营
				}
			}
		}
		return list;
	}
	
	@Override
	public Integer qryMaterielTotal(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQryMateriel(pamvo);
		return multBodyObjectBO.queryDataTotal(StockOutVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}
	
	/**
	 * 获取物料map
	 * @param pamvo
	 * @return
	 */
	private Map<String,List<ComboBoxVO>> getMaterielMap(QryParamVO pamvo) {
		Map<String, List<ComboBoxVO>> map = new HashMap<String, List<ComboBoxVO>>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select m.vname name, sum(b.outnum) code,b.pk_materielbill id");
		sql.append("  from cn_materielbill_b b ");
		sql.append("  left join cn_materielbill mat on b.pk_materielbill = mat.pk_materielbill");
		sql.append("  left join bd_account ba on mat.pk_corp = ba.pk_corp ");
		sql.append("  left join cn_materiel m on m.pk_materiel = b.pk_materiel ");
		sql.append(" where nvl(b.dr, 0) = 0 ");
		sql.append("   and nvl(mat.dr, 0) = 0 ");
		sql.append("   and mat.vstatus = 3 ");
		if(pamvo.getBegdate()!=null){
			sql.append("and deliverdate >=? ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate()!=null){
			sql.append("and deliverdate <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (pamvo.getCorps() != null && pamvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(pamvo.getCorps());
			sql.append(" and mat.pk_corp  in (" + corpIdS + ")");
		}
		if(!StringUtil.isEmpty(pamvo.getUser_code())){
			sql.append("and fastcode like ? ");
			spm.addParam("%"+pamvo.getUser_code()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVqrysql())){
			sql.append(pamvo.getVqrysql());
		}
		sql.append(" group by b.pk_materiel, b.pk_materielbill, m.vname ");
		List<ComboBoxVO> list = (List<ComboBoxVO>)singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(ComboBoxVO.class));
		List<ComboBoxVO> setList= new ArrayList<>();
		for (ComboBoxVO comboBoxVO : list) {
			if(!map.isEmpty() && map.containsKey(comboBoxVO.getId())){
				setList= new ArrayList<>();
				setList = map.get(comboBoxVO.getId());
				setList.add(comboBoxVO);
			}else{
				setList= new ArrayList<>();
				setList.add(comboBoxVO);
				map.put(comboBoxVO.getId(), setList);
			}
		}
		return map;
	}
	
	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQryMateriel(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" select ba.pk_corp, ");
		sql.append("        ba.vprovince, ");
		sql.append("        ba.unitname corpname, ");
		sql.append("        pk_materielbill pk_id, ");
		sql.append("        mat.vreceiver vreceivername, ");
		sql.append("        mat.phone, ");
		sql.append("        mat.vaddress vreceiveaddress, ");
		sql.append("        log.vname logisticsunit, ");
		sql.append("        fastcost, ");
		sql.append("        fastcode, ");
		sql.append("        deliverdate, ");
		sql.append("        vmemo ");
		sql.append("   from cn_materielbill mat ");
		sql.append("   left join bd_account ba on mat.pk_corp = ba.pk_corp ");
		sql.append("   left join cn_logistics log on mat.pk_logistics = log.pk_logistics ");
		sql.append("  where nvl(mat.dr, 0) = 0 ");
		sql.append("    and nvl(log.dr, 0) = 0 ");
		sql.append("    and nvl(ba.dr, 0) = 0 ");
		sql.append("    and mat.vstatus = 3 ");
		if(pamvo.getBegdate()!=null){
			sql.append("and deliverdate >=? ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate()!=null){
			sql.append("and deliverdate <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (pamvo.getCorps() != null && pamvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(pamvo.getCorps());
			sql.append(" and mat.pk_corp  in (" + corpIdS + ")");
		}
		if(!StringUtil.isEmpty(pamvo.getUser_code())){
			sql.append("and fastcode like ? ");
			spm.addParam("%"+pamvo.getUser_code()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVqrysql())){
			sql.append(pamvo.getVqrysql());
		}
		sql.append(" order by deliverdate desc,mat.ts desc ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
}
