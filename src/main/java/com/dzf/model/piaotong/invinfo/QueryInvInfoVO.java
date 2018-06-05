package com.dzf.model.piaotong.invinfo;

import java.io.Serializable;

/**
 * 查询库存信息--请求参数业务报文
 * 
 * @author gejw
 * @time 2018年6月5日 上午11:10:10
 *
 */
public class QueryInvInfoVO implements Serializable {

    private String taxpayerNum;// 纳税人识别号
    private String enterpriseName;// 销货方地址

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

}