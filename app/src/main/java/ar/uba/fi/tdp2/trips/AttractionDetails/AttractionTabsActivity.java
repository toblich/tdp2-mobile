package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;

import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.User;

public class AttractionTabsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, AttractionTabsInterface {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    private TabLayout tabLayout;
    protected int attractionId; // Accessed by fragments
    public CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_attraction_tabs);

        Bundle bundle = getIntent().getExtras();
        String attractionName = bundle.getString("attractionName");
        this.setTitle(attractionName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // TODO enable for back-button

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.information)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.gallery)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.points_of_interest)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Creating our pager adapter
        attractionId = bundle.getInt("attractionId");
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount(), attractionId);

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void openAttractionShareDialog() {
        //AttractionShareActivity shareFragment = new AttractionShareActivity();
        //Bundle bundle = new Bundle();
        //bundle.putCharSequence("attractionName", getTitle());
        //shareFragment.setArguments(bundle);
        //shareFragment.show(getFragmentManager(), "tag");
///////
        Intent intent = new Intent(this, AttractionShareActivity.class);
        intent.putExtra("attractionName", getTitle());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);

        final Context activityContext = this;

        MenuItem item = menu.findItem(R.id.attraction_share);
        item.getIcon().setColorFilter(getResources().getColor(R.color.toolbarContent), PorterDuff.Mode.SRC_IN);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openAttractionShareDialog();
                /*
                User user = User.getInstance(getSharedPreferences("user", 0));
                if (user != null && user.fbPost && user.fbPublicProfile) {
                    openAttractionShareDialog();
                    return false;
                }
                if (user != null && !user.fbPost) {
                    user.getFbPostPermissions((Activity) activityContext, callbackManager,
                            getSharedPreferences("user", 0),
                            new User.Callback() {
                                @Override
                                public void onSuccess(User user) {
                                    openAttractionShareDialog();
                                }
                                @Override
                                public void onError(User user) {}
                            });
                } else {
                    User.loginWithSocialNetwork((Activity) activityContext,
                            callbackManager,
                            getSharedPreferences("user", 0),
                            new User.Callback() {
                                @Override
                                public void onSuccess(User user) {
                                        Toast.makeText(activityContext, R.string.wait_a_second, Toast.LENGTH_LONG).show();
                                    user.getFbPostPermissions((Activity) activityContext, callbackManager,
                                            getSharedPreferences("user", 0),
                                            new User.Callback() {
                                                @Override
                                                public void onSuccess(User user) {
                                                    openAttractionShareDialog();
                                                }
                                                @Override
                                                public void onError(User user) {}
                                            });
                                }
                                @Override
                                public void onError(User user) {}
                            });
                }
                */


                return false;
            }
        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
