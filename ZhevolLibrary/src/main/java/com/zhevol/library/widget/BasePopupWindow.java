package com.zhevol.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.zhevol.library.R;

import butterknife.ButterKnife;

/**
 * PopupWindow 的基类，所有其他的 PopupWindow 都继承此类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public abstract class BasePopupWindow implements View.OnClickListener {

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * PopupWindow 控件
     */
    protected PopupWindow mPopupWindow;
    /**
     * 在这个 View 的下方弹出此 PopupWindow
     */
    protected View mAnchor;

    public BasePopupWindow(Context context, View anchor) {
        this.mAnchor = anchor;
        this.mContext = context;
        View rootView = LayoutInflater.from(context).inflate(buildPopupWindowLayoutId(), null);
        mPopupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setWidth(anchor.getWidth());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        ButterKnife.bind(this, rootView);
        setButtonOnClickListener(rootView);
        bindPopupWindowData();
        bindPopupWindowListeners();
    }

    /**
     * 绑定 PopupWindow 的相关数据
     * 此方法是空实现方法，如果要绑定，请重写此方法并在里面绑定相关的数据即可
     */
    protected void bindPopupWindowData() {

    }

    /**
     * 绑定其他控件的 Listener
     * 此方法是空实现方法，如果要绑定，请重写此方法并在里面实现相关的事件监听即可
     */
    protected void bindPopupWindowListeners() {

    }

    /**
     * 想要绑定的 PopupWindow 的布局ID
     *
     * @return 布局ID
     */
    protected abstract int buildPopupWindowLayoutId();

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
     * 打开 PopupWindow
     */
    public void show() {
        if (mPopupWindow == null) {
            return;
        }
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(mAnchor, 0, mContext.getResources().getDimensionPixelSize(R.dimen.dp_02));
        } else {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 关闭 PopupWindow
     */
    public void close() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        onClick(v.getId());
    }

    /**
     * 单击事件在这里面处理
     * 此方法是空实现，如果有相关的按钮的点击事件需要处理，重写此方法并处理即可
     *
     * @param id 单击控件的id
     */
    protected void onClick(int id) {

    }

    /**
     * 设置 PopupWindow 的宽度
     *
     * @param width 宽度
     */
    private void setPopupWindowWidth(int width) {
        mPopupWindow.setWidth(width);
    }

}
