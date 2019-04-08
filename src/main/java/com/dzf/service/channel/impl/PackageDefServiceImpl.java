package com.dzf.service.channel.impl;

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
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
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
        if(qryvo.getVstatus()!= -1 && qryvo.getVstatus()==3){
            str.append(" and vstatus = ?");
            params.addParam(qryvo.getVstatus());
        }else if(qryvo.getVstatus()!= -1 && qryvo.getVstatus()==4){
            str.append(" and vstatus != 3");
        }
        if(qryvo.getItype()==-1){
        	return new PackageDefVO[]{};
        }else if(qryvo.getItype()==1){
            str.append(" and nvl(itype,0) = ?");
            params.addParam(0);
        }else if(qryvo.getItype()==2){
            str.append(" and nvl(itype,0) = ?");
            params.addParam(1);
        }
//        str.append(" order by vstatus,dpublishdate desc");
        str.append(" order by sortnum asc");
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
    
    @Override
    public void save(String pk_corp, PackageDefVO[] insertData, PackageDefVO[] delData, PackageDefVO[] updateData)
            throws DZFWarpException {
        if (insertData != null && insertData.length > 0) {
            for (PackageDefVO insert : insertData) {
                BusiTypeVO bvo = queryBusitype(insert.getVbusitypecode());
                insert.setPk_corp(pk_corp);
                if(bvo != null){
                    insert.setPk_busitype(bvo.getPk_busitype());
                    insert.setPk_product(bvo.getPk_product());
                    insert.setVbusitypename(bvo.getVbusitypename());
                }else{
                    throw new BusinessException("代理记账业务类型为空，保存失败");
                }
                if(insert.getIspromotion() != null && insert.getIspromotion().booleanValue()){
                    if(insert.getIpublishnum() == null || insert.getIpublishnum() <=0 ){
                        throw new BusinessException("发布个数不可以小于1，请重新填写");
                    }
                }
            }
            singleObjectBO.insertVOArr(pk_corp, insertData);
        }
        if (updateData != null && updateData.length > 0){
            for (PackageDefVO update : updateData) {
                if(update.getIspromotion() != null && update.getIspromotion().booleanValue()){
                    if(update.getIpublishnum() == null || update.getIpublishnum() <=0 ){
                        throw new BusinessException("发布个数不可以小于1，请重新填写");
                    }
                }
            }
            singleObjectBO.updateAry(updateData);
        }
        if(delData != null && delData.length > 0){
            singleObjectBO.deleteVOArray(delData);
        }
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
            }else if(vo.getVstatus() == 3){
                throw new BusinessException("已下架的套餐不允许删除");
            }
        }
        singleObjectBO.deleteVOArray(vos);
    }

    @Override
    public PackageDefVO[] updatePublish(PackageDefVO[] vos) throws DZFWarpException {
        for(PackageDefVO vo : vos){
            if(vo.getVstatus() == 2){
                throw new BusinessException("已发布的套餐不允许发布");
            }else if(vo.getVstatus() == 3){
                throw new BusinessException("已下架的套餐不允许发布");
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
