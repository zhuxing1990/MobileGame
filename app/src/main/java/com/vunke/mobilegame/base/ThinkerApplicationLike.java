package com.vunke.mobilegame.base;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.lzy.okgo.OkGo;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.vunke.mobilegame.service.ThinkerService;
import com.vunke.mobilegame.utils.WorkLog;

/**
 * Created by zhuxi on 2017/3/8.
 * 重要 一定要继承 DefaultApplicationLike
 * SuppressWarnings 直接复制
 * DefaultLifeCycle   自动生成的 Application 文件
 * 重要  DefaultLifeCycle 的 application = ""切记一定要和 AndroidManifest.xml 里  applicatin 属性   android:name=“com.vunke.mobilegame.base.ThinkerApplication”  必须一样
 * flags = ShareConstants.TINKER_ENABLE_ALL  直接复制
 * loadVerifyFlag = fals  直接复制
 */
@SuppressWarnings("uuused")
@DefaultLifeCycle(application = "com.vunke.mobilegame.base.ThinkerApplication"
        ,flags = ShareConstants.TINKER_ENABLE_ALL
        ,loadVerifyFlag = false)
public class ThinkerApplicationLike extends DefaultApplicationLike {
    public ThinkerApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initOnCreate();
    }

    public void initOnCreate() {
        OkGo.init(getApplication());
        //友盟推送
        PushAgent mPushAgent = PushAgent.getInstance(getApplication());
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                WorkLog.i("友盟推送", "onSuccess: deviceToken:"+deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                WorkLog.i("友盟推送", "onFailure: s:"+s+"\n s1:"+s1);
            }
        });
        mPushAgent.setDebugMode(false);
    }


    /**
     * 这个方法直接复制
     * 记得先写好Serivce  AndroidManifest.xml 一定要注册Serivce
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);
        TinkerInstaller.install(this,new DefaultLoadReporter(getApplication())
                ,new DefaultPatchReporter(getApplication()),new DefaultPatchListener(getApplication()),ThinkerService.class,new UpgradePatch());
        Tinker tinker = Tinker.with(getApplication());
        WorkLog.e("Thinker", "onBaseContextAttached: "+"loadPatch");
    }

    /**
     * 这个方法直接复制
     * @param callback
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }
}
