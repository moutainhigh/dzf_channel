package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.RebateCountVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.UserCache;
import com.dzf.service.channel.report.IRebateCountService;

@Service("rebateCountSer")
public class RebateCountServiceImpl implements IRebateCountService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Override
    public List<RebateCountVO> query(QryParamVO paramvo) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter params = new SQLParameter();
        sql.append("select reb.pk_corp,");
        sql.append(" acc.innercode as corpcode,");
        sql.append(" acc.unitname as corpname,");
        sql.append(" area.region_name as vprovname,");
        sql.append(" sum(decode(reb.iseason,1,reb.nrebatemny)) as nmny1,");
        sql.append(" sum(decode(reb.iseason,2,reb.nrebatemny)) as nmny2, ");
        sql.append(" sum(decode(reb.iseason,3,reb.nrebatemny)) as nmny3,");
        sql.append(" sum(decode(reb.iseason,4,reb.nrebatemny)) as nmny4");
        sql.append(" from cn_rebate reb ");
        sql.append(" join bd_account acc on reb.pk_corp = acc.pk_corp ");
        sql.append(" join ynt_area area on area.region_id = acc.vprovince ");
        sql.append(" where reb.fathercorp = ? and reb.vyear = ? and nvl(reb.dr,0) = 0 and reb.istatus = 3");
        sql.append(" group by reb.pk_corp,acc.innercode,acc.unitname,area.region_name");
        params.addParam(paramvo.getPk_corp());
        params.addParam(paramvo.getVyear());
        List<RebateCountVO> list = (List<RebateCountVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(RebateCountVO.class));
        if(list != null && list.size() > 0){
            QueryDeCodeUtils.decKeyUtils(new String[]{"corpname"}, list, 1);
            HashMap<String, String> map = queryChannelManger();
            UserVO uvo = null;
            String userid = null;
            ArrayList<RebateCountVO> listFilter = new ArrayList<>();
            for(RebateCountVO rvo : list){
                userid = map.get(rvo.getPk_corp());
                if(!StringUtil.isEmpty(userid)){
                    uvo = UserCache.getInstance().get(userid, null);
                    if(uvo != null){
                        rvo.setVmanagername(uvo.getUser_name());
                    }
                }
                if(!StringUtil.isEmpty(paramvo.getCorpkname())){
                    if(rvo.getCorpname().indexOf(paramvo.getCorpkname()) > -1){
                        listFilter.add(rvo);
                    }
                }
                
            }
            if(listFilter != null && listFilter.size() > 0){
                return listFilter;
            }
        }
        return list;
    }
    
    private HashMap<String, String> queryChannelManger(){
        String condition = " nvl(dr,0) = 0 and pk_corp is not null";
        ChnAreaBVO[] vos = (ChnAreaBVO[]) singleObjectBO.queryByCondition(ChnAreaBVO.class, condition, null);
        HashMap<String, String> map = new HashMap<>();
        if(vos != null && vos.length > 0){
            for(ChnAreaBVO vo : vos){
                if(!map.containsKey(vo.getPk_corp())){
                    map.put(vo.getPk_corp(), vo.getUserid());
                }
            }
        }
        return map;
    }

}
