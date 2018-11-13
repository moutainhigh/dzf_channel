package com.dzf.action.channel.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.channel.report.RebateCountVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.report.IRebateCountService;

/**
 * 返点统计表
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "rebateCount")
public class RebateCountAction extends BaseAction<RebateCountVO> {

	private static final long serialVersionUID = 5761681623723616215L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IRebateCountService rebateCountSer;
	
	
	public void query() {
        Grid grid = new Grid();
        try {
            UserVO uservo = getLoginUserInfo();
            if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
                throw new BusinessException("登陆用户错误");
            } else if (uservo == null) {
                throw new BusinessException("登陆用户错误");
            }
            QryParamVO paramvo = new QryParamVO();
            paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
            paramvo.setPk_corp(getLogincorppk());
            List<RebateCountVO> list = rebateCountSer.query(paramvo);
            if (list != null && list.size() > 0) {
                int page = paramvo == null ? 1 : paramvo.getPage();
                int rows = paramvo == null ? 100000 : paramvo.getRows();
                RebateCountVO[] rebatVOs = (RebateCountVO[]) QueryUtil.getPagedVOs(list.toArray(new RebateCountVO[0]), page, rows);
                grid.setRows(Arrays.asList(rebatVOs));
                grid.setTotal((long) (list.size()));
            } else {
                grid.setRows(new ArrayList<RebateVO>());
                grid.setTotal(0L);
            }
            grid.setMsg("查询成功");
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, log, e, "查询失败");
        }
        writeJson(grid);
    }

	
}
