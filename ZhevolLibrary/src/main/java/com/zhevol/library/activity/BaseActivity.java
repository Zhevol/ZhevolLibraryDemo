package com.zhevol.library.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.blankj.utilcode.util.ActivityUtils;
import com.zhevol.library.R;
import com.zhevol.library.application.BaseApplication;
import com.zhevol.library.config.IntentConfig;

import java.io.Serializable;

import butterknife.ButterKnife;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * Activity 的基类，抽取了一些基础的方法，方便可控化的管理。其他 Activity 都应继承此类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetContent();
        setContentView(bindLayoutId());
        bindViews();
        bindListeners();
        View rootView = findViewById(android.R.id.content);
        setButtonOnClickListener(rootView);
        bindData(savedInstanceState);
        BaseApplication.getInstance().addActivity(this);
    }

    /**
     * 在设置布局之前执行的方法
     */
    protected void doBeforeSetContent() {
        supportRequestWindowFeature(FEATURE_NO_TITLE);
    }

    /**
     * 返回一个用于显示界面布局的id
     *
     * @return 界面的布局id
     */
    protected abstract int bindLayoutId();

    /**
     * 采用ButterKnife绑定控件的方式，所以在这个方法里面绑定ButterKnife
     * 如果要在Activity中增加控件或者新的View，则重写并添加{@code super.initView();}即可
     */
    protected void bindViews() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化事件监听器，此方法是空实现，如果要在子 Activity 中设置 Listener，直接重写此方法
     */
    protected void bindListeners() {

    }

    /**
     * 查找Button、ImageButton并设置单击监听器
     *
     * @param view 要设置监听器的view
     */
    private void setButtonOnClickListener(View view) {
        //遍历view里面所有的Button和ImageButton
        if (view instanceof AppCompatImageButton || view instanceof ImageButton
                || view instanceof AppCompatButton || view instanceof Button) {
            view.setOnClickListener(this);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                setButtonOnClickListener(childView);
            }
        }
    }

    /**
     * 初始化数据，并且显示到界面上，此方法是空实现，如果要在子 Activity 中设置相关的数据，直接重写此方法
     */
    protected void bindData(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnFinishActivity) {
            //处理共同的返回按钮的操作
            finishActivity();
        } else {
            //如果单击的不是返回按钮，则还是由子类去做处理
            onClick(id);
        }
    }

    /**
     * 退出当前Activity
     * 如果要修改返回按钮的相关执行过程，则应重写此方法
     */
    protected void finishActivity() {
        finish();
    }

    /**
     * 单击事件在这里面处理，此方法是空实现。
     * 如果子 Activity 中有相关的按钮控件需要设置点击监听，直接重写此方法
     *
     * @param id 单击控件的id
     */
    protected void onClick(int id) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // -------------------------------- 点击空白处隐藏输入法 ---------------------------------- //

    /**
     * 获取屏幕触摸事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                //隐藏软键盘
                IBinder token = view.getWindowToken();
                if (token != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定是否需要隐藏软键盘
     *
     * @param v  当前的拥有焦点的 View
     * @param ev 事件
     * @return 是否需要隐藏
     */
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }

    // -------------------------------- 跳转 Activity ---------------------------------- //

    /**
     * 跳转 Activity，默认不关闭当前的 Activity
     *
     * @param cls 要跳转到的目标 Activity
     */
    protected void startActivity(Class<?> cls) {
        startActivity(cls, false);
    }

    /**
     * 跳转 Activity
     *
     * @param cls    要跳转到的目标 Activity
     * @param finish 是否 finish 当前的 Activity
     */
    protected void startActivity(Class<?> cls, boolean finish) {
        ActivityUtils.startActivity(this, cls);
        if (finish) {
            finish();
        }
    }

    /**
     * * 跳转 Activity，并传递一个 Bundle 到目标Activity
     *
     * @param cls        要跳转到的目标 Activity
     * @param intentData 要携带的数据对象
     */
    protected void startActivity(Class<?> cls, Serializable intentData) {
        Intent intent = new Intent(this, cls);
        if (intentData != null) {
            intent.putExtra(IntentConfig.BASE_INTENT_DATA, intentData);
        }
        startActivity(intent);
    }
}
