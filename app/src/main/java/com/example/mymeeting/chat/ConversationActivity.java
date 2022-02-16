package com.example.mymeeting.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.List;

public class ConversationActivity extends BaseActivity {

    final String TAG = "ConversationActivity";

    // 当前聊天的 ID
    private String mChatId;
    private MyChatFragment chatFragment;

    //消息接受监听器
    EMMessageListener msgListener;
    EMGroupChangeListener groupListener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销监听
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // 加载所有会话到内存
        EMClient.getInstance().chatManager().loadAllConversations();
        // 加载所有群组到内存
        EMClient.getInstance().groupManager().loadAllGroups();

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

        EaseTitleBar titleBarMessage = findViewById(R.id.title_bar);
        //设置右侧菜单图标
        titleBarMessage.setRightImageResource(R.drawable.chat_user_info);
        //设置标题
        if(getIntent().getExtras().get("chatType").equals(EMMessage.ChatType.Chat)){
            titleBarMessage.setTitle("和用户"+getIntent().getExtras().get("conversationId").toString()+"的聊天");
        }else if(getIntent().getExtras().get("chatType").equals(EMMessage.ChatType.GroupChat)){
            titleBarMessage.setTitle("");
        }

//        //设置标题位置
//        titleBarMessage.setTitlePosition(EaseTitleBar.TitlePosition.Left);
        //设置右侧菜单图标的点击事件
        titleBarMessage.setOnRightClickListener(new EaseTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {

            }
        });
        //设置返回按钮的点击事件
        titleBarMessage.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                finish();
            }
        });

        // 这里直接使用EaseUI封装好的聊天界面
        chatFragment = new MyChatFragment();
        // 将参数传递给聊天界面
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.ec_layout_container, chatFragment).commit();

    }

    /**
     * 初始化界面
     */
    private void initView() {

    }
}