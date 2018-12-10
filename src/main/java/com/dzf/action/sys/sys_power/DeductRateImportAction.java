package com.dzf.action.sys.sys_power;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sys_power.DeductRateVO;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.ICommonContstant;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.ExcelComMethod;
import com.dzf.service.channel.sys_power.IDeductRateService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 扣款率导入Action
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "dtrateimport")
public class DeductRateImportAction extends BaseAction<DeductRateVO> {

	private static final long serialVersionUID = 95091248614523273L;

	private Logger log = Logger.getLogger(this.getClass());

	private int startRow = 1;
	
	@Autowired
	private IDeductRateService rateser;
	
	@Autowired
	private IPubService pubser;

	// 更新导入数组编码
	public static final Object[][] STYLE_1 = new Object[][] {
			{ 0, "corpcode" }, // 加盟商编码
			{ 1, "corpname" }, // 加盟商名称
			{ 2, "inewrate" }, // 新增
			{ 3, "irenewrate" }, // 续费
	};

	// 更新导入数组名称
	public static final Object[][] STYLE_1_NAME = new Object[][] { 
			{ 0, "加盟商编码" }, 
			{ 1, "加盟商名称" }, 
			{ 2, "新增" },
			{ 3, "续费" }, };

	/**
	 * 导入更新
	 */
	public void saveImport() {
		Json json = new Json();
		try {
			
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_55);
			
			((MultiPartRequestWrapper) getRequest()).getParameterMap();
			File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("impufile");
			String[] filenames = ((MultiPartRequestWrapper) getRequest()).getFileNames("impufile");
			if (infiles == null) {
				throw new BusinessException("导入文件为空");
			}
			File infile = infiles[0];
			String filename = filenames[0];
			DeductRateVO[] rateVOs = null;
			if (filename.endsWith(".xls")) {
				rateVOs = onBoUpdateImp(infile);
			} else {
				throw new BusinessException("导入文件格式不正确");
			}
			if (rateVOs == null || rateVOs.length == 0) {
				throw new BusinessException("导入文件出错");
			}
			
			int rignum = 0;
			int errnum = 0;
			List<DeductRateVO> rightlist = new ArrayList<DeductRateVO>();
			StringBuffer errmsg = new StringBuffer();
			Map<String, String> map = rateser.queryCorpMap();//加盟商信息
			for(DeductRateVO ratevo : rateVOs){
				try {
					ratevo = rateser.saveImport(ratevo, map, getLogincorppk(), getLoginUserid());
					rignum++;
					rightlist.add(ratevo);
				} catch (Exception e) {
					errnum++;
					errmsg.append(e.getMessage()).append("<br>");
				}
			}
			
			json.setSuccess(true);
			if (rignum > 0 && rignum == rateVOs.length) {
				json.setRows(rightlist);
				json.setMsg("成功" + rignum + "条");
			} else if (errnum > 0) {
				json.setMsg("成功" + rignum + "条，失败" + errnum + "条，失败原因：" + errmsg.toString());
				json.setStatus(-1);
				if (rignum > 0) {
					json.setRows(rightlist);
				}
			}
			if(rignum > 0){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_44.getValue(), "导入更新"+rignum+"条扣款率", ISysConstants.SYS_3);
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "导入失败");
		}
		writeJson(json);
	}

	/**
	 * 更新文件选择
	 * 
	 * @param infile
	 * @return
	 * @throws DZFWarpException
	 */
	private DeductRateVO[] onBoUpdateImp(File infile) throws DZFWarpException {
		FileInputStream is = null;
		try {
			is = new FileInputStream(infile);
			HSSFWorkbook rwb = new HSSFWorkbook(is);
			int sheetno = rwb.getNumberOfSheets();
			// if (sheetno == 0) {
			// throw new BusinessException("需要导入的数据为空！");
			// }
			// HSSFSheet sheets =
			// rwb.getSheetAt(ICommonContstant.START_SHEET);// 取第1个工作簿
			if (sheetno < 1) {
				throw new BusinessException(ICommonContstant.WARN_INFO);
			}
			HSSFSheet sheets = rwb.getSheetAt(ICommonContstant.START_SHEET);
			ExcelComMethod.checkTemplete(rwb, STYLE_1, STYLE_1_NAME, startRow);
			return doUpdateImport(infile, sheets);
		} catch (FileNotFoundException e2) {
			throw new BusinessException("文件未找到");
		} catch (IOException e2) {
			throw new BusinessException("文件格式不正确，请选择导入文件");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					log.error("导入文件解析错误", e);
				}
			}
		}
	}

	/**
	 * 获取更新文件数据
	 * 
	 * @param excelfile
	 * @param sheets
	 * @return
	 * @throws DZFWarpException
	 */
	private DeductRateVO[] doUpdateImport(File excelfile, HSSFSheet sheets) throws DZFWarpException {
		DeductRateVO[] vos = getUpdateDataByExcel(excelfile.getPath(), sheets);
		if (vos == null || vos.length <= 0) {
			throw new BusinessException("导入文件数据为空，请检查。");
		}
		return vos;
	}

	/**
	 * 获取更新数据
	 * 
	 * @param filepath
	 * @param sheets1
	 * @return
	 * @throws DZFWarpException
	 */
	public DeductRateVO[] getUpdateDataByExcel(String filepath, HSSFSheet sheets1) throws DZFWarpException {
		List<DeductRateVO> clist = null;
		DeductRateVO[] vos = null;
		try {
			int iBegin = 1;
			clist = new ArrayList<DeductRateVO>();
			boolean isdatarow = false;
			for (; iBegin < (sheets1.getLastRowNum() + 1); iBegin++) {
				DeductRateVO excelvo = new DeductRateVO();
				isdatarow = false;
				for (int j = 0; j < STYLE_1.length; j++) {
					HSSFCell aCell = sheets1.getRow(iBegin).getCell((new Integer(STYLE_1[j][0].toString())).intValue());
					String sTmp = ExcelComMethod.getExcelCellValue(aCell);
					if (!StringUtil.isEmptyWithTrim(sTmp)) {
						// 需要对特殊字段前台复制进行处理
						sTmp = sTmp.replaceAll(" ", "");
						excelvo.setAttributeValue(STYLE_1[j][1].toString(), sTmp);
						isdatarow = true;
					}
				}

				if (excelvo != null && isdatarow) {
					checkIsNotEmpty(excelvo, iBegin + 1);
					checkData(excelvo, iBegin + 1);
					clist.add(excelvo);
				}
			}
			if (clist.size() > 0) {
				vos = clist.toArray(new DeductRateVO[1]);
			}
		} catch (BusinessException e) {
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			throw new BusinessException("解析Excel失败");
		}
		return vos;
	}
	
	/**
	 * 最大最小值校验
	 * @param excelvo
	 * @param iBegin
	 * @throws DZFWarpException
	 */
	private void checkData(DeductRateVO excelvo, int iBegin) throws DZFWarpException {
		if(excelvo.getInewrate().compareTo(0) < 0){
			throw new BusinessException("第" + iBegin + "行新增不能小于0");
		}
		if(excelvo.getInewrate().compareTo(100) > 0){
			throw new BusinessException("第" + iBegin + "行新增不能大于100");
		}
		if(excelvo.getIrenewrate().compareTo(0) < 0){
			throw new BusinessException("第" + iBegin + "行续费不能小于0");
		}
		if(excelvo.getIrenewrate().compareTo(100) > 0){
			throw new BusinessException("第" + iBegin + "行续费不能大于100");
		}
	}

	/**
	 * 非空字段校验
	 * 
	 * @param sTmp
	 * @param code
	 */
	private void checkIsNotEmpty(DeductRateVO excelvo, int iBegin) {
		if (StringUtil.isEmptyWithTrim(excelvo.getCorpcode())) {
			throw new BusinessException("第" + iBegin + "行加盟商编码不能为空");
		}
		if (StringUtil.isEmptyWithTrim(excelvo.getCorpname())) {
			throw new BusinessException("第" + iBegin + "行加盟商名称不能为空");
		}
		if (excelvo.getInewrate() == null) {
			throw new BusinessException("第" + iBegin + "行新增不能为空");
		}
		if (excelvo.getIrenewrate() == null) {
			throw new BusinessException("第" + iBegin + "行续费不能为空");
		}
	}

	/**
	 * 登录用户校验
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
			throw new BusinessException("登陆用户错误");
		}else if(uservo == null){
			throw new BusinessException("登陆用户错误");
		}
	}
	
}
