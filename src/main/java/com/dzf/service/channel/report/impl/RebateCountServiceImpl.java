package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.RebateCountVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.report.IRebateCountService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("rebateCountSer")
public class RebateCountServiceImpl implements IRebateCountService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    
    @Autowired
    private IPubService pubService;
    
    @Autowired
    private IUserService userser;
    
    @Override
    public List<RebateCountVO> query(QryParamVO paramvo) throws DZFWarpException {
    	StringBuffer sql = new StringBuffer();
    	String condition = pubService.makeCondition(paramvo.getCuserid(),paramvo.getAreaname(),IStatusConstant.IQUDAO);
    	if(condition==null){
    		return new ArrayList<RebateCountVO>();
    	}
        SQLParameter params = new SQLParameter();
        sql.append("select reb.pk_corp,");
        sql.append(" account.innercode as corpcode,");
        sql.append(" account.unitname as corpname,");
        sql.append(" account.vprovince, ");
        sql.append(" area.region_name as vprovname,");
        sql.append(" sum(decode(reb.iseason,1,reb.nrebatemny)) as nmny1,");
        sql.append(" sum(decode(reb.iseason,2,reb.nrebatemny)) as nmny2, ");
        sql.append(" sum(decode(reb.iseason,3,reb.nrebatemny)) as nmny3,");
        sql.append(" sum(decode(reb.iseason,4,reb.nrebatemny)) as nmny4");
        sql.append(" from cn_rebate reb ");
        sql.append(" join bd_account account on reb.pk_corp = account.pk_corp ");
        sql.append(" join ynt_area area on area.region_id = account.vprovince ");
        sql.append(" where reb.fathercorp = ? and reb.vyear = ? and nvl(reb.dr,0) = 0 and reb.istatus = 3");
        sql.append(" and "+QueryUtil.getWhereSql());
     	
        if(!condition.equals("alldata")){
    		sql.append(condition);
    	}
        sql.append(" group by reb.pk_corp,account.innercode,account.unitname,account.vprovince,area.region_name");
        params.addParam(paramvo.getPk_corp());
        params.addParam(paramvo.getVyear());
        List<RebateCountVO> list = (List<RebateCountVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(RebateCountVO.class));
        Map<Integer, String> areaMap = pubService.getAreaMap(paramvo.getAreaname(),1);       
        if(list != null && list.size() > 0){
            HashMap<String, String> map = queryChannelManger();
            HashMap<String, UserVO> usermap = userser.queryUserMap(paramvo.getPk_corp(), true);
            UserVO uvo = null;
            String userid = null;
            ArrayList<RebateCountVO> listFilter = new ArrayList<>();
            for(RebateCountVO rvo : list){
            	rvo.setCorpname(CodeUtils1.deCode(rvo.getCorpname()));
                userid = map.get(rvo.getPk_corp());
                if(!StringUtil.isEmpty(userid)){
                	  uvo = usermap.get(userid);
                	
                    if(uvo != null){
                        rvo.setVmanagername(uvo.getUser_name());	
                    }
                }
                if(!StringUtil.isEmpty(paramvo.getCorpkname())){
                    if(rvo.getCorpname().indexOf(paramvo.getCorpkname()) > -1){
                        listFilter.add(rvo);
                    }
                }
                if(areaMap.containsKey(rvo.getVprovince())){
                	rvo.setAreaname(areaMap.get(rvo.getVprovince()));
				}
                
            }
            if(!StringUtil.isEmpty(paramvo.getCorpkname())){
                return listFilter;
            }
        }
        return list;
    }
    
    private HashMap<String, String> queryChannelManger(){
        String condition = " nvl(dr,0) = 0 and type = 1 and pk_corp is not null";
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
