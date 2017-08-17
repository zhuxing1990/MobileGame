package com.vunke.mobilegame.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.adapter.GridViewPagerAdapter;
import com.vunke.mobilegame.adapter.MainGamesAdapter;
import com.vunke.mobilegame.base.BaseActivity;
import com.vunke.mobilegame.base.ConfigInfo;
import com.vunke.mobilegame.base.RxBus;
import com.vunke.mobilegame.dialog.SettingDiaLog;
import com.vunke.mobilegame.manage.AppsManage;
import com.vunke.mobilegame.manage.LoginManage;
import com.vunke.mobilegame.manage.OrderManage;
import com.vunke.mobilegame.manage.UpdateApkManager;
import com.vunke.mobilegame.model.GameInfo;
import com.vunke.mobilegame.model.UpdateGameSetting;
import com.vunke.mobilegame.utils.NetUtils;
import com.vunke.mobilegame.utils.SharedPreferencesUtil;
import com.vunke.mobilegame.utils.UiUtils;
import com.vunke.mobilegame.utils.WorkLog;
import com.vunke.mobilegame.view.TvFocusGridView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 首页
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    /**
     * 时间控件
     */
//    private TextClock main_TextClock;
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
//    private TvFocusGridView gridView;
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
    private Subscription subscribe1;
    private TextView main_settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initViwe();
        initData();
        initListener();
        initRxBus();
        OrderManage.QueryAllOrder(mcontext);
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
    private boolean hasTime = false;
    private void initRxBus() {
        subscribe1  = RxBus.getInstance().toObservable(UpdateGameSetting.class)
                .filter(new Func1<UpdateGameSetting, Boolean>() {
                    @Override
                    public Boolean call(UpdateGameSetting updateGameSetting) {
                        return updateGameSetting.getUpdateCode() == AppsManage.UPDATE_DATA|updateGameSetting.getUpdateCode() ==AppsManage.ADDED_APK|updateGameSetting.getUpdateCode() ==AppsManage.REMOVED_APK;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UpdateGameSetting>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        subscribe1.unsubscribe();
                    }

                    @Override
                    public void onNext(UpdateGameSetting updateGameSetting) {
                        if (updateGameSetting.getUpdateCode() == AppsManage.UPDATE_DATA) {
                            hasTime = updateGameSetting.isHasTime();
                            if (hasTime){
                                if (updateGameSetting.getToubi()==1){
                                    showToast("现在可以游戏了");
                                }else{
                                    showToast("增加游戏时间");
                                }
                            }else{
                                showToast("游戏时间结束，投币即可继续游戏");
                            }
                        }else if(updateGameSetting.getUpdateCode() ==AppsManage.ADDED_APK||updateGameSetting.getUpdateCode() ==AppsManage.REMOVED_APK){
                            initData();
                        }
                    }
                });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        WorkLog.i(TAG, "initData: ");
        CancelRx();
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
                        subscribe.unsubscribe();
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
            TvFocusGridView gridView = createGridView(gameInfos, i);
//            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    WorkLog.i(TAG, "onItemClick:  position:"+position);
//                    WorkLog.i(TAG, "onItemClick: package:"+gameInfos.get(position).getGame_package());
//                    AppsManage.StartPackager(gameInfos.get(position).getGame_package(),getApplicationContext());
//                }
//            });
            list.add(gridView);
        }
        //  设置 ViewPager 的 设配器                  上下文     集合（girdView）
        gridview_adapter = new GridViewPagerAdapter(mcontext, list);
        main_viewpager.setAdapter(gridview_adapter);
    }
    private TvFocusGridView  gridView;
