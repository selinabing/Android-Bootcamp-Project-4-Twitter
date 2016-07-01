package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by selinabing on 6/27/16.
 */
@Parcel
public class Tweet {
    public String getBody() {
        return body;
    }

    public long getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getRelativeTimestamp() {
        return relativeTimestamp;
    }

    public boolean isFavorited() { return favorited; }

    public int getRetweetCount() { return retweetCount; }

    public int getFavoriteCount() { return favoriteCount; }

    public boolean isRetweeted() { return retweeted; }

    public String getMediaImgUrl() { return mediaImgUrl; }

    public void setFavorited() {favorited = !favorited; }

    public void setRetweeted() { retweeted = !retweeted; }

    /**
     *

     */
    private String body;
    private long id;
    private User user;
    private String createdAt;
    private String relativeTimestamp;
    private boolean favorited;
    private int retweetCount;
    private int favoriteCount;
    private boolean retweeted;
    private String mediaImgUrl;



    // Deserialize JSON
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.id = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.relativeTimestamp = tweet.getRelativeTimeAgo(tweet.getCreatedAt());
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            try {
                tweet.mediaImgUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                int mediaUrlRangeLeft = (Integer)jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getJSONArray("indices").get(0);
                int mediaUrlRangeRight = (Integer)jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getJSONArray("indices").get(1);
                //tweet.body = tweet.body.substring(0,mediaUrlRangeLeft) + tweet.body.substring(mediaUrlRangeRight,tweet.body.length());
                tweet.body = tweet.body.substring(0,mediaUrlRangeLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
