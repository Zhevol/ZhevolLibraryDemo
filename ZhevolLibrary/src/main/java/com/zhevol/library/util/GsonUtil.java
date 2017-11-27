package com.zhevol.library.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * GSON解析的方法类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/27 0027.
 *
 * @author Zhevol
 */
public class GsonUtil {
    /**
     * 解析只包含一个对象的Json字符串
     *
     * @param jsonRes 源Json字符串
     * @param tClass  解析出来的对象的class
     * @param <T>     解析出来的对象的泛型标记
     * @return 解析出来的对象
     */
    public static <T> T parseObject(String jsonRes, Class<T> tClass) {
        try {
            return new Gson().fromJson(jsonRes, tClass);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
