package com.zhevol.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.zhevol.library.R;

import butterknife.ButterKnife;

/**
 * 弹框对话框的基类，其他对话框需要继承此类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public abstract class BaseDialog implements View.OnClickListener {
    /**
     * 弹框的返回按钮
     */
    protected AppCompatButton btnFinishAlert;
    /**
     * Context 是肯定要的，基本对话框要用它
     */
    protected Context mContext;
    /**
     * 自定义Dialog，Dialog还是要有一个的吧
     */
    private Dialog dialog;

    /**
     * 构造方法 来实现 最基本的对话框
     *
     * @param context 上下文
     */
    public BaseDialog(Context context) {
        this.mContext = context;
        //在这里初始化 基础对话框
        dialog = new Dialog(context, getDialogStyleId());

        // 调整dialog背景大小
        View rootView = bindViews();
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(rootView);
        btnFinishAlert = rootView.findViewById(R.id.btnFinishAlert);
        ButterKnife.bind(this, rootView);
        setButtonOnClickListener(rootView);
        bindDialogData();
        bindDialogListeners();
    }

    /**
     * 对话框布局的样式ID (通过这个抽象方法，我们可以给不同的对话框设置不同样式主题)
     *
     * @return 样式主题
     */
    protected abstract int getDialogStyleId();

    /**
     * 构建对话框的方法(都说了是不同的对话框，布局什么的肯定是不一样的)
     *
     * @return 对话框的View
     */
    protected abstract View bindViews();

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
     * 初始化对话框的内容
     */
    protected void bindDialogData() {

    }

    /**
     * 初始化事件监听器
     */
    protected void bindDialogListeners() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnFinishAlert) {
            dismiss();
        } else {
            onClick(id);
        }
    }

    /**
     * 单击事件在这里面处理
     *
     * @param id 单击控件的id
     */
    protected void onClick(int id) {

    }

    /**
     * 展示当前对话框
     */
    public void show() {
        dialog.show();
    }

    /**
     * 退出当前对话框
     */
    protected void dismiss() {
        if (isShowing()) {
            dialog.cancel();
            dialog.dismiss();
        }
    }

    /**
     * 当前对话框的显示状态
     *
     * @return 是否显示
     */
    private boolean isShowing() {
        return dialog.isShowing();
    }


    /**
     * 设置取消按钮的文字，如果不设置，默认为“取消”
     *
     * @param buttonText 文本文字
     */
    public void setCancleButtonText(String buttonText) {
        btnFinishAlert.setText(buttonText);
    }

    /**
     * 设置监听器
     *
     * @param dismissListener 监听器
     * @return 当前对话框
     */
    public BaseDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        dialog.setOnDismissListener(dismissListener);
        return this;
    }
}
