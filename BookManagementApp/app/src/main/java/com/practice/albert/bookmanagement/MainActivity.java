package com.practice.albert.bookmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.practice.albert.tool.LogTool;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layout_body_activity;
    private BottomNavigationView navigation;
    private ViewPager mPager;
    private List<View> mListViews;// Tab页面
//    private LinearLayout layout_body_activity;
    private static final int PAGE1 = 0;// 页面1
    private static final int PAGE2 = 1;// 页面2
    private static final int PAGE3 = 2;// 页面3  final不可更改
    private int currentIndex = PAGE1; // 默认选中第1个，可以动态的改变此参数值

    private Button tab1_scan;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //
                    mPager.setCurrentItem(PAGE1);
                    currentIndex = PAGE1;
                    return true;
                case R.id.navigation_dashboard:
                    mPager.setCurrentItem(PAGE2);
                    currentIndex = PAGE2;
                    return true;
                case R.id.navigation_notifications:
                    mPager.setCurrentItem(PAGE3);
                    currentIndex = PAGE3;
                    return true;
            }
            return false;
        }
    };
    private ViewPager.OnPageChangeListener  MyOnPageChangerListener = new  ViewPager.OnPageChangeListener() {
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case PAGE1:// 切换到页卡1
                    navigation.getMenu().getItem(PAGE1).setChecked(true);
                    break;
                case PAGE2:// 切换到页卡2
                    navigation.getMenu().getItem(PAGE2).setChecked(true);
                    break;
                case PAGE3:// 切换到页卡3
                    navigation.getMenu().getItem(PAGE3).setChecked(true);
                    break;
                default:
                    break;
            }
            currentIndex = arg0;// 动画结束后，改变当前图片位置
        }
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }
    };

            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //隐藏标题栏 继承AppCompatActivity的解决方法
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //mainUI
        layout_body_activity = (LinearLayout) findViewById(R.id.bodylayout1);
        layout_body_activity.setFocusable(true);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mListViews = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View lay1 = inflater.inflate(R.layout.tab1, null);
        View lay2 = inflater.inflate(R.layout.tab2, null);
        View lay3 = inflater.inflate(R.layout.tab3, null);
        mListViews.add(lay1);
        mListViews.add(lay2);
        mListViews.add(lay3);
        mPager.setAdapter(new MyPagerAdapter(mListViews));
        mPager.setCurrentItem(currentIndex);
//        mPager.setOnPageChangeListener(new MyOnPageChangeListener()); //已过时
        mPager.addOnPageChangeListener(MyOnPageChangerListener);

        LogTool.v("test:","test test" );

        //tab1
//        tab1_titlelayout1 = (LinearLayout)lay1.findViewById(R.id.tab1_titlelayout1);
        tab1_scan = (Button) lay1.findViewById(R.id.scan_button);
        tab1_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent scan_intent = new Intent();
                scan_intent.setClass(MainActivity.this, ZBarScanActivity.class);
//                scan_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(scan_intent);
            }
        });
    }

}
