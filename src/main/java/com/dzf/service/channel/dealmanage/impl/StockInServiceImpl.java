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
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.channel.dealmanage.StockInBVO;
import com.dzf.model.channel.dealmanage.StockInVO;
import com.dzf.model.channel.stock.StockNumVO;
import com.dzf.model.channel.stock.SupplierVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.dealmanage.ICarryOverService;
import com.dzf.service.channel.dealmanage.IGoodsManageService;
import com.dzf.service.channel.dealmanage.IStockInService;
import com.dzf.service.pub.IBillCodeService;

@Service("stockinser")
public class StockInServiceImpl implements IStockInService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IBillCodeService billCodeSer;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IGoodsManageService manser;
	
	@Autowired
	private ICarryOverService  carryover;
	
	@Override
	public Integer queryTotalRow(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(StockInVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StockInVO> query(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(pamvo);
		List<StockInVO> list = (List<StockInVO>) multBodyObjectBO.queryDataPage(StockInVO.class, sqpvo.getSql(),
				sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[]{"coperatorname"}, list, 1);
		}
		return list;
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
		sql.append("SELECT sin.*,u.user_name coperatorname  \n");
		sql.append("  FROM cn_stockin sin \n");
		sql.append("  LEFT JOIN sm_user u ON sin.coperatorid = u.cuserid  \n") ; 
		sql.append(" WHERE nvl(sin.dr, 0) = 0  \n");
		sql.append("   AND sin.pk_corp = ? \n");
		spm.addParam(pamvo.getPk_corp());
		if (!StringUtil.isEmpty(pamvo.getCuserid())) {
			sql.append("   AND sin.coperatorid = ? \n");
			spm.addParam(pamvo.getCuserid());
		}
		if (!StringUtil.isEmpty(pamvo.getVbillcode())) {
			sql.append("   AND sin.vbillcode like ? \n");
			spm.addParam("%" + pamvo.getVbillcode() + "%");
		}
		if(pamvo.getQrytype() != null && pamvo.getQrytype() != -1){
			sql.append("   AND sin.vstatus = ? \n");
			spm.addParam(pamvo.getQrytype());
		}
		if(pamvo.getBegdate() != null){
			sql.append("   AND sin.dstockdate >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate() != null ){
			sql.append("   AND sin.dstockdate <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		if(!StringUtil.isEmpty(pamvo.getBeginperiod())){
			sql.append(" AND substr(sin.dconfirmtime, 0, 10) >= ? \n");
			spm.addParam(pamvo.getBeginperiod());
		}
		if(!StringUtil.isEmpty(pamvo.getEndperiod())){
			sql.append(" AND substr(sin.dconfirmtime, 0, 10) <= ? \n");
			spm.addParam(pamvo.getEndperiod());
		}
		sql.append(" ORDER BY sin.ts DESC");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierVO> querySupplierRef(QryParamVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_supplier, vname FROM cn_supplier WHERE nvl(dr, 0) = 0");
		if (!StringUtil.isEmpty(pamvo.getCorpcode())) {
			sql.append(" AND vname like ? ");
			spm.addParam("%" + pamvo.getCorpcode() + "%");
		}
		sql.append(" ORDER BY ts DESC");
		return (List<SupplierVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(SupplierVO.class));
	}

	@Override
	public StockInVO save(StockInVO hvo, String pk_corp, StockInBVO[] bodyVOs) throws DZFWarpException {
		checkMnyBeforeSave(hvo, bodyVOs);
		if (StringUtil.isEmpty(hvo.getPk_stockin())) {
			hvo.setVbillcode(getVbillcode(hvo));
			hvo = (StockInVO) singleObjectBO.saveObject(pk_corp, hvo);
			return hvo;
		} else {
			return saveEdit(hvo, pk_corp);
		}
	}
	
	/**
	 * 保存前金额校验
	 * @throws DZFWarpException
	 */
	private void checkMnyBeforeSave(StockInVO hvo, StockInBVO[] bVOs) throws DZFWarpException {
		if(bVOs != null && bVOs.length > 0){
			DZFDouble ntotalmny = DZFDouble.ZERO_DBL;
			DZFDouble ntotalcost = DZFDouble.ZERO_DBL;
			Map<String,GoodsVO> gmap = getGoodsMap();
			GoodsVO gdvo = null;
			for(StockInBVO bvo : bVOs){
				if(bvo.getTstamp() != null){
					//1、新增时，校验商品是否发生变化（商品的规格、型号修改，也会更新主表时间戳）
					if(gmap.containsKey(bvo.getPk_goods())){
						gdvo = gmap.get(bvo.getPk_goods());
						if(gdvo.getUpdatets().compareTo(bvo.getTstamp()) != 0){
							throw new BusinessException("商品"+bvo.getVgoodsname()+"发生变化，请刷新界面后，重新操作");
						}
					}else{
						throw new BusinessException("商品"+bvo.getVgoodsname()+"发生变化，请刷新界面后，重新操作");
					}
				}
				
				//2、采购总金额汇总计算
				ntotalmny = SafeCompute.add(ntotalmny, bvo.getNtotalmny());
				
				//3、总成本汇总计算
				ntotalcost = SafeCompute.add(ntotalcost, bvo.getNtotalcost());
			}
			if(ntotalmny.compareTo(hvo.getNtotalmny()) != 0){
				throw new BusinessException("采购总金额计算错误");
			}
			if(ntotalcost.compareTo(hvo.getNtotalcost()) != 0){
				throw new BusinessException("总成本计算错误");
			}
		}else{
			throw new BusinessException("表体数据不能为空");
		}
	}
	
	/**
	 * 获取商品信息
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,GoodsVO> getGoodsMap() throws DZFWarpException {
		Map<String,GoodsVO> map = new HashMap<String,GoodsVO>();
		String sql = " nvl(dr,0) = 0 ";
		GoodsVO[] gdVOs = (GoodsVO[]) singleObjectBO.queryByCondition(GoodsVO.class, sql, null);
		if(gdVOs != null){
			for(GoodsVO gdvo : gdVOs){
				map.put(gdvo.getPk_goods(), gdvo);
			}
		}
		return map;
	}
	
	/**
	 * 获取单据编码
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private String getVbillcode(StockInVO hvo) throws DZFWarpException {
		String code;
		DZFDate date = new DZFDate();
		String year = String.valueOf(date.getYear());
		String str = "rk" + year + date.getStrMonth();
		MaxCodeVO mcvo = new MaxCodeVO();
		mcvo.setTbName(hvo.getTableName());
		mcvo.setFieldName("vbillcode");
		mcvo.setPk_corp("000001");
		mcvo.setBillType(str);
		mcvo.setCorpIdField("pk_corp");
		mcvo.setDiflen(3);
		try {
			code = billCodeSer.getDefaultCode(mcvo);
		} catch (Exception e) {
			throw new BusinessException("获取单据编码失败");
		}
		return code;
	}

	/**
	 * 修改保存
	 * 
	 * @param hvo
	 * @param pk_corp
	 * @return
	 */
	private StockInVO saveEdit(StockInVO hvo, String pk_corp) {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(hvo.getTableName(), hvo.getPk_stockin(), uuid, 120);
			checkBeforeOperate(hvo, pk_corp, 1);
			hvo = (StockInVO) singleObjectBO.saveObject(pk_corp, hvo);

		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(hvo.getTableName(), hvo.getPk_stockin(), uuid);
		}
		return hvo;
	}

	/**
	 * 操作前校验
	 * @param hvo
	 * @param pk_corp
	 * @param opertype  1：修改保存；2：删除（需要返回主子信息）；3：确认入库（需要返回主子信息）；4：取消确认入库（需要返回主子信息）；
	 * @throws DZFWarpException
	 */
	private StockInVO checkBeforeOperate(StockInVO hvo, String pk_corp, Integer opertype) throws DZFWarpException {
		StockInVO oldvo = null;
		if(opertype == 1 || opertype == 2){
			oldvo = queryById(hvo.getPk_stockin(), pk_corp, 2);//不查询子表
		}else if(opertype == 3 || opertype == 4){
			oldvo = queryById(hvo.getPk_stockin(), pk_corp, 1);//查询子表
		}
		
		if (oldvo != null) {
			if (oldvo.getUpdatets().compareTo(hvo.getUpdatets()) != 0) {
				throw new BusinessException("界面数据发生变化，请刷新后再次尝试");
			}
			if(IStatusConstant.ISTOCKINSTATUS_2 == hvo.getVstatus()){
				if(opertype == 2){
					throw new BusinessException("该入库单已经确认，不允许删除");
				}else if(opertype == 3){
					throw new BusinessException("该入库单已经确认，不允许再次确认");
				}
			}
		} else {
			throw new BusinessException("界面数据发生变化，请刷新后再次尝试");
		}
		return oldvo;
	}

	/**
	 * 通过主键查询数据
	 * 
	 * @param pk_stockin
	 * @param pk_corp
	 * @param qrytype  1：详情查询、修改查询（包含子表）；2：普通查询（不包含子表）；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public StockInVO queryById(String pk_stockin, String pk_corp, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n");
		sql.append("  FROM cn_stockin  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_stockin = ? \n");
		spm.addParam(pk_stockin);
		sql.append("   AND pk_corp = ? \n");
		spm.addParam(pk_corp);
		List<StockInVO> list = (List<StockInVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(StockInVO.class));
		if (list != null && list.size() > 0) {
			StockInVO hvo = list.get(0);
			if (qrytype != 2) {
				StockInBVO[] bVOs = queryBody(pk_stockin, pk_corp);
				hvo.setChildren(bVOs);
			}
			return hvo;
		}
		return null;
	}

	/**
	 * 查询子表数据
	 * 
	 * @param pk_stockin
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private StockInBVO[] queryBody(String pk_stockin, String pk_corp) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT b.*,  \n");
		sql.append("       r.vname AS vsuppliername,  \n");
		sql.append("       s.vgoodsname || ' ' || '(' || c.invspec || c.invtype || ')' AS vgoodsname  \n");
		sql.append("  FROM cn_stockin_b b  \n");
		sql.append("  LEFT JOIN cn_supplier r ON b.pk_supplier = r.pk_supplier  \n");
		sql.append("  LEFT JOIN cn_goods s ON b.pk_goods = s.pk_goods  \n");
		sql.append("  LEFT JOIN cn_goodsspec c ON b.pk_goodsspec = c.pk_goodsspec  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(r.dr, 0) = 0  \n");
		sql.append("   AND nvl(s.dr, 0) = 0  \n");
		sql.append("   AND nvl(c.dr, 0) = 0  \n");
		sql.append("   AND b.pk_stockin = ? \n");
		spm.addParam(pk_stockin);
		sql.append("   AND b.pk_corp = ? \n");
		spm.addParam(pk_corp);
		List<StockInBVO> list = (List<StockInBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(StockInBVO.class));
		if (list != null && list.size() > 0) {
			return list.toArray(new StockInBVO[0]);
		}
		return null;
	}

	@Override
	public void delete(StockInVO pamvo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(pamvo.getTableName(), pamvo.getPk_stockin(), uuid, 120);
			
			checkBeforeOperate(pamvo, pamvo.getPk_corp(), 2);
			SQLParameter spm = new SQLParameter();
			spm.addParam(pamvo.getPk_corp());
			spm.addParam(pamvo.getPk_stockin());
			
			String sql = " DELETE FROM cn_stockin_b WHERE pk_corp = ? AND pk_stockin = ? ";
			singleObjectBO.executeUpdate(sql, spm);
			
			sql = " DELETE FROM cn_stockin WHERE pk_corp = ? AND pk_stockin = ? ";
			int res = singleObjectBO.executeUpdate(sql, spm);
			if(res == 0){
				throw new BusinessException("入库单删除失败");
			}
			
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(pamvo.getTableName(), pamvo.getPk_stockin(), uuid);
		}
	}

	@Override
	public StockInVO updateconfirm(StockInVO stvo, String cuserid) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(stvo.getTableName(), stvo.getPk_stockin(), uuid, 120);
			stvo = checkBeforeOperate(stvo, stvo.getPk_corp(), 3);
			carryover.checkIsOper(new DZFDateTime(),1);
			StockInBVO[] bVOs = (StockInBVO[]) stvo.getChildren();
			for(StockInBVO bvo : bVOs){
				updateStockNum(bvo, cuserid);
			}
			stvo.setVstatus(IStatusConstant.ISTOCKINSTATUS_2);
			stvo.setUpdatets(new DZFDateTime());
			stvo.setVconfirmid(cuserid);
			stvo.setDconfirmtime(new DZFDateTime());
			singleObjectBO.update(stvo, new String[]{"vstatus","vconfirmid","dconfirmtime"});
			return stvo;
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(stvo.getTableName(), stvo.getPk_stockin(), uuid);
		}
	}
	
	/**
	 * 更新仓库入库数量
	 * @param bvo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void updateStockNum(StockInBVO bvo, String cuserid) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n");
		sql.append("  FROM cn_stocknum  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_corp = ?  \n");
		sql.append("   AND pk_warehouse = ?  \n");
		sql.append("   AND pk_goods = ?  \n");
		sql.append("   AND pk_goodsspec = ? \n");
		spm.addParam(bvo.getPk_corp());
		spm.addParam(bvo.getPk_warehouse());
		spm.addParam(bvo.getPk_goods());
		spm.addParam(bvo.getPk_goodsspec());
		List<StockNumVO> list = (List<StockNumVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(StockNumVO.class));
		if(list != null && list.size() > 0){
			StockNumVO numvo = list.get(0);
			updateAddStock(numvo, bvo);
		}else if(list != null && list.size() > 1){
			throw new BusinessException("库存商品数量错误");
		}else{
			StockNumVO numvo = new StockNumVO();
			numvo.setPk_corp(bvo.getPk_corp());
			numvo.setPk_warehouse(bvo.getPk_warehouse());
			numvo.setPk_goods(bvo.getPk_goods());
			numvo.setPk_goodsspec(bvo.getPk_goodsspec());
			numvo.setIstocknum(bvo.getNnum());
			numvo.setCoperatorid(cuserid);
			numvo.setDoperatedate(new DZFDate());
			numvo.setDr(0);
			singleObjectBO.saveObject(bvo.getPk_corp(), numvo);
		}

	}
	
	/**
	 * 更新库存（增加）
	 * @param numvo
	 * @param bvo
	 * @throws DZFWarpException
	 */
	private void updateAddStock(StockNumVO numvo, StockInBVO bvo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(numvo.getTableName(), numvo.getPk_goods() + "" + numvo.getPk_goodsspec(), uuid, 60);
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			
			sql.append("UPDATE cn_stocknum \n");
			sql.append("   SET istocknum = nvl(istocknum,0) + ?  \n");
			spm.addParam(bvo.getNnum());
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
			LockUtil.getInstance().unLock_Key(numvo.getTableName(), numvo.getPk_goods() + "" + numvo.getPk_goodsspec(), uuid);
		}
	}
	
	/**
	 * 更新库存（减少）
	 * @param bvo
	 * @throws DZFWarpException
	 */
	private void updateSubStock(StockInBVO bvo) throws DZFWarpException {
		Map<String,String> namemap = getGoodsName();
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_stocknum", bvo.getPk_goods() + "" + bvo.getPk_goodsspec(), uuid, 60);
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			
			sql.append("UPDATE cn_stocknum \n");
			sql.append("   SET istocknum = nvl(istocknum,0) - ?  \n");
			spm.addParam(bvo.getNnum());
			sql.append(" WHERE nvl(dr,0) = 0 \n");
			sql.append("   AND pk_corp = ? \n");
			spm.addParam(bvo.getPk_corp());
			
			sql.append("   AND pk_goods = ? ");
			spm.addParam(bvo.getPk_goods());
			sql.append("   AND pk_goodsspec = ? ");
			spm.addParam(bvo.getPk_goodsspec());
			
			sql.append("   AND nvl(istocknum,0) - nvl(isellnum,0) >= ?");
			spm.addParam(bvo.getNnum());
			
			int res = singleObjectBO.executeUpdate(sql.toString(), spm);
			if (res != 1) {
				String name = namemap.get(bvo.getPk_goods() + "" + bvo.getPk_goodsspec());
				throw new BusinessException("商品【"+name+"】可取消入库数量不足，请检查订单后重试；");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_stocknum", bvo.getPk_goods() + "" + bvo.getPk_goodsspec(), uuid);
		}
	}
	
	/**
	 * 查询商品名称
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public Map<String,String> getGoodsName() throws DZFWarpException {
		Map<String,String> map = new HashMap<String,String>();
		List<GoodsBoxVO> list = manser.queryComboBox();
		if(list != null && list.size() > 0){
			String key = "";
			for(GoodsBoxVO bvo : list){
				key = bvo.getPk_goods() + "" + bvo.getId();
				map.put(key, bvo.getName());
			}
		}
		return map;
	}

	@Override
	public void saveSupplier(StockInVO pamvo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			boolean flag = isMeasExist(pamvo);
			if(flag){
				throw new BusinessException("供应商"+pamvo.getVmemo()+"已经存在");
			}
			LockUtil.getInstance().tryLockKey("cn_supplier", pamvo.getVmemo(), uuid, 120);
			SupplierVO supvo = new SupplierVO();
			supvo.setPk_corp(pamvo.getPk_corp());
			supvo.setVname(pamvo.getVmemo());
			supvo.setCoperatorid(pamvo.getCoperatorid());
			supvo.setDoperatedate(new DZFDate());
			supvo.setDr(0);
			supvo.setVcode(getSupcode(supvo));
			singleObjectBO.saveObject(pamvo.getPk_corp(), supvo);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_supplier", pamvo.getVmemo(), uuid);
		}
	}
	
	/**
	 * 获取供应商编码
	 * @param hvo
	 * @return
	 * @throws DZFWarpException
	 */
	private String getSupcode(SupplierVO supvo) throws DZFWarpException {
		String code;
		String str = "gys";
		MaxCodeVO mcvo = new MaxCodeVO();
		mcvo.setTbName(supvo.getTableName());
		mcvo.setFieldName(supvo.getVcode());
		mcvo.setPk_corp(supvo.getPk_corp());
		mcvo.setBillType(str);
		mcvo.setCorpIdField("pk_corp");
		mcvo.setDiflen(3);
		try {
			code = billCodeSer.getDefaultCode(mcvo);
		} catch (Exception e) {
			throw new BusinessException("获取单据编码失败");
		}
		return code;
	}
	
	/**
	 * 判断供应商是否存在
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private boolean isMeasExist(StockInVO pamvo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vname from cn_supplier where nvl(dr,0) = 0 ");
		if (!StringUtil.isEmptyWithTrim(pamvo.getVmemo())) {
			sql.append(" and vname = ? ");
			sp.addParam(pamvo.getVmemo());
		} else {
			throw new BusinessException("供应商名称不能为空");
		}
		List<GoodsVO> list = (List<GoodsVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(GoodsVO.class));
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public StockInVO updateCancelConf(StockInVO stvo, String cuserid) throws DZFWarpException {
		stvo = checkBeforeOperate(stvo, stvo.getPk_corp(), 4);
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(stvo.getTableName(), stvo.getPk_stockin(), uuid, 60);
			StockInBVO[] bVOs = (StockInBVO[]) stvo.getChildren();
			if(bVOs == null || bVOs.length == 0 ){
				throw new BusinessException("入库单表体数据为空");
			}
			carryover.checkIsOper(stvo.getDconfirmtime(),2);
			Map<String, StockInBVO> map = new HashMap<String, StockInBVO>();
			String key = "";
			StockInBVO countvo = null;
			for(StockInBVO bvo : bVOs){
				key = bvo.getPk_goods() + "" + bvo.getPk_goodsspec();
				if(!map.containsKey(key)){
					countvo = new StockInBVO();
					countvo.setPk_corp(bvo.getPk_corp());
					countvo.setPk_goods(bvo.getPk_goods());
					countvo.setPk_goodsspec(bvo.getPk_goodsspec());
					countvo.setNnum(bvo.getNnum());
					map.put(key, countvo);
				}else{
					countvo = map.get(key);
					countvo.setNnum(ToolsUtil.addInteger(countvo.getNnum(), bvo.getNnum()));
					map.put(key, countvo);
				}
			}
			StockInBVO stbvo = null;
			for(String pk_goods_spec : map.keySet()){
				stbvo = map.get(pk_goods_spec);
				updateSubStock(stbvo);
			}
			
			stvo.setVstatus(IStatusConstant.ISTOCKINSTATUS_1);
			stvo.setUpdatets(new DZFDateTime());
			stvo.setVconfirmid(null);
			stvo.setDconfirmtime(null);
			singleObjectBO.update(stvo, new String[]{"vstatus","vconfirmid","dconfirmtime"});
			
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(stvo.getTableName(), stvo.getPk_stockin(), uuid);
		}
		
		return stvo;
	}

}
