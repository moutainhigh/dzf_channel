package com.dzf.service.channel.matmanage.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.service.channel.matmanage.IMatCommonService;

@Service("matservice")
public class MatCommonServiceImpl implements IMatCommonService{
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	/**
	 * 物料审核过滤 大区负责人只能看见自己负责的加盟商
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ChnAreaBVO> queryPro(UserVO uservo, String stype,String vpro, String vcorp) throws DZFWarpException {

		List<String> corp = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select b.vprovince,b.isCharge,b.pk_corp \n");
		sql.append("    from cn_chnarea_b b \n");
		sql.append("    right join cn_chnarea a on \n");
		sql.append("    a.pk_chnarea = b.pk_chnarea \n");
		sql.append("    where nvl(a.dr, 0) = 0 \n");
		sql.append("    and nvl(b.dr, 0) = 0 ");
		sql.append("    and a.type = 1 and b.type = 1 \n");
		if (stype != null && "3".equals(stype)) {// 物料申请
			if (!StringUtil.isEmpty(uservo.getCuserid())) {
				sql.append(" and b.userid = ? ");
				spm.addParam(uservo.getCuserid());
			}
		}
		if (stype != null && "2".equals(stype)) {// 物料审核
			if (!StringUtil.isEmpty(uservo.getCuserid())) {
				sql.append(" and a.userid = ? ");
				spm.addParam(uservo.getCuserid());
			}
		}

		List<ChnAreaBVO> list = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaBVO.class));
		
		//添加数据权限
		if (list != null && list.size() > 0) {
			for (ChnAreaBVO vo : list) {
				if ("Y".equals(vo.getIsCharge().toString())) {// 是否是省市负责人
					vpro = vpro + "," + vo.getVprovince();
				} else {
					if (vo.getPk_corp() != null) {
						corp.add(vo.getPk_corp());
					}
				}
			}
		}
		if (vpro != null && !StringUtil.isEmpty(vpro)) {
			vpro = vpro.substring(1);
			vpro = "(" + vpro + ")";
		}
		if (corp != null && corp.size() > 0) {
			for (String c : corp) {
				vcorp = vcorp + "," + "'" + c + "'";
			}
			vcorp = vcorp.substring(1);
			vcorp = "(" + vcorp + ")";
		}
		
		list.get(0).setCorpname(vcorp);
		list.get(0).setVprovname(vpro);

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielFileVO> queryMatFile(MaterielFileVO pamvo,UserVO uservo)  throws DZFWarpException {
		StringBuffer sql=new StringBuffer();
		SQLParameter spm=new SQLParameter();
		sql.append("  select pk_materiel,vname,vunit,vcode, \n");
		sql.append("     (nvl(intnum,0) - nvl(outnum,0)) enapplynum \n");
		sql.append("     from cn_materiel  \n");
		sql.append("     where nvl(dr,0) = 0  \n");
		sql.append("     and isseal = 1 \n");
		
		if (!StringUtil.isEmpty(pamvo.getVcode())) {
			sql.append(" AND (vname like ? ");
			sql.append(" OR vcode like ? ) ");
			spm.addParam("%" + pamvo.getVcode() + "%");
			spm.addParam("%" + pamvo.getVcode() + "%");
		}
		
		List<MaterielFileVO> bvoList = (List<MaterielFileVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(MaterielFileVO.class) );
		return bvoList;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielFileVO> queryMat() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append("select vname, vunit  \n");
		sql.append("  from cn_materiel  \n");
		sql.append(" where nvl(dr, 0) = 0  \n");
		sql.append("   and isseal = 1  \n");

		List<MaterielFileVO> bvoList = (List<MaterielFileVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(MaterielFileVO.class));
		return bvoList;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderBVO> queryNumber(MatOrderVO pamvo) throws DZFWarpException {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select DISTINCT b.vname,  \n");
		sql.append("       b.vunit  \n");
		sql.append("  from cn_materielbill_b b  \n");
		sql.append("  left join cn_materielbill c on c.pk_materielbill = b.pk_materielbill  \n");
		sql.append("  left join cn_materiel m on m.pk_materiel = b.pk_materiel  \n");
		sql.append("  left join bd_account co on co.pk_corp = c.pk_corp  \n");
		sql.append(" where nvl(b.dr, 0) = 0  \n");
		sql.append("   and nvl(c.dr, 0) = 0  \n");
		sql.append("   and nvl(m.dr, 0) = 0  \n");
		sql.append("   and m.isseal = 1  \n");

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
	public List<MatOrderVO> queryAllProvince() throws DZFWarpException {
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
	public List<MatOrderVO> queryCityByProId(Integer pid) throws DZFWarpException {
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
	public List<MatOrderVO> queryAreaByCid(Integer cid) throws DZFWarpException {
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
	public MatOrderVO queryById(String pk_materielbill) throws DZFWarpException {

		StringBuffer str = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_materielbill);
		str.append("  select pk_materielbill, \n");
		str.append("     vcontcode,vstatus,updatets \n");
		str.append("     from cn_materielbill  \n");
		str.append("     where nvl(dr,0) = 0 and \n");
		str.append("     pk_materielbill = ? \n");
		return (MatOrderVO) singleObjectBO.executeQuery(str.toString(), spm, new BeanProcessor(MatOrderVO.class));

	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public String checkIsInfo(MatOrderVO vo, MatOrderBVO[] bvos, String message) throws DZFWarpException {
		if (bvos != null && bvos.length > 0) {
			for (MatOrderBVO mbvo : bvos) {
				MaterielFileVO mvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class,
						mbvo.getPk_materiel());
				if (mvo != null && mvo.getIsappl() != null) {
					if (mvo.getIsappl() == 1) {// 勾选了申请条件
						Integer passNum = null;
						// 获取上季度提单审核通过数
						passNum = queryContNum(vo, vo.getFathercorp());

						StringBuffer csql = new StringBuffer();
						SQLParameter cspm = new SQLParameter();
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
						List<MatOrderVO> mvoList = (List<MatOrderVO>) singleObjectBO.executeQuery(csql.toString(), cspm,
								new BeanListProcessor(MatOrderVO.class));

						Integer sumout = 0;// 上季度实发数量
						// Integer sumsucc = 0;//上季度申请通过数量

						if (mvoList != null && mvoList.size() > 0) {
							for (MatOrderVO ovo : mvoList) {
								if (ovo.getOutnum() == null) {
									ovo.setOutnum(0);
								}
								if (ovo.getSuccnum() == null) {
									ovo.setSuccnum(0);
								}
								sumout = sumout + ovo.getOutnum();
								// sumsucc = sumsucc + ovo.getSuccnum();
							}
						}
						Integer ssumout = (int) (0.7 * sumout);
						if (sumout == 0) {// 上季度没有发货
							// 可以申请保存
						} else {
							if (passNum != null && ssumout != null) {
								if (passNum >= ssumout) {
									// 可以申请保存
								} else {
									// 提示再申请保存
									mbvo.setSumapply(sumout);
									// mbvo.setSumsucc(sumsucc);
									message = message + "该加盟商" + mbvo.getVname() + "上季度申请数" + mbvo.getSumapply() + "，"
											+ "提单审核通过数" + passNum + "，" + "不符合该物料的申请条件，望知悉" + "<br/>";

								}
							}

						}
					} else {
						// 不需要校验
					}
				}
			}
		}

		return message;
	}
	
	
	/**
	 * 查询合同提单量
	 * 
	 * @param paramvo
	 * @param corpid
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryContNum(MatOrderVO vo, String corpid) throws DZFWarpException {

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
		// sql.append(" AND nvl(ct.isncust, 'N') = 'N' \n");
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
		if (countvo != null && countvo.getNum() != null) {
			num = countvo.getNum();
		}
		return num;
	}
	
	/**
	 * 判断是否能发货
	 * 
	 * @param id
	 * @param flag
	 * @param applynum
	 */
	public String checkIsApply(String id, Integer outnum) throws DZFWarpException {
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
		MaterielFileVO mmvo = (MaterielFileVO) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanProcessor(MaterielFileVO.class));
		if (mmvo != null && mmvo.getStocknum() != null) {
			if (outnum > 0 && mmvo.getStocknum() == 0) {// 库存为0
				message = mmvo.getVname() + "已无货" + "<br/>";
				return message;
			}
			if (mmvo.getStocknum() < outnum) {// 库存不足
				message = mmvo.getVname() + "可发货数量为" + mmvo.getStocknum() + "<br/>";
				return message;
			}

		}
		return "";

	}

	
	/**
	 * 获取上个季度时间 将中国标准时间转为2019-04-12日期类型
	 * 
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws ParseException
	 */
	public String getLastQuarter(String startdate, String enddate) throws ParseException, DZFWarpException {
		startdate = startdate.replace("GMT", "").replaceAll("\\(.*\\)", "");
		enddate = enddate.replace("GMT", "").replaceAll("\\(.*\\)", "");
		// 将字符串转化为date类型
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z", Locale.ENGLISH);
		Date sdate = format.parse(startdate);
		Date edate = format.parse(enddate);
		String start = new SimpleDateFormat("yyyy-MM-dd").format(sdate);
		String end = new SimpleDateFormat("yyyy-MM-dd").format(edate);
		return start + "," + end;
	}
	
	/**
	 * 设置默认值
	 * 
	 * @param data
	 * @throws DZFWarpException
	 */
	public void setDefaultValue(MatOrderVO data, UserVO uservo) throws DZFWarpException {
		data.setCoperatorid(uservo.getCuserid());
		data.setDoperatedate(new DZFDate());
		data.setPk_corp("000001");
		data.setVstatus(IStatusConstant.VSTATUS_1);// 合同状态：默认为待审核
	}
	
	/**
	 * 判断是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isInteger(String str) throws DZFWarpException {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 判断是否能修改
	 * 
	 * @param istatus
	 * @param msg
	 * @throws DZFWarpException
	 */
	public void checkIsOperOrder(Integer istatus, String msg) throws DZFWarpException {
		if (istatus == null || istatus == 2 || istatus == 3) {
			throw new BusinessException(msg);
		}
	}
	
	/**
	 * 检查是否是最新数据
	 * 
	 * @param pk_materiel
	 * @param updatets
	 * @return
	 */
	public void checkData(String pk_materielbill, DZFDateTime updatets) throws DZFWarpException {
		MatOrderVO vo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, pk_materielbill);
		if (!updatets.equals(vo.getUpdatets())) {
			throw new BusinessException("合同编号：" + vo.getVcontcode() + ",数据已发生变化;<br>");
		}
	}
	
	/**
	 * 设置省市区
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void setCitycountry(MatOrderVO vo) throws DZFWarpException {
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



}
