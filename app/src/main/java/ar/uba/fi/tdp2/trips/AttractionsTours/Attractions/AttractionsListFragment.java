package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttractionsListFragment extends Fragment {

    private static final String ARG_CITY_ID = "cityId";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private List<Attraction> attractions;

    private Double latitude;
    private Double longitude;

    private int cityId;
    private OnFragmentInteractionListener mListener;
    private Context localContext;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RV_AttractionAdapter attractionsAdapter;
    private TextView noAttractionsTextView;
    private User user;
    private CardView attractionCard;

    public AttractionsListFragment() {
        // Required empty public constructor
    }

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
        attractionCard = (CardView) (inflater.inflate(R.layout.attraction_card, container, false).findViewById(R.id.attraction_card));
        getAttractionsList();
        return fragment;
    }

    @Override
    public void onResume() {
        getAttractionsList();
        super.onResume();
    }

    //TODO: ver como obtener el fragmento para que llame a este onActivityResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        user = User.getInstance(localContext.getSharedPreferences("user", 0));
        switch (requestCode) {
            case SessionActivity.RequestCode.FAVORITE:
                if (user != null) {
                    //Marco como favorito
                    Toast.makeText(localContext, "CallbackFavorito", Toast.LENGTH_SHORT).show();
                    attractionCard.findViewById(R.id.attraction_card_fav_icon).performClick();
                } else {
                    Toast.makeText(localContext, R.string.login_required_for_favorite, Toast.LENGTH_SHORT).show();
                }
                break;
            case SessionActivity.RequestCode.VISITED:
                if (user != null) {
                    //Marco como visitado
                    Toast.makeText(localContext, "CallbackVisitado", Toast.LENGTH_SHORT).show();
                    attractionCard.findViewById(R.id.attraction_card_visited_icon).performClick();
                } else {
                    Toast.makeText(localContext, R.string.login_required_for_visited, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getAttractionsList() {

        attractions = new ArrayList<>();

        if (!Utils.isNetworkAvailable()) {
            Toast.makeText(localContext, getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
            Log.e(Utils.LOGTAG, getString(R.string.no_internet_error));
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<Attraction>> call;
        user = User.getInstance(getContext().getSharedPreferences("user", 0));
        if (user != null) {
            String bearer = "Bearer " + user.token;
            call = backendService.getAttractionsWithAuth(latitude, longitude, 0.5, bearer);
        } else {
            call = backendService.getAttractions(latitude, longitude, 0.5);
        }

        call.enqueue(new Callback<List<Attraction>>() {
            @Override
            public void onResponse(Call<List<Attraction>> call, Response<List<Attraction>> response) {
                if (response.body() == null) {
                    return;
                }
                Log.d(Utils.LOGTAG, "Got Attractions: " + response.body().toString());
                attractions = response.body();

                checkAttractionsPresence();
                attractionsAdapter = new RV_AttractionAdapter(attractions, localContext, getActivity());
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
}
