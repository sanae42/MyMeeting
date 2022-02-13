package com.example.mymeeting.allParticipants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymeeting.R;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.note.AllNoteListAdapter;

import java.util.List;

public class AllParticipantsListAdapter extends RecyclerView.Adapter<AllParticipantsListAdapter.ViewHolder> {

    private static final String TAG = "AllParticipantsListAdapter";

    private Context mContext;

    private List<_User> mAllParticipantsList;

    public AllParticipantsListAdapter(List<_User> allParticipantsList) {
        mAllParticipantsList = allParticipantsList;
    }

    //***************************************************
    //自定义recyclerView的项目监听器
    private AllParticipantsListAdapter.OnClickListener onClickListener;//接口对象

    public AllParticipantsListAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(AllParticipantsListAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    /**
     * 定义点击事件回调接口.
     */
    public interface OnClickListener {
        /**
         * 点击事件.
         */
        void onClick(View itemView, int position);

        /**
         * 长点击事件.
         */
        void onLongClick(View itemView, int position);

//        /**
//         * 选项改变事件.
//         */
//        void onCheckedChange(View itemView, int position, boolean isChecked);
    }


    //***************************************************

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.participants_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        _User participant = mAllParticipantsList.get(position);
        //recyclerView的item样式设置
        holder.objectId.setText(participant.getObjectId());
        holder.name.setText(participant.getUsername());
        holder.nick.setText(participant.getNick());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                meetingItem meetingItem = mMeetingItemList.get(position);
//                Intent intent = new Intent(mContext, MeetingActivity.class);
//
//                //传递序列化的meetingItem到MeetingActivity
//                intent.putExtra("meeting_item",meetingItem);
//                //强制转换mContext为MainActivity，使用startActivityForResult，可以不用手动刷新了
//                ((MainActivity) mContext).startActivityForResult(intent, 3);
////                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAllParticipantsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView objectId;
        TextView name;
        TextView nick;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            objectId = (TextView) view.findViewById(R.id.objectId);
            name = (TextView) view.findViewById(R.id.name);
            nick = (TextView) view.findViewById(R.id.nick);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 设置单击事件并回调给页面
                    if (onClickListener != null) {
                        onClickListener.onClick(itemView,getLayoutPosition());
                    }
                }
            });
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onLongClick(itemView,getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
