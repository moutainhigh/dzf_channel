package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.report.ManagerVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 数据分析excel导出配置
 * @author gejw
 *
 */
public class ManagerExcelField implements IExceport<ManagerVO>{
	
	private ManagerVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Integer type;
	
	private Fieldelement[] fields1 = new Fieldelement[]{
			new Fieldelement("corpname", "加盟商",false,0,false),
			new Fieldelement("bondmny", "保证金",true,2,false),
			new Fieldelement("predeposit", "预存款",true,2,false),
			new Fieldelement("num", "提单量",false,0,false),
			new Fieldelement("ntotalmny", "合同总金额",true,2,false),
			new Fieldelement("ndeductmny", "扣款金额",true,2,false),
			new Fieldelement("outmny", "预存款余额",true,2,false),
	};
	
	private Fieldelement[] fields2 = new Fieldelement[]{
            new Fieldelement("vprovname", "省（市）",false,0,false),
            new Fieldelement("cusername", "渠道经理",false,0,false),
            new Fieldelement("corpname", "加盟商",false,0,false),
            new Fieldelement("bondmny", "保证金",true,2,false),
            new Fieldelement("predeposit", "预存款",true,2,false),
            new Fieldelement("num", "提单量",false,0,false),
            new Fieldelement("ntotalmny", "合同总金额",true,2,false),
            new Fieldelement("ndeductmny", "扣款金额",true,2,false),
            new Fieldelement("outmny", "预存款余额",true,2,false),
    };
	
	private Fieldelement[] fields3 = new Fieldelement[]{
	        new Fieldelement("areaname", "大区",false,0,false),
            new Fieldelement("username", "区总",false,0,false),
            new Fieldelement("vprovname", "省（市）",false,0,false),
            new Fieldelement("cusername", "渠道经理",false,0,false),
            new Fieldelement("corpname", "加盟商",false,0,false),
            new Fieldelement("bondmny", "保证金",true,2,false),
            new Fieldelement("predeposit", "预存款",true,2,false),
            new Fieldelement("num", "提单量",false,0,false),
            new Fieldelement("ntotalmny", "合同总金额",true,2,false),
            new Fieldelement("ndeductmny", "扣款金额",true,2,false),
            new Fieldelement("outmny", "预存款余额",true,2,false),
    };
	
	public ManagerExcelField(int type) {
	    this.type = type;
    }

    @Override
	public String getExcelport2007Name() {
        if(type==1){
            return "省数据分析" + now + ".xls";
        }else if (type==2){
            return "大区数据统计" + now + ".xls";
        }else{
            return "渠道总数据分析" + now + ".xls";
        }
	}
	
	@Override
	public String getExcelport2003Name() {
	    if(type==1){
            return "省数据分析" + now + ".xls";
        }else if (type==2){
            return "大区数据统计" + now + ".xls";
        }else{
            return "渠道总数据分析" + now + ".xls";
        }
	}

	@Override
	public String getExceportHeadName() {
	    if(type==1){
            return "省数据分析";
        }else if (type==2){
            return "大区数据统计";
        }else{
            return "渠道总数据分析";
        }
	}

	@Override
	public String getSheetName() {
	    if(type==1){
	        return "省数据分析";
        }else if (type==2){
            return "大区数据统计";
        }else{
            return "渠道总数据分析";
        }
	}

	@Override
	public ManagerVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
	    if(type == 1){
	        return fields1;
        }else if(type == 2){
            return fields2;
        }else if(type == 3){
            return fields3;
        }
	    return fields1; 
	}

    public void setVos(ManagerVO[] vos) {
        this.vos = vos;
    }

    @Override
	public String getQj() {
		return qj;
	}

	@Override
	public String getCreateSheetDate() {
		return now;
	}

	@Override
	public String getCreateor() {
		return creator;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String getCorpName() {
		return corpname;
	}
	
	public void setCorpName(String corpname){
		this.corpname = corpname;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}
	
	
}