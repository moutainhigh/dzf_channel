package com.dzf.service.channel.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.model.channel.PackageDefVO;
import com.dzf.model.packagedef.PackageQryVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.model.sys.sys_set.BusiTypeVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.IPackageDefService;

@Service("packageDefImpl")
public class PackageDefServiceImpl implements IPackageDefService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    
    @Override
    public PackageDefVO[] query(PackageQryVO qryvo) throws DZFWarpException {
        StringBuffer str = new StringBuffer();
        SQLParameter params = new SQLParameter();
        str.append(" nvl(dr,0) = 0 and pk_corp = '000001'");
        if(qryvo.getDbegindate() != null){
            str.append(" and doperatedate >= ?");
            params.addParam(qryvo.getDbegindate());
        }
        if(qryvo.getDenddate() != null){
            str.append(" and doperatedate <= ?");
            params.addParam(qryvo.getDenddate());
        }
        if(!StringUtil.isEmpty(qryvo.getVtaxpayertype())){
            str.append(" and vtaxpayertype = ?");
            params.addParam(qryvo.getVtaxpayertype());
        }
        if(qryvo.getIcashcycle() != -1){
            str.append(" and icashcycle = ?");
            params.addParam(qryvo.getIcashcycle());
        }
        if(qryvo.getIcontcycle() !=-1){
            str.append(" and icontcycle = ?");
            params.addParam(qryvo.getIcontcycle());
        }
        if(qryvo.getIcompanytype() !=-1){
            str.append(" and icompanytype = ?");
            params.addParam(qryvo.getIcompanytype());
        }
        if(qryvo.getPtype()==-1){
        	return new PackageDefVO[0];
        }else if(qryvo.getPtype()==1){
            str.append(" and nvl(ispromotion,'N') = ?");
            params.addParam("Y");
        }else if(qryvo.getPtype()==2){
            str.append(" and nvl(ispromotion,'N') = ?");
            params.addParam("N");
        }
        if(qryvo.getVstatus()!= -1 && qryvo.getVstatus()==3){
            str.append(" and vstatus = ?");
            params.addParam(qryvo.getVstatus());
        }else if(qryvo.getVstatus()!= -1 && qryvo.getVstatus()==4){
            str.append(" and vstatus != 3");
        }
        if(qryvo.getItype()==-1){
        	return new PackageDefVO[0];
        }else if(qryvo.getItype()==1){
            str.append(" and nvl(itype,0) = ?");
            params.addParam(0);
        }else if(qryvo.getItype()==2){
            str.append(" and nvl(itype,0) = ?");
            params.addParam(1);
        }
        str.append(" order by vstatus asc,sortnum asc");
        PackageDefVO[] vos = (PackageDefVO[]) singleObjectBO.queryByCondition(PackageDefVO.class, str.toString(), params);
        if(vos != null && vos.length > 0){
            UserVO uvo = null;
            for(PackageDefVO vo : vos){
                uvo = UserCache.getInstance().get(vo.getCoperatorid(), null);
                if(uvo != null){
                    vo.setCoperatorname(uvo.getUser_name());
                }
                if(vo.getItype()==null){
                	vo.setItype(0);
                }
            }
        }
        return vos;
    }
    
    private void checkIsSimilar(PackageDefVO checkVO) throws DZFWarpException {
    	StringBuffer sql = new StringBuffer();
    	SQLParameter spm = new SQLParameter();
		sql.append("select 1 from cn_packagedef where nvl(dr, 0) = 0 ") ; 
		if(!StringUtil.isEmpty(checkVO.getPk_packagedef())){
			sql.append("and pk_packagedef != ? ");
			spm.addParam(checkVO.getPk_packagedef());
		}
		sql.append("and vtaxpayertype = ? ");
		spm.addParam(checkVO.getVtaxpayertype());
		sql.append("and icompanytype = ? ");
		spm.addParam(checkVO.getIcompanytype());
		sql.append("and itype = ? ");
		spm.addParam(checkVO.getItype());
		sql.append("and nmonthmny = ? ");
		spm.addParam(checkVO.getNmonthmny());
		sql.append("and icashcycle = ? ");
		spm.addParam(checkVO.getIcashcycle());
		sql.append("and icontcycle = ? ");
		spm.addParam(checkVO.getIcontcycle());
    	boolean exists = singleObjectBO.isExists(checkVO.getPk_corp(), sql.toString(), spm);
    	if(exists){
    		throw new BusinessException("服务套餐已存在");
    	}
    }
    
    @Override
    public void saveNew(PackageDefVO insertData) throws DZFWarpException {
    	checkIsSimilar(insertData);
        BusiTypeVO bvo = queryBusitype(insertData.getVbusitypecode());
        if(bvo != null){
        	insertData.setPk_busitype(bvo.getPk_busitype());
        	insertData.setPk_product(bvo.getPk_product());
        	insertData.setVbusitypename(bvo.getVbusitypename());
        }else{
            throw new BusinessException("代理记账业务类型为空，保存失败");
        }
        if(insertData.getIspromotion() != null && insertData.getIspromotion().booleanValue()){
            if(insertData.getIpublishnum() == null || insertData.getIpublishnum() <=0 ){
                throw new BusinessException("发布个数不可以小于1，请重新填写");
            }
        }
        singleObjectBO.insertVO(insertData.getPk_corp(), insertData);
    }
    
    @Override
    public void saveModify(PackageDefVO updateData) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(updateData.getTableName(), updateData.getPk_packagedef(),uuid, 120);
			PackageDefVO oldvo = checkData(updateData);
			checkIsSimilar(updateData);
	        if(updateData.getIspromotion() != null && updateData.getIspromotion().booleanValue()){
	            if(updateData.getIpublishnum() == null || updateData.getIpublishnum() <=0 ){
	                throw new BusinessException("发布个数不可以小于1，请重新填写");
	            }
	            if(oldvo.getVstatus() == 3){
		            if(updateData.getIpublishnum().compareTo(oldvo.getIpublishnum())<0){
		            	 throw new BusinessException("修改已下架的套餐，发布个数不可以小于之前的个数");
		            }
	            }
	        }
	        if(oldvo.getVstatus() == 1){
		        singleObjectBO.update(updateData);
	        }else if(oldvo.getVstatus() == 2){
	        	singleObjectBO.update(updateData, new String[]{"itype"});
	        }else{
	        	singleObjectBO.update(updateData, new String[]{"vtaxpayertype","icompanytype",
	        			"itype","nmonthmny","icashcycle","icontcycle","ipublishnum"});
	        }
		}catch (Exception e) {
            if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            else
                throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(updateData.getTableName(), updateData.getPk_packagedef(),uuid);
		}
    }
    
	/**
	 * 时间戳校验
	 */
	private PackageDefVO checkData(PackageDefVO vo) throws DZFWarpException {
		PackageDefVO oldvo = (PackageDefVO) singleObjectBO.queryByPrimaryKey(PackageDefVO.class, vo.getPk_packagedef());
		if(oldvo == null){
			throw new BusinessException("非法操作");
		}
		if(oldvo.getUpdatets().compareTo(vo.getUpdatets()) != 0){
			throw new BusinessException("数据已发生变化，请取消本次操作后刷新");
		}
		return oldvo;
	}

    private BusiTypeVO queryBusitype(String vbusitypecode){
        String condition = "nvl(dr,0) = 0 and pk_corp = '000001' and vbusitypecode = ? ";
        SQLParameter params = new SQLParameter();
        params.addParam(vbusitypecode);
        BusiTypeVO[] vos = (BusiTypeVO[]) singleObjectBO.queryByCondition(BusiTypeVO.class, condition, params);
        if(vos != null && vos.length > 0){
            return vos[0];
        }
        return null;
    }

    @Override
    public void delete(PackageDefVO[] vos) throws DZFWarpException {
        for(PackageDefVO vo : vos){
            if(vo.getVstatus() == 2){
                throw new BusinessException("已发布的套餐不允许删除");
            }
        }
        singleObjectBO.deleteVOArray(vos);
    }

    @Override
    public PackageDefVO[] updatePublish(PackageDefVO[] vos) throws DZFWarpException {
        for(PackageDefVO vo : vos){
            if(vo.getVstatus() == 2){
                throw new BusinessException("已发布的套餐不允许发布");
            }
            if(vo.getIcontcycle() == null){
                throw new BusinessException("合同周期不能为空");
            }
            vo.setVstatus(2);
            vo.setDpublishdate(new DZFDate());
        }
        singleObjectBO.updateAry(vos, new String[]{"vstatus","dpublishdate"});
        return vos;
    }

    @Override
    public PackageDefVO[] updateOff(PackageDefVO[] vos) throws DZFWarpException {
        for(PackageDefVO vo : vos){
            if(vo.getVstatus() == 1){
                throw new BusinessException("未发布的套餐不允许下架");
            }else if(vo.getVstatus() == 3){
                throw new BusinessException("已下架的套餐不允许下架");
            }
            vo.setVstatus(3);
            vo.setDoffdate(new DZFDate());
        }
        singleObjectBO.updateAry(vos, new String[]{"vstatus","doffdate"});
        return vos;
    }

	@Override
	public void updateRows(PackageDefVO[] vos) throws DZFWarpException {
		for (int i = 0; i < vos.length; i++) {
			vos[i].setSortnum(i);
		}
		singleObjectBO.updateAry(vos, new String[]{"sortnum"});		
	}

}
