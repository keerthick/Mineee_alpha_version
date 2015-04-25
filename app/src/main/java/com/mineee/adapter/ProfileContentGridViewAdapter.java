package com.mineee.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mineee.controller.FeedListAppController;
import com.mineee.main.R;
import com.mineee.modal.FeedRowData;
import com.mineee.util.FeedImageView;

import java.util.List;

/**
 * Created by keerthick on 4/25/2015.
 */
public class ProfileContentGridViewAdapter extends BaseAdapter {

    Activity activity;
    int resource;
    List<FeedRowData> gridFeedList;
    ImageLoader imageLoader = FeedListAppController.getInstance().getImageLoader();

    public ProfileContentGridViewAdapter(Activity activity, int resource, List<FeedRowData> objects) {
        //super(activity, resource, objects);

        this.activity = activity;
        this.resource = resource;
        this.gridFeedList = objects;
    }

    @Override
    public int getCount() {
        return gridFeedList.size();
    }

    @Override
    public Object getItem(int position) {
        return gridFeedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final GridFeedHolder holder;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new GridFeedHolder();
            //holder.image = (DynamicHeightImageView)row.findViewById(R.id.image);
            holder.image = (FeedImageView)row.findViewById(R.id.image);
            holder.title = (TextView)row.findViewById(R.id.title);
            holder.description = (TextView)row.findViewById(R.id.description);

            row.setTag(holder);
        }
        else {
            holder = (GridFeedHolder) row.getTag();
        }

        final FeedRowData data = gridFeedList.get(position);

        if (data.getProdImageName() != null) {
            holder.image.setImageUrl(data.getImage_path() +"320x320/" + data.getProdImageName(), imageLoader);
            holder.image.setVisibility(View.VISIBLE);
            holder.image
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            holder.image.setVisibility(View.GONE);
        }

        holder.image.setHeightRatio(1.0);
        holder.title.setText(data.getProductName());
        if(data.getDescription() != null && !"".equals(data.getDescription()))
            holder.description.setText(data.getDescription());
        else
            holder.description.setText("");

        return row;
    }



    static class GridFeedHolder {
        //DynamicHeightImageView image;
        FeedImageView image;
        TextView title;
        TextView description;
    }
}
