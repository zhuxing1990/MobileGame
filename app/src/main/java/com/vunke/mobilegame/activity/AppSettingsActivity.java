package com.vunke.mobilegame.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.base.BaseActivity;
import com.vunke.mobilegame.utils.UiUtils;

/**
 * Created by zhuxi on 2017/7/13.
 */
public class AppSettingsActivity extends BaseActivity implements View.OnClickListener{
    private Button appsettings_but1;
    private Button appsettings_but2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appsettings);
        initView();
    }

    private void initView() {
        appsettings_but1 = (Button) findViewById(R.id.appsettings_but1);
        appsettings_but2 = (Button) findViewById(R.id.appsettings_but2);
        appsettings_but1.setOnClickListener(this);
        appsettings_but2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.appsettings_but1:
//                if(android.os.Build.VERSION.SDK_INT > 10 ){
//                    //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
//                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
//                } else {
//                   startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
//                }
                UiUtils.StartWifiSettings(mcontext);
                finish();
               break;
            case R.id.appsettings_but2:
                startSettings();
               break;
            default:
               break;
        }
    }
    private Dialog dialog;
    private void startSettings() {
        Intent intent = new Intent(mcontext,TimeSettingActivity.class);
        startActivity(intent);
        finish();
//        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
//        final EditText editText = new EditText(this);
//        editText.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL|InputType.TYPE_CLASS_NUMBER);
//        editText.setEms(3);
//        editText.setHint("在此处输入");
//        builder.setView(editText);
//        builder.setTitle("请输入投币费率,1-999/分钟");
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (TextUtils.isEmpty(editText.getText().toString())){
//                    showToast("不能为空");
//                }else{
//                    String str = editText.getText().toString();
//                    long gametime = Long.valueOf(str);
//                    int i = GameTimeManage.UpdateTime(mcontext, gametime);
//                    if (i==-1){
//                        showToast("修改失败!");
//                    }else{
//                        showToast("修改成功!");
//                        Intent intent = new Intent(mcontext, FloatWindowsService.class);
//                        intent.setAction(FloatWindowsService.UPDATE_GAME_TIME);
//                        startService(intent);
//                        finish();
//                    }
//                }
//            }
//        });
//        builder.setNegativeButton("取消",null);
//        builder.setCancelable(false);
//        dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog!=null&&dialog.isShowing()){
            dialog.cancel();
        }
    }
}
