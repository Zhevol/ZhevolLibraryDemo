package com.zhevol.library.demo.activity;

import com.blankj.ALog;
import com.blankj.utilcode.util.ToastUtils;
import com.zhevol.library.activity.BaseTopToolbarActivity;
import com.zhevol.library.demo.R;
import com.zhevol.library.dialog.MessageDialog;
import com.zhevol.library.listener.OnMessageDialogSureListener;
import com.zhevol.library.widget.PasswordEditText;

import butterknife.BindView;

/**
 * Demo 的主 Activity<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class MainActivity extends BaseTopToolbarActivity {

    @BindView(R.id.etMain)
    PasswordEditText etMain;

    @Override
    protected int bindLayoutId() {
        /*显示隐藏密码输入框、消息对话框、底部弹框、Adapter、Intent数据传递、其他自定义弹框、
         *碎片Fragment、延时任务、其他自定义底部弹框、RecyclerView示例*/
        return R.layout.activity_main;
    }

    @Override
    protected String bindTopToolbarTitle() {
        return "我的宝库";
    }

    @Override
    protected void onClick(int id) {
        super.onClick(id);
        switch (id) {
            case R.id.btnMessageDialog:
                ALog.e(etMain.getText().toString());
                showMessageDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 展示消息对话框
     */
    private void showMessageDialog() {
        MessageDialog dialog = new MessageDialog(this, "消息提示", "消息内容");
        dialog.setSureButtonText("查看");
        dialog.setOnMessageDialogSureListener(new OnMessageDialogSureListener() {
            @Override
            public void messageSure() {
                ToastUtils.showShort("点击了查看消息按钮");
            }
        });
        dialog.show();
    }
}
