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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mineee.controller.FeedListAppController;
import com.mineee.fragment.Dialog.UserListDialog;
import com.mineee.main.R;
import com.mineee.modal.FeedLikeUnlikeModel;
import com.mineee.modal.FeedRowData;
import com.mineee.modal.SessionPreferencesManager;
import com.mineee.util.FeedImageView;
import com.mineee.util.JsonPArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by keerthick on 4/11/2015.
 */
public class CustomFeedListAdapter extends BaseAdapter {

    private static final String TAG = CustomFeedListAdapter.class.getSimpleName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedRowData> feedItems;
    private List<FeedLikeUnlikeModel> feedActionItems;
    private FragmentManager supportFragmentManager;
    ImageLoader imageLoader = FeedListAppController.getInstance().getImageLoader();

    private String URL_FEED = "http://mineee.com/api/index.php?rquest=unlike&up_id=<up_id>&user_id=@@@&device=mineee";
    public CustomFeedListAdapter(Activity activity, FragmentManager supportFragmentManager, List<FeedRowData> feedItems, List<FeedLikeUnlikeModel> feedActionItems) {
        this.activity = activity;
        this.feedItems = feedItems;
        this.feedActionItems = feedActionItems;
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

        FeedRowData item = feedItems.get(position);

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

            //holder.likeBt.setTag(item);
            holder.likeBt.setTag(feedActionItems.get(position));
            convertView.setTag(holder);
        }
        else{
            holder = (FeedViewHolder)convertView.getTag();
            //holder.likeBt.setTag(item);
        }

        if (imageLoader == null)
            imageLoader = FeedListAppController.getInstance().getImageLoader();





        holder.name.setText(item.getName());
        holder.prodName.setText(item.getProductName());
        holder.option.setText(item.getOptionString());
        // Converting timestamp into x ago format

        /*CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);*/
        //DateUtils.

        if(item.isUserLiked())
        //if(((FeedRowData) holder.likeBt.getTag()).isUserLiked())
            holder.likeBt.setText("UnLike");
        else
            holder.likeBt.setText("Like");
        holder.likeCt.setText(item.getLikeCount()+"+ likes");
        //holder.likeCt.setText(((FeedRowData) holder.likeBt.getTag()).getLikeCount()+"+ likes");
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

                //FeedViewHolder holder = (FeedViewHolder)v.getTag();
                if(holder instanceof FeedViewHolder){
                    Log.d(TAG,"FeedViewHolder");
                }
                if(holder.likeBt instanceof Button){
                    Log.d(TAG,"Button");
                }
                if(holder.likeBt.getTag() instanceof FeedViewHolder)
                    Log.d(TAG,"getTag:--->FeedViewHolder");

                Log.d(TAG,(holder.likeBt.getTag()).toString());
                //FeedRowData data = (FeedRowData) (holder.likeBt.getTag());
                FeedLikeUnlikeModel data = (FeedLikeUnlikeModel) (holder.likeBt.getTag());
                Log.d(TAG, "postion:"+position);
                Log.d(TAG, "UP_id: on click listener" + data.getUpId());
                String loggedUserId = null;
                if(SessionPreferencesManager.contains(activity.getApplicationContext(), "LOGGED_USER_ID")) {
                    loggedUserId = SessionPreferencesManager.getLoggedUserID(activity.getApplicationContext());

                }
                Log.d(TAG,loggedUserId);
                if(data.isUserLiked()) {
                    holder.likeBt.setText("Like");
                    String count = data.getLikeCount();
                    data.setLikeCount((Integer.parseInt(count) - 1) + "");
                    holder.likeCt.setText(data.getLikeCount() + "+ likes");
                    data.setUserLiked("0");
                    Log.d(TAG, "UP_id:" + data.getUpId());

                    URL_FEED = "http://mineee.com/api/index.php?rquest=unlike&up_id="+data.getUpId()+"&user_id="+loggedUserId+"&device=mineee";
                    //feedItems.add(position, data);


                    fetchJson(URL_FEED, count, "UNLIKE");
                }
                else{
                    holder.likeBt.setText("Unlike");
                    String count = data.getLikeCount();
                    data.setLikeCount((Integer.parseInt(count) + 1) + "");
                    holder.likeCt.setText(data.getLikeCount() + "+ likes");
                    data.setUserLiked("1");
                    //feedItems.add(position, data);

                    Log.d(TAG, "UP_id:" + data.getUpId());

                    URL_FEED = "http://mineee.com/api/index.php?rquest=like&up_id="+data.getUpId()+"&user_id="+loggedUserId+"&device=mineee";
                    fetchJson(URL_FEED, count, "LIKE");
                }

                Log.d(TAG,feedItems.toString());
            }
        });

        //holder.likeBt.setTag(holder);

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

    private void fetchJson(final String URL_FEED, final String prevCt, final String action){

        // making fresh volley request and getting json
        JsonPArrayRequest jsonReq = new JsonPArrayRequest(URL_FEED, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response, prevCt, action);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.getStackTrace();
                Log.d(TAG, URL_FEED);
                Toast.makeText(activity, "Unable to set your like/Unlike -error in request call", Toast.LENGTH_SHORT)
                        .show();
                //hidePDialog();
            }
        });

        // Adding request to volley request queue
        FeedListAppController.getInstance().addToRequestQueue(jsonReq);

    }
    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONArray response, String prevCt, String action) {
        try {
            JSONArray feedArray = response;

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                String newCnt = feedObj.getString("cnt");
                int newCntInt = Integer.parseInt(newCnt.trim());
                int prevCntInt = Integer.parseInt(prevCt.trim());

                Log.d(TAG,"newCount"+newCnt+"----prevcount"+prevCt);
                if("LIKE".equals(action)){
                    if(newCntInt > prevCntInt){

                    }
                    else{
                        Toast.makeText(activity, "Unable to set your like action", Toast.LENGTH_SHORT)
                                .show();
                    }
                }else if("UNLIKE".equals(action)){
                    if(newCntInt < prevCntInt){

                    }
                    else{
                        Toast.makeText(activity, "Unable to set your unlike action", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
