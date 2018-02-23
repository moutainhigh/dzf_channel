package com.dzf.service.channel.rebate.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.rebate.ManagerRefVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.rebate.IRebateInptService;

@Service("rebateinptser")
public class RebateInptServiceImol implements IRebateInptService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public RebateVO save(RebateVO data, String pk_corp) throws DZFWarpException {
		try {
			chcekBeforeSave(data);
			LockUtil.getInstance().tryLockKey(data.getTableName(), data.getPk_corp()+""+data.getVyear()+""+data.getIseason(), 10);
			return (RebateVO) singleObjectBO.saveObject(pk_corp, data);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_corp()+""+data.getVyear()+""+data.getIseason());
		}
	}
	
	/**
	 * 保存前校验
	 * @param data
	 * @throws DZFWarpException
	 */
	private void chcekBeforeSave(RebateVO data) throws DZFWarpException{
		checkCodeOnly(data);//返点单单号唯一性校验
		checkDataOnly(data);//返点信息年-季度唯一性校验
	}
	
	/**
	 * 返点单号唯一性校验
	 * @param vo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public void checkCodeOnly(RebateVO vo) throws DZFWarpException {
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		if (!StringUtil.isEmptyWithTrim(vo.getVbillcode())) {
			sql.append(" and vbillcode = ? ");
			spm.addParam(vo.getVbillcode());
		} else {
			throw new BusinessException("返点单号不能为空");
		}
		if (!StringUtil.isEmpty(vo.getPk_rebate())) {
			sql.append(" and pk_rebate != ? ");
			spm.addParam(vo.getPk_rebate());
		}
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RebateVO.class));
		if (list != null && list.size() > 0) {
			throw new BusinessException("返点单号："+vo.getVbillcode()+"已经在系统中存在");
		}
	}
	
	/**
	 * 返点信息年-季度唯一性校验
	 * @param vo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public void checkDataOnly(RebateVO vo) throws DZFWarpException {
		if(StringUtil.isEmpty(vo.getVyear())){
			throw new BusinessException("所属年不能为空");
		}
		if(vo.getIseason() == null){
			throw new BusinessException("所属季度不能为空");
		}
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vbillcode from cn_rebate where nvl(dr,0) = 0 ");
		if (!StringUtil.isEmptyWithTrim(vo.getPk_corp())) {
			sql.append(" and pk_corp = ? ");
			spm.addParam(vo.getPk_corp());
		} else {
			throw new BusinessException("加盟商信息不能为空");
		}
		sql.append(" and vyear = ? and iseason = ? ");
		spm.addParam(vo.getVyear());
		spm.addParam(vo.getIseason());
		if (!StringUtil.isEmpty(vo.getPk_rebate())) {
			sql.append(" and pk_rebate != ? ");
			spm.addParam(vo.getPk_rebate());
		}
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RebateVO.class));
		if (list != null && list.size() > 0) {
			String corpname = "";
			CorpVO corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if(corpvo != null){
				corpname = corpvo.getUnitname();
			}
			throw new BusinessException("加盟商："+corpname+vo.getVyear()+"年"+vo.getIseason()+"季度返点单，已经在系统中存在");
		}
	}

	@Override
	public List<ManagerRefVO> queryManagerRef(QryParamVO paramvo) throws DZFWarpException {
		List<ManagerRefVO> retlist = new ArrayList<ManagerRefVO>();
		ChnAreaBVO[] areaVOs = (ChnAreaBVO[]) singleObjectBO.queryByCondition(ChnAreaBVO.class, " nvl(dr,0) = 0 ",
				null);
		if (areaVOs != null && areaVOs.length > 0) {
			UserVO uservo = null;
			ManagerRefVO refvo = null;
			for (ChnAreaBVO vo : areaVOs) {
				if (!StringUtil.isEmpty(vo.getUserid())) {
					refvo = new ManagerRefVO();
					refvo.setCuserid(vo.getUserid());
					uservo = UserCache.getInstance().get(vo.getUserid(), null);
					if (uservo != null) {
						refvo.setUsercode(uservo.getUser_code());
						refvo.setUsername(uservo.getUser_name());
					}
					if (!StringUtil.isEmpty(paramvo.getUser_code())) {
						if (!StringUtil.isEmpty(refvo.getUsercode()) || !StringUtil.isEmpty(refvo.getUsername())) {
							if ((!StringUtil.isEmpty(refvo.getUsercode())
									&& refvo.getUsercode().indexOf(paramvo.getUser_code()) != -1)
									|| (!StringUtil.isEmpty(refvo.getUsername())
											&& refvo.getUsername().indexOf(paramvo.getUser_code()) != -1)) {
								retlist.add(refvo);
							}
						}
					} else {
						retlist.add(refvo);
					}
				}
			}
		}
		return retlist;
	}

}
