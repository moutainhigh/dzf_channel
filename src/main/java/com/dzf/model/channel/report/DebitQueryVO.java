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
    
	@FieldAlias("ndemny")
	private DZFDouble ndeductmny; // 扣款金额-预付款
	
	@FieldAlias("nderebmny")
	private DZFDouble ndedrebamny;//扣款金额-返点款
	
	@FieldAlias("outymny")
	private DZFDouble outymny; // 金额-预付款
	
	@FieldAlias("outfmny")
	private DZFDouble outfmny; // 余额-返点款

	@FieldAlias("num")
	private Integer num;// 查询量
	
	/***************前台展示使用************/
	private String head;
	private DZFDouble one1;
	private DZFDouble two1;
	private DZFDouble three1;
	private DZFDouble four1;
	private DZFDouble five1;
	private DZFDouble six1;
	private DZFDouble seven1;
	private DZFDouble eight1;
	private DZFDouble nine1;
	private DZFDouble ten1;
	private DZFDouble eleven1;
	private DZFDouble twelve1;
	private DZFDouble thirteen1;
	private DZFDouble fourteen1;
	private DZFDouble fifteen1;
	
	private DZFDouble one2;
	private DZFDouble two2;
	private DZFDouble three2;
	private DZFDouble four2;
	private DZFDouble five2;
	private DZFDouble six2;
	private DZFDouble seven2;
	private DZFDouble eight2;
	private DZFDouble nine2;
	private DZFDouble ten2;
	private DZFDouble eleven2;
	private DZFDouble twelve2;
	private DZFDouble thirteen2;
	private DZFDouble fourteen2;
	private DZFDouble fifteen2;
	

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

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public DZFDouble getNdedrebamny() {
		return ndedrebamny;
	}

	public void setNdedrebamny(DZFDouble ndedrebamny) {
		this.ndedrebamny = ndedrebamny;
	}

	public DZFDouble getOutymny() {
		return outymny;
	}

	public void setOutymny(DZFDouble outymny) {
		this.outymny = outymny;
	}

	public DZFDouble getOutfmny() {
		return outfmny;
	}

	public void setOutfmny(DZFDouble outfmny) {
		this.outfmny = outfmny;
	}

	public DZFDouble getOne1() {
		return one1;
	}

	public void setOne1(DZFDouble one1) {
		this.one1 = one1;
	}

	public DZFDouble getTwo1() {
		return two1;
	}

	public void setTwo1(DZFDouble two1) {
		this.two1 = two1;
	}

	public DZFDouble getThree1() {
		return three1;
	}

	public void setThree1(DZFDouble three1) {
		this.three1 = three1;
	}

	public DZFDouble getFour1() {
		return four1;
	}

	public void setFour1(DZFDouble four1) {
		this.four1 = four1;
	}

	public DZFDouble getFive1() {
		return five1;
	}

	public void setFive1(DZFDouble five1) {
		this.five1 = five1;
	}

	public DZFDouble getSix1() {
		return six1;
	}

	public void setSix1(DZFDouble six1) {
		this.six1 = six1;
	}

	public DZFDouble getSeven1() {
		return seven1;
	}

	public void setSeven1(DZFDouble seven1) {
		this.seven1 = seven1;
	}

	public DZFDouble getEight1() {
		return eight1;
	}

	public void setEight1(DZFDouble eight1) {
		this.eight1 = eight1;
	}

	public DZFDouble getNine1() {
		return nine1;
	}

	public void setNine1(DZFDouble nine1) {
		this.nine1 = nine1;
	}

	public DZFDouble getTen1() {
		return ten1;
	}

	public void setTen1(DZFDouble ten1) {
		this.ten1 = ten1;
	}

	public DZFDouble getEleven1() {
		return eleven1;
	}

	public void setEleven1(DZFDouble eleven1) {
		this.eleven1 = eleven1;
	}

	public DZFDouble getTwelve1() {
		return twelve1;
	}

	public void setTwelve1(DZFDouble twelve1) {
		this.twelve1 = twelve1;
	}

	public DZFDouble getThirteen1() {
		return thirteen1;
	}

	public void setThirteen1(DZFDouble thirteen1) {
		this.thirteen1 = thirteen1;
	}

	public DZFDouble getFourteen1() {
		return fourteen1;
	}

	public void setFourteen1(DZFDouble fourteen1) {
		this.fourteen1 = fourteen1;
	}

	public DZFDouble getFifteen1() {
		return fifteen1;
	}

	public void setFifteen1(DZFDouble fifteen1) {
		this.fifteen1 = fifteen1;
	}

	public DZFDouble getOne2() {
		return one2;
	}

	public void setOne2(DZFDouble one2) {
		this.one2 = one2;
	}

	public DZFDouble getTwo2() {
		return two2;
	}

	public void setTwo2(DZFDouble two2) {
		this.two2 = two2;
	}

	public DZFDouble getThree2() {
		return three2;
	}

	public void setThree2(DZFDouble three2) {
		this.three2 = three2;
	}

	public DZFDouble getFour2() {
		return four2;
	}

	public void setFour2(DZFDouble four2) {
		this.four2 = four2;
	}

	public DZFDouble getFive2() {
		return five2;
	}

	public void setFive2(DZFDouble five2) {
		this.five2 = five2;
	}

	public DZFDouble getSix2() {
		return six2;
	}

	public void setSix2(DZFDouble six2) {
		this.six2 = six2;
	}

	public DZFDouble getSeven2() {
		return seven2;
	}

	public void setSeven2(DZFDouble seven2) {
		this.seven2 = seven2;
	}

	public DZFDouble getEight2() {
		return eight2;
	}

	public void setEight2(DZFDouble eight2) {
		this.eight2 = eight2;
	}

	public DZFDouble getNine2() {
		return nine2;
	}

	public void setNine2(DZFDouble nine2) {
		this.nine2 = nine2;
	}

	public DZFDouble getTen2() {
		return ten2;
	}

	public void setTen2(DZFDouble ten2) {
		this.ten2 = ten2;
	}

	public DZFDouble getEleven2() {
		return eleven2;
	}

	public void setEleven2(DZFDouble eleven2) {
		this.eleven2 = eleven2;
	}

	public DZFDouble getTwelve2() {
		return twelve2;
	}

	public void setTwelve2(DZFDouble twelve2) {
		this.twelve2 = twelve2;
	}

	public DZFDouble getThirteen2() {
		return thirteen2;
	}

	public void setThirteen2(DZFDouble thirteen2) {
		this.thirteen2 = thirteen2;
	}

	public DZFDouble getFourteen2() {
		return fourteen2;
	}

	public void setFourteen2(DZFDouble fourteen2) {
		this.fourteen2 = fourteen2;
	}

	public DZFDouble getFifteen2() {
		return fifteen2;
	}

	public void setFifteen2(DZFDouble fifteen2) {
		this.fifteen2 = fifteen2;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
