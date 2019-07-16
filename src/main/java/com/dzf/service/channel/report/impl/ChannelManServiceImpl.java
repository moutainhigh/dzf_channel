package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.report.IChannelManService;
import com.dzf.service.pub.IPubService;

@Service("manchannel")
public class ChannelManServiceImpl extends ManCommonServiceImpl implements IChannelManService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
    @Autowired
    private IPubService pubService;
    
	private int qrytype = IStatusConstant.IQUDAO;
    
	private String wheresql = QueryUtil.getWhereSql();
	
	@Override
	public List<ManagerVO> query(ManagerVO qvo) throws DZFWarpException {
		ArrayList<ManagerVO> retList = new ArrayList<>();
		Integer level = pubService.getDataLevel(qvo.getUserid());
		
		ArrayList<String> pk_corps = new ArrayList<>();
		Map<String, ManagerVO> manaMap = new HashMap<>();
		if(level!=null && level<=3){
			manaMap = qryChannel(qvo,pk_corps);
		}
		if(pk_corps!=null && pk_corps.size()!=0){
			retList = queryCommon(qvo,manaMap,pk_corps);
			sortList(retList);
		}
		return retList;
	}
	
	
	/**
	 * 查询省市数据分析
	 * @param qvo
	 * @return
	 */
	private Map<String, ManagerVO> qryChannel(ManagerVO qvo,ArrayList<String> pk_corps) {
 		Map<String, ManagerVO> map = new HashMap<>();
 		
		String corpName;
		List<ManagerVO> qryCharge= qryCharge(qvo);		
		if(qryCharge != null && qryCharge.size()>0){
			Map<String, UserVO> opermap = pubService.getManagerMap(qrytype);// 渠道运营
			Map<Integer, ChnAreaVO> chnmap = pubService.getChnMap(qvo.getAreaname(), qrytype);// 渠道运营
			ChnAreaVO areaVO ;
			UserVO userVO;
			for (ManagerVO managerVO : qryCharge) {
				corpName = CodeUtils1.deCode(managerVO.getCorpname());
				if (StringUtil.isEmpty(qvo.getCorpname()) || corpName.indexOf(qvo.getCorpname()) != -1) {
					areaVO = chnmap.get(managerVO.getVprovince());
					managerVO.setVprovname(areaVO.getVprovnames());
					managerVO.setAreaname(areaVO.getAreaname());
					// managerVO.setUsername(areaVO.getUsername());

					managerVO.setCorpname(corpName);
					userVO = opermap.get(managerVO.getPk_corp());
					if (userVO != null) {
						managerVO.setCusername(userVO.getUser_name());
					}
					setDefult(managerVO);
					pk_corps.add(managerVO.getPk_corp());
					map.put(managerVO.getPk_corp(), managerVO);
				}
			}
			
        }
		List<ManagerVO> qryNotCharge = qryNotCharge(qvo);
        if(qryNotCharge != null && qryNotCharge.size() > 0){
        	for (ManagerVO managerVO2 : qryNotCharge) {
    			if(!map.containsKey(managerVO2.getPk_corp())){
    				corpName = CodeUtils1.deCode(managerVO2.getCorpname());
    				if(StringUtil.isEmpty(qvo.getCorpname()) || corpName.indexOf(qvo.getCorpname()) != -1){
    					managerVO2.setCorpname(corpName);
    					setDefult(managerVO2);
    					pk_corps.add(managerVO2.getPk_corp());
    					map.put(managerVO2.getPk_corp(), managerVO2);
    				}
    			}
			}
        }
		return map;
	}
	
	/**
	 * 查询  是  省/市负责人相关的数据
	 * @param qvo
	 * @return
	 */
	private List<ManagerVO> qryCharge(ManagerVO qvo) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select account.pk_corp, account.innercode, account.unitname corpname,");
		sql.append("       account.drelievedate,account.vprovince    ");
		sql.append("  from bd_account account ");
		sql.append(" where ").append(wheresql);
		sql.append("   and nvl(account.dr, 0) = 0 ");
		sql.append("   and exists (select distinct b.vprovince ");
		sql.append("          from cn_chnarea_b b ");
		sql.append("         where b.userid = ? ");
		sql.append("           and nvl(b.dr, 0) = 0 ");
		sql.append("           and type = ? ");
		sql.append("           and nvl(b.ischarge, 'N') = 'Y' ");
		sql.append("           and account.vprovince = b.vprovince) ");
		sp.addParam(qvo.getUserid());
    	sp.addParam(qrytype);
	    List<ManagerVO> list =(List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ManagerVO.class));
	    return list;
	}
	
	/**
	 * 查询非省市负责人数据
	 * @param qvo
	 * @return
	 */
	private List<ManagerVO> qryNotCharge(ManagerVO qvo) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select account.pk_corp, ");
		sql.append("       account.innercode, ");
		sql.append("       account.unitname corpname,");
		sql.append("       account.drelievedate, ");//解约日期
		sql.append("       a.areaname, ");
		sql.append("       a.areacode, ");
		sql.append("       a.userid, ");
		sql.append("       b.userid cuserid, ");
		sql.append("       b.vprovname, ");
		sql.append("       b.vprovince ");
		sql.append("  from bd_account account ");
		sql.append(" right join cn_chnarea_b b on account.pk_corp = b.pk_corp ");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea ");
		sql.append(" where ").append(wheresql);
		sql.append("   and nvl(b.dr, 0) = 0 ");
		sql.append("   and nvl(a.dr, 0) = 0 ");
		sql.append("   and b.type = ? ");
		sql.append("   and nvl(b.ischarge, 'N') = 'N' ");
		sql.append("   and b.userid=? ");
		sp.addParam(qrytype);
		sp.addParam(qvo.getUserid());
		List<ManagerVO> list = (List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ManagerVO.class));
		return list;
	}
	
	
}
