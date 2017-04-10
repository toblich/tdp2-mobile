package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ar.uba.fi.tdp2.trips.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        int attractionId = bundle.getInt("attractionId");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);

        final Context activityContext = this;

        MenuItem item = menu.findItem(R.id.attraction_share);
        item.getIcon().setColorFilter(getResources().getColor(R.color.toolbarContent), PorterDuff.Mode.SRC_IN);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(activityContext, "Share attraction!", Toast.LENGTH_LONG).show();
                return false;
            }
        });

//        ShareActionProvider shareActionProvider = (ShareActionProvider) menu
//        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        MenuItem searchItem = menu.findItem(R.id.cities_search);
//        searchItem.getIcon().setColorFilter(getResources().getColor(R.color.toolbarContent), PorterDuff.Mode.SRC_IN);
//        SearchView search = (SearchView) searchItem.getActionView();
//        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextChange(String query) {
//                final List<City> filteredModelList = filter(cities, query);
//                if (adapter != null) {
//                    adapter.setFilter(filteredModelList);
//                }
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//        });
//
//        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                adapter.setFilter(cities);
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return true;
//            }
//        });

        return true;
    }

//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
