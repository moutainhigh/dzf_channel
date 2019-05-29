package com.dzf.service.channel.branch.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.service.channel.branch.IBranchUserService;


@Service("userBranch")
public class BranchUserServiceImpl implements IBranchUserService {

	@Autowired
	private SingleObjectBO singleObjectBO;



}
