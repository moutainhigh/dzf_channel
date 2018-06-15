package com.dzf.action.sys.sys_power;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sys_power.DataPowerVO;
import com.dzf.model.channel.sys_power.URoleVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.pub.BusinessException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.sys_power.IDataPowerService;

@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "dataPower")
public class DataPowerAction extends BaseAction<DataPowerVO> {

    private Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    private IDataPowerService dataPower;

	public void queryByID() {
		Json json = new Json();
		try {
			String roleid = getRequest().getParameter("roleid");
			if (StringUtil.isEmpty(roleid)) {
				throw new BusinessException("请选择角色");
			}
			DataPowerVO vo = dataPower.queryByID(roleid);
			json.setSuccess(true);
			json.setRows(vo);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
   public void queryRoles() {
        Grid grid = new Grid();
        try {
            List<URoleVO> list = dataPower.queryRoles();
    		if(list==null||list.size()==0){
				grid.setRows(new ArrayList<URoleVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
        } catch (Exception e) {
            printErrorLog(grid, log, e, "查询失败!");
        }
        writeJson(grid);
    }
    
    public void save() {
    	Json json = new Json();
        if (data != null) {
            try {
            	data.setCoperatorid(getLoginUserid());
            	data.setDoperatedate(new DZFDate());
            	data.setPk_corp(getLogincorppk());
            	dataPower.save(data);
            	json.setRows(data);
            	json.setSuccess(true);
            	json.setMsg("保存成功!");
            } catch (Exception e) {
                printErrorLog(json, log, e, "保存失败!");
            }
        } else {
        	json.setSuccess(false);
        	json.setMsg("保存失败");
		}
        writeJson(json);
    }

}