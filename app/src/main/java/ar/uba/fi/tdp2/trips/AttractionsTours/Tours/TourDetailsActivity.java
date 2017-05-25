package ar.uba.fi.tdp2.trips.AttractionsTours.Tours;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ar.uba.fi.tdp2.trips.Common.ActivityWithCallbackManager;
import ar.uba.fi.tdp2.trips.Common.OnFragmentInteractionListener;
import ar.uba.fi.tdp2.trips.R;

public class TourDetailsActivity extends ActivityWithCallbackManager implements OnFragmentInteractionListener {

    int tourId; // accessed by TourDetailsFragment
    private String tourName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        tourId = bundle.getInt("tourId");
        tourName = bundle.getString("tourName");
        System.out.println("Created activity with tourId: " + String.valueOf(tourId) + " for tour: " + tourName);

        setContentView(R.layout.activity_tour_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(tourName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void onFragmentInteraction(Uri uri) {

    }
}
