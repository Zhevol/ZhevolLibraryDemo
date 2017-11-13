package com.zhevol.library.application;

import android.app.Activity;
import android.app.Application;

import com.blankj.ALog;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * Application类，可以在里面初始化相关的全局变量<br/>
 * GitHub: https://github.com/Zhevol/ZhevolLibraryDemo.git<br/>
 * E_mail: 212448124@qq.com<br/>
 * Created by Zhevol on 2017/11/13 0013.
 *
 * @author Zhevol
 */
public class BaseApplication extends Application {

    /**
     * Application 实例
     */
    private static BaseApplication mBaseApplication;

    /**
     * Activity的集合
     */
    private List<Activity> activityList;

    @Override
    public void onCreate() {
        super.onCreate();
        mBaseApplication = this;
        activityList = new ArrayList<>();
        //配置 Blankj/Utils
        Utils.init(this);
        //配置 ALog
        ALog.init(this);
        //配置 EasyHttp
        EasyHttp.init(this);
    }

    /**
     * 获取本应用的 Application 的实例
     *
     * @return BaseApplication 实例
     */
    public static BaseApplication getInstance() {
        return mBaseApplication;
    }

    /**
     * 将 Activity 添加到 {@code activityList} 中.
     *
     * @param activity Activity 实例
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * app的退出管理，关闭所有的Activity并退出程序
     */
    public void exitApp() {
        try {
            for (Activity activity : activityList) {
                if (activity != null) {
                    activity.finish();
                }
            }
            activityList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * SharedPreferences 对象持有者
     */
    private static class SharedPreferencesHolder {
        static final SPUtils ZLA_SP = SPUtils.getInstance("ZLA_SP");
    }

    /**
     * 通过 {@link SharedPreferencesHolder} 来实例化 SPUtils 的单例
     *
     * @return SPUtils 实例
     */
    public static SPUtils getSPInstance() {
        return SharedPreferencesHolder.ZLA_SP;
    }
}
