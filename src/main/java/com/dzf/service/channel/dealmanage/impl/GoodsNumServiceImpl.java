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
     * 
     * @param pamvo
     * @return
     * @throws DZFWarpException
     */
	private QrySqlSpmVO getQrySqlSpm(GoodsNumVO qvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("    select t.vname, s.invspec, s.invtype,");
		sql.append("  g.vgoodscode, g.vgoodsname, g.vmeasname goodsunit,s.nprice goodsprice,");
//		sql.append("  nvl(num.ioutnum,0) ioutnum,  nvl(num.istocknum,0) istockinnum,");
		sql.append("  num.pk_goods, num.pk_goodsspec,");
		sql.append("  nvl(num.istocknum,0) - nvl(num.ioutnum, 0) istocknum, ");
		sql.append("  nvl(num.isellnum, 0) - nvl(sh.nnum,0) ilocknum,  ");
		sql.append("  nvl(stockbill.nsendnum,0)  nsendnum,   ");
		sql.append("  nvl(stockbill.noutnum,0) noutnum,  ");
		sql.append("  nvl(num.istocknum, 0) - nvl(num.isellnum,0) ibuynum");
		sql.append("  from cn_stocknum num  ");
		sql.append(" left join cn_goods g on num.pk_goods = g.pk_goods  ");
		sql.append(" left join cn_goodstype t on g.pk_goodstype = t.pk_goodstype    ");
		sql.append(" left join cn_goodsspec s on num.pk_goodsspec = s.pk_goodsspec  ");
		sql.append(" left join (select sob.pk_goods, sob.pk_goodsspec, sum(case so.vstatus when 0 then sob.nnum else 0 end) as noutnum, ");
		sql.append(" sum(case when so.vstatus=1 and sob.pk_goodsbill_b is not null then sob.nnum else 0 end) as nsendnum");
		sql.append(" from cn_stockout so   ");
		sql.append(" left join cn_stockout_b sob on sob.pk_stockout =  so.pk_stockout    ");
		sql.append("  where (so.vstatus = 0 or so.vstatus = 1)  and nvl(so.dr, 0) = 0 and nvl(sob.dr, 0) = 0");
		sql.append("   group by sob.pk_goods, sob.pk_goodsspec) stockbill   ");
		sql.append("   on num.pk_goods = stockbill.pk_goods and num.pk_goodsspec = stockbill.pk_goodsspec ");
		sql.append(" left join (select pk_goods,pk_goodsspec,sum(nnum) nnum from cn_stockout_b");
		sql.append(" 	 where nvl(dr,0)=0 and pk_corp is null and pk_goodsbill_b is null");
		sql.append(" 	 group by pk_goods,pk_goodsspec)sh  ");
		sql.append(" 	on num.pk_goods = sh.pk_goods and num.pk_goodsspec = sh.pk_goodsspec   ");
		sql.append("  where nvl(g.dr, 0) = 0    ");
		sql.append("    and nvl(num.dr, 0) = 0  ");
		sql.append("    and nvl(t.dr, 0) = 0    ");
		sql.append("    and nvl(s.dr, 0) = 0     ");
		sql.append("    and num.istocknum > 0");// 入库单数量大于0
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
