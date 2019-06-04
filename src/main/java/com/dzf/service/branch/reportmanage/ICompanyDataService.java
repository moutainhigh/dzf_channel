package com.dzf.service.branch.reportmanage;

import java.util.List;

import com.dzf.model.branch.reportmanage.CompanyDataVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface ICompanyDataService {

	public List<CompanyDataVO> query(QryParamVO qvo) throws DZFWarpException;

}
