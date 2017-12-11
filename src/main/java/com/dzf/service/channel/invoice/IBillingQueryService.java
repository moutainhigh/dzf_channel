package com.dzf.service.channel.invoice;

import java.util.List;

import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.pub.DZFWarpException;

public interface IBillingQueryService {
	
	/**
	 * 查询
	 * @return
	 * @throws DZFWarpException
	 */
	List<BillingInvoiceVO> query(BillingInvoiceVO vo) throws DZFWarpException;
	
	public void insertBilling(BillingInvoiceVO vo) throws DZFWarpException;
}
