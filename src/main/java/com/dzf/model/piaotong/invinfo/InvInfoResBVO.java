package com.dzf.model.piaotong.invinfo;

import java.io.Serializable;

import com.dzf.pub.SuperVO;

/**
 * 查询库存信息--响应报文--分机信息
 * @author gejw
 * @time 2018年6月5日 上午11:33:22
 *
 */
public class InvInfoResBVO extends SuperVO implements Serializable {
    
    private String extensionNum;// 分机号

    private String invoiceKindCode;// 发票种类

    private String invoiceCode;// 发票代码
    
    private String invoiceStartNo;// 发票起始号码

    private String invoiceEndNo;// 发票终止号码

    private String invoiceSurplusNum;// 剩余份数

    private String invoiceBuyTime;// 购买日期

    public String getExtensionNum() {
        return extensionNum;
    }

    public void setExtensionNum(String extensionNum) {
        this.extensionNum = extensionNum;
    }

    public String getInvoiceKindCode() {
        return invoiceKindCode;
    }

    public void setInvoiceKindCode(String invoiceKindCode) {
        this.invoiceKindCode = invoiceKindCode;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceStartNo() {
        return invoiceStartNo;
    }

    public void setInvoiceStartNo(String invoiceStartNo) {
        this.invoiceStartNo = invoiceStartNo;
    }

    public String getInvoiceEndNo() {
        return invoiceEndNo;
    }

    public void setInvoiceEndNo(String invoiceEndNo) {
        this.invoiceEndNo = invoiceEndNo;
    }

    public String getInvoiceSurplusNum() {
        return invoiceSurplusNum;
    }

    public void setInvoiceSurplusNum(String invoiceSurplusNum) {
        this.invoiceSurplusNum = invoiceSurplusNum;
    }

    public String getInvoiceBuyTime() {
        return invoiceBuyTime;
    }

    public void setInvoiceBuyTime(String invoiceBuyTime) {
        this.invoiceBuyTime = invoiceBuyTime;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }

}
