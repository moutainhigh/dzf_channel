package com.dzf.service.piaotong;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.dzf.model.piaotong.PiaoTongInvVO;
import com.dzf.model.piaotong.PiaoTongResBVO;
import com.dzf.model.piaotong.PiaoTongResVO;
import com.dzf.model.piaotong.invinfo.InvInfoResBVO;
import com.dzf.model.piaotong.invinfo.InvInfoResHVO;
import com.dzf.model.piaotong.invinfo.InvInfoResVO;
import com.dzf.model.piaotong.invinfo.QueryInvInfoVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.MD516;
import com.dzf.pub.RemoteClient;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.Base64CodeUtils;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.service.gl.taxrpt.utils.OFastJSON;

public class PiaoTongBill {
    private Logger logger = Logger.getLogger(this.getClass());

    // 大账房公钥 私钥 不能删除
    // privateKey=MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALWa80VdSrdfZN9bon7r57OiSEtU36+abBGxyPziuwmLIzLcTV2j2m3ibWiiJo0G8ZdFsgHIMjfzPkN0TEKLhJhEM8sgBNN8dk/QkqTz1eKxPyXZvwpcUvnHBU/aojtCui0KMnCgSOFEE2/VFYOYqbOwxGr7nF+9GK0TZ5aDbF6XAgMBAAECgYAxnNM47/OphPYZzv7lja0O9hap/dXeM2Gys88kxwwx30EOdADuxAS4YFNjcmj1vh/iI7gtpHOTtXAdHXh39YWTXb6o5NRWR85up/hfjykFdV2i8KybdjgrON0Q/jpSrmgseJ++WVrMu6OD5J0Lx7NQN03u9FlIbq4cKZAkgmSYgQJBANitEuZG8y92erQgRLuAgO3dGbQc9ip0aHFg08OWank5eNBYyA9adI9oYP2/RfgtVrJkLLUMa7//m8xA+SMKCyMCQQDWkHZJy6wx77nLlqS+ux772KnMMDQu1+mgj/uV2tg2Pqk7vAv0FtS05LdVJmvFj3ebzNJ9Wcd+CKtc7/cFM3/9AkEA1qSNJmV+nrQF1c5piD1Ce2AeCFjwWxo6JKbA4O5ux2vxjKOGe/8lnLLP8k6656EyU0H9L9UAIJD8K5jptJZMdQJAdEYnBAiukQUjP1OshXur57jbEz8QGE6DWbKNGQFWZ9b/KNLX+3ef30OOokNG5fHAVJrjenB7ri4Ohze3OodVFQJBAMitj1uD63LRyJv7CoT6vZkPtGy6YZk+/GWaLjlbg3iP7XBGKVOMgjSm98r6MjOAzXY/3YWLwVZAhHWg13pcMQA=
    // publicKey=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1mvNFXUq3X2TfW6J+6+ezokhLVN+vmmwRscj84rsJiyMy3E1do9pt4m1ooiaNBvGXRbIByDI38z5DdExCi4SYRDPLIATTfHZP0JKk89XisT8l2b8KXFL5xwVP2qI7QrotCjJwoEjhRBNv1RWDmKmzsMRq+5xfvRitE2eWg2xelwIDAQAB

    private static String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
            "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
            "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    //
    private static String xxptbm = null;
    private static String ptxxurl = null;
    private static String platformCode = null;
    private static String signType = null;
    private static String format = null;
    private static String xxversion = null;
    private static String xxpwd = null;
    private static String privateKey = null;
    private static String publicKey = null;
    private static String dzftaxno = null;
    private static String ptuname = null;

    static {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("piaotong");
        ptxxurl = bundle.getString("ptxxurl");
        xxptbm = bundle.getString("xxptbm");
        platformCode = bundle.getString("platformCode");
        signType = bundle.getString("signType");
        format = bundle.getString("format");
        xxversion = bundle.getString("xxversion");
        xxpwd = bundle.getString("xxpwd");
        privateKey = bundle.getString("privateKey");
        publicKey = bundle.getString("publicKey");
        dzftaxno = bundle.getString("dzftaxno");
        ptuname = bundle.getString("ptuname");
    }

    /**
     * 生成电子发票
     * @author gejw
     * @time 上午11:04:21
     * @param hvo
     * @return
     */
    public PiaoTongResVO sendBill(PiaoTongInvVO hvo) {

        String result = null;
        try {
            logger.info("----------------请求开票-----------BEGIN");
            Map<String, String> map = getBusiParams(hvo);
            List<NameValuePair> params = getParam(map);
            String url = ptxxurl + "/tp/openapi/invoiceBlue.pt";
            result = RemoteClient.sendPostData(url, params);

            logger.info(result);
            logger.info("----------------请求开票-----------END");
        } catch (Exception e) {
            logger.error(e);
        }

        PiaoTongResVO resvo = parseResult(result);

        return resvo;
    }
    
    /**
     * 查询票通库存发票信息
     * @author gejw
     * @date 2018年6月5日
     * @time 上午11:07:24
     */
    public InvInfoResBVO[] queryInvRepertoryInfo(QueryInvInfoVO qvo){


        String result = null;
        try {
            logger.info("----------------查询票通库存发票信息-----------BEGIN");
            qvo.setEnterpriseName(ptuname);
            Map<String, String> map = getBusiParams(qvo);
            List<NameValuePair> params = getParam(map);
            String url = ptxxurl + "/tp/openapi/getInvoiceRepertoryInfo.pt";
            result = RemoteClient.sendPostData(url, params);

            logger.info(result);
            logger.info("----------------查询票通库存发票信息-----------END");
        } catch (Exception e) {
            logger.error(e);
        }

        InvInfoResBVO[] resvos = parseInvInfo(result);
        return resvos;
    }

