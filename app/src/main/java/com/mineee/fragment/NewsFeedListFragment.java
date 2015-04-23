package com.mineee.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.mineee.adapter.CustomFeedListAdapter;
import com.mineee.controller.FeedListAppController;
import com.mineee.main.R;
import com.mineee.main.UploadThingActivity;
import com.mineee.modal.FeedRowData;
import com.mineee.util.JsonPArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by keerthick on 4/4/2015.
 */
public class NewsFeedListFragment extends Fragment {

    // Log tag
    private static final String TAG = NewsFeedListFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int RESULT_LOAD_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 101;
    private static final int ADD_NEW_ITEM_RELOAD = 102;
    // Movies json URL_FEED
    //private static final String URL_FEED = "http://api.androidhive.info/json/movies.json";
    private static final String URL_FEED = "http://mineee.com/api/index.php?rquest=getWall&user_id=311&start=1&device=mineee";
    private ProgressDialog pDialog;

    private List<FeedRowData> feedList = new ArrayList<FeedRowData>();

    private ListView listView;
    //private CustomListAdapter adapter;

    private AddFloatingActionButton fab;

    private CustomFeedListAdapter listAdapter;

    private Uri fileUri;

    public NewsFeedListFragment(){
        Log.d(TAG,"NewsFeedListFragment");
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewsFeedListFragment newInstance(int sectionNumber) {
        NewsFeedListFragment fragment = new NewsFeedListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed_feed, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        listAdapter = new CustomFeedListAdapter(this.getActivity(), feedList);
        listView.setAdapter(listAdapter);

        fab = (AddFloatingActionButton) rootView.findViewById(R.id.updateNew);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(getActivity(),"Update screen coming soon", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.add_thing_dialog_title)
                        .setPositiveButton(R.string.add_thing_dialog_camera, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                fileUri = getOutputMediaFile(); // create a file to save the image
                                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                                startActivityForResult(i, RESULT_LOAD_CAMERA);

                            }
                        })
                        .setNegativeButton(R.string.add_thing_dialog_gallery, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                            }
                        });
                builder.create();
                builder.show();

            }
        });

        pDialog = new ProgressDialog(this.getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        fetchJson(URL_FEED,true);

        return rootView;
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
                    hidePDialog();
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
                        hidePDialog();
                        feedList.clear();
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Log.d(TAG,URL_FEED);
                    hidePDialog();
                }
            });

            // Adding request to volley request queue
            FeedListAppController.getInstance().addToRequestQueue(jsonReq);
        }
    }
    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONArray response) {
        try {
            JSONArray feedArray = response;

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedRowData item = new FeedRowData();

                item.setFeedUserId(feedObj.getInt("user_id"));
                item.setProfilePhoto(feedObj.getString("profpic"));
                item.setUnique_Name(feedObj.getString("uniquename"));
                item.setProdImageName(feedObj.getString("image_name"));
                item.setProductName(feedObj.getString("pd_name"));
                item.setName(feedObj.getString("name"));
                item.setImage_path(feedObj.getString("image_folder"));
                item.setOptionString(feedObj.getString("up_option"));
                item.setLikeCount(feedObj.getString("likeCount"));
                item.setDescription(feedObj.getString("description"));
                item.setTimeStamp(feedObj.getString("dt_ct"));

                feedList.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    private static Uri getOutputMediaFile() {

        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Log.d(TAG,selectedImage.getPath());

            // Get the cursor
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);
            cursor.close();

            Intent uploadAct = new Intent(getActivity(), UploadThingActivity.class);
            uploadAct.putExtra("imgPath",imgPath);
            startActivityForResult(uploadAct, ADD_NEW_ITEM_RELOAD);
        }
        if (requestCode == RESULT_LOAD_CAMERA && resultCode == Activity.RESULT_OK ) {

            Log.d(TAG,fileUri.getPath());

            String imgPath = fileUri.getPath();

            Intent uploadAct = new Intent(getActivity(), UploadThingActivity.class);
            uploadAct.putExtra("imgPath",imgPath);
            startActivityForResult(uploadAct, ADD_NEW_ITEM_RELOAD);
        }

        if(requestCode == ADD_NEW_ITEM_RELOAD && resultCode == Activity.RESULT_OK ){
            fetchJson(URL_FEED+"&random="+Math.random(),false);
        }
    }
}
