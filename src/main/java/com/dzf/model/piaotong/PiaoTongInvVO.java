package com.dzf.model.piaotong;

import java.io.Serializable;
import java.util.List;

public class PiaoTongInvVO implements Serializable {

    private String invoiceReqSerialNo;// 发票请求流水号

    private String buyerName;// 购买方名称
    private String buyerTaxpayerNum;// 购 买 方 纳 税 人识别号
    private String buyerAddress;// 购买方地址
    private String buyerTel;// 购买方电话
    private String buyerBankName;// 购买方开户行
    private String buyerBankAccount;// 购 买 方 银 行 账号
    private String takerName;// 收票人名称
    private String takerTel;// 收票人手机号
    private String takerEmail;// 收票人邮箱

    private String taxpayerNum;// 纳税人识别号
    private String sellerAddress;// 销货方地址
    private String sellerTel;// 销货方电话
    private String sellerBankName;// 销货方开户行
    private String sellerBankAccount;// 销 货 方 银 行 账号
    private String sellerEnterpriseName;// 销货方纳税人名称
    private String sellerTaxpayerNum;// 销货方纳税人识别号

    private String casherName;// 收款人名称
    private String reviewerName;//复核人名称
    private String drawerName;// 开票人名称
    
    private String itemName;//主 开 票 项 目 名称
    
    private String remark;//备注
    private String definedData;//自定义数据 
    private String tradeNo;//订单号
    private Integer extensionNum;//分机号
    private String agentInvoiceFlag;//代开标示

    private List<PiaoTongInvBVO> itemList;// 开票项目列表信息

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDefinedData() {
        return definedData;
    }

    public void setDefinedData(String definedData) {
        this.definedData = definedData;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Integer getExtensionNum() {
        return extensionNum;
    }

    public void setExtensionNum(Integer extensionNum) {
        this.extensionNum = extensionNum;
    }

    public String getAgentInvoiceFlag() {
        return agentInvoiceFlag;
    }

    public void setAgentInvoiceFlag(String agentInvoiceFlag) {
        this.agentInvoiceFlag = agentInvoiceFlag;
    }

    public String getTakerTel() {
        return takerTel;
    }

    public void setTakerTel(String takerTel) {
        this.takerTel = takerTel;
    }

    public String getTakerEmail() {
        return takerEmail;
    }

    public void setTakerEmail(String takerEmail) {
        this.takerEmail = takerEmail;
    }

    public String getTaxpayerNum() {
        return taxpayerNum;
    }

    public void setTaxpayerNum(String taxpayerNum) {
        this.taxpayerNum = taxpayerNum;
    }

    public String getCasherName() {
        return casherName;
    }

    public void setCasherName(String casherName) {
        this.casherName = casherName;
    }

    public List<PiaoTongInvBVO> getItemList() {
        return itemList;
    }

    public void setItemList(List<PiaoTongInvBVO> itemList) {
        this.itemList = itemList;
    }

    public String getInvoiceReqSerialNo() {
        return invoiceReqSerialNo;
    }

    public String getSellerEnterpriseName() {
        return sellerEnterpriseName;
    }

    public String getSellerTaxpayerNum() {
        return sellerTaxpayerNum;
    }

    public String getBuyerTaxpayerNum() {
        return buyerTaxpayerNum;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerBankName() {
        return buyerBankName;
    }

    public String getBuyerBankAccount() {
        return buyerBankAccount;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public String getBuyerTel() {
        return buyerTel;
    }

    public String getDrawerName() {
        return drawerName;
    }

    public String getTakerName() {
        return takerName;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public String getSellerTel() {
        return sellerTel;
    }

    public String getSellerBankName() {
        return sellerBankName;
    }

    public String getSellerBankAccount() {
        return sellerBankAccount;
    }

    public void setInvoiceReqSerialNo(String invoiceReqSerialNo) {
        this.invoiceReqSerialNo = invoiceReqSerialNo;
    }

    public void setSellerEnterpriseName(String sellerEnterpriseName) {
        this.sellerEnterpriseName = sellerEnterpriseName;
    }

    public void setSellerTaxpayerNum(String sellerTaxpayerNum) {
        this.sellerTaxpayerNum = sellerTaxpayerNum;
    }

    public void setBuyerTaxpayerNum(String buyerTaxpayerNum) {
        this.buyerTaxpayerNum = buyerTaxpayerNum;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setBuyerBankName(String buyerBankName) {
        this.buyerBankName = buyerBankName;
    }

    public void setBuyerBankAccount(String buyerBankAccount) {
        this.buyerBankAccount = buyerBankAccount;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public void setBuyerTel(String buyerTel) {
        this.buyerTel = buyerTel;
    }

    public void setDrawerName(String drawerName) {
        this.drawerName = drawerName;
    }

    public void setTakerName(String takerName) {
        this.takerName = takerName;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public void setSellerTel(String sellerTel) {
        this.sellerTel = sellerTel;
    }

    public void setSellerBankName(String sellerBankName) {
        this.sellerBankName = sellerBankName;
    }

    public void setSellerBankAccount(String sellerBankAccount) {
        this.sellerBankAccount = sellerBankAccount;
    }
}