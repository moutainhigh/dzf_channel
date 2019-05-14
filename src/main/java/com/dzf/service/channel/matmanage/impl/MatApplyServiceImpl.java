package com.dzf.service.channel.matmanage.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatApplyService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("matapply")
public class MatApplyServiceImpl implements IMatApplyService {
    
    private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private IPubService pubser;
	
	@Autowired
    private IUserService userser;
	

	@Override
	public int queryTotalRow(QryParamVO qvo,MatOrderVO parm,String stype) throws DZFWarpException{
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,parm,stype,null,null);
		return multBodyObjectBO.queryDataTotal(MatOrderVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> query(QryParamVO qvo,MatOrderVO pamvo, UserVO uservo,String stype)  throws DZFWarpException {
		String vpro = "";
		String vcorp = "";
		List<String> corp = new ArrayList<String>();
		if(stype!=null && !"1".equals(stype)){
			//物料审核，申请数据权限
            List<ChnAreaBVO> list = queryPro(uservo,stype);
            if(list!=null && list.size()>0){
            	for (ChnAreaBVO vo : list) {
    				if("Y".equals(vo.getIsCharge().toString())){//是否是省市负责人
    					vpro = vpro + "," +vo.getVprovince();
    				}else{
    					if(vo.getPk_corp()!=null){
    						corp.add(vo.getPk_corp());
    					}
    				}
    			}
            }
			if(vpro!=null && !StringUtil.isEmpty(vpro)){
				vpro = vpro.substring(1);
				vpro = "("+vpro+")";
			}
			if(corp!=null && corp.size()>0){
				for (String  c : corp) {
					vcorp = vcorp + "," + "'" + c + "'";
				}
				vcorp = vcorp.substring(1);
				vcorp = "(" + vcorp + ")";
			}
			
		}
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,pamvo,stype,vpro,vcorp);
		List<MatOrderVO> list = (List<MatOrderVO>)singleObjectBO.executeQuery(sqpvo.getSql(),
				sqpvo.getSpm(),new BeanListProcessor(MatOrderVO.class));
		HashMap<String, UserVO> map = userser.queryUserMap(uservo.getPk_corp(), true);
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if(list!=null && list.size()>0){
			for (MatOrderVO mvo : list) {
				if (mvo.getCoperatorid() != null) {
					uservo = map.get(mvo.getCoperatorid());
					if(uservo!=null){
						mvo.setApplyname(uservo.getUser_name());
					}
				}
				uservo = marmap.get(mvo.getFathercorp());
				if (uservo != null) {
					mvo.setVmanagername(uservo.getUser_name());// 渠道经理
					mvo.setVmanagerid(uservo.getCuserid());
				}
				String[] updates= {"vmanagerid"};
				singleObjectBO.update(mvo, updates);
			}
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname", "corpname" }, list, 1);
		}
		if(stype!=null && !"1".equals(stype)){
			if(StringUtil.isEmpty(vpro) && StringUtil.isEmpty(vcorp)){//没有数据可以查看
				list = null;
			}
		}
		return list;
	}

