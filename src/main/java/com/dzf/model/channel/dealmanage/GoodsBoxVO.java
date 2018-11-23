package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 商品管理
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsBoxVO extends SuperVO {

	private static final long serialVersionUID = 9026641645160969490L;
	
	private String id;
	
	private String name;
	
	@FieldAlias("gid")
	private String pk_goods;//商品主键 
	
	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号
	
	@FieldAlias("tstp")
	private DZFDateTime tstamp;//商品最新时间戳

	public DZFDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DZFDateTime tstamp) {
		this.tstamp = tstamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
