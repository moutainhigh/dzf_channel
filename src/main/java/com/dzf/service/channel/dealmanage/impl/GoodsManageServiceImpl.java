package com.dzf.service.channel.dealmanage.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.channel.dealmanage.GoodsDocVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.dealmanage.IGoodsManageService;
import com.dzf.spring.SpringUtils;

@Service("goodsmanageser")
public class GoodsManageServiceImpl implements IGoodsManageService {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Integer queryTotalRow(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(ChnPayBillVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsVO> query(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		List<GoodsVO> list = (List<GoodsVO>) multBodyObjectBO.queryDataPage(GoodsVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		
		return list;
	}

	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n") ;
		sql.append("  FROM cn_goods g  \n") ; 
		sql.append(" WHERE nvl(g.dr, 0) = 0  \n") ; 
		if(pamvo.getVstatus() != null && pamvo.getVstatus() != -1){
			sql.append("   AND g.vstatus = ?  \n") ; 
			spm.addParam(pamvo.getVstatus());
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodscode())){
			sql.append("   AND g.vgoodscode like ?  \n") ; 
			spm.addParam(pamvo.getVgoodscode()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodsname())){
			sql.append("   AND g.vgoodsname like ?  \n") ; 
			spm.addParam(pamvo.getVgoodsname()+"%");
		}
		sql.append(" ORDER BY g.updatets DESC \n");
		return qryvo;
	}

	@Override
	public GoodsVO save(GoodsVO datavo, File[] files, String[] filenames) throws DZFWarpException {
		datavo = (GoodsVO) singleObjectBO.saveObject(datavo.getPk_corp(), datavo);
		List<GoodsDocVO> doclist = new ArrayList<GoodsDocVO>();
		for(int i = 0; i < files.length; i++){
			String fname = System.nanoTime()  + filenames[i].substring(filenames[i].indexOf("."));
			String filepath = "";
			try {
				filepath = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).upload(files[i], filenames[i], null);
			} catch (AppException e) {
				throw new BusinessException("图片上传错误");
			}
			if(!StringUtil.isEmpty(filepath)){
				throw new BusinessException("图片上传错误");
			}
			GoodsDocVO docvo = new GoodsDocVO();
			docvo.setPk_corp(datavo.getPk_corp());
			docvo.setPk_goods(datavo.getPk_goods());
			docvo.setDocName(filenames[i]);
			docvo.setDocTemp(fname);
			docvo.setVfilepath(filepath);
			docvo.setCoperatorid(datavo.getCoperatorid());
			docvo.setDoperatedate(new DZFDate());
			docvo.setDr(0);
			doclist.add(docvo);
		}
		if(doclist != null && doclist.size() > 0){
			singleObjectBO.insertVOArr(datavo.getPk_corp(), doclist.toArray(new GoodsDocVO[0]));
		}else{
			throw new BusinessException("图片上传错误");
		}
		return datavo;
	}
}
