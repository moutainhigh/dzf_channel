package com.dzf.action.channel.matmanage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.ICommonContstant;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.ExcelComMethod;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.matmanage.IMatApplyService;
import com.dzf.service.channel.matmanage.IMatHandleService;
import com.dzf.service.pub.IPubService;

/**
 * 物料档案
 */
@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "mathandle")
public class MatHandleAction extends BaseAction<MatOrderVO> {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IMatHandleService mathandle;

	@Autowired
	private IMatApplyService matapply;

	@Autowired
	private IPubService pubser;

	private int startRow = 1;

	private Object[][] STYLE_1 = new Object[][] { { 0, "vmanagername" }, { 1, "corpname" }, { 2, "" }, // 代理记账服务合同
			{ 3, "" }, // 加盟合同
			{ 4, "" }, // 易拉宝
			{ 5, "" }, // 手提袋
			{ 6, "" }, // 宣传单页
			{ 7, "" }, // 工牌
			{ 8, "" }, // 文件
			{ 9, "" }, // 授权书
			{ 10, "logname" }, // 快递公司
			{ 11, "fastcost" }, // 金额
			{ 12, "fastcode" }, // 单号
			{ 13, "deliverdate" }, // 发货时间
			{ 14, "vreceiver" }, // 收货人
			{ 15, "phone" }, // 联系电话
			{ 16, "vaddress" }, // 地址
			{ 17, "vmemo" }, // 备注
	};
	private Object[][] STYLE_NAME_1 = new Object[][] { { 0, "渠道经理" }, { 1, "加盟商" }, { 2, "代理记账服务合同" }, { 3, "加盟合同	" },
			{ 4, "易拉宝" }, { 5, "手提袋" }, { 6, "宣传单页" }, { 7, "工牌" }, { 8, "文件" }, { 9, "授权书/牌" }, { 10, "快递公司" },
			{ 11, "金额" }, { 12, "单号" }, { 13, "发货时间" }, { 14, "收货人" }, { 15, "联系电话" }, { 16, "地址" }, { 17, "备注" }, };