//    private List<GameInfo> mlist;
    private TvFocusGridView createGridView(List<GameInfo> gameInfos, int page) {
        gridView = new TvFocusGridView(this);
        //  设置 gridView 的 设配器           上下文     游戏信息     页码
        gridView.setAdapter(new MainGamesAdapter(mcontext, gameInfos, page));
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
        gridView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                WorkLog.i(TAG, "onClick: position:"+ position);
//                AppsManage.StartPackager(mlist.get(position).getGame_package(),context);
//            }
//        });
//        public static final int CountSize = 18;
       final List<GameInfo> mlist = new ArrayList<>();
        int i = MainGamesAdapter.CountSize * page;//当前页的其实位置
        int iEnd = i +  MainGamesAdapter.CountSize;
        while ((i < gameInfos.size()) && (i < iEnd)) {
            mlist.add(gameInfos.get(i));
            i++;
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i(TAG, "onItemClick: "+mlist.get(position).toString());
                if (hasTime) {
                    WorkLog.i(TAG, "onClick: position:" + position);
                    WorkLog.i(TAG, "onClick :" + mlist.get(position).getGame_package());
                    AppsManage.StartPackager(mlist.get(position).getGame_package(), getApplicationContext());
//                    Intent intent = new Intent(mcontext, FloatWindowsService.class);
//                    intent.setAction(FloatWindowsService.SHOW_FLOAT_WINDOW);
//                    startService(intent);
                }else{
                    showToast("余额不足，请投币");
                }
            }
        });
        return gridView;
    }


    private void initViwe() {
//        main_TextClock = (TextClock) findViewById(R.id.main_textclock);
        main_newwork_status = (ImageView) findViewById(R.id.main_newwork_status);
        main_viewpager = (ViewPager) findViewById(R.id.main_viewpager);
        main_go_right = (ImageView) findViewById(R.id.main_go_right);
        main_go_left = (ImageView) findViewById(R.id.main_go_left);
        main_settings = (TextView) findViewById(R.id.main_settings);
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
        main_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettings();
            }
        });
        main_settings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               if (hasFocus){
                   main_settings.setTextColor(Color.parseColor("#FF0000"));
                   main_settings.setTextSize(30);//DensityUtil.px2sp(mcontext,34)
               }else{
                   main_settings.setTextColor(Color.parseColor("#FFFFFF"));
                   main_settings.setTextSize(30);//DensityUtil.px2sp(mcontext,30)
               }
            }
        });
    }
    SettingDiaLog diaLog;
//    private Dialog dialog;
    private void startSettings() {
//        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
//        final EditText editText = new EditText(this);
//        editText.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//        editText.setHint("请输入密码");
//        builder.setView(editText);
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (TextUtils.isEmpty(editText.getText().toString())){
//                    showToast("密码为空");
//                }else{
//                    String password = editText.getText().toString();
//                    if (password.equals("123456")){
//                        showToast("密码正确");
//                        Intent intent = new Intent(mcontext,AppSettingsActivity.class);
//                        startActivity(intent);
//                    }else{
//                        showToast("密码错误");
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
         diaLog  =  new SettingDiaLog(mcontext);
                diaLog.builder()
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setEditTextHint("请输入密码")
                .setEditTextInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = diaLog.getEditText().toString().trim();
                        if (TextUtils.isEmpty(text)){
                            showToast("请输入密码");
                        }else {
//                            showToast("密码正确");
//                            Intent intent = new Intent(mcontext,AppSettingsActivity.class);
//                            startActivity(intent);
//                            diaLog.cancel();
//                        }else{
//                            showToast("密码错误");
                            RequestSetting(text);
                            diaLog.cancel();
                        }
                    }
                })
                .setNeutralButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        diaLog.cancel();
                    }
                })
                .show();

    }

    private void RequestSetting(String text) {
        String userName = SharedPreferencesUtil.getStringValue(mcontext,LoginActivity.LoginKey,"");
        LoginManage.LoginSetting(userName,text,mcontext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
//        Intent intent = new Intent(mcontext, FloatWindowsService.class);
//        intent.setAction(FloatWindowsService.HIDE_FLOAT_WINDOW);
//        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CancelRx();
        CancelRx2();
//        if (dialog!=null&&dialog.isShowing()){
//            dialog.cancel();
//        }
        diaLog.cancel();
    }

    private void CancelRx2() {
        if (subscribe1!=null&&!subscribe1.isUnsubscribed()) {
            subscribe1.unsubscribe();
        }
    }

    private void CancelRx() {
        if (subscribe!=null&&!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
