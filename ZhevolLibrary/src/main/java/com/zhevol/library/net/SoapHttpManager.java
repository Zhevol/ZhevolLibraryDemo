package com.zhevol.library.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.blankj.ALog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Soap 请求的管理者
 *
 * @author Zhevol
 * @date 2018/3/23 0023
 */
public class SoapHttpManager {

    /**
     * SoapHttpManager
     */
    private static class SoapHttpManagerHolder {
        /**
         * 单例
         */
        final static SoapHttpManager INSTANCE = new SoapHttpManager();
    }

    /**
     * 获取 SoapHttpManager 单例
     *
     * @return SoapHttpManager 单例
     */
    public static SoapHttpManager getInstance() {
        return SoapHttpManagerHolder.INSTANCE.setShowDialog(true);
    }

    /**
     * 展示 Dialog
     */
    private boolean showDialog;

    /**
     * 初始化一个 Dialog
     */
    private ProgressDialog mProgressDialog;

    /**
     * 获取 showDialog 的值
     *
     * @return true-展示 Dialog,false-不展示 Dialog
     */
    private boolean isShowDialog() {
        return showDialog;
    }

    /**
     * 设置是否展示 Dialog
     *
     * @param showDialog true-展示 Dialog,false-不展示 Dialog
     */
    public SoapHttpManager setShowDialog(boolean showDialog) {
        SoapHttpManagerHolder.INSTANCE.showDialog = showDialog;
        return SoapHttpManagerHolder.INSTANCE;
    }

    /**
     * 发送网络请求
     */
    public <T> void doRequest(final OnRequestListener<T> listener) {
        if (isShowDialog()) {
            mProgressDialog = ProgressDialog.show(listener.domainContext(), "", "请稍候…", true);
        }
        SoapHttp.getInstance()
                .setRequestUrl(HttpConfig.BASE_URL)
                .setNameSpace(HttpConfig.NAME_SPACE)
                .setMethodName(listener.requestMethod())
                .setParam(buildParams(listener.requestParams()))
                .setOnSoapHttpListener(new SoapHttp.OnSoapHttpListener() {
                    @Override
                    public void onSuccess(String result) {
                        ALog.e("请求的结果" + result);
                        closeDialog();
                        listener.onSuccess(parseObject(result, listener.getTClass()));
                    }

                    @Override
                    public void onFailed(Exception e) {
                        ALog.e("错误信息" + e.toString());
                        closeDialog();
                        Toast.makeText(listener.domainContext(), "请求异常，请重试", Toast.LENGTH_SHORT).show();
                    }
                })
                .doSoapHttpRequest();
    }

    /**
     * 关闭 Dialog
     */
    private void closeDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 解析只包含一个对象的Json字符串
     *
     * @param jsonRes 源Json字符串
     * @param tClass  解析出来的对象的class
     * @param <T>     解析出来的对象的泛型标记
     * @return 解析出来的对象
     */
    private <T> T parseObject(String jsonRes, Class<T> tClass) {
        try {
            return new Gson().fromJson(jsonRes, tClass);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * 发送请求的接口
     *
     * @param <T> 泛型
     */
    public interface OnRequestListener<T> {

        /**
         * 请求发生的上下文内容
         *
         * @return 上下文
         */
        Context domainContext();

        /**
         * 请求的地址
         *
         * @return 请求的地址
         */
        String requestMethod();

        /**
         * 组装请求的参数
         *
         * @return 请求的参数
         */
        Map<String, String> requestParams();

        /**
         * 获取泛型的 Class 类型
         *
         * @return Class
         */
        Class<T> getTClass();

        /**
         * 请求成功的返回
         *
         * @param t 泛型对象
         */
        void onSuccess(T t);
    }

    /**
     * 构建请求参数
     *
     * @param mapParam 请求参数
     * @return 最终请求参数
     */
    private Map<String, String> buildParams(Map<String, String> mapParam) {
        HashMap<String, String> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("2F865C8D75FC0061CA176AB53CE8AFEE");
        if (mapParam != null && !mapParam.isEmpty()) {
            for (Map.Entry<String, String> entry : mapParam.entrySet()) {
                sb.append(entry.getValue());
                params.put(entry.getKey(), entry.getValue());
            }
        }
        params.put("token", md5(sb.toString()));
        return params;
    }

    /**
     * 获取MD5加密后的字符串
     *
     * @param str 明文
     * @return 加密后的字符串
     */
    private String md5(String str) {
        //18623627565
        StringBuilder sb = new StringBuilder();
        try {
            /* 创建MD5加密对象 */
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            /* 进行加密 */
            md5.update(str.getBytes("UTF-8"));
            /* 获取加密后的字节数组 */
            byte[] md5Bytes = md5.digest();
            for (byte bt : md5Bytes) {
                int temp = bt & 0xFF;
                // 转化成十六进制不够两位
                if (temp <= 0xF) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(temp));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sb.append("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            sb.append("");
        }
        ALog.i("源字符串：" + str + "，加密结果:" + sb.toString());
        return sb.toString();
    }

}
