package org.khmeracademy.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.khmeracademy.Model.CommentItem;
import org.khmeracademy.R;

import java.util.ArrayList;

/**
 * Created by sok-ngim on 1/13/16.
 */

public class ReplyCommentAdapter extends RecyclerView.Adapter<ReplyCommentAdapter.DataObjectHolder> {
    private static String LOG_TAG = "CommentAdapter";
    private ArrayList<CommentItem> mDataset;
    private Context mContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView commentText;
        TextView dateTime;
        ImageView userIcon;

        public DataObjectHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tv_cmt_user_name);
            commentText = (TextView) itemView.findViewById(R.id.tv_cmt_text);
            dateTime = (TextView) itemView.findViewById(R.id.tv_cmt_date);
            userIcon = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public ArrayList<CommentItem> getList() {
        return mDataset;
    }
    public ReplyCommentAdapter(ArrayList<CommentItem> myDataset, Activity activity) {
        mContext = activity;
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
        Picasso.with(mContext)
                .load(mDataset.get(position).getUserImageUrl())
                .placeholder(R.drawable.icon_user1)
                .error(org.khmeracademy.R.drawable.thumbnail_error).into(holder.userIcon);
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