package com.dzf.service.channel.dealmanage.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.stock.GoodsNumVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.dealmanage.IGoodsNumService;

@Service("numGoods")
public class GoodsNumServiceImpl implements IGoodsNumService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private MultBodyObjectBO multBodyObjectBO;

    @Override
    public List<GoodsNumVO> query(GoodsNumVO qvo) throws DZFWarpException {
        QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
        List<GoodsNumVO> list = (List<GoodsNumVO>) multBodyObjectBO.queryDataPage(GoodsNumVO.class, sqpvo.getSql(),
                sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
        if(list!=null && list.size()>0){
            HashMap<String,Integer>  inMap = queryStockInNum(qvo);
            HashMap<String,Integer>  outMap = queryStockOutNum(qvo);
            String id ;
            for (GoodsNumVO goodsNumVO : list) {
            	id = goodsNumVO.getPk_goods()+goodsNumVO.getPk_goodsspec();
            	goodsNumVO.setIstockinnum(inMap.get(id));
            	goodsNumVO.setIoutnum(outMap.get(id));
			}
        }
        return list;
    }

	private HashMap queryStockInNum(GoodsNumVO qvo) {
		HashMap<String, Integer> inMap = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select sib.pk_goods, sib.pk_goodsspec, sum(nnum) istockinnum ");
		sql.append("  from cn_stockin_b sib ");
		sql.append("  left join cn_stockin si on sib.pk_stockin = si.pk_stockin ");
		sql.append(" where nvl(sib.dr, 0) = 0 ");
		sql.append("   and nvl(si.dr, 0) = 0 ");
		sql.append("   and substr(si.dconfirmtime, 0, 10) <= ? ");
		sql.append("   and si.vstatus = 2 ");
		sql.append(" group by sib.pk_goods, sib.pk_goodsspec ");
		spm.addParam(qvo.getNowdate());
		List<GoodsNumVO> retlist = (List<GoodsNumVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(GoodsNumVO.class));
		String id ;
		for (GoodsNumVO goodsNumVO : retlist) {
			id = goodsNumVO.getPk_goods()+goodsNumVO.getPk_goodsspec();
			inMap.put(id, goodsNumVO.getIstockinnum());
		}
		return inMap;
	}

	private HashMap queryStockOutNum(GoodsNumVO qvo) {
		HashMap<String, Integer> outMap = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select sob.pk_goods, sob.pk_goodsspec, sum(nnum) ioutnum ");
		sql.append("  from cn_stockout_b sob ");
		sql.append("  left join cn_stockout so on sob.pk_stockout = so.pk_stockout ");
		sql.append(" where nvl(sob.dr, 0) = 0 ");
		sql.append("   and nvl(so.dr, 0) = 0   ");
		sql.append("   and substr(so.dconfirmtime,0,10) <= ? ");
		sql.append("   and so.vstatus != 0 ");
		sql.append("  and sob.pk_goodsbill_b is not null ");
		sql.append(" group by sob.pk_goods, sob.pk_goodsspec ");
		spm.addParam(qvo.getNowdate());
		List<GoodsNumVO> retlist = (List<GoodsNumVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(GoodsNumVO.class));
		String id ;
		for (GoodsNumVO goodsNumVO : retlist) {
			id = goodsNumVO.getPk_goods()+goodsNumVO.getPk_goodsspec();
			outMap.put(id, goodsNumVO.getIoutnum());
		}
		return outMap;
	}

	@Override
    public Integer queryTotalRow(GoodsNumVO qvo) throws DZFWarpException {
        QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
        return multBodyObjectBO.queryDataTotal(StockOutVO.class, sqpvo.getSql(), sqpvo.getSpm());
    }

    /**
     * 获取查询条件
     * @param qvo
     * @return
     * @throws DZFWarpException
     */
	private QrySqlSpmVO getQrySqlSpm(GoodsNumVO qvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" ");
		sql.append("SELECT ");
		sql.append("    t.vname, ");
		sql.append("    s.invspec, ");
		sql.append("    s.invtype, ");
		sql.append("    g.vgoodscode, ");
		sql.append("    g.vgoodsname, ");
		sql.append("    g.vmeasname   goodsunit, ");
		sql.append("    s.nprice      goodsprice, ");
		sql.append("    num.pk_goods, ");
		sql.append("    num.pk_goodsspec, ");
		sql.append("    nvl(num.istocknum, 0) - nvl(num.ioutnum, 0) istocknum, ");
		sql.append("    nvl(num.isellnum, 0) - nvl(sh.nnum, 0) ilocknum, ");
		sql.append("    nvl(stockbill.nsendnum,0) nsendnum, ");
		sql.append("    nvl(bill.amount,0)- nvl(num.ioutnum, 0) noutnum, ");
		sql.append("    nvl(num.istocknum, 0) - nvl(num.isellnum,0) ibuynum ");
		sql.append("FROM ");
		sql.append("    cn_stocknum    num ");
		sql.append("    LEFT JOIN cn_goods       g ON num.pk_goods = g.pk_goods ");
		sql.append("    LEFT JOIN cn_goodstype   t ON g.pk_goodstype = t.pk_goodstype ");
		sql.append("    LEFT JOIN cn_goodsspec   s ON num.pk_goodsspec = s.pk_goodsspec ");
		sql.append("    LEFT JOIN ( ");
		sql.append("        SELECT sob.pk_goods,sob.pk_goodsspec,SUM(sob.nnum) AS nsendnum");
		sql.append("        FROM  cn_stockout     so ");
		sql.append("        LEFT JOIN cn_stockout_b   sob ON sob.pk_stockout = so.pk_stockout ");
		sql.append("        WHERE ");
		sql.append("            so.vstatus = 1 ");
		sql.append("            AND nvl(so.dr, 0) = 0 ");
		sql.append("            AND sob.pk_goodsbill_b IS NOT NULL ");
		sql.append("            AND nvl(sob.dr, 0) = 0 ");
		sql.append("        GROUP BY ");
		sql.append("            sob.pk_goods, ");
		sql.append("            sob.pk_goodsspec ");
		sql.append("    ) stockbill ON num.pk_goods = stockbill.pk_goods ");
		sql.append("                   AND num.pk_goodsspec = stockbill.pk_goodsspec ");
		sql.append("    LEFT JOIN ( ");
		sql.append("        SELECT  pk_goods,  pk_goodsspec,SUM(nnum) nnum");
		sql.append("        FROM  cn_stockout_b ");
		sql.append("        WHERE ");
		sql.append("            nvl(dr, 0) = 0 ");
		sql.append("            AND pk_corp IS NULL ");
		sql.append("            AND pk_goodsbill_b IS NULL ");
		sql.append("        GROUP BY ");
		sql.append("            pk_goods, ");
		sql.append("            pk_goodsspec ");
		sql.append("    ) sh ON num.pk_goods = sh.pk_goods ");
		sql.append("            AND num.pk_goodsspec = sh.pk_goodsspec ");
		sql.append("    LEFT JOIN ( ");
		sql.append("        SELECT b.pk_goods,b.pk_goodsspec,SUM(amount) amount ");
		sql.append("        FROM  cn_goodsbill_b   b ");
		sql.append("            LEFT JOIN cn_goodsbill     g ON b.pk_goodsbill = g.pk_goodsbill ");
		sql.append("        WHERE ");
		sql.append("            nvl(b.dr, 0) = 0 ");
		sql.append("            AND nvl(g.dr, 0) = 0 ");
		sql.append("            AND g.vstatus IN(1,2,3) ");
		sql.append("        GROUP BY ");
		sql.append("            b.pk_goods, ");
		sql.append("            b.pk_goodsspec ");
		sql.append("    ) bill ON num.pk_goods = bill.pk_goods ");
		sql.append("              AND num.pk_goodsspec = bill.pk_goodsspec ");
		sql.append("WHERE ");
		sql.append("    nvl(g.dr, 0) = 0 ");
		sql.append("    AND nvl(num.dr, 0) = 0 ");
		sql.append("    AND nvl(t.dr, 0) = 0 ");
		sql.append("    AND nvl(s.dr, 0) = 0 ");
		// 入库单数量大于0
		sql.append("    and num.istocknum > 0");
		if (!StringUtil.isEmpty(qvo.getPk_goodstype())) {
			sql.append("and g.pk_goodstype=? ");
			spm.addParam(qvo.getPk_goodstype());
		}
		if (!StringUtil.isEmpty(qvo.getVgoodscode())) {
			sql.append("and g.vgoodscode like ? ");
			spm.addParam("%" + qvo.getVgoodscode() + "%");
		}
		if (!StringUtil.isEmpty(qvo.getVgoodsname())) {
			sql.append("and g.vgoodsname like ? ");
			spm.addParam("%" + qvo.getVgoodsname() + "%");
		}
		sql.append(" order by t.vname,g.vgoodsname");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

}
