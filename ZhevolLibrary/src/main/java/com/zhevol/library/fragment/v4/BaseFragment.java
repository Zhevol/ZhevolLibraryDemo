package com.zhevol.library.fragment.v4;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.blankj.utilcode.util.ActivityUtils;
import com.zhevol.library.R;
import com.zhevol.library.config.IntentConfig;

import java.io.Serializable;

import butterknife.ButterKnife;

/**
 * Fragment 的基类，其他 Fragment 应继承这个类<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private View fragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(bindLayoutId(), container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setButtonOnClickListener(fragmentView);
        bindData(savedInstanceState);
        bindListeners();
    }

    /**
     * 获取 Fragment 的布局 id
     *
     * @return 布局 id
     */
    protected abstract int bindLayoutId();

    /**
     * 初始化数据
     */
    protected void bindData(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 绑定相关的事件监听器
     */
    protected void bindListeners() {

    }

    /**
     * 查找Button、ImageButton并设置单击监听器
     *
     * @param view 要设置监听器的view
     */
    private void setButtonOnClickListener(View view) {
        //遍历view里面所有的Button和ImageButton
        if (view instanceof Button || view instanceof ImageButton) {
            view.setOnClickListener(this);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                setButtonOnClickListener(childView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnFinishActivity) {
            finishActivity();
        } else {
            onClick(id);
        }
    }

    /**
     * 退出当前Activity
     * 如果要修改返回按钮的相关执行过程，则应重写此方法
     */
    protected void finishActivity() {
        getActivity().finish();
    }

    /**
     * 单击事件在这里面处理
     *
     * @param id 单击控件的id
     */
    protected void onClick(int id) {

    }

    /**
     * 获取 Fragment 的 View
     *
     * @return Fragment 的 View
     */
    public View getFragmentView() {
        return fragmentView;
    }

    /**
     * 跳转 Activity
     *
     * @param cls 要跳转到的目标 Activity
     */
    protected void startActivity(Class<?> cls) {
        ActivityUtils.startActivity(getActivity(), cls);
    }

    /**
     * 跳转 Activity
     *
     * @param cls          目标 Activity
     * @param serializable 要传递的 Serializable 参数
     */
    protected void startActivity(Class<?> cls, Serializable serializable) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(IntentConfig.BASE_INTENT_DATA, serializable);
        startActivity(intent);
    }
}
