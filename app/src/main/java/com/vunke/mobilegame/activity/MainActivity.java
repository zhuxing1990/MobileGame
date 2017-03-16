package com.vunke.mobilegame.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextClock;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.adapter.GridViewPagerAdapter;
import com.vunke.mobilegame.adapter.MainGamesAdapter;
import com.vunke.mobilegame.base.BaseActivity;
import com.vunke.mobilegame.manage.AppsManage;
import com.vunke.mobilegame.model.GameInfo;
import com.vunke.mobilegame.utils.WorkLog;
import com.vunke.mobilegame.view.TvFocusGridView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 首页
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    /**
     * 时间控件
     */
    private TextClock main_TextClock;
    /**
     * 网络状态 （暂未设置）
     */
    private ImageView main_newwork_status;
    /**
     * 右箭头
     */
    private ImageView main_go_right;
    /**
     * 左箭头
     */
    private ImageView main_go_left;
    /**
     * ViewPager
     */
    private ViewPager main_viewpager;
    /**
     * 集合  装载GridView
     */
    private List<GridView> list;
    /**
     * JavaBean  游戏信息
     */
    private GameInfo info;
    /**
     * 集合 装载游戏信息的
     */
    private List<GameInfo> infolist;
    /**
     * 自定义 GridView 实现动画效果
     */
    private TvFocusGridView gridView;
    /**
     * 设配器  用于设配ViewPager
     */
    private GridViewPagerAdapter gridview_adapter;

    /**
     * 总页数 记录viewPager的页数
     */
    private int pages = 0;

    /**
     *
     */
    private Subscription subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViwe();
        initData();
        initListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        subscribe = Observable.unsafeCreate(new Observable.OnSubscribe<List<GameInfo>>() {
            @Override
            public void call(Subscriber<? super List<GameInfo>> subscriber) {
                infolist = AppsManage.getAllAppList(getApplicationContext());
                subscriber.onNext(infolist);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GameInfo>>() {
                    @Override
                    public void onCompleted() {
                        List<GameInfo> gameInfos = AppsManage.qureyGameInfo(getApplicationContext());
                        WorkLog.d(TAG, "onCompleted: "+ gameInfos.toString());
                        setViewPager(gameInfos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        subscribe.unsubscribe();
                    }

                    @Override
                    public void onNext(List<GameInfo> gameInfos) {
                        if (gameInfos != null && !gameInfos.isEmpty()) {
                            WorkLog.d(TAG, "onNext: " + "gameInfos:" + gameInfos.size());
                            AppsManage.InsertGameInfo(getApplicationContext(),gameInfos);
                        } else {
                            WorkLog.d(TAG, "onNext:  " + "gameInfos is Empty");
                        }
                    }
                });

    }

    private void setViewPager(final List<GameInfo> gameInfos) {
        /**
         * 根据总数据获取当前总页数
         */
        for (int i = 0; i < gameInfos.size(); i += 18) {
            pages++;
        }
        WorkLog.i(TAG, "initData: " + "总页" + pages);
        list = new ArrayList<>();

        /**
         * 根据数据页码 填充ViewPager需要的集合
         */
        for (int i = 0; i < pages; i++) {
//            GridView gridView = new GridView(this);
            gridView = new TvFocusGridView(this);
            //  设置 gridView 的 设配器           上下文     游戏信息     页码
            gridView.setAdapter(new MainGamesAdapter(mcontext, gameInfos, i));
            gridView.setNumColumns(6);//设置girdView列数  1行6列
            gridView.setGravity(Gravity.CENTER);// 位置居中
            gridView.setVerticalSpacing(12);// 垂直间隔
//            gridView.setHorizontalSpacing(8);// 水平间隔
            gridView.setClipToPadding(false);//  是否允许ViewGroup在padding中绘制     具体解释:http://www.tuicool.com/articles/m6N36zQ
            gridView.setSelected(true);//支持选择
            gridView.setSelection(0);// 选择当前下标为 0  第一个
            gridView.setSelector(android.R.color.transparent);//设置选中后的透明效果
            gridView.setMySelector(R.drawable.frame);//设置选中后的边框
            gridView.setMyScaleValues(1.1f, 1.1f);//设置选中后 默认扩大倍数

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AppsManage.StartPackager(gameInfos.get(position).getGame_package(),getApplicationContext());
                }
            });
            list.add(gridView);
        }
        //  设置 ViewPager 的 设配器                  上下文     集合（girdView）
        gridview_adapter = new GridViewPagerAdapter(mcontext, list);
        main_viewpager.setAdapter(gridview_adapter);
    }

    private void initViwe() {
        main_TextClock = (TextClock) findViewById(R.id.main_textclock);
        main_newwork_status = (ImageView) findViewById(R.id.main_newwork_status);
        main_viewpager = (ViewPager) findViewById(R.id.main_viewpager);
        main_go_right = (ImageView) findViewById(R.id.main_go_right);
        main_go_left = (ImageView) findViewById(R.id.main_go_left);
    }

    private void initListener() {

        main_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {// 当页面选中的状态下
                //根据当前选择的下标 设置 左右箭头
                if (position == 0) {//如果下标为0   也就是第一页   隐藏左箭头 显示右箭头
                    main_go_right.setVisibility(View.VISIBLE);
                    main_go_left.setVisibility(View.INVISIBLE);
                } else if (position > 0 && position < pages - 1) {//如果下标大于0 并且小于当前总页码   全部显示
                    main_go_right.setVisibility(View.VISIBLE);
                    main_go_left.setVisibility(View.VISIBLE);
                } else if (position == pages - 1) {//如果下标等于 当前总页码  隐藏右箭头 显示左箭头
                    main_go_right.setVisibility(View.INVISIBLE);
                    main_go_left.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }
}
