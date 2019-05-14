package com.dzf.service.channel.dealmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.stock.StockOutBVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.dealmanage.ICarryOverService;
import com.dzf.service.channel.dealmanage.IOtherOutService;
import com.dzf.service.pub.IBillCodeService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("outOther")
public class OtherOutServiceImpl implements IOtherOutService{
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IBillCodeService billCode;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO= null;
	
	@Autowired
	private ICarryOverService  carryover;
	
	@Autowired
    private IUserService userser;
	
	@Override
	public Integer queryTotalRow(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(StockOutVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StockOutVO> query(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		List<StockOutVO> list = (List<StockOutVO>) multBodyObjectBO.queryDataPage(StockOutVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		for (StockOutVO stockOutVO : list) {
			stockOutVO.setCoperatname(CodeUtils1.deCode(stockOutVO.getCoperatname()));
		}
		return list;
	}
	
	@Override
	public StockOutVO queryByID(String soutid) throws DZFWarpException {
		StockOutVO retvo = (StockOutVO) singleObjectBO.queryByPrimaryKey(StockOutVO.class, soutid);
		if(retvo == null){
			throw new BusinessException("很抱歉，该出库单已被删除!");
		}
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append("SELECT b.nnum,b.nmny,b.nprice,b.pk_stockout_b,");
		corpsql.append(" g.pk_goods,s.pk_goodsspec,s.invspec,s.invtype,");
		corpsql.append(" g.vgoodsname ||' '|| '(' || s.invspec || s.invtype || ')' AS vgoodsname,");
		corpsql.append(" nvl(n.istocknum,0)-nvl(n.isellnum,0)+b.nnum AS usenum ");
		corpsql.append("  FROM cn_stockout_b b");
		corpsql.append("  LEFT JOIN cn_goods g ON b.pk_goods= g.pk_goods");
		corpsql.append("  LEFT JOIN cn_goodsspec s ON b.pk_goodsspec= s.pk_goodsspec");
		corpsql.append("  LEFT JOIN cn_stocknum n ON b.pk_goods = n.pk_goods and b.pk_goodsspec=n.pk_goodsspec \n") ;
		corpsql.append(" where nvl(b.dr,0)=0 and b.pk_stockout= ?  ");
//		and nvl(n.istocknum,0)>nvl(n.isellnum,0) )
		sp.addParam(soutid);
		List<StockOutBVO> bvos = (List<StockOutBVO>) singleObjectBO.executeQuery(corpsql.toString(),sp,new BeanListProcessor(StockOutBVO.class));
		retvo.setChildren(bvos.toArray(new StockOutBVO[bvos.size()]));
		return retvo;
	}
	
	@Override
	public StockOutVO queryForLook(String soutid) throws DZFWarpException {
		StockOutVO retvo = (StockOutVO) singleObjectBO.queryByPrimaryKey(StockOutVO.class, soutid);
		if(retvo == null){
			throw new BusinessException("很抱歉，该出库单已被删除!");
		}
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append("SELECT b.nnum,b.nmny,b.nprice,b.pk_stockout_b,");
		corpsql.append(" g.pk_goods,s.pk_goodsspec,s.invspec,s.invtype, g.vgoodsname");
		corpsql.append("  FROM cn_stockout_b b");
		corpsql.append("  LEFT JOIN cn_goods g ON b.pk_goods= g.pk_goods");
		corpsql.append("  LEFT JOIN cn_goodsspec s ON b.pk_goodsspec= s.pk_goodsspec");
		corpsql.append(" where nvl(b.dr,0)= 0  and b.pk_stockout= ? ");
		sp.addParam(soutid);
		List<StockOutBVO> bvos = (List<StockOutBVO>) singleObjectBO.executeQuery(corpsql.toString(),sp,new BeanListProcessor(StockOutBVO.class));
		retvo.setChildren(bvos.toArray(new StockOutBVO[bvos.size()]));
		UserVO uvo =  userser.queryUserJmVOByID(retvo.getCoperatorid());
		if (uvo != null) {
			retvo.setCoperatname(uvo.getUser_name());
		}
		return retvo;
	}
	
	@Override
	public List<StockOutBVO> queryGoodsAble() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT c.pk_goodsspec ,  \n") ;
		sql.append("       c.invspec,  \n") ; 
		sql.append("       c.invtype,  \n") ; 
		sql.append("       c.pk_goods,  \n") ; 
		sql.append("       s.vgoodsname ||' '|| '(' || c.invspec || c.invtype || ')' AS vgoodsname,  \n") ; 
		sql.append("       c.nprice,  \n") ; 
		sql.append("       nvl(n.istocknum,0)-nvl(n.isellnum,0) AS usenum  \n") ; 
		sql.append("  FROM cn_goodsspec c  \n") ; 
		sql.append("  LEFT JOIN cn_goods s ON c.pk_goods = s.pk_goods  \n") ; 
		sql.append("  LEFT JOIN cn_stocknum n ON c.pk_goods = n.pk_goods and c.pk_goodsspec=n.pk_goodsspec \n") ; 
		sql.append(" WHERE nvl(c.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(s.dr, 0) = 0 \n");
		sql.append("   AND nvl(n.dr, 0) = 0 \n");
		sql.append("   AND nvl(n.istocknum,0)>nvl(n.isellnum,0) \n");
		List<StockOutBVO> vos=(List<StockOutBVO>)singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(StockOutBVO.class));
		return vos;
	}

	@Override
	public void saveNew(StockOutVO vo) throws DZFWarpException {
		StockOutBVO[] bvos =(StockOutBVO[]) vo.getChildren();
		//1、保存主表
		vo.setVstatus(0);
		vo.setItype(1);
		vo.setDoperatedate(new DZFDateTime());
		setDefaultCode(vo);
		vo.setChildren(null);
		vo=(StockOutVO)singleObjectBO.insertVO(vo.getFathercorp(), vo);
		//2、保存子表
		for (StockOutBVO stockOutBVO : bvos) {
			updateStockNum(stockOutBVO);
			stockOutBVO.setPk_stockout(vo.getPk_stockout());
			stockOutBVO.setFathercorp(vo.getFathercorp());
			stockOutBVO.setPk_corp(vo.getPk_corp());
			stockOutBVO.setPk_warehouse(IStatusConstant.CK_ID);//仓库主键
		}
		singleObjectBO.insertVOArr(vo.getFathercorp(), bvos);
	}
	
	@Override
	public void saveEdit(StockOutVO vo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(vo.getTableName(), vo.getPk_stockout(),uuid, 120);
			if(!lockKey){
				String message="单据编码："+vo.getVbillcode()+",其他用户正在操作此数据;<br>";
				throw new BusinessException(message);
			}
			checkData(vo);
			
			//子表汇总
			StockOutBVO[] bvos =(StockOutBVO[]) vo.getChildren();
			HashMap<String, StockOutBVO> map = new HashMap<>();
			String mapKey;
			StockOutBVO getVO;
			for (StockOutBVO stockOutBVO : bvos) {
				stockOutBVO.setPk_stockout(vo.getPk_stockout());
				stockOutBVO.setFathercorp(vo.getFathercorp());
				stockOutBVO.setPk_corp(vo.getPk_corp());
				stockOutBVO.setPk_warehouse(IStatusConstant.CK_ID);//仓库主键
				
				mapKey = stockOutBVO.getPk_goods()+stockOutBVO.getPk_goodsspec();
				if(!map.containsKey(mapKey)){
					getVO = new StockOutBVO();
					getVO.setPk_goods(stockOutBVO.getPk_goods());
					getVO.setPk_goodsspec(stockOutBVO.getPk_goodsspec());
					getVO.setNnum(stockOutBVO.getNnum());
					getVO.setVgoodsname(stockOutBVO.getVgoodsname());
					getVO.setInvspec(stockOutBVO.getInvspec());
					getVO.setInvtype(stockOutBVO.getInvtype());
					map.put(mapKey, getVO);
				}else{
					getVO=map.get(mapKey);
					getVO.setNnum(getVO.getNnum()+stockOutBVO.getNnum());
				}
			}
			//查询原先子表
			List<StockOutBVO> oldChildren = queryOldChildren(vo.getPk_stockout());
			for (StockOutBVO stockOutBVO : oldChildren) {
				mapKey = stockOutBVO.getPk_goods()+stockOutBVO.getPk_goodsspec();
				if(!map.containsKey(mapKey)){
					getVO = new StockOutBVO();
					getVO.setPk_goods(stockOutBVO.getPk_goods());
					getVO.setPk_goodsspec(stockOutBVO.getPk_goodsspec());
					getVO.setNnum(-stockOutBVO.getNnum());
					getVO.setVgoodsname(stockOutBVO.getVgoodsname());
					getVO.setInvspec(stockOutBVO.getInvspec());
					getVO.setInvtype(stockOutBVO.getInvtype());
					map.put(mapKey, getVO);
				}else{
					getVO=map.get(mapKey);
					getVO.setNnum(getVO.getNnum()-stockOutBVO.getNnum());
				}
			}
			
			//更新库存表
			for(StockOutBVO stockOutBVO : map.values()){
				updateStockNum(stockOutBVO);
			}
			
			//1、更新主表；
			singleObjectBO.update(vo,new String[]{"vgetdate","vmemo"});
			//2、删除子表
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			sql.append("DELETE FROM cn_stockout_b  \n") ;
			sql.append(" WHERE  pk_stockout = ? \n") ; 
			spm.addParam(vo.getPk_stockout());
			singleObjectBO.executeUpdate(sql.toString(), spm);
			//3、插入子表	
			singleObjectBO.insertVOArr(vo.getFathercorp(),bvos);
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_stockout(),uuid);
		}	
	}
	
	/**
	 * 查询出库单子表信息（删除、修改）
	 * @param pk_stockout
	 * @return
	 */
	private List<StockOutBVO> queryOldChildren(String pk_stockout) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT b.pk_goods,b.pk_goodsspec,b.nnum \n") ;
		sql.append(" FROM cn_stockout_b b \n") ; 
		sql.append(" WHERE  b.pk_stockout = ?   \n") ; 
		spm.addParam(pk_stockout);
		List<StockOutBVO> list=(List<StockOutBVO>)singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(StockOutBVO.class));
		return list;
	}

	@Override
	public void delete(StockOutVO vo) throws DZFWarpException {
		if(vo.getVstatus()!=0){
			throw new BusinessException("只有待确认的出库单可以删除");
		}
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(vo.getTableName(), vo.getPk_stockout(),uuid, 120);
			if(!lockKey){
				String message="单据编码："+vo.getVbillcode()+",其他用户正在操作此数据;<br>";
				throw new BusinessException(message);
			}
			checkData(vo);
			
			List<StockOutBVO> oldChildren = queryOldChildren(vo.getPk_stockout());
			for (StockOutBVO stockOutBVO : oldChildren) {
				stockOutBVO.setNnum(-stockOutBVO.getNnum());
				updateStockNum(stockOutBVO);
			}
			
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			sql.append("DELETE FROM cn_stockout  \n") ;
			sql.append(" WHERE  pk_stockout = ? \n") ; 
			spm.addParam(vo.getPk_stockout());
			singleObjectBO.executeUpdate(sql.toString(), spm);
			
			sql = new StringBuffer();
			sql.append("DELETE FROM cn_stockout_b  \n") ;
			sql.append(" WHERE  pk_stockout = ? \n") ; 
			singleObjectBO.executeUpdate(sql.toString(), spm);
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_stockout(),uuid);
		}
	}
	
	/**
	 * 校验商品库存数量是否充足
	 * @param bvos
	 */
	private void updateStockNum(StockOutBVO stockOutBVO)  throws DZFWarpException {
		StringBuffer sql =null;
		SQLParameter spm =null;
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_stocknum",stockOutBVO.getPk_goods()+stockOutBVO.getPk_goodsspec(),uuid, 120);
			sql = new StringBuffer();
			sql.append(" update cn_stocknum num ");
			sql.append("    set num.isellnum = nvl(num.isellnum,0) + ? ");
			sql.append("  where nvl(num.dr, 0) = 0 ");
			sql.append("    and num.pk_goods = ? ");
			sql.append("    and num.pk_goodsspec = ? ");
			sql.append("    and istocknum-nvl(num.isellnum,0) >=? ");
			spm = new SQLParameter();
			spm.addParam(stockOutBVO.getNnum());
			spm.addParam(stockOutBVO.getPk_goods());
			spm.addParam(stockOutBVO.getPk_goodsspec());
			spm.addParam(stockOutBVO.getNnum());
			int updates = singleObjectBO.executeUpdate(sql.toString(),spm);
			if(updates==0){
				sql=new StringBuffer();
				if(!StringUtil.isEmpty(stockOutBVO.getInvspec())){
					sql.append(stockOutBVO.getInvspec());
				}
				if(!StringUtil.isEmpty(stockOutBVO.getInvtype())){
					sql.append(stockOutBVO.getInvtype());
				}
				throw new BusinessException("商品名称为"+stockOutBVO.getVgoodsname()+",规格型号为"+sql.toString()+"库存不足");
			}
		}catch (Exception e) {
            if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            else
                throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_stocknum",stockOutBVO.getPk_goods()+stockOutBVO.getPk_goodsspec(),uuid);
		}
	}

	@Override
	public void updateCommit(StockOutVO vo) throws DZFWarpException {
		checkStatus(",不能确认出库;",vo);
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(vo.getTableName(), vo.getPk_stockout(),uuid, 120);
			if(!lockKey){
				throw new BusinessException("单据编码："+vo.getVbillcode()+",其他用户正在操作此数据;<br>");
			}
			checkData(vo);
			carryover.checkIsOper(new DZFDateTime(),1);
			//1、更新出库单主表
			vo.setVstatus(1);
			vo.setDconfirmtime(new DZFDateTime());
			singleObjectBO.update(vo, new String[]{"vstatus","vconfirmid","dconfirmtime"});
			//2、该出库单子表按照商品规格、商品、库存合并
			List<StockOutBVO> vos = queryChildren(vo.getPk_stockout());
			//3、更新库存表 
			for (StockOutBVO stockOutBVO : vos) {
				updateOutNum(stockOutBVO,1);
			}
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_stockout(),uuid);
		}
	}
	
	@Override
	public void updateCancel(StockOutVO vo) throws DZFWarpException {
		checkStatus(",不能取消确认;",vo);
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(vo.getTableName(), vo.getPk_stockout(),uuid, 120);
			if(!lockKey){
				String message="单据编码："+vo.getVbillcode()+",其他用户正在操作此数据;<br>";
				throw new BusinessException(message);
			}
			checkData(vo);
			carryover.checkIsOper(vo.getDconfirmtime(),2);
			//1、更新出库单主表
			vo.setVstatus(0);
			vo.setDconfirmtime(null);
			vo.setVconfirmid(null);
			singleObjectBO.update(vo, new String[]{"vstatus","vconfirmid","dconfirmtime"});
			//2、该出库单子表按照商品规格、商品、库存合并
			List<StockOutBVO> vos = queryChildren(vo.getPk_stockout());
			//3、更新库存表 
			for (StockOutBVO stockOutBVO : vos) {
				updateOutNum(stockOutBVO,2);
			}
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_stockout(),uuid);
		}
	}
	
	/**
	 * 出库单子表按照商品规格、商品、库存合并（出库单主表主键）
	 * @param pk_stockout
	 * @return
	 */
	private List<StockOutBVO> queryChildren(String pk_stockout) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_stockout);
		sql.append(" select b.pk_goods, b.pk_goodsspec, sum(b.nnum) nnum ");
		sql.append("   from cn_stockout_b b ");
		sql.append("  where nvl(b.dr, 0) = 0 ");
		sql.append("    and b.pk_stockout = ? ");
		sql.append("  group by b.pk_goods, b.pk_goodsspec ");
		List<StockOutBVO> vos=(List<StockOutBVO>)singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(StockOutBVO.class));
		return vos;
	}

	/**
	 * 更新库存表 ioutnum字段(type,1:确认；2：取消确认)
	 * @param vo
	 * @throws DZFWarpException
	 */
	private void updateOutNum(StockOutBVO vo,int type) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("cn_stocknum",vo.getPk_goods()+vo.getPk_goodsspec(),uuid, 120);
			SQLParameter spm = new SQLParameter();
			spm.addParam(vo.getNnum());
			spm.addParam(vo.getPk_goods());
			spm.addParam(vo.getPk_goodsspec());
			
			StringBuffer sql = new StringBuffer();
			sql.append(" update cn_stocknum num ");
			sql.append("    set num.ioutnum  = nvl(num.ioutnum, 0) ");
			if(type==1){
				sql.append("+ ?");
			}else{
				sql.append("- ?");
			}
			sql.append("  where  num.pk_goods=? and num.pk_goodsspec =?  ");
			singleObjectBO.executeUpdate(sql.toString(),spm);
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("cn_stocknum",vo.getPk_goods()+vo.getPk_goodsspec(),uuid);
		}
	}
	
	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select c.vbillcode,");
		sql.append("       c.pk_stockout,");
		sql.append("       c.vgetdate,");
		sql.append("       c.vmemo,");
		sql.append("       c.vstatus,");
		sql.append("       c.coperatorid,");
		sql.append("       c.doperatedate,");
		sql.append("       c.dconfirmtime,");
		sql.append("       c.updatets,");
		sql.append("       c.itype,");
		sql.append("       u.user_name coperatname");
		sql.append("  from cn_stockout c");
		sql.append("  LEFT JOIN sm_user u ON c.coperatorid = u.cuserid ") ; 
		sql.append(" where nvl(c.dr,0)=0 and nvl(c.itype,0)=1 ");
		if(pamvo.getBegdate()!=null){
			sql.append("and vgetdate >=? ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate()!=null){
			sql.append("and vgetdate <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if(!StringUtil.isEmpty(pamvo.getBeginperiod())){
			sql.append("and substr(c.dconfirmtime,0,10)>=? ");
			spm.addParam(pamvo.getBeginperiod());
		}
		if(!StringUtil.isEmpty(pamvo.getEndperiod())){
			sql.append("and substr(c.dconfirmtime,0,10)<= ? ");
			spm.addParam(pamvo.getEndperiod());
		}
		if( pamvo.getQrytype()!=null && pamvo.getQrytype()!=-1){
			sql.append("and c.vstatus=? ");
			spm.addParam(pamvo.getQrytype());
		}
		if(!StringUtil.isEmpty(pamvo.getUser_code())){
			sql.append("and c.vbillcode like ? ");
			spm.addParam("%"+pamvo.getUser_code()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getCuserid())){
			sql.append("and c.coperatorid = ? ");
			spm.addParam(pamvo.getCuserid());
		}
		sql.append(" order by c.ts desc ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 是否是最新数据
	 * @param qryvo
	 */
	private void checkData(StockOutVO qryvo) throws DZFWarpException{
		StockOutVO vo =(StockOutVO) singleObjectBO.queryByPrimaryKey(StockOutVO.class, qryvo.getPk_stockout());
		if(vo==null){
			throw new BusinessException("该单据编码已被其它用户删除");
		}
		if(!vo.getUpdatets().equals(qryvo.getUpdatets())){
			throw new BusinessException("单据编码："+vo.getVbillcode()+",数据已发生变化;<br>");
		}
		if(vo.getItype()==null || vo.getItype()!=1){
			throw new BusinessException("对不起,非法操作!");
		}
	}
	
	/**
	 * 校验状态能否操作
	 * @param msg
	 * @param vo
	 * @throws DZFWarpException
	 */
	private void checkStatus(String msg,StockOutVO vo) throws DZFWarpException{
		HashMap<Integer,String> map=new HashMap<>();
		map.put(0, "待确认");
		map.put(1, "待发货");
		String message="订单编号："+vo.getVbillcode()+"是"+map.get(vo.getVstatus())+msg+"<br>";
		if(msg.equals(",不能确认出库;")){
			if(vo.getVstatus()!=0){
				throw new BusinessException(message);
			}
		}
		if(msg.equals(",不能取消确认;")){
			if(vo.getVstatus()!=1){
				throw new BusinessException(message);
			}
		}
	}
	
	/**
	 * 设置默认出库单编码
	 * @param vo
	 */
	private void setDefaultCode(StockOutVO vo) {
		MaxCodeVO mcvo=new MaxCodeVO();
		DZFDate now =new DZFDate();
		mcvo.setTbName(vo.getTableName());
		mcvo.setFieldName("vbillcode");
		mcvo.setPk_corp(vo.getFathercorp());
		mcvo.setBillType("dck"+now.getYear()+now.getStrMonth());
		mcvo.setCorpIdField("fathercorp");
		mcvo.setDiflen(3);
		vo.setVbillcode(billCode.getDefaultCode(mcvo));
	}

}