    private PiaoTongResVO parseResult(String result) {
        if (StringUtil.isEmpty(result))
            throw new BusinessException("开票申请失败，请联系管理员");

        PiaoTongResVO resvo = JSON.parseObject(result, PiaoTongResVO.class);
        if (resvo == null || StringUtil.isEmpty(resvo.getCode()) || StringUtil.isEmpty(resvo.getMsg())) {
            // throw new BusinessException("开票申请失败，请联系管理员");
            return resvo;
        } else if (!IPiaoTongConstant.SUCCESS.equals(resvo.getCode())) {
            // throw new BusinessException(
            // StringUtil.isEmpty(resvo.getMsg())
            // ? IPiaoTongConstant.errorMap.get(resvo.getCode()) :
            // resvo.getMsg()
            // );
            return resvo;
        }

        String content = resvo.getContent();

        if (StringUtil.isEmpty(content))
            return null;

        byte[] bytes;
        try {
            bytes = CommonXml.decrypt3DES(xxpwd, Base64CodeUtils.decode(content));
            content = new String(bytes, "UTF-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        PiaoTongResBVO contentvo = JSON.parseObject(content, PiaoTongResBVO.class);
        resvo.setBvo(contentvo);
        return resvo;
        // return contentvo;
    }
    
    private InvInfoResBVO[] parseInvInfo(String result) {
        if (StringUtil.isEmpty(result))
            throw new BusinessException("电子票余量查询失败，请联系管理员");

        InvInfoResVO resvo = JSON.parseObject(result, InvInfoResVO.class);
        if (resvo == null || StringUtil.isEmpty(resvo.getCode()) || StringUtil.isEmpty(resvo.getMsg())) {
            // throw new BusinessException("开票申请失败，请联系管理员");
            return null;
        } else if (!IPiaoTongConstant.SUCCESS.equals(resvo.getCode())) {
            return null;
        }

        String content = resvo.getContent();

        if (StringUtil.isEmpty(content))
            return null;

        byte[] bytes;
        try {
            bytes = CommonXml.decrypt3DES(xxpwd, Base64CodeUtils.decode(content));
            content = new String(bytes, "UTF-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        InvInfoResHVO contentvo = JSON.parseObject(content, InvInfoResHVO.class);
        
        return contentvo.getExtensionInfos();
    }

    private Map<String, String> getBusiParams(PiaoTongInvVO hvo) throws Exception {
        hvo.setInvoiceReqSerialNo(getInvSerialNo(xxptbm));
        hvo.setTaxpayerNum(dzftaxno);//
        hvo.setSellerTaxpayerNum(dzftaxno);
        String content = OFastJSON.toJSONString(hvo);
        byte[] bytes = CommonXml.encrypt3DES(xxpwd, content.getBytes("UTF-8"));
        content = Base64CodeUtils.encode(bytes);
        content = content.replace("\r\n", "").replace("\n", "");
        Map<String, String> map = new HashMap<String, String>();
        map.put("platformCode", platformCode);
        map.put("signType", signType);
        map.put("format", format);
        map.put("version", xxversion);
        map.put("content", content);
        map.put("timestamp", new DZFDateTime().toString());
        map.put("serialNo", getSerialNo(xxptbm));// getSerialNo("DEMO"));
        String sign = sign(getSignatureContent(map), privateKey);
        sign = sign.replace("\r\n", "").replace("\n", "");
        map.put("sign", sign);
        return map;
    }

    private Map<String, String> getBusiParams(QueryInvInfoVO qvo) throws Exception {
        qvo.setTaxpayerNum(dzftaxno);
        String content = OFastJSON.toJSONString(qvo);
        byte[] bytes = CommonXml.encrypt3DES(xxpwd, content.getBytes("UTF-8"));
        content = Base64CodeUtils.encode(bytes);
        content = content.replace("\r\n", "").replace("\n", "");
        Map<String, String> map = new HashMap<String, String>();
        map.put("platformCode", platformCode);
        map.put("signType", signType);
        map.put("format", format);
        map.put("version", xxversion);
        map.put("content", content);
        map.put("timestamp", new DZFDateTime().toString());
        map.put("serialNo", getSerialNo(xxptbm));// getSerialNo("DEMO"));
        String sign = sign(getSignatureContent(map), privateKey);
        sign = sign.replace("\r\n", "").replace("\n", "");
        map.put("sign", sign);
        return map;
    }

    private List<NameValuePair> getParam(Map<String, String> map) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), "\"" + entry.getValue() + "\""));
        }

        return params;
    }

    private String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64CodeUtils.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature.getInstance("SHA1WithRSA");

            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));

            byte[] signed = signature.sign();

            return Base64CodeUtils.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getSignatureContent(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        StringBuffer content = new StringBuffer();
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if (params.get(key) != null) {
                String value = String.valueOf(params.get(key));
                content.append(
                        new StringBuilder().append(i == 0 ? "" : "&").append(key).append("=").append(value).toString());
            }
        }
        return content.toString();
    }

    private String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[(x % 62)]);
        }
        return shortBuffer.toString();
    }

    private String getSerialNo(String prefix) {
        return prefix + new DZFDateTime().toString().replace(" ", "").replace(":", "").replace("-", "")
                + generateShortUuid();
    }

    /**
     * 发票请求流水号
     * 
     * @author gejw
     * @time 上午11:25:39
     * @param prefix
     * @return
     */
    private String getInvSerialNo(String prefix) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return prefix + MD516.Md5(uuid);
    }
}
