package org.khmeracademy.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import org.khmeracademy.Model.PlayListItem;
import org.khmeracademy.NetworkRequest.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longdy on 12/16/2015.
 */
public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistRecyclerAdapter.SimpleItemViewHolder> {

    private List<PlayListItem> items;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Provide access to all the views for a data item in a view holder
    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView description;
        TextView duration;
        TextView video_url;
        LinearLayout mListRow;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(org.khmeracademy.R.id.iv_playlist_thumbnail);
            title = (TextView) itemView.findViewById(org.khmeracademy.R.id.tv_playlist_title);
            description = (TextView) itemView.findViewById(org.khmeracademy.R.id.tv_playlist_description);
            duration = (TextView) itemView.findViewById(org.khmeracademy.R.id.tv_playlist_duration);
            mListRow = (LinearLayout) itemView.findViewById(org.khmeracademy.R.id.list_row);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    //mListRow.setBackgroundColor(Color.parseColor("#d5d5d5"));
                    return false;
                }
            });

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaylistRecyclerAdapter(List<PlayListItem> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    // Create new items (invoked by the layout manager)
    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public SimpleItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(org.khmeracademy.R.layout.list_playlist_item, viewGroup, false);
        return new SimpleItemViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PlaylistRecyclerAdapter.SimpleItemViewHolder viewHolder, int position) {
        //viewHolder.thumbnail.setImageUrl("http://img.youtube.com/vi/" + items.get(position).getVideo_url() + "/mqdefault.jpg", imageLoader);
        Picasso.with(mContext)
                .load("http://img.youtube.com/vi/" + items.get(position).getVideo_url() + "/mqdefault.jpg")
                .placeholder(org.khmeracademy.R.drawable.thumbnail_not_yet_display)
                .error(org.khmeracademy.R.drawable.thumbnail_error).into(viewHolder.thumbnail);
        viewHolder.title.setText(items.get(position).getTitle());
        viewHolder.description.setText(items.get(position).getDescription());
        viewHolder.duration.setText(items.get(position).getDuration());

        if (items.get(position).isSelected()) {
            viewHolder.mListRow.setBackgroundColor(Color.parseColor("#8A8A8A"));
        } else {
            viewHolder.mListRow.setBackgroundColor(Color.parseColor("#555555"));
        }
    }

/*    public void add(int position, PlayListItem item) {
        items.add(position, item);
        notifyItemInserted(position);
    }
    public void remove(String item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }*/

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaylistRecyclerAdapter(ArrayList<PlayListItem> item, Context context) {
        items = item;
        mPref = context.getSharedPreferences("item", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mContext = context;
    }

    public void setSelected(int pos) {
        int stored_index = mPref.getInt("position", 0);
        try {
            if (items.size() > 1) {
                // Because big playlist have index bigger than small playlist, IF not check , => Error
                if (stored_index > items.size() - 1) {
                    stored_index = 0;
                }
                // set old position to not highlighted after selecting new position
                items.get(stored_index).setSelected(false);
                // save current position to stop hightlighted after selecting new position
                mEditor.putInt("position", pos);
                mEditor.apply();
            }
            // set current position to be highlighted
            items.get(pos).setSelected(true);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
