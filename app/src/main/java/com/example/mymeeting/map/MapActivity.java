package com.example.mymeeting.map;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.map.ui.indoor.IndoorMapFragment;
import com.example.mymeeting.map.ui.outdoor.OutdoorMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MapActivity extends BaseActivity {

//    OutdoorMapFragment outdoorMapFragment;
//    IndoorMapFragment indoorMapFragment;
//
//    final FragmentManager fragmentManager = getSupportFragmentManager();

    NavController navController;

    ViewPager mViewPager;
    BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_outdoor:
//                    navController.navigate(R.id.action_indoorMapFragment_to_outdoorMapFragment);
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_indoor:
//                    navController.navigate(R.id.action_outdoorMapFragment_to_indoorMapFragment);
                    mViewPager.setCurrentItem(1);
                    return true;

            }
            return false;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mViewPager=(ViewPager) findViewById(R.id.view_pager);

        navView = findViewById( R.id.nav_view );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );


//        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);


        //ViewPager的监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navView.getMenu().getItem(position).setChecked(true);
                //滑动页面后做的事，这里与BottomNavigationView结合，使其与正确page对应
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //底部导航栏有几项就有几个Fragment
        final ArrayList<Fragment> fgLists=new ArrayList<>(2);
        fgLists.add(new OutdoorMapFragment());
        fgLists.add(new IndoorMapFragment());
//        fgLists.add(new MyFragment());


        //设置适配器用于装载Fragment
        FragmentPagerAdapter mPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fgLists.get(position);  //得到Fragment
            }

            @Override
            public int getCount() {
                return fgLists.size();  //得到数量
            }
        };
        mViewPager.setAdapter(mPagerAdapter);   //设置适配器
        mViewPager.setOffscreenPageLimit(1); //预加载剩下两页

        //以上就将Fragment装入了ViewPager

    }

}