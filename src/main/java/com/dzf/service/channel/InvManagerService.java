package com.dzf.service.channel;

import java.util.HashMap;
import java.util.List;

import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.piaotong.invinfo.InvInfoResBVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDouble;

public interface InvManagerService {
	
	/**
	 * 查询
	 * @return
	 * @throws DZFWarpException
	 */
	List<ChInvoiceVO> query(ChInvoiceVO vo) throws DZFWarpException;
	
	/**
	 * 获取发票总记录数
	 */
	Integer queryTotalRow(ChInvoiceVO vo) throws DZFWarpException;
	
	/**
	 * 获取渠道商总记录数
	 */
	Integer queryChTotalRow(ChInvoiceVO vo) throws DZFWarpException;
	
	/**
	 * 查询渠道商
	 */
	List<CorpVO> queryChannel(ChInvoiceVO vo) throws DZFWarpException;
	
	/**
	 * 开票
	 */
	List<ChInvoiceVO> onBilling(String[] pk_invoices, String userid,String invtime) throws DZFWarpException;
	
	/**
	 * 开具电子票（本接口已经废弃 2019-03-15 下一版删除）
	 * @param pk_invoices
	 * @param uvo
	 * @return
	 * @throws DZFWarpException
	 */
	List<ChInvoiceVO> onAutoBill(String[] pk_invoices, UserVO uvo) throws DZFWarpException;
	
	/**
	 * 删除开票
	 */
	public void delete(ChInvoiceVO vo) throws DZFWarpException;

        /**
     * 获取可开票金额
     * @return
     * @throws DZFWarpException
     */
    ChInvoiceVO queryTotalPrice(String pk_corp, int ipaytype, String invprice) throws DZFWarpException;

    void save(ChInvoiceVO data) throws DZFWarpException;
    
    InvInfoResBVO[] queryInvRepertoryInfo() throws DZFWarpException;
    
    /**
     * 换票
     * @author gejw
     * @time 下午4:27:37
     * @param data
     * @throws DZFWarpException
     */
    void onChange(ChInvoiceVO data) throws DZFWarpException;
    
    /**
     * 开具电子发票
     * @param cvo
     * @param uservo
     * @param useMap
     * @return  错误信息
     * @throws DZFWarpException
     */
    public ChInvoiceVO updateAutoBill(ChInvoiceVO cvo, UserVO uservo, HashMap<String, DZFDouble> useMap) throws DZFWarpException;
    
    /**
     * 包含数字校验
     * @param content
     * @return
     * @throws DZFWarpException
     */
    public boolean hasDigit(String content) throws DZFWarpException;
    
    /**
     * 查询累计合同扣款金额
     * @return
     * @throws DZFWarpException
     */
    public HashMap<String, DZFDouble> queryUsedMny() throws DZFWarpException;
}
