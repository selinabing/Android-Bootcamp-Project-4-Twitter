package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DetailedTweetActivity extends AppCompatActivity {

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.tvBody)
    LinkifiedTextView tvBody;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
    @BindView(R.id.tvNumLikes) TextView tvNumLikes;
    @BindView(R.id.tvNumTweets) TextView tvNumTweets;
    @BindView(R.id.ivHeartIcon) ImageView ivHeartIcon;
    @BindView(R.id.ivRetweetIcon) ImageView ivRetweetIcon;
    @BindView(R.id.ivReplyIcon) ImageView ivReplyIcon;
    @BindView(R.id.ivMediaImg) ImageView ivMediaImg;


    TwitterClient client = TwitterApplication.getRestClient();
    private final int REQUEST_CODE_COMPOSE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_tweet);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Tweet");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_twitter_bird);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#006EEE")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        final Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        populate(tweet);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void populate(final Tweet tweet) {
        tvNickname.setTextColor(Color.BLACK);
        tvNickname.setText(tweet.getUser().getName());
        tvUsername.setText("@"+tweet.getUser().getScreenName());
        tvBody.setTextColor(Color.BLACK);
        tvBody.setText(tweet.getBody());
        tvRelativeTime.setText(tweet.getRelativeTimestamp());
        ivProfileImage.setImageResource(0);
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).transform(new RoundedCornersTransformation(5,5)).into(ivProfileImage);
        if(tweet.getFavoriteCount()>0) {
            tvNumLikes.setText("" + tweet.getFavoriteCount());
        } else {
            tvNumLikes.setText("");
        }
        if(tweet.getRetweetCount()>0) {
            tvNumTweets.setText("" + tweet.getRetweetCount());
        } else {
            tvNumTweets.setText("");
        }
        if(tweet.isFavorited()) {
            tvNumLikes.setTextColor(Color.RED);
            Picasso.with(this).load(R.drawable.ic_heart_red).into(ivHeartIcon);
        } else {
            Picasso.with(this).load(R.drawable.ic_heart_gray).into(ivHeartIcon);
        }
        if(tweet.isRetweeted()) {
            tvNumTweets.setTextColor(Color.parseColor("#2BBA30"));
            Picasso.with(this).load(R.drawable.ic_retweet_green).into(ivRetweetIcon);
        } else {
            Picasso.with(this).load(R.drawable.ic_retweet).into(ivRetweetIcon);
        }

        ivMediaImg.setImageResource(0);
        String picUrl = tweet.getMediaImgUrl();
        if (picUrl != null) {
            Picasso.with(this).load(picUrl).into(ivMediaImg);
        }

        final User user = tweet.getUser();

        ivHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int prevNumLikes;
                String strLikes = tvNumLikes.getText().toString();
                if (strLikes != "") {
                    prevNumLikes = Integer.valueOf(strLikes);
                } else {
                    prevNumLikes = 0;
                }
                if(tweet.isFavorited()){
                    client.postFavoriteDestroy(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Picasso.with(getApplicationContext()).load(R.drawable.ic_heart_gray).into(ivHeartIcon);
                            if(prevNumLikes <= 1) {
                                tvNumLikes.setText("");
                            } else {
                                int currNumLikes = prevNumLikes - 1;
                                tvNumLikes.setText(""+currNumLikes);
                            }
                            tweet.setFavorited();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG","UNLIKEsadness:"+errorResponse.toString());
                        }
                    });
                } else {
                    client.postFavoriteCreate(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Picasso.with(getApplicationContext()).load(R.drawable.ic_heart_red).into(ivHeartIcon);
                            int currNumLikes = prevNumLikes + 1;
                            tvNumLikes.setText(""+currNumLikes);
                            tweet.setFavorited();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG","LIKEsadness:"+errorResponse.toString());
                        }
                    });
                }
            }
        });

        ivRetweetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int prevNumTweets;
                String strTweets = tvNumTweets.getText().toString();
                if (strTweets != "") {
                    prevNumTweets = Integer.valueOf(strTweets);
                } else {
                    prevNumTweets = 0;
                }
                if(tweet.isRetweeted()){
                    client.postUnRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if(prevNumTweets <= 1) {
                                tvNumTweets.setText("");
                            } else {
                                int currNumTweets = prevNumTweets - 1;
                                tvNumTweets.setText(""+currNumTweets);
                            }
                            tweet.setRetweeted();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG","UNRETWEETsadness:"+errorResponse.toString());
                        }
                    });
                } else {
                    client.postRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            int currNumTweets = prevNumTweets + 1;
                            tvNumTweets.setText(""+currNumTweets);
                            tweet.setRetweeted();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG","RETWEETsadness:"+errorResponse.toString());
                        }
                    });
                }
            }
        });

        ivReplyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ComposeActivity.class);
                i.putExtra("username","@"+tweet.getUser().getScreenName()+" ");
                i.putExtra("status_id",tweet.getId());
                startActivityForResult(i, REQUEST_CODE_COMPOSE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
        }
    }

}
