package com.vunke.mobilegame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.base.BaseActivity;
import com.vunke.mobilegame.manage.GameTimeManage;
import com.vunke.mobilegame.service.FloatWindowsService;

/**
 * Created by zhuxi on 2017/7/19.
 */
public class TimeSettingActivity extends BaseActivity implements View.OnClickListener{
    private TextView timesetting_defaulttime;
    private EditText timesetting_edittext;
    private Button timesetting_saved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesetting);
        initView();
        initDefaultTime();
    }

    private void initDefaultTime() {
        long l = GameTimeManage.QueryGameTime(mcontext);
        if (l>0){
            timesetting_defaulttime.setText("1币/"+l+"分钟");
        }
    }

    private void initView() {
        timesetting_defaulttime = (TextView) findViewById(R.id.timesetting_defaulttime);
        timesetting_edittext = (EditText) findViewById(R.id.timesetting_edittext);
        timesetting_edittext.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_DATETIME_VARIATION_NORMAL);
        timesetting_saved = (Button) findViewById(R.id.timesetting_saved);
        timesetting_saved.setOnClickListener(this);
    }
    private String time;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.timesetting_saved:
                time = timesetting_edittext.getText().toString().trim();
                if (TextUtils.isEmpty(time)){
                    showToast("不能为空");
                }else{
                    long gametime = Long.valueOf(time);
                    if (gametime<=0){
                        showToast("不能小于1分钟");
                        return;
                    }
                    int i = GameTimeManage.UpdateTime(mcontext, gametime);
                    if (i==-1){
                        showToast("保存失败!");
                    }else{
                        showToast("保存成功!");
                        Intent intent = new Intent(mcontext, FloatWindowsService.class);
                        intent.setAction(FloatWindowsService.UPDATE_GAME_TIME);
                        startService(intent);
                        finish();
                    }
                }
                break;
        }
    }
}
