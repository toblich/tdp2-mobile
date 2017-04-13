package ar.uba.fi.tdp2.trips.PointOfInterestDetails;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import ar.uba.fi.tdp2.trips.R;

public class PointOfInterestTabsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, PointOfInterestTabsInterface {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_interest_tabs);

        Bundle bundle = getIntent().getExtras();
        String poiName = bundle.getString("poiName");
        String poiOrder = bundle.getString("poiOrder");
        this.setTitle(poiOrder + " - " + poiName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.information)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.gallery)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Creating our pager adapter
        int attractionId = bundle.getInt("attractionId");
        int poiId = bundle.getInt("poiId");
        PointOfInterestDetailsPager adapter = new PointOfInterestDetailsPager(getSupportFragmentManager(), tabLayout.getTabCount(), attractionId, poiId);

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
}
