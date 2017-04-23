package ar.uba.fi.tdp2.trips.TourDetails;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import ar.uba.fi.tdp2.trips.ActivityWithCallbackManager;
import ar.uba.fi.tdp2.trips.AttractionDetails.WriteReviewFragment;
import ar.uba.fi.tdp2.trips.R;

public class TourDetailsActivity extends ActivityWithCallbackManager implements TourDetailsFragment.OnFragmentInteractionListener {

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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO
    }
}
