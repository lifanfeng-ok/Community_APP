package com.example.community;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class Train_fragment extends Fragment {
    public BottomNavigationView navigationView;
    public Fragment time_Fragment;
    public Fragment number_Fragment;
    public Fragment[] fragmentlist;
    public TextView tv1;
    private int lastFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        time_Fragment=new time_fragment();
        number_Fragment=new number_fragment();
        fragmentlist=new Fragment[]{time_Fragment,number_Fragment};
        lastFragment = 0;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_train_fragment, null);
        navigationView=view.findViewById(R.id.bnv_main3);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getChildFragmentManager().beginTransaction().replace(R.id.fl_main3, time_Fragment)
                .show(time_Fragment).commit();
        navigationView.setSelectedItemId(R.id.navigation_train);
//        return inflater.inflate(R.layout.fragment_regular_fragment, null, false);
        return view;
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //每次点击后都将所有图标重置到默认不选中图片
            resetToDefaultIcon();

            switch (item.getItemId()) {
                case R.id.navigation_time:
                    //判断要跳转的页面是否是当前页面，若是则不做动作
                    if (lastFragment != 0) {
                        switchFragment(lastFragment, 0);
                        lastFragment = 0;
                    }
                    //设置按钮的
                    item.setIcon(R.drawable.ic_alarm);
                    return true;
                case R.id.navigation_number:
                    if (lastFragment != 1) {
                        switchFragment(lastFragment, 1);
                        lastFragment = 1;
                    }
                    item.setIcon(R.drawable.ic_alarm_add);
                    return true;

            }
            return false;
        }
    };
    private void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //隐藏上个Fragment
        transaction.hide(fragmentlist[lastFragment]);

        //判断transaction中是否加载过index对应的页面，若没加载过则加载
        if (fragmentlist[index].isAdded() == false) {
            transaction.add(R.id.fl_main3, fragmentlist[index]);
        }
        //根据角标将fragment显示出来
        transaction.show(fragmentlist[index]).commitAllowingStateLoss();
    }


    /**
     * 重新配置每个按钮的图标
     */
    private void resetToDefaultIcon() {
        navigationView.getMenu().findItem(R.id.navigation_time).setIcon(R.drawable.ic_alarm);
        navigationView.getMenu().findItem(R.id.navigation_number).setIcon(R.drawable.ic_alarm_add);
    }
}