package com.dzf.service.channel.dealmanage.impl;

import java.util.ArrayList;
import java.util.List;

import org.omg.Security.SecQOPPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockInVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.dealmanage.StockSumVO;
import com.dzf.model.channel.stock.GoodsNumVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IStockOutInService;
import com.dzf.service.channel.dealmanage.IStockSumService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.sun.tools.javah.resources.l10n;

import oracle.net.aso.s;

@Service("sumStock")
public class IStockSumImpl implements IStockSumService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Integer queryTotalRow(StockSumVO qvo) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
        return multBodyObjectBO.queryDataTotal(StockSumVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@Override
	public List<StockSumVO> query(StockSumVO qvo) {
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	        List<StockSumVO> list = (List<StockSumVO>) multBodyObjectBO.queryDataPage(StockSumVO.class, sqpvo.getSql(),
	                sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
	        return list;
	}

	@Override
	public List<GoodsBoxVO> queryComboBox() {
		return null;
	}

	
	private QrySqlSpmVO getQrySqlSpm(StockSumVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("    select cg.pk_goods, cg.vgoodscode, cg.vgoodsname, ");
		sql.append("    spec.invspec,spec.invtype, ");
		sql.append("    sib2.num nnumin,");
		sql.append("    spec.pk_goodsspec, ");
		sql.append("    sib2.mny totalmoneyin, ");
		sql.append("    sob.num nnumout,  sob.mny totalmoneyout,");
		sql.append("    nvl(sib1.num,0)  nnumstart,");
		sql.append("    decode( nvl(sib1.num,0), 0, 0, sib1.mny /nvl(sib1.num,0) )  npricestart,");
		sql.append("    sib1.mny totalmoneys, ");
		sql.append("    ((nvl(num.istocknum, 0) - nvl(num.ioutnum, 0)) + nvl(sib2.num, 0) - ");
		sql.append("    nvl(sib3.num, 0)) nnumend, ");
		sql.append("    (nvl(sib1.mny,0) + nvl(sib2.mny,0) - nvl(sob.mny,0)) totalmoneye, ");
		sql.append("    decode(((  nvl(sib3.num,0) + nvl(sib2.num, 0) - nvl(sob.num, 0)) ),0,0,");
		sql.append("    ( (nvl(sib1.mny,0) + nvl(sib2.mny,0) - nvl(sob.mny,0))   /");
		sql.append("    ( (  nvl(sib3.num, 0) + nvl(sib2.num, 0) - nvl(sob.num, 0)) )) ) npriceend");
		
		sql.append("    from cn_goods cg");
		sql.append("    left join cn_goodsspec spec on cg.pk_goods = spec.pk_goods ");
		sql.append("    left join cn_stocknum num on num.pk_goods = spec.pk_goods ");
		sql.append("    and num.pk_goodsspec = spec.pk_goodsspec ");
		sql.append("    left join (select sum(nmny) mny,sum(nnum) num, ");
		sql.append("    pk_goodsspec, pk_goods ");
		sql.append("    from cn_stockin_b ib ");
		sql.append("    left join cn_stockin si on si.pk_stockin =ib.pk_stockin ");
		if (qvo.getBegdate() != null) {
			sql.append("   where substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		sql.append("    group by pk_goodsspec, pk_goods) sib1 on sib1.pk_goods =  num.pk_goods ");
		sql.append("    and sib1.pk_goodsspec = num.pk_goodsspec");
		
		
		sql.append("    left join (select sum(nmny) mny,sum(nnum) num, ");
		sql.append("    pk_goodsspec, pk_goods ");
		sql.append("    from cn_stockin_b ib ");
		sql.append("    left join cn_stockin si on si.pk_stockin =ib.pk_stockin ");
		if (qvo.getEnddate()!= null) {
			sql.append("   where substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}
		sql.append("    group by pk_goodsspec, pk_goods) sib3 on sib3.pk_goods =  num.pk_goods ");
		sql.append("    and sib3.pk_goodsspec = num.pk_goodsspec");
		
		
		
		sql.append("    left join (select sum(nmny) mny,sum(nnum) num,pk_goodsspec,pk_goods");
		sql.append("    from cn_stockin_b ib ");
		sql.append("    left join cn_stockin si on si.pk_stockin = ib.pk_stockin ");
		if (qvo.getBegdate() != null) {
			sql.append("   where substr(si.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   and  substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}
		sql.append("     group by pk_goodsspec, pk_goods) sib2 on sib2.pk_goods =num.pk_goods");
		sql.append("     and sib2.pk_goodsspec = num.pk_goodsspec");
		sql.append("     left join (select sum(nnum) num, sum(nmny) mny, pk_goodsspec,pk_goods");
		sql.append("     from cn_stockout_b ob");
		sql.append("     left join cn_stockout so on ob.pk_stockout = so.pk_stockout");
		if (qvo.getBegdate() != null) {
			sql.append("   where substr(so.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   and substr(so.dconfirmtime,0,10) <= ?  \n ");
			spm.addParam(qvo.getEnddate());
		}
		sql.append("     group by pk_goodsspec, pk_goods) sob on sob.pk_goods =num.pk_goods");
		sql.append("     and sob.pk_goodsspec = ");
		sql.append("     num.pk_goodsspec");
		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" where cg.pk_goods in (").append(inSql).append(")");
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	
}
