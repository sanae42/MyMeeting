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

    NavController navController;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_outdoor:
                    navController.navigate(R.id.action_indoorMapFragment_to_outdoorMapFragment);
                    return true;
                case R.id.navigation_indoor:
                    navController.navigate(R.id.action_outdoorMapFragment_to_indoorMapFragment);
                    return true;

            }
            return false;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        BottomNavigationView navView = findViewById( R.id.nav_view );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);

    }

}