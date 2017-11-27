package com.dzf.service.channel.sale.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.cache.UserCache;
import com.dzf.service.channel.sale.IChnAreaService;

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
		sql.append("select * from cn_chnarea where nvl(dr,0) = 0");
		sql.append(" and pk_corp=? order by areacode " );   
		sp.addParam(qvo.getPk_corp());
		List<ChnAreaVO> vos =(List<ChnAreaVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ChnAreaVO.class));
		ChnAreaBVO[] bvos = queryBy1ID(null, qvo.getPk_corp());
		Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
		if(bvos!=null&&bvos.length>0){
			for (ChnAreaBVO chnAreaBVO : bvos) {
				if(map.containsKey(chnAreaBVO.getPk_chnarea())){
					StringBuffer sf=map.get(chnAreaBVO.getPk_chnarea()).append(chnAreaBVO.getVprovname()).append(",");
					map.put(chnAreaBVO.getPk_chnarea(), sf);
				}else{
					StringBuffer sf=new StringBuffer();
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
		}
		vo=(ChnAreaVO) multBodyObjectBO.saveMultBObject(vo.getPk_corp(), vo);
		SuperVO[] bvos = (SuperVO[])vo.getTableVO("cn_chnarea_b");
		vo.setChildren(bvos);
		return vo;
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
	public ChnAreaVO queryByPrimaryKey(String pk,String pk_corp) throws DZFWarpException {
		ChnAreaVO hvo = (ChnAreaVO)singleObjectBO.queryVOByID(pk, ChnAreaVO.class);
		UserVO user = UserCache.getInstance().get(hvo.getUserid(), null);
		if(user != null){
			hvo.setUsername(user.getUser_name());
		}
		if(hvo != null){
			ChnAreaBVO[] bvos = queryBy1ID(pk, pk_corp);
			if(bvos!=null&&bvos.length>0){
				for (ChnAreaBVO chnAreaBVO : bvos) {
					user = UserCache.getInstance().get(chnAreaBVO.getUserid(), null);
					if(user != null){
						chnAreaBVO.setUsername(user.getUser_name());
					}
				}
			}
			hvo.setChildren(bvos); 
		}
		return hvo;
	}
	
	private ChnAreaBVO[] queryBy1ID(String pk,String pk_corp) throws DZFWarpException {
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append("SELECT * FROM cn_chnarea_b where nvl(dr,0)= 0");
		if(pk!=null){
			corpsql.append(" and pk_chnarea = ? ");
			sp.addParam(pk);
		}
		corpsql.append(" and pk_corp = ?");
		sp.addParam(pk_corp);
		corpsql.append(" order by ts asc");
		List<ChnAreaBVO> b1vos = (List<ChnAreaBVO>) singleObjectBO.executeQuery(corpsql.toString(),sp,new BeanListProcessor(ChnAreaBVO.class));
		return b1vos.toArray(new ChnAreaBVO[0]);
	}
	
	@Override
	public void delete(String pk,String pk_corp) throws DZFWarpException {
		if (!StringUtil.isEmpty(pk)) {
			SQLParameter sp = new SQLParameter();
			StringBuffer sql = new StringBuffer();
			StringBuffer sql1 = new StringBuffer();
			StringBuffer sql2 = new StringBuffer();
			sp.addParam(pk);
			sp.addParam(pk_corp);
			sql.append("update cn_chnarea set dr = 1 where pk_chnarea = ? and pk_corp =? and nvl(dr,0)=0");
			sql1.append("update cn_chnarea_b set dr = 1 where pk_chnarea= ? and pk_corp =? and nvl(dr,0)=0");
			singleObjectBO.executeUpdate(sql.toString(), sp);
			singleObjectBO.executeUpdate(sql1.toString(), sp);
		}else{
			throw new BusinessException("删除失败");
		}
	}
	
}
