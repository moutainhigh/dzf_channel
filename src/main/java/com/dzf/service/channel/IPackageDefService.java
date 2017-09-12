package com.dzf.service.channel;

import com.dzf.model.packagedef.PackageDefVO;
import com.dzf.model.packagedef.PackageQryVO;
import com.dzf.pub.DZFWarpException;

public interface IPackageDefService {

    /**
     * 查询
     * @param qryvo
     * @return
     * @throws DZFWarpException
     */
    public PackageDefVO[] query(PackageQryVO qryvo) throws DZFWarpException;

    /**
     * 新增保存
     * @param pk_corp
     * @param insertData
     * @param delData
     * @param updateData
     * @throws DZFWarpException
     */
    public void save(String pk_corp, PackageDefVO[] insertData, PackageDefVO[] delData, PackageDefVO[] updateData)
            throws DZFWarpException;

    /**
     * 删除
     * @param vos
     * @throws DZFWarpException
     */
    public void delete(PackageDefVO[] vos) throws DZFWarpException;

    /**
     * 发布
     * @param vos
     * @throws DZFWarpException
     */
    public PackageDefVO[] updatePublish(PackageDefVO[] vos) throws DZFWarpException;
    
    /**
     * 下架
     * @param vos
     * @throws DZFWarpException
     */
    public PackageDefVO[] updateOff(PackageDefVO[] vos) throws DZFWarpException;
    
}
