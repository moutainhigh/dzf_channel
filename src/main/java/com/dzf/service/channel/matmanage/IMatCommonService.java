package com.dzf.service.channel.matmanage;

import java.text.ParseException;
import java.util.List;

import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDateTime;

public interface IMatCommonService {
 
	/**
	 * 物料审核过滤 大区负责人只能看见自己负责的加盟商
	 * @param vcorp 
	 * @param vpro 
	 */
	public List<ChnAreaBVO> queryPro(UserVO uservo, String stype, String vpro, String vcorp) throws DZFWarpException;

	
	/**
	 * 下拉查询所有物料档案
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	public List<MaterielFileVO> queryMatFile(MaterielFileVO pamvo, UserVO uservo) throws DZFWarpException ;
	
	
	/**
	 * 查询申请的物料信息
	 * @param pamvo
	 * @return
	 */
	List<MatOrderBVO> queryNumber(MatOrderVO pamvo) throws DZFWarpException;
	
	/**
	 * 没有申请，查询所有已经启用的物料
	 * @return
	 */
	public List<MaterielFileVO> queryMat() throws DZFWarpException;
	
	/**
	 * 查询所有的省份
	 * @return
	 */
	public List<MatOrderVO> queryAllProvince() throws DZFWarpException;
	
	/**
	 * 查询省份下的市
	 * @param pid
	 * @return
	 */
	public List<MatOrderVO> queryCityByProId(Integer pid) throws DZFWarpException;
	
	/**
	 * 查询市下的县
	 * @param cid
	 * @return
	 */
	public List<MatOrderVO> queryAreaByCid(Integer cid) throws DZFWarpException;

	public MatOrderVO queryById(String pk_materielbill) throws DZFWarpException;
	
	public String checkIsInfo(MatOrderVO vo, MatOrderBVO[] bvos, String message) throws DZFWarpException ;

	/**
	 * 判断是否能发货
	 * @param id
	 * @param outnum
	 * @return
	 * @throws DZFWarpException
	 */
	public String checkIsApply(String id, Integer outnum) throws DZFWarpException ;
	
	
	
	/**
	 * 获取上个季度时间 
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws ParseException
	 * @throws DZFWarpException
	 */
	public String getLastQuarter(String startdate, String enddate) throws ParseException, DZFWarpException;


	/**
	 * 查询合同提单量
	 * @param vo
	 * @param fathercorp
	 * @return
	 */
	public Integer queryContNum(MatOrderVO vo, String fathercorp)  throws DZFWarpException ;


	/**
	 * 设置默认值
	 * @param vo
	 * @param uservo
	 */
	public void setDefaultValue(MatOrderVO vo, UserVO uservo) throws DZFWarpException ;
 

	/**
	 * 判断是否是数字
	 * @param pname
	 * @return
	 */
	public boolean isInteger(String pname)  throws DZFWarpException ;


	/**
	 * 判断是否能修改
	 * @param vstatus
	 * @param string
	 */
	public void checkIsOperOrder(Integer vstatus, String string) throws DZFWarpException ;


	/**
	 * 检查是否是最新数据
	 * @param pk_materielbill
	 * @param updatets
	 */
	public void checkData(String pk_materielbill, DZFDateTime updatets) throws DZFWarpException ;


	/**
	 * 设置省市区
	 */
	public void setCitycountry(MatOrderVO vo) throws DZFWarpException ;
	
}
