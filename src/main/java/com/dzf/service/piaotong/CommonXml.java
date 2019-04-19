package com.dzf.service.piaotong;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dzf.pub.jm.Base64CodeUtils;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

public class CommonXml {
	
	private static Logger log = Logger.getLogger(CommonXml.class);
	
	public static final int BUFFER = 1024;
	//版本号
	private static String version = null;
	//平台编码
	private static String appKey = null;
	//加密密钥
	private static String appSecret = null;
	//平台税号
	private static String uid = null;
	//随机数
	private static String randnum = null;
	//不压缩
	private static String unzip = null;
	//dsc加密
	private static String endes = null;
	//ca加密
	private static String enca = null;
	//加密方式
	private static String codeType = null;
	//注册密码
	private static String regpwd = null;
	//请求列表
	public static final String REQUEST_FPCY_QYSH = "REQUEST_FPCY_QYSH";
	//请求PDF 
	public static final String REQUEST_FPXZ_DZFP = "REQUEST_FPXZ_DZFP";
	//根据纳税人识别号发送短信验证码
	public static final String SEND_VERIFICATION_CODE = "SEND_VERIFICATION_CODE";
	//根据短信验证码获取token
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	static {
		ResourceBundle bundle = PropertyResourceBundle.getBundle("ticket");
		bundle.getString("busiurl");
		bundle.getString("authurl");
		version = bundle.getString("version");
		appKey = bundle.getString("appKey");
		appSecret = bundle.getString("appSecret");
		uid = bundle.getString("uid");
		randnum = bundle.getString("randnum");
		unzip = bundle.getString("unzip");
		bundle.getString("unencry");
		endes = bundle.getString("endes");
		enca = bundle.getString("enca");
		codeType = bundle.getString("codeType");
		bundle.getString("standardtime");
		regpwd = bundle.getString("regpwd");
		bundle.getString("pagesize");
	}
	
