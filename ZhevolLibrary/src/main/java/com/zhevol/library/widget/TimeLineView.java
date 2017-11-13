package com.zhevol.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.zhevol.library.R;

import java.util.List;

/**
 * 时间轴控件<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class TimeLineView extends LinearLayout {

    private int mLineMarginLeft;
    private int mLineMarginTop;
    private int mLineStrokeWidth;
    private int mLineColor;
    private int mPointSize;
    private int mPointColor;

    /**
     * 线的画笔
     */
    private Paint mLinePaint;
    /**
     * 点的画笔
     */
    private Paint mPointPaint;

    /**
     * 第一个点的位置，X坐标
     */
    private int mFirstX;
    /**
     * 第一个点的位置，Y坐标
     */
    private int mFirstY;
    /**
     * 最后一个图标的位置，X坐标
     */
    private int mLastX;
    /**
     * 最后一个图标的位置，Y坐标
     */
    private int mLastY;

    public TimeLineView(Context context) {
        this(context, null);
    }

    public TimeLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimeLineView);
        mLineMarginLeft = ta.getDimensionPixelSize(R.styleable.TimeLineView_lineMarginLeft,
                context.getResources().getDimensionPixelSize(R.dimen.dp_08));
        mLineMarginTop = ta.getDimensionPixelSize(R.styleable.TimeLineView_lineMarginTop,
                context.getResources().getDimensionPixelSize(R.dimen.dp_04));
        mLineStrokeWidth = ta.getDimensionPixelSize(R.styleable.TimeLineView_lineStrokeWidth,
                context.getResources().getDimensionPixelSize(R.dimen.dp_01));
        mLineColor = ta.getColor(R.styleable.TimeLineView_lineLineColor, Color.parseColor("#FFD6D6D6"));
        mPointSize = ta.getDimensionPixelSize(R.styleable.TimeLineView_linePointSize,
                context.getResources().getDimensionPixelSize(R.dimen.dp_04));
        mPointColor = ta.getColor(R.styleable.TimeLineView_linePointColor, Color.parseColor("#FFEEEEEE"));
        ta.recycle();
        setWillNotDraw(false);
        initView();
    }

    /**
     * 初始化相关参数
     */
    private void initView() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineStrokeWidth);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setDither(true);
        mPointPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画左边的线
        drawTimeline(canvas);
    }

    /**
     * 画左边的时间轴线
     *
     * @param canvas 画布
     */
    private void drawTimeline(Canvas canvas) {
        int childCount = getChildCount();
        if (childCount > 0) {
            if (childCount > 1) {
                // 大于1，证明至少有2个，也就是第一个和第二个之间连成线，第一个和最后一个分别有点和icon
                drawFirstPoint(canvas);
                drawLastIcon(canvas);
                drawBetweenLine(canvas);
            } else if (childCount == 1) {
                drawFirstPoint(canvas);
            }
        }
    }

    /**
     * 画第一个点
     *
     * @param canvas 画布
     */
    private void drawFirstPoint(Canvas canvas) {
        View child = getChildAt(0);
        if (child != null) {
            int top = child.getTop();
            mFirstX = mLineMarginLeft;
            mFirstY = top + child.getPaddingTop() + mLineMarginTop;

            // 画圆
            mPointPaint.setColor(mLineColor);
            canvas.drawCircle(mFirstX, mFirstY, mPointSize, mPointPaint);
            mPointPaint.setColor(mPointColor);
            canvas.drawCircle(mFirstX, mFirstY, mPointSize - mLineStrokeWidth, mPointPaint);
        }
    }

    /**
     * 画最后一个圆点
     *
     * @param canvas 画布
     */
    private void drawLastIcon(Canvas canvas) {
        View child = getChildAt(getChildCount() - 1);
        if (child != null) {
            int top = child.getTop();
            mLastX = mLineMarginLeft;
            mLastY = top + child.getPaddingTop() + mLineMarginTop;

            // 画图
            mPointPaint.setColor(mLineColor);
            canvas.drawCircle(mLastX, mLastY, mPointSize, mPointPaint);
            mPointPaint.setColor(mPointColor);
            canvas.drawCircle(mLastX, mLastY, mPointSize - mLineStrokeWidth, mPointPaint);
        }
    }

    /**
     * 画中间的线
     *
     * @param canvas 画布
     */
    private void drawBetweenLine(Canvas canvas) {
        // 从开始的点到最后的图标之间，画一条线
        canvas.drawLine(mFirstX, mFirstY, mLastX, mLastY - mPointSize, mLinePaint);
        for (int i = 0; i < getChildCount() - 1; i++) {
            // 画圆
            int top = getChildAt(i).getTop();
            int y = top + getChildAt(i).getPaddingTop() + mLineMarginTop;
            mPointPaint.setColor(mLineColor);
            canvas.drawCircle(mFirstX, y, mPointSize, mPointPaint);
            mPointPaint.setColor(mPointColor);
            canvas.drawCircle(mFirstX, y, mPointSize - mLineStrokeWidth, mPointPaint);
        }
    }

    /**
     * 获取左边距
     *
     * @return 左边距
     */
    public int getLineMarginLeft() {
        return mLineMarginLeft;
    }

    /**
     * 设置左边距
     *
     * @param lineMarginLeft 左边距
     */
    public void setLineMarginLeft(int lineMarginLeft) {
        this.mLineMarginLeft = lineMarginLeft;
        invalidate();
    }

    /**
     * 添加View
     *
     * @param views View的集合
     */
    public void addViews(List<View> views) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        for (View view : views) {
            addView(view);
        }
    }
}
