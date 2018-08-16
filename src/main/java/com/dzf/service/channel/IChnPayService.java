package com.dzf.service.channel;

import java.io.File;
import java.util.HashMap;

import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IChnPayService {

	public ChnPayBillVO save(ChnPayBillVO vo,CorpVO corpvo,String cuserid,File[] files,String[] filenames) throws DZFWarpException;
	
	public void delete(String cids) throws DZFWarpException;
	
	public ChnPayBillVO[] query(ChnPayBillVO cust,UserVO uservo) throws DZFWarpException ;
	
	public ChnPayBillVO queryByID(String cid) throws DZFWarpException;
	
	public HashMap<String,Object> updateStatusMult(ChnPayBillVO[] vos, int stat) throws DZFWarpException;
	
	public void deleteImageFile(ChnPayBillVO paramvo) throws DZFWarpException;	
	
}
