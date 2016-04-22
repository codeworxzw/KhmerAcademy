package org.khmeracademy.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.khmeracademy.Model.CommentItem;
import org.khmeracademy.R;

import java.util.ArrayList;

/**
 * Created by sok-ngim on 1/13/16.
 */

public class ReplyCommentAdapter extends RecyclerView.Adapter<ReplyCommentAdapter.DataObjectHolder> {
    private static String LOG_TAG = "CommentAdapter";
    private ArrayList<CommentItem> mDataset;
    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView commentText;
        TextView dateTime;

        public DataObjectHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tv_cmt_user_name);
            commentText = (TextView) itemView.findViewById(R.id.tv_cmt_text);
            dateTime = (TextView) itemView.findViewById(R.id.tv_cmt_date);
        }
    }

    public ArrayList<CommentItem> getList() {
        return mDataset;
    }
    public ReplyCommentAdapter(ArrayList<CommentItem> myDataset) {
        this.mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reply_comment_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.userName.setText(mDataset.get(position).getUserName());
        holder.commentText.setText(mDataset.get(position).getCmt_text());
        holder.dateTime.setText(mDataset.get(position).getCmt_date());
    }

    public void addItem(CommentItem cmt, int index) {
        mDataset.add(cmt);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}