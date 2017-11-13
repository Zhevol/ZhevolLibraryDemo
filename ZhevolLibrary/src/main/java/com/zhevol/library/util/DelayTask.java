package com.zhevol.library.util;

import android.content.Context;
import android.os.Handler;

import com.zhevol.library.dialog.ProgressDialog;

/**
 * 延迟执行<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class DelayTask {
    /**
     * 单例
     */
    private static volatile DelayTask mDelayTask;
    /**
     * 延迟执行的时间，默认500ms
     */
    private long delayMillis;
    /**
     * 是否显示 Dialog 弹框，默认显示
     */
    private boolean isShowDialog;
    /**
     * 显示弹框的时候显示的文字
     */
    private String dialogText;
    /**
     * 弹框对象
     */
    private ProgressDialog mDialog;

    public static DelayTask getInstance() {
        if (mDelayTask == null) {
            synchronized (DelayTask.class) {
                if (mDelayTask == null) {
                    mDelayTask = new DelayTask();
                }
            }
        }
        mDelayTask.delayMillis = 500;
        mDelayTask.isShowDialog = true;
        mDelayTask.dialogText = "请稍候\u2026";
        return mDelayTask;
    }

    /**
     * 设置延迟执行的时间
     *
     * @param delayMillis 延迟的时间
     * @return 单例对象
     */
    public DelayTask delayMillis(long delayMillis) {
        if (mDelayTask.delayMillis != delayMillis) {
            mDelayTask.delayMillis = delayMillis;
        }
        return mDelayTask;
    }

    /**
     * 设置是否显示进度条框
     *
     * @param isShowDialog 是否显示的值
     * @return 单例
     */
    public DelayTask isShowDialog(boolean isShowDialog) {
        mDelayTask.isShowDialog = isShowDialog;
        return mDelayTask;
    }

    /**
     * 设置弹框要显示的文字
     *
     * @param dialogText 要显示的文字
     * @return 单例
     */
    public DelayTask dialogText(String dialogText) {
        mDelayTask.dialogText = dialogText;
        return mDelayTask;
    }

    /**
     * 执行延时任务
     *
     * @param context 上下文
     */
    public void doDelayTask(Context context, final OnTaskDelayListener onTaskDelayListener) {
        if (mDelayTask.isShowDialog) {
            mDialog = new ProgressDialog(context);
            mDialog.setMessage(mDelayTask.dialogText);
            mDialog.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
                onTaskDelayListener.onTaskDelay();
            }
        }, mDelayTask.delayMillis);
    }

    /**
     * 关闭弹框
     */
    private void dismissDialog() {
        if (mDelayTask.isShowDialog && mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog.cancel();
        }
    }

    /**
     * 延时执行的任务的监听
     */
    public interface OnTaskDelayListener {
        /**
         * 需要延迟执行的任务
         */
        void onTaskDelay();
    }
}
