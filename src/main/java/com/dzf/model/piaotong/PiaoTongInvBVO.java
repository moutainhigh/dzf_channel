package com.dzf.model.piaotong;

import java.io.Serializable;

public class PiaoTongInvBVO implements Serializable {

    private String goodsSerialNo;// 商品行序号
    private String goodsName;// 货物名称
    private String taxClassificationCode;// 对 应 税 收 分 类编码
    private String specificationModel;// 规格型号
    private String meteringUnit;// 单位
    private String quantity;// 数量
    private String includeTaxFlag;// 含税标志
    private String unitPrice;// 单价
    private String invoiceAmount;// 金额
    private String taxRateValue;// 税率
    private String taxRateAmount;// 税额
    private String discountAmount;// 折扣金额
    private String discountTaxRateAmount;// 折扣税额
    private String deductionAmount;// 差 额 开 票 抵 扣金额
    private String preferentialPolicyFlag;// 优惠政策标识
    private String vatSpecialManage;// 增 值 税 特 殊 管理
    private String zeroTaxFlag;// 零税率标识

    public String getTaxClassificationCode() {
        return taxClassificationCode;
    }

    public void setTaxClassificationCode(String taxClassificationCode) {
        this.taxClassificationCode = taxClassificationCode;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountTaxRateAmount() {
        return discountTaxRateAmount;
    }

    public void setDiscountTaxRateAmount(String discountTaxRateAmount) {
        this.discountTaxRateAmount = discountTaxRateAmount;
    }

    public String getPreferentialPolicyFlag() {
        return preferentialPolicyFlag;
    }

    public void setPreferentialPolicyFlag(String preferentialPolicyFlag) {
        this.preferentialPolicyFlag = preferentialPolicyFlag;
    }

    public String getVatSpecialManage() {
        return vatSpecialManage;
    }

    public void setVatSpecialManage(String vatSpecialManage) {
        this.vatSpecialManage = vatSpecialManage;
    }

    public String getZeroTaxFlag() {
        return zeroTaxFlag;
    }

    public void setZeroTaxFlag(String zeroTaxFlag) {
        this.zeroTaxFlag = zeroTaxFlag;
    }

    public String getGoodsSerialNo() {
        return goodsSerialNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getMeteringUnit() {
        return meteringUnit;
    }

    public String getSpecificationModel() {
        return specificationModel;
    }

    public String getIncludeTaxFlag() {
        return includeTaxFlag;
    }

    public String getDeductionAmount() {
        return deductionAmount;
    }

    public String getTaxRateAmount() {
        return taxRateAmount;
    }

    public String getTaxRateValue() {
        return taxRateValue;
    }

    public void setGoodsSerialNo(String goodsSerialNo) {
        this.goodsSerialNo = goodsSerialNo;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setMeteringUnit(String meteringUnit) {
        this.meteringUnit = meteringUnit;
    }

    public void setSpecificationModel(String specificationModel) {
        this.specificationModel = specificationModel;
    }

    public void setIncludeTaxFlag(String includeTaxFlag) {
        this.includeTaxFlag = includeTaxFlag;
    }

    public void setDeductionAmount(String deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public void setTaxRateAmount(String taxRateAmount) {
        this.taxRateAmount = taxRateAmount;
    }

    public void setTaxRateValue(String taxRateValue) {
        this.taxRateValue = taxRateValue;
    }

}