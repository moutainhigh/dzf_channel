package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

/**
 * 加盟商扣款查询VO
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class DebitQueryVO extends SuperVO {

	@FieldAlias("bdate")
	private String dbegindate; // 开始日期

	@FieldAlias("edate")
	private String denddate; // 结束日期
	
	@FieldAlias("chndate")
	private DZFDate chndate;//加盟商时间

	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("ccode")
    private String corpcode;//单位编码
	
	@FieldAlias("cname")
	private String corpname;//单位名称
	
    private String[] corps;//渠道商ids
    
	@FieldAlias("ndeductmny")
	private DZFDouble ndeductmny; // 折扣金额 扣款金额
	
	@FieldAlias("outmny")
	private DZFDouble outmny; // 预存款余额金额

	@FieldAlias("num")
	private Integer num;// 查询量
	
	/***************前台展示使用************/
	private String head;
	private DZFDouble one;
	private DZFDouble two;
	private DZFDouble three;
	private DZFDouble four;
	private DZFDouble five;
	private DZFDouble six;
	private DZFDouble seven;
	private DZFDouble eight;
	private DZFDouble nine;
	private DZFDouble ten;
	private DZFDouble eleven;
	private DZFDouble twelve;
	private DZFDouble thirteen;
	private DZFDouble fourteen;
	private DZFDouble fifteen;
	

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	public String getDbegindate() {
		return dbegindate;
	}

	public void setDbegindate(String dbegindate) {
		this.dbegindate = dbegindate;
	}

	public String getDenddate() {
		return denddate;
	}

	public void setDenddate(String denddate) {
		this.denddate = denddate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public DZFDate getChndate() {
		return chndate;
	}

	public void setChndate(DZFDate chndate) {
		this.chndate = chndate;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}


	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String[] getCorps() {
		return corps;
	}

	public void setCorps(String[] corps) {
		this.corps = corps;
	}

	public DZFDouble getNdeductmny() {
		return ndeductmny;
	}

	public void setNdeductmny(DZFDouble ndeductmny) {
		this.ndeductmny = ndeductmny;
	}

	public DZFDouble getOutmny() {
		return outmny;
	}

	public void setOutmny(DZFDouble outmny) {
		this.outmny = outmny;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public DZFDouble getOne() {
		return one;
	}

	public void setOne(DZFDouble one) {
		this.one = one;
	}

	public DZFDouble getTwo() {
		return two;
	}

	public void setTwo(DZFDouble two) {
		this.two = two;
	}

	public DZFDouble getThree() {
		return three;
	}

	public void setThree(DZFDouble three) {
		this.three = three;
	}

	public DZFDouble getFour() {
		return four;
	}

	public void setFour(DZFDouble four) {
		this.four = four;
	}

	public DZFDouble getFive() {
		return five;
	}

	public void setFive(DZFDouble five) {
		this.five = five;
	}

	public DZFDouble getSix() {
		return six;
	}

	public void setSix(DZFDouble six) {
		this.six = six;
	}

	public DZFDouble getSeven() {
		return seven;
	}

	public void setSeven(DZFDouble seven) {
		this.seven = seven;
	}

	public DZFDouble getEight() {
		return eight;
	}

	public void setEight(DZFDouble eight) {
		this.eight = eight;
	}

	public DZFDouble getNine() {
		return nine;
	}

	public void setNine(DZFDouble nine) {
		this.nine = nine;
	}

	public DZFDouble getTen() {
		return ten;
	}

	public void setTen(DZFDouble ten) {
		this.ten = ten;
	}

	public DZFDouble getEleven() {
		return eleven;
	}

	public void setEleven(DZFDouble eleven) {
		this.eleven = eleven;
	}

	public DZFDouble getTwelve() {
		return twelve;
	}

	public void setTwelve(DZFDouble twelve) {
		this.twelve = twelve;
	}

	public DZFDouble getThirteen() {
		return thirteen;
	}

	public void setThirteen(DZFDouble thirteen) {
		this.thirteen = thirteen;
	}

	public DZFDouble getFourteen() {
		return fourteen;
	}

	public void setFourteen(DZFDouble fourteen) {
		this.fourteen = fourteen;
	}

	public DZFDouble getFifteen() {
		return fifteen;
	}

	public void setFifteen(DZFDouble fifteen) {
		this.fifteen = fifteen;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
