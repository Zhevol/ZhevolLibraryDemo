package com.zhevol.library.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zhevol.library.listener.OnAnimatorListener;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 应用相关方法类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class BaseUtil {

    private static final String NULL = "null";

    /**
     * 生成一个和状态栏大小相同的彩色矩形条
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    public static View createStatusBarView(Activity activity, int color) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 判断字符串是不是无意义的，如果是返回true，否则返回false
     *
     * @param resStr 源字符串
     * @return 是否无意义
     */
    public static boolean isStringMeaningless(String resStr) {
        if (resStr == null || "".equals(resStr) || NULL.equals(resStr) || resStr.length() == 0) {
            return true;
        } else {
            for (int i = 0, len = resStr.length(); i < len; ++i) {
                if (!Character.isWhitespace(resStr.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * 将传入的时间转换为yyyy年MM月dd日的样式
     *
     * @param mDate 传入的时间字符串
     * @return 中文格式的时间字符串
     */

    public static String getChineseStyleDate(String mDate) {
        try {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = myFormat.parse(mDate);
            myFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            mDate = myFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mDate;
    }

    /**
     * 从Raw资源文件中读取Json字符串
     *
     * @param context 上下文
     * @param resId   资源ID
     * @return Json字符串
     */
    public static String getJsonFromRawResource(Context context, int resId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resId);
            byte[] bt = new byte[inputStream.available()];
            int readResult = inputStream.read(bt);
            if (readResult != -1) {
                return new String(bt);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 展示Tab切换时的动画
     *
     * @param screenWidth 值（传入正负以控制动画的方向）
     */
    public static AnimatorSet displayTabChangeAnimator(View childView, int screenWidth, OnAnimatorListener listener) {
        AnimatorSet animatorSet = new AnimatorSet();
        //移出屏幕
        ObjectAnimator llOut = ObjectAnimator.ofFloat(childView, "translationX", 0, -screenWidth);
        //设为不可见
        ObjectAnimator llAlphaOut = ObjectAnimator.ofFloat(childView, "alpha", 1f, 0f);
        //移到最右端
        ObjectAnimator llTranslate = ObjectAnimator.ofFloat(childView, "translationX", -screenWidth, screenWidth);
        //移入屏幕
        ObjectAnimator llIn = ObjectAnimator.ofFloat(childView, "translationX", screenWidth, 0);
        //设为可见
        ObjectAnimator llAlphaIn = ObjectAnimator.ofFloat(childView, "alpha", 0f, 1f);
        animatorSet.setDuration(100);
        animatorSet.play(llOut).with(llAlphaOut);
        animatorSet.start();
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(150);
        animatorSet.setStartDelay(100);
        animatorSet.play(llIn).with(llAlphaIn).after(llTranslate);
        animatorSet.addListener(listener);
        animatorSet.start();
        return animatorSet;
    }

    /**
     * 获取指定转换类型的当前时间
     *
     * @param format 转换类型
     * @return 转换后的时间
     */
    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取日历中的相对于今天的某一天
     *
     * @param offSet 偏移量
     * @return 某一天的字符串
     */
    public static String getDateByOffset(int offSet) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, offSet);
        return myFormat.format(c.getTime());
    }

}
