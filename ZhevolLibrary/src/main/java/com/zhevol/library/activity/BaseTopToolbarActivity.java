package com.zhevol.library.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.blankj.utilcode.util.BarUtils;
import com.zhevol.library.R;

/**
 * 顶部有 Toolbar 的 Activity 的基类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public abstract class BaseTopToolbarActivity extends BaseActivity {

    protected Toolbar tbTopToolbar;
    protected AppCompatTextView tvTopToolbarTitle;
    protected AppCompatImageButton cibTopToolbarRight;
    protected AppCompatButton btnTopToolbarRight;

    @Override
    protected void bindViews() {
        super.bindViews();
        tbTopToolbar = findViewById(R.id.tbTopToolbar);
        tvTopToolbarTitle = findViewById(R.id.tvTopToolbarTitle);
        cibTopToolbarRight = findViewById(R.id.cibTopToolbarRight);
        btnTopToolbarRight = findViewById(R.id.btnTopToolbarRight);

        tbTopToolbar.setTitle("");
        tvTopToolbarTitle.setText(bindTopToolbarTitle());
        tbTopToolbar.setNavigationIcon(R.mipmap.icon_top_toolbar_back);
        setSupportActionBar(tbTopToolbar);

        if (hideRightImageButton()) {
            cibTopToolbarRight.setVisibility(View.GONE);
        }
        if (hideRightButton()) {
            btnTopToolbarRight.setVisibility(View.GONE);
        }
    }

    /**
     * 是否隐藏右边的 ImageButton
     * 默认隐藏，如果要显示，请重写此函数，且不能调用 super
     * 此方法和 {@link #hideRightButton()} 同时只能重写一个
     *
     * @return true 隐藏右边的 ImageButton，默认隐藏，false 不隐藏
     */
    protected boolean hideRightImageButton() {
        return true;
    }

    /**
     * 是否隐藏右边的更多 Button
     * 默认隐藏，如果要显示，请重写此函数，且不能调用 super
     * 此方法和 {@link #hideRightImageButton()} 同时只能重写一个
     *
     * @return true 隐藏右边的更多 Button，默认隐藏，false 不隐藏
     */
    protected boolean hideRightButton() {
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        BarUtils.addMarginTopEqualStatusBarHeight(tbTopToolbar);
        tbTopToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNavigation();
            }
        });
    }

    /**
     * 顶部Toolbar的左按钮的点击事件，可重写此方法来修改
     */
    protected void clickNavigation() {
        finish();
    }

    /**
     * 绑定顶部的Toolbar上的文字
     *
     * @return 标题文字内容
     */
    protected abstract String bindTopToolbarTitle();
}
