package com.dzf.service.pub.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.model.sys.sys_set.ConCodeVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IBillTypeCode;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SessionRedis.IRedisSessionCallback;
import com.dzf.pub.SessionRedis.SessionRedisClient;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.pub.IBillCodeService;

import redis.clients.jedis.Jedis;

@Service("billCodeServiceImpl")
public class BillCodeServiceImpl implements IBillCodeService {
    
    @Autowired
    private SingleObjectBO singleObjectBO;
    
    @Override
    public String getBillCode(MaxCodeVO mcvo) throws DZFWarpException {
        ConCodeVO codeVO = queryCodeSet(mcvo.getBillType(),mcvo.getPk_corp(),mcvo.getBusiType());
        StringBuffer newcode = new StringBuffer();
        if(codeVO != null){
            DZFDate today = new DZFDate();
            String vcode = codeVO.getVcontcode();
            String number = codeVO.getInumber();//流水号
            String vprefix = codeVO.getVprefix();//前缀字符
            String year = codeVO.getVyear() == null ? "" : String.valueOf(today.getYear());//年
            String month = codeVO.getVmonth()== null ? "" : today.getStrMonth();//月
            newcode.append(vprefix).append(year).append(month);
            int diflength = newcode.length()+1;
            if (!StringUtil.isEmpty(vcode) && !StringUtil.isEmpty(number)) {
                String str = null;
                mcvo.setNewCode(newcode.toString());
                mcvo.setVcode(vcode);
                mcvo.setDiflen(diflength);
                BigDecimal  maxCode = getMaxCode(mcvo,codeVO);
                if (maxCode != null) {
                    str = String.format("%0"+Integer.parseInt(number)+"d", maxCode.intValue());
                } else {
                    str = String.format("%0"+Integer.parseInt(number)+"d", 1);  
                }   
                newcode.append(str) ;
            }
        }
        if(StringUtil.isEmptyWithTrim(newcode.toString())){
            throw new BusinessException("自动生成编码失败，请设置编码规则或手工录入编码。");
        }
        return newcode.toString();
    
    }
    
    private BigDecimal getMaxCode(MaxCodeVO mcvo,ConCodeVO codeVO){
        if(StringUtil.isEmpty(mcvo.getCorpIdField())){
            mcvo.setCorpIdField("pk_corp");
        }
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select max(substr(");
        sql.append(mcvo.getFieldName());
        sql.append(",");
        sql.append(mcvo.getDiflen());
        sql.append(",30))+1 as vcode ");
        sql.append(" from ").append(mcvo.getTbName());
        sql.append(" WHERE ").append(mcvo.getFieldName());
        sql.append(" LIKE ");
        sql.append("'");
        sql.append(mcvo.getNewCode());
        sql.append("%'");
        sql.append(" and ").append(mcvo.getCorpIdField()).append(" = ?");
        sql.append(" and nvl(dr,0) = 0  ");
        sql.append(" and length(").append(mcvo.getFieldName()).append(")= ? " );
        sp.addParam(mcvo.getPk_corp());
        sp.addParam(mcvo.getVcode().length());
        
        if(!StringUtil.isEmpty(mcvo.getBillType()) && isBillType(mcvo.getBillType())){
            sql.append(" and vbilltype = ?");
            sp.addParam(mcvo.getBillType());
        }
        BigDecimal next_code =  (BigDecimal) singleObjectBO.executeQuery(sql.toString(), sp,new ColumnProcessor("vcode"));
        String ncode = String.valueOf(next_code);
        int len = ncode.length();
        if(len > Integer.parseInt(codeVO.getInumber())){
            String updateSql = " update ynt_contcodeset set inumber = ?,vcontcode = ? where pk_contcodeset = ? and pk_corp = ? ";
            SQLParameter param = new SQLParameter();
            param.addParam(len);
            param.addParam(mcvo.getNewCode() + String.format("%0"+len+"d", 1));
            param.addParam(codeVO.getPk_contcodeset());
            param.addParam(mcvo.getPk_corp());
            
            singleObjectBO.executeUpdate(updateSql, param);
        }
        return next_code;
    }

    /**
     * 单据号编码规则
     * @author gejw
     * @time 下午4:54:35
     * @param billType
     * @param pk_corp
     * @param typemax
     * @return
     */
    private ConCodeVO queryCodeSet(String billType, String pk_corp,String typemax){
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select * from ynt_contcodeset where vbilltype = ? and pk_corp= ? and nvl(dr,0)=0");
        sp.addParam(billType);
        sp.addParam(pk_corp);
        if(!StringUtil.isEmpty(typemax) && (billType.equals(IBillTypeCode.EP01) || billType.equals(IBillTypeCode.EP02))){
           sql.append(" and busitypemax = ? ");
           sp.addParam(typemax);
        }
        ArrayList<ConCodeVO> list = (ArrayList<ConCodeVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ConCodeVO.class));
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    
    private boolean isBillType(String billtype){
        if(billtype.equals(IBillTypeCode.EP11)){
            return false;
        }
        return true;
    }
    
    
    @Override
    public String getDefaultCode(final MaxCodeVO mcvo) throws DZFWarpException {
        String code = ((String ) SessionRedisClient.getInstance().exec(new IRedisSessionCallback() {
            public Object exec(Jedis jedis) {
            	if(jedis == null){
            		return null;
            	}
                return getCode(jedis,mcvo);
            }
        }));
        return code;
    }

    private String getCode(Jedis jedis,MaxCodeVO mcvo){
    	if(jedis == null){
    		return null;
    	}
        String code=jedis.hget(mcvo.getTbName()+mcvo.getPk_corp(), mcvo.getBillType());//获取value
        if(!StringUtil.isEmpty(code)){//从redis取值
            code=addCode(code,mcvo.getDiflen());
            jedis.hset(mcvo.getTbName()+mcvo.getPk_corp(), mcvo.getBillType(),code);
            Long l = jedis.setnx(code, code);
            if(l == 0){
                getCode(jedis,mcvo);
            }
            jedis.expire(code, 120);
        }else{
            code=makeCode(mcvo);
            jedis.hset(mcvo.getTbName()+mcvo.getPk_corp(), mcvo.getBillType(),code); 
        }
        return code;
    }
    
    /**
     * 从数据库里取上一个编码，并加1
     * @param mcvo
     * @return
     */
	private String makeCode(MaxCodeVO mcvo) {
		String code;
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		Integer len=mcvo.getBillType().length();
		sql.append("select max("+mcvo.getFieldName()+") as count from "+mcvo.getTbName());
		sql.append(" where pk_corp=? and nvl(dr,0) = 0  and substr("+mcvo.getFieldName()+",0,"+len+")= ? ");
		sp.addParam(mcvo.getPk_corp());
		sp.addParam(mcvo.getBillType());
		String maxcode=null;
		try {
			 maxcode = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		} catch (NullPointerException e) {
			code=mcvo.getBillType()+String.format("%0"+mcvo.getDiflen()+"d", 1);
			return code;
		}
		return addCode(maxcode,mcvo.getDiflen());
	}
	
	/**
	 * 在上一个编码上加1
	 * @param maxcode：上一个编码
	 * @param wei：几位流水号
	 * @return
	 */
	private String addCode(String maxcode,Integer wei){
		Integer num=Integer.parseInt(maxcode.substring(maxcode.length()-wei))+1;
		String str = String.format("%0"+wei+"d", num);
		maxcode=maxcode.substring(0,maxcode.length()-wei)+str;
		return maxcode;
	}

}
