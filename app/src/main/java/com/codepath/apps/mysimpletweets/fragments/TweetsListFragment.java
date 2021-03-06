package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.DetailedTweetActivity;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by selinabing on 6/28/16.
 */
public class TweetsListFragment extends Fragment {
    // inflation logic
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapterTweets;
    @BindView(R.id.lvTweets)
    ListView lvTweets;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this,view);
        lvTweets.setAdapter(adapterTweets);

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), DetailedTweetActivity.class);
                Tweet tweet = (Tweet) lvTweets.getItemAtPosition(position);
                i.putExtra("tweet", Parcels.wrap(tweet));
                getActivity().startActivity(i);
            }
        });

        lvTweets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = (Tweet) lvTweets.getItemAtPosition(position);
                adapterTweets.remove(tweet);
                return true;
            }
        });

        return view;
    }

    // creation lifecycle

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        adapterTweets = new TweetsArrayAdapter(getActivity(), tweets);

    }

    public void addAll(List<Tweet> tweets) {
        adapterTweets.addAll(tweets);
    }

    public void clear() { adapterTweets.clear(); }

    public void add (int index, Tweet tweet) {
        adapterTweets.insert(tweet,index);
    }

    public void scrollToTop () { lvTweets.setSelection(0); }

}
