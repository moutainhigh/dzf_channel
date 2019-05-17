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
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.Logger;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.image.ImageCommonPath;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IChnPayService;
import com.dzf.service.pub.IBillCodeService;
import com.dzf.service.pub.IPubService;

@Service("dfz_chnpay")
public class ChnPayServiceImpl implements IChnPayService {

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
		querysql.append(" select a.*,ba.vprovince,su.user_name submitname,us.user_name as vconfirmname from cn_paybill a ");
		querysql.append(" left join sm_user su on a.submitid=su.cuserid and a.submitid is not null ");
		querysql.append(" left join sm_user us on a.vconfirmid = us.cuserid  ");
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
    	String condition = pubService.makeCondition(uservo.getCuserid(),chn.getAreaname(),IStatusConstant.IYUNYING);
    	if(condition!=null && !condition.equals("alldata")){
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
			QueryDeCodeUtils.decKeyUtils(new String[]{"vconfirmname","submitname"}, chns, 1);
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
		String uuid = UUID.randomUUID().toString();
		String id=vo.getPk_paybill();
		try {
			if(!StringUtil.isEmpty(id)){
				LockUtil.getInstance().tryLockKey(vo.getTableName(), id,uuid, 60);
				checkData(vo.getTstamp(),vo.getPk_paybill());
			}
			makeCode(vo);
			vo.setTstamp(new DZFDateTime());
			if(files!=null&&filenames!=null){
				vo=saveAttachment(vo,corpvo,cuserid,files[0],filenames[0]);
			}
			Integer vprovince=vo.getVprovince();
			if(!StringUtil.isEmpty(id)){//修改
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
		}catch (Exception e) {
            if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            else
                throw new WiseRunException(e);
		} finally {
			if(!StringUtil.isEmpty(id)){
				LockUtil.getInstance().unLock_Key(vo.getTableName(), id,uuid);
			}
		}
		return vo;
	}
	
	/**
	 * 校验编码+生成编码
	 * @param vo
	 * @param corpvo
	 */
	private void makeCode(ChnPayBillVO vo) {
		MaxCodeVO mcvo=new MaxCodeVO();
		mcvo.setTbName(vo.getTableName());
		mcvo.setFieldName("vbillcode");
		mcvo.setPk_corp(vo.getPk_corp());
		mcvo.setBillType("FK"+new DZFDate().getYear()+new DZFDate().getStrMonth());
		mcvo.setCorpIdField("pk_corp");
		mcvo.setEntryCode(vo.getVbillcode());
		mcvo.setDiflen(3);
		vo.setVbillcode(billCode.getDefaultCode(mcvo));
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName()+vo.getPk_corp(), vo.getVbillcode(),uuid, 60);
			if(!checkCodeIsUnique(vo) ){
				throw new BusinessException("付款单据号重复,请重新输入或稍后再试!");
			}
		}catch (Exception e) {
            if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            else
                throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName()+vo.getPk_corp(), vo.getVbillcode(),uuid);
		}
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
			Logger.error(this, e.getMessage(), e);
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
	    int len=vos.length;
	    HashMap<Integer, StringBuffer> map = new HashMap<>();
	    ArrayList<ChnPayBillVO> list = new ArrayList<>();
	    StringBuffer setBuff=new StringBuffer();
	    ArrayList<String> ids=new ArrayList<>();
	    Integer putStatus;
	    //1、校验状态是否可以操作
		for (ChnPayBillVO vo : vos) {
			if (judStatus(vo, stat)){
				list.add(vo);
				ids.add(vo.getPk_paybill());
				if(stat==2){
					vo.setSubmitid(vos[0].getSubmitid());
					vo.setSubmitime(new DZFDateTime());
				}else if(stat==1){
					vo.setSubmitid(null);
					vo.setSubmitime(null);
				}
			}else{
				putStatus=vo.getSystype()==1?300:vo.getVstatus();
				setBuff=map.get(putStatus)==null?new StringBuffer():map.get(putStatus);
				setBuff.append(vo.getVbillcode()).append("、");
				map.put(putStatus,setBuff);
			}
		}
		if (list != null && list.size() > 0) {
			vos = list.toArray(new ChnPayBillVO[list.size()]);
		} else {
			return reFail(map,len,stat);
		}
		
		// 2、校验数据是否变化+3、其它用户正在操作
		HashMap<String, ChnPayBillVO> conMap = qryByIds(ids);
		ChnPayBillVO getvo;
		list = new ArrayList<>();
		ids=new ArrayList<>();
		String uuid = UUID.randomUUID().toString();
		String cids="";//只是为了删除功能
		try {
			for (ChnPayBillVO vo : vos) {
				boolean lockKey = LockUtil.getInstance().addLockKey("cn_paybill", vo.getPk_paybill(), uuid, 120);
				if (!lockKey) {
					setBuff = map.get(200) == null ? new StringBuffer() : map.get(200);
					setBuff.append(vo.getVbillcode()).append("、");
					map.put(200, setBuff);
				}else{
					getvo = conMap.get(vo.getPk_paybill());
					if (vo.getTstamp() != null && vo.getTstamp().compareTo(getvo.getTstamp()) != 0) {
						setBuff = map.get(100) == null ? new StringBuffer() : map.get(100);
						setBuff.append(vo.getVbillcode()).append("、");
						map.put(100, setBuff);
					} else {
						cids+="'"+vo.getPk_paybill()+"'"+",";
						vo.setVstatus(stat);
						vo.setTstamp(new DZFDateTime());
						ids.add(vo.getPk_paybill());
						list.add(vo);
					}
				}
			}
			if (list != null && list.size() > 0) {
				vos = list.toArray(new ChnPayBillVO[list.size()]);
			} else {
				return reFail(map, len, stat);
			}
			if(stat!=-1){
				singleObjectBO.updateAry(vos, new String[] { "vstatus", "tstamp","submitid","submitime"});
			}else {
				cids=cids.substring(0, cids.length()-1);
				delete(cids);
			}
			return reSucess(map, len, vos, stat);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			for (String string : conMap.keySet()) {
				LockUtil.getInstance().unLock_Key("cn_paybill", string, uuid);
			}
		}
	}
	
	/**
	 * 根据id,查询付款单
	 * @param ids
	 * @return
	 * @throws BusinessException
	 */
   private HashMap<String , ChnPayBillVO> qryByIds(List<String> ids) throws BusinessException{
      HashMap<String , ChnPayBillVO> map=new HashMap<>();
      String condition = "  nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_paybill",ids.toArray(new String[ids.size()]));
      ArrayList<ChnPayBillVO> list = (ArrayList<ChnPayBillVO>)singleObjectBO.retrieveByClause(ChnPayBillVO.class, condition, null);
      for (ChnPayBillVO vo : list) {
         map.put(vo.getPk_paybill(), vo);
      }
      return map;
   }

	
	private Boolean judStatus(ChnPayBillVO chn, int stat) {
		boolean flg=true;
		if(chn.getSystype()==1){//加盟商录入单据
			return false;
		}
		if (stat == 2) {// 批量提交  
			if(chn.getVstatus() != 1){
				flg=false;
			}
		} 
		if (stat ==1) {// 批量取消提交
			if(chn.getVstatus() != 2){
				flg=false;
			}
		}
		if(stat==-1){//删除
			if(chn.getVstatus() !=1 && chn.getVstatus() !=4){
				flg=false;
			}
		}
		return flg;
	}
	
	private HashMap<String, Object> reSucess(HashMap<Integer, StringBuffer> map,int len, ChnPayBillVO[] vos,Integer stat) {
		String czmsg="";
		if(stat==2){
			czmsg="不能提交；<br>";
		}else if(stat==1){
			czmsg="不能取消提交；<br>";
		}else if(stat==-1){
			czmsg="不能删除；<br>";
		}
		HashMap<String, Object> retmap = new HashMap<>();
		StringBuffer errmsg=new StringBuffer();
		errmsg.append("成功").append(vos.length).append("条,失败").append(len - vos.length).append("条");
		if(len - vos.length!=0){
			errmsg.append(",其中:");
			errmsg.append(getStatusMsg(map, czmsg));
		}
		retmap.put("errmsg", errmsg.toString());
		retmap.put("list", vos);
		retmap.put("len_suc", vos.length);
		String str="1";
		if(vos.length<len){
			str="2";
		}
		retmap.put("stat",str);
		return retmap;
	}

	private HashMap<String, Object> reFail(HashMap<Integer, StringBuffer> map,int len,Integer stat) {
		String czmsg="";
		if(stat==2){
			czmsg="不能提交；<br>";
		}else if(stat==1){
			czmsg="不能取消提交；<br>";
		}else if(stat==-1){
			czmsg="不能删除；<br>";
		}
		HashMap<String, Object> retmap = new HashMap<>();
		StringBuffer errmsg=new StringBuffer();
		errmsg .append("成功0条,失败").append(len).append("条,其中:");
		errmsg.append(getStatusMsg(map, czmsg));
		retmap.put("errmsg", errmsg.toString());
		retmap.put("list", null);
		retmap.put("stat","2");
		retmap.put("len_suc", 0);
		return retmap;
	}

	private String getStatusMsg(HashMap<Integer, StringBuffer> map, String czmsg) {
		StringBuffer errmsg=new StringBuffer();
		for (Integer inte : map.keySet()) {
			errmsg.append("付款编号："); 
			if(inte==1){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",是待提交,").append(czmsg); 
			}else if(inte==2){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",是待审批,").append(czmsg); 
			}else if(inte==3){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",是已确认,").append(czmsg); 
			}else if(inte==4){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",是已驳回,").append(czmsg); 
			}else if(inte==5){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",是待确认,").append(czmsg); 
			}else if(inte==100){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",数据已发生变化<br>"); 
			}else if(inte==200){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",其他用户正在操作<br>"); 
			}else if(inte==300){
				errmsg.append(map.get(inte).substring(0, map.get(inte).length() - 1));
				errmsg.append(",加盟商录入单据<br>"); 
			}
			
		}
		return errmsg.toString();
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

	private void delete(String cids) throws DZFWarpException {
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
	        	boolean ret = true;
	        	for(ChnPayBillVO vo : list){
	        		if(!StringUtil.isEmpty(vo.getVfilepath())){
	        			f = new File(vo.getVfilepath());
	        			parent=f.getParentFile();
						ret = f.delete();
						if(!ret){
						    Logger.info(this, "删除文件失败。");
						}
	        		}
	        	}
	        	if(parent!=null){
	        	    ret = parent.delete();//删除文件夹  
	        	    if(!ret){
                        Logger.info(this, "删除文件失败。");
                    }
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
//	private HashMap<String,Object> checkDatas(ChnPayBillVO[] vos,String pk_paybills,String errmsg,int stat){
//		HashMap<String,Object> cmap=new HashMap<>();
//		String condition = "  nvl(dr,0)=0 and pk_paybill in("+ pk_paybills + ")";//pk_corp=? and
//		SQLParameter params = new SQLParameter();
//		ArrayList<ChnPayBillVO> list = (ArrayList<ChnPayBillVO>)singleObjectBO.retrieveByClause(ChnPayBillVO.class, condition, params);
//		HashMap<String , ChnPayBillVO> map=new HashMap<>();
//		for (ChnPayBillVO vo : list) {
//			map.put(vo.getPk_paybill(), vo);
//		}
//		ArrayList<ChnPayBillVO> clist=new ArrayList<ChnPayBillVO>();
//		String str="";
//		String cids="";//只是为了删除功能
//		for (ChnPayBillVO vo : vos) {
//			if(vo.getTstamp().compareTo(map.get(vo.getPk_paybill()).getTstamp())!=0){
//				str+=vo.getVbillcode()+"、";
//			}else{
//				vo.setVstatus(stat);
//				cids+="'"+vo.getPk_paybill()+"'"+",";
//				vo.setTstamp(new DZFDateTime());
//				clist.add(vo);
//			}
//		}
//		if(!StringUtil.isEmpty(str)){
//			errmsg="付款编号："+str.substring(0,str.length()-1)+"数据已发生变化;";
//		}
//		cmap.put("list", clist);
//		cmap.put("errmsg", errmsg);
//		cmap.put("cids", cids);
//		return cmap;
//	}
	
	@Override
	public void deleteImageFile(ChnPayBillVO vo) throws DZFWarpException {
		vo=queryByID(vo.getPk_paybill());
		File file = new File(vo.getVfilepath());
		boolean ret = file.delete();
		if(!ret){
		    Logger.info(this, "deleteImageFile():删除文件失败。");
		}
		vo.setDocName(null);
		vo.setDocOwner(null);
		vo.setDocTime(null);
		vo.setVfilepath(null);
		singleObjectBO.update(vo,new String[]{"docName","docOwner","docTime","vfilepath"});
	}

}