	/**
	 * 查询数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			List<MatOrderVO> mList = new ArrayList<MatOrderVO>();
			String stype = getRequest().getParameter("stype");

			MatOrderVO pamvo = new MatOrderVO();
			QryParamVO qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			pamvo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), pamvo);
			pamvo.setPk_corp(getLogincorppk());

			String powSql = pubser.makeCondition(uservo.getCuserid(), pamvo.getAreaname(), 1);
			if (powSql != null && !powSql.equals("alldata")) {
				qvo.setVqrysql(powSql);
				mList = mathandle.query(qvo, pamvo, uservo);
			} else if (powSql != null) {
				mList = mathandle.query(qvo, pamvo, uservo);
			}

			grid.setRows(mList);
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			grid.setMsg("查询失败");
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询快递公司下拉
	 */
	public void queryComboBox() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			List<MatOrderVO> list = mathandle.queryComboBox();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<MatOrderVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 发货保存、修改保存
	 */
	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			String stype = getRequest().getParameter("stype");
			String type = getRequest().getParameter("type");
			
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_68);
			MatOrderVO vo = new MatOrderVO();
			vo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), vo);

			Map<String, String> bmapping = FieldMapping.getFieldMapping(new MatOrderBVO());
			String body = getRequest().getParameter("body"); // 物料数据
			String message = "";
			if (body != null) {
				body = body.replace("}{", "},{");
				body = "[" + body + "]";
				JSONArray bodyarray = (JSONArray) JSON.parseArray(body);
				MatOrderBVO[] bodyVOs = DzfTypeUtils.cast(bodyarray, bmapping, MatOrderBVO[].class,
						JSONConvtoJAVA.getParserConfig());

				if (bodyVOs == null || bodyVOs.length == 0) {
					throw new BusinessException("物料数据不能为空");
				}
				message = mathandle.saveApply(vo, uservo, bodyVOs, stype,type);
			} else {
				matapply.editSave(vo);
			}

			if (!StringUtil.isEmpty(message)) {// 需要提示信息
				json.setMsg("提示");
				json.setRows(message);
			} else {
				json.setMsg("保存成功");
				json.setSuccess(true);
			}
		} catch (Exception e) {
			json.setMsg("保存失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}

	
	/**
	 * 编辑回显
	 */
	public void queryById() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			MatOrderVO vo = new MatOrderVO();
			vo = (MatOrderVO) DzfTypeUtils.cast(getRequest(), vo);
			String id = getRequest().getParameter("id");
			String stype = getRequest().getParameter("stype");
			if (!StringUtil.isEmpty(id)) {
				vo = mathandle.queryDataById(vo, id, uservo,stype);
			}
			if (!StringUtil.isEmpty(vo.getMessage())) {
				json.setMsg("提示");
				json.setRows(vo);
			} else {
				json.setRows(vo);
				json.setMsg("查询成功");
				json.setSuccess(true);
			}
		} catch (Exception e) {
			json.setMsg("查询失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);

	}
	
	/**
	 * 处理单导入
	 */
	public void matImport() {
		Json json = new Json();
		MatOrderVO[] vos = null;
		FileInputStream is = null;

		try {
			UserVO uservo = getLoginUserInfo();
			MultiPartRequestWrapper req = ((MultiPartRequestWrapper) getRequest());
			File[] infiles = req.getFiles("file");
			String[] filenames = req.getFileNames("file");
			if (infiles == null) {
				throw new BusinessException("导入文件为空");
			}
			File infile = infiles[0];
			String filename = filenames[0];
			if (!filename.endsWith(".xlsx")) {
				throw new BusinessException("导入文件格式不正确");
			}
			is = new FileInputStream(infile);
			XSSFWorkbook rwb = new XSSFWorkbook(is);
			int sheetno = rwb.getNumberOfSheets();
			if (sheetno < 1) {
				throw new BusinessException(ICommonContstant.WARN_INFO);
			}
			XSSFSheet sheets = rwb.getSheetAt(ICommonContstant.START_SHEET);
			// ExcelComMethod.checkTemplete(rwb, STYLE_1,
			// STYLE_NAME_1,startRow);

			List<MatOrderVO> clist = new ArrayList<MatOrderVO>();
			Integer counter = 0;
			String corpname = "";
			String managname = "";
			String date = "";
			String logname = "";
			Integer num = 0;
			// Integer matnum = 0;
			for (int iBegin = startRow; iBegin < (sheets.getLastRowNum() + 1); iBegin++) {
				MatOrderVO excelvo = new MatOrderVO();
				List<MatOrderBVO> blist = new ArrayList<MatOrderBVO>();
				num = 0;
				List<String> list = new ArrayList<String>();
				for (int j = 0; j < STYLE_1.length; j++) {
					XSSFRow xssFRow = sheets.getRow(iBegin);
					XSSFCell aCell = xssFRow.getCell(Integer.parseInt(STYLE_1[j][0].toString()));
					String sTmp = ExcelComMethod.getExcelCellValue(aCell);
					if (!StringUtil.isEmpty(sTmp)) {
						if (j == 1) {// 加盟商
							MatOrderVO vo = mathandle.queryIsExist(sTmp);
							if (vo == null) {
								throw new BusinessException(
										"第" + (iBegin + 1) + "行" + STYLE_NAME_1[j][1].toString() + "不存在！");
							} else {
								corpname = sTmp;
							}
						}
						if (j == 0) {// 渠道经理
							managname = sTmp;
						}
						if (j == 10) {// 快递公司
							logname = sTmp;
						}

						if (j == 13) {// 发货时间
							try {
								new DZFDate(sTmp);
								date = sTmp.replaceAll(" ", "");
							} catch (IllegalArgumentException e) {
								throw new BusinessException(
										"第" + (iBegin + 1) + "行" + STYLE_NAME_1[j][1].toString() + "错误！");
							}
						}
						// 物料
						if (j == 2 || j == 3 || j == 4 || j == 5 || j == 6 || j == 7 || j == 8 || j == 9) {
							list = mathandle.queryAllMatName();
							if (list.contains(STYLE_NAME_1[j][1].toString().trim())) {
								num++;// 有值的物料
							}
							counter++;
							MatOrderBVO bvo = new MatOrderBVO();
							bvo.setOutnum(Integer.parseInt(sTmp.trim()));
							bvo.setVname(STYLE_NAME_1[j][1].toString().trim());
							blist.add(bvo);
						}
						excelvo.setAttributeValue(STYLE_1[j][1].toString(), sTmp.replaceAll(" ", ""));
					}
				}
				if (num != 0) {// 至少有一个物料有值
					MatOrderBVO[] bvos = blist.toArray(new MatOrderBVO[0]);
					excelvo.setChildren(bvos);
					excelvo = mathandle.getFullVO(excelvo, corpname, managname, date, logname, uservo);
					clist.add(excelvo);
				}

			}

			if (clist.size() > 0) {
				vos = clist.toArray(new MatOrderVO[0]);
				vos = mathandle.saveImoprt(vos);
			}
			json.setRows(vos);
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "导入失败");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("关闭流失败", e);
				}
			}
		}
		writeJson(json);
	}

	/**
	 * 登录用户校验
	 * 
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误！");
		} else if (uservo == null) {
			throw new BusinessException("请先登录！");
		}
	}
}
