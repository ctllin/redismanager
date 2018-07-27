package org.springframework.cores.system.utils;

/**
 * <p>Title: StringUtils</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: www.hanshow.com</p>
 *
 * @author guolin
 * @version 1.0
 * @date 2018-07-27 16:24
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctl.utils.ConfigUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public StringUtils() {
    }

    public static String lowerFirst(String str) {
        return isBlank(str) ? "" : str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String upperFirst(String str) {
        return isBlank(str) ? "" : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return "";
        } else {
            String regEx = "<.+?>";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(html);
            String s = m.replaceAll("");
            return s;
        }
    }

    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        } else {
            try {
                StringBuilder sb = new StringBuilder();
                int currentLength = 0;
                char[] var7;
                int var6 = (var7 = replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()).length;

                for(int var5 = 0; var5 < var6; ++var5) {
                    char c = var7[var5];
                    currentLength += String.valueOf(c).getBytes("GBK").length;
                    if (currentLength > length - 3) {
                        sb.append("...");
                        break;
                    }

                    sb.append(c);
                }

                return sb.toString();
            } catch (UnsupportedEncodingException var8) {
                var8.printStackTrace();
                return "";
            }
        }
    }

    public static String rabbr(String str, int length) {
        return abbr(replaceHtml(str), length);
    }

    public static Double toDouble(Object val) {
        if (val == null) {
            return 0.0D;
        } else {
            try {
                return Double.valueOf(trim(val.toString()));
            } catch (Exception var2) {
                return 0.0D;
            }
        }
    }

    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }

    public static final String MD5(String s) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for(int i = 0; i < j; ++i) {
                byte b = md[i];
                str[k++] = hexDigits[b >> 4 & 15];
                str[k++] = hexDigits[b & 15];
            }

            return new String(str);
        } catch (Exception var10) {
            return "";
        }
    }

    public static void main(String[] args) {
        MD5("ctllin"+ ConfigUtils.getType("md5.pass.encrypt.str")+"ctl");
    }
}
