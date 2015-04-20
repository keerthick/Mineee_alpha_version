package com.mineee.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.mineee.main.R;
import com.mineee.modal.UserProfileData;
import com.mineee.util.JsonPObjectRequest;

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
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = ProfileFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private UserProfileData item = null;

    private String URL_FEED="http://mineee.com/api/index.php?rquest=getUserProfileDetails&user_id=311&loggedinUserid=311&device=mineee";
    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profView = inflater.inflate(R.layout.fragment_profile, container, false);

        JsonPObjectRequest jsonReq = new JsonPObjectRequest(URL_FEED, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    //hidePDialog();
                    parseJsonFeed(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //hidePDialog();
            }
        });

        if(item != null){
            NetworkImageView proPicImage = (NetworkImageView)profView.findViewById(R.id.profPic);
            TextView profName = (TextView)profView.findViewById(R.id.profName);
            Button following = (Button)profView.findViewById(R.id.following);
            Button follower = (Button)profView.findViewById(R.id.follower);

            proPicImage.setImageUrl("http://mineee.com/img/profilePic/"+item.getProfileImageName(), FeedListAppController.getInstance().getImageLoader());
            profName.setText(item.getName());
            following.setText(item.getFollowingCount() + " following" );
            follower.setText(item.getFollowerCount() + " followers" );

        }

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
    private void parseJsonFeed(JSONObject response) {
        try {


            JSONObject feedObj = response;

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
}
