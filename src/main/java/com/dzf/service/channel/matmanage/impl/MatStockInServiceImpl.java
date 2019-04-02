package com.dzf.service.channel.matmanage.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInBVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatStockInService;
import com.dzf.service.pub.IBillCodeService;

@Service("matstockin")
public class MatStockInServiceImpl implements IMatStockInService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IBillCodeService billCode;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MaterielFileVO> queryComboBox() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pk_materiel ,vname  \n");
		sql.append("  FROM cn_materiel  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append(" order by doperatetime desc \n");
		return (List<MaterielFileVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(MaterielFileVO.class));
	}

	@Override
	public void saveStockIn(MaterielStockInVO data, UserVO uservo) {
		if (StringUtil.isEmpty(data.getPk_materielin())) {
			 data.setVbillcode((getMatcode(data)));
			 setDefaultValue(data,uservo);
			 //1.新增到入库单主表VO
			 data=(MaterielStockInVO) singleObjectBO.insertVO("000001", data);
			 //2.新增到入库单子表VO
             MaterielStockInBVO bvo = new MaterielStockInBVO();		
             bvo.setNcost(data.getCost());
             bvo.setNnum(data.getNum());
             bvo.setPk_materielin(data.getPk_materielin());
			 bvo.setPk_materiel(data.getPk_materiel());
			 
			 StringBuffer sql = new StringBuffer();
			 SQLParameter spm=new SQLParameter();
			 spm.addParam(data.getPk_materiel());
			 sql.append("   select vname,vunit \n ");
			 sql.append("         from cn_materiel \n");
			 sql.append("         where nvl(dr,0)=0 and pk_materiel =? \n");
			 MaterielFileVO mvo = (MaterielFileVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielFileVO.class));
			 if(mvo!=null){
				 bvo.setVname(mvo.getVname());
				 bvo.setVunit(mvo.getVunit());
			 }
             singleObjectBO.insertVO("000001", bvo);
		} else {
			 saveEdit(data);
		}
	}
	
    private void saveEdit(MaterielStockInVO data) {
		
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielin(), uuid, 60);
			String message;
			if (!lockKey) {
				message = "单据编码：" + data.getVbillcode() + ",其他用户正在操作此数据;<br>";
				throw new BusinessException(message);
			}
			MaterielStockInVO checkData = checkData(data.getPk_materielin(), data.getUpdatets());
			
			//1.修改入库主表
			String[] updates = {"vmemo", "stockdate" };
		    singleObjectBO.update(data, updates);
		    //2.修改入库子表
		    StringBuffer sql=new StringBuffer();
		    SQLParameter spm=new SQLParameter();
		    spm.addParam(data.getPk_materielin());
		    sql.append("  select pk_materielin_b \n");
		    sql.append("     from cn_materielin_b \n");
		    sql.append("     where nvl(dr,0)=0 and pk_materielin=? \n");
		    MaterielStockInBVO bvo = (MaterielStockInBVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielStockInBVO.class));
		    bvo.setNcost(data.getCost());
		    bvo.setNnum(data.getNum());
		    String[] updates2 = {"ncost", "nnum" };
		    singleObjectBO.update(bvo, updates2);
		    
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielin(), uuid);
		}
}

    /**
	 * 检查是否是最新数据
	 * @param pk_materielin
	 * @param updatets
	 * @return
	 */
	private MaterielStockInVO checkData(String pk_materielin, DZFDateTime updatets) {
		MaterielStockInVO vo = (MaterielStockInVO) singleObjectBO.queryByPrimaryKey(MaterielStockInVO.class, pk_materielin);
		if (!updatets.equals(vo.getUpdatets())) {
			throw new BusinessException("单据编码：" + vo.getVbillcode() + ",数据已发生变化;<br>");
		}
		return vo;
	}

	
	/**
	 * 获取单据编码
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private String getMatcode(MaterielStockInVO vo) throws DZFWarpException {
		String code;
		String str = "cgrk";
		DZFDate now = new DZFDate();
		MaxCodeVO mcvo = new MaxCodeVO();
		mcvo.setTbName(vo.getTableName());
		mcvo.setFieldName("vbillcode");
		mcvo.setPk_corp("000001");
		mcvo.setBillType(str + now.getYear() + now.getStrMonth());
		mcvo.setCorpIdField("pk_corp");
		mcvo.setDiflen(3);
		try {
			code = billCode.getDefaultCode(mcvo);
		} catch (Exception e) {
			throw new BusinessException("获取单据编码失败");
		}
		return code;
	}
	
	/**
	 * 设置默认值
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(MaterielStockInVO data,UserVO uservo) throws DZFWarpException {
		data.setCoperatorid(uservo.getCuserid());
		data.setDoperatetime(new DZFDateTime());
		data.setPk_corp("000001");
	}

	@Override
	public int queryTotalRow(MaterielStockInVO qvo) {
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	     return multBodyObjectBO.queryDataTotal(MaterielFileVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	private QrySqlSpmVO getQrySqlSpm(MaterielStockInVO pamvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("   SELECT r.pk_materielin,r.vbillcode,r.ntotalmny,r.stockdate, \n");
		sql.append("         r.vmemo,r.coperatorid,r.doperatetime, \n  ");
		sql.append("         d.vname wlname,d.vunit unit,d.nnum num,d.ncost cost \n");
		sql.append("         from  cn_materielin r \n");
		sql.append("         left join cn_materielin_b d on \n");
		sql.append("         r.pk_materielin = d.pk_materielin \n");
		sql.append("         where nvl(r.dr,0) =0 and nvl(d.dr,0) =0 \n");
		if (!StringUtil.isEmpty(pamvo.getPk_materiel())) {
			sql.append(" AND  d.pk_materiel = ? \n");
			spm.addParam(pamvo.getPk_materiel());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getBegindate())) {
			sql.append(" and substr(r.doperatetime,0,10) >= ? \n");
			spm.addParam(pamvo.getBegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getEnddate())) {
			sql.append(" and substr(r.doperatetime,0,10) <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" order by r.doperatetime desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
		
	}

	@Override
	public List<MaterielStockInVO> query(MaterielStockInVO qvo) {
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	        List<MaterielStockInVO> list = (List<MaterielStockInVO>) multBodyObjectBO.queryDataPage(MaterielStockInVO.class, sqpvo.getSql(),
	                sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
	        return list;
	}

	@Override
	public MaterielStockInVO queryDataById(String id) {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(  "select l.pk_materielin,l.updatets,l.vmemo,l.stockdate,l.ntotalmny, \n ");
		sql.append(  "   b.ncost cost,b.nnum num,b.vname wlname, \n");
		sql.append(  "   m.pk_materiel \n");
		sql.append(  "   from cn_materielin l \n");
		sql.append(  "   left join cn_materielin_b b on \n");
		sql.append(  "   l.pk_materielin = b.pk_materielin \n");
		sql.append(  "   left join cn_materiel m on \n");
		sql.append(  "   b.pk_materiel = m.pk_materiel \n");
		sql.append(  "   where nvl(l.dr,0)=0 and nvl(b.dr,0)=0 and nvl(m.dr,0)=0 \n");
		sql.append(  "   and l.pk_materielin =? \n");
		spm.addParam(id);
		MaterielStockInVO vo = (MaterielStockInVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielStockInVO.class));
		if(vo!=null){
			return vo;
		}
		return null;
	}

	@Override
	public void delete(MaterielStockInVO data) {
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielin(), uuid, 60);
			String message;
			if (!lockKey) {
				message = "单据编码：" + data.getVbillcode() + ",其他用户正在操作此数据;<br>";
				throw new BusinessException(message);
			}
			SQLParameter spm = new SQLParameter();
			spm.addParam(data.getPk_materielin());
			
			//1.删除入库单子表数据
			String sql="delete from cn_materielin_b where pk_materielin = ? \n";
			singleObjectBO.executeUpdate(sql, spm);
			//2.删除入库单主表数据
			sql="delete from cn_materielin where pk_materielin = ? \n";
			int i = singleObjectBO.executeUpdate(sql, spm);
			if(i == 0){
				throw new BusinessException("入库单删除失败");
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielin(), uuid);
		}
	}
}
