package org.khmeracademy.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.khmeracademy.Model.SubCategoryItem;
import org.khmeracademy.R;

import java.util.List;

/**
 * Created by Longdy on 12/16/2015.
 */
public class SubCatRecyclerAdapter extends RecyclerView.Adapter<SubCatRecyclerAdapter.SimpleItemViewHolder>  {

    private List<SubCategoryItem> items;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Provide access to all the views for a data item in a view holder
    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        ImageView bgImage;
        TextView title;
        View color;
        TextView numVideos;
        TextView describe;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            bgImage = (ImageView) itemView.findViewById(org.khmeracademy.R.id.bgImageItem);
            title = (TextView) itemView.findViewById(org.khmeracademy.R.id.title);
            color = itemView.findViewById(org.khmeracademy.R.id.colorItem);
            numVideos = (TextView)itemView.findViewById(org.khmeracademy.R.id.num_videos);
            describe = (TextView) itemView.findViewById(org.khmeracademy.R.id.describe);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SubCatRecyclerAdapter(List<SubCategoryItem> items, Context context) {
        this.items = items;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    // Create new items (invoked by the layout manager)
    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public SimpleItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(org.khmeracademy.R.layout.list_sub_item, viewGroup, false);
        return new SimpleItemViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SubCatRecyclerAdapter.SimpleItemViewHolder viewHolder, int position) {

        Picasso.with(mContext)
                .load(items.get(position).getBgImage())
                .placeholder(R.drawable.thumbnail_not_yet_display)
                .error(R.drawable.thumbnail_not_yet_display).into(viewHolder.bgImage);
        viewHolder.bgImage.setTag(position);
        viewHolder.color.setBackgroundColor(items.get(position).getColor());
        viewHolder.title.setText(items.get(position).getTitle());
        viewHolder.numVideos.setText("["+items.get(position).getNumVideos()+" videos]");
        viewHolder.describe.setText(items.get(position).getDescribe());
    }

    public void clearData() {
        this.items.clear();
        this.notifyDataSetChanged();
    }

}
