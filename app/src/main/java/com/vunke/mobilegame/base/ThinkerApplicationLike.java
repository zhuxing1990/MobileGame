package com.vunke.mobilegame.base;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.vunke.mobilegame.service.ThinkerService;

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
