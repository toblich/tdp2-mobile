package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

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
import android.widget.Toast;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.SessionActivity.RequestCode;
import ar.uba.fi.tdp2.trips.Common.OnFragmentInteractionListener;
import ar.uba.fi.tdp2.trips.R;

public class AttractionTabsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, OnFragmentInteractionListener {

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
    public int attractionId; // Accessed by fragments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attraction_tabs);

        Bundle bundle = getIntent().getExtras();
        String attractionName = bundle.getString("attractionName");
        this.setTitle(attractionName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        AttractionDetailsPager adapter = new AttractionDetailsPager(getSupportFragmentManager(), tabLayout.getTabCount(), attractionId);

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

    private void loginForSharing() {
        Intent intent = new Intent(this, SessionActivity.class);
        intent.putExtra("attractionName", getTitle());
        startActivityForResult(intent, RequestCode.SHARE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);

        MenuItem item = menu.findItem(R.id.attraction_share);
        item.getIcon().setColorFilter(getResources().getColor(R.color.toolbarContent), PorterDuff.Mode.SRC_IN);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                loginForSharing();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.SHARE:
                Toast.makeText(this, "RequestCode SHARE", Toast.LENGTH_LONG).show();
                // TODO open share fragment
                break;
            case RequestCode.REVIEW:
                Toast.makeText(this, "RequestCode REVIEW", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "RequestCode WRONG (activity): " + requestCode, Toast.LENGTH_LONG).show();
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
