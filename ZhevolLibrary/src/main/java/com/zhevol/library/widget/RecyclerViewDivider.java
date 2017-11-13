package com.zhevol.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView 的万能分隔线<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private Drawable mDivider;
    /**
     * 分割线高度，默认为1px
     */
    private int mDividerHeight = 2;
    /**
     * 列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
     */
    private int mOrientation;
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    /**
     * 是否显示底部的分隔线，默认为true-显示
     */
    private boolean isBottomDividerVisible;
    /**
     * 是否显示第一条分隔线，默认为true-显示
     */
    private boolean isFirstDividerVisible;

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context     上下文
     * @param orientation 列表方向
     */
    public RecyclerViewDivider(Context context, int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mOrientation = orientation;

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        isBottomDividerVisible = true;
        isFirstDividerVisible = true;
    }

    /**
     * 自定义分割线
     *
     * @param context     上下文
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    public RecyclerViewDivider(Context context, int orientation, int drawableId) {
        this(context, orientation);
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
        isBottomDividerVisible = true;
        isFirstDividerVisible = true;
    }

    /**
     * 自定义分割线
     *
     * @param context       上下文
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public RecyclerViewDivider(Context context, int orientation, int dividerHeight, int dividerColor) {
        this(context, orientation);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        isBottomDividerVisible = true;
        isFirstDividerVisible = true;
    }


    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDividerHeight);
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 设置底部的分隔线的是否显示
     *
     * @param bottomDividerVisible 是否显示
     * @return 分隔线对象
     */
    public RecyclerViewDivider setBottomDividerVisible(boolean bottomDividerVisible) {
        isBottomDividerVisible = bottomDividerVisible;
        return this;
    }

    /**
     * 设置是否显示第一条分隔线
     *
     * @param firstDividerVisible 是否显示
     * @return 分隔线对象
     */
    public RecyclerViewDivider setFirstDividerVisible(boolean firstDividerVisible) {
        isFirstDividerVisible = firstDividerVisible;
        return this;
    }

    /**
     * 绘制横向 item 分割线
     *
     * @param canvas 画笔
     * @param parent 父
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = (isFirstDividerVisible ? 0 : 1); i < (isBottomDividerVisible ? childSize : childSize - 1); i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    /**
     * 绘制纵向 item 分割线
     *
     * @param canvas 画笔
     * @param parent 父
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
