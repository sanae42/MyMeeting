package com.example.mymeeting.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mymeeting.R;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;

public class ConversationActivity extends AppCompatActivity {

    // 当前聊天的 ID
    private String mChatId;
    private EaseChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // 这里直接使用EaseUI封装好的聊天界面
        chatFragment = new EaseChatFragment();
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