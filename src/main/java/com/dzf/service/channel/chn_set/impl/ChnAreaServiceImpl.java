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
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.service.channel.chn_set.IChnAreaService;

@Service("chn_area")
public class ChnAreaServiceImpl implements IChnAreaService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO = null;

	@SuppressWarnings("unchecked")
	@Override
	public ChnAreaVO[] query(ChnAreaVO qvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		StringBuffer sf=  new StringBuffer();
		sql.append("select * from cn_chnarea where nvl(dr,0) = 0");
		sql.append(" and pk_corp=? and type=?  order by areacode " );   
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
			UserVO uvo = null;
			for (ChnAreaVO chnAreaVO : vos) {
				uvo=UserCache.getInstance().get(chnAreaVO.getUserid(), null);
				if(uvo != null){
					chnAreaVO.setUsername(uvo.getUser_name());
				}
				StringBuffer isf = map.get(chnAreaVO.getPk_chnarea());
				if(isf!=null){
					chnAreaVO.setVprovnames(isf.toString().substring(0,isf.toString().length()-1));
				}
			}
		}
		return vos.toArray(new ChnAreaVO[0]);
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
		UserVO user = UserCache.getInstance().get(hvo.getUserid(), null);
		if(user != null){
			hvo.setUsername(user.getUser_name());
		}
		if(hvo != null){
			ChnAreaBVO[] bvos = queryBy1ID(pk);
			hvo.setChildren(bvos); 
		}
		return hvo;
	}
	
	private ChnAreaBVO[] queryBy1ID(String pk) throws DZFWarpException {
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append("SELECT pk_chnarea,pk_corp,vprovince,vprovname,ischarge,userid,vmemo,ts");
		corpsql.append(" FROM cn_chnarea_b where nvl(dr,0)= 0");
		corpsql.append(" and pk_chnarea = ? ");
		corpsql.append(" order by ts desc");
		sp.addParam(pk);
		Map<String, ChnAreaBVO > map = new HashMap<String,ChnAreaBVO>();
		List<ChnAreaBVO> b1vos = (List<ChnAreaBVO>) singleObjectBO.executeQuery(corpsql.toString(),sp,new BeanListProcessor(ChnAreaBVO.class));
		int i=0;
		CorpVO cvo =null;
		UserVO user =null;
		for (ChnAreaBVO chnAreaBVO : b1vos) {
			user = UserCache.getInstance().get(chnAreaBVO.getUserid(), null);
			if(user != null){
				chnAreaBVO.setUsername(user.getUser_name());
			}
			cvo=CorpCache.getInstance().get(null, chnAreaBVO.getPk_corp());
			if(!StringUtil.isEmpty(chnAreaBVO.getUserid())){
				String id=chnAreaBVO.getVprovince()+chnAreaBVO.getUserid();
				if(map.containsKey(id)){
					ChnAreaBVO vo = map.get(id);
					if(!StringUtil.isEmpty(chnAreaBVO.getPk_corp())){
						vo.setPk_corp(vo.getPk_corp()+","+chnAreaBVO.getPk_corp());
						vo.setCorpname(vo.getCorpname()+","+cvo.getUnitname());
					}
					map.put(id,vo);
				}else{
					if(cvo!=null){
						chnAreaBVO.setCorpname(cvo.getUnitname());
					}
					map.put(id,chnAreaBVO);
				}
			}else{
				if(cvo!=null){
					chnAreaBVO.setCorpname(cvo.getUnitname());
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
		String sql="select vdeptuserid userid from cn_leaderset where nvl(dr,0)=0 and pk_corp=? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		ChnAreaBVO vo =(ChnAreaBVO) singleObjectBO.executeQuery(sql, sp, new BeanProcessor(ChnAreaBVO.class));
		String username=null;
		if(vo!=null&&!StringUtil.isEmpty(vo.getUserid())){
			UserVO uvo=UserCache.getInstance().get(vo.getUserid(), null);
			if(uvo!=null){
				username=uvo.getUser_name();
			}
		}
		return username;
	}
	
	@Override
	public ArrayList queryComboxArea(String pk_area,String type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select region_id as id, region_name as name\n");
		sql.append("  from ynt_area\n");
		sql.append(" where nvl(dr, 0) = 0\n");
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
		buf.append("  from cn_chnarea_b b left join cn_chnarea a on a.pk_chnarea = b.pk_chnarea");
		buf.append("  where nvl(b.dr, 0) = 0 and nvl(a.dr, 0) = 0 and ");
		if(paramvo.getQrytype()!=2){
			buf.append("  a.type=?");//1,3 是查询渠道区域划分
			spm.addParam(1);
		}else{
			buf.append("  a.type=?");//2     是查询培训区域划分
			spm.addParam(2);
		}
		if(!checkIsLeader(paramvo) || paramvo.getQrytype()!=3 ){//3是为了销售数据分析下拉，这个暂时没有权限限制
			buf.append("  and (b.userid=? or a.userid=?)");
			spm.addParam(paramvo.getCuserid());
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
		buf.append("  where nvl(b.dr, 0) = 0 and nvl(a.dr, 0) = 0 and ");
		if(paramvo.getQrytype()!=2){
			buf.append("  a.type=?");//1,3 是查询渠道区域划分
			spm.addParam(1);
		}else{
			buf.append("  a.type=?");//2     是查询培训区域划分
			spm.addParam(2);
		}
		if(!checkIsLeader(paramvo) || paramvo.getQrytype()!=3 ){
			buf.append("  and (b.userid=? or a.userid=?)");
			spm.addParam(paramvo.getCuserid());
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
		List<ComboBoxVO> list=null;
		UserVO uvo=null;
		StringBuffer buf=new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append(" select distinct b.userid as id ");
		buf.append("  from cn_chnarea_b b left join cn_chnarea a on a.pk_chnarea = b.pk_chnarea");
		buf.append("  where nvl(b.dr, 0) = 0 and nvl(a.dr, 0) = 0 and ");
		if(paramvo.getQrytype()!=2){
			buf.append("  a.type=?");//1,3 是查询渠道区域划分
			spm.addParam(1);
		}else{
			buf.append("  a.type=?");//2     是查询培训区域划分
			spm.addParam(2);
		}
		if(!checkIsLeader(paramvo) || paramvo.getQrytype()!=3){
			buf.append("  and (b.userid=? or a.userid=?)");
			spm.addParam(paramvo.getCuserid());
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
		list =(List<ComboBoxVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ComboBoxVO.class));
		for (ComboBoxVO comboBoxVO : list) {
			uvo = UserCache.getInstance().get(comboBoxVO.getId(), null);
			if(uvo!=null){
				comboBoxVO.setName(uvo.getUser_name());
			}
		}
		return list;
	}
	
	private boolean checkIsLeader(QryParamVO paramvo) {
		String sql="select vdeptuserid corpname,vcomuserid username,vgroupuserid cusername from cn_leaderset where nvl(dr,0)=0";
		List<ManagerVO> list =(List<ManagerVO>)singleObjectBO.executeQuery(sql, null, new BeanListProcessor(ManagerVO.class));
		if(list!=null&&list.size()>0){
			ManagerVO vo=list.get(0);
			if(paramvo.getCuserid().equals(vo.getCusername())||paramvo.getCuserid().equals(vo.getCorpname())||paramvo.getCuserid().equals(vo.getUsername())){
				return true;
			}
		}
		return false;
	}
	
}
