package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    //private TweetsListFragment fragmentTweetsList;

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabStrip;
    SmartFragmentStatePagerAdapter fragmentPagerAdapter;
    private final int REQUEST_CODE_COMPOSE = 50;
    HomeTimelineFragment homeTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //get viewpager
        //set viewpager adapter for pager
        //find sliding tabstrip
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        homeTimelineFragment = new HomeTimelineFragment();
        fragmentPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(viewpager);
        tabStrip.setTabBackground(Color.parseColor("#006EEE"));
        tabStrip.setIndicatorColor(Color.parseColor("#006EEE"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Home");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_twitter_bird);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#006EEE")));
/*        if (savedInstanceState == null) {
            fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        } //set fragment list reference only if it hasn't been*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(getApplicationContext(),SearchActivity.class);
                i.putExtra("query",query);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem mi) {
        Intent i = new Intent(this, ProfileActivity.class);
        //i.putExtra("screen_name",)
        startActivity(i);
        //overridePendingTransition();
    }

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider{
        final int PAGE_COUNT = 2;
        private int tabIcons[] = {R.drawable.ic_slider_home, R.drawable.ic_slider_notification};
        private String tabTitles[] = {"Home","Mentions"};

        public TweetsPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return homeTimelineFragment;
            else
                return new MentionsTimelineFragment();
        }



        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

    }


    public void onCompose(View view) {
        Intent i = new Intent (TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE_COMPOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            homeTimelineFragment.appendTweet(tweet);
            homeTimelineFragment.scrollToTop();
        }
    }
}
