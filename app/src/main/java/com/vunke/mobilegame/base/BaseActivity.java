package com.vunke.mobilegame.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.umeng.message.PushAgent;
import com.vunke.mobilegame.utils.AppManager;
import com.vunke.mobilegame.utils.WorkLog;

/**
 * Created by zhuxi on 2017/3/9.
 */
public class BaseActivity extends AppCompatActivity {
    public BaseActivity mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext = this;
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
        //友盟推送
        PushAgent.getInstance(this).onAppStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }
    /**
     * 吐司
     * */
    public void showToast(CharSequence string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        WorkLog.i("BaseActivity", "onTrimMemory: level:"+level);
        System.gc();
    }
}
