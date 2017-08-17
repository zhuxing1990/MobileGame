package com.vunke.mobilegame.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.base.BaseActivity;
import com.vunke.mobilegame.base.ConfigInfo;
import com.vunke.mobilegame.manage.LoginManage;
import com.vunke.mobilegame.manage.UpdateApkManager;
import com.vunke.mobilegame.utils.NetUtils;
import com.vunke.mobilegame.utils.SharedPreferencesUtil;
import com.vunke.mobilegame.utils.UiUtils;
import com.vunke.mobilegame.utils.WorkLog;

/**
 * Created by zhuxi on 2017/7/20.
 */
public class LoginActivity extends BaseActivity{
    private static final String TAG = "LoginActivity";
    private EditText login_user;
    private EditText login_password;
    private Button login_confrim;
    private String userName;
    private String password;
    public static final String LoginKey = "userName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
        initLogin();
       UpdateAPK(mcontext);
    }
    public static void UpdateAPK(Context mcontext) {
        if (NetUtils.isNetConnected(mcontext)) {
            long theDate = System.currentTimeMillis();
            long isSameToday = (long) SharedPreferencesUtil.getLongValue(mcontext,
                    ConfigInfo.UPDATE_TOMORROW, ConfigInfo.defaultValue);
            boolean sameToday = UiUtils.isSameToday(theDate, isSameToday);
            if (sameToday) {
                WorkLog.i(TAG, "theDate:" + theDate
                        + "\n isSameToday:" + isSameToday);
                WorkLog.i(TAG, "暂不更新");
                return;
            } else {
                WorkLog.i(TAG, "theDate:" + theDate
                        + "\n isSameToday:" + isSameToday);
                WorkLog.e(TAG, "检测更新");
                UpdateApkManager.getInstance(mcontext).GetUpdateInfo();
            }
        }
    }
    private void initLogin() {
        String name = SharedPreferencesUtil.getStringValue(mcontext, LoginKey, "");
        if (TextUtils.isEmpty(name)){
            return;
        }
        login_user.setText(name);
        LoginManage.AutoMaticLogin(mcontext,name);
    }

    private void initListener() {
        login_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
    }
    private void initView() {
        login_user = (EditText) findViewById(R.id.login_user);
        login_password = (EditText) findViewById(R.id.login_password);
        login_confrim = (Button) findViewById(R.id.login_confrim);
    }
    private void startLogin() {
        userName = login_user.getText().toString().trim();
        password = login_password.getText().toString().trim();
        if (TextUtils.isEmpty(userName)){
            showToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)){
            showToast("密码不能为空");
            return;
        }
        SharedPreferencesUtil.setStringValue(mcontext,LoginKey,userName);
        UiUtils.stopClick(login_confrim,5);
        LoginManage.startLogin(userName,password,mcontext);
    }

    /**
     *  记录按下返回键的时间
     */
    private long back_time = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - back_time >= 2000) {
                showToast("再按一次退出");
                back_time = System.currentTimeMillis();
                return false;
            } else {
//                this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
