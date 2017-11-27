package com.zhevol.library.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.blankj.ALog;
import com.zhevol.library.R;
import com.zhevol.library.listener.OnDoHttpListener;
import com.zhevol.library.listener.OnEasyHttpListener;
import com.zhevol.library.net.EasyHttpManager;
import com.zhevol.library.util.BaseUtil;
import com.zhevol.library.util.GsonUtil;

/**
 * 包含有错误信息提示页面的 Activity<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/27 0027.
 *
 * @author Zhevol
 */
public abstract class BaseErrorContentActivity extends BaseTopToolbarActivity {

    protected AppCompatTextView tvErrorContent;

    @Override
    protected void bindData(Bundle savedInstanceState) {
        tvErrorContent.setVisibility(View.GONE);
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        tvErrorContent = findViewById(R.id.tvErrorContent);
    }

    /**
     * 展示错误提示页面
     *
     * @param errorText 错误提示信息
     */
    protected void showErrorContent(String errorText) {
        tvErrorContent.setVisibility(View.VISIBLE);
        tvErrorContent.setText(errorText);
    }

    /**
     * 隐藏错误提示信息页面
     */
    protected void hideErrorContent() {
        if (tvErrorContent.getVisibility() == View.VISIBLE)
            tvErrorContent.setVisibility(View.GONE);
    }

    /**
     * 查询学员统计信息
     */
    protected <T> void doHttpRequest(final OnDoHttpListener<T> mOnDoHttpListener) {
        EasyHttpManager.getInstance().clearCache(this).doHttpRequest(this, mOnDoHttpListener.requestUrl(),
                mOnDoHttpListener.buildHttpParams(), true,
                new OnEasyHttpListener() {
                    @Override
                    public void onSuccess(String s) {
                        ALog.e("请求结果:" + s);
                        if (BaseUtil.isStringMeaningless(s)) {
                            showErrorContent(mOnDoHttpListener.errorMessage());
                            return;
                        }
                        T mT = GsonUtil.parseObject(s, mOnDoHttpListener.getTClass());
                        if (mT == null) {
                            showErrorContent(mOnDoHttpListener.errorMessage());
                        } else {
                            mOnDoHttpListener.buildAndShow(mT);
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        showErrorContent(mOnDoHttpListener.errorMessage());
                    }
                });
    }
}
