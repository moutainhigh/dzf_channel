package com.dzf.model.piaotong.invinfo;

import java.io.Serializable;

/**
 * 查询库存信息--响应报文--分机信息
 * @author gejw
 * @time 2018年6月5日 上午11:33:22
 *
 */
public class InvInfoResHVO implements Serializable {
    
    private String taxpayerNum; //纳税人识别号

    private String enterpriseName;//企业名称 

    private InvInfoResBVO[] extensionInfos;

    public String getTaxpayerNum() {
        return taxpayerNum;
    }

    public void setTaxpayerNum(String taxpayerNum) {
        this.taxpayerNum = taxpayerNum;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public InvInfoResBVO[] getExtensionInfos() {
        return extensionInfos;
    }

    public void setExtensionInfos(InvInfoResBVO[] extensionInfos) {
        this.extensionInfos = extensionInfos;
    }
   
}
