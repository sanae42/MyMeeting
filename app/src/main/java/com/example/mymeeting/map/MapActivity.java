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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MapActivity extends BaseActivity {

//    OutdoorMapFragment outdoorMapFragment;
//    IndoorMapFragment indoorMapFragment;
//
//    final FragmentManager fragmentManager = getSupportFragmentManager();

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();  //使用fragmentmanager和transaction来实现切换效果
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_outdoor:
                    transaction.replace(R.id.nav_host_fragment,new OutdoorMapFragment());  //对应的java class
                    transaction.commit();  //一定不要忘记commit，否则不会显示
                    return true;
                case R.id.navigation_indoor:
                    transaction.replace(R.id.nav_host_fragment,new IndoorMapFragment());  //对应的java class
                    transaction.commit();  //一定不要忘记commit，否则不会显示
                    return true;

            }
            return false;
        }
    };

    // 设置默认进来是tab 显示的页面
    private void setDefaultFragment(){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment,new OutdoorMapFragment());
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setDefaultFragment();

        BottomNavigationView navView = findViewById( R.id.nav_view );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

    }

}