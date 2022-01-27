package com.example.mymeeting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb.doBomb;
import com.example.mymeeting.pager.SectionsPagerAdapter;
import com.example.mymeeting.sp.UserStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.sql.Time;
import java.time.ZonedDateTime;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends AppCompatActivity {

    //    侧边栏
    private DrawerLayout mDrawerLayout;

    //    搜索栏
    SearchView searchView;

    //    ViewPager及其适配器
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;

    //  侧边栏头部布局
    RelativeLayout loggedLayout;
    RelativeLayout unloggedLayout;

    String appkey = "de0d0d10141439f301fc9d139da66920";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //    初始化控件
        initiateView();

        //TODO: 测试阶段默认自动登录
        //TODO: BombUser自带自动登录功能
//        UserStatus userStatus = new UserStatus(getContext());
//        if(true){
//            userStatus.login(1002,"18301038","111111",true);
//        }

    }


    /**
     * 初始化控件
     */
    private void initiateView(){
        //        导航条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //    侧边栏布局
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //   设置actionbar（即toolbar）最左侧按钮功能，点击唤出侧边栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        //  设置NavigationView布局
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //   将NavigationView中的call作为默认选项选中
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            //侧边栏按键点击
            public boolean onNavigationItemSelected(MenuItem item) {
        //     点击NavigationView中选项关闭侧边栏
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        //  根据用户是否登录改变侧边栏headerLayout样式
        //TODO:登录相关
        Bmob.initialize(getContext(),appkey);
        View headview=navView.inflateHeaderView(R.layout.nav_header);
        loggedLayout = (RelativeLayout)headview.findViewById(R.id.loggedLayout);
        unloggedLayout = (RelativeLayout)headview.findViewById(R.id.unloggedLayout);
        Button logoutButton = (Button) headview.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();
                loggedLayout.setVisibility(View.GONE);
                unloggedLayout.setVisibility(View.VISIBLE);
            }
        });
        Button loginButton = (Button) headview.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LoginActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("type", "new");
//                intent.putExtras(bundle);
                //TODO:收到结果时决定是否刷新布局为登录状态
                startActivityForResult(intent,1);
            }
        });
        if (BmobUser.isLogin()) {
            loggedLayout.setVisibility(View.VISIBLE);
            unloggedLayout.setVisibility(View.GONE);
//            User user = BmobUser.getCurrentUser(User.class);
//            Snackbar.make(view, "已经登录：" + user.getUsername(), Snackbar.LENGTH_LONG).show();
        } else {
            loggedLayout.setVisibility(View.GONE);
            unloggedLayout.setVisibility(View.VISIBLE);
        }


        //        悬浮按钮
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "要新建会议吗（测试会议）", Snackbar.LENGTH_SHORT)
                        .setAction("是的", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                doBomb dobomb = new doBomb(getContext());
//                                dobomb.addMeetingTest();
//                                dobomb.searchAllMeeting();
                                dobomb.searchAttendingMeeting();

                            }
                        })
                        .show();
            }
        });

        //SectionsPagerAdapter设置
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        //ViewPager设置（中间空白部分）
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        //tab(上方切换条)
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }



    /**
     * 菜单按键监听，此处菜单即toolbar上一系列按键
     */
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

    /**
     * 菜单创建 及搜索框相关
     */
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
            //    TODO: 获得fragment实例并调用refresh
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

    /**
     * 接收活动返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    Boolean if_login = data.getBooleanExtra("login", false);
                    if(if_login==true){
                        loggedLayout.setVisibility(View.VISIBLE);
                        unloggedLayout.setVisibility(View.GONE);
                    }
                    //TODO:登录成功，刷新侧边栏头部，并且刷新两个fragment获取数据
                }
                break;

        default:
        }
    }
}
