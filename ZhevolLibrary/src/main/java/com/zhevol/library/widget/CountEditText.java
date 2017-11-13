package com.zhevol.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.zhevol.library.R;

import java.util.regex.Pattern;

/**
 * 带计数器的 EditText<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class CountEditText extends LinearLayout {


    /**
     * 计数器位置左上
     */
    private static final int LEFT_TOP = 0;

    /**
     * 计数器位置左下
     */
    private static final int LEFT_BOTTOM = 1;

    /**
     * 计数器位置右上
     */
    private static final int RIGHT_TOP = 2;

    /**
     * 计数器位置右下
     */
    private static final int RIGHT_BOTTOM = 3;

    /**
     * 计数器位置
     */
    private int mCountPosition;

    /**
     * 计数的TextView
     */
    private AppCompatTextView mCountTextView;
    /**
     * 内容编辑区域
     */
    private AppCompatEditText mContentEditText;

    /**
     * 最大长度，默认100
     */
    private int mMaxLength;
    /**
     * 提示文字的颜色，默认#FF666666
     */
    private int mTextColorHint;
    /**
     * 提示文字，默认为请输入内容
     */
    private String mHint;
    /**
     * 文本字体大小，默认14
     */
    private float mContentTextSize;
    /**
     * 文本内容颜色，默认#FF333333
     */
    private int mContentTextColor;
    /**
     * 计数器字体大小，默认14
     */
    private float mCountTextSize;
    /**
     * 计数器内容颜色，默认#FF666666
     */
    private int mCountTextColor;

    /**
     * 文本内容变化监听器
     */
    private CountTextWatcher mCountTextWatcher;

    public CountEditText(Context context) {
        this(context, null);
    }

    public CountEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        int paddingDimen = context.getResources().getDimensionPixelSize(R.dimen.dp_16);
        setPadding(paddingDimen, paddingDimen, paddingDimen, paddingDimen);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountEditText);
        if (typedArray != null) {
            int position = typedArray.getInt(R.styleable.CountEditText_countPosition, 3);
            if (position == LEFT_TOP) {
                mCountPosition = LEFT_TOP;
            } else if (position == LEFT_BOTTOM) {
                mCountPosition = LEFT_BOTTOM;
            } else if (position == RIGHT_TOP) {
                mCountPosition = RIGHT_TOP;
            } else {
                mCountPosition = RIGHT_BOTTOM;
            }
            mMaxLength = typedArray.getInt(R.styleable.CountEditText_maxLength, 100);
            mTextColorHint = typedArray.getColor(R.styleable.CountEditText_textColorHint, Color.parseColor("#FF666666"));
            mHint = typedArray.getString(R.styleable.CountEditText_hint);
            int textSize = context.getResources().getDimensionPixelSize(R.dimen.sp_14);
            mContentTextSize = typedArray.getDimensionPixelSize(R.styleable.CountEditText_contentTextSize, textSize);
            mContentTextColor = typedArray.getColor(R.styleable.CountEditText_contentTextColor, Color.parseColor("#FF333333"));
            mCountTextSize = typedArray.getDimensionPixelSize(R.styleable.CountEditText_countTextSize, textSize);
            mCountTextColor = typedArray.getColor(R.styleable.CountEditText_countTextColor, Color.parseColor("#FF666666"));
            if (TextUtils.isEmpty(mHint)) {
                mHint = context.getString(R.string.countEditTextHint);
            }
            typedArray.recycle();
        }
        //初始化控件
        initWidget(context);
        //设置计数器文本
        mCountTextView.setGravity(Gravity.CENTER_VERTICAL);
        mCountTextView.setText(context.getString(R.string.countTextDefault, 0, mMaxLength));
        mCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCountTextSize);
        mCountTextView.setTextColor(mCountTextColor);

        //设置文本输入框
        mContentEditText.setGravity(Gravity.TOP);
        mContentEditText.setHint(mHint);
        mContentEditText.setBackground(null);
        mContentEditText.setHintTextColor(mTextColorHint);
        mContentEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentTextSize);
        mContentEditText.setTextColor(mContentTextColor);
        mContentEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        mContentEditText.setSingleLine(false);
        InputFilter[] inputFilters = {new InputFilter.LengthFilter(mMaxLength), emojiFilter};
        mContentEditText.setFilters(inputFilters);

        if (mCountPosition == LEFT_TOP || mCountPosition == RIGHT_TOP) {
            addView(mCountTextView);
            addView(mContentEditText);
        } else {
            addView(mContentEditText);
            addView(mCountTextView);
        }

        //设置文本编辑监听器
        mContentEditText.addTextChangedListener(mTextWatcher);
    }

    /**
     * 初始化内容的输入框和计数的文本控件
     */
    private void initWidget(Context context) {
        mCountTextView = new AppCompatTextView(context);
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (mCountPosition == LEFT_TOP || mCountPosition == LEFT_BOTTOM) {
            tvLp.gravity = Gravity.START;
            tvLp.setMargins(context.getResources().getDimensionPixelSize(R.dimen.dp_04), 0, 0, 0);
        } else {
            tvLp.gravity = Gravity.END;
            tvLp.setMargins(0, 0, context.getResources().getDimensionPixelSize(R.dimen.dp_04), 0);
        }
        mCountTextView.setLayoutParams(tvLp);
        mContentEditText = new AppCompatEditText(context);
        LayoutParams etLp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        etLp.weight = 1.f;
        mContentEditText.setLayoutParams(etLp);
    }


    /**
     * 获取输入内容
     *
     * @return 内容
     */
    public String getText() {
        return mContentEditText.getText().toString();
    }

    /**
     * 设置文本
     *
     * @param text 文本内容
     */
    public void setText(String text) {
        mContentEditText.setText(text);
    }

    /**
     * 获取提示的内容
     *
     * @return 内容
     */
    public String getHint() {
        return mContentEditText.getHint().toString();
    }

    /**
     * 设置提示的文本
     *
     * @param hintText 提示的文本内容
     */
    public void setHint(String hintText) {
        mContentEditText.setHint(hintText);
    }

    /**
     * 禁止输入 Emoji 表情
     */
    private static InputFilter emojiFilter = new InputFilter() {
        //此种正则表达式的筛选，还有6个 Emoji 表情无法屏蔽，后期再考虑优化（搜狗输入法）
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]" +
                "|[\u2600-\u27ff]|\u2122|[\u2196-\u2199]|[\u23e9-\u23ea]|\u303d|\u25b6|\u25c0" +
                "|[\u2b05-\u2b07]|\u2b1c|[\u2b50-\u2b5c]|[\u3297-\u3299]|\u0023|[\u0030-\u0039]" +
                "|\u00a9|\u00ae", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (emoji.matcher(source).find()) {
                return "";
            } else {
                return null;
            }
        }
    };

    /**
     * 文本编辑的事件监听
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mContentEditText.getSelectionStart();
            editEnd = mContentEditText.getSelectionEnd();
            // 先去掉监听器，否则会出现栈溢出
            mContentEditText.removeTextChangedListener(mTextWatcher);
            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            while (calculateLength(s.toString()) > mMaxLength) {
                // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            // 恢复监听器
            mContentEditText.addTextChangedListener(mTextWatcher);
            setLeftCount();
            if (mCountTextWatcher != null) {
                mCountTextWatcher.afterTextChanged(getText());
            }
        }
    };

    /**
     * 刷新剩余输入字数
     */

    private void setLeftCount() {
        mCountTextView.setText(getResources().getString(R.string.countTextDefault, getInputCount(), mMaxLength));
    }

    /**
     * 获取用户输入内容字数
     */
    private long getInputCount() {
        return calculateLength(getText());
    }

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点
     * 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param cs 内容
     * @return 长度
     */
    public static long calculateLength(CharSequence cs) {
        double len = 0;
        for (int i = 0; i < cs.length(); i++) {
            int tmp = (int) cs.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 1;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    /**
     * 设置文本内容变化监听器
     *
     * @param countTextWatcher 监听器
     */
    public void addCountTextWatcher(CountTextWatcher countTextWatcher) {
        this.mCountTextWatcher = countTextWatcher;
    }

    /**
     * 计数的文本变化监听器
     * 可让用户实现文本变化的监听
     * 并在 {@link #afterTextChanged(String)} 方法中做对应的处理
     */
    public interface CountTextWatcher {
        /**
         * 文本发生变化后回调的方法
         *
         * @param text 当前的文本内容
         */
        void afterTextChanged(String text);
    }
}