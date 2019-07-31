package com.dzf.service.channel.dealmanage.impl;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.dealmanage.GoodsSpecVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.dealmanage.IGoodsLookService;
import com.sun.tools.jdi.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("lookgoods")
public class GoodsLookServiceImpl implements IGoodsLookService {

	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO= null;
	

	@Override
	public Integer queryTotalRow(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo,false);
		return multBodyObjectBO.queryDataTotal(GoodsVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsVO> query(GoodsVO pamvo) throws DZFWarpException {
//		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo,true);
//		List<GoodsVO> list = (List<GoodsVO>) multBodyObjectBO.queryDataPage(GoodsVO.class, 
//				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select s.pk_goods, ");
		sql.append("       s.vgoodsname, ");
		sql.append("       s.updatets, ");
		sql.append("       s.vmeasname, ");
		sql.append("       c.vfilepath, ");
		sql.append("       (case ");
		sql.append("         when pa.nnum is null then ");
		sql.append("          gs.nprice ");
		sql.append("         else ");
		sql.append("          pa.nnum * nvl(gs.nprice, 0) ");
		sql.append("       end) nprice ");
		sql.append("  from cn_goods s ");
		sql.append(" inner join cn_goodsdoc c on s.pk_goods = c.pk_goods ");
		sql.append("                         and nvl(c.isfirst, 'N') = 'Y' ");
		sql.append("  left join cn_goodsspec gs on s.pk_goods = gs.pk_goods ");
		sql.append("  left join cn_stocknum sn on sn.pk_goodsspec = gs.pk_goodsspec ");
		sql.append("  left join cn_goodspackage pa on pa.pk_goodsspec = gs.pk_goodsspec ");
		sql.append("                              and nvl(sn.dr, 0) = nvl(pa.dr, 0) ");
		sql.append("                              and pa.vstatus = 2 ");
		sql.append(" where nvl(s.dr, 0) = 0 ");
		sql.append("   and nvl(gs.dr, 0) = 0 ");
		sql.append("   and nvl(sn.dr, 0) = 0 ");
		sql.append("   and nvl(pa.dr, 0) = 0 ");
		sql.append("   and s.vstatus = 2 ");
		sql.append("   and ((pa.vstatus = 2 and ");
		sql.append("        (nvl(sn.istocknum, 0) - nvl(sn.isellnum, 0)) > pa.nnum) or ");
		sql.append("        (nvl(pa.vstatus, 0) != 2 and ");
		sql.append("        nvl(sn.istocknum, 0) > nvl(sn.isellnum, 0))) ");
		sql.append(" ORDER BY s.updatets DESC ");
		List<GoodsVO> list = (List<GoodsVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(GoodsVO.class));
		LinkedHashMap map = new LinkedHashMap();
		GoodsVO getvo = new GoodsVO();
		for (GoodsVO goodsVO : list) {
        	if(map.containsKey(goodsVO.getPk_goods())){
        		getvo = (GoodsVO) map.get(goodsVO.getPk_goods());
        		if(getvo.getNprice().compareTo(goodsVO.getNprice()) > 0){
        			map.put(goodsVO.getPk_goods(), goodsVO);
        		}
        	}else{
        		map.put(goodsVO.getPk_goods(), goodsVO);
        	}
		}
		return new ArrayList<>(map.values());
	}
	
	@Override
	public GoodsVO queryByID(String gid) throws DZFWarpException {
		GoodsVO oldvo = (GoodsVO) singleObjectBO.queryByPrimaryKey(GoodsVO.class, gid);
		if(oldvo == null){
			throw new BusinessException("很抱歉，没有此商品；请重新刷新页面!");
		}else if(oldvo.getVstatus()!=2){
			throw new BusinessException("很抱歉，该商品已下架；请重新刷新页面!");
		}
		
		String str="SELECT vfilepath FROM cn_goodsdoc where nvl(dr,0)= 0 and pk_goods=? order by doctime";
		SQLParameter spm = new SQLParameter();
		spm.addParam(gid);
		List<GoodsVO> docvos = (List<GoodsVO>) singleObjectBO.executeQuery(str,spm,new BeanListProcessor(GoodsVO.class));
		if(docvos!=null && docvos.size()>0){
			oldvo.setChildren(docvos.toArray(new GoodsVO[docvos.size()]));
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select gs.nprice, ");
		sql.append("   (case nvl(pa.vstatus,0)  when 2 then pa.updatets else gs.updatets end ) updatets, ");
		sql.append("   nvl(sn.istocknum,0) - nvl(sn.isellnum, 0)as istocknum, ");
		sql.append("   gs.invspec,gs.invtype, gs.pk_goodsspec,");
		sql.append("   pa.vmeasname,pa.nnum ,pa.pk_goodspackage ");
		sql.append("   from cn_stocknum sn ");
		sql.append("   left join cn_goodsspec gs on sn.pk_goodsspec = gs.pk_goodsspec ");
		sql.append("   left join cn_goodspackage pa on pa.pk_goodsspec = gs.pk_goodsspec and nvl(sn.dr,0)=nvl(pa.dr,0) and pa.vstatus=2");
		sql.append("  where nvl(sn.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and ( (pa.vstatus = 2 and (nvl(sn.istocknum, 0) - nvl(sn.isellnum, 0))>pa.nnum )");
		sql.append("    or ( nvl(pa.vstatus,0)!=2  and nvl(sn.istocknum, 0) > nvl(sn.isellnum, 0) ) ) ");
		sql.append("    and sn.pk_goods = ? ");
		sql.append(" order by gs.ts  ");
		List<GoodsSpecVO> numvos = (List<GoodsSpecVO>) singleObjectBO.executeQuery(sql.toString(),spm,new BeanListProcessor(GoodsSpecVO.class));
		if(numvos!=null && numvos.size()>0){
			for (GoodsSpecVO goodsSpecVO : numvos) {
				if(goodsSpecVO.getNnum()!=null && goodsSpecVO.getNnum()!=0){
					goodsSpecVO.setIstocknum(goodsSpecVO.getIstocknum()/goodsSpecVO.getNnum());
					goodsSpecVO.setNprice(goodsSpecVO.getNprice().multiply(goodsSpecVO.getNnum()));
				}
			}
			oldvo.setBodys(numvos.toArray(new GoodsSpecVO[numvos.size()]));
		}
		return oldvo;
	}

	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(GoodsVO pamvo, boolean isDetail) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select s.pk_goods");
		sql.append("       ,s.vgoodsname");
		sql.append("       ,s.updatets");
		sql.append("       ,s.vmeasname");
		if(isDetail){
			sql.append("   ,sel.nprice");
			sql.append("   ,c.vfilepath");
		}
		sql.append("  from cn_goods s");
		if(isDetail){
			sql.append(" left join cn_goodsdoc c on s.pk_goods = c.pk_goods and nvl(c.isfirst,'N')='Y' ");
			sql.append(" inner join (select min(sp.nprice) nprice, sp.pk_goods from cn_goodsspec sp ");
			sql.append("  	inner join cn_stocknum sn on sn.pk_goodsspec = sp.pk_goodsspec");
			sql.append("  	where sn.istocknum > nvl(sn.isellnum, 0) group by sp.pk_goods)sel ");
			sql.append("  on s.pk_goods = sel.pk_goods ");
		}
		sql.append("    where vstatus = ? ");
		sql.append(" ORDER BY s.updatets DESC");
		spm.addParam(2);//已保存
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	

}
