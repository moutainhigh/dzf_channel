package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.PackageDefVO;
import com.dzf.model.packagedef.PackageDefBVO;
import com.dzf.model.packagedef.PackageDefCVO;
import com.dzf.model.packagedef.PackageQryVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.model.sys.sys_set.BusiTypeVO;
import com.dzf.model.sys.sys_set.YntArea;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.IPackageDefService;

@Service("packageDefImpl")
public class PackageDefServiceImpl implements IPackageDefService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    
    @Override
    public List<PackageDefVO> query(PackageQryVO qryvo) throws DZFWarpException {
    	List<PackageDefVO> retlist = new ArrayList<PackageDefVO>();
        StringBuffer str = new StringBuffer();
        SQLParameter params = new SQLParameter();
        str.append(" select p.pk_packagedef, ");
        str.append("        p.pk_product, ");
        str.append("        p.pk_busitype, ");
        str.append("        p.vbusitypename, ");
        str.append("        p.vtaxpayertype, ");
        str.append("        p.nmonthmny, ");
        str.append("        p.icashcycle, ");
        str.append("        p.icontcycle, ");
        str.append("        p.ipublishnum, ");
        str.append("        p.iusenum, ");
        str.append("        p.dpublishdate, ");
        str.append("        p.doffdate, ");
        str.append("        p.vstatus, ");
        str.append("        p.ispromotion, ");
        str.append("        p.vmemo, ");
        str.append("        p.coperatorid, ");
        str.append("        p.doperatedate, ");
        str.append("        p.vbusitypecode, ");
        str.append("        p.icompanytype, ");
        str.append("        p.itype, ");
        str.append("        p.sortnum, ");
        str.append("        p.updatets, ");
        str.append("        u.user_name coperatorname, ");
        str.append("        wm_concat(b.vcity) cityids, ");
        str.append("        wm_concat(c.pk_corp) corpids ");
        str.append("   from cn_packagedef p ");
        str.append("   left join cn_packagedef_b b on p.pk_packagedef = b.pk_packagedef ");
        str.append("   left join cn_packagedef_c c on p.pk_packagedef = c.pk_packagedef ");
        str.append("   left join sm_user u ON p.coperatorid = u.cuserid ") ; 
        str.append(" where nvl(p.dr,0) = 0 and nvl(b.dr,0) = 0 and nvl(c.dr,0) = 0 and p.pk_corp =? ");
        params.addParam("000001");
        if(qryvo.getDbegindate() != null){
            str.append(" and p.doperatedate >= ?");
            params.addParam(qryvo.getDbegindate());
        }
        if(qryvo.getDenddate() != null){
            str.append(" and p.doperatedate <= ?");
            params.addParam(qryvo.getDenddate());
        }
        if(!StringUtil.isEmpty(qryvo.getVtaxpayertype())){
            str.append(" and p.vtaxpayertype = ?");
            params.addParam(qryvo.getVtaxpayertype());
        }
        if(qryvo.getIcashcycle() != -1){
            str.append(" and p.icashcycle = ?");
            params.addParam(qryvo.getIcashcycle());
        }
        if(qryvo.getIcontcycle() !=-1){
            str.append(" and p.icontcycle = ?");
            params.addParam(qryvo.getIcontcycle());
        }
        if(qryvo.getIcompanytype() !=-1){
            str.append(" and nvl(p.icompanytype,99) = ?");
            params.addParam(qryvo.getIcompanytype());
        }
        if(qryvo.getPtype()==-1){
        	return retlist;
        }else if(qryvo.getPtype()==1){
            str.append(" and nvl(p.ispromotion,'N') = ?");
            params.addParam("Y");
        }else if(qryvo.getPtype()==2){
            str.append(" and nvl(p.ispromotion,'N') = ?");
            params.addParam("N");
        }
        if(qryvo.getVstatus()!= -1 && qryvo.getVstatus()==3){
            str.append(" and p.vstatus = ?");
            params.addParam(qryvo.getVstatus());
        }else if(qryvo.getVstatus()!= -1 && qryvo.getVstatus()==4){
            str.append(" and p.vstatus != 3");
        }
        if(qryvo.getItype()==-1){
        	return retlist;
        }else if(qryvo.getItype()==1){
            str.append(" and nvl(p.itype,0) = ?");
            params.addParam(0);
        }else if(qryvo.getItype()==2){
            str.append(" and nvl(p.itype,0) = ?");
            params.addParam(1);
        }
        str.append("  group by p.pk_packagedef, ");
        str.append("           p.pk_product, ");
        str.append("           p.pk_busitype, ");
        str.append("           p.vbusitypename, ");
        str.append("           p.vtaxpayertype, ");
        str.append("           p.nmonthmny, ");
        str.append("           p.icashcycle, ");
        str.append("           p.icontcycle, ");
        str.append("           p.ipublishnum, ");
        str.append("           p.iusenum, ");
        str.append("           p.dpublishdate, ");
        str.append("           p.doffdate, ");
        str.append("           p.vstatus, ");
        str.append("           p.ispromotion, ");
        str.append("           p.vmemo, ");
        str.append("           p.coperatorid, ");
        str.append("           p.doperatedate, ");
        str.append("           p.vbusitypecode, ");
        str.append("           p.icompanytype, ");
        str.append("           p.itype, ");
        str.append("           p.sortnum, ");
        str.append("           p.updatets, ");
        str.append("           u.user_name ");
        
        str.append(" order by p.vstatus asc,p.sortnum asc");
        retlist = (List<PackageDefVO>)singleObjectBO.executeQuery(str.toString(), params, new BeanListProcessor(PackageDefVO.class));
        if(retlist != null && retlist.size() > 0){
        	setRetList(retlist);
        }
        return retlist;
    }


	private void setRetList(List<PackageDefVO> retlist) throws DZFWarpException {
		HashMap<String, String> qryCityMap = qryCityMap();
		
		StringBuffer nameStr;
		StringBuffer idStr;
		String names;
		String ids;
		
		UserVO uvo;
		CorpVO cvo;
		String corpids;
		String corpIds[];
		String cityids;
		String cityIds[];
		
		HashSet<String> set;
		
		for (PackageDefVO vo : retlist) {
			vo.setCoperatorname(CodeUtils1.deCode(vo.getCoperatorname()));
			if (vo.getItype() == null) {
				vo.setItype(0);
			}
			corpids = vo.getCorpids();
			nameStr = new StringBuffer();
			idStr = new StringBuffer();
			set = new HashSet<String>();
			if (!StringUtil.isEmpty(corpids)) {
				corpIds = corpids.split(",");
				for (String string : corpIds) {
					if (set.add(string)) {
						idStr.append(string).append(",");
						cvo = CorpCache.getInstance().get(null, string);
						if (cvo != null) {
							nameStr.append(cvo.getUnitname()).append(",");
						}
					}
				}
				vo.setCorpids(corpids);
				names = nameStr.toString();
				ids = idStr.toString();
				vo.setCorpids(ids.substring(0, ids.length() - 1));
				if (!StringUtil.isEmpty(names)) {
					vo.setCorpnames(names.substring(0, names.length() - 1));
				}
			}
			
			cityids = vo.getCityids();
			nameStr = new StringBuffer();
			idStr = new StringBuffer();
			set = new HashSet<String>();
			if (!StringUtil.isEmpty(cityids)) {
				cityIds = cityids.split(",");
				for (String string : cityIds) {
					if (set.add(string)) {
						idStr.append(string).append(",");
						nameStr.append(qryCityMap.get(string)).append(",");
					}
				}
				names = nameStr.toString();
				ids = idStr.toString();
				vo.setCityids(ids.substring(0, ids.length() - 1));
				if (!StringUtil.isEmpty(names)) {
					vo.setCitynames(names.substring(0, names.length() - 1));
				}
			}
		}
	}
    
	private HashMap<String,String> qryCityMap() throws DZFWarpException {
		HashMap<String,String> map = new HashMap<>();
        StringBuffer sql = new StringBuffer();
        sql.append(" select c.region_id id,c.region_name name,p.region_name code");
        sql.append(" from ynt_area c left join ynt_area p  on c.parenter_id= p.region_id ");
        sql.append(" where nvl(c.dr,0) = 0 and  nvl(p.dr,0) = 0 ");
        sql.append("  and p.region_id>1 and p.region_id<33");
        sql.append("  and c.region_id>=33 and c.region_id<=377 ");
        List<ComboBoxVO> list = (List<ComboBoxVO>)singleObjectBO.executeQuery(sql.toString(), null, new BeanListProcessor(ComboBoxVO.class));
        String name;
        for (ComboBoxVO comboBoxVO : list) {
        	name = comboBoxVO.getCode()+"("+comboBoxVO.getName()+")";
        	map.put(comboBoxVO.getId(),name);
		}
		return map;
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
        PackageDefVO insertVO =(PackageDefVO) singleObjectBO.insertVO(insertData.getPk_corp(), insertData);
        saveChildren(insertVO);
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
	        }else if(oldvo.getVstatus() ==3){
	        	singleObjectBO.update(updateData, new String[]{"vtaxpayertype","icompanytype",
	        			"itype","nmonthmny","icashcycle","icontcycle","ipublishnum"});
	        }
	        if(oldvo.getVstatus() == 2){
	        	singleObjectBO.update(updateData, new String[]{"itype"});
	        }else{
	        	deleteChildren(updateData.getPk_packagedef());
	        	saveChildren(updateData);
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
     * 删除套餐子表信息
     * @param pk_packagedef
     * @throws DZFWarpException
     */
    private void deleteChildren(String pk_packagedef) throws DZFWarpException {
    	SQLParameter spm = new SQLParameter();
    	spm.addParam(pk_packagedef);
    	
    	String sql = "delete from cn_packagedef_b where pk_packagedef = ? ";
    	singleObjectBO.executeUpdate(sql, spm);
    	
    	sql = "delete from cn_packagedef_c where pk_packagedef = ? ";
    	singleObjectBO.executeUpdate(sql, spm);
    }
    
    /**
     * 保存套餐子表信息 
     * @param vo
     * @throws DZFWarpException
     */
    private void saveChildren(PackageDefVO vo) throws DZFWarpException {
        String id = vo.getPk_packagedef();
        String icitys = vo.getCityids();
        String corpids = vo.getCorpids();
        if(!StringUtil.isEmpty(icitys)){
        	String[] citys = icitys.split(",");
        	PackageDefBVO bvo = new PackageDefBVO();
        	List<PackageDefBVO> bvos = new ArrayList<>();
        	for (String string : citys) {
        		bvo = new PackageDefBVO();
        		bvo.setPk_packagedef(id);
        		bvo.setVcity(Integer.valueOf(string));
        		bvos.add(bvo);
			}
        	singleObjectBO.insertVOArr("000001", bvos.toArray(new PackageDefBVO[bvos.size()]));
        }
        if(!StringUtil.isEmpty(corpids)){
        	String[] corps = corpids.split(",");
        	PackageDefCVO cvo = new PackageDefCVO();
        	List<PackageDefCVO> cvos = new ArrayList<>();
        	for (String string : corps) {
        		cvo = new PackageDefCVO();
        		cvo.setPk_packagedef(id);
        		cvo.setPk_corp(string);
        		cvos.add(cvo);
			}
        	singleObjectBO.insertVOArr("000001", cvos.toArray(new PackageDefCVO[cvos.size()]));
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
	
	@Override
	public HashMap<String,List<YntArea>> queryCityMap() throws DZFWarpException {
		HashMap<String,List<YntArea> > map = new HashMap<>();
		List<YntArea> setList = new ArrayList<>();
        StringBuffer sql = new StringBuffer();
        sql.append(" select region_id,region_name,parenter_id ");
        sql.append(" from ynt_area where nvl(dr,0) = 0 ");
        sql.append(" and region_id>=33 and region_id<=377 ");
        List<YntArea> list = (List<YntArea>)singleObjectBO.executeQuery(sql.toString(), null, new BeanListProcessor(YntArea.class));
        for (YntArea yntArea : list) {
			if(!map.containsKey(yntArea.getParenter_id())){
				setList = new ArrayList<>();
				setList.add(yntArea);
				map.put(yntArea.getParenter_id(), setList);
			}else{
				setList = map.get(yntArea.getParenter_id());
				setList.add(yntArea);
			}
		}
		return map;
	}

}
