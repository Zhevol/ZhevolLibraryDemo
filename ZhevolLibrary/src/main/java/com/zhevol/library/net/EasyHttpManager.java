package com.zhevol.library.net;

import android.app.Dialog;
import android.content.Context;

import com.blankj.ALog;
import com.zhevol.library.dialog.ProgressDialog;
import com.zhevol.library.listener.OnEasyHttpListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.model.HttpParams;
import com.zhouyou.http.subsciber.IProgressDialog;

/**
 * 网络请求的管理类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class EasyHttpManager {
    /**
     * 单例
     */
    private static volatile EasyHttpManager mEasyHttpManager;
    /**
     * 显示弹框的时候显示的文字
     */
    private String dialogText;

    /**
     * 获取单例
     *
     * @return EasyHttpManager 的单例对象
     */
    public static EasyHttpManager getInstance() {
        if (mEasyHttpManager == null) {
            synchronized (EasyHttpManager.class) {
                if (mEasyHttpManager == null) {
                    mEasyHttpManager = new EasyHttpManager();
                }
            }
        }
        mEasyHttpManager.dialogText = "请稍候\u2026";
        return mEasyHttpManager;
    }

    /**
     * 设置弹框要显示的文字
     *
     * @param dialogText 要显示的文字
     * @return 单例
     */
    public EasyHttpManager dialogText(String dialogText) {
        mEasyHttpManager.dialogText = dialogText;
        return mEasyHttpManager;
    }

    /**
     * 发起网络请求
     *
     * @param context            上下文内容
     * @param requestUrl         请求的url
     * @param requestParams      请求的参数
     * @param onEasyHttpListener 请求的回调
     */
    public void doHttpRequest(final Context context, String requestUrl, HttpParams requestParams, boolean isShowProgress,
                              final OnEasyHttpListener onEasyHttpListener) {
        ALog.e("请求的URL：" + requestUrl + "\r\n请求的参数：" + requestParams.toString() + "\r\n保存的cacheKey：" + context.getClass().getSimpleName());
        final IProgressDialog iProgressDialog = new IProgressDialog() {
            @Override
            public Dialog getDialog() {
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage(mEasyHttpManager.dialogText);
                return dialog;
            }
        };
        EasyHttp.post(requestUrl)
                .params(requestParams)
                .cacheKey(context.getClass().getSimpleName())
                .execute(new ProgressDialogCallBack<String>(iProgressDialog, isShowProgress, true) {
                    @Override
                    public void onSuccess(String s) {
                        onEasyHttpListener.onSuccess(s);
                    }

                    @Override
                    public void onError(ApiException e) {
                        onEasyHttpListener.onFailed(e);
                    }
                });
    }

    /**
     * 清除 Context 对应的缓存
     *
     * @param context 上下文
     * @return EasyHttpManager 对象
     */
    public EasyHttpManager clearCache(Context context) {
        EasyHttp.removeCache(context.getClass().getSimpleName());
        return mEasyHttpManager;
    }
}
