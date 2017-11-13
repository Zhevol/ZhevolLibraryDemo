package com.zhevol.library.listener;

import android.animation.Animator;

/**
 * 动画播放的事件监听器<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public abstract class OnAnimatorListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        animationEnd();
    }

    /**
     * 动画结束后需要执行的操作
     */
    public abstract void animationEnd();

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
