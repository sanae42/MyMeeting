package com.example.mymeeting.academicSchedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymeeting.R;
import com.example.mymeeting.bomb.Schedule;
import com.example.mymeeting.db.noteItem;
import com.example.mymeeting.note.AllNoteListAdapter;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>{

    private static final String TAG = "ScheduleListAdapter";

    private Context mContext;

    private List<Schedule> mScheduleList;

    public ScheduleListAdapter(List<Schedule> scheduleList) {
        mScheduleList = scheduleList;
    }

    //***************************************************
    //自定义recyclerView的项目监听器
    private ScheduleListAdapter.OnClickListener onClickListener;//接口对象

    public ScheduleListAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ScheduleListAdapter.OnClickListener onClickListener) {
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
    public ScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_item, parent, false);
        final ScheduleListAdapter.ViewHolder holder = new ScheduleListAdapter.ViewHolder(view);

        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView card_view;
        TextView title;
        TextView content;
        TextView date;
        TextView start;
        TextView end;

        public ViewHolder(View view) {
            super(view);
            card_view = (CardView) view.findViewById(R.id.card_view);
            title = (TextView) view.findViewById(R.id.title_textview);
            content = (TextView) view.findViewById(R.id.content_textview);
            date = (TextView) view.findViewById(R.id.date_textview);
            start = (TextView) view.findViewById(R.id.start_textview);
            end = (TextView) view.findViewById(R.id.end_textview);

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
    public void onBindViewHolder(@NonNull ScheduleListAdapter.ViewHolder holder, int position) {
        Schedule schedule = mScheduleList.get(position);
        //recyclerView的item样式设置
        holder.title.setText(schedule.getTitle());
        holder.content.setText(schedule.getContent());
        holder.date.setText(schedule.getDate());
        holder.start.setText(schedule.getStart());
        holder.end.setText(schedule.getEnd());
    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }
}
