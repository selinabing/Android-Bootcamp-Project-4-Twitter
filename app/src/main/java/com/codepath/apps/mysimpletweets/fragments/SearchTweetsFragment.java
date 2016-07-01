package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by selinabing on 6/30/16.
 */
public class SearchTweetsFragment extends TweetsListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeLine();
    }

    public static SearchTweetsFragment newInstance(String query) {
        SearchTweetsFragment searchTweetsFragment = new SearchTweetsFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        searchTweetsFragment.setArguments(args);
        return searchTweetsFragment;
    }

    // send API request to get timeline json
    // fill listview by creating tweet object from json
    private void populateTimeLine() {
        String query = getArguments().getString("query");
        Log.d("DEBUG","query is [[["+query);
        client.getSearch(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", "search -> "+json.toString());
                clear();
                try {
                    addAll(Tweet.fromJSONArray(json.getJSONArray("statuses")));
                } catch(JSONException e) {
                    Log.d("DEBUG","lifesad");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

}
