package com.mineee.adapter;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mineee.controller.FeedListAppController;
import com.mineee.fragment.Dialog.UserListDialog;
import com.mineee.main.R;
import com.mineee.modal.FeedRowData;
import com.mineee.util.FeedImageView;

import java.util.List;

/**
 * Created by keerthick on 4/11/2015.
 */
public class CustomFeedListAdapter extends BaseAdapter {

    private static final String TAG = CustomFeedListAdapter.class.getSimpleName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedRowData> feedItems;
    private FragmentManager supportFragmentManager;
    ImageLoader imageLoader = FeedListAppController.getInstance().getImageLoader();

    public CustomFeedListAdapter(Activity activity, FragmentManager supportFragmentManager, List<FeedRowData> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
        this.supportFragmentManager = supportFragmentManager;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final FeedViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.feed_item, null);

            holder = new FeedViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);

            holder.prodName = (TextView) convertView.findViewById(R.id.prodName);

            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            holder.statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
            holder.url = (TextView) convertView.findViewById(R.id.txtUrl);
            holder.profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
            holder.feedImageView = (FeedImageView) convertView.findViewById(R.id.feedImage1);

            holder.likeCt = (TextView)convertView.findViewById(R.id.likesCount);

            holder.option = (TextView)convertView.findViewById(R.id.option);

            holder.likeBt = (Button)convertView.findViewById(R.id.likeButton);

            convertView.setTag(holder);
        }
        else{
            holder = (FeedViewHolder)convertView.getTag();
        }

        if (imageLoader == null)
            imageLoader = FeedListAppController.getInstance().getImageLoader();



        FeedRowData item = feedItems.get(position);

        holder.name.setText(item.getName());
        holder.prodName.setText(item.getProductName());
        holder.option.setText(item.getOptionString());
        // Converting timestamp into x ago format

        /*CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);*/
        //DateUtils.
        if(item.isUserLiked())
            holder.likeBt.setText("UnLike");
        else
            holder.likeBt.setText("Like");
        holder.likeCt.setText(item.getLikeCount()+"+ likes");
        holder.timestamp.setText(item.getTimeStamp());

        // Check for empty status message
        if (!TextUtils.isEmpty(item.getDescription())) {
            holder.statusMsg.setText(item.getDescription());
            holder.statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.statusMsg.setVisibility(View.GONE);
        }

        // user profile pic
        holder.profilePic.setImageUrl("http://mineee.com/img/profilePic/"+item.getProfilePhoto(), imageLoader);

        // Feed image
        if (item.getProdImageName() != null) {
            holder.feedImageView.setImageUrl(item.getImage_path() + "320x320/" + item.getProdImageName(), imageLoader);
            holder.feedImageView.setVisibility(View.VISIBLE);
            holder.feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }

        holder.likeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedRowData data = feedItems.remove(position);
                Log.d(TAG, "postion:"+position);
                if(data.isUserLiked()) {
                    holder.likeBt.setText("Like");
                    String count = data.getLikeCount();
                    data.setLikeCount((Integer.parseInt(count) - 1) + "");
                    holder.likeCt.setText(data.getLikeCount() + "+ likes");
                    data.setUserLiked("0");
                    feedItems.add(position, data);
                }
                else{
                    holder.likeBt.setText("Unlike");
                    String count = data.getLikeCount();
                    data.setLikeCount((Integer.parseInt(count) + 1) + "");
                    holder.likeCt.setText(data.getLikeCount() + "+ likes");
                    data.setUserLiked("1");
                    feedItems.add(position, data);
                }
            }
        });

        holder.likeBt.setTag(holder);

        holder.likeCt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedRowData data = feedItems.get(position);


                {
                    UserListDialog dialog = new UserListDialog();
                    Bundle args = new Bundle();
                    //args.putString(LOGGED_USER_ID, loggedUserId);
                    String FEED_URL = "http://mineee.com/api/index.php?rquest=getWhoLiked&up_id="+data.getUpId()+"&device=mineee";
                    args.putString("FEED_URL", FEED_URL);
                    args.putString("TITILE", "who liked");
                    dialog.setArguments(args);
                    dialog.show(supportFragmentManager,"FOLLOWER" );
                }




            }
        });


        return convertView;
    }

    private static class FeedViewHolder {
        TextView name;
        TextView prodName;
        TextView timestamp;
        TextView statusMsg;
        TextView url;
        NetworkImageView profilePic;
        FeedImageView feedImageView;
        TextView likeCt;
        TextView option;
        Button likeBt;
    }
}
