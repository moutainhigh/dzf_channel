package com.dzf.action.channel.chn_set;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.SaleSetVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.service.channel.chn_set.ISaleSetService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 
 * 销售管理 规则设置
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/chn_set")
@Action(value = "saleset")
public class SaleSetAction extends BaseAction<SaleSetVO> {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private ISaleSetService saleSet;

	@Autowired
	private IPubService pubser;

	/**
	 * 查询
	 */
	public void query() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			SaleSetVO vo = new SaleSetVO();
			vo = (SaleSetVO) DzfTypeUtils.cast(getRequest(), vo);
			SaleSetVO query = saleSet.query(vo);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setData(query);
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}

	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		try {
			if (data != null) {
				UserVO uservo = getLoginUserInfo();
				if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
					throw new BusinessException("登陆用户错误");
				} else if (uservo == null) {
					throw new BusinessException("登陆用户错误");
				}
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_20);
				String pk_corp = getLoginCorpInfo().getPk_corp();
				data.setPk_corp(pk_corp);
				if (StringUtil.isEmpty(data.getPk_saleset())) {// 新增，默认录入人与日期
					data.setDoperatedate(new DZFDate());
					data.setCoperatorid(getLoginUserInfo().getCuserid());
					data.setTs(new DZFDateTime());
				} else {// 修改，最后修改日期，修改人
					data.setLastmodifypsnid(getLoginUserInfo().getCuserid());
					data.setLastmodifydate(new DZFDateTime());
				}
				saleSet.save(data);
				if (!StringUtil.isEmpty(data.getLastmodifypsnid())) {
					UserVO uvo = UserCache.getInstance().get(data.getLastmodifypsnid(), null);
					data.setLastmodifypsn(uvo.getUser_name());
				}
				json.setData(data);
				json.setSuccess(true);
				json.setMsg("保存成功");
			}
			// 日志记录 销售管理规则设置
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_XSGZSZ.getValue(), LogRecordEnum.OPE_CHANNEL_XSGZSZ.getName(),
					ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}

	public void history() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String pk_corp = getLogincorppk();
			SaleSetVO vo = new SaleSetVO();
			vo.setPk_corp(pk_corp);
			List<SaleSetVO> list = saleSet.queryHistory(vo);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
			grid.setRows(list);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

}
