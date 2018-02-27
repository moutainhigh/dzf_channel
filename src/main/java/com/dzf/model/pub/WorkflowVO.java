package com.dzf.model.pub;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 代办事项VO
 * 
 * @author dzf
 * 
 */
public class WorkflowVO extends SuperVO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final String TABLE_NAME = "ynt_workflow";
    @FieldAlias("workflowid")
    private String pk_workflow;// 主键
    @FieldAlias("corpid")
    private String pk_corp;// 所属分部
    @FieldAlias("corpkid")
    private String pk_corpk;// 公司名称
    @FieldAlias("billid")
    private String pk_bill;// 业务主键
    @FieldAlias("code")
    private String vcode;// 编码
    @FieldAlias("isdeal")
    private DZFBoolean isdeal;// 是否处理
    @FieldAlias("senderman")
    private String senderman;// 发送人PK
    @FieldAlias("senddate")
    private DZFDate dsenddate;// 发送日期
    @FieldAlias("dealman")
    private String dealman;// 处理人PK
    @FieldAlias("dealdate")
    private DZFDate ddealdate;// 处理日期
    @FieldAlias("note")
    private String vnote;// 备注
    @FieldAlias("operatorid")
    private String coperatorid;// 创建人
    @FieldAlias("operatedate")
    private DZFDate doperatedate;// 创建日期
    @FieldAlias("dr")
    private Integer dr;// 删除标记
    @FieldAlias("ts")
    private DZFDateTime ts;// 时间戳
    @FieldAlias("contentid")
    private String pk_content;// 流程节点主键  --审批流程子表主键
    @FieldAlias("contentname")
    private String vcontentname;// 当前节点名称
    @FieldAlias("ncontentid")
    private String pk_ncontent;// 上一节点PK
    @FieldAlias("ncontentname")
    private String vncontentname;// 上一节点名称
    @FieldAlias("iprocess")
    private Integer iprocess;// 流程顺序号
    @FieldAlias("procesetid")
    private String pk_proceset;// 流程主键--审批流程主键

    @FieldAlias("dealtime")
    private DZFDateTime ddealtime;// 处理时间

    private String vbilltype;// 单据类型

    @FieldAlias("sendname")
    private String sendmanname;// 提交人姓名

    @FieldAlias("dealname")
    private String dealmanname;// 处理人姓名

    @FieldAlias("pronote")
    private String vapprovenote;// 审批批语

    @FieldAlias("vsnote")
    private String vstatusnote;// 审批状态
    
    @FieldAlias("sendtime")
    private DZFDateTime dsendtime;// 发送时间
    
    private String actiontype;
    
    @FieldAlias("vtent")
    private String vcontent;

    public String getVcontent() {
        return vcontent;
    }

    public void setVcontent(String vcontent) {
        this.vcontent = vcontent;
    }

    public String getActiontype() {
        return actiontype;
    }

    public void setActiontype(String actiontype) {
        this.actiontype = actiontype;
    }

    public DZFDateTime getDsendtime() {
        return dsendtime;
    }

    public void setDsendtime(DZFDateTime dsendtime) {
        this.dsendtime = dsendtime;
    }

    public String getVbilltype() {
        return vbilltype;
    }

    public void setVbilltype(String vbilltype) {
        this.vbilltype = vbilltype;
    }

    public String getSendmanname() {
        return sendmanname;
    }

    public void setSendmanname(String sendmanname) {
        this.sendmanname = sendmanname;
    }

    public String getDealmanname() {
        return dealmanname;
    }

    public void setDealmanname(String dealmanname) {
        this.dealmanname = dealmanname;
    }

    public String getVapprovenote() {
        return vapprovenote;
    }

    public void setVapprovenote(String vapprovenote) {
        this.vapprovenote = vapprovenote;
    }

    public String getVstatusnote() {
        return vstatusnote;
    }

    public void setVstatusnote(String vstatusnote) {
        this.vstatusnote = vstatusnote;
    }

    public DZFDateTime getDdealtime() {
        return ddealtime;
    }

    public void setDdealtime(DZFDateTime ddealtime) {
        this.ddealtime = ddealtime;
    }

    public String getPk_proceset() {
        return pk_proceset;
    }

    public void setPk_proceset(String pk_proceset) {
        this.pk_proceset = pk_proceset;
    }

    public Integer getIprocess() {
        return iprocess;
    }

    public void setIprocess(Integer iprocess) {
        this.iprocess = iprocess;
    }

    public String getVcontentname() {
        return vcontentname;
    }

    public void setVcontentname(String vcontentname) {
        this.vcontentname = vcontentname;
    }

    public String getPk_ncontent() {
        return pk_ncontent;
    }

    public void setPk_ncontent(String pk_ncontent) {
        this.pk_ncontent = pk_ncontent;
    }

    public String getVncontentname() {
        return vncontentname;
    }

    public void setVncontentname(String vncontentname) {
        this.vncontentname = vncontentname;
    }

    public String getPk_content() {
        return pk_content;
    }

    public void setPk_content(String pk_content) {
        this.pk_content = pk_content;
    }

    public String getPk_workflow() {
        return pk_workflow;
    }

    public void setPk_workflow(String pk_workflow) {
        this.pk_workflow = pk_workflow;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_corpk() {
        return pk_corpk;
    }

    public void setPk_corpk(String pk_corpk) {
        this.pk_corpk = pk_corpk;
    }

    public String getPk_bill() {
        return pk_bill;
    }

    public void setPk_bill(String pk_bill) {
        this.pk_bill = pk_bill;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public DZFBoolean getIsdeal() {
        return isdeal;
    }

    public void setIsdeal(DZFBoolean isdeal) {
        this.isdeal = isdeal;
    }

    public String getSenderman() {
        return senderman;
    }

    public void setSenderman(String senderman) {
        this.senderman = senderman;
    }

    public DZFDate getDsenddate() {
        return dsenddate;
    }

    public void setDsenddate(DZFDate dsenddate) {
        this.dsenddate = dsenddate;
    }

    public String getDealman() {
        return dealman;
    }

    public void setDealman(String dealman) {
        this.dealman = dealman;
    }

    public DZFDate getDdealdate() {
        return ddealdate;
    }

    public void setDdealdate(DZFDate ddealdate) {
        this.ddealdate = ddealdate;
    }

    public String getVnote() {
        return vnote;
    }

    public void setVnote(String vnote) {
        this.vnote = vnote;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(DZFDate doperatedate) {
        this.doperatedate = doperatedate;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_workflow";
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

}
