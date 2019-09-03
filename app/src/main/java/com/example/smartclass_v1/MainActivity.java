package com.example.smartclass_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {

    private ViewPager mViewPager;
    private List<View> mViews = new ArrayList<View>();
    private pageAdapter mAdapter = new pageAdapter(mViews);


    // Tab
    private LinearLayout mTabLight, mTabSyn, mTabProfiles;

    //air空调
    private Button scaleBt;
    private View Popup;
    private CommonPopupWindow window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);

        Intent it = new Intent(this, TcpService.class);
        startService(it);

        initView();
        initEvents();
        initPopupWindow();
    }

    //View的滑动事件
    private void initEvents() {
        mTabLight.setOnClickListener(this);
        mTabSyn.setOnClickListener(this);
        mTabProfiles.setOnClickListener(this);

        //滑动切换页面
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        mTabLight.setBackgroundColor(0x4Dffffff);
                        mTabSyn.setBackgroundColor(0x00);
                        mTabProfiles.setBackgroundColor(0x00);
                        break;
                    case 1:
                        mTabSyn.setBackgroundColor(0x4Dffffff);
                        mTabLight.setBackgroundColor(0x00);
                        mTabProfiles.setBackgroundColor(0x00);
                        break;
                    case 2:
                        mTabProfiles.setBackgroundColor(0x4Dffffff);
                        mTabLight.setBackgroundColor(0x00);
                        mTabSyn.setBackgroundColor(0x00);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    //实例化控件
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        // Tab
        mTabLight = (LinearLayout) findViewById(R.id.tab_light);
        mTabSyn = (LinearLayout) findViewById(R.id.tab_syn);
        mTabProfiles = (LinearLayout) findViewById(R.id.tab_Profiles);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View tab01 = mInflater.inflate(R.layout.tab_light, null);
        View tab02 = mInflater.inflate(R.layout.tab_syn, null);
        View tab03 = mInflater.inflate(R.layout.tab_profiles, null);

        mViews.add(tab01);
        mViews.add(tab02);
        mViews.add(tab03);
        mViewPager.setAdapter(mAdapter);

        //air
        Popup = findViewById(R.id.tab_syn);
        scaleBt = (Button) findViewById(R.id.bt_air);
    }

    //ImageButton的点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_light:
                mViewPager.setCurrentItem(0);// 跳到第一个页面
                mTabLight.setBackgroundColor(0x4Dffffff); // 标题变为选中
                mTabSyn.setBackgroundColor(0x00);
                mTabProfiles.setBackgroundColor(0x00);
                break;
            case R.id.tab_syn:
                mViewPager.setCurrentItem(1);
                mTabSyn.setBackgroundColor(0x4Dffffff);
                mTabLight.setBackgroundColor(0x00);
                mTabProfiles.setBackgroundColor(0x00);
                break;
            case R.id.tab_Profiles:
                mViewPager.setCurrentItem(2);
                mTabProfiles.setBackgroundColor(0x4Dffffff);
                mTabLight.setBackgroundColor(0x00);
                mTabSyn.setBackgroundColor(0x00);
                break;
            default:
                break;
        }
    }

    //air空调
    private void initPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        // create popup window
        window = new CommonPopupWindow(this, R.layout.popup, ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenHeight * 0.7)) {
            @Override
            protected void initView() {
                View view = getContentView();
                //dataList.setAdapter(new ArrayAdapter<String>(PopupActivity.this, R.layout.item_popup_list, datas));
            }

            @Override
            protected void initEvent() {
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance = getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1.0f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);
                    }
                });
            }
        };
    }

    //air空调界面
    public void ponClick(View v) {
        PopupWindow win = window.getPopupWindow();
        win.setAnimationStyle(R.style.animScale);

        window.showAtLocation(Popup, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }



    //灯光控制
    public void light(View view){
        Intent intent = null;
        Intent CMDintent = new Intent();
        switch (view.getId()) {
            case R.id.bt_light_1_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_1_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_1_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_1_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_2_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_2_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_2_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_2_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_3_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_3_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_3_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_3_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_4_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_4_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_4_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_4_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_5_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_5_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_5_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_5_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_6_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_6_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_6_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_6_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_7_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_7_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_light_7_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "light_7_off");
                sendBroadcast(CMDintent);
                break;
        }
    }

    //窗户开关
    public void window(View view){
        Intent CMDintent = new Intent();
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_win10_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win10_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win10_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win10_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win11_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win11_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win11_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win11_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win12_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win12_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win12_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win12_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win0_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win0_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win0_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win0_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win1_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win1_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win1_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win1_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win2_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win2_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win2_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win2_off");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win3_on:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win3_on");
                sendBroadcast(CMDintent);
                break;
            case R.id.bt_win3_off:
                CMDintent.setAction("com.example.communication.data2");
                CMDintent.putExtra("data", "win3_off");
                sendBroadcast(CMDintent);
                break;
        }
    }
}

