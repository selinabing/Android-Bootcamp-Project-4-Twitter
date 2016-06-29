package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.ivCancel)
    ImageView ivCancel;
    @BindView(R.id.ivProfileImageCompose)
    ImageView ivProfileImageCompose;
    @BindView(R.id.etComposeTweetBody)
    EditText etComposeTweetBody;
    @BindView(R.id.btnTweet)
    Button btnTweet;
    @BindView(R.id.tvCharacterCount)
    TextView tvCharacterCount;
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        client = TwitterApplication.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG","compose activity on success");
                user = User.fromJSON(response);
                Picasso.with(getApplicationContext()).load(user.getProfileImageUrl()).into(ivProfileImageCompose);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG","compose activity on failure");
            }
        });

        etComposeTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charRemain = 140 - etComposeTweetBody.getText().toString().length();
                if (charRemain < 0) {
                    tvCharacterCount.setTextColor(Color.RED);
                    tvCharacterCount.setText(charRemain+"");
                    btnTweet.setBackgroundColor(Color.parseColor("#ccd6dd"));
                    btnTweet.setClickable(false);
                    Log.d("DEBUG","on text change failure");
                } else {
                    btnTweet.setClickable(true);
                    tvCharacterCount.setTextColor(Color.BLACK);
                    tvCharacterCount.setText(charRemain+"");
                    btnTweet.setBackgroundColor(Color.parseColor("#55acee"));
                    Log.d("DEBUG","on text change success");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });


    }


    public void onTweet(View view) {
        client.postTweet(etComposeTweetBody.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = Tweet.fromJSON(response);
                Intent data = new Intent();
                data.putExtra("tweet",tweet);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }


    public void onCancel(View view) {
        finish();
    }
}

