package com.myapp.finalapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment>fragList; //不同fragment（页面）的集合
    private List<String>titleList;  //标题集合
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment>fragList, List<String>titleList){
        super(fm);
        this.fragList=fragList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int arg0){
        return fragList.get(arg0);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return titleList.get(position);
    }

    @Override
    public int getCount(){
        return fragList.size();
    }
}
