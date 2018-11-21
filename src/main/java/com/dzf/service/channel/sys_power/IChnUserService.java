package com.dzf.service.channel.sys_power;

import java.util.List;

import com.dzf.model.pub.ComboBoxVO;
import com.dzf.pub.DZFWarpException;

public interface IChnUserService {

    /**
     * 用户下拉
     * @author gejw
     * @time 上午10:12:01
     * @param loginCorpID
     * @param qryVO
     * @return
     * @throws DZFWarpException
     */
    public List<ComboBoxVO> queryCombobox(String loginCorpID) throws DZFWarpException;
}
