package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by selinabing on 6/27/16.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public static class ViewHolder {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvNickname) TextView tvNickname;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;

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

        final User user = tweet.getUser();

        viewHolder.ivProfileImage.setTag(tweet.getUser().getScreenName());
        final View finalConvertView = convertView;
        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(finalConvertView.getContext(),ProfileActivity.class);
                i.putExtra("screen_name",viewHolder.ivProfileImage.getTag().toString());
                i.putExtra("user",user);
                finalConvertView.getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
