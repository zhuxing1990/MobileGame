package com.vunke.mobilegame.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextClock;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.adapter.GridViewPagerAdapter;
import com.vunke.mobilegame.adapter.MainGames2Adapter;
import com.vunke.mobilegame.base.BaseActivity;
import com.vunke.mobilegame.model.GameInfo;
import com.vunke.mobilegame.view.TvFocusGridView;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends BaseActivity {
    private TextClock main_TextClock;
    private ImageView main_newwork_status;
    private ViewPager main_viewpager;
    private List<GridView> list;
    private GameInfo info;
    private List<GameInfo> infolist;
    private GridViewPagerAdapter gridview_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initViwe();
        initData();
        initListener();
    }
    private int myposition;
    private void initData() {
        //数据总数
        int dataSize = 150;
        infolist = new ArrayList<>();
        for (int i = 0; i <dataSize ; i++) {
            info = new GameInfo();
            info.set_id(1);
            info.setClick(0);
            info.setOrder(1111);
            info.setCreate_time(System.currentTimeMillis());
            info.setGame_desc("ceshi");
            info.setGame_Icon("http://www.bbvdd.com/d/20170309155547ljp.png");
            info.setGame_name("测试"+i);
            info.setUsed_time(System.currentTimeMillis());
            info.setUpdate_time(System.currentTimeMillis());
            info.setGame_package("com.vunke.test");
            info.setVersion_code("1");
            info.setGame_activity("com.vunke.test.TestActivity");
            infolist.add(info);
        }
//        String result = String .format("%.2f",dataSize/18);
//        showToast("当前有多少页:");
        int j = 0;
        for (int i = 0; i <dataSize; i+=18) {
                j++;
        }
        list = new ArrayList<>();
        for (int i = 0; i <j; i++) {
//            GridView gridView = new GridView(this);
            TvFocusGridView gridView = new TvFocusGridView(this);
            gridView.setAdapter(new MainGames2Adapter(mcontext,infolist,i));
            gridView.setNumColumns(6);
            gridView.setColumnWidth(8);
            gridView.setVerticalSpacing(9);
            gridView.setHorizontalSpacing(9);
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

            gridView.setClipToPadding(false);
            gridView.setSelected(true);
            gridView.setSelection(0);
            gridView.setSelector(android.R.color.transparent);
            gridView.setMySelector(R.drawable.temp_meiyanxiangji);
            gridView.setMyScaleValues(1.1f, 1.1f);
            list.add(gridView);
        }
        gridview_adapter = new GridViewPagerAdapter(mcontext,list);
        main_viewpager.setAdapter(gridview_adapter);

    }

    private void initViwe() {
        main_TextClock = (TextClock) findViewById(R.id.main_textclock);
        main_newwork_status = (ImageView) findViewById(R.id.main_newwork_status);
        main_viewpager = (ViewPager) findViewById(R.id.main_viewpager);
    }
    private void initListener() {

    }
}
