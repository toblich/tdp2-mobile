package ar.uba.fi.tdp2.trips.TourDetails;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.RV_AttractionAdapter;
import ar.uba.fi.tdp2.trips.Tour;
import ar.uba.fi.tdp2.trips.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TourDetailsFragment extends Fragment {
    private int tourId;
    private Tour tour;
    private OnFragmentInteractionListener mListener;
    private Context localContext;
    private TourDetailsActivity activity;
//    public CallbackManager callbackManager;

    public TourDetailsFragment() {
        // Required empty public constructor
    }

//    public static TourDetailsFragment newInstance(int tourId) {
//        TourDetailsFragment fragment = new TourDetailsFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_TOUR_ID, tourId);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void setMenuVisibility(boolean b) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (TourDetailsActivity) getActivity();
//        callbackManager = activity.callbackManager;
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
        Call<Tour> call = backendService.getTour(tourId);

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
    }

    private void addFooter(LayoutInflater inflater, ListView informationList) {
        View footer = inflater.inflate(R.layout.tour_details_footer, informationList, false);

        /* Set description */
        TextView description = (TextView) footer.findViewById(R.id.tour_description);
        description.setText(tour.getDescription());

        /* Add attraction cards */
        RecyclerView recyclerView = (RecyclerView) footer.findViewById(R.id.tour_attractions_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(localContext));

        RV_AttractionAdapter attractionListAdapter = new RV_AttractionAdapter(tour.getAttractions(), localContext);
        recyclerView.setAdapter(attractionListAdapter);

        informationList.addFooterView(footer);
    }


    private void addHeader(LayoutInflater inflater, ListView informationList) {
        View header = inflater.inflate(R.layout.tour_details_header, informationList, false);

        /* Set cover photo */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int placeholderId = R.mipmap.photo_placeholder;
        ImageView coverPhoto = (ImageView) header.findViewById(R.id.tour_cover_photo);
        Glide.with(localContext)
                .load(tour.getPhotoUri() != null ? tour.getPhotoUri() : placeholderId)
                .override(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .fitCenter()
                .placeholder(placeholderId)
                .error(placeholderId) // TODO see if it possible to log the error
                .into(coverPhoto);

        informationList.addHeaderView(header);
    }

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
