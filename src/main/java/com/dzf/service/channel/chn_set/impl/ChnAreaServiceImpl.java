package com.dzf.service.channel.chn_set.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.service.channel.chn_set.IChnAreaService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("chn_area")
public class ChnAreaServiceImpl implements IChnAreaService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO = null;
	
    @Autowired
    private IPubService pubService;
    
    @Autowired
    private IUserService userServiceImpl;

	@SuppressWarnings("unchecked")
	@Override
	public ChnAreaVO[] query(ChnAreaVO qvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		StringBuffer sf=  new StringBuffer();
		sql.append("select h.*,us.user_name as username from cn_chnarea h");
		sql.append(" left join sm_user us on us.cuserid = h.userid");
		sql.append(" where nvl(h.dr,0) = 0");
		sql.append(" and h.pk_corp=? and h.type=?  order by h.areacode " );   
		sp.addParam(qvo.getPk_corp());
		sp.addParam(qvo.getType());
		List<ChnAreaVO> vos =(List<ChnAreaVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ChnAreaVO.class));
		sql = new StringBuffer();
		sp = new SQLParameter();
		sql.append("SELECT pk_chnarea,vprovname,type FROM cn_chnarea_b");
		sql.append(" where nvl(dr,0)= 0 and type=? order by ts desc");
		sp.addParam(qvo.getType());
		List<ChnAreaBVO> bvos = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(),sp,new BeanListProcessor(ChnAreaBVO.class));
		Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
		if(bvos!=null&&bvos.size()>0){
			for (ChnAreaBVO chnAreaBVO : bvos) {
				if(map.containsKey(chnAreaBVO.getPk_chnarea())){
					sf=map.get(chnAreaBVO.getPk_chnarea());
					if(!sf.toString().contains(chnAreaBVO.getVprovname())){
						sf=sf.append(chnAreaBVO.getVprovname()).append(",");
						map.put(chnAreaBVO.getPk_chnarea(), sf);
					}
				}else{
					sf=new StringBuffer();
					sf.append(chnAreaBVO.getVprovname()).append(",");
					map.put(chnAreaBVO.getPk_chnarea(), sf);
				}
			}
		}
		if(vos != null){
//			UserVO uvo = null;
			QueryDeCodeUtils.decKeyUtils(new String[]{"username"}, vos, 1);
			for (ChnAreaVO chnAreaVO : vos) {
//				uvo=UserCache.getInstance().get(chnAreaVO.getUserid(), null);
//				if(uvo != null){
//					chnAreaVO.setUsername(uvo.getUser_name());
//				}
				StringBuffer isf = map.get(chnAreaVO.getPk_chnarea());
				if(isf!=null){
					chnAreaVO.setVprovnames(isf.toString().substring(0,isf.toString().length()-1));
				}
			}
		}
		if(vos != null && vos.size() > 0){
			return vos.toArray(new ChnAreaVO[0]);
		}else{
			return new ChnAreaVO[0];
		}
	}
	

	@Override
	public ChnAreaVO save(ChnAreaVO vo) throws DZFWarpException {
		vo.setAreacode(vo.getAreacode().replaceAll(" ", ""));
		vo.setAreaname(vo.getAreaname().replaceAll(" ", ""));
		if(!checkIsUnique(vo) ){
			throw new BusinessException("大区编码或名称重复,请重新输入");
		}
		if(!StringUtil.isEmpty(vo.getPk_chnarea())){
			ChnAreaVO oldvo =(ChnAreaVO) singleObjectBO.queryByPrimaryKey(ChnAreaVO.class, vo.getPk_chnarea());
			vo.setDoperatedate(oldvo.getDoperatedate());
			vo.setCoperatorid(oldvo.getCoperatorid());
			vo.setTs(oldvo.getTs());
			SQLParameter sp = new SQLParameter();
			StringBuffer sql = new StringBuffer();
			sp.addParam(vo.getPk_chnarea());
			sql.append("update cn_chnarea_b set dr = 1 where pk_chnarea=? and nvl(dr,0)=0  ");
			singleObjectBO.executeUpdate(sql.toString(), sp);
		}
		vo=(ChnAreaVO) multBodyObjectBO.saveMultBObject(vo.getPk_corp(), vo);
		if(!checkCorpIsOnly(vo.getType()) ){
			throw new BusinessException("加盟商重复,请重新输入");
		}
		SuperVO[] bvos = (SuperVO[])vo.getTableVO("cn_chnarea_b");
		vo.setChildren(bvos);
		return vo;
	}
	
	/**
	 * 校验加盟商是否重复
	 * @param vo
	 * @return
	 */
	private boolean checkCorpIsOnly(Integer type) {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(type);
		sql.append(" select count(1) as count from ( ");
		sql.append(" select count(1)  from cn_chnarea_b  ");
		sql.append(" where pk_corp is not null and nvl(dr,0)=0 and type=? ");
		sql.append(" group by pk_corp having count(1)>1) ");
		String res = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		int num = Integer.valueOf(res);
		if(num <= 0)
			ret = true;
		return ret;
	}
	
	/**
	 * 校验大区步骤,大区编码
	 * @param vo
	 * @return
	 */
	private boolean checkIsUnique(ChnAreaVO vo) {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) as count from cn_chnarea");
		sql.append(" where pk_corp=? and nvl(dr,0) = 0 ");
		sql.append(" and (areaname = ? or areacode = ?)");
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getAreaname());
		sp.addParam(vo.getAreacode());
		if(!StringUtil.isEmpty(vo.getPk_chnarea())){
			sql.append(" and pk_chnarea != ?");
			sp.addParam(vo.getPk_chnarea());
		}
		sql.append(" and type = ?");
		sp.addParam(vo.getType());
		String res = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		int num = Integer.valueOf(res);
		if(num <= 0)
			ret = true;
		return ret;
	}
	
	/**
	 * 通过主表主键查询主从表数据
	 */
	@Override
	public ChnAreaVO queryByPrimaryKey(String pk) throws DZFWarpException {
		ChnAreaVO hvo = (ChnAreaVO)singleObjectBO.queryVOByID(pk, ChnAreaVO.class);
		if(hvo != null){
			if(!StringUtil.isEmpty(hvo.getUserid())){
				UserVO uvo = userServiceImpl.queryUserJmVOByID(hvo.getUserid());
				if(uvo != null){
					hvo.setUsername(uvo.getUser_name());
				}
			}
			ChnAreaBVO[] bvos = queryBy1ID(pk);
			hvo.setChildren(bvos); 
		}
		return hvo;
	}
	
	private ChnAreaBVO[] queryBy1ID(String pk) throws DZFWarpException {
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append("SELECT b.pk_chnarea,b.pk_corp,b.vprovince,b.vprovname,b.ischarge,b.userid,b.vmemo,b.ts,us.user_name as username");
		corpsql.append(" FROM cn_chnarea_b b ");
		corpsql.append(" left join sm_user us on us.cuserid =b.userid ");
		corpsql.append(" where nvl(b.dr,0)= 0 and b.pk_chnarea = ? ");
		corpsql.append(" order by ts desc");
		sp.addParam(pk);
		Map<String, ChnAreaBVO > map = new HashMap<String,ChnAreaBVO>();
		List<ChnAreaBVO> b1vos = (List<ChnAreaBVO>) singleObjectBO.executeQuery(corpsql.toString(),sp,new BeanListProcessor(ChnAreaBVO.class));
		int i=0;
		CorpVO cvo =null;
		QueryDeCodeUtils.decKeyUtils(new String[]{"username"}, b1vos, 1);
		for (ChnAreaBVO chnAreaBVO : b1vos) {
			cvo=CorpCache.getInstance().get(null, chnAreaBVO.getPk_corp());
			if(!StringUtil.isEmpty(chnAreaBVO.getUserid())){
				String id=chnAreaBVO.getVprovince()+chnAreaBVO.getUserid();
				if(map.containsKey(id)){
					ChnAreaBVO vo = map.get(id);
					if(!StringUtil.isEmpty(chnAreaBVO.getPk_corp())){
						vo.setPk_corp(vo.getPk_corp()+","+chnAreaBVO.getPk_corp());
						vo.setCorpname(vo.getCorpname()+","+cvo.getUnitname());
						vo.setInnercode(vo.getInnercode()+","+cvo.getInnercode());
					}
					map.put(id,vo);
				}else{
					if(cvo!=null){
						chnAreaBVO.setCorpname(cvo.getUnitname());
						chnAreaBVO.setInnercode(cvo.getInnercode());
					}
					map.put(id,chnAreaBVO);
				}
			}else{
				if(cvo!=null){
					chnAreaBVO.setCorpname(cvo.getUnitname());
					chnAreaBVO.setInnercode(cvo.getInnercode());
				}
				map.put(i+"",chnAreaBVO);
				i++;
			}
		}
		Collection<ChnAreaBVO> vos = map.values();
		List<ChnAreaBVO> list= new ArrayList<ChnAreaBVO>(vos);
		return list.toArray(new ChnAreaBVO[0]);
	}
	
	@Override
	public void delete(String pk,String pk_corp) throws DZFWarpException {
		if (!StringUtil.isEmpty(pk)) {
			SQLParameter sp = new SQLParameter();
			StringBuffer main_sql = new StringBuffer();
			StringBuffer depe_sql = new StringBuffer();
			sp.addParam(pk);
			depe_sql.append("update cn_chnarea_b set dr = 1 where pk_chnarea= ? and nvl(dr,0)=0");
			singleObjectBO.executeUpdate(depe_sql.toString(), sp);
			sp.addParam(pk_corp);
			main_sql.append("update cn_chnarea set dr = 1 where pk_chnarea = ? and pk_corp =? and nvl(dr,0)=0");
			singleObjectBO.executeUpdate(main_sql.toString(), sp);
		}else{
			throw new BusinessException("删除失败");
		}
	}

	@Override
	public String queryManager(String pk_corp) throws DZFWarpException {
		StringBuffer sql= new StringBuffer();
		sql.append("select ld.vdeptuserid userid,us.user_name as username from cn_leaderset ld ");
		sql.append(" left join sm_user us on us.cuserid = ld.vdeptuserid ");
		sql.append(" where nvl(ld.dr,0) = 0 and ld.pk_corp = ? ");
//		String sql="select vdeptuserid userid from cn_leaderset where nvl(dr,0)=0 and pk_corp=? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		ChnAreaBVO vo =(ChnAreaBVO) singleObjectBO.executeQuery(sql.toString(), sp, new BeanProcessor(ChnAreaBVO.class));
		String username=null;
		if(vo!=null&&!StringUtil.isEmpty(vo.getUsername())){
		    username = CodeUtils1.deCode(vo.getUsername());
		}
		return username;
	}
	
	@Override
	public ArrayList queryComboxArea(String pk_area,String type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select region_id as id, region_name as name  ");
		sql.append("  from ynt_area  ");
		sql.append(" where nvl(dr, 0) = 0  ");
		sql.append("   and parenter_id = 1 and region_id not in  ");
		sql.append("    (select vprovince from cn_chnarea_b where nvl(dr,0)=0 ");
		sql.append(" and type = ? ");
		sp.addParam(type);
		if(!StringUtil.isEmpty(pk_area)){
			sql.append(" and pk_chnarea != ?");
			sp.addParam(pk_area);
		}
		sql.append("   ) order by region_id asc ");
		ArrayList list = (ArrayList) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ComboBoxVO.class));
		return list;
	}
	
	@Override
    public List<ComboBoxVO> queryArea(QryParamVO paramvo) throws DZFWarpException {
		List<ComboBoxVO> list=null;
		StringBuffer buf=new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append(" select distinct a.areaname as name, a.areacode as id ");
		buf.append("  from cn_chnarea_b b left join cn_chnarea a on a.pk_chnarea = b.pk_chnarea ");
		buf.append("  where nvl(b.dr, 0) = 0 and nvl(a.dr, 0) = 0 and a.type=? ");
		spm.addParam(paramvo.getQrytype());
		Integer level=pubService.getDataLevel(paramvo.getCuserid());
		if(paramvo.getSeletype()!=null && paramvo.getSeletype()==1 ){
			//销售数据
		}else if(level==null){
			return list;
		}else if(level==2){
			buf.append("  and a.userid=? ");
			spm.addParam(paramvo.getCuserid());
		}else if(level==3){
			buf.append("  and b.userid=? ");
			spm.addParam(paramvo.getCuserid());
		}
		list =(List<ComboBoxVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ComboBoxVO.class));
		return list;
	}
    
	@Override
	public List<ComboBoxVO> queryProvince(QryParamVO paramvo) throws DZFWarpException {
		List<ComboBoxVO> list=null;
		StringBuffer buf=new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append(" select distinct b.vprovname as name, b.vprovince as id");
		buf.append("  from cn_chnarea_b b left join cn_chnarea a on a.pk_chnarea = b.pk_chnarea");
		buf.append("  where nvl(b.dr, 0) = 0 and nvl(a.dr, 0) = 0 and a.type=? ");
		spm.addParam(paramvo.getQrytype());
		Integer level=pubService.getDataLevel(paramvo.getCuserid());
		if(paramvo.getSeletype()!=null && paramvo.getSeletype()==1 ){
			//销售数据
		}else if(level==null){
			return list;
		}else if(level==2){
			buf.append("  and a.userid=? ");
			spm.addParam(paramvo.getCuserid());
		}else if(level==3){
			buf.append("  and b.userid=? ");
			spm.addParam(paramvo.getCuserid());
		}
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			buf.append("  and a.areaname=? ");
			spm.addParam(paramvo.getAreaname());
		}
		list =(List<ComboBoxVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ComboBoxVO.class));
		return list;
	}
	
	@Override
	public List<ComboBoxVO> queryTrainer(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer buf=new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append(" select distinct b.userid as id,us.user_name as name");
		buf.append("  from cn_chnarea_b b ");
		buf.append("  left join cn_chnarea a on a.pk_chnarea = b.pk_chnarea");
		buf.append("  left join sm_user us on us.cuserid = b.userid");
		buf.append("  where nvl(b.dr, 0) = 0 and nvl(a.dr, 0) = 0 and a.type=? ");
		spm.addParam(paramvo.getQrytype());
		Integer level=pubService.getDataLevel(paramvo.getCuserid());
		if(paramvo.getSeletype()!=null && paramvo.getSeletype()==1 ){
			//销售数据
		}else if(level==null){
			return null;
		}else if(level==2){
			buf.append("  and a.userid=? ");
			spm.addParam(paramvo.getCuserid());
		}else if(level==3){
			buf.append("  and b.userid=? ");
			spm.addParam(paramvo.getCuserid());
		}
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			buf.append("  and a.areaname=? ");
			spm.addParam(paramvo.getAreaname());
		}
		if(paramvo.getVprovince()!=null){
			buf.append("  and b.vprovince=? ");
			spm.addParam(paramvo.getVprovince());
		}
		List<ComboBoxVO> list =(List<ComboBoxVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ComboBoxVO.class));
		QueryDeCodeUtils.decKeyUtils(new String[]{"name"}, list, 1);
		return list;
	}
	
}
