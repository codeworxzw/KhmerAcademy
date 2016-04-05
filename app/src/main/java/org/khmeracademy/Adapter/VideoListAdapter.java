package org.khmeracademy.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.khmeracademy.Model.ListVideoItem;
import org.khmeracademy.R;

import java.util.List;

/**
 * Created by Longdy on 12/21/2015.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.SimpleItemViewHolder>  {

    private List<ListVideoItem> items;

    // Provide a reference to the views for each data item
    // Provide access to all the views for a data item in a view holder
    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        private TextView videoTitle,duration,numVideos;
        private View bgColorNum ;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.tv_video_title);
            duration = (TextView) itemView.findViewById(R.id.tv_video_duration);
            numVideos = (TextView)itemView.findViewById(R.id.tv_video_list_num);
            bgColorNum = itemView.findViewById(R.id.vi_num_list_video);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public VideoListAdapter(List<ListVideoItem> items) {
        this.items = items;
    }

    @Override
    public SimpleItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.row_item_list_view_layout, viewGroup, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleItemViewHolder viewHolder, int position) {
        viewHolder.numVideos.setText("" + items.get(position).getListVideoNum());
        viewHolder.videoTitle.setText(items.get(position).getVideoTitle());
        viewHolder.duration.setText(items.get(position).getDuration());
        viewHolder.bgColorNum.setBackgroundColor(items.get(position).getBgNumColor());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

}