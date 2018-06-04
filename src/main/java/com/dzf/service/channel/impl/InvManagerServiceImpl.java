package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.model.piaotong.PiaoTongInvBVO;
import com.dzf.model.piaotong.PiaoTongInvVO;
import com.dzf.model.piaotong.PiaoTongResBVO;
import com.dzf.model.piaotong.PiaoTongResVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.InvManagerService;
import com.dzf.service.piaotong.IPiaoTongConstant;
import com.dzf.service.piaotong.PiaoTongBill;
import com.itextpdf.xmp.impl.Base64;

@Service("invManagerService")
public class InvManagerServiceImpl implements InvManagerService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private MultBodyObjectBO multBodyObjectBO;

    @SuppressWarnings("unchecked")
    @Override
    public List<ChInvoiceVO> query(ChInvoiceVO paramvo) throws DZFWarpException {
        QrySqlSpmVO qryvo = getQrySql(paramvo);
        List<ChInvoiceVO> retlist = (List<ChInvoiceVO>) multBodyObjectBO.queryDataPage(ChInvoiceVO.class,
                qryvo.getSql(), qryvo.getSpm(), paramvo.getPage(), paramvo.getRows(), "ts");
        if (retlist != null && retlist.size() > 0) {
            UserVO uservo = null;
            for (ChInvoiceVO vo : retlist) {
                uservo = UserCache.getInstance().get(vo.getInvperson(), null);
                if (uservo != null) {
                    vo.setIperson(uservo.getUser_name());
                }
            }
        }
        return retlist;
    }

    // private String queryUserName(String userid){
    // UserVO uvo = (UserVO)singleObjectBO.queryVOByID(userid, UserVO.class);
    // return CodeUtils1.deCode(uvo.getUser_name());
    // }

    @Override
    public Integer queryTotalRow(ChInvoiceVO paramvo) throws DZFWarpException {
        QrySqlSpmVO qryvo = getQrySql(paramvo);
        return multBodyObjectBO.queryDataTotal(ChInvoiceVO.class, qryvo.getSql(), qryvo.getSpm());
    }

    /**
     * 获取查询条件
     * 
     * @param paramvo
     * @return
     */
    private QrySqlSpmVO getQrySql(ChInvoiceVO paramvo) {
        QrySqlSpmVO qryvo = new QrySqlSpmVO();
        StringBuffer sql = new StringBuffer();
        SQLParameter spm = new SQLParameter();
        sql.append("select * from cn_invoice ");
        sql.append(" where nvl(dr,0) = 0 ");
        if (paramvo.getInvstatus() != null && paramvo.getInvstatus() != -1) {
            sql.append(" and invstatus = ?");
            spm.addParam(paramvo.getInvstatus());
        } else {
            sql.append(" and invstatus in (1,2,3)");
        }
        if (paramvo.getInvtype() != null && paramvo.getInvtype() != -1) {
            sql.append(" and invtype = ?");
            spm.addParam(paramvo.getInvtype());
        }
        if (!StringUtil.isEmpty(paramvo.getBdate())) {
            sql.append(" and apptime >= ?");
            spm.addParam(paramvo.getBdate());
        }
        if (!StringUtil.isEmpty(paramvo.getEdate())) {
            sql.append(" and apptime <= ?");
            spm.addParam(paramvo.getEdate());
        }
        if (paramvo.getCorps() != null && paramvo.getCorps().length > 0) {
            String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
            sql.append(" and pk_corp  in (" + corpIdS + ")");
        }
        if (!StringUtil.isEmpty(paramvo.getCorpname())) {
            sql.append(" and corpname like ?");
            spm.addParam("%" + paramvo.getCorpname() + "%");
        }
        qryvo.setSql(sql.toString());
        qryvo.setSpm(spm);
        return qryvo;
    }

    @Override
    public Integer queryChTotalRow(ChInvoiceVO vo) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select count(1) from bd_corp where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y'");
        sql.append("  and nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N'");
        if (!StringUtil.isEmpty(vo.getCorpcode())) {
            sql.append(" and instr(innercode,?) > 0");
            sp.addParam(vo.getCorpcode());
        }
        String total = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor()).toString();
        return Integer.valueOf(total);
    }

    @Override
    public List<CorpVO> queryChannel(ChInvoiceVO vo) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        // int page = vo.getPage();
        // int size = vo.getRows();
        // sql.append("select pk_corp,unitname,innercode from (select
        // pk_corp,unitname,innercode,rownum rn from bd_corp ");
        // sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' and
        // nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N' ");
        // if(!StringUtil.isEmpty(vo.getCorpcode())){
        // sql.append(" and instr(innercode,?) > 0");
        // sp.addParam(vo.getCorpcode());
        // }
        // sql.append(" and rownum <= ?)");
        // sql.append(" where rn > ?");
        // sql.append(" order by innercode ");
        sql.append("select pk_corp,unitname,innercode from bd_corp ");
        sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' ");
        sql.append(" and nvl(ischannel,'N') = 'Y' and nvl(isseal,'N')='N' ");
        if (vo.getDr() != null && vo.getDr() != -1) {// 给区域划分（省市过滤）用的
            sql.append(" and vprovince=? ");
            sp.addParam(vo.getDr());
            if (!StringUtil.isEmpty(vo.getVmome())) {
                String[] split = vo.getVmome().split(",");
                sql.append(" and pk_corp not in (");
                sql.append(SqlUtil.buildSqlConditionForIn(split));
                sql.append(" )");
            }
        }
        // if(!StringUtil.isEmpty(vo.getCorpcode())){
        // sql.append(" and instr(innercode,?) > 0");
        // sp.addParam(vo.getCorpcode());
        // }
        sql.append(" order by innercode ");
        // sp.addParam(page*size);
        // sp.addParam((page-1)*size);
        List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), sp,
                new BeanListProcessor(CorpVO.class));

        if (list != null && list.size() > 0) {
            encodeCorpVO(list);
            List<CorpVO> rList = new ArrayList<>();
            if (!StringUtil.isEmpty(vo.getCorpcode())) {
                for (CorpVO cvo : list) {
                    if (cvo.getUnitname().contains(vo.getCorpcode()) || cvo.getInnercode().contains(vo.getCorpcode())) {
                        rList.add(cvo);
                    }
                }
                return rList;
            }
        }
        return list;
    }

    private List<CorpVO> encodeCorpVO(List<CorpVO> vos) {
        for (CorpVO vo : vos) {
            vo.setUnitname(CodeUtils1.deCode(vo.getUnitname()));
        }
        return vos;
    }

    @Override
    public List<ChInvoiceVO> onBilling(String[] pk_invoices, String userid) throws DZFWarpException {
        if (pk_invoices == null || pk_invoices.length == 0) {
            throw new BusinessException("请选择发票！");
        }
        List<ChInvoiceVO> lists = new ArrayList<ChInvoiceVO>();
        List<ChInvoiceVO> listError = new ArrayList<ChInvoiceVO>();
        HashMap<String, DZFDouble> mapUse = queryUsedMny();
        ChInvoiceVO[] cvos = queryByPks(pk_invoices);
        for (ChInvoiceVO vo : cvos) {
            DZFDouble umny = CommonUtil.getDZFDouble(mapUse.get(vo.getPk_corp()));
            DZFDouble invmny = queryInvoiceMny(vo.getPk_corp());
            if (vo.getInvstatus() != 1) {
                vo.setMsg("要确认开票的单据不是待开票状态");
                listError.add(vo);
                continue;
            }
            DZFDouble invprice = new DZFDouble(vo.getInvprice());
            if (invprice.compareTo(umny.sub(invmny)) > 0) {
                StringBuffer msg = new StringBuffer();
                msg.append("你本次要确认开票的金额").append(invprice.setScale(2, DZFDouble.ROUND_HALF_UP)).append("元大于可开票金额")
                        .append(umny.sub(invmny).setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，请确认。");
                vo.setMsg(msg.toString());
                listError.add(vo);
                continue;
            }
            lists.add(vo);
            vo.setInvperson(userid);
            updateTicketPrice(vo);
            updateInvoice(vo);
        }
        return listError;
    }
    
    public boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    @Override
    public List<ChInvoiceVO> onAutoBill(String[] pk_invoices, UserVO uvo) throws DZFWarpException {
        //
        if (pk_invoices == null || pk_invoices.length == 0) {
            throw new BusinessException("请选择数据。");
        }
        
        if(uvo.getUser_name().length() > 8){
            throw new BusinessException("操作用户名称长度不能大于8。");
        }else if(hasDigit(uvo.getUser_name())){
            throw new BusinessException("操作用户名称不能包含数字。");
        }
        
        
       
        List<ChInvoiceVO> lists = new ArrayList<ChInvoiceVO>();
        List<ChInvoiceVO> listError = new ArrayList<ChInvoiceVO>();
        HashMap<String, DZFDouble> mapUse = queryUsedMny();
        ChInvoiceVO[] cvos = queryByPks(pk_invoices);
        if(cvos == null || cvos.length == 0){
            throw new BusinessException("请选择数据。");
        }
        for (ChInvoiceVO vo : cvos) {
            if(vo.getInvtype() != 2){
                throw new BusinessException("您好！只有申请开具电子发票的开票申请才可提交电子发票自动开票接口，请知悉并重新选择数据。");
            }
        }
        for (ChInvoiceVO vo : cvos) {
            DZFDouble umny = CommonUtil.getDZFDouble(mapUse.get(vo.getPk_corp()));
            DZFDouble invmny = queryInvoiceMny(vo.getPk_corp());
            if (vo.getInvstatus() != 1 && vo.getInvstatus() != 3) {
                vo.setMsg("要确认开票的单据不是待开票状态");
                listError.add(vo);
                continue;
            }
            DZFDouble invprice = new DZFDouble(vo.getInvprice());
            if (invprice.compareTo(umny.sub(invmny)) > 0) {
                StringBuffer msg = new StringBuffer();
                msg.append("你本次要确认开票的金额").append(invprice.setScale(2, DZFDouble.ROUND_HALF_UP)).append("元大于可开票金额")
                        .append(umny.sub(invmny).setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，请确认。");
                vo.setMsg(msg.toString());
                listError.add(vo);
                continue;
            }
            lists.add(vo);
            vo.setInvperson(uvo.getCuserid());
            PiaoTongResVO resvo = savePiaoTong(vo, uvo);
            if(resvo == null){
                vo.setMsg("票通未返回接收数据结果。");
                listError.add(vo);
            }else if(!IPiaoTongConstant.SUCCESS.equals(resvo.getCode())){
                vo.setMsg(resvo.getMsg());
                listError.add(vo);
            }
        }
        return listError;
    }
    
    private ChInvoiceVO[] queryByPks(String[] pk_invoices){
        String condition = SqlUtil.buildSqlForIn("pk_invoice", pk_invoices);
        StringBuffer sql = new StringBuffer();
        sql.append(" invstatus in (1,2,3)");
        sql.append("and ").append(condition);
        return (ChInvoiceVO[]) singleObjectBO.queryByCondition(ChInvoiceVO.class, sql.toString(), null);
    }

    private PiaoTongResVO savePiaoTong(ChInvoiceVO cvo, UserVO uvo) {
        PiaoTongInvVO hvo = new PiaoTongInvVO();
        // 销方信息
        // hvo.setTaxpayerNum("91110108397823696Y");//大账房
        // hvo.setSellerTaxpayerNum("91110108397823696Y");
        // hvo.setSellerEnterpriseName("北京大账房信息技术有限公司");
        // 测试
//        hvo.setTaxpayerNum("110101201702071");//
//        hvo.setSellerTaxpayerNum("110101201702071");
        hvo.setSellerEnterpriseName("北京大账房信息技术有限公司");
        hvo.setSellerAddress("北京海淀区万泉庄路15号5层501");
        hvo.setSellerTel("010-82552270");
        hvo.setSellerBankName("建行西直门北大街支行");
        hvo.setSellerBankAccount("11001174900053000702");
        hvo.setCasherName(uvo.getUser_name());
        hvo.setDrawerName(uvo.getUser_name());

        // 购买方信息
        hvo.setBuyerName(cvo.getCorpname());
        hvo.setBuyerTaxpayerNum(cvo.getTaxnum());
        hvo.setBuyerAddress(cvo.getCorpaddr());
        hvo.setBuyerBankName(cvo.getBankname());
        hvo.setBuyerBankAccount(cvo.getBankcode());
        hvo.setBuyerTel(cvo.getInvphone());
        hvo.setTakerEmail(cvo.getEmail());
//        hvo.setTakerEmail("gejingwei@dazhangfang.com");
        hvo.setTakerName(cvo.getRusername());
        hvo.setTakerTel(cvo.getInvphone());

        // 开票项目信息
        PiaoTongInvBVO bvo = new PiaoTongInvBVO();
        bvo.setGoodsName("技术服务费");
        bvo.setTaxClassificationCode("3040203000000000000");// 对 应 税 收 分 类编码
                                                            // ---信息技术服务
        bvo.setQuantity("1.00");// 数量
        bvo.setIncludeTaxFlag("1");// 含税标志
        bvo.setUnitPrice(cvo.getInvprice().toString());// 单价
        bvo.setInvoiceAmount(cvo.getInvprice().setScale(2, DZFDouble.ROUND_HALF_UP).toString());// 金额
        bvo.setTaxRateValue("0.06");// 税率
        List<PiaoTongInvBVO> itemList = new ArrayList<>();
        itemList.add(bvo);
        hvo.setItemList(itemList);

        PiaoTongBill bill = new PiaoTongBill();
        PiaoTongResVO resvo = bill.sendBill(hvo);
        updateSendBack(resvo,cvo);
        return resvo;
    }

    private void updateSendBack(PiaoTongResVO resvo,ChInvoiceVO cvo) {
        if(resvo != null && IPiaoTongConstant.SUCCESS.equals(resvo.getCode()) ){
            updatePtInvoice(resvo,cvo);
            updateTicketPrice(cvo);
        }else if(resvo == null || StringUtil.isEmpty(resvo.getCode()) || StringUtil.isEmpty(resvo.getMsg())){
            updatePtNotInv(resvo,cvo);
        }else if(!IPiaoTongConstant.SUCCESS.equals(resvo.getCode())){
            updatePtNotInv(resvo,cvo);
        }
    }
    
    /**
     * 开票成功：自动开票回写
     * @author gejw
     * @time 下午2:46:11
     * @param resvo
     */
    private void updatePtInvoice(PiaoTongResVO resvo,ChInvoiceVO cvo) {
        PiaoTongResBVO bvo = resvo.getBvo();
        cvo.setReqserialno(bvo.getInvoiceReqSerialNo());
        cvo.setQrcodepath(Base64.decode(bvo.getQrCodePath()));
        DZFDate time = new DZFDate();
        cvo.setInvtime(time.toString());
        cvo.setInvstatus(2);
        cvo.setBillway(1);
        singleObjectBO.update(cvo, new String[] { "reqserialno", "qrcodepath","invtime", "invperson", "invstatus","billway"});
    }
    
    /**
     * 开票失败：自动开票回写
     * @author gejw
     * @time 上午9:56:08
     * @param cvo
     */
    private void updatePtNotInv(PiaoTongResVO resvo,ChInvoiceVO cvo) {
        cvo.setErrcode(resvo.getCode());
        cvo.setInvstatus(3);
        cvo.setBillway(1);
        singleObjectBO.update(cvo, new String[] {"errcode","invstatus","billway"});
    }


    /**
     * 查询累计扣款
     * 
     * @param pk_invoices
     * @return
     * @throws DZFWarpException
     */
    public HashMap<String, DZFDouble> queryUsedMny() throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append(" select a.pk_corp,");
        sql.append(" sum(nvl(detail.nusedmny,0)) as debittotalmny ");
        sql.append(" from bd_account a");
        sql.append(
                " left join cn_detail detail on a.pk_corp = detail.pk_corp and detail.iopertype = 2 and nvl(detail.dr,0) = 0 and detail.doperatedate <= ?");
        sp.addParam(new DZFDate());
        sql.append(" where a.ischannel = 'Y'  ");
        sql.append(" group by a.pk_corp");
        List<BillingInvoiceVO> list = (List<BillingInvoiceVO>) singleObjectBO.executeQuery(sql.toString(), sp,
                new BeanListProcessor(BillingInvoiceVO.class));
        HashMap<String, DZFDouble> map = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (BillingInvoiceVO bvo : list) {
                map.put(bvo.getPk_corp(), bvo.getDebittotalmny() == null ? DZFDouble.ZERO_DBL : bvo.getDebittotalmny());
            }
        }
        return map;
    }

    /**
     * 查询已开票金额
     * 
     * @param vo
     */
    private DZFDouble queryInvoiceMny(String pk_corp) {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append(" select sum(nvl(invprice,0)) as billtotalmny ");
        sql.append(" from cn_invoice  where invstatus = 2 ");
        sql.append(" and apptime <= ? and pk_corp = ?");
        sql.append(" and nvl(dr,0) = 0");
        sp.addParam(new DZFDate());
        sp.addParam(pk_corp);
        sql.append(" group by pk_corp ");
        Object obj = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor());
        return obj == null ? DZFDouble.ZERO_DBL : new DZFDouble(obj.toString());
    }

    /**
     * 根据主键查询
     * 
     * @param pk_invoice
     * @return
     */
    private ChInvoiceVO queryByPk(String pk_invoice) {
        return (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, pk_invoice);
    }

    /**
     * 更新开票金额
     */
    private void updateTicketPrice(ChInvoiceVO vo) {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getInvprice());
        sp.addParam(vo.getPk_corp());
        if (vo.getIpaytype() == 0) {
            sp.addParam(2);
        }
        sql.append("update cn_balance set nticketmny = nvl(nticketmny,0) + ? ");
        sql.append("where nvl(dr,0)=0 and pk_corp = ? and ipaytype = ?");
        singleObjectBO.executeUpdate(sql.toString(), sp);
    }

    /**
     * 更新发票
     */
    private void updateInvoice(ChInvoiceVO[] vos) {
        DZFDate time = new DZFDate();
        for (ChInvoiceVO vo : vos) {
            vo.setInvtime(time.toString());
            vo.setInvstatus(2);
        }
        singleObjectBO.updateAry(vos, new String[] { "invtime", "invperson", "invstatus" });
    }

    private void updateInvoice(ChInvoiceVO vo) {
        DZFDate time = new DZFDate();
        vo.setInvtime(time.toString());
        vo.setInvstatus(2);
        vo.setBillway(2);
        singleObjectBO.update(vo, new String[] { "invtime", "invperson", "invstatus","billway" });
    }

    @Override
    public void delete(ChInvoiceVO vo) throws DZFWarpException {
        ChInvoiceVO chvo = (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, vo.getPk_invoice());
        if (chvo != null) {
//            if (chvo.getInvstatus() != 3) {
//                if (chvo.getInvcorp() != 2) {
//                    throw new BusinessException("加盟商提交的开票申请不能删除。");
//                }
//            }
            if (chvo.getInvstatus() == 2) {
                throw new BusinessException("发票状态为【已开票】，不能删除！");
            }
            singleObjectBO.deleteObject(vo);
        }
    }

    @Override
    public ChInvoiceVO queryTotalPrice(String pk_corp, int ipaytype, String invprice) throws DZFWarpException {
        ChInvoiceVO returnVo = new ChInvoiceVO();
        DZFDouble addPrice = queryAddPrice(pk_corp, ipaytype, invprice);
        DZFDouble ticketPrice = queryTicketPrice(pk_corp, ipaytype);
        DZFDouble nticketmny = ticketPrice.sub(addPrice);

        returnVo.setNticketmny(String.valueOf(nticketmny));
        return returnVo;
    }

    /**
     * 查询已添加的开票金额总额
     */
    private DZFDouble queryAddPrice(String pk_corp, int ipaytype, String invprice) {

        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append(
                "select nvl(sum(nvl(invprice,0)),0) price from cn_invoice where nvl(dr,0) = 0 and pk_corp = ? and invstatus = 1 and ipaytype = ?");
        sp.addParam(pk_corp);
        sp.addParam(ipaytype);
        String price = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("price")).toString();
        DZFDouble nprice = new DZFDouble(price);
        if (!StringUtil.isEmpty(invprice)) {
            nprice = nprice.sub(new DZFDouble(invprice));
        }
        return nprice;
    }

    private DZFDouble queryTicketPrice(String pk_corp, int ipaytype) {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select nvl(sum(nvl(nusedmny,0))-sum(nvl(nticketmny,0)),0) as nticketmny from cn_balance ");
        sql.append("where nvl(dr,0)=0 and pk_corp = ? and ipaytype = ?");
        sp.addParam(pk_corp);
        if (ipaytype == 0) {
            ipaytype = 2;
        }
        sp.addParam(ipaytype);
        String nticketmny = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("nticketmny"))
                .toString();
        DZFDouble price = new DZFDouble(nticketmny);
        return price;
    }

    @Override
    public void save(ChInvoiceVO vo) throws DZFWarpException {
        ChInvoiceVO ovo = (ChInvoiceVO) singleObjectBO.queryByPrimaryKey(ChInvoiceVO.class, vo.getPk_invoice());
        if (StringUtil.isEmpty(vo.getPk_corp())) {
            if (ovo == null) {
                throw new BusinessException("数据已被删除，请刷新重新操作。");
            }
            vo.setPk_corp(ovo.getPk_corp());
        }
        if(hasDigit(vo.getRusername())){
            throw new BusinessException("收票人不能包含数字。");
        }
        if (ovo.getInvstatus() == 2) {
            throw new BusinessException("已开票，不允许修改。");
        }
        String[] fieldNames = new String[] { "taxnum", "invprice", "invtype", "corpaddr", "invphone", "bankname",
                "bankcode", "email", "vmome","rusername" };
        if(ovo.getInvstatus() == 3){
            vo.setInvstatus(1);
            fieldNames = new String[] { "taxnum", "invprice", "invtype", "corpaddr", "invphone", "bankname",
                    "bankcode", "email", "vmome","rusername","invstatus" };
        }
        checkInvPrice(vo);
        singleObjectBO.update(vo, fieldNames);
    }

    /**
     * 校验开票金额（>0 && <=可开票金额）
     */
    private void checkInvPrice(ChInvoiceVO vo) {
        if (new DZFDouble(vo.getInvprice()).sub(new DZFDouble(0)).doubleValue() <= 0) {
            throw new BusinessException("开票金额小于0！");
        }
        DZFDouble addPrice = queryAddPrice(vo.getPk_corp(), vo.getIpaytype(), vo.getTempprice());
        DZFDouble ticketPrice = queryTicketPrice(vo.getPk_corp(), vo.getIpaytype());
        DZFDouble invPrice = new DZFDouble(vo.getInvprice());
        DZFDouble nticketmny = ticketPrice.sub(addPrice).sub(invPrice);
        if (nticketmny.doubleValue() < 0) {
            throw new BusinessException("开票金额不可以大于可开票金额，请重新填写");
        }
    }

}
