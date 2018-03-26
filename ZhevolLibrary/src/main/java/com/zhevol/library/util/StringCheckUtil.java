package com.zhevol.library.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串校验的相关方法类
 *
 * @author Zhevol
 * @date 2018/3/26 0026
 */
public class StringCheckUtil {

    /**
     * 判断字符串是否是车牌照
     *
     * @param res
     *            源字符串
     * @return 比对结果
     */
    public static boolean isStringLicenceNum(String res) {
//		String regEx = "/^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}[A-Z0-9]{4}[学]{1}$/";
        String regEx = "/(^[\\u4E00-\\u9FA5]{1}[A-Z0-9]{6}$)|(^[A-Z]{2}[A-Z0-9]{2}[A-Z0-9\\u4E00-\\u9FA5]" +
                "{1}[A-Z0-9]{4}$)|(^[\\u4E00-\\u9FA5]{1}[A-Z0-9]{5}[挂学警军港澳]{1}$)|(^[A-Z]{2}[0-9]{5}$)|" +
                "(^(08|38){1}[A-Z0-9]{4}[A-Z0-9挂学警军港澳]{1}$)/";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(res);
        return matcher.matches();
    }

}
