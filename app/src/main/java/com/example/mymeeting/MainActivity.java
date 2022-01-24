package com.example.mymeeting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mymeeting.pager.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    //    侧边栏
    private DrawerLayout mDrawerLayout;

    //    搜索栏
    SearchView searchView;

//    ViewPager及其适配器
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;

//    TODO：登录相关功能ViewModel暂未启用
    //    ViewModel
    private SharedViewModel model;
    //    登录状态
    private boolean log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //    TODO：登录相关功能ViewModel暂未启用
//        model = ViewModelProviders.of(this).get(SharedViewModel.class);
//        model.getLog().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean b) {
//                log = model.getLog().getValue();
//
//            }
//        });

        //    初始化控件
        initiateView();

//        //model初始化
//        model = ViewModelProviders.of(this).get(SharedViewModel.class);
//        model.logout();
//        //初始是未登录状态
//        log = false;
////        ((fragment1) sectionsPagerAdapter.getItem(0)).changeLogMode(log);
//        model.getLog().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean b) {
//                log = model.getLog().getValue();
//            }
//        });
    }

//    初始化控件
    private void initiateView(){
        //        导航条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        侧边栏布局
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        设置actionbar（即toolbar）最左侧按钮功能，点击唤出侧边栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        }

//        设置NavigationView布局
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
//        将NavigationView中的call作为默认选项选中
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            //侧边栏按键点击
            public boolean onNavigationItemSelected(MenuItem item) {
//                点击NavigationView中选项关闭侧边栏
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

//        悬浮按钮
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "点击了悬浮按钮", Toast.LENGTH_SHORT).show();
                Snackbar.make(v, "Data deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                //    TODO：登录相关功能ViewModel暂未启用
//                if(model.getLog().getValue()==true)model.logout();
//                else model.login();
            }
        });

        //SectionsPagerAdapter设置
//        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),fragments);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        //ViewPager设置（中间空白部分）
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        //tab(上方切换条)
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }




    //菜单按键监听，此处菜单即toolbar上一系列按键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //开启侧边栏
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            //导航条按键
//            case R.id.backup:
//                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    //  菜单创建 及搜索框相关
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        searchView = (SearchView) findViewById(R.id.search);
        //默认就是搜索框展开
        searchView.setIconified(true);
        //一直都是搜索框，搜索图标在输入框左侧（默认是内嵌的）
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //文字输入完成，提交的回调
            @Override
            public boolean onQueryTextSubmit(String s) {
//                TODO: 获得fragment实例并调用refresh
                ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,viewPager.getCurrentItem())).refresh(s);
                return true;
            }

            //输入文字发生改变
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //点击搜索图标，搜索框展开时的回调
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return true;
    }
}
