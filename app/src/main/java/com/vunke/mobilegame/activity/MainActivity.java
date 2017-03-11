package com.vunke.mobilegame.activity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextClock;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.adapter.MainGamesAdapter;
import com.vunke.mobilegame.base.BaseActivity;
import com.vunke.mobilegame.model.GameInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainActivity extends BaseActivity {
    private TextClock main_TextClock;
    private ImageView main_newwork_status;
    private GridView main_gridView;
    private GameInfo info;
    private List<GameInfo> list;
    private MainGamesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViwe();
        initListener();
        initData();
    }



    private void initViwe() {
        main_TextClock = (TextClock) findViewById(R.id.main_textclock);
        main_newwork_status = (ImageView) findViewById(R.id.main_newwork_status);
        main_gridView = (GridView) findViewById(R.id.main_gridView);
    }
    private void initListener() {
        main_gridView.requestFocus();
    }
    private void initData() {
        list = new ArrayList<>();
        for (int i = 0; i <50 ; i++) {
            info = new GameInfo();
            info.set_id(1);
            info.setClick(0);
            info.setOrder(1111);
            info.setCreate_time(System.currentTimeMillis());
            info.setGame_desc("ceshi");
            info.setGame_Icon("http://www.bbvdd.com/d/20170309155547ljp.png");
            info.setGame_name("测试");
            info.setUsed_time(System.currentTimeMillis());
            info.setUpdate_time(System.currentTimeMillis());
            info.setGame_package("com.vunke.test");
            info.setVersion_code("1");
            info.setGame_activity("com.vunke.test.TestActivity");
            list.add(info);
        }
//        horizontal_layout(list);
        adapter = new MainGamesAdapter(mcontext,list);
        main_gridView.setAdapter(adapter);
    }



}