	public static String getCurDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(Calendar.getInstance().getTime());
	}
	
	public static String createxml(String content, String sourcecode, String taxid, String token){
		Element root = DocumentHelper.createElement("interface");
		Element globalInfo = root.addElement("globalInfo");
		globalInfo.addElement("appKey").addText(appKey);
		globalInfo.addElement("appSecret").addText("");//应用密码
		globalInfo.addElement("accessToken").addText(getToken(taxid, token));//访问令牌
		globalInfo.addElement("UID").addText(uid);//用户ID
		globalInfo.addElement("version").addText(version);//接口版本
		globalInfo.addElement("interfaceCode").addText(sourcecode);//接口编码
		globalInfo.addElement("passWord").addText(getPwd());
		globalInfo.addElement("requestTime").addText(new DZFDateTime().toString());//数据交换请求发出时间
		globalInfo.addElement("dataExchangeId").addText(getSerialNumber());//数据交换流水号
		Element returnStateInfo = root.addElement("returnStateInfo");
		returnStateInfo.addElement("returnCode").addText("");//返回代码
		returnStateInfo.addElement("returnMessage").addText("");//base64 返回描述
		Element data = root.addElement("data");
		Element dataDescription = data.addElement("dataDescription");
		dataDescription.addElement("zipCode").addText(unzip);
		dataDescription.addElement("encryptCode").addText(endes);
		dataDescription.addElement("codeType").addText(codeType);
		data.addElement("content").addText(content);
		Document dc = DocumentHelper.createDocument(root);
		String xml = dc.asXML();
		return xml;
	}
	
	/**
	 * 获取流水号
	 * @return
	 */
	private static String getSerialNumber(){
		SecureRandom random = new SecureRandom();
		DZFDate billdate = new DZFDate();
		String ymd = billdate.getYear() + billdate.getStrMonth() + billdate.getStrDay();
		String num = "20160825" + ymd + random.nextInt(999999999);
		
		return num;
	}
	
	/**
	 * 10位随机数+Base64(MD5(10位随机数+注册密码))
	 * @return
	 */
	public static String getPwd(){
		String content = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((randnum + regpwd).getBytes("UTF-8"));
            byte[] md5Byte = md.digest();
			content = Base64CodeUtils.encode(md5Byte);
		} catch (Exception e) {
			log.error(e);
		}
		return randnum + content;
	}
	
	/**
	 * 获取accessToken
	 * Base64({ NSRSBH&TOKEN }3DES}
	 * @param taxid
	 * @return
	 */
	public static String getToken(String taxid, String token){

		String str = taxid + "&" + token;
		byte[] jm = encrypt3DES(appSecret, str.getBytes());
		try {
			str = Base64CodeUtils.encode(jm);
		} catch (Exception e) {
			log.error(e);
		}
		return str;
	}
	
	/**
     * 3DES加密
     * 
     * @param decryptPassword
     *            密钥
     * @param decryptByte
     *            byte[]
     * @return byte[]
     */
    public static byte[] encrypt3DES(String encryptPassword, byte[] decryptByte) {
        try {
            Cipher cipher = init3DES(encryptPassword, Cipher.ENCRYPT_MODE);
            byte[] doFinal = cipher.doFinal(decryptByte);
            return doFinal;
        }
        catch (Exception e) {
        	log.error(e);
        }
        return null;
    }
    
    /**
     * 3DES解密
     * 
     * @param decryptPassword
     *            密钥
     * @param decryptByte
     *            byte[]
     * @return byte[]
     */
    public static byte[] decrypt3DES(String decryptPassword, byte[] decryptByte) {
        try {
            Cipher cipher = init3DES(decryptPassword, Cipher.DECRYPT_MODE);
            byte[] doFinal = cipher.doFinal(decryptByte);
            return doFinal;
        }
        catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    
    /**
     * 3DES初始化
     * 
     * @param decryptPassword
     *            秘钥
     * @param cipherMode
     *            加密/解密
     * @return Cipher
     * @throws Exception
     *             异常
     */
    private static Cipher init3DES(String decryptPassword, int cipherMode) throws Exception {
        SecretKey deskey = new SecretKeySpec(decryptPassword.getBytes(), "DESede");
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(cipherMode, deskey);
        return cipher;
    }
	
    public static Element getContentElement(String zip, String encry, String content) {
		Element root = null;
		try {
			byte[] bytes = Base64CodeUtils.decode(content);// 先base64解码
//			String strs = new String(bytes);
			String strs = null;
			//解压缩
			if (!CommonXml.unzip.equals(zip)){
				bytes = decompress(bytes);
			}
			//解密
			if(CommonXml.endes.equals(encry)){//3des加密、解密
				byte[] jm = decrypt3DES(appSecret, bytes);
//				byte[] jm = ThreeDes.decryptMode(ThreeDes.keyBytes, strs.getBytes());
				bytes = jm;
//				strs = new String(jm);
//				strs = new String(jm);
			}else if(CommonXml.enca.equals(encry)){
				
			}
			strs = new String(bytes,"UTF-8");
			Document document = DocumentHelper.parseText(strs);
			root = document.getRootElement();
		} catch (Exception e) {
			log.error(e);
		}
		return root;
	}
	
	/**
	 * 将压缩后的 byte[] 数据解压缩
	 *
	 * @param compressed
	 *            压缩后的 byte[] 数据
	 * @return 解压后的字符串
	 */
    public static byte[] decompress(byte[] data){
		 ByteArrayInputStream bais = null;
		 ByteArrayOutputStream baos = null;
		 GZIPInputStream gis = null;
		 try{
			 bais = new ByteArrayInputStream(data);
			 baos = new ByteArrayOutputStream();
			 gis = new GZIPInputStream(bais);
			 int count;
			 byte[] data1 = new byte[BUFFER];
			 while ((count = gis.read(data1, 0, BUFFER)) != -1) {
				 baos.write(data1, 0, count);
		     }
			 data = baos.toByteArray();
			 baos.flush();
		     baos.close();
		     bais.close();
		 }catch(IOException e){
			 log.error(e.getMessage(), e);
		 }finally {
			if (gis != null) {
				try {
					gis.close();
				} catch (IOException e) {
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		 }
		return data;
	}
//	public static String unZipString(byte[] compressed) {
//		if (compressed == null)
//			return null;
//
//		ByteArrayOutputStream out = null;
//		ByteArrayInputStream in = null;
//		GZIPInputStream zin = null;
//		String decompressed;
//		try {
//			out = new ByteArrayOutputStream();
//			in = new ByteArrayInputStream(compressed);
//			zin = new GZIPInputStream(in);
////			@SuppressWarnings("unused")
////			ZipEntry entry = zin.getNextEntry();
//			byte[] buffer = new byte[1024];
//			int offset = -1;
//			while ((offset = zin.read(buffer)) != -1) {
//				out.write(buffer, 0, offset);
//			}
//			decompressed = out.toString();
//		} catch (IOException e) {
//			decompressed = null;
//		} finally {
//			if (zin != null) {
//				try {
//					zin.close();
//				} catch (IOException e) {
//				}
//			}
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//				}
//			}
//			if (out != null) {
//				try {
//					out.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return decompressed;
//	}

	/**
	 * 压缩字符串为 byte[] 保存为字符串
	 *
	 * @param str
	 *            压缩前的文本
	 * @return
	 */
//	public static byte[] ZipString(String str) {
//		if (str == null)
//			return null;
//
//		byte[] compressed;
//		ByteArrayOutputStream out = null;
//		ZipOutputStream zout = null;
//		try {
//			out = new ByteArrayOutputStream();
//			zout = new ZipOutputStream(out);
//			zout.putNextEntry(new ZipEntry("0"));
//			zout.write(str.getBytes());
//			zout.closeEntry();
//			compressed = out.toByteArray();
//		} catch (IOException e) {
//			compressed = null;
//		} finally {
//			if (zout != null) {
//				try {
//					zout.close();
//				} catch (IOException e) {
//				}
//			}
//			if (out != null) {
//				try {
//					out.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return compressed;
//	}

}
