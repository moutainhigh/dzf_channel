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
	
    @FieldAlias("acode")
    private String areacode;//大区编码
	
	@FieldAlias("aname")
    public String areaname;//大区名称
	
	@FieldAlias("uid")
	public String userid; // 用户主键（大区总经理）
    
    @FieldAlias("uname")
    public String username; // 用户名称（大区总经理）
    
	@FieldAlias("provname")
	public String vprovname;// 省市名称
	
	@FieldAlias("ovince")
	public Integer vprovince;// 地区
	
	@FieldAlias("cuid")
	public String cuserid; // 用户主键（渠道）
	
    @FieldAlias("cuname")
    public String cusername; // 用户名称（渠道））

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
	
	@FieldAlias("chtype")
	private String channeltype;//加盟商类型  1：普通；2：金牌
	
    private String[] corps;//渠道商ids
    
	@FieldAlias("ndemny")
	private DZFDouble ndeductmny; // 扣款金额-预付款
	
	@FieldAlias("nderebmny")
	private DZFDouble ndedrebamny;//扣款金额-返点款
	
	@FieldAlias("outymny")
	private DZFDouble outymny; // 余额-预付款
	
	@FieldAlias("outfmny")
	private DZFDouble outfmny; // 余额-返点款

	@FieldAlias("num")
	private Integer num;// 查询量
	
    @FieldAlias("dreldate")
    private DZFDate drelievedate;//解约日期
	
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
	private DZFDouble sixteen1;
	private DZFDouble seventeen1;
	private DZFDouble eighteen1;
	private DZFDouble nineteen1;
	private DZFDouble twenty1;
	private DZFDouble twenty11;
	private DZFDouble twenty21;
	private DZFDouble twenty31;
	private DZFDouble twenty41;
	private DZFDouble twenty51;
	private DZFDouble twenty61;
	private DZFDouble twenty71;
	private DZFDouble twenty81;
	private DZFDouble twenty91;
	private DZFDouble thirty1;
	private DZFDouble thirty11;
	private DZFDouble thirty21;
	private DZFDouble thirty31;
	private DZFDouble thirty41;
	private DZFDouble thirty51;
	private DZFDouble thirty61;
	private DZFDouble thirty71;

	
	
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
	private DZFDouble sixteen2;
	private DZFDouble seventeen2;
	private DZFDouble eighteen2;
	private DZFDouble nineteen2;
	private DZFDouble twenty2;
	private DZFDouble twenty12;
	private DZFDouble twenty22;
	private DZFDouble twenty32;
	private DZFDouble twenty42;
	private DZFDouble twenty52;
	private DZFDouble twenty62;
	private DZFDouble twenty72;
	private DZFDouble twenty82;
	private DZFDouble twenty92;
	private DZFDouble thirty2;
	private DZFDouble thirty12;
	private DZFDouble thirty22;
	private DZFDouble thirty32;
	private DZFDouble thirty42;
	private DZFDouble thirty52;
	private DZFDouble thirty62;
	private DZFDouble thirty72;

	public DZFDate getDrelievedate() {
        return drelievedate;
    }

    public void setDrelievedate(DZFDate drelievedate) {
        this.drelievedate = drelievedate;
    }

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

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public DZFDouble getSixteen1() {
		return sixteen1;
	}

	public void setSixteen1(DZFDouble sixteen1) {
		this.sixteen1 = sixteen1;
	}

	public DZFDouble getSeventeen1() {
		return seventeen1;
	}

	public void setSeventeen1(DZFDouble seventeen1) {
		this.seventeen1 = seventeen1;
	}

	public DZFDouble getEighteen1() {
		return eighteen1;
	}

	public void setEighteen1(DZFDouble eighteen1) {
		this.eighteen1 = eighteen1;
	}

	public DZFDouble getNineteen1() {
		return nineteen1;
	}

	public DZFDouble getThirty71() {
		return thirty71;
	}

	public void setThirty71(DZFDouble thirty71) {
		this.thirty71 = thirty71;
	}

	public DZFDouble getThirty72() {
		return thirty72;
	}

	public void setThirty72(DZFDouble thirty72) {
		this.thirty72 = thirty72;
	}

	public void setNineteen1(DZFDouble nineteen1) {
		this.nineteen1 = nineteen1;
	}

	public DZFDouble getTwenty1() {
		return twenty1;
	}

	public void setTwenty1(DZFDouble twenty1) {
		this.twenty1 = twenty1;
	}

	public DZFDouble getTwenty11() {
		return twenty11;
	}

	public void setTwenty11(DZFDouble twenty11) {
		this.twenty11 = twenty11;
	}

	public DZFDouble getTwenty21() {
		return twenty21;
	}

	public void setTwenty21(DZFDouble twenty21) {
		this.twenty21 = twenty21;
	}

	public DZFDouble getTwenty31() {
		return twenty31;
	}

	public void setTwenty31(DZFDouble twenty31) {
		this.twenty31 = twenty31;
	}

	public DZFDouble getTwenty41() {
		return twenty41;
	}

	public void setTwenty41(DZFDouble twenty41) {
		this.twenty41 = twenty41;
	}

	public DZFDouble getTwenty51() {
		return twenty51;
	}

	public void setTwenty51(DZFDouble twenty51) {
		this.twenty51 = twenty51;
	}

	public DZFDouble getTwenty61() {
		return twenty61;
	}

	public void setTwenty61(DZFDouble twenty61) {
		this.twenty61 = twenty61;
	}

	public DZFDouble getTwenty71() {
		return twenty71;
	}

	public void setTwenty71(DZFDouble twenty71) {
		this.twenty71 = twenty71;
	}

	public DZFDouble getTwenty81() {
		return twenty81;
	}

	public void setTwenty81(DZFDouble twenty81) {
		this.twenty81 = twenty81;
	}

	public DZFDouble getTwenty91() {
		return twenty91;
	}

	public void setTwenty91(DZFDouble twenty91) {
		this.twenty91 = twenty91;
	}

	public DZFDouble getThirty1() {
		return thirty1;
	}

	public void setThirty1(DZFDouble thirty1) {
		this.thirty1 = thirty1;
	}

	public DZFDouble getThirty11() {
		return thirty11;
	}

	public void setThirty11(DZFDouble thirty11) {
		this.thirty11 = thirty11;
	}

	public DZFDouble getThirty21() {
		return thirty21;
	}

	public void setThirty21(DZFDouble thirty21) {
		this.thirty21 = thirty21;
	}

	public DZFDouble getThirty31() {
		return thirty31;
	}

	public void setThirty31(DZFDouble thirty31) {
		this.thirty31 = thirty31;
	}

	public DZFDouble getThirty41() {
		return thirty41;
	}

	public void setThirty41(DZFDouble thirty41) {
		this.thirty41 = thirty41;
	}

	public DZFDouble getThirty51() {
		return thirty51;
	}

	public void setThirty51(DZFDouble thirty51) {
		this.thirty51 = thirty51;
	}

	public DZFDouble getThirty61() {
		return thirty61;
	}

	public void setThirty61(DZFDouble thirty61) {
		this.thirty61 = thirty61;
	}

	public DZFDouble getSixteen2() {
		return sixteen2;
	}

	public void setSixteen2(DZFDouble sixteen2) {
		this.sixteen2 = sixteen2;
	}

	public DZFDouble getSeventeen2() {
		return seventeen2;
	}

	public void setSeventeen2(DZFDouble seventeen2) {
		this.seventeen2 = seventeen2;
	}

	public DZFDouble getEighteen2() {
		return eighteen2;
	}

	public void setEighteen2(DZFDouble eighteen2) {
		this.eighteen2 = eighteen2;
	}

	public DZFDouble getNineteen2() {
		return nineteen2;
	}

	public void setNineteen2(DZFDouble nineteen2) {
		this.nineteen2 = nineteen2;
	}

	public DZFDouble getTwenty2() {
		return twenty2;
	}

	public void setTwenty2(DZFDouble twenty2) {
		this.twenty2 = twenty2;
	}

	public DZFDouble getTwenty12() {
		return twenty12;
	}

	public void setTwenty12(DZFDouble twenty12) {
		this.twenty12 = twenty12;
	}

	public DZFDouble getTwenty22() {
		return twenty22;
	}

	public void setTwenty22(DZFDouble twenty22) {
		this.twenty22 = twenty22;
	}

	public DZFDouble getTwenty32() {
		return twenty32;
	}

	public void setTwenty32(DZFDouble twenty32) {
		this.twenty32 = twenty32;
	}

	public DZFDouble getTwenty42() {
		return twenty42;
	}

	public void setTwenty42(DZFDouble twenty42) {
		this.twenty42 = twenty42;
	}

	public DZFDouble getTwenty52() {
		return twenty52;
	}

	public void setTwenty52(DZFDouble twenty52) {
		this.twenty52 = twenty52;
	}

	public DZFDouble getTwenty62() {
		return twenty62;
	}

	public void setTwenty62(DZFDouble twenty62) {
		this.twenty62 = twenty62;
	}

	public DZFDouble getTwenty72() {
		return twenty72;
	}

	public void setTwenty72(DZFDouble twenty72) {
		this.twenty72 = twenty72;
	}

	public DZFDouble getTwenty82() {
		return twenty82;
	}

	public void setTwenty82(DZFDouble twenty82) {
		this.twenty82 = twenty82;
	}

	public DZFDouble getTwenty92() {
		return twenty92;
	}

	public void setTwenty92(DZFDouble twenty92) {
		this.twenty92 = twenty92;
	}

	public DZFDouble getThirty2() {
		return thirty2;
	}

	public void setThirty2(DZFDouble thirty2) {
		this.thirty2 = thirty2;
	}

	public DZFDouble getThirty12() {
		return thirty12;
	}

	public void setThirty12(DZFDouble thirty12) {
		this.thirty12 = thirty12;
	}

	public DZFDouble getThirty22() {
		return thirty22;
	}

	public void setThirty22(DZFDouble thirty22) {
		this.thirty22 = thirty22;
	}

	public DZFDouble getThirty32() {
		return thirty32;
	}

	public void setThirty32(DZFDouble thirty32) {
		this.thirty32 = thirty32;
	}

	public DZFDouble getThirty42() {
		return thirty42;
	}

	public void setThirty42(DZFDouble thirty42) {
		this.thirty42 = thirty42;
	}

	public DZFDouble getThirty52() {
		return thirty52;
	}

	public void setThirty52(DZFDouble thirty52) {
		this.thirty52 = thirty52;
	}

	public DZFDouble getThirty62() {
		return thirty62;
	}

	public void setThirty62(DZFDouble thirty62) {
		this.thirty62 = thirty62;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getCusername() {
		return cusername;
	}

	public void setCusername(String cusername) {
		this.cusername = cusername;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getChanneltype() {
		return channeltype;
	}

	public void setChanneltype(String channeltype) {
		this.channeltype = channeltype;
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
