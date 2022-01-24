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

    private static final String TAG = "FruitAdapter";

    private Context mContext;

    private List<Meeting> mMeetingList;

    public MeetingListAdapter(List<Meeting> meetingList) {
        mMeetingList = meetingList;
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
        Meeting meeting = mMeetingList.get(position);
        holder.fruitName.setText(meeting.getName());
        Glide.with(mContext).load(meeting.getImageId()).into(holder.fruitImage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Meeting meeting = mMeetingList.get(position);
                Intent intent = new Intent(mContext, MeetingActivity.class);
                intent.putExtra(MeetingActivity.FRUIT_NAME, meeting.getName());
                intent.putExtra(MeetingActivity.FRUIT_IMAGE_ID, meeting.getImageId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMeetingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView fruitImage;
        TextView fruitName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            fruitName = (TextView) view.findViewById(R.id.fruit_name);
        }
    }
}
