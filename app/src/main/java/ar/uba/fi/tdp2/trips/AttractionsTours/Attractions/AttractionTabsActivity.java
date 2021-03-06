package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.SessionActivity.RequestCode;
import ar.uba.fi.tdp2.trips.Common.OnFragmentInteractionListener;
import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.R;

public class AttractionTabsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, OnFragmentInteractionListener {

    private ViewPager viewPager;

    private TabLayout tabLayout;
    public int attractionId;
    private String attractionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attraction_tabs);

        Bundle bundle = getIntent().getExtras();
        attractionName = bundle.getString("attractionName");
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
        if (requestCode != RequestCode.SHARE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        User user = User.getInstance(getSharedPreferences("user", 0));
        if (user == null) {
            return;
        }
        String initialText = String.format("%s %s!", getString(R.string.attraction_post_message), attractionName);

        ShareAttractionFragment shareAttractionFragment = ShareAttractionFragment.newInstance(initialText);

        getSupportFragmentManager().beginTransaction()
                .add(shareAttractionFragment, "shareAttractionDialog")
                .commitAllowingStateLoss();
    }
}
