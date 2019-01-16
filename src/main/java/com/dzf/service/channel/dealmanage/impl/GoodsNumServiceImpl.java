package com.dzf.service.channel.dealmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.visitor.functions.Left;
import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
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
        return list;
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
        /*
         * sql.append(" select t.vname, "); sql.append(
         * "        s.invspec,s.invtype, "); sql.append("        g.vgoodscode, "
         * ); sql.append("        g.vgoodsname, "); sql.append(
         * "        num.istocknum - nvl(num.ioutnum, 0) istocknum, ");
         * sql.append(
         * "        nvl(num.isellnum, 0) - nvl(num.ioutnum,0)  ilocknum, ");
         * sql.append("        num.istocknum  - nvl(num.isellnum, 0) iusenum");
         * sql.append("   from cn_stocknum num "); sql.append(
         * "   left join cn_goods g on num.pk_goods = g.pk_goods "); sql.append(
         * "   left join cn_goodstype t on g.pk_goodstype = t.pk_goodstype ");
         * sql.append(
         * "   left join cn_goodsspec s on num.pk_goodsspec = s.pk_goodsspec ");
         * sql.append("  where nvl(g.dr, 0) = 0 "); sql.append(
         * "    and nvl(num.dr, 0) = 0 "); sql.append(
         * "    and nvl(t.dr, 0) = 0 "); sql.append("    and nvl(s.dr, 0) = 0 "
         * );
         */
        sql.append("    select t.vname, ");
        sql.append("        s.invspec, s.invtype,   ");
        sql.append("  g.vgoodscode, g.vgoodsname, g.vmeasname goodsunit,s.nprice goodsprice,    ");
        sql.append("  siib.nmny isstockmny,");
        sql.append("  nvl(num.ioutnum,0) ioutnum,  nvl(num.istocknum,0) istockinnum,    ");
        sql.append("  nvl(num.istocknum,0) - nvl(num.ioutnum, 0) istocknum, ");
        sql.append("  nvl(num.isellnum, 0) ilocknum,  ");
        sql.append("  nvl(stockbill.nsendnum,0) nsendnum,   ");
        //sql.append("  nvl(num.istocknum,0) -nvl( num.ioutnum,0)-nvl(stockbill.nsendnum,0) ibuynum  ");
        sql.append("  nvl(num.istocknum, 0) - nvl(num.isellnum,0) ibuynum");
        sql.append("  from cn_stocknum num  ");
        sql.append("  left join (select sib.pk_goods, sib.pk_goodsspec, sum(sib.nmny) nmny  ");
        sql.append("  from cn_stockin_b sib");
        sql.append("  where nvl(sib.dr, 0) = 0");
        sql.append("  group by sib.pk_goods, sib.pk_goodsspec) siib on siib.pk_goods =");
        sql.append("  num.pk_goods");
        sql.append("  and siib.pk_goodsspec =");
        sql.append("  num.pk_goodsspec");
        sql.append(" left join cn_goods g on num.pk_goods = g.pk_goods  ");
        sql.append(" left join cn_goodstype t on g.pk_goodstype = t.pk_goodstype    ");
        sql.append(" left join cn_goodsspec s on num.pk_goodsspec = s.pk_goodsspec  ");
        sql.append(" left join (select sob.pk_goods, sob.pk_goodsspec, sum(sob.nnum) nsendnum ");
        sql.append(" from cn_stockout so   ");
        sql.append(" left join cn_stockout_b sob on sob.pk_stockout =  so.pk_stockout    ");
        sql.append("  where so.vstatus = 1  and nvl(so.dr, 0) = 0 and nvl(sob.dr, 0) = 0");
        sql.append("   group by sob.pk_goods, sob.pk_goodsspec) stockbill on num.pk_goods = stockbill.pk_goods  ");
        sql.append("   and num.pk_goodsspec = stockbill.pk_goodsspec    ");
       // sql.append(" left join cn_goodsbill_b gbill on num.pk_goods=gbill.pk_goods");
        sql.append("  where nvl(g.dr, 0) = 0    ");
        sql.append("    and nvl(num.dr, 0) = 0  ");
        sql.append("    and nvl(t.dr, 0) = 0    ");
        sql.append("    and nvl(s.dr, 0) = 0     ");
        sql.append("    and num.istocknum > 0");//入库单数量大于0
          if(!StringUtil.isEmpty(qvo.getPk_goodstype())){ 
              sql.append("and g.pk_goodstype=? "); 
              spm.addParam(qvo.getPk_goodstype()); 
              }
          if(!StringUtil.isEmpty(qvo.getVgoodscode())){ 
              sql.append( "and g.vgoodscode like ? ");
              spm.addParam("%"+qvo.getVgoodscode()+"%"); 
          }
          if(!StringUtil.isEmpty(qvo.getVgoodsname())){ 
              sql.append("and g.vgoodsname like ? ");
              spm.addParam("%"+qvo.getVgoodsname()+"%"); 
          }
             sql.append(" order by t.vname,g.vgoodsname");
             qryvo.setSql(sql.toString());
             qryvo.setSpm(spm);
            return qryvo;
    }

}
