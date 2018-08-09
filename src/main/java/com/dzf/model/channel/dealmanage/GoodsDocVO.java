package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 商品图片
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsDocVO extends SuperVO {

	private static final long serialVersionUID = -8547934427510141187L;
	
	@FieldAlias("doc_id")
	private String pk_goodsdoc; // 主键
	
	@FieldAlias("gid")
	private String pk_goods; // 主表主键

	@FieldAlias("corp_id")
	private String pk_corp; // 客户主键

	@FieldAlias("doc_name")
	private String docName; // 附件名称(中文)

	@FieldAlias("doc_temp")
	private String docTemp; // 附件名称(下载用 非中文)

	@FieldAlias("doc_owner")
	private String docOwner; // 上传人

	@FieldAlias("doc_time")
	private String docTime; // 上传时间

	@FieldAlias("fpath")
	private String vfilepath;// 文件存储路径　

	private String coperatorid;
	
	private DZFDate doperatedate;
	
	private Integer dr; // 删除标记
	
	private DZFDateTime ts; // 时间

	public String getPk_goodsdoc() {
		return pk_goodsdoc;
	}

	public void setPk_goodsdoc(String pk_goodsdoc) {
		this.pk_goodsdoc = pk_goodsdoc;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocTemp() {
		return docTemp;
	}

	public void setDocTemp(String docTemp) {
		this.docTemp = docTemp;
	}

	public String getDocOwner() {
		return docOwner;
	}

	public void setDocOwner(String docOwner) {
		this.docOwner = docOwner;
	}

	public String getDocTime() {
		return docTime;
	}

	public void setDocTime(String docTime) {
		this.docTime = docTime;
	}

	public String getVfilepath() {
		return vfilepath;
	}

	public void setVfilepath(String vfilepath) {
		this.vfilepath = vfilepath;
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
	public String getPKFieldName() {
		return "pk_goodsdoc";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_goods";
	}

	@Override
	public String getTableName() {
		return "cn_goodsdoc";
	}

}
