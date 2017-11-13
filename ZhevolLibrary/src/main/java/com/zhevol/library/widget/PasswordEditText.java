package com.zhevol.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.zhevol.library.R;

/**
 * 带有查看明文密码和密文密码开关的编辑框<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class PasswordEditText extends AppCompatEditText {
    // 模式的显示图标
    @DrawableRes
    private int mShowPwdIcon = R.mipmap.icon_mp_text_hide;

    // 模式的加密图标
    @DrawableRes
    private int mHidePwdIcon = R.mipmap.icon_mp_text_show;

    /**
     * 是否显示指示器
     */
    private boolean mIsShowPwdIcon;
    /**
     * 显示隐藏指示器
     */
    private Drawable mDrawableSide;

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFields(attrs, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFields(attrs, defStyleAttr);
    }

    /**
     * 初始化布局
     *
     * @param attrs        属性集合
     * @param defStyleAttr 默认 Style 属性
     */
    public void initFields(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            // 获取属性信息
            TypedArray styles = getContext().getTheme().obtainStyledAttributes(attrs,
                    R.styleable.PasswordEditText, defStyleAttr, 0);
            try {
                // 根据参数, 设置Icon
                mShowPwdIcon = styles.getResourceId(R.styleable.PasswordEditText_zlIconShow, mShowPwdIcon);
                mHidePwdIcon = styles.getResourceId(R.styleable.PasswordEditText_zlIconHide, mHidePwdIcon);
                mIsShowPwdIcon = styles.getBoolean(R.styleable.PasswordEditText_zlIsShowPwd, true);
            } finally {
                styles.recycle();
            }
        }

        // 密码状态
        restorePasswordIconVisibility(!mIsShowPwdIcon);
        if (mIsShowPwdIcon) {
            togglePasswordIconVisibility();
        }
    }

    // 存储状态
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        return new PwdSavedState(state, mIsShowPwdIcon);
    }

    // 恢复状态
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        PwdSavedState savedState = (PwdSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mIsShowPwdIcon = savedState.isShowingIcon();
        restorePasswordIconVisibility(mIsShowPwdIcon);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDrawableSide == null) {
            return super.onTouchEvent(event);
        }
        final Rect bounds = mDrawableSide.getBounds();
        // 点击的位置
        final int x = (int) event.getRawX();

        int iconX = (int) getTopRightCorner().x;
        // Icon的位置
        int leftIcon = iconX - bounds.width();

        // 大于Icon的位置, 才能触发点击
        if (x >= leftIcon && getText().toString().length() != 0) {
            togglePasswordIconVisibility(); // 变换状态
            event.setAction(MotionEvent.ACTION_CANCEL);
            return false;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取上右角的距离
     *
     * @return PointF 对象
     */
    public PointF getTopRightCorner() {
        float[] src = new float[8];
        float[] dst = new float[]{0, 0, getWidth(), 0, 0, getHeight(), getWidth(), getHeight()};
        getMatrix().mapPoints(src, dst);
        return new PointF(getX() + src[2], getY() + src[3]);
    }

    /**
     * 显示密码提示标志
     *
     * @param shouldShowIcon 是否展示密码提示标识
     */
    private void showPasswordVisibilityIndicator(boolean shouldShowIcon) {
        if (shouldShowIcon) {
            Drawable drawable = mIsShowPwdIcon ?
                    ContextCompat.getDrawable(getContext(), mHidePwdIcon) :
                    ContextCompat.getDrawable(getContext(), mShowPwdIcon);

            // 在最右侧显示图像
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

            mDrawableSide = drawable;
        } else {
            // 不显示周边的图像
            setCompoundDrawables(null, null, null, null);
            mDrawableSide = null;
        }
    }

    /**
     * 变换状态
     */
    private void togglePasswordIconVisibility() {
        mIsShowPwdIcon = !mIsShowPwdIcon;
        restorePasswordIconVisibility(mIsShowPwdIcon);
        showPasswordVisibilityIndicator(true);
    }

    /**
     * 设置密码指示器的状态
     *
     * @param isShowPwd 是否展示明文密码
     */
    private void restorePasswordIconVisibility(boolean isShowPwd) {
        if (isShowPwd) {
            // 可视密码输入
            setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            // 非可视密码状态
            setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        }

        // 移动光标
        setSelection(getText().length());
    }

    /**
     * 存储密码状态, 显示Icon的位置
     */
    private static class PwdSavedState extends View.BaseSavedState {

        private final boolean mShowingIcon;

        private PwdSavedState(Parcelable superState, boolean showingIcon) {
            super(superState);
            mShowingIcon = showingIcon;
        }

        private PwdSavedState(Parcel in) {
            super(in);
            mShowingIcon = in.readByte() != 0;
        }

        boolean isShowingIcon() {
            return mShowingIcon;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeByte((byte) (mShowingIcon ? 1 : 0));
        }

        public static final Creator<PwdSavedState> CREATOR = new Creator<PwdSavedState>() {

            @Override
            public PwdSavedState createFromParcel(Parcel in) {
                return new PwdSavedState(in);
            }

            @Override
            public PwdSavedState[] newArray(int size) {
                return new PwdSavedState[size];
            }
        };
    }
}
