package ar.uba.fi.tdp2.trips.AttractionDetails;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.PointOfInterest;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.RV_PointOfInterestAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PointOfInterestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PointOfInterestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointOfInterestFragment extends Fragment {
    private static final String ARG_ATTRACTION_ID = "attractionId";

    private int attractionId;
    private List<PointOfInterest> pointsOfInterest;
    private Context localContext;
    private RecyclerView recyclerView;
    private RelativeLayout rl;
    private LinearLayoutManager llm;

    private OnFragmentInteractionListener mListener;

    public PointOfInterestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param attractionId The id of the attraction whose details will be shown.
     * @return A new instance of fragment PointOfInterestFragment.
     */
    public static PointOfInterestFragment newInstance(int attractionId) {
        PointOfInterestFragment fragment = new PointOfInterestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ATTRACTION_ID, attractionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPointsOfInterest();
    }

    @Override
    public void setMenuVisibility(boolean b) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            attractionId = getArguments().getInt(ARG_ATTRACTION_ID);
        }
        localContext = getContext();
        llm = new LinearLayoutManager(localContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_point_of_interest, container, false);
        rl = (RelativeLayout) fragment.findViewById(R.id.fragment_empty_poi_list);
        recyclerView = (RecyclerView) fragment.findViewById(R.id.rvPointsOfInterest);
        recyclerView.setLayoutManager(llm);
        getPointsOfInterest();
        return fragment;
    }

    public void getPointsOfInterest() {
        pointsOfInterest = new ArrayList<>();

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<List<PointOfInterest>> call  = backendService.getPointsOfInterest(attractionId);

        call.enqueue(new Callback<List<PointOfInterest>>() {
            @Override
            public void onResponse(Call<List<PointOfInterest>> call, Response<List<PointOfInterest>> response) {
                Log.d("TRIPS", "got points of interest: " + response.body().toString());
                pointsOfInterest = response.body();

                if (pointsOfInterest.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    rl.setVisibility(View.GONE);
                }

                RV_PointOfInterestAdapter adapter = new RV_PointOfInterestAdapter(pointsOfInterest, localContext);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<PointOfInterest>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
