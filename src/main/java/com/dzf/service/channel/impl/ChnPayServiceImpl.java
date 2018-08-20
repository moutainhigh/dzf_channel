package com.dzf.service.channel.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.Logger;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.image.ImageCommonPath;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IChnPayService;
import com.dzf.service.pub.IBillCodeService;
import com.dzf.service.pub.IPubService;

@Service("dfz_chnpay")
public class ChnPayServiceImpl implements IChnPayService {

	private static final String ChnPayBillVO = null;

	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private IBillCodeService billCode = null;
	
    @Autowired
    private IPubService pubService;

	@SuppressWarnings("unchecked")
	@Override
	public ChnPayBillVO[] query(ChnPayBillVO chn, UserVO uservo) throws DZFWarpException {
		StringBuffer querysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		querysql.append(" select a.*,ba.vprovince,su.user_name submitname from cn_paybill a ");
		querysql.append(" left join sm_user su on a.submitid=su.cuserid and a.submitid is not null ");
		querysql.append(" left join bd_account ba on a.pk_corp=ba.pk_corp ");
		querysql.append(" 	where nvl(a.dr,0) = 0 and  a.systype=2 ");
		if (chn.getDoperatedate() != null) {// 付款日期
			querysql.append(" and a.dpaydate >= ? ");
			sp.addParam(chn.getDoperatedate());
		}
		if (chn.getDpaydate() != null) {// 付款日期
			querysql.append(" and a.dpaydate <= ? ");
			sp.addParam(chn.getDpaydate());
		}
		if (!StringUtil.isEmpty(chn.getPk_paybill())) {
			querysql.append(" and a.pk_paybill = ? ");
			sp.addParam(chn.getPk_paybill());
		}
		if (chn.getIpaytype() != null && chn.getIpaytype()>-1) {
			querysql.append(" and a.ipaytype =? ");
			sp.addParam(chn.getIpaytype());
		}else if(chn.getIpaytype() != null && chn.getIpaytype()<-1){
			if (chn.getIpaytype() == -2) {
				querysql.append(" and a.vstatus = 1 ");
			} else if (chn.getIpaytype() == -3) {
				querysql.append(" and a.vstatus = 4");
			}
		}
		if (chn.getIpaymode() != null && chn.getIpaymode() != -1) {
			querysql.append(" and a.ipaymode =? ");
			sp.addParam(chn.getIpaymode());
		}
		if (chn.getVstatus() != null && chn.getVstatus() != -1) {
			querysql.append(" and a.vstatus =? ");
			sp.addParam(chn.getVstatus());
		}
		if(!StringUtil.isEmpty(chn.getPk_corp())){
		    String[] strs = chn.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    querysql.append(" AND a.pk_corp in (").append(inSql).append(")");
		}
    	String condition = pubService.makeCondition(uservo.getCuserid(),chn.getAreaname());
    	if(condition!=null && !condition.equals("flg")){
    		querysql.append(condition);
    	}else if(condition==null){
    		return null;
    	}
		querysql.append(" order by a.dpaydate desc ");
		List<ChnPayBillVO> chns = (List<ChnPayBillVO>) singleObjectBO.executeQuery(querysql.toString(), sp, 
				new BeanListProcessor(ChnPayBillVO.class));
		Map<Integer, String> areaMap = pubService.getAreaMap(chn.getAreaname(),3);
		List<ChnPayBillVO> rets=new ArrayList<ChnPayBillVO>();
		if (chns != null) {
			CorpVO cvo = null;
			UserVO uvo = null;
			for (ChnPayBillVO chnb : chns) {
				cvo=CorpCache.getInstance().get(null, chnb.getPk_corp());
				if(cvo!=null){
					chnb.setCorpname(cvo.getUnitname());
					chnb.setVprovname(cvo.getCitycounty());
					if(!StringUtil.isEmpty(chn.getCorpname())){
						if(cvo.getUnitname().indexOf(chn.getCorpname().replaceAll(" ", ""))>=0 ){
							rets.add(chnb);
						}
					}
				}
				uvo = UserCache.getInstance().get(chnb.getVconfirmid(), null);
				if (uvo != null) {
					chnb.setVconfirmname(uvo.getUser_name());
				}
				if(!StringUtil.isEmpty(chnb.getSubmitname())){
					chnb.setSubmitname(CodeUtils1.deCode(chnb.getSubmitname()));
				}
				if(areaMap.containsKey(chnb.getVprovince())){
					chnb.setAreaname(areaMap.get(chnb.getVprovince()));
				}
			}
		}
		if(!StringUtil.isEmpty(chn.getCorpname())){
			chns=rets;
		}
		return chns.toArray(new ChnPayBillVO[0]);
	}
	
	
	@Override
	public ChnPayBillVO save(ChnPayBillVO vo,CorpVO corpvo,String cuserid,File[] files,String[] filenames) throws DZFWarpException {
		if(!StringUtil.isEmpty(vo.getPk_paybill())){
			checkData(vo.getTstamp(),vo.getPk_paybill());
		}
		if(StringUtil.isEmpty(vo.getVbillcode())){
			MaxCodeVO mcvo=new MaxCodeVO();
			mcvo.setTbName(vo.getTableName());
			mcvo.setFieldName("vbillcode");
			mcvo.setPk_corp(vo.getPk_corp());
			mcvo.setBillType("FK"+new DZFDate().getYear()+new DZFDate().getStrMonth());
			mcvo.setDiflen(3);
			String code = billCode.getDefaultCode(mcvo);
			if (StringUtil.isEmpty(code)) {
				throw new BusinessException("获取付款编号失败,没有相应的编码规则");
			}
			vo.setVbillcode(code);
			if(!checkCodeIsUnique(vo) ){
				throw new BusinessException("请手动输入付款编码!");
			}
		}else{
			vo.setVbillcode(vo.getVbillcode().replaceAll(" ", ""));
			if(!checkCodeIsUnique(vo) ){
				throw new BusinessException("付款编码重复,请重新输入");
			}
		}
		vo.setTstamp(new DZFDateTime());
		if(files!=null&&filenames!=null){
			vo=saveAttachment(vo,corpvo,cuserid,files[0],filenames[0]);
		}
		Integer vprovince=vo.getVprovince();
		if(!StringUtil.isEmpty(vo.getPk_paybill())){//修改
			String[] str={"vbillcode","dpaydate","vhandleid","vbankname","vbankcode",
					"vhandlename","vmemo","npaymny","ipaymode","ipaytype","tstamp"};
			if(vo.getVstatus()==4){
				vo.setVstatus(1);
				vo.setSubmitid(null);
				vo.setSubmitime(new DZFDateTime());
				String[] str1={"vstatus","submitid","submitime"};
				str=(String[]) ArrayUtils.addAll(str, str1);
			}
			if(files!=null&&filenames!=null){
				String[] str1={"docName","docOwner","docTime","vfilepath"};
				str=(String[]) ArrayUtils.addAll(str, str1);
			}
			singleObjectBO.update(vo,str);
		}else{
			vo.setVstatus(1);//待提交
			vo.setTs(new DZFDateTime());//时间戳
			vo= (ChnPayBillVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);//新增
		}
		corpvo= CorpCache.getInstance().get(null, vo.getPk_corp());
		if(corpvo!=null){
			Map<Integer, String> areaMap = pubService.getAreaMap(null, 3);
			vo.setVprovname(corpvo.getCitycounty());
			vo.setAreaname(areaMap.get(vprovince));
		}
		return vo;
	}
	
