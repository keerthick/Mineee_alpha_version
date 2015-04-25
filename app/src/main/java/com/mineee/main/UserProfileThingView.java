package com.mineee.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.etsy.android.grid.StaggeredGridView;
import com.mineee.adapter.ProfileContentGridViewAdapter;
import com.mineee.controller.FeedListAppController;
import com.mineee.modal.FeedRowData;
import com.mineee.util.JsonPArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class UserProfileThingView extends ActionBarActivity {

    private static final String TAG = UserProfileThingView.class.getSimpleName();
    private StaggeredGridView mGridView;
    private ProfileContentGridViewAdapter mAdapter;
    private List<FeedRowData> feedList = new ArrayList<FeedRowData>();

    private String URL_FEED = "http://mineee.com/api/index.php?rquest=getProfileContent&user_id=311&option=1&device=mineee";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_thing_view);

        Intent i = getIntent();
        String option = i.getStringExtra("option");
        String userId = i.getStringExtra("userId");

        URL_FEED = "http://mineee.com/api/index.php?rquest=getProfileContent&user_id="+userId+"&option="+option+"&device=mineee";

        setTitle(i.getStringExtra("title"));
        //getActionBar().set
        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        mAdapter = new ProfileContentGridViewAdapter(this, R.layout.grid_feed_item,feedList );
        mGridView.setAdapter(mAdapter);

        fetchJson(URL_FEED,true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile_thing_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseJsonFeed(JSONArray response) {
        try {
            JSONArray feedArray = response;

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedRowData item = new FeedRowData();

                item.setFeedUserId(311);
                //item.setProfilePhoto(feedObj.getString("profpic"));
                //item.setUnique_Name(feedObj.getString("uniquename"));
                item.setProdImageName(feedObj.getString("image_name"));
                item.setProductName(feedObj.getString("pd_name"));
                //item.setName(feedObj.getString("name"));
                item.setImage_path(feedObj.getString("image_folder"));
                //item.setOptionString(feedObj.getString("up_option"));
                item.setLikeCount(feedObj.getString("likes"));
                item.setDescription(feedObj.getString("description"));
                //item.setTimeStamp(feedObj.getString("dt_ct"));

                feedList.add(item);
            }

            // notify data changes to list adapater
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchJson(final String URL_FEED, boolean loadFromCache){
        // We first check for cached request
        Cache cache = FeedListAppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);

        entry = null;
        if (entry != null && loadFromCache) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    //hidePDialog();
                    data = data.replace("(","");
                    data = data.replace(")","");
                    parseJsonFeed(new JSONArray(data));

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }else {
            // making fresh volley request and getting json
            JsonPArrayRequest jsonReq = new JsonPArrayRequest(URL_FEED, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        //hidePDialog();
                        feedList.clear();
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Log.d(TAG, URL_FEED);
                    //hidePDialog();
                }
            });

            // Adding request to volley request queue
            FeedListAppController.getInstance().addToRequestQueue(jsonReq);
        }
    }

}
