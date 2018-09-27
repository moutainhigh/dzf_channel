package com.dzf.model.channel.sys_power;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 按钮权限vo
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ButtonVO extends SuperVO {
	
	@FieldAlias("bid")
    public String pk_button;//按钮主键
	
	@FieldAlias("name")
	public String but_name; //按钮名称
    
    @FieldAlias("code")
    public String but_code; //按钮编码
    
    @FieldAlias("fid")
    public String pk_funnode;//节点主键
    
    @FieldAlias("jsid")
    public String js_id;//js id
    
    @FieldAlias("jsmethod")
    public String js_method;//js方法名
    
	@FieldAlias("seal")
	public DZFBoolean isSeal;//是否禁用
    
	private String	dr;	
	
	private DZFDateTime	ts;

	/////////////////非储存字段///////////////////////////////////////////
	
	@FieldAlias("fname")
	private String fun_name;//节点名称
	
	@FieldAlias("rid")
	private String pk_role;//角色主键
	
	@FieldAlias("rcode")
	private String role_code;//角色编码
	
	@FieldAlias("rname")
	private String role_name;//角色名称
	
	private String jms02;//会计经理
	private String jms03;//销售经理
	private String jms04;//销售主管
	private String jms05;//外勤主管
	private String jms06;//主管会计
	private String jms07;//主办会计
	private String jms08;//记账会计
	private String jms09;//财税支持
	private String jms10;//销售
	private String jms11;//外勤
	
	private String jms12;//角色备用
	private String jms13;//角色备用
	
	public String getFun_name() {
		return fun_name;
	}

	public void setFun_name(String fun_name) {
		this.fun_name = fun_name;
	}

	public String getRole_code() {
		return role_code;
	}

	public void setRole_code(String role_code) {
		this.role_code = role_code;
	}

	public String getPk_role() {
		return pk_role;
	}

	public void setPk_role(String pk_role) {
		this.pk_role = pk_role;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public String getJms02() {
		return jms02;
	}

	public void setJms02(String jms02) {
		this.jms02 = jms02;
	}

	public String getJms03() {
		return jms03;
	}

	public void setJms03(String jms03) {
		this.jms03 = jms03;
	}

	public String getJms04() {
		return jms04;
	}

	public void setJms04(String jms04) {
		this.jms04 = jms04;
	}

	public String getJms05() {
		return jms05;
	}

	public void setJms05(String jms05) {
		this.jms05 = jms05;
	}

	public String getJms06() {
		return jms06;
	}

	public void setJms06(String jms06) {
		this.jms06 = jms06;
	}

	public String getJms07() {
		return jms07;
	}

	public void setJms07(String jms07) {
		this.jms07 = jms07;
	}

	public String getJms08() {
		return jms08;
	}

	public void setJms08(String jms08) {
		this.jms08 = jms08;
	}

	public String getJms09() {
		return jms09;
	}

	public void setJms09(String jms09) {
		this.jms09 = jms09;
	}

	public String getJms10() {
		return jms10;
	}

	public void setJms10(String jms10) {
		this.jms10 = jms10;
	}

	public String getJms11() {
		return jms11;
	}

	public void setJms11(String jms11) {
		this.jms11 = jms11;
	}

	public String getJms12() {
		return jms12;
	}

	public void setJms12(String jms12) {
		this.jms12 = jms12;
	}

	public String getJms13() {
		return jms13;
	}

	public void setJms13(String jms13) {
		this.jms13 = jms13;
	}

	public String getPk_button() {
		return pk_button;
	}

	public void setPk_button(String pk_button) {
		this.pk_button = pk_button;
	}

	public String getBut_name() {
		return but_name;
	}

	public void setBut_name(String but_name) {
		this.but_name = but_name;
	}

	public String getBut_code() {
		return but_code;
	}

	public void setBut_code(String but_code) {
		this.but_code = but_code;
	}

	public String getPk_funnode() {
		return pk_funnode;
	}

	public void setPk_funnode(String pk_funnode) {
		this.pk_funnode = pk_funnode;
	}

	public String getJs_id() {
		return js_id;
	}

	public void setJs_id(String js_id) {
		this.js_id = js_id;
	}

	public String getJs_method() {
		return js_method;
	}

	public void setJs_method(String js_method) {
		this.js_method = js_method;
	}

	public DZFBoolean getIsSeal() {
		return isSeal;
	}

	public void setIsSeal(DZFBoolean isSeal) {
		this.isSeal = isSeal;
	}

	public String getDr() {
		return dr;
	}

	public void setDr(String dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	
	@Override  
	public boolean equals(Object o) {  
        if (o == this) return true;  
        if (!(o instanceof ButtonVO)) {  
            return false;  
        }  
        ButtonVO vo = (ButtonVO) o;  
        return new EqualsBuilder().append(pk_button, vo.pk_button).isEquals();  
	 }  
	  
    @Override  
    public int hashCode() {  
        return new HashCodeBuilder(17, 37).append(pk_button).toHashCode();  
    }

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_button";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "sm_button";
	}

}
