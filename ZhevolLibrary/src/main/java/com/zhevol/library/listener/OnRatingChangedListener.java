package com.zhevol.library.listener;

import android.support.annotation.FloatRange;

import com.zhevol.library.widget.RatingBar;

/**
 * RatingBar 的事件监听器<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public interface OnRatingChangedListener {

    /**
     * 评分变化时的回调方法
     *
     * @param mpRatingBar 绑定此方法的控件
     * @param rating      评分
     */
    void onRatingChanged(RatingBar mpRatingBar, @FloatRange(from = 0f, to = 1.f) float rating);
}
