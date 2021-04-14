package com.example.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class index extends FragmentActivity {
    private BottomNavigationView navigationView;
    private Fragment video_Fragment;
    private Fragment train_Fragment;
    private Fragment social_Fragment;
    private Fragment regular_Fragment;
    private Fragment my_Fragment;
    public Fragment[] fragmentlist;
    private int lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index2);
        initFragment();
    }
    private void initFragment() {
        navigationView = (BottomNavigationView) findViewById(R.id.bnv_main);
        //配置菜单按钮显示图标
//        navigationView.setItemIconTintList(null);
        //将三个fragment先放在数组里
        video_Fragment = new Video_Fragment();
        train_Fragment = new Train_fragment();
        social_Fragment = new Social_fragment();
        regular_Fragment = new Regular_fragment();
        my_Fragment = new My_fragment();
        fragmentlist = new Fragment[]{video_Fragment, train_Fragment, social_Fragment,regular_Fragment,my_Fragment};
        //此时标识标识首页
        //0表示首页，1表示videoFragment，2表示trainFragment,3表示regular_fragment,4表示my_fragment
        lastFragment = 0;
        //为navigationView设置点击事件
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //设置默认页面为headFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, video_Fragment)
                .show(video_Fragment).commit();
        navigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navigationView.setSelectedItemId(R.id.navigation_video);
    }

    /**
     * 给BottomNavigationView添加按钮的点击事件
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //每次点击后都将所有图标重置到默认不选中图片
            resetToDefaultIcon();

            switch (item.getItemId()) {
                case R.id.navigation_video:
                    //判断要跳转的页面是否是当前页面，若是则不做动作
                    if (lastFragment != 0) {
                        switchFragment(lastFragment, 0);
                        lastFragment = 0;
                    }
                    //设置按钮的
                    item.setIcon(R.drawable.ic_duo);
                    return true;
                case R.id.navigation_train:
                    if (lastFragment != 1) {
                        switchFragment(lastFragment, 1);
                        lastFragment = 1;
                    }
                    item.setIcon(R.drawable.ic_directions_run);
                    return true;
                case R.id.navigation_social:
                    if (lastFragment != 2) {
                        switchFragment(lastFragment, 2);
                        lastFragment = 2;
                    }
                    item.setIcon(R.drawable.ic_person_add);
                    return true;
                case R.id.navigation_daka:
                    if (lastFragment != 3) {
                        switchFragment(lastFragment, 3);
                        lastFragment = 3;
                    }
                    item.setIcon(R.drawable.ic_calendar_today);
                    return true;
                case R.id.navigation_my:
                    if (lastFragment != 4) {
                        switchFragment(lastFragment, 4);
                        lastFragment = 4;
                    }
                    item.setIcon(R.drawable.ic_account_box);
                    return true;
            }
            return false;
        }
    };


    private void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //隐藏上个Fragment
        transaction.hide(fragmentlist[lastFragment]);

        //判断transaction中是否加载过index对应的页面，若没加载过则加载
        if (fragmentlist[index].isAdded() == false) {
            transaction.add(R.id.fl_main, fragmentlist[index]);
        }
        //根据角标将fragment显示出来
        transaction.show(fragmentlist[index]).commitAllowingStateLoss();
    }


    /**
     * 重新配置每个按钮的图标
     */
    private void resetToDefaultIcon() {
        navigationView.getMenu().findItem(R.id.navigation_video).setIcon(R.drawable.ic_duo);
        navigationView.getMenu().findItem(R.id.navigation_train).setIcon(R.drawable.ic_directions_run);
        navigationView.getMenu().findItem(R.id.navigation_social).setIcon(R.drawable.ic_person_add);
        navigationView.getMenu().findItem(R.id.navigation_daka).setIcon(R.drawable.ic_calendar_today);
        navigationView.getMenu().findItem(R.id.navigation_my).setIcon(R.drawable.ic_account_box);
    }


}