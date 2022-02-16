package com.example.mymeeting.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.modules.contact.EaseContactListFragment;
import com.hyphenate.easeui.modules.contact.EaseContactListLayout;

public class MyContactListFragment extends EaseContactListFragment {

    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
//        return super.onMenuItemClick(item, position);

        // 跳转到聊天界面，开始聊天
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
        intent.putExtra("conversationId", "1");
        intent.putExtra("chatType", EMMessage.ChatType.Chat);
        //优先漫游
        intent.putExtra("isRoaming", true);
        startActivity(intent);

        if(item instanceof EMConversation) {
            if(EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item)) {

            }else {

            }
        }

        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EaseContactListLayout contactList = contactLayout.getContactList();
        contactList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //添加点击事件实现逻辑
                String username = contactList.getItem(position).getUsername();
                // 跳转到聊天界面，开始聊天
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
                intent.putExtra("conversationId", username);
                intent.putExtra("chatType", 1);
                //优先漫游
                intent.putExtra("isRoaming", true);
                startActivity(intent);
            }
        });
    }
}
