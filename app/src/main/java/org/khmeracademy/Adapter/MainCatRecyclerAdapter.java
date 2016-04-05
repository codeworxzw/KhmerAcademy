package org.khmeracademy.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.khmeracademy.Model.MainCategoryItem;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.R;

import java.util.List;

/**
 * Created by Tarek on 4/24/2015.
 */
public class MainCatRecyclerAdapter extends RecyclerView.Adapter<MainCatRecyclerAdapter.SimpleItemViewHolder> {

    private List<MainCategoryItem> items;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Provide access to all the views for a data item in a view holder
    public final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        ImageView bgImage;
        TextView title,description;
        View color;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            bgImage = (ImageView) itemView.findViewById(R.id.bgImageItem);
            title = (TextView) itemView.findViewById(R.id.title);
            color = itemView.findViewById(R.id.colorItem);
            description = (TextView) itemView.findViewById(R.id.main_cat_des);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainCatRecyclerAdapter(List<MainCategoryItem> items, Context context) {
        mContext = context;
        this.items = items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    // Create new items (invoked by the layout manager)
    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public SimpleItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_main_item, viewGroup, false);
        return new SimpleItemViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SimpleItemViewHolder viewHolder, int position) {
        Picasso.with(mContext)
                .load(items.get(position).getBgImage())
                .placeholder(R.drawable.thumbnail_not_yet_display)
                .error(R.drawable.thumbnail_not_yet_display).into(viewHolder.bgImage);
        viewHolder.bgImage.setTag(position);
        viewHolder.title.setText(items.get(position).getTitle());
        viewHolder.color.setBackgroundColor(items.get(position).getColor());
        viewHolder.description.setText(items.get(position).getDescription());
    }
}