package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;

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
	List<ChInvoiceVO> onBilling(String[] pk_invoices, String userid) throws DZFWarpException;
	
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
    
}
