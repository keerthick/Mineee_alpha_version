package com.mineee.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mineee.controller.FeedListAppController;
import com.mineee.main.R;
import com.mineee.modal.UserListData;
import com.mineee.util.CircularNetworkImageView;

import java.util.List;

/**
 * Created by keerthick on 4/30/2015.
 */
public class CustomUserListAdapter extends BaseAdapter {

    private static final String TAG = CustomUserListAdapter.class.getSimpleName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<UserListData> feedItems;
    ImageLoader imageLoader = FeedListAppController.getInstance().getImageLoader();

    public CustomUserListAdapter(Activity activity, List<UserListData> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;

    }


    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return feedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserListFeedViewHolder holder;

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_list_items, null);

            holder = new UserListFeedViewHolder();

            holder.profilePic = (CircularNetworkImageView) convertView.findViewById(R.id.prof_pic_view);
            holder.profName = (TextView) convertView.findViewById(R.id.user_name);

            convertView.setTag(holder);
        }
        else{
            holder = (UserListFeedViewHolder)convertView.getTag();
        }

        if (imageLoader == null)
            imageLoader = FeedListAppController.getInstance().getImageLoader();



        UserListData item = feedItems.get(position);

        holder.profilePic.setImageUrl("http://mineee.com/img/profilePic/"+item.getProfPicName(), imageLoader);
        holder.profName.setText(item.getName());

        return convertView;
    }

    private static class UserListFeedViewHolder {
        CircularNetworkImageView profilePic;
        TextView profName;
    }
}
