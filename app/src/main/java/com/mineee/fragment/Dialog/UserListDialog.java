package com.mineee.fragment.Dialog;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.mineee.adapter.CustomUserListAdapter;
import com.mineee.controller.FeedListAppController;
import com.mineee.main.R;
import com.mineee.modal.UserListData;
import com.mineee.util.JsonPArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keerthick on 4/24/2015.
 */
public class UserListDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = UserListDialog.class.getSimpleName();
    private ListView userList;
    private List<UserListData> feedList = new ArrayList<UserListData>();
    private CustomUserListAdapter listAdapter;
    private String URL_FEED;
    private String TITILE;
    private String loggedUserId;




    //String[] listitems = { "item01", "item02", "item03", "item04" };

    public UserListDialog(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.userlist_dialog, null, false);
        userList = (ListView) view.findViewById(R.id.list);

        /*listAdapter = new CustomUserListAdapter(this.getActivity(), feedList);
        userList.setAdapter(listAdapter);*/
        //userList.setOnScrollListener(this);



        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
            loggedUserId = getArguments().getString("LOGGED_USER_ID");
            URL_FEED = getArguments().getString("FEED_URL");
            TITILE = getArguments().getString("TITILE");

        }
        if (TITILE != null && !"".equals(TITILE))
            getDialog().setTitle(TITILE);
        else
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, listitems);

        userList.setAdapter(adapter);*/

        listAdapter = new CustomUserListAdapter(this.getActivity(), feedList);
        userList.setAdapter(listAdapter);
        fetchJson(URL_FEED);

        userList.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        dismiss();
        Toast.makeText(getActivity(), feedList.get(position).getName(), Toast.LENGTH_SHORT)
                .show();
    }


    private void fetchJson(final String URL_FEED){

            // making fresh volley request and getting json
            JsonPArrayRequest jsonReq = new JsonPArrayRequest(URL_FEED, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        //hidePDialog();
                        //feedList.clear();
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
    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONArray response) {
        try {
            JSONArray feedArray = response;

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                UserListData item = new UserListData();

                item.setUserId(feedObj.getString("id"));
                item.setName(feedObj.getString("name"));
                item.setProfPicName(feedObj.getString("image_name"));

                feedList.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
