package ar.uba.fi.tdp2.trips.AttractionsTours.Tours;

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

import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.Common.OnFragmentInteractionListener;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToursListFragment extends Fragment {
    private static final String ARG_CITY_ID = "cityId";

    private List<Tour> tours;
    private int cityId;
    private OnFragmentInteractionListener mListener;
    private Context localContext;
    private RecyclerView recyclerView;
    private TextView noToursTextView;
    private LinearLayoutManager linearLayoutManager;
    private RV_TourAdapter toursAdapter;

    public ToursListFragment() {
        // Required empty public constructor
    }

    public static ToursListFragment newInstance(int cityId) {
        ToursListFragment fragment = new ToursListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CITY_ID, cityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityId = getArguments().getInt(ARG_CITY_ID);
        }
        localContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_tours_list, container, false);
        recyclerView = (RecyclerView) fragment.findViewById(R.id.rv);
        noToursTextView = (TextView) fragment.findViewById(R.id.noToursTextView);
        linearLayoutManager = new LinearLayoutManager(localContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        getToursList();
        return fragment;
    }

    public void getToursList() {

        tours = new ArrayList<>();

        if (!Utils.isNetworkAvailable()) {
            Toast.makeText(localContext, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
            Log.e(Utils.LOGTAG, getString(R.string.no_internet_error));
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<Tour>> call  = backendService.getTours(cityId);

        call.enqueue(new Callback<List<Tour>>() {
            @Override
            public void onResponse(Call<List<Tour>> call, Response<List<Tour>> response) {
                Log.d(Utils.LOGTAG, "Got Tours: " + response.body().toString());
                tours = response.body();

                checkToursPresence();
                toursAdapter = new RV_TourAdapter(tours, localContext);
                recyclerView.setAdapter(toursAdapter);
            }

            @Override
            public void onFailure(Call<List<Tour>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }

    private void checkToursPresence() {
        boolean isToursEmpty = (tours.size() == 0);

        if (isToursEmpty) {
            noToursTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noToursTextView.setVisibility(View.GONE);
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
        List<Tour> filtered = new ArrayList<>();
        String filter = expr.toLowerCase();
        for (Tour tour : tours) {
            if (tour.getName().toLowerCase().contains(filter)) {
                filtered.add(tour);
            }
        }
        toursAdapter.setFilter(filtered);
    }
}
