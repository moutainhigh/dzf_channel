package com.dzf.service.channel;

import com.dzf.model.channel.PackageDefVO;
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
     * @param insertData
     * @throws DZFWarpException
     */
    public void saveNew(PackageDefVO insertData) throws DZFWarpException;
    
    /**
     * 修改保存
     * @param updateData
     * @throws DZFWarpException
     */
    public void saveModify(PackageDefVO updateData) throws DZFWarpException;

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
    
    /**
     * 排序，更新
     * @param vos
     * @throws DZFWarpException
     */
    public void updateRows(PackageDefVO[] vos) throws DZFWarpException;
    
}
