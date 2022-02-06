package com.example.mymeeting.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymeeting.R;
import com.example.mymeeting.allParticipants.AllParticipantsListAdapter;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.db.noteItem;

import java.util.List;

public class AllNoteListAdapter  extends RecyclerView.Adapter<AllNoteListAdapter.ViewHolder>{

    private static final String TAG = "AllNoteListAdapter";

    private Context mContext;

    private List<noteItem> mAllNoteList;

    public AllNoteListAdapter(List<noteItem> allNoteList) {
        mAllNoteList = allNoteList;
    }

    //***************************************************
    //自定义recyclerView的项目监听器
    private OnClickListener onClickListener;//接口对象

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
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
    public AllNoteListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_item, parent, false);
        final AllNoteListAdapter.ViewHolder holder = new AllNoteListAdapter.ViewHolder(view);

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
    public void onBindViewHolder(@NonNull AllNoteListAdapter.ViewHolder holder, int position) {
        noteItem note = mAllNoteList.get(position);
        //recyclerView的item样式设置
        holder.title.setText(note.getTitle());
        holder.create_date.setText(note.getCreateDate().toString());
        holder.content.setText(note.getContent());
    }

    @Override
    public int getItemCount() {
        return mAllNoteList.size();
    }
}
