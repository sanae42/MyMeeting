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

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.map.ui.indoor.IndoorMapFragment;
import com.example.mymeeting.map.ui.outdoor.OutdoorMapFragment;
import com.example.mymeeting.note.AllNoteListAdapter;
import com.example.mymeeting.note.EditNoteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

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
        // 加载所有群组到内存，如果使用了群组的话
        EMClient.getInstance().groupManager().loadAllGroups();

        String toChatUsername = "";
        if(BmobUser.getCurrentUser().getUsername().equals("18301038"))
        {
            toChatUsername = "1";
        }else if(BmobUser.getCurrentUser().getUsername().equals("1")){
            toChatUsername = "18301038";
        }
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage("你好呀", toChatUsername);

        message.setMessageStatusCallback(new EMCallBack(){

            @Override
            public void onSuccess() {
                Log.d(TAG, "消息发送成功");
            }

            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        message.setChatType(EMMessage.ChatType.Chat);
        EMClient.getInstance().chatManager().sendMessage(message);

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
//                    navController.navigate(R.id.action_indoorMapFragment_to_outdoorMapFragment);
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_friend:
//                    navController.navigate(R.id.action_outdoorMapFragment_to_indoorMapFragment);
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_other:
//                    navController.navigate(R.id.action_outdoorMapFragment_to_indoorMapFragment);
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
        EaseContactListFragment easeContactListFragment = new EaseContactListFragment();

        fgLists.add(myConversationListFragment);
        fgLists.add(easeContactListFragment);
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
}