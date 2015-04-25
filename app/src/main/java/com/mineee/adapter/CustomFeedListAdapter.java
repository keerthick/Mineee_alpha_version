package com.mineee.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mineee.controller.FeedListAppController;
import com.mineee.main.R;
import com.mineee.modal.FeedRowData;
import com.mineee.util.FeedImageView;

import java.util.List;

/**
 * Created by keerthick on 4/11/2015.
 */
public class CustomFeedListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedRowData> feedItems;
    ImageLoader imageLoader = FeedListAppController.getInstance().getImageLoader();

    public CustomFeedListAdapter(Activity activity, List<FeedRowData> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = FeedListAppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);

        TextView prodName = (TextView) convertView.findViewById(R.id.prodName);

        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);

        TextView likeCt = (TextView)convertView.findViewById(R.id.likesCount);

        TextView option = (TextView)convertView.findViewById(R.id.option);

        FeedRowData item = feedItems.get(position);

        name.setText(item.getName());
        prodName.setText(item.getProductName());
        option.setText(item.getOptionString());
        // Converting timestamp into x ago format

        /*CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);*/
        //DateUtils.
        likeCt.setText(item.getLikeCount()+"+ likes");
        timestamp.setText(item.getTimeStamp());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getDescription())) {
            statusMsg.setText(item.getDescription());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        /*if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }
*/
        // user profile pic
        profilePic.setImageUrl("http://mineee.com/img/profilePic/"+item.getProfilePhoto(), imageLoader);

        // Feed image
        if (item.getProdImageName() != null) {
            feedImageView.setImageUrl(item.getImage_path() + "320x320/" + item.getProdImageName(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        return convertView;
    }

}