	/**
	 * 保存附件
	 * @param retvo
	 * @param files
	 * @param filenames
	 */
	private ChnPayBillVO saveAttachment(ChnPayBillVO vo,CorpVO corpvo,String cuserid,File file,String filename){
		String uploadPath = ImageCommonPath.getPaybillFilePath(corpvo.getInnercode(),vo.getPk_paybill(), null);
		String uuid=UUID.randomUUID().toString().replaceAll("-", "");
		String fname = uuid+ filename.substring(filename.indexOf("."));
		String filepath = uploadPath + File.separator + fname;
		vo.setDocName(filename);
		vo.setVfilepath(filepath);
		vo.setDocTime(new DZFDateTime());
		vo.setDocOwner(cuserid);
		InputStream  is = null;
		OutputStream os = null;
		try {
			File ff = new File(filepath);
			if (!ff.getParentFile().exists()) {
				ff.getParentFile().mkdirs();
			}
			is = new FileInputStream(file);
			os = new FileOutputStream(filepath);
			IOUtils.copy(is, os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				    Logger.error(this, e.getMessage(), e);
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				    Logger.error(this, e.getMessage(), e);
				}
			}
		}
		return vo;
	}

	@Override
	public HashMap<String, Object> updateStatusMult(ChnPayBillVO[] vos, int stat) throws DZFWarpException {
		HashMap<String, Object> mapz = new HashMap<>();
		String errmsg = "";
		ArrayList<ChnPayBillVO> list = new ArrayList<>();
		HashMap<String, Object> hmap = new HashMap<>();
		hmap.put("str0", new StringBuffer());
		hmap.put("str1", new StringBuffer());
		hmap.put("str2", new StringBuffer());
		hmap.put("str3", new StringBuffer());
		hmap.put("str4", new StringBuffer());
		hmap.put("errmsg", errmsg);
		int len = vos.length;
		int i = 0;
		StringBuffer sb = new StringBuffer();
		for (ChnPayBillVO vo : vos) {
			if ((boolean) judStatus(vo, stat, i, len, hmap).get("flg")) {
				list.add(vo);
				sb.append("'");
				sb.append(vo.getPk_paybill());
				sb.append("',");
			}
			if(stat==2){
				vo.setSubmitid(vos[0].getSubmitid());
				vo.setSubmitime(new DZFDateTime());
			}else if(stat==1){
				vo.setSubmitid(null);
				vo.setSubmitime(null);
			}
			i++;
		}
		errmsg = (String) hmap.get("errmsg");
		if (list != null && list.size() > 0) {
			vos = list.toArray(new ChnPayBillVO[list.size()]);
		} else {
			return reFail(mapz, len, errmsg);
		}
		String s = sb.substring(0, sb.length()-1);
		HashMap<String, Object> cmap = checkDatas(vos, s, errmsg, stat);
		list=(ArrayList<ChnPayBillVO>) cmap.get("list");
		errmsg=(String) cmap.get("errmsg");
		if(list!=null&&list.size()>0){
			vos=list.toArray(new ChnPayBillVO[list.size()]);
		}else{
			return reFail(mapz,len,errmsg);
		}
		if(stat!=-1){
			singleObjectBO.updateAry(vos, new String[] { "vstatus", "tstamp","submitid","submitime"});
//			singleObjectBO.updateAry(vos, new String[] { "vstatus", "tstamp"});
		}else {
			String cids=(String) cmap.get("cids");
			cids=cids.substring(0, cids.length()-1);
			delete(cids);
		}
		return reSucess(mapz, len, vos, errmsg);
	}

	private HashMap<String, Object> judStatus(ChnPayBillVO chn, int stat, int i, int len,HashMap<String, Object> hmap) {
		StringBuffer str0 = (StringBuffer) hmap.get("str0");
		StringBuffer str1 = (StringBuffer) hmap.get("str1");
		StringBuffer str2 = (StringBuffer) hmap.get("str2");
		StringBuffer str3 = (StringBuffer) hmap.get("str3");
		StringBuffer str4 = (StringBuffer) hmap.get("str4");
		String errmsg = (String) hmap.get("errmsg");
		boolean flg = false;
		if (stat == 2) {// 批量提交
			if (chn.getVstatus() == 1 && chn.getSystype()==2) {
				flg = true;
			}
			if (chn.getVstatus() == 2) {
				str0.append(chn.getVbillcode() + '、');
			} else if (chn.getVstatus() == 3) {
				str1.append(chn.getVbillcode() + '、');
			}else if (chn.getVstatus() == 4) {
				str2.append(chn.getVbillcode() + '、');
			}else if (chn.getVstatus() == 5) {
				str3.append(chn.getVbillcode() + '、');
			}else if(chn.getSystype()==1){
				str4.append(chn.getVbillcode() + '、');
			}
			if (i == len - 1) {
				if (str0.length() != 0) {
					String str;
					str = str0.substring(0, str0.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是待审批单据，不能提交；<br>";
				}
				if (str1.length() != 0) {
					String str = str1.substring(0, str1.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是已确认单据，不能提交；<br>";
				}
				if (str2.length() != 0) {
					String str = str2.substring(0, str2.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是已驳回单据，不能提交；<br>";
				}
				if (str3.length() != 0) {
					String str = str3.substring(0, str3.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是待确认单据，不能提交；<br>";
				}
				if (str4.length() != 0) {
					String str = str4.substring(0, str4.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，加盟商录入单据，不能操作；<br>";
				}
			}
		} else if (stat ==1) {// 批量取消提交
			if (chn.getVstatus() == 2 && chn.getSystype()==2) {
				flg = true;
			}
			if (chn.getVstatus() == 1) {
				str0.append(chn.getVbillcode() + '、');
			} else if (chn.getVstatus() == 3) {
				str1.append(chn.getVbillcode() + '、');
			}else if (chn.getVstatus() == 4) {
				str2.append(chn.getVbillcode() + '、');
			}else if (chn.getVstatus() == 5 ) {
				str3.append(chn.getVbillcode() + '、');
			}else if(chn.getSystype()==1){
				str4.append(chn.getVbillcode() + '、');
			}
			if (i == len - 1) {
				if (str0.length() != 0) {
					String str;
					str = str0.substring(0, str0.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是待提交单据，不能取消提交；<br>";
				}
				if (str1.length() != 0) {
					String str = str1.substring(0, str1.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是已确认单据，不能取消提交；<br>";
				}
				
				if (str2.length() != 0) {
					String str = str2.substring(0, str2.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是已驳回单据，不能取消提交；<br>";
				}
				if (str3.length() != 0) {
					String str = str3.substring(0, str3.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是待确认单据，不能取消提交；<br>";
				}
				if (str4.length() != 0) {
					String str = str4.substring(0, str4.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，加盟商录入单据，不能操作；<br>";
				}
			}
		} else if(stat==-1){//删除
			if ((chn.getVstatus() ==1 || chn.getVstatus() ==4) && chn.getSystype()==2) {
				flg = true;
			}
			if (chn.getVstatus() == 2) {
				str0.append(chn.getVbillcode() + '、');
			} else if (chn.getVstatus() == 3) {
				str1.append(chn.getVbillcode() + '、');
			}/*else if (chn.getVstatus() == 4) {
				str2.append(chn.getVbillcode() + '、');
			}*/else if (chn.getVstatus() == 5) {
				str3.append(chn.getVbillcode() + '、');
			}else if(chn.getSystype()==1){
				str4.append(chn.getVbillcode() + '、');
			}
			if (i == len - 1) {
				if (str0.length() != 0) {
					String str;
					str = str0.substring(0, str0.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是待审批单据，不能删除；<br>";
				}
				if (str1.length() != 0) {
					String str = str1.substring(0, str1.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是已确认单据，不能删除；<br>";
				}
//				if (str2.length() != 0) {
//					String str = str2.substring(0, str2.length() - 1);
//					errmsg = errmsg + "付款编号：" + str + "，是已驳回付款，不能删除；<br>";
//				}
				if (str3.length() != 0) {
					String str = str3.substring(0, str3.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，是待确认单据，不能删除；<br>";
				}
				if (str4.length() != 0) {
					String str = str4.substring(0, str4.length() - 1);
					errmsg = errmsg + "付款编号：" + str + "，加盟商录入单据，不能操作；<br>";
				}
			}
		}
		hmap.put("flg", flg);
		hmap.put("str0", str0);
		hmap.put("str1", str1);
		hmap.put("str2", str2);
		hmap.put("str3", str3);
		hmap.put("str4", str4);
		hmap.put("errmsg", errmsg);
		return hmap;
	}

	private HashMap<String, Object> reSucess(HashMap<String, Object> mapz, int len, ChnPayBillVO[] vos, String errmsg) {
		if (!StringUtil.isEmpty(errmsg)) {
			errmsg = "其中<br>" + errmsg;
		}
		errmsg = "成功" + vos.length + "条," + "失败" + (len - vos.length) + "条," + errmsg;
		errmsg = errmsg.substring(0, errmsg.length() - 1);
		mapz.put("errmsg", errmsg);
		mapz.put("list", vos);
		String str="1";
		if(vos.length<len){
			str="2";
		}
		mapz.put("stat",str);
		mapz.put("len_suc", vos.length);
		return mapz;
	}

	private HashMap<String, Object> reFail(HashMap<String, Object> mapz, int len, String errmsg) {
		errmsg = "成功0条," + "失败" + len + "条,其中<br>" + errmsg;
		errmsg = errmsg.substring(0, errmsg.length() - 1);
		mapz.put("errmsg", errmsg);
		mapz.put("list", null);
		mapz.put("stat","2");
		mapz.put("len_suc", 0);
		return mapz;
	}

	private boolean checkCodeIsUnique(ChnPayBillVO vo) {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) as count from cn_paybill");
		sql.append(" where pk_corp=? and nvl(dr,0) = 0 and vbillcode = ? ");
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getVbillcode());
		if(!StringUtil.isEmpty(vo.getPk_paybill())){
			sql.append(" and pk_paybill != ?");
			sp.addParam(vo.getPk_paybill());
		}
		String res = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		int num = Integer.valueOf(res);
		if(num <= 0)
			ret = true;
		return ret;
	}


	@Override
	public ChnPayBillVO queryByID(String billid) throws DZFWarpException {
		ChnPayBillVO retvo=(ChnPayBillVO)singleObjectBO.queryVOByID(billid, ChnPayBillVO.class);
		CorpVO cvo=CorpCache.getInstance().get(null, retvo.getPk_corp());
		if(cvo!=null){
			retvo.setCorpname(cvo.getUnitname());
			retvo.setVprovince(cvo.getVprovince());
		}
		return  retvo;
	}

	@Override
	public void delete(String cids) throws DZFWarpException {
		if (!StringUtil.isEmpty(cids)) {
			SQLParameter sp = new SQLParameter();
			StringBuffer sql = new StringBuffer();
			sql.append("update cn_paybill set dr = 1 where pk_paybill in ("+ cids + ")");
			singleObjectBO.executeUpdate(sql.toString(), sp);
			String sql1="select * from  cn_paybill where pk_paybill in ("+ cids + ")";
			List<ChnPayBillVO> list =  (List<ChnPayBillVO>)singleObjectBO.executeQuery(sql1, null,new BeanListProcessor(ChnPayBillVO.class));
	        if(list != null && list.size() > 0){
	        	File f = null;
	        	File parent=null;
	        	for(ChnPayBillVO vo : list){
	        		if(!StringUtil.isEmpty(vo.getVfilepath())){
	        			f = new File(vo.getVfilepath());
	        			parent=f.getParentFile();
						f.delete();
	        		}
	        	}
	        	if(parent!=null){
	        		parent.delete();//删除文件夹  
	        	}
	        }
		}else{
			throw new BusinessException("删除失败");
		}
	}
	
	/**
	 * 付款时间戳校验
	 */
	private void checkData(DZFDateTime tstamp, String pk_paybill) {
		ChnPayBillVO vo =(ChnPayBillVO) singleObjectBO.queryByPrimaryKey(ChnPayBillVO.class, pk_paybill);
		if(tstamp.compareTo(vo.getTstamp()) != 0){
			throw new BusinessException("数据已发生变化，请取消本次操作后刷新");
		}
	}
	
	/**
	 * 多个付款时间戳校验
	 */
	private HashMap<String,Object> checkDatas(ChnPayBillVO[] vos,String pk_paybills,String errmsg,int stat){
		HashMap<String,Object> cmap=new HashMap<>();
		String condition = "  nvl(dr,0)=0 and pk_paybill in("+ pk_paybills + ")";//pk_corp=? and
		SQLParameter params = new SQLParameter();
		ArrayList<ChnPayBillVO> list = (ArrayList<ChnPayBillVO>)singleObjectBO.retrieveByClause(ChnPayBillVO.class, condition, params);
		HashMap<String , ChnPayBillVO> map=new HashMap<>();
		for (ChnPayBillVO vo : list) {
			map.put(vo.getPk_paybill(), vo);
		}
		ArrayList<ChnPayBillVO> clist=new ArrayList<ChnPayBillVO>();
		String str="";
		String cids="";//只是为了删除功能
		for (ChnPayBillVO vo : vos) {
			if(vo.getTstamp().compareTo(map.get(vo.getPk_paybill()).getTstamp())!=0){
				str+=vo.getVbillcode()+"、";
			}else{
				vo.setVstatus(stat);
				cids+="'"+vo.getPk_paybill()+"'"+",";
				vo.setTstamp(new DZFDateTime());
				clist.add(vo);
			}
		}
		if(!StringUtil.isEmpty(str)){
			errmsg="付款编号："+str.substring(0,str.length()-1)+"数据已发生变化;";
		}
		cmap.put("list", clist);
		cmap.put("errmsg", errmsg);
		cmap.put("cids", cids);
		return cmap;
	}
	
	@Override
	public void deleteImageFile(ChnPayBillVO vo) throws DZFWarpException {
		vo=queryByID(vo.getPk_paybill());
		File file = new File(vo.getVfilepath());
		file.delete();
		vo.setDocName(null);
		vo.setDocOwner(null);
		vo.setDocTime(null);
		vo.setVfilepath(null);
		singleObjectBO.update(vo,new String[]{"docName","docOwner","docTime","vfilepath"});
	}

}
