package ar.uba.fi.tdp2.trips.AttractionsTours.Tours;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.Attraction;
import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.Common.OnFragmentInteractionListener;
import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.RV_AttractionAdapter;
import ar.uba.fi.tdp2.trips.Common.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourDetailsFragment extends Fragment implements OnMapReadyCallback {
    private int tourId;
    private Tour tour;
    private OnFragmentInteractionListener mListener;
    private Context localContext;
    private TourDetailsActivity activity;
    private User user;

    public TourDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(boolean b) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (TourDetailsActivity) getActivity();
        localContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tourId = activity.tourId;
        View fragment = inflater.inflate(R.layout.fragment_tour_details, container, false);
        ListView lw = (ListView) fragment.findViewById(R.id.tour_information_list);
        getTourDetails(lw);
        return fragment;
    }

    public void getTourDetails(final ListView lw) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<Tour> call;
        user = User.getInstance(getContext().getSharedPreferences("user", 0));
        if (user != null) {
            String bearer = "Bearer " + user.token;
            call = backendService.getTourWithAuth(tourId, bearer);
        } else {
            call = backendService.getTour(tourId);
        }

        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if (response.body() == null) {
                    return;
                }
                Log.d("TRIPS", "got tour: " + response.body().toString());
                tour = response.body();

                setViewContent(lw);
            }

            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }

    public void setViewContent(ListView informationList) {
        if (localContext == null) {
            return;
        }

        TourInformationListAdapter adapter = new TourInformationListAdapter(localContext, tour);
        informationList.setAdapter(adapter);

        LayoutInflater inflater = (LayoutInflater) localContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        addHeader(inflater, informationList);
        addFooter(inflater, informationList);

        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mMapFragment)
                .commit();
    }

    private void addFooter(LayoutInflater inflater, ListView informationList) {
        View footer = inflater.inflate(R.layout.tour_details_footer, informationList, false);

        /* Set description */
        TextView description = (TextView) footer.findViewById(R.id.tour_description);
        description.setText(tour.getDescription());

        /* Add attraction cards */
        RecyclerView recyclerView = (RecyclerView) footer.findViewById(R.id.tour_attractions_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(localContext));

        RV_AttractionAdapter attractionListAdapter = new RV_AttractionAdapter(tour.getAttractions(), localContext, getActivity());
        recyclerView.setAdapter(attractionListAdapter);

        informationList.addFooterView(footer);
    }


    private void addHeader(LayoutInflater inflater, ListView informationList) {
        View header = inflater.inflate(R.layout.tour_details_header, informationList, false);
        // The next line enables the map to be loaded
        informationList.addHeaderView(header);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        final List<Attraction> attractions = tour.getAttractions();

        if (attractions.isEmpty()) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Attraction attraction : attractions) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(attraction.latitude, attraction.longitude))
                    .title(attraction.name));

            builder.include(new LatLng(attraction.latitude, attraction.longitude));
        }

        final LatLngBounds bounds = builder.build();

        // Disables scroll for map
        map.getUiSettings().setAllGesturesEnabled(false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        if (height > width)
            height /= 2; // map uses about half the height when in portrait mode
        int padding = (int) (Math.max(width, height) * 0.12); // offset from edges of the map 12% of screen
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

        // Redirects to GoogleMaps
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                Attraction firstAttraction = attractions.get(0);
                int size = attractions.size();
                Attraction lastAttraction = attractions.get(size-1);

                StringBuilder builder = new StringBuilder();

                builder.append("https://maps.google.com/maps?")
                        .append("saddr=")
                        .append(getLatLngStr(firstAttraction))
                        .append("&daddr=")
                        .append(getLatLngStr(lastAttraction));

                for (int i = 1; i < size-1; i++) {
                    builder.append("+to:")
                            .append(getLatLngStr(attractions.get(i)));
                }

                Uri gmmIntentUri = Uri.parse(builder.toString());

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(localContext, R.string.google_maps_not_installed, Toast.LENGTH_LONG).show();
                }
            }

            private String getLatLngStr(Attraction attraction) {
                return attraction.latitude + "," + attraction.longitude;
            }
        });
    }
}
