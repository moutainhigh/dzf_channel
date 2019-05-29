package com.dzf.service.branch.setup.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.service.branch.setup.IBranchUserService;


@Service("userBranch")
public class BranchUserServiceImpl implements IBranchUserService {

	@Autowired
	private SingleObjectBO singleObjectBO;



}
