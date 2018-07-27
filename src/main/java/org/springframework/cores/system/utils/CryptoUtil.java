package org.springframework.cores.system.utils;

import java.security.Key;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: CryptoUtil</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: www.hanshow.com</p>
 *
 * @author guolin
 * @version 1.0
 * @date 2018-07-27 14:02
 */
public class CryptoUtil {
    private static Logger logger = LoggerFactory.getLogger(CryptoUtil.class);

    public static Key DEFAULT_KEY = null;
    public static final String DEFAULT_SECRET_KEY1 = "?:P)(OL><KI*&UJMNHY^%TGBVFR$#EDCXSW@!QAZ";
    public static final String DEFAULT_SECRET_KEY2 = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,9ol.0p;/";
    public static final String DEFAULT_SECRET_KEY3 = "!QAZ@WSX#EDC$RFV%TGB^YHN&UJM*IK<(OL>)P:?";
    public static final String DEFAULT_SECRET_KEY4 = "1qaz@WSX3edc$RFV5tgb^YHN7ujm*IK<9ol.)P:?";
    public static final String DEFAULT_SECRET_KEY5 = "!QAZ2wsx#EDC4rfv%TGB6yhn&UJM8ik,(OL>0p;/";
    public static final String DEFAULT_SECRET_KEY6 = "1qaz2wsx3edc4rfv5tgb^YHN&UJM*IK<(OL>)P:?";
    public static final String DEFAULT_SECRET_KEY = "?:P)(OL><KI*&UJMNHY^%TGBVFR$#EDCXSW@!QAZ";
    public static final String DES = "DES";
    public static final Base32 base32 = new Base32();

    public CryptoUtil() {
    }

    public static void main(String[] args) {
        String s1 = "123456";
        String s2 = encode(s1);
        System.out.println("s2=" + s2);
        System.out.println("decode=" + decode("MTIzNDU2"));
    }

    public static String encode(String str) {
        try {
            Base64 base64 = new Base64();
            byte[] textByte = str.getBytes("UTF-8");
            String encodedText = base64.encodeToString(textByte);
            return encodedText;
        } catch (Exception var4) {
            logger.error("encode失败", var4);
            return "";
        }
    }

    public static String decode(String encodedText) {
        String pass = "";
        try {
            Base64 base64 = new Base64();
            pass = new String(base64.decode(encodedText), "UTF-8");
        } catch (Exception var3) {
            logger.error("decode失败", var3);
        }
        return pass;
    }

    public static String encode64xx(String key, String str) {
        try {
            return new String(Base64.encodeBase64(str.getBytes("UTF-8")), "UTF-8");
        } catch (Exception var3) {
            logger.error("encode64xx失败", var3);
            return "";
        }
    }

    public static String decode64xx(String str) {
        String pass = "";
        try {
            pass = new String(Base64.decodeBase64(str.getBytes("UTF-8")), "UTF-8");
        } catch (Exception var3) {
            logger.error("decode64xx失败", var3);
        }
        return pass;
    }
}