	private QrySqlSpmVO getQrySqlSpm(QryParamVO qvo,MatOrderVO pamvo,
			String stype, String vpro,String vcorp)  throws DZFWarpException {
		
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT distinct bi.pk_materielbill,  \n") ;
		sql.append("                bi.vcontcode,  \n") ; 
		//sql.append("                bi.aname,  \n") ; 
		sql.append("                bi.vaddress,  \n") ; 
		sql.append("                bi.vreceiver,  \n") ; 
		sql.append("                bi.phone,  \n") ; 
		sql.append("                bi.fastcode,  \n") ; 
		sql.append("                bi.fastcost,  \n") ; 
		sql.append("                bi.deliverdate,  \n") ; 
		sql.append("                bi.vmemo,  \n") ; 
		sql.append("                bi.vreason,  \n") ; 
		sql.append("                bi.vstatus,  \n") ; 
		sql.append("                bi.doperatedate,  \n") ; 
		sql.append("                bi.coperatorid,  \n") ; 
		sql.append("                bi.applydate,  \n") ; 
		sql.append("                bi.fathercorp,  \n") ; 
		sql.append("                bi.corpname,  \n") ; 
		sql.append("                bi.vmanagerid,  \n") ; 
		sql.append("                bi.ts,  \n") ; 
		sql.append("                b.vname,  \n") ; 
		sql.append("                b.vunit,  \n") ; 
		sql.append("                nvl(b.outnum, 0) outnum,  \n") ; 
		sql.append("                nvl(b.applynum, 0) applynum,  \n") ; 
		sql.append("                log.vname logname,  \n") ; 
		sql.append("                ba.vprovname proname,  \n") ; 
		sql.append("                ba.vprovince,  \n") ;
		sql.append("                c.areaname \n") ; 
		sql.append("  from cn_materielbill bi  \n") ; 
		sql.append("  left join cn_materielbill_b b on bi.pk_materielbill = b.pk_materielbill  \n") ; 
		sql.append("  left join cn_logistics log on log.pk_logistics = bi.pk_logistics  \n") ; 
		sql.append("  left join cn_chnarea_b ba on ba.vprovince = bi.vprovince  \n") ; 
		sql.append("  left join cn_chnarea c on c.pk_chnarea = ba.pk_chnarea  \n") ; 
		if(stype!=null && "1".equals(stype)){
			sql.append("  left join bd_account co on co.pk_corp = bi.fathercorp  \n") ; 
		}
		sql.append("  where nvl(bi.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(log.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(ba.dr, 0) = 0 and ba.type = 1 \n") ; 
		sql.append("   and nvl(c.dr, 0) = 0 and c.type = 1 \n") ;
		if(stype!=null && "1".equals(stype)){
			sql.append("   and nvl(co.dr, 0) = 0  \n") ; 
		}
		
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			sql.append(" AND  bi.corpname like ? ");
			spm.addParam("%" + pamvo.getCorpname() + "%");
		}
		if (!StringUtil.isEmpty(pamvo.getVmanagerid())) {
			sql.append(" AND  bi.vmanagerid = ? ");
			spm.addParam(pamvo.getVmanagerid());
		}
		if(stype!=null && "1".equals(stype)){//物料处理界面
			sql.append("   AND bi.vstatus in (2,3) \n");
		}
		if (pamvo.getVstatus() != null && pamvo.getVstatus() != 0) {
			sql.append("   AND bi.vstatus = ? \n");
			spm.addParam(pamvo.getVstatus());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getBegindate())) {
			sql.append(" and bi.doperatedate >= ? ");
			spm.addParam(pamvo.getBegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getEnddate())) {
			sql.append(" and bi.doperatedate <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplybegindate())) {
			sql.append(" and bi.applydate>= ? ");
			spm.addParam(pamvo.getApplybegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplyenddate())) {
			sql.append(" and bi.applydate <= ? ");
			spm.addParam(pamvo.getApplyenddate());
		}
		if(!StringUtil.isEmpty(vpro) && !StringUtil.isEmpty(vcorp)){
			sql.append(" and (ba.vprovince in "+vpro);
			sql.append(" or ba.pk_corp in "+vcorp+")");
		}
		if(!StringUtil.isEmpty(vpro) && StringUtil.isEmpty(vcorp)){
			sql.append(" and ba.vprovince in "+vpro);
		}
		if(!StringUtil.isEmpty(vcorp) && StringUtil.isEmpty(vpro)){
			sql.append(" and ba.pk_corp in "+vcorp);
		}
		
		if(!StringUtil.isEmpty(qvo.getVqrysql())){
			sql.append(qvo.getVqrysql());
		}
		
		sql.append(" order by bi.ts desc");
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderBVO> queryNumber(MatOrderVO pamvo)  throws DZFWarpException {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select DISTINCT b.vname,  \n") ;
		sql.append("       b.vunit  \n") ; 
		sql.append("  from cn_materielbill_b b  \n") ; 
		sql.append("  left join cn_materielbill c on c.pk_materielbill = b.pk_materielbill  \n") ; 
		sql.append("  left join cn_materiel m on m.pk_materiel = b.pk_materiel  \n") ; 
		sql.append("  left join bd_account co on co.pk_corp = c.pk_corp  \n") ; 
		sql.append(" where nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(c.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(m.dr, 0) = 0  \n") ; 
		sql.append("   and m.isseal = 1  \n") ; 

		if (!StringUtil.isEmptyWithTrim(pamvo.getBegindate())) {
			sql.append(" and c.doperatedate >= ? ");
			spm.addParam(pamvo.getBegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getEnddate())) {
			sql.append(" and c.doperatedate <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplybegindate())) {
			sql.append(" and c.applydate>= ? ");
			spm.addParam(pamvo.getApplybegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplyenddate())) {
			sql.append(" and c.applydate <= ? ");
			spm.addParam(pamvo.getApplyenddate());
		}
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			sql.append(" AND co.unitname like ? ");
			spm.addParam("%" + pamvo.getCorpname() + "%");
		}
		if (pamvo.getVstatus() != null && pamvo.getVstatus() != 0) {
			sql.append("   AND c.vstatus = ? \n");
			spm.addParam(pamvo.getVstatus());
		}

		List<MatOrderBVO> bvoList = (List<MatOrderBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(MatOrderBVO.class));
		return bvoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielFileVO> queryMatFile() throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		sql.append("select vname, vunit  \n") ;
		sql.append("  from cn_materiel  \n") ; 
		sql.append(" where nvl(dr, 0) = 0  \n") ; 
		sql.append("   and isseal = 1  \n");

		List<MaterielFileVO> bvoList = (List<MaterielFileVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(MaterielFileVO.class));
		return bvoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> queryAllProvince()  throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append("  select region_id vprovince,region_name pname,parenter_id parid \n");
		sql.append("     from ynt_area \n");
		sql.append("     where nvl(dr,0) = 0  \n");
		sql.append("     and parenter_id = 1 \n");

		List<MatOrderVO> plist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(MatOrderVO.class));
		return plist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> queryCityByProId(Integer pid)  throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pid);
		sql.append("  select region_id vcity,region_name cityname,parenter_id parid \n");
		sql.append("     from ynt_area \n");
		sql.append("     where nvl(dr,0) = 0  \n");
		sql.append("     and parenter_id = ? \n");

		List<MatOrderVO> clist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(MatOrderVO.class));
	    return clist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> queryAreaByCid(Integer cid)  throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(cid);
		sql.append("  select region_id varea,region_name countryname,parenter_id parid \n");
		sql.append("     from ynt_area \n");
		sql.append("     where nvl(dr,0) = 0  \n");
		sql.append("     and parenter_id = ? \n");

		List<MatOrderVO> clist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(MatOrderVO.class));
		return clist;
	}

	@Override
	public MatOrderVO showDataByCorp(String corpid)   throws DZFWarpException{

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(corpid);
		sql.append("  select b.citycounty,b.vprovince,b.vcity,b.varea, \n");
		sql.append("     b.legalbodycode vreceiver,b.phone1 phone \n");
		sql.append("     from bd_account b \n");
		sql.append("     where nvl(b.dr,0) = 0 \n");
		sql.append("     and vprovince is not null \n");
		sql.append("     and b.pk_corp = ? \n");

		MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanProcessor(MatOrderVO.class));
		if (mvo != null) {
			setCitycountry(mvo);
			String[] str = new String[] { "vreceiver", "phone" };
			QueryDeCodeUtils.decKeyUtil(str, mvo, 1);
			return mvo;
		}
		return null;
	}

	@Override
	public String saveApply(MatOrderVO vo, UserVO uservo,
			MatOrderBVO[] bvos,String type,String stype,String kind)  throws DZFWarpException {
		
		String message = "";
		if(!StringUtil.isEmpty(vo.getPk_materielbill())){
			MatOrderVO mvo=queryById(vo.getPk_materielbill());
			if(mvo.getVstatus()!=null && mvo.getVstatus()==4){//已驳回的修改保存
				save(vo, uservo, bvos, type);
				return null;
			}
		}
		if(StringUtil.isEmpty(kind)){//不需要校验（修改保存，详情保存等）
			save(vo, uservo, bvos, type);
			return null;
		}
		if(type!=null && "1".equals(type)){//发货保存
			save(vo, uservo, bvos, type);
			return null;
		}
		if(stype!=null && "1".equals(stype)){//提示后保存
			save(vo, uservo, bvos, type);
			return null;
		}else{
			
			 //获取上个季度时间
			 try {
				 if(vo.getDedubegdate()!=null && vo.getDuduenddate()!=null){
					 String lastQuarter = getLastQuarter(vo.getDedubegdate(), vo.getDuduenddate());
					 String[] quarter = lastQuarter.split(",");
					 vo.setDedubegdate(quarter[0]);
					 vo.setDuduenddate(quarter[1]);
				 }
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}
			
			 //校验
			 message = checkIsInfo(vo,bvos,message);
			if(message.isEmpty()){
				save(vo, uservo, bvos, type);
			}
		}
		
		return message;
  }
	
	
	@SuppressWarnings("unchecked")
	private String checkIsInfo(MatOrderVO vo,MatOrderBVO[] bvos,String message)  throws DZFWarpException{
		if(bvos!=null && bvos.length>0){
			for (MatOrderBVO mbvo :bvos) {
				MaterielFileVO mvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class,mbvo.getPk_materiel());
				if(mvo!=null && mvo.getIsappl()!=null){
					if(mvo.getIsappl() == 1){//勾选了申请条件
						Integer passNum = null;
							//获取上季度提单审核通过数
							passNum	= queryContNum(vo,vo.getFathercorp());
						
						StringBuffer csql = new StringBuffer();
						SQLParameter cspm=new SQLParameter();
						cspm.addParam(vo.getFathercorp());
						cspm.addParam(mbvo.getPk_materiel());
						csql.append("  select b.vname,b.applynum,b.outnum,b.succnum \n");
						csql.append("      from cn_materielbill l  \n");
						csql.append("      left join cn_materielbill_b b on  \n");
						csql.append("      l.pk_materielbill = b.pk_materielbill \n");
						csql.append("      where nvl(l.dr,0) = 0 \n");
						csql.append("      and nvl(b.dr,0) = 0 \n");
						csql.append("      and l.fathercorp = ? \n");
						csql.append("      and b.pk_materiel = ? \n");
						
						if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
							csql.append(" and l.deliverdate >= ? ");
							cspm.addParam(vo.getDedubegdate());
						}
						if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
							csql.append(" and l.deliverdate <= ? ");
							cspm.addParam(vo.getDeduenddate());
						}
						List<MatOrderVO> mvoList = (List<MatOrderVO>) singleObjectBO.executeQuery(csql.toString(), cspm, new BeanListProcessor(MatOrderVO.class));
						
						Integer sumout = 0;//上季度实发数量
						//Integer sumsucc = 0;//上季度申请通过数量
						
						if(mvoList!=null && mvoList.size()>0){
							for (MatOrderVO ovo : mvoList) {
								if(ovo.getOutnum()==null){
									ovo.setOutnum(0);
								}
								if(ovo.getSuccnum()==null){
									ovo.setSuccnum(0);
								}
								sumout = sumout + ovo.getOutnum();
								//sumsucc = sumsucc + ovo.getSuccnum();
							}
						}
						Integer ssumout= (int) (0.7*sumout);
						if(sumout == 0){//上季度没有发货
							//可以申请保存
						}else{
							if(passNum!=null&&ssumout!=null){
								if(passNum >= ssumout){
									//可以申请保存
								}else{
									//提示再申请保存
									mbvo.setSumapply(sumout);
									//mbvo.setSumsucc(sumsucc);
									message = message + "该加盟商"+mbvo.getVname()+
											"上季度申请数"+mbvo.getSumapply()+"，"+
											"提单审核通过数"+passNum+"，"+
											"不符合该物料的申请条件，望知悉"+"<br/>";

								}
							}
							
						}
					}else{
						//不需要校验
					}
				}
			}
		}
		
		return message;
	}
	
	
	/**
	 * 查询合同提单量
	 * @param paramvo
	 * @param corpid
	 * @return
	 * @throws DZFWarpException
	 */
	private Integer queryContNum(MatOrderVO vo, String corpid) throws DZFWarpException {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT  \n");
		// 合同数量去掉补提单合同数
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus,0) != 5   \n");
		if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
			sql.append("        AND SUBSTR(t.deductdata, 1, 10) >= ? \n");
			spm.addParam(vo.getDedubegdate());
		}
		if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
			sql.append("        AND SUBSTR(t.deductdata, 1, 10) < = ? \n");
			spm.addParam(vo.getDeduenddate());
		}
		
		if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
			sql.append("        AND ( ( nvl(SUBSTR(t.dchangetime, 1, 10),'1970-01-01') >= ? \n");
			spm.addParam(vo.getDedubegdate());
		}
		if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
			sql.append("        AND nvl(SUBSTR(t.dchangetime, 1, 10),'1970-01-01')  <= ? \n");
			spm.addParam(vo.getDeduenddate());
		}
		
		if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
			sql.append("        AND t.vdeductstatus != 10 ) OR nvl(SUBSTR(t.dchangetime, 1, 10),'1970-01-01') < ? \n");
			spm.addParam(vo.getDedubegdate());
		}
		if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
			sql.append("      OR nvl(SUBSTR(t.dchangetime, 1, 10),'1970-01-01') > ? ) \n");
			spm.addParam(vo.getDeduenddate());
		}
		
		sql.append("             THEN 1  \n");
		sql.append("             WHEN nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus,0) != 5 \n");
		sql.append("                  AND t.vdeductstatus = 10  \n");
		if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
			sql.append("        AND SUBSTR(t.dchangetime, 1, 10) >= ? \n");
			spm.addParam(vo.getDedubegdate());
		}
		if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
			sql.append("        AND SUBSTR(t.dchangetime, 1, 10) < = ? \n");
			spm.addParam(vo.getDeduenddate());
		}
		
		if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
			sql.append("         AND nvl(SUBSTR(t.deductdata, 1, 10),'1970-01-01') < ? \n");
			spm.addParam(vo.getDedubegdate());
		}
		if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
			sql.append("        AND nvl(SUBSTR(t.deductdata, 1, 10),'1970-01-01') > ?  \n");
			spm.addParam(vo.getDeduenddate());
		}
		
		sql.append("             THEN  -1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END)  AS num  \n");
		sql.append("  FROM cn_contract t \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(ct.dr, 0) = 0 \n");
		//sql.append("   AND nvl(ct.isncust, 'N') = 'N' \n");
		sql.append("   AND t.vdeductstatus in (?, ?, ?) \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		
		sql.append("   AND t.pk_corp = ? \n");
		spm.addParam(corpid);
		
		
		if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
			sql.append("        AND (  SUBSTR(t.deductdata, 1, 10) >= ?  \n");
			spm.addParam(vo.getDedubegdate());
		}
		if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
			sql.append("         AND SUBSTR(t.deductdata, 1, 10) <= ? OR \n");
			spm.addParam(vo.getDeduenddate());
		}
		
		if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
			sql.append("          SUBSTR(t.dchangetime, 1, 10)  >= ?  \n");
			spm.addParam(vo.getDedubegdate());
		}
		if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
			sql.append("         AND SUBSTR(t.dchangetime, 1, 10) <= ? ) \n");
			spm.addParam(vo.getDeduenddate());
		}
		sql.append("   GROUP BY t.pk_corp \n");
		
		CustCountVO countvo = (CustCountVO) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanProcessor(CustCountVO.class));
		Integer num = null;
		if(countvo!=null && countvo.getNum()!=null){
			num = countvo.getNum();
		}
		return num;
	}
	
	@Override
	public void editSave(MatOrderVO data) {
		
		String uuid = UUID.randomUUID().toString();
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>");
			}
			MatOrderVO mvo=queryById(data.getPk_materielbill());
 			if(mvo.getUpdatets()!=null){
 				data.setUpdatets(mvo.getUpdatets());
 			}
			checkData(data.getPk_materielbill(), data.getUpdatets());
			
			data.setCitycounty(data.getPname()+"-"+data.getCityname()+"-"+data.getCountryname());
			String[] updates = {"vprovince","vcity","varea","citycounty",
					"vaddress","vreceiver","phone","fastcost"};
    		singleObjectBO.update(data, updates);
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielbill(), uuid);
		}
	}

	
	private void saveEdit(MatOrderVO data,MatOrderBVO[] bvos,String type,UserVO uservo)  throws DZFWarpException {
    	if(type==null){
    		checkIsOperOrder(data.getVstatus(),"只有待审批或已驳回状态的申请单支持修改！");
    	}
		String uuid = UUID.randomUUID().toString();
		String msg = "";
		String mmsg = "";
		Integer sumnum = 0;
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>");
			}
			
		    checkData(data.getPk_materielbill(), data.getUpdatets());
			if(type!=null && "1".equals(type)){//发货
				if(bvos!=null && bvos.length>0){
					for (MatOrderBVO mbvo : bvos) {
						if(mbvo.getOutnum()==null){
							mbvo.setOutnum(0);
						}
						msg = checkIsApply(mbvo.getPk_materiel(),mbvo.getOutnum());
						mmsg = mmsg + msg;
						sumnum = sumnum + mbvo.getOutnum();
					}
					if(!StringUtil.isEmpty(mmsg)){
						throw new BusinessException(mmsg);
					}
					if(sumnum == 0){
						throw new BusinessException("总实发数量不可为0");
					}
				}
				String sql = "select pk_logistics,vname logname from cn_logistics "
		    			+ "where pk_logistics = ? ";
		    	SQLParameter spm=new SQLParameter();
		    	spm.addParam(data.getLogname());
		    	MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql, spm, new BeanProcessor(MatOrderVO.class));
		    	if(mvo!=null){
		    		data.setPk_logistics(mvo.getPk_logistics());
		    		data.setLogname(mvo.getLogname());
		    	}
		    	
				data.setVstatus(3);
				data.setDeliverid(uservo.getCuserid());
				data.setCitycounty(data.getPname()+"-"+data.getCityname()+"-"+data.getCountryname());
		    	String[] supdates ={"vstatus","pk_logistics","fastcode","fastcost",
						"deliverid","deliverdate","vprovince","vcity","varea","citycounty",
						"vaddress","vreceiver","phone"};
		    	
		    	singleObjectBO.update(data, supdates);
		    	
		    }else{
		    	//1.修改主订单
		    	if(data.getVstatus()==4){//已驳回的修改
		    		data.setVstatus(1);
		    		String[] updates = {"vcontcode", "fathercorp", "corpname",
							"vprovince","vcity","varea",
							"vaddress","vreceiver","phone","vmemo",
							"applydate","vstatus"};
		    		singleObjectBO.update(data, updates);
		    	}else{
		    		String[] updates = {"vcontcode", "fathercorp", "corpname",
							"vprovince","vcity","varea",
							"vaddress","vreceiver","phone","vmemo",
							"applydate"};
				    singleObjectBO.update(data, updates);
		    	}
				
		    }
		
		    //2.修改子订单
			
			//删除原有子订单
			SQLParameter spm = new SQLParameter();
			spm.addParam(data.getPk_materielbill());
			String sql = "delete from cn_materielbill_b where pk_materielbill = ? ";
			singleObjectBO.executeUpdate(sql, spm);
			
			//增加修改后的子订单
		    List<MatOrderBVO> bvolist = Arrays.asList(bvos);
		    if(bvolist!=null && bvolist.size()>0){
		    	  for (MatOrderBVO bvo : bvolist) {
				    	bvo.setPk_materielbill(data.getPk_materielbill());
				    	singleObjectBO.insertVO("000001", bvo);
				    	
				    	if(type!=null && "1".equals(type)){//发货
				    		//修改物料档案发货数量
					    	if(!StringUtil.isEmpty(bvo.getPk_materiel())){
					    		MaterielFileVO mfvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, bvo.getPk_materiel());
						    	if(mfvo!=null && mfvo.getOutnum()!=null){
						    		mfvo.setOutnum(mfvo.getOutnum()+bvo.getOutnum());
						    	}
						    	String[] updates = {"outnum"};
						    	singleObjectBO.update(mfvo, updates);
					    	}
				    	}
				    
					}
		    }
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielbill(), uuid);
		}
  }
     
     /**
      * 保存
      * @param vo
      * @param uservo
      * @param bvos
      * @param type
      */
     private void save(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos,String type)  throws DZFWarpException{
    	 
    	 if (StringUtil.isEmpty(vo.getPk_materielbill())) {// 新增保存
 			setDefaultValue(vo, uservo);
 			// 1.新增到订单主表VO
 			vo.setPk_corp("000001");
 			if(isInteger(vo.getPname())  &&
 					isInteger(vo.getCityname())  &&
 					isInteger(vo.getCountryname())){
 				vo.setVprovince(Integer.parseInt(vo.getPname()));
 				vo.setVcity(Integer.parseInt(vo.getCityname()));
 				vo.setVarea(Integer.parseInt(vo.getCountryname()));
 			}
 			if ("市辖区".equals(vo.getCityname()) || "市".equals(vo.getCityname()) || "县".equals(vo.getCityname())) {
 				vo.setCitycounty(vo.getPname() + "-" + vo.getCountryname());
 			} else {
 				vo.setCitycounty(vo.getPname() + "-" + vo.getCityname() + "-" + vo.getCountryname());
 			}
 			vo = (MatOrderVO) singleObjectBO.insertVO("000001", vo);
 			// 2.新增到订单子表VO
 			for (MatOrderBVO bvo : bvos) {
 				bvo.setPk_materielbill(vo.getPk_materielbill());
 				bvo.setSuccnum(0);//审核通过数 默认为0
 				singleObjectBO.insertVO("000001", bvo);
 			}
 		} else {
 			// 编辑保存
 			MatOrderVO mvo=queryById(vo.getPk_materielbill());
 			if(mvo.getUpdatets()!=null){
 				vo.setUpdatets(mvo.getUpdatets());
 				vo.setVstatus(mvo.getVstatus());
 			}
 			 saveEdit(vo,bvos,type,uservo);
 		}
     }
      
     /**
 	 * 检查是否是最新数据
 	 * @param pk_materiel
 	 * @param updatets
 	 * @return
 	 */
 	private MatOrderVO checkData(String pk_materielbill, DZFDateTime updatets)  throws DZFWarpException {
 		MatOrderVO vo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, pk_materielbill);
 		if (!updatets.equals(vo.getUpdatets())) {
 			throw new BusinessException("合同编号：" + vo.getVcontcode() + ",数据已发生变化;<br>");
 		}
 		return vo;
 	}
 	
 	/**
 	 * 判断是否能修改
 	 * @param istatus
 	 * @param msg
 	 * @throws DZFWarpException
 	 */
 	private void checkIsOperOrder(Integer istatus, String msg) throws DZFWarpException{
		if (istatus == null || istatus == 2 || istatus == 3) {
			throw new BusinessException(msg);
		} 
	}
 	
 	/**
 	 * 判断是否能发货
 	 * @param id
 	 * @param flag 
 	 * @param applynum
 	 */
 	private String checkIsApply(String id,Integer outnum)  throws DZFWarpException{
 		String message = " ";
 		StringBuffer sql = new StringBuffer();
 		SQLParameter spm = new SQLParameter();
 		spm.addParam(id);
 		sql.append("select nvl(intnum - outnum,0) stocknum, \n");
 		sql.append("   nvl(intnum,0),nvl(outnum,0), \n");
 		sql.append("   vname \n");
 		sql.append("   from cn_materiel \n");
 		sql.append("   where nvl(dr,0) = 0 \n");
 		sql.append("   and pk_materiel = ? \n");
 		MaterielFileVO mmvo = (MaterielFileVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielFileVO.class));
 	    if(mmvo!=null && mmvo.getStocknum()!=null){
 	    	if(outnum>0 && mmvo.getStocknum()==0){//库存为0
 	    		message = mmvo.getVname()+"已无货"+"<br/>";
 	    		return message;
 	    	}
 	    	if(mmvo.getStocknum()<outnum){//库存不足
 	    		message = mmvo.getVname()+"可发货数量为"+mmvo.getStocknum()+"<br/>";
 	    		return message;
 	    	}
 	    	
 	    }
 	    return "";
 	    
 	}

	/**
	 * 设置默认值
	 * 
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(MatOrderVO data, UserVO uservo) throws DZFWarpException {
		data.setCoperatorid(uservo.getCuserid());
		data.setDoperatedate(new DZFDate());
		data.setPk_corp("000001");
		data.setVstatus(IStatusConstant.VSTATUS_1);// 合同状态：默认为待审核
	}

	@SuppressWarnings("unchecked")
	@Override
	public MatOrderVO queryDataById(MatOrderVO mvo,String id,UserVO uservo,String type,String stype)  throws DZFWarpException {
		String message = "";
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(id);
		sql.append(  "select  m.pk_materielbill, \n ");
		sql.append(  "   m.vcontcode, \n");
		sql.append(  "   m.vstatus,m.vreason, \n");
		sql.append(  "   m.fathercorp,m.corpname, \n");
		sql.append(  "   m.vprovince,m.vcity,m.varea,m.citycounty, \n ");
		sql.append(  "   m.vaddress,m.vreceiver,m.phone,m.vmemo, \n ");
		sql.append(  "   m.coperatorid,m.applydate, \n");
		sql.append(  "   m.pk_logistics, \n");
		sql.append(  "   m.fastcode,m.fastcost, \n");
		sql.append(  "   m.deliverid,m.deliverdate, \n");
		sql.append(  "   m.auditerid,m.auditdate, \n");
		sql.append(  "   lg.vname logname \n");
		
		sql.append("     from cn_materielbill m \n");
		sql.append("     left join cn_logistics lg on \n");
		sql.append("     lg.pk_logistics = m.pk_logistics \n");
		sql.append("     where nvl(m.dr,0) = 0 and nvl(lg.dr,0) = 0 \n");
		sql.append("     and m.pk_materielbill = ? \n");
		
		MatOrderVO vo= (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MatOrderVO.class));
		if(vo!=null){
				if("1".equals(type)){//发货
					if(vo.getDeliverid()!=null){
						//uservo = UserCache.getInstance().get(vo.getDeliverid(), null);
						uservo = userser.queryUserJmVOByID(vo.getDeliverid());
						if(uservo!=null){
							vo.setDename(uservo.getUser_name());
						}
					}else{
						//uservo = UserCache.getInstance().get(uservo.getCuserid(), null);
						uservo = userser.queryUserJmVOByID(uservo.getCuserid());
						if(uservo!=null){
							vo.setDename(uservo.getUser_name());//发货人
						}
					}
				}
					if (vo.getCoperatorid() != null) {
						//uservo = UserCache.getInstance().get(vo.getCoperatorid(), null);
						uservo = userser.queryUserJmVOByID(vo.getCoperatorid());
						if(uservo!=null){
							vo.setApplyname(uservo.getUser_name());//申请人
						}
					}
					if(vo.getAuditerid()!=null){
						//uservo = UserCache.getInstance().get(vo.getAuditerid(), null);
						uservo = userser.queryUserJmVOByID(vo.getAuditerid());
						if(uservo!=null){
							vo.setAudname(uservo.getUser_name());//审核人
						}
					}
					
			StringBuffer ssql = new StringBuffer();
			SQLParameter sspm = new SQLParameter();
			sspm.addParam(id);
			ssql.append("  select b.pk_materielbill_b, \n");
			ssql.append("    b.vname,b.vunit,b.applynum,b.outnum, \n");
			ssql.append("    l.pk_materiel \n");
			ssql.append("    from cn_materielbill_b b \n");
			ssql.append("    left join cn_materiel l on \n");
			ssql.append("    b.pk_materiel = l.pk_materiel \n");
			ssql.append("    where nvl(b.dr,0) = 0 \n");
			ssql.append("    and b.pk_materielbill = ? \n");
			List<MatOrderBVO> bvolist = (List<MatOrderBVO>) singleObjectBO.executeQuery(ssql.toString(), sspm, new BeanListProcessor(MatOrderBVO.class));
			MatOrderBVO[] b = new MatOrderBVO[bvolist.size()];
			MatOrderBVO[] bvos = (MatOrderBVO[]) bvolist.toArray(b);
			if(bvolist!=null && bvolist.size()>0){
				vo.setChildren(bvos);
			}
			
			setCitycountry(vo);
			
			if(!StringUtil.isEmpty(mvo.getDedubegdate()) &&
					!StringUtil.isEmpty(mvo.getDuduenddate())){
				 //获取上个季度时间
				 try {
					String lastQuarter = getLastQuarter(mvo.getDedubegdate(), mvo.getDuduenddate());
					String[] quarter = lastQuarter.split(",");
					vo.setDedubegdate(quarter[0]);
					vo.setDuduenddate(quarter[1]);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
			if(stype!=null && "1".equals(stype)){
				//点击审核校验
				message = checkIsInfo(vo, bvos, message);
				if(!message.isEmpty()){
					vo.setMessage(message);
				}
			}
			return vo;
		}
		return null;
	}
	

	private void setCitycountry(MatOrderVO vo)  throws DZFWarpException{
		if (vo.getCitycounty() != null) {
			String[] citycountry = vo.getCitycounty().split("-");
			if (citycountry.length == 3) {
				vo.setPname(citycountry[0]);
				vo.setCityname(citycountry[1]);
				vo.setCountryname(citycountry[2]);
			} else if (citycountry.length == 2) {
				String str = "";
				if (citycountry[1] != null) {
					str = citycountry[1].substring(citycountry[1].length() - 1);
				}
				if ("区".equals(str)) {
					vo.setCityname("市辖区");
				} else if ("县".equals(str)) {
					vo.setCityname("县");
				} else if ("市".equals(str)) {
					vo.setCityname("市");
				}
				vo.setPname(citycountry[0]);
				vo.setCountryname(citycountry[1]);
			}

		}
	}

	private MatOrderVO queryById(String pk_materielbill)  throws DZFWarpException {
		
		StringBuffer str=new StringBuffer();
		SQLParameter spm=new SQLParameter();
		spm.addParam(pk_materielbill);
		str.append("  select pk_materielbill, \n");
		str.append("     vcontcode,vstatus,updatets \n");
		str.append("     from cn_materielbill  \n");
		str.append("     where nvl(dr,0) = 0 and \n");
		str.append("     pk_materielbill = ? \n");
		return (MatOrderVO) singleObjectBO.executeQuery(str.toString(), spm, new BeanProcessor(MatOrderVO.class));
		
	}

	@Override
	public void delete(MatOrderVO qryvo)   throws DZFWarpException{
		
		MatOrderVO mvo=queryById(qryvo.getPk_materielbill());
		if(mvo.getUpdatets()!=null){
			qryvo.setUpdatets(mvo.getUpdatets());
			qryvo.setStatus(mvo.getStatus());
		}
		
		checkIsOperOrder(qryvo.getStatus(), "只有待审批或已驳回状态的申请单支持删除！");
		
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(qryvo.getTableName(), qryvo.getPk_materielbill(), uuid, 60);
			//1.删除主订单
			SQLParameter spm = new SQLParameter();
	    	spm.addParam(qryvo.getPk_materielbill());
			String sql = " DELETE FROM cn_materielbill WHERE pk_materielbill = ? ";
			singleObjectBO.executeUpdate(sql, spm);
			//2.删除子订单
			sql = "DELETE FROM cn_materielbill_b WHERE pk_materielbill = ?";
			singleObjectBO.executeUpdate(sql, spm);
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(qryvo.getTableName(), qryvo.getPk_materielbill(), uuid);
		}
		
	}
	
	/**
	 * 物料审核过滤
	 * 大区负责人只能看见自己负责的加盟商
	 * @param stype 
	 */
	@SuppressWarnings("unchecked")
	private List<ChnAreaBVO> queryPro(UserVO uservo, String stype)   throws DZFWarpException{
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select b.vprovince,b.isCharge,b.pk_corp \n");
		sql.append("    from cn_chnarea a \n");
		sql.append("    left join cn_chnarea_b  b on \n");
		sql.append("    a.pk_chnarea = b.pk_chnarea \n");
		sql.append("    where nvl(a.dr, 0) = 0 \n");
		sql.append("    and nvl(b.dr, 0) = 0 ");
		sql.append("    and a.type = 1 and b.type = 1 \n");
		if(stype!=null && "3".equals(stype)){//物料申请
			if(!StringUtil.isEmpty(uservo.getCuserid())){
				sql.append(" and b.userid = ? ");
				spm.addParam(uservo.getCuserid());
			}
		}
		if(stype!=null && "2".equals(stype)){//物料审核
			if(!StringUtil.isEmpty(uservo.getCuserid())){
				sql.append(" and a.userid = ? ");
				spm.addParam(uservo.getCuserid());
			}
		}
		
		List<ChnAreaBVO> list = (List<ChnAreaBVO>)singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(ChnAreaBVO.class));
	    return list;
	}
	
	/**
	 * 查询直接负责的加盟商
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CorpVO> queryChannel(UserVO uservo) throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(uservo.getCuserid());
		sql.append("    select distinct \n");
		//sql.append("       b.vprovince,b.isCharge, \n");
		sql.append("       c.pk_corp,c.innercode,c.unitname \n");
		sql.append("       from cn_chnarea a \n");
		sql.append("       left join cn_chnarea_b b on \n");
		sql.append("       a.pk_chnarea = b.pk_chnarea \n");
		sql.append("       left join bd_account c on \n");
		sql.append("       c.vprovince = b.vprovince \n");
		sql.append("       where nvl(a.dr,0) = 0 \n");
		sql.append("       and nvl(b.dr,0) = 0  \n");
		sql.append("       and nvl(c.dr,0) = 0  \n");
		sql.append("       and a.type =1 and b.type = 1 \n");
		sql.append("       and b.userid = ? \n");
		
		List<CorpVO> corpList = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(CorpVO.class));
		if(corpList!=null && corpList.size()>0){
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, corpList, 1);
			return corpList;
		}
		return null;
	}
	
	@Override
	public MatOrderVO queryUserData(UserVO uservo) {
		
		// uservo = UserCache.getInstance().get(uservo.getCuserid(), null);
		   uservo = userser.queryUserJmVOByID(uservo.getCuserid());
		 MatOrderVO mvo = new MatOrderVO();
		 if(uservo!=null){
			 mvo.setApplyname(uservo.getUser_name());
			 QueryDeCodeUtils.decKeyUtil(new String[] { "applyname" }, mvo, 1);
		 }
		 return mvo;
	}

	
	/**
	 * 获取上个季度时间
	 * 将中国标准时间转为2019-04-12日期类型
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws ParseException
	 */
	private String getLastQuarter(String startdate,String enddate) throws ParseException, DZFWarpException{
		startdate = startdate.replace("GMT", "").replaceAll("\\(.*\\)", "");
		enddate = enddate.replace("GMT", "").replaceAll("\\(.*\\)", "");
		//将字符串转化为date类型
		SimpleDateFormat format =  new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z",Locale.ENGLISH);
		Date sdate = format.parse(startdate);
		Date edate = format.parse(enddate);
		String start = new SimpleDateFormat("yyyy-MM-dd").format(sdate);
		String end = new SimpleDateFormat("yyyy-MM-dd").format(edate);
		return start+","+end;
	}
	
	/**
	 * 判断是否是数字
	 * @param str
	 * @return
	 */
	private static boolean isInteger(String str) throws DZFWarpException {  
	    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
	    return pattern.matcher(str).matches();  
	}

}
