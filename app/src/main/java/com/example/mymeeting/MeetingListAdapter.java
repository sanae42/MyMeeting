package com.example.mymeeting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListAdapter.ViewHolder> {

    private static final String TAG = "MeetingAdapter";

    private Context mContext;

    private List<meetingItem> mMeetingItemList;

    public MeetingListAdapter(List<meetingItem> meetingItemList) {
        mMeetingItemList = meetingItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.meeting_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                Fruit fruit = mFruitList.get(position);
//                Intent intent = new Intent(mContext, FruitActivity.class);
//                intent.putExtra(FruitActivity.FRUIT_NAME, fruit.getName());
//                intent.putExtra(FruitActivity.FRUIT_IMAGE_ID, fruit.getImageId());
//                mContext.startActivity(intent);
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        meetingItem meetingItem = mMeetingItemList.get(position);
        //recyclerView的item样式设置
        holder.meetingName.setText(meetingItem.getName());
        holder.meetingDate.setText(meetingItem.getHostDate().toString());
        holder.meetingLocation.setText(meetingItem.getLocation());
        Glide.with(mContext).load(meetingItem.getImageId()).into(holder.meetingImage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingItem meetingItem = mMeetingItemList.get(position);
                Intent intent = new Intent(mContext, MeetingActivity.class);

                //传递序列化的meetingItem到MeetingActivity
                intent.putExtra("meeting_item",meetingItem);
                //强制转换mContext为MainActivity，使用startActivityForResult，可以不用手动刷新了
                ((MainActivity) mContext).startActivityForResult(intent, 3);
//                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMeetingItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView meetingImage;
        TextView meetingName;
        TextView meetingDate;
        TextView meetingLocation;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            meetingImage = (ImageView) view.findViewById(R.id.meeting_image);
            meetingName = (TextView) view.findViewById(R.id.meeting_name);
            meetingDate = (TextView) view.findViewById(R.id.meeting_date);
            meetingLocation = (TextView) view.findViewById(R.id.meeting_location);
        }
    }
}
