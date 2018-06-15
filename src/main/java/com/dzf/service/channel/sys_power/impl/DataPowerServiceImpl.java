package com.dzf.service.channel.sys_power.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.sys_power.DataPowerVO;
import com.dzf.model.channel.sys_power.URoleVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.sys_power.IDataPowerService;

@Service("dataPowerService")
public class DataPowerServiceImpl implements IDataPowerService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    // 只标准末级的状态即可
    private void setCheckStatus(List<URoleVO> list) {
        if (list != null && list.size() > 0) {
            for (URoleVO vo : list) {
                if (!StringUtil.isEmpty(vo.getPk_user_role())) {
                    vo.setChecked(Boolean.TRUE);
                } else {
                    vo.setChecked(Boolean.FALSE);
                }
            }
        }
    }

    @Override
    public List<URoleVO> queryRoles() throws DZFWarpException {
        StringBuffer sf = new StringBuffer();
        sf.append(" select sr.pk_role,sr.role_name from sm_role sr ");
        sf.append(" where sr.roletype =7  and nvl(sr.dr,0) = 0 and nvl(sr.seal,'N') = 'N'");
        List<URoleVO> list = (List<URoleVO>) singleObjectBO.executeQuery(sf.toString(), null,new BeanListProcessor(URoleVO.class));
        setCheckStatus(list);
        return list;
    }

    @Override
    public void save(DataPowerVO vo) throws DZFWarpException {
    	SQLParameter sp = new SQLParameter();
    	sp.addParam(vo.getPk_role());
    	String sql =" delete from cn_datapower where pk_role=?  ";
		singleObjectBO.executeUpdate(sql.toString(), sp);
    	if(vo.getIdatalevel()!=null){
    		singleObjectBO.insertVOWithPK(vo);
    	}
    }

	@Override
	public DataPowerVO queryByID(String roleid) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
    	sp.addParam(roleid);
    	DataPowerVO retvo=new DataPowerVO();
    	String sql =" select * from cn_datapower where pk_role=? ";
    	List<DataPowerVO> vos =(List<DataPowerVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DataPowerVO.class));
    	if(vos!=null && vos.size()>0){
    		retvo=vos.get(0);
    	}
    	return retvo;
	}
}