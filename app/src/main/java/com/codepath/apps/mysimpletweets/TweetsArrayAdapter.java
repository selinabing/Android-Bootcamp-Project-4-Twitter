package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by selinabing on 6/27/16.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    TwitterClient client = TwitterApplication.getRestClient();

    public static class ViewHolder {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvNickname) TextView tvNickname;
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

        public ViewHolder(View itemView) {
            ButterKnife.bind(this,itemView);
        }
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get tweet
        final Tweet tweet = getItem(position);
        final ViewHolder viewHolder;
        // find or inflate template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNickname.setTextColor(Color.BLACK);
        viewHolder.tvNickname.setText(tweet.getUser().getName());
        viewHolder.tvUsername.setText("@"+tweet.getUser().getScreenName());
        viewHolder.tvBody.setTextColor(Color.BLACK);
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvRelativeTime.setText(tweet.getRelativeTimestamp());
        viewHolder.ivProfileImage.setImageResource(0);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).transform(new RoundedCornersTransformation(5,5)).into(viewHolder.ivProfileImage);
        if(tweet.getFavoriteCount()>0) {
            viewHolder.tvNumLikes.setText("" + tweet.getFavoriteCount());
        } else {
            viewHolder.tvNumLikes.setText("");
        }
        if(tweet.getRetweetCount()>0) {
            viewHolder.tvNumTweets.setText("" + tweet.getRetweetCount());
        } else {
            viewHolder.tvNumTweets.setText("");
        }
        if(tweet.isFavorited()) {
            viewHolder.tvNumLikes.setTextColor(Color.RED);
            Picasso.with(getContext()).load(R.drawable.ic_heart_red).into(viewHolder.ivHeartIcon);
        } else {
            viewHolder.tvNumLikes.setTextColor(Color.parseColor("#66757f"));
            Picasso.with(getContext()).load(R.drawable.ic_heart_gray).into(viewHolder.ivHeartIcon);
        }
        if(tweet.isRetweeted()) {
            viewHolder.tvNumTweets.setTextColor(Color.parseColor("#2BBA30"));
            Picasso.with(getContext()).load(R.drawable.ic_retweet_green).into(viewHolder.ivRetweetIcon);
        } else {
            viewHolder.tvNumTweets.setTextColor(Color.parseColor("#66757f"));
            Picasso.with(getContext()).load(R.drawable.ic_retweet).into(viewHolder.ivRetweetIcon);
        }
        viewHolder.ivMediaImg.setImageResource(0);
        String picUrl = tweet.getMediaImgUrl();
        if (picUrl != null) {
            Picasso.with(getContext()).load(picUrl).into(viewHolder.ivMediaImg);
        }

        final User user = tweet.getUser();

        viewHolder.ivProfileImage.setTag(tweet.getUser().getScreenName());
        final View finalConvertView = convertView;
        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(finalConvertView.getContext(),ProfileActivity.class);
                i.putExtra("screen_name",viewHolder.ivProfileImage.getTag().toString());
                i.putExtra("user", Parcels.wrap(user));
                finalConvertView.getContext().startActivity(i);
            }
        });

        viewHolder.ivHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int prevNumLikes;
                String strLikes = viewHolder.tvNumLikes.getText().toString();
                if (strLikes != "") {
                    prevNumLikes = Integer.valueOf(strLikes);
                } else {
                    prevNumLikes = 0;
                }
                if(tweet.isFavorited()){
                    client.postFavoriteDestroy(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Picasso.with(getContext()).load(R.drawable.ic_heart_gray).into(viewHolder.ivHeartIcon);
                            if(prevNumLikes <= 1) {
                                viewHolder.tvNumLikes.setText("");
                            } else {
                                int currNumLikes = prevNumLikes - 1;
                                viewHolder.tvNumLikes.setText(""+currNumLikes);
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
                            Picasso.with(getContext()).load(R.drawable.ic_heart_red).into(viewHolder.ivHeartIcon);
                            int currNumLikes = prevNumLikes + 1;
                            viewHolder.tvNumLikes.setText(""+currNumLikes);
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

        viewHolder.ivRetweetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int prevNumTweets;
                String strTweets = viewHolder.tvNumTweets.getText().toString();
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
                                viewHolder.tvNumTweets.setText("");
                            } else {
                                int currNumTweets = prevNumTweets - 1;
                                viewHolder.tvNumTweets.setText(""+currNumTweets);
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
                            viewHolder.tvNumTweets.setText(""+currNumTweets);
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

        viewHolder.ivReplyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ComposeActivity.class);
                i.putExtra("username","@"+tweet.getUser().getScreenName()+" ");
                i.putExtra("status_id",tweet.getId());
                getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
