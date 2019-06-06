package com.dzf.pub.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * 关闭输入、输出流
 * @author gejw
 * @time 2019年4月22日 下午5:50:26
 *
 */
public class InOutUtils {

    private static Logger logger = Logger.getLogger(InOutUtils.class);
    
    private InOutUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void close(InputStream in,String msg) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                logger.info(msg, e);
            }
        }
    }

    public static void close(OutputStream out,String msg) {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                logger.info(msg, e);
            }
        }
    }
}
