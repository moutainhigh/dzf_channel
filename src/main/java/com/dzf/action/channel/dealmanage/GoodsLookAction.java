package com.dzf.action.channel.dealmanage;

import com.dzf.action.pub.BaseAction;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.dealmanage.IGoodsLookService;
import com.dzf.spring.SpringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 商品购买
 * 
 */
@ParentPackage("basePackage")
@SuppressWarnings({ "serial" })
@Namespace("/dealmanage")
@Action(value = "goodslook")
public class GoodsLookAction extends BaseAction<GoodsVO> {

	@Autowired
	private IGoodsLookService goodsLook;

	private Logger log = Logger.getLogger(this.getClass());

	public void queryGoods() {
		Grid grid = new Grid();
		try {
			GoodsVO paramvo = new GoodsVO();
			paramvo = (GoodsVO) DzfTypeUtils.cast(getRequest(), paramvo);
			paramvo.setPage(1);
			paramvo.setRows(100000);
//			int total= goodsBuy.queryTotalRow(paramvo);
//			grid.setTotal((long)total);
//			if(total > 0){
				List<GoodsVO> clist = goodsLook.query(paramvo);
				grid.setRows(clist);
				grid.setMsg("查询成功!");
//			}else{
//				grid.setRows(new ArrayList<GoodsVO>());
//				grid.setMsg("查询数据为空!");
//			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 通过主键查询商品信息
	 */
	public void queryByID() {
		Json json = new Json();
		try {
			String gid = getRequest().getParameter("gid");
			if (StringUtil.isEmpty(gid)) {
				throw new BusinessException("请选择一个商品");
			}
			GoodsVO retvo = goodsLook.queryByID(gid);
			json.setSuccess(true);
			json.setRows(retvo);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	public void getImage(){
    	String imgpath = getRequest().getParameter("imgpath");
		//从文件服务器下载图片
		OutputStream output = null;
		if (StringUtil.isEmptyWithTrim(imgpath)) {
			throw new BusinessException("下载文件传入参数不能为空！");
		}
		if(imgpath.startsWith("/")){
			imgpath = imgpath.substring(1);
		}
		try {
			output = getResponse().getOutputStream();
			byte[] bytes = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).downFile(imgpath);
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


}
