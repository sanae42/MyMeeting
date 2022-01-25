package com.example.mymeeting.pager;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mymeeting.R;
import com.example.mymeeting.MeetingFragment;


//public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//    //    @StringRes
//    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
//    private final Context mContext;
//
//    List<fragment1> fragments;
//
//    public SectionsPagerAdapter(Context context, FragmentManager fm, List<fragment1> fragments) {
//        super(fm);
//        mContext = context;
//        this.fragments=fragments;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        // getItem is called to instantiate the fragment for the given page.
//        // Return a PlaceholderFragment (defined as a static inner class below).
//        switch (position){
//            case 0:
//                return fragments.get(0);
//            case 1:
////                return new fra2();
//                return fragments.get(1);
////                            case 2:
////                                     return new Image3Fm();
//        }
//        return null;
////        return fragments[position];
//    }
//
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return mContext.getResources().getString(TAB_TITLES[position]);
//
//    }
//
//    @Override
//    public int getCount() {
//        // Show 2 total pages.
//        return 2;
//    }
//
//}

/**
 * 主活动pageView组件的适配器
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    //    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    Fragment[] fragments = {new MeetingFragment(1),new MeetingFragment(2)};

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        return fragments[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);

    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

}
