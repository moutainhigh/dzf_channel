package com.dzf.action.channel.dealmanage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.model.channel.dealmanage.GoodsDocVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.channel.dealmanage.MeasVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.dealmanage.IGoodsManageService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.spring.SpringUtils;

/**
 * 商品管理
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "goodsmanage")
public class GoodsManageAction extends BaseAction<GoodsVO> {

	private static final long serialVersionUID = 3652362465205566316L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IGoodsManageService manser;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			GoodsVO paramvo = (GoodsVO) DzfTypeUtils.cast(getRequest(), new GoodsVO());
			int total = manser.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<GoodsVO> clist = manser.query(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<GoodsVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			File[] files = ((MultiPartRequestWrapper) getRequest()).getFiles("imageFile");
			String[] filenames = ((MultiPartRequestWrapper) getRequest()).getFileNames("imageFile");
			if (StringUtil.isEmpty(data.getPk_goods()) && (files == null || files.length == 0)) {
				throw new BusinessException("商品图片不能为空");
			}
			if (StringUtil.isEmpty(data.getPk_corp())) {
				data.setPk_corp(getLogincorppk());
			}
			String operat = "";
			if(!StringUtil.isEmpty(data.getPk_goods())){
				operat = "edit";
			}
			setDefaultValue(data);
			GoodsVO retvo = manser.save(data, files, filenames);
			json.setSuccess(true);
			json.setRows(retvo);
			json.setMsg("保存成功");
			if(operat == "edit"){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_42.getValue(), "编辑商品“"+data.getVgoodscode()+"”", ISysConstants.SYS_3);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 设置默认值
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(GoodsVO data) throws DZFWarpException {
		if(StringUtil.isEmpty(data.getPk_goods())){
			data.setCoperatorid(getLoginUserid());
			data.setDoperatedate(new DZFDate());
			data.setVstatus(IStatusConstant.IGOODSSTATUS_1);
		}
	}
	
	/**
	 * 初始化
	 */
	public void initMeasCombox() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			List<ComboBoxVO> list = manser.queryMeasCombox(getLogincorppk());
			grid.setMsg("查询成功");
			grid.setSuccess(true);
			grid.setRows(list);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 保存计量单位
	 */
	public void saveMeas() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			data.setPk_corp(getLogincorppk());
			data.setCoperatorid(getLoginUserid());
			MeasVO measvo = manser.saveMeas(data);
			json.setSuccess(true);
			json.setRows(measvo);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 通过主键查询商品信息
	 */
	public void queryByID() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			GoodsVO pamvo = new GoodsVO();
			pamvo = (GoodsVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_goods())) {
				throw new BusinessException("主键为空");
			}
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			String type = getRequest().getParameter("type");
			Integer itype = Integer.parseInt(type);
			GoodsVO retvo = manser.queryByID(pamvo,itype);
			json.setSuccess(true);
			json.setRows(retvo);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	/**
	 * 获取商品图片信息
	 */
	public void getAttaches() {
		Json json = new Json();
		json.setSuccess(false);
		try {
			GoodsVO pamvo = new GoodsVO();
			pamvo = (GoodsVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			GoodsDocVO[] resvos = manser.getAttatches(pamvo);
			if (resvos != null) {
				json.setRows(Arrays.asList(resvos));
			} else {
				json.setRows(new GoodsDocVO[0]);
			}
			json.setSuccess(true);
			json.setMsg("获取商品图片成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "获取商品图片失败");
		}
		writeJson(json);
	}

	/**
	 * 获取商品图片信息
	 */
	public void getAttachImage() {
		GoodsDocVO pamvo = new GoodsDocVO();
		pamvo = (GoodsDocVO) DzfTypeUtils.cast(getRequest(), pamvo);
		if (StringUtil.isEmpty(pamvo.getPk_corp())) {
			pamvo.setPk_corp(getLogincorppk());
		}
		GoodsDocVO docvo = manser.queryGoodsDocById(pamvo);
		if (docvo == null) {
			throw new BusinessException("商品图片信息错误");
		}
		if (StringUtil.isEmpty(docvo.getVfilepath())) {
			throw new BusinessException("商品图片路径错误");
		}
		OutputStream output = null;
		try {
			output = getResponse().getOutputStream();
			byte[] bytes = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).downFile(docvo.getVfilepath());
			output.write(bytes);
			output.flush();
		} catch (Exception e) {
			com.dzf.pub.Logger.error(this, e.getMessage(), e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				com.dzf.pub.Logger.error(this, e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 删除图片
	 */
	public void deleteFile() {
		Json json = new Json();
		json.setSuccess(false);
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);
			GoodsDocVO pamvo = new GoodsDocVO();
			pamvo = (GoodsDocVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			manser.deleteFile(pamvo);
			json.setSuccess(true);
			json.setMsg("删除商品图片成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除商品图片失败");
		}
		writeJson(json);
	}
	
	/**
	 * 删除商品
	 */
	public void delete() {
		Json json = new Json();
		json.setSuccess(false);
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);
			GoodsVO pamvo = new GoodsVO();
			pamvo = (GoodsVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (StringUtil.isEmpty(pamvo.getPk_corp())) {
				pamvo.setPk_corp(getLogincorppk());
			}
			manser.delete(pamvo);
			json.setSuccess(true);
			json.setMsg("删除商品成功");
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_42.getValue(), "删除商品“"+pamvo.getVgoodscode()
				+pamvo.getVgoodsname()+"”", ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除商品失败");
		}
		writeJson(json);
	}
	
	/**
	 * 操作数据
	 */
	public void updateData() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_44);
			String data = getRequest().getParameter("data");
			if (StringUtil.isEmpty(data)) {
				throw new BusinessException("数据不能为空");
			}
			String type = getRequest().getParameter("type");
			Integer itype = Integer.parseInt(type);
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> custmaping = FieldMapping.getFieldMapping(new GoodsVO());
			GoodsVO[] goodsVOs = DzfTypeUtils.cast(arrayJson, custmaping, GoodsVO[].class,
					JSONConvtoJAVA.getParserConfig());
			List<GoodsVO> rightlist = new ArrayList<GoodsVO>();
			int rignum = 0;
			int errnum = 0;
			StringBuffer errmsg = new StringBuffer();
			if (goodsVOs != null && goodsVOs.length > 0) {
				for (GoodsVO vo : goodsVOs) {
					try {
						vo = manser.updateData(vo, itype);
						rightlist.add(vo);
						rignum++;
					} catch (Exception e) {
						errnum++;
						errmsg.append(e.getMessage()).append("<br>");
					}
				}
			}
			json.setSuccess(true);
			if (rignum > 0 && rignum == goodsVOs.length) {
				json.setRows(Arrays.asList(goodsVOs));
				json.setMsg("成功" + rignum + "条");
			} else if (errnum > 0) {
				json.setMsg("成功" + rignum + "条，失败" + errnum + "条，失败原因：" + errmsg.toString());
				json.setStatus(-1);
				if (rignum > 0) {
					json.setRows(rightlist);
				}
			}
			if (rignum > 0) {
				if (itype == 1) {// 发布
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_42.getValue(), "发布商品", ISysConstants.SYS_3);
				} else if (itype == 2) {// 下架
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_42.getValue(), "下架商品", ISysConstants.SYS_3);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 查询商品下拉
	 */
	public void queryComboBox() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			List<ComboBoxVO> vos = manser.queryComboBox();
			if (vos == null || vos.size() == 0) {
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
}
