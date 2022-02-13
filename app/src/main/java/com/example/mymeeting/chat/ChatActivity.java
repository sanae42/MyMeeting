package com.example.mymeeting.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.map.ui.indoor.IndoorMapFragment;
import com.example.mymeeting.map.ui.outdoor.OutdoorMapFragment;
import com.example.mymeeting.note.AllNoteListAdapter;
import com.example.mymeeting.note.EditNoteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.multimedia.audiokit.utils.Constant;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

import static org.litepal.LitePalApplication.getContext;

public class ChatActivity extends BaseActivity {

    final String TAG = "ChatActivity";

    ViewPager mViewPager;
    BottomNavigationView navView;

    //消息接受监听器
    EMMessageListener msgListener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //环信退出
        EMClient.getInstance().logout(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 加载所有会话到内存
        EMClient.getInstance().chatManager().loadAllConversations();
        // 加载所有群组到内存
        EMClient.getInstance().groupManager().loadAllGroups();

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                for(EMMessage msg:messages){
                    Log.d(TAG, "收到一条消息: "+msg.getUserName()+" "+msg.getBody().toString());
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }
            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);


        initiateView();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_message:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_friend:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_other:
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    //初始化布局
    private void initiateView(){
        //        导航条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //   设置actionbar（即toolbar）最左侧按钮显示状态和图标
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        mViewPager=(ViewPager) findViewById(R.id.view_pager);

        navView = findViewById( R.id.nav_view );
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );


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
        final ArrayList<Fragment> fgLists=new ArrayList<>(3);
        MyConversationListFragment myConversationListFragment = new MyConversationListFragment();
        MyContactListFragment myContactListFragment = new MyContactListFragment();

        fgLists.add(myConversationListFragment);
        fgLists.add(myContactListFragment);
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
        mViewPager.setOffscreenPageLimit(2); //预加载剩下两页
        //以上就将Fragment装入了ViewPager

    }

    /**
     * 按键监听，此处即toolbar上按键
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(int error) {
            EMLog.d("global listener", "onDisconnect" + error);
            if (error == EMError.USER_REMOVED) {
                Toast.makeText(getContext(), "USER_REMOVED", Toast.LENGTH_SHORT).show();
                finish();
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                Toast.makeText(getContext(), "USER_LOGIN_ANOTHER_DEVICE", Toast.LENGTH_SHORT).show();
                finish();
            } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                Toast.makeText(getContext(), "SERVER_SERVICE_RESTRICTED", Toast.LENGTH_SHORT).show();
                finish();
            } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                Toast.makeText(getContext(), "USER_KICKED_BY_CHANGE_PASSWORD", Toast.LENGTH_SHORT).show();
                finish();
            } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                Toast.makeText(getContext(), "USER_KICKED_BY_OTHER_DEVICE", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}