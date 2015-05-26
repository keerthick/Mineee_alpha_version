package com.mineee.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.NetworkImageView;
import com.mineee.controller.FeedListAppController;
import com.mineee.fragment.Dialog.UserListDialog;
import com.mineee.main.R;
import com.mineee.main.UserProfileThingView;
import com.mineee.modal.UserProfileData;
import com.mineee.util.JsonPArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static final String LOGGED_USER_ID = "loggedUserId";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private String loggedUserId;
    private UserProfileData item = null;
    private View profView;
    private Button had;
    private Button owns;
    private Button wants;
    private Button recommends;
    private Intent send;

    private String URL_FEED="http://mineee.com/api/index.php?rquest=getUserProfileDetails&user_id=311&loggedinUserid=311&device=mineee";
    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(int sectionNumber, String loggedUserId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(LOGGED_USER_ID, loggedUserId);
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
            loggedUserId = getArguments().getString(LOGGED_USER_ID);
            URL_FEED = "http://mineee.com/api/index.php?rquest=getUserProfileDetails&user_id="+loggedUserId+"&loggedinUserid="+loggedUserId+"&device=mineee";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profView = inflater.inflate(R.layout.fragment_profile, container, false);

        JsonPArrayRequest jsonReq = new JsonPArrayRequest(URL_FEED, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    //hidePDialog();
                    parseJsonFeed(response);
                    updateUI();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,URL_FEED);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                //hidePDialog();
            }
        });

        FeedListAppController.getInstance().addToRequestQueue(jsonReq);

        had = (Button)profView.findViewById(R.id.buttonHad);
        owns = (Button)profView.findViewById(R.id.buttonOwn);
        wants = (Button)profView.findViewById(R.id.buttonWants);
        recommends = (Button)profView.findViewById(R.id.buttonRecommends);

        had.setOnClickListener(this);
        owns.setOnClickListener(this);
        wants.setOnClickListener(this);
        recommends.setOnClickListener(this);

        send = new Intent(getActivity(), UserProfileThingView.class);

        send.putExtra("userId","311");



        return profView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.follower:{
             UserListDialog dialog = new UserListDialog();
                Bundle args = new Bundle();
                args.putString(LOGGED_USER_ID, loggedUserId);
                String FEED_URL = "http://mineee.com/api/index.php?rquest=getFollowers&user_id="+loggedUserId+"&user_name=&device=mineee";
                args.putString("FEED_URL", FEED_URL);
                args.putString("TITILE", "Followers");
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(),"FOLLOWER" );
            }

                break;
            case R.id.following: {
                UserListDialog dialog = new UserListDialog();
                Bundle args = new Bundle();
                args.putString(LOGGED_USER_ID, loggedUserId);
                String FEED_URL = "http://mineee.com/api/index.php?rquest=getFollowing&user_id=" + loggedUserId + "&user_name=&device=mineee";
                args.putString("FEED_URL", FEED_URL);
                args.putString("TITILE", "Following");
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(), "FOLLOWING");
            }
                break;
            case R.id.buttonHad:
                send.putExtra("option","4");
                send.putExtra("title","Things I Had");
                startActivity(send);
                break;
            case R.id.buttonOwn:
                send.putExtra("option","1");
                send.putExtra("title","Things I Owns");
                startActivity(send);
                break;
            case R.id.buttonWants:
                send.putExtra("option","3");
                send.putExtra("title","Things I Wants");
                startActivity(send);
                break;
            case R.id.buttonRecommends:
                send.putExtra("option","5");
                send.putExtra("title","Things I Recommends");
                startActivity(send);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONArray response) {
        try {


            JSONObject feedObj = response.getJSONObject(0);

            item = new UserProfileData();

            item.setName(feedObj.getString("name"));
            item.setProfileImageName(feedObj.getString("image_name"));
            item.setFollowerCount(feedObj.getString("followerCount"));
            item.setFollowingCount(feedObj.getString("followingCount"));
            item.setIsFollowing(feedObj.getString("isFollowing"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(){
        if(item != null){
            NetworkImageView proPicImage = (NetworkImageView)profView.findViewById(R.id.profPic);
            TextView profName = (TextView)profView.findViewById(R.id.profName);
            Button following = (Button)profView.findViewById(R.id.following);
            Button follower = (Button)profView.findViewById(R.id.follower);

            follower.setOnClickListener(this);
            following.setOnClickListener(this);

            proPicImage.setImageUrl("http://mineee.com/img/profilePic/"+item.getProfileImageName(), FeedListAppController.getInstance().getImageLoader());
            profName.setText(item.getName());
            following.setText(item.getFollowingCount() + "\n following" );
            follower.setText(item.getFollowerCount() + "\n followers" );

        }
    }
}
