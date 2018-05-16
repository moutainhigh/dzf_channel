package com.dzf.service.channel.chn_set.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.sale.RejectreasonVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;

@Service("rejectreasonser")
public class RejectreasonServiceImpl implements IRejectreasonService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Override
	public Integer queryTotalRow(QryParamVO paramvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo,uservo);
		return multBodyObjectBO.queryDataTotal(RejectreasonVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RejectreasonVO> query(QryParamVO paramvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo,uservo);
		return (List<RejectreasonVO>) multBodyObjectBO.queryDataPage(RejectreasonVO.class, 
					sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
	}

	/**
	 * 获取查询条件
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO paramvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		String sql = " SELECT * FROM cn_rejectreason n WHERE nvl(n.dr, 0) = 0 ORDER BY ts DESC ";
		qryvo.setSql(sql);
		return qryvo;
	}

	@Override
	public RejectreasonVO save(RejectreasonVO data, String pk_corp) throws DZFWarpException {
		boolean flag = isExist(data);
		if(flag){
			throw new BusinessException("保存失败，驳回原因重复");
		}
		if (StringUtil.isEmpty(data.getPk_rejectreason())) {// 新增操作
			return (RejectreasonVO) singleObjectBO.saveObject(pk_corp, data);
		} else {// 更新操作
			checkBeforeUpdate(data);
			data.setUpdatets(new DZFDateTime());//最后修改日期
			String uuid = UUID.randomUUID().toString();
			try {
				LockUtil.getInstance().tryLockKey(data.getTableName(), data.getPk_rejectreason(),uuid, 30);
				singleObjectBO.update(data,
						new String[] { "vreason", "vsuggest", "lastmodifypsnid", "updatets" });
			} finally {
				LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_rejectreason(),uuid);
			}
		}
		return data;
	}
	
	/**
	 * 校验
	 * @throws DZFWarpException
	 */
	private void checkBeforeUpdate(RejectreasonVO data) throws DZFWarpException {
		RejectreasonVO oldvo = (RejectreasonVO) singleObjectBO.queryByPrimaryKey(RejectreasonVO.class,
				data.getPk_rejectreason());
		if(oldvo != null && ((oldvo.getDr() != null && oldvo.getDr() != 1) || oldvo.getDr() == null)){
			if(oldvo.getUpdatets().compareTo(data.getUpdatets()) != 0){
				throw new BusinessException("数据发生变化，请刷新后再次尝试");
			}
		}else{
			throw new BusinessException("数据发生变化，请刷新后再次尝试");
		}
	}

	@Override
	public void delete(RejectreasonVO data) throws DZFWarpException {
		checkBeforeUpdate(data);
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(data.getTableName(), data.getPk_rejectreason(),uuid, 30);
			singleObjectBO.deleteObject(data);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_rejectreason(),uuid);
		}
	}

	@Override
	public RejectreasonVO queryById(RejectreasonVO data) throws DZFWarpException {
		RejectreasonVO oldvo = (RejectreasonVO) singleObjectBO.queryByPrimaryKey(RejectreasonVO.class,
				data.getPk_rejectreason());
		if(oldvo != null && ((oldvo.getDr() != null && oldvo.getDr() != 1) || oldvo.getDr() == null)){
			return oldvo;
		}else{
			throw new BusinessException("该数据已经被删除，请刷新后再次尝试");
		}
	}
	
	/**
	 * 数据唯一性校验
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public boolean isExist(RejectreasonVO data) throws DZFWarpException {
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vreason from cn_rejectreason where nvl(dr,0) = 0 ");
		sql.append(" and pk_corp = ? ");
		spm.addParam(data.getPk_corp());
		if (!StringUtil.isEmptyWithTrim(data.getVreason())) {
			sql.append(" and vreason = ? ");
			spm.addParam(data.getVreason());
		} else {
			throw new BusinessException("驳回原因不能为空");
		}
		if (!StringUtil.isEmpty(data.getPk_rejectreason())) {
			sql.append(" and pk_rejectreason != ? ");
			spm.addParam(data.getPk_rejectreason());
		}
		List<RejectreasonVO> list = (List<RejectreasonVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RejectreasonVO.class));
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
}
