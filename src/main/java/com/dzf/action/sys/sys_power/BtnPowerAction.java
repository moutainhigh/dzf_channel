package com.dzf.action.sys.sys_power;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sys_power.URoleVO;
import com.dzf.model.pub.Grid;
import com.dzf.service.pub.IPubService;

@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "btnPower")
public class BtnPowerAction extends BaseAction<URoleVO> {

    private Logger log = Logger.getLogger(this.getClass());
    @Autowired
    private IPubService pubservice;

    public void checkBtnPower() {
        Grid grid = new Grid();
        try {
            String btncode = getRequest().getParameter("btncode");
            String funnode = getRequest().getParameter("funnode");
            pubservice.checkButton(getLoginUserInfo(), funnode, btncode);
            grid.setSuccess(true);
            grid.setMsg("校验通过");
            grid.setRows(null);
        } catch (Exception e) {
            printErrorLog(grid, log, e,e.getMessage());
            grid.setSuccess(false);
            grid.setMsg(e.getMessage());
            grid.setRows(null);
        }
        writeJson(grid);
    }
}