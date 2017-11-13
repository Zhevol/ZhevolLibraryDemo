package com.zhevol.library.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhevol.library.R;

/**
 * 带圆环的进度条<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class ProgressDialog extends AlertDialog {

    private ProgressBar mProgress;
    private TextView mMessageView;

    private CharSequence mMessage;
    private Context mContext;

    /**
     * 构造器
     *
     * @param context 上下文
     */
    public ProgressDialog(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * 构造器
     *
     * @param context 上下文
     * @param theme   弹框的 Style
     */
    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        TypedArray a = mContext.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
        View view = inflater.inflate(a.getResourceId(R.styleable.AlertDialog_progressLayout, R.layout.alert_progress_dialog), null);
        mProgress = view.findViewById(R.id.progress);
        mMessageView = view.findViewById(R.id.message);
        setView(view);
        a.recycle();
        if (mMessage != null) {
            setMessage(mMessage);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            mMessageView.setText(message);
        } else {
            mMessage = message;
        }
    }
}

