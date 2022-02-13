package com.example.mymeeting.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mymeeting.R;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;


public class MyConversationListFragment extends EaseConversationListFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //设置头像尺寸
        conversationListLayout.setAvatarSize(EaseCommonUtils.dip2px(mContext, 50));
        //设置是否隐藏未读消息数，默认为不隐藏
        conversationListLayout.hideUnreadDot(false);
        //设置未读消息数展示位置，默认为左侧
        conversationListLayout.showUnreadDotPosition(EaseConversationSetStyle.UnreadDotPosition.LEFT);
        //设置用户默认头像
        conversationListLayout.setAvatarDefaultSrc(getResources().getDrawable(R.mipmap.user_icon));

        EaseIM.getInstance().setUserProvider(new EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                EaseUser user = new EaseUser(username);
//                //设置用户昵称
//                user.setNickname("测试昵称");
//                //设置头像地址
//                user.setAvatar();

                return user;
            }
        });
    }



    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        //添加点击事件实现逻辑
        Object item = conversationListLayout.getItem(position).getInfo();
        // 跳转到聊天界面，开始聊天
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
        intent.putExtra("conversationId", ((EMConversation)item).conversationId());
        intent.putExtra("chatType", EMMessage.ChatType.Chat);
        //优先漫游
        intent.putExtra("isRoaming", true);
        startActivity(intent);

        if(item instanceof EMConversation) {
            if(EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item)) {

            }else {

            }
        }
    }
}