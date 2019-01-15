package com.dzf.service.channel.dealmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        sql.append("  g.vgoodscode, g.vgoodsname, g.vmeasname goodsunit,g.nprice goodsprice,    ");
        sql.append("  sib.nmny isstockmny,");
        sql.append("  nvl(num.ioutnum,0) ioutnum,  nvl(num.istocknum,0) istockinnum,    ");
        sql.append("  nvl(num.istocknum,0) - nvl(num.ioutnum, 0) istocknum, ");
        sql.append("  nvl(num.isellnum, 0) - nvl(num.ioutnum, 0) ilocknum,  ");
        sql.append("  nvl(goodsbill.nsendnum,0) nsendnum,   ");
        sql.append("  nvl(num.istocknum-num.ioutnum- nvl(goodsbill.nsendnum,0),0) ibuynum   ");
        sql.append("  from cn_stocknum num  ");
        sql.append(" left join cn_stockin_b sib on sib.pk_goods = num.pk_goods  ");
        sql.append(" and sib.pk_goodsspec = num.pk_goodsspec    ");
        sql.append(" left join cn_goods g on num.pk_goods = g.pk_goods  ");
        sql.append(" left join cn_goodstype t on g.pk_goodstype = t.pk_goodstype    ");
        sql.append(" left join cn_goodsspec s on num.pk_goodsspec = s.pk_goodsspec  ");
        sql.append(" left join (select gsb.pk_goods, gsb.pk_goodsspec, sum(gsb.amount) nsendnum ");
        sql.append(" from cn_goodsbill gb   ");
        sql.append(" left join cn_goodsbill_b gsb on gb.pk_goodsbill =  gsb.pk_goodsbill    ");
        sql.append("  where gb.vstatus = 1  and nvl(gb.dr, 0) = 0 and nvl(gsb.dr, 0) = 0");
        sql.append("   group by gsb.pk_goods, gsb.pk_goodsspec) goodsbill on num.pk_goods = goodsbill.pk_goods  ");
        sql.append("   and num.pk_goodsspec = goodsbill.pk_goodsspec    ");
        sql.append("  where nvl(g.dr, 0) = 0    ");
        sql.append("    and nvl(num.dr, 0) = 0  ");
        sql.append("    and nvl(t.dr, 0) = 0    ");
        sql.append("    and nvl(s.dr, 0) = 0     ");
        sql.append("    and nvl(sib.dr, 0) = 0  ");
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
             sql.append(" order by t.vname,g.vgoodsname ");
             qryvo.setSql(sql.toString());
             qryvo.setSpm(spm);
            return qryvo;
    }

}
