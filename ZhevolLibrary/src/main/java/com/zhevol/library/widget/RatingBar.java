package com.zhevol.library.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.zhevol.library.R;
import com.zhevol.library.listener.OnRatingChangedListener;

/**
 * 自定义的评分控件<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class RatingBar extends LinearLayout {

    /**
     * 默认星星总数
     */
    private static final int DEFAULT_STAR_NUM = 5;

    /**
     * 评分条的正常的星星总数
     */
    private int mStarNum;
    /**
     * 正常情况下的 Drawable
     */
    private Drawable mStarDrawableNormal;
    /**
     * 选中情况下的 Drawable
     */
    private Drawable mStarDrawableProgress;
    /**
     * 是否只指示值，不可以点击。默认是 false 可点击。
     */
    private boolean mIsIndicator;
    /**
     * 星星之间的间距，默认8
     */
    private int mStarSpacing;
    /**
     * 是否开启星星 Progress 的动画，默认 true 开启
     */
    private boolean mAnimation;
    /**
     * 评分变化的监听器
     */
    private OnRatingChangedListener mOnRatingChangedListener;

    /**
     * 当前评分的值，float 型的
     */
    @FloatRange(from = 0.f, to = 1.f)
    private float mRating;

    public RatingBar(Context context) {
        this(context, null);
    }

    public RatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public RatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatingBar);
        mStarNum = a.getInteger(R.styleable.RatingBar_starNum, DEFAULT_STAR_NUM);
        mStarDrawableNormal = a.getDrawable(R.styleable.RatingBar_starDrawableNormal);
        mStarDrawableProgress = a.getDrawable(R.styleable.RatingBar_starDrawableProgress);
        mIsIndicator = a.getBoolean(R.styleable.RatingBar_isIndicator, false);
        mStarSpacing = a.getDimensionPixelSize(R.styleable.RatingBar_starSpacing,
                context.getResources().getDimensionPixelSize(R.dimen.dp_08));
        mAnimation = a.getBoolean(R.styleable.RatingBar_animation, true);
        a.recycle();
        mRating = 0.f;

        if (mStarDrawableNormal == null) {
            mStarDrawableNormal = ContextCompat.getDrawable(context, R.drawable.rating_bar_normal);
        }
        if (mStarDrawableProgress == null) {
            mStarDrawableProgress = ContextCompat.getDrawable(context, R.drawable.rating_bar_progress);
        }

        for (int i = 0; i < mStarNum; i++) {
            AppCompatImageView ivStar = buildStarImageView(context, i);
            ivStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mIsIndicator) {
                        int clickIndex = indexOfChild(v) + 1;
                        setStarProgress(clickIndex);
                        mRating = (float) clickIndex / mStarNum;
                        if (mOnRatingChangedListener != null) {
                            mOnRatingChangedListener.onRatingChanged(RatingBar.this, mRating);
                        }
                    }
                }
            });
            addView(ivStar);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getImageViewMaxEdgeLength(), View.MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 创建星星的 View
     *
     * @param context 上下文
     * @param index   下标
     * @return AppCompatImageView
     */
    private AppCompatImageView buildStarImageView(Context context, int index) {
        AppCompatImageView imageView = new AppCompatImageView(context);
        int edgeLength = Math.round(getImageViewMaxEdgeLength());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(edgeLength, edgeLength);
        if (index != mStarNum - 1) {
            lp.setMargins(0, 0, mStarSpacing, 0);
        }
        imageView.setLayoutParams(lp);
        imageView.setImageDrawable(mStarDrawableNormal);
        imageView.setMaxWidth(edgeLength);
        imageView.setMaxHeight(edgeLength);
        return imageView;
    }

    /**
     * 设置点击的图片之前的图片为 Progress 状态
     *
     * @param clickIndex 点击的图片的下标
     */
    private void setStarProgress(int clickIndex) {
        clickIndex = clickIndex > mStarNum ? mStarNum : clickIndex;
        clickIndex = clickIndex < 0 ? 0 : clickIndex;
        for (int i = 0; i < clickIndex; ++i) {
            ((AppCompatImageView) getChildAt(i)).setImageDrawable(mStarDrawableProgress);
            if (mAnimation) {
                ObjectAnimator alpha = ObjectAnimator.ofFloat(getChildAt(i), "alpha", 0f, 1f);
                alpha.setDuration(500);
                alpha.setInterpolator(new DecelerateInterpolator());
                //设置动画重复次数，这里-1代表无限
                alpha.setRepeatCount(0);
                //设置动画循环模式。
                alpha.setRepeatMode(ValueAnimator.RESTART);
                alpha.start();
            }
        }
        for (int i = mStarNum - 1; i >= clickIndex; --i) {
            ((AppCompatImageView) getChildAt(i)).setImageDrawable(mStarDrawableNormal);
        }
    }

    /**
     * 获取普通状态下的图片的长宽的最大值
     *
     * @return 长宽的最大值
     */
    private int getImageViewMaxEdgeLength() {
        int drawableWidth = mStarDrawableNormal.getIntrinsicWidth();
        int drawableHeight = mStarDrawableNormal.getIntrinsicHeight();
        return drawableWidth > drawableHeight ? drawableWidth : drawableHeight;
    }

    /**
     * 获取星星总数
     *
     * @return 总数
     */
    public int getStarNum() {
        return mStarNum;
    }

    /**
     * 设置评分值，值区间为0-1
     *
     * @param rating 评分值
     */
    public void setRating(@FloatRange(from = 0.f, to = 1.f) float rating) {
        this.mRating = rating;
        int clickIndex = (int) (rating * mStarNum);
        setStarProgress(clickIndex);
    }

    /**
     * 获取评分的值
     *
     * @return 评分值
     */
    public float getRating() {
        return mRating;
    }

    /**
     * 设置评分变化的事件监听器
     *
     * @param onRatingChangedListener 评分事件监听器
     */
    public void setOnRatingChangedListener(OnRatingChangedListener onRatingChangedListener) {
        this.mOnRatingChangedListener = onRatingChangedListener;
    }
}
