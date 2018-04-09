package com.dzf.model.piaotong;

import java.io.Serializable;

public class PiaoTongResBVO implements Serializable {

	private String invoiceReqSerialNo; //发票请求流水号

	private String qrCodePath;//二维码 url
	
	private String qrCode;//二维码图片Base64
	
	private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInvoiceReqSerialNo() {
        return invoiceReqSerialNo;
    }

    public void setInvoiceReqSerialNo(String invoiceReqSerialNo) {
        this.invoiceReqSerialNo = invoiceReqSerialNo;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }


	
}
