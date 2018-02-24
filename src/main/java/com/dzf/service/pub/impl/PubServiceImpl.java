package com.dzf.service.pub.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.sys.sys_set.YntArea;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.AreaCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_set.IAreaSearch;

@Service("highseascorpser")
public class PubServiceImpl implements IPubService {
	
    @Autowired
    private IAreaSearch areaService;
    
	@Autowired
	private SingleObjectBO singleObjectBO;

    @Override
	public HashMap<Integer, String> queryAreaMap(String parenter_id) throws DZFWarpException {
		HashMap<Integer, String> areamap = new HashMap<Integer, String>();
		YntArea arvo = AreaCache.getInstance().get(areaService);
		YntArea[] vos = (YntArea[]) arvo.getChildren();
		YntArea[] areaVOs = getAreas(vos, parenter_id);
		for (YntArea vo : areaVOs) {
			if(!StringUtil.isEmpty(vo.getRegion_id())){
				areamap.put(Integer.parseInt(vo.getRegion_id()), vo.getRegion_name());
			}
		}
		return areamap;
	}
    
	/**
	 * 通过id，获取对应下级地区数组
	 * @param vos
	 * @param parenter_id
	 * @return
	 */
    private YntArea[] getAreas(YntArea[] vos,String parenter_id){
        if(vos != null && vos.length > 0){
            if(vos[0].getRegion_id().equals(parenter_id)){
                return vos[0].getChildren();
            }else{
                YntArea[] vos_1 = vos[0].getChildren();
                if(vos_1 != null && vos_1.length > 0){
                    for(YntArea vo_1 : vos_1){
                        if(vo_1.getRegion_id().equals(parenter_id)){
                            return vo_1.getChildren();
                        }else{
                            YntArea[] vos_2 = vo_1.getChildren();
                            if(vos_2 != null && vos_2.length > 0){
                                for(YntArea vo_2 : vos_2){
                                    if(vo_2.getRegion_id().equals(parenter_id)){
                                        return vo_2.getChildren();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ChnAreaVO> queryLargeArea() throws DZFWarpException {
		Map<String, ChnAreaVO> lareamap = new HashMap<String, ChnAreaVO>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT b.vprovince AS coperatorid, a.areaname, a.areacode, a.userid \n");
		sql.append("  FROM cn_chnarea a \n");
		sql.append("  LEFT JOIN cn_chnarea_b b ON a.pk_chnarea = b.pk_chnarea \n");
		sql.append(" WHERE nvl(a.dr, 0) = 0 \n");
		sql.append("   AND nvl(b.dr, 0) = 0 \n");
		List<ChnAreaVO> areaVOs = (List<ChnAreaVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(ChnAreaVO.class));
		if(areaVOs != null && areaVOs.size() > 0){
			for(ChnAreaVO vo : areaVOs){
				if(!lareamap.containsKey(vo.getCoperatorid())){
					lareamap.put(vo.getCoperatorid(), vo);
				}
			}
		}
		return lareamap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String queryCode(String tablename) throws DZFWarpException {
		DZFDate date = new DZFDate();
		String year = String.valueOf(date.getYear());
		String str = year + date.getStrMonth() + date.getStrDay();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" SELECT MAX(vbillcode) FROM \n");
		sql.append(tablename);
		sql.append("  WHERE nvl(dr,0) = 0 \n");
		sql.append(" AND vbillcode LIKE ? ");
		spm.addParam(str + "____");
		ArrayList<Object> result = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm,
				new ArrayListProcessor());
		String code = "";
		if (result != null && !result.isEmpty()) {
			for (int i = 0; i < result.size(); i++) {
				Object[] obj = (Object[]) result.get(i);
				code = String.valueOf(obj[0]);
			}
		}
		if (StringUtil.isEmpty(code)) {
			return str + "0001";
		}
		int num = Integer.parseInt(code.substring(6));
		num = num + 1;
		String nums = String.valueOf(num);
		switch (nums.length()) {
		case 1:
			nums = "000" + nums;
			break;
		case 2:
			nums = "00" + nums;
			break;
		case 3:
			nums = "0" + nums;
			break;
		}
		return str + nums;
	}

}
