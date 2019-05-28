package com.dzf.action.channel.report;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.Grid;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.service.channel.report.IChannelManService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 加盟商管理
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "channelman")
public class ChannelManAction extends BaseAction<ManagerVO>{

	@Autowired
	private IChannelManService channel;
	

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询主表数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			ManagerVO qvo = new ManagerVO();
			qvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setUserid(getLoginUserid());
			List<ManagerVO> vos = channel.query(qvo);
			if(vos==null||vos.size()==0){
				grid.setRows(new ArrayList<ManagerVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_SSJFX.getValue(), "省数据分析查询成功", ISysConstants.SYS_3);
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

}
