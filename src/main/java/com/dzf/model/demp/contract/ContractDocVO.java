package com.dzf.model.demp.contract;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 
 * 附件
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ContractDocVO extends SuperVO {

	@FieldAlias("doc_id")
	private String pk_contract_doc; // 主键
	
	@FieldAlias("c_id")
	private String pk_contract; // 主表主键

	@FieldAlias("corp_id")
	private String pk_corp; // 客户主键

	@FieldAlias("doc_name")
	private String docName; // 附件名称(中文)

	@FieldAlias("doc_temp")
	private String docTemp; // 附件名称(下载用 非中文)

	@FieldAlias("doc_owner")
	private String docOwner; // 上传人

	@FieldAlias("doc_time")
	private DZFDateTime docTime; // 上传时间

	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间

	@FieldAlias("fpath")
	private String vfilepath;// 文件存储路径

	private String coperatorid;
	
	private DZFDate doperatedate;
	
	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	@FieldAlias("doctype")
	private Integer idoctype;//附件类型  1或空：附件；   2:执照；
	
	public Integer getIdoctype() {
		return idoctype;
	}

	public void setIdoctype(Integer idoctype) {
		this.idoctype = idoctype;
	}

	public String getPk_contract_doc() {
		return pk_contract_doc;
	}

	public void setPk_contract_doc(String pk_contract_doc) {
		this.pk_contract_doc = pk_contract_doc;
	}

	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getDocTemp() {
		return docTemp;
	}

	public void setDocTemp(String docTemp) {
		this.docTemp = docTemp;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public DZFDateTime getDocTime() {
		return docTime;
	}

	public void setDocTime(DZFDateTime docTime) {
		this.docTime = docTime;
	}

	public String getDocOwner() {
		return docOwner;
	}

	public void setDocOwner(String docOwner) {
		this.docOwner = docOwner;
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

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVfilepath() {
		return vfilepath;
	}

	public void setVfilepath(String vfilepath) {
		this.vfilepath = vfilepath;
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_contract";
	}

	@Override
	public String getPKFieldName() {
		return "pk_contract_doc";
	}

	@Override
	public String getTableName() {
		return "ynt_contract_doc";
	}
}
