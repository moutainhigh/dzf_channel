package com.dzf.model.piaotong.invinfo;

import java.io.Serializable;

/**
 * 查询库存信息--响应报文
 * 
 * @author gejw
 * @time 2018年6月5日 上午11:22:58
 *
 */
public class InvInfoResVO implements Serializable {

    private String code; // 响应状态 业务返回码

    private String msg;// 响应消息 业务返回码描述

    private String sign;// 签名串

    private String serialNo;// 交易请求流水号

    private String content;// 业务报文内容 3DES加密

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
