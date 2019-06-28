package com.dzf.service.channel.matmanage.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
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

}
