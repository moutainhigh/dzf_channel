package com.dzf.service.channel.chn_set.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.model.channel.sale.AccountSetVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.chn_set.IAccountSetService;
import com.dzf.service.channel.report.impl.DataCommonRepImpl;

@Service("setAccount")
public class AccountSetServiceImpl extends DataCommonRepImpl implements IAccountSetService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<AccountSetVO> queryCorpk(String pk_corp,String corpkname) throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_corp);
		sql.append("select g.vendperiod, ");
		sql.append("       g.pk_corpk, ");
		sql.append("       t.pk_contract, ");
		sql.append("       t.vcontcode, ");
		sql.append("       t.vbeginperiod, ");
		sql.append("       p.unitname corpkname, ");
		sql.append("       p.innercode ");
		sql.append("  from (select max(m.vendperiod) vendperiod, m.pk_corpk ");
		sql.append("          from ynt_contract m ");
		sql.append("         where m.pk_corp = ? ");
		sql.append("           and (m.vstatus = 1 or m.vstatus = 9) ");
		sql.append("           and m.icosttype = 0 ");
		sql.append("           and m.isflag = 'Y' ");
		sql.append("           and nvl(m.dr, 0) = 0 ");
		sql.append("         group by m.pk_corpk) g ");
		sql.append("  left join ynt_contract t on g.pk_corpk = t.pk_corpk ");
		sql.append("                          and g.vendperiod = t.vendperiod ");
		sql.append("  left join bd_corp p on g.pk_corpk = p.pk_corp ");
		sql.append(" where not exists (select pk_contract ");
		sql.append("          from cn_accountset ");
		sql.append("         where t.pk_contract = pk_contract ");
		sql.append("           and nvl(dr, 0) = 0) ");
		sql.append(" order by p.innercode ");
		List<AccountSetVO> list=(List<AccountSetVO>)singleObjectBO.executeQuery(sql.toString(),spm, new BeanListProcessor(AccountSetVO.class));
		if(list!=null && list.size()>0){
			if(!StringUtils.isEmpty(corpkname)){
				List<AccountSetVO> filist = new ArrayList<AccountSetVO>();
				for (AccountSetVO accountSetVO : list) {
					accountSetVO.setCorpkname(CodeUtils1.deCode(accountSetVO.getCorpkname()));
					if ( accountSetVO.getInnercode().indexOf(corpkname) >= 0 || accountSetVO.getCorpkname().indexOf(corpkname) >= 0){
						filist.add(accountSetVO);
					}
				}
				return filist;
			}else{
				QueryDeCodeUtils.decKeyUtils(new String[]{"corpkname"}, list, 1);
				return list;
			}
		}
		return null;
	}
	
	@Override
	public void save(AccountSetVO vo) throws DZFWarpException {
//		AccountSetVO qryContract = qryContract(vo);
		vo.setDoperatedate(new DZFDate());
		vo.setIstatus(0);
		vo.setDr(0);
		singleObjectBO.insertVO(vo.getPk_corp(), vo);
	}
	
	@Override
	public void saveEdit(AccountSetVO vo) throws DZFWarpException{
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getPk_accountset(),uuid, 60);
			checkData(vo);
			singleObjectBO.update(vo, new String[]{"vchangeperiod"});
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_accountset(),uuid);
		}	
	}
	
	@Override
	public void updateStatus(AccountSetVO vo) throws DZFWarpException{
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getPk_accountset(),uuid, 60);
			checkData(vo);
			singleObjectBO.update(vo, new String[]{"istatus"});
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_accountset(),uuid);
		}	
	}
	
	@Override
	public void delete(String[] ids) throws DZFWarpException{
		singleObjectBO.deleteByPKs(AccountSetVO.class, ids);
	}
	
	public AccountSetVO qryContract(AccountSetVO vo) throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(vo.getPk_corp());
		spm.addParam(vo.getPk_corpk());
		spm.addParam(vo.getVendperiod());
		
		sql.append("select max(m.vendperiod) vendperiod, m.pk_corpk, p.unitname corpkname, p.innercode wy");
		sql.append("  from ynt_contract m ");
		sql.append("  left join bd_corp p on m.pk_corpk = p.pk_corp ");
		sql.append(" where m.pk_corp = ? ");
		sql.append("   and (m.vstatus = 1 or m.vstatus = 9) ");
		sql.append("   and m.icosttype = 0 ");
		sql.append("   and m.isflag = 'Y' ");
		sql.append("   and nvl(m.dr, 0) = 0 ");
		sql.append(" group by m.pk_corpk, p.unitname, p.innercode ");
		sql.append(" order by p.innercode ");
		
		
		sql.append("select m.vbeginperiod, m.vendperiod, m.pk_corpk, m.pk_contract, m.vcontcode ");
		sql.append("  from ynt_contract m ");
		sql.append(" where m.pk_corp = ? ");
		sql.append("   and (m.vstatus = 1 or m.vstatus = 9) ");
		sql.append("   and m.icosttype = 0 ");
		sql.append("   and m.isflag = 'Y' ");
		sql.append("   and nvl(m.dr, 0) = 0 ");
		sql.append("   and m.vendperiod = ? ");
		sql.append("   and m.pk_corpk = ? ");
		AccountSetVO retvo=(AccountSetVO)singleObjectBO.executeQuery(sql.toString(),spm, 
				new BeanProcessor(AccountSetVO.class));
		return retvo;
	}
	
	/**
	 * 是否是最新数据
	 * @param qryvo
	 */
	private void checkData(AccountSetVO qvo) throws DZFWarpException{
		AccountSetVO vo =(AccountSetVO) singleObjectBO.queryByPrimaryKey(AccountSetVO.class, qvo.getPk_accountset());
		if(vo==null){
			throw new BusinessException("该行数据已被其它用户删除!");
		}
		if(!vo.getUpdatets().equals(qvo.getUpdatets())){
			throw new BusinessException("该行数据已发生变化,请取消操作刷新再试!");
		}
	}
	
//	public List<PersonStatisVO> query(QryParamVO paramvo) throws DZFWarpException, IllegalAccessException, Exception {}
	
}
