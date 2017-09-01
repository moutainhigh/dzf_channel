package com.dzf.service.sys.searchpwd.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.service.sys.searchpwd.ISearchPswSrv;

@Service("searchPsw")
public class SearchPswSrvImpl implements ISearchPswSrv{

	private SingleObjectBO singleObjectBO = null;
	
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public UserVO getPhoByUcode(String ucode) throws BusinessException{
		
		if(ucode == null || "".equals(ucode)){
			throw new BusinessException("发送验证码失败！"); 
		}
		String sql = "select * from sm_user where nvl(dr,0)=0 and user_code = ?";
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(ucode);
		List<UserVO> uVo = (List<UserVO>) singleObjectBO.executeQuery(sql,sp,new BeanListProcessor(UserVO.class));
		return uVo.get(0);
	}

	@Override
	public UserVO savePsw(UserVO uvo) {
		
		String sql = "update sm_user set user_password = ? where nvl(dr,0)=0 and user_code = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(uvo.getUser_password());
		sp.addParam(uvo.getUser_code());
		int flag = singleObjectBO.executeUpdate(sql, sp);
		if(flag > 0){
			return uvo;
		}else{
			return new UserVO();
		}
	}

	/**
	 * param:UserVO
	 * 校验用户编码是否存在
	*/
	public UserVO UCodeIsExist(String user_code) throws BusinessException{
		
		String sql = "select * from sm_user where nvl(dr,0)=0 and user_code = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(user_code);
		
		List<UserVO> uVo = (List<UserVO>) singleObjectBO.executeQuery(sql,sp,new BeanListProcessor(UserVO.class));
		
		return uVo.get(0);
	}
}
