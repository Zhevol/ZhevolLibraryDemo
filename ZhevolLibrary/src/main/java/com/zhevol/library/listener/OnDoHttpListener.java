package com.zhevol.library.listener;

import com.zhouyou.http.model.HttpParams;

/**
 * 网络请求的监听器<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/27 0027.
 *
 * @author Zhevol
 */
public interface OnDoHttpListener<T> {
    /**
     * 网络请求的Url
     *
     * @return url
     */
    String requestUrl();

    /**
     * 组装网络请求的参数
     *
     * @return 请求参数
     */
    HttpParams buildHttpParams();

    /**
     * 错误提示信息
     *
     * @return 提示信息
     */
    String errorMessage();

    /**
     * 获取泛型对应的对象的Class
     *
     * @return Class
     */
    Class<T> getTClass();

    /**
     * 展示请求下来的数据
     *
     * @param t 数据
     */
    void buildAndShow(T t);

}
