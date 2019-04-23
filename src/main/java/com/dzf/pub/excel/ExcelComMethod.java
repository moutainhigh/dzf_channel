package com.dzf.pub.excel;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.constant.ICommonContstant;

/**
 * Excel导入公共方法
 * @author gejw
 * @time 2018年1月31日 下午5:50:24
 *
 */
public class ExcelComMethod {


    private static DecimalFormat formatter = new DecimalFormat("#.##"); // #,##0.00
    
    private ExcelComMethod() {
        throw new IllegalStateException("ExcelComMethod class");
    }
    
    private static final ThreadLocal<DateFormat> sdf = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    
    /**
     *  校验是否最新模版(操作Excel2007的版本，扩展名是.xlsx)
     * @author gejw
     * @time 下午5:50:43
     * @param rwb
     * @param STYLE
     * @param STYLE_NAME
     * @param startRow
     */
    public static void checkTemplete(XSSFWorkbook rwb,Object[][] STYLE,Object[][] STYLE_NAME,int startRow){
        String sheetName = rwb.getSheetName(ICommonContstant.START_SHEET);
        if("示范样例".equals(sheetName)){
            throw new BusinessException(ICommonContstant.WARN_INFO);
        }
        XSSFSheet sheets = rwb.getSheetAt(ICommonContstant.START_SHEET);
        XSSFRow rows=sheets.getRow(startRow-1);
        if (rows==null||rows.getPhysicalNumberOfCells()!= STYLE.length) {
            throw new BusinessException(ICommonContstant.WARN_INFO);
        }
        for(int i=0;i<STYLE_NAME.length;i++){
            if(!STYLE_NAME[i][1].toString().equals(getExcelCellValue(rows.getCell(i)))){
                throw new BusinessException(ICommonContstant.WARN_INFO);
            }
        }
        if(sheets.getLastRowNum()<startRow){
			throw new BusinessException("需要导入的数据为空");
		}
    }
    
    /**
     * 校验是否最新模版  (操作Excel2003以前（包括2003）的版本，扩展名是.xls)
     * @param rwb
     * @param STYLE
     * @param STYLE_NAME
     * @param startRow
     */
    public static void checkTemplete(HSSFWorkbook rwb,Object[][] STYLE,Object[][] STYLE_NAME,int startRow){
        String sheetName = rwb.getSheetName(ICommonContstant.START_SHEET);
        if("示范样例".equals(sheetName)){
            throw new BusinessException(ICommonContstant.WARN_INFO);
        }
        HSSFSheet sheets = rwb.getSheetAt(ICommonContstant.START_SHEET);
        HSSFRow rows=sheets.getRow(startRow-1);
        if (rows==null||rows.getPhysicalNumberOfCells()!= STYLE.length) {
            throw new BusinessException(ICommonContstant.WARN_INFO);
        }
        for(int i=0;i<STYLE_NAME.length;i++){
            if(!STYLE_NAME[i][1].toString().equals(getExcelCellValue(rows.getCell(i)))){
                throw new BusinessException(ICommonContstant.WARN_INFO);
            }
        }
        if(sheets.getLastRowNum()<startRow){
			throw new BusinessException("需要导入的数据为空");
		}
    }
    
    /**
     * 获取单元格的值（操作Excel2007的版本，扩展名是.xlsx）
     * @param cell
     * @return
     * @throws DZFWarpException
     */
    public static String getExcelCellValue(XSSFCell cell) throws DZFWarpException {
        String ret = "";
        try {
            if (cell == null) {
                ret = null;
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                ret = cell.getRichStringCellValue().getString();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    ret = formatter.format(cell.getNumericCellValue());
                } else {
                    ret = sdf.get().format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                }
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
                ret = cell.getCellFormula();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
                ret = "" + cell.getErrorCellValue();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
                ret = "" + cell.getBooleanCellValue();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                ret = null;
            }
        } catch (Exception ex) {
            throw new WiseRunException(ex);
        }
        return ret;
    }
    
    /**
     * 获取单元格的值(操作Excel2003以前（包括2003）的版本，扩展名是.xls)
     * @param cell
     * @return
     * @throws DZFWarpException
     */
    public static String getExcelCellValue(HSSFCell cell) throws DZFWarpException {
        String ret = "";
        try {
            if (cell == null) {
                ret = null;
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                ret = cell.getRichStringCellValue().getString();
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    ret = formatter.format(cell.getNumericCellValue());
                } else {
                    ret = sdf.get().format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                }
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
                ret = cell.getCellFormula();
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
                ret = "" + cell.getErrorCellValue();
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
                ret = "" + cell.getBooleanCellValue();
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                ret = null;
            }
        } catch (Exception ex) {
            throw new WiseRunException(ex);
        }
        return ret;
    }
}
