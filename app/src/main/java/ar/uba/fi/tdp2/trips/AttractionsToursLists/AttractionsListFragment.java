package ar.uba.fi.tdp2.trips.AttractionsToursLists;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.RV_AttractionAdapter;
import ar.uba.fi.tdp2.trips.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AttractionsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AttractionsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttractionsListFragment extends Fragment {

    private static final String ARG_CITY_ID = "cityId";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private List<Attraction> attractions;

    private Double latitude;
    private Double longitude;

    private int cityId;
    private AttractionsListFragment.OnFragmentInteractionListener mListener;
    private Context localContext;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RV_AttractionAdapter attractionsAdapter;
    private TextView noAttractionsTextView;

    public AttractionsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cityId The id of the attraction whose details will be shown.
     * @return A new instance of fragment AttractionDetailsFragment.
     */
    public static AttractionsListFragment newInstance(Double latitude, Double longitude, int cityId) {
        AttractionsListFragment fragment = new AttractionsListFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putInt(ARG_CITY_ID, cityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityId = getArguments().getInt(ARG_CITY_ID);
            latitude = getArguments().getDouble(ARG_LATITUDE);
            longitude = getArguments().getDouble(ARG_LONGITUDE);
        }
        localContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_attractions_list, container, false);
        recyclerView = (RecyclerView) fragment.findViewById(R.id.rv);
        noAttractionsTextView = (TextView) fragment.findViewById(R.id.noAttractionsTextView);
        linearLayoutManager = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAttractionsList();
        return fragment;
    }

    public void getAttractionsList() {

        attractions = new ArrayList<>();

        if (!Utils.isNetworkAvailable()) {
            Toast.makeText(localContext, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
            Log.e(Utils.LOGTAG, getString(R.string.no_internet_error));
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<Attraction>> call  = backendService.getAttractionsRadiusAndCityID(latitude, longitude, 2.0, cityId);

        call.enqueue(new Callback<List<Attraction>>() {
            @Override
            public void onResponse(Call<List<Attraction>> call, Response<List<Attraction>> response) {
                if (response.body() == null) {
                    return;
                }
                Log.d(Utils.LOGTAG, "Got Attractions: " + response.body().toString());
                attractions = response.body();

                checkAttractionsPresence();
                attractionsAdapter = new RV_AttractionAdapter(attractions, localContext);
                recyclerView.setAdapter(attractionsAdapter);
            }

            @Override
            public void onFailure(Call<List<Attraction>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }

    private void checkAttractionsPresence() {
        boolean isAttractionsEmpty = (attractions.size() == 0);

        if (isAttractionsEmpty) {
            noAttractionsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noAttractionsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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

    public void setFilter(String expr) {
        List<Attraction> filtered = new ArrayList<>();
        String filter = expr.toLowerCase();
        for (Attraction attraction : attractions) {
            if (attraction.name.toLowerCase().contains(filter)) {
                filtered.add(attraction);
            }
        }
        attractionsAdapter.setFilter(filtered);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
