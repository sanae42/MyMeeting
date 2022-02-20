package com.example.mymeeting.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymeeting.R;
import com.example.mymeeting.db.notificationItem;

import java.util.List;

public class NotificationListdapter extends RecyclerView.Adapter<NotificationListdapter.ViewHolder> {

    private static final String TAG = "NotificationAListdapter";

    private Context mContext;

    private List<notificationItem> mNotificationList;

    public NotificationListdapter(List<notificationItem> notificationList) {
        mNotificationList = notificationList;
    }

    //***************************************************
    //自定义recyclerView的项目监听器
    private NotificationListdapter.OnClickListener onClickListener;//接口对象

    public NotificationListdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(NotificationListdapter.OnClickListener onClickListener) {
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
    public NotificationListdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        final NotificationListdapter.ViewHolder holder = new NotificationListdapter.ViewHolder(view);

        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView card_view;
        TextView title;
        TextView create_date;
        TextView content;

        public ViewHolder(View view) {
            super(view);
            card_view = (CardView) view.findViewById(R.id.card_view);
            title = (TextView) view.findViewById(R.id.title);
            create_date = (TextView) view.findViewById(R.id.create_date);
            content = (TextView) view.findViewById(R.id.content);

            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 设置单击事件并回调给页面
                    if (onClickListener != null) {
                        onClickListener.onClick(itemView,getLayoutPosition());
                    }
                }
            });
            card_view.setOnLongClickListener(new View.OnLongClickListener() {
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

    @Override
    public void onBindViewHolder(@NonNull NotificationListdapter.ViewHolder holder, int position) {
        notificationItem notification = mNotificationList.get(position);
        //recyclerView的item样式设置
        holder.title.setText(notification.getTitle());
        holder.create_date.setText(notification.getCreateDate().toString());
        holder.content.setText(notification.getContent());
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }
}
