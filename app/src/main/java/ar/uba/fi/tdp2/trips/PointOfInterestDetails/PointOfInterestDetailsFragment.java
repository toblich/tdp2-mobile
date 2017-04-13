package ar.uba.fi.tdp2.trips.PointOfInterestDetails;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.PointOfInterest;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PointOfInterestDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PointOfInterestDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointOfInterestDetailsFragment extends Fragment {
    private static final String ARG_ATTRACTION_ID = "attractionId";
    private static final String ARG_POI_ID = "poiId";

    private int attractionId;
    private int poiId;
    private Context localContext;
    private PointOfInterest pointOfInterest;

    private OnFragmentInteractionListener mListener;

    public PointOfInterestDetailsFragment() {
        // Required empty public constructor
    }

    public static PointOfInterestDetailsFragment newInstance(int attractionId, int poiId) {
        PointOfInterestDetailsFragment fragment = new PointOfInterestDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ATTRACTION_ID, attractionId);
        args.putInt(ARG_POI_ID, poiId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            attractionId = getArguments().getInt(ARG_ATTRACTION_ID);
            poiId = getArguments().getInt(ARG_POI_ID);
        }
        localContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_point_of_interest_details, container, false);
        RelativeLayout rl = (RelativeLayout) fragment.findViewById(R.id.poi_details_relative_layout);
        ScrollView scrollView = (ScrollView) fragment.findViewById(R.id.poi_details_scroll_view);
        LinearLayout ll = (LinearLayout) scrollView.findViewById(R.id.poi_details_linear_layout);
        getPointOfInterestDetails(ll, rl);
        return fragment;
    }

    private void getPointOfInterestDetails(final LinearLayout ll, final RelativeLayout rl) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<PointOfInterest> call  = backendService.getPointOfInterest(attractionId, poiId);

        call.enqueue(new Callback<PointOfInterest>() {
            @Override
            public void onResponse(Call<PointOfInterest> call, Response<PointOfInterest> response) {
                Log.d(Utils.getLOGTAG(localContext), "Got Point of Interest: " + response.body().toString());
                pointOfInterest = response.body();
                setContentView(ll, rl);
            }
            @Override
            public void onFailure(Call<PointOfInterest> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.getLOGTAG(localContext), t.toString());
            }
        });
    }

    private void setContentView(LinearLayout ll, RelativeLayout rl) {
        ImageView poi_cover_photo = (ImageView) ll.findViewById(R.id.poi_details_image);
        TextView poi_description = (TextView) ll.findViewById(R.id.poi_details_description);
        FloatingActionButton fab = (FloatingActionButton) rl.findViewById(R.id.poi_details_audioguide_button);

        poi_description.setText(pointOfInterest.description);

        if (pointOfInterest.audioguide != null) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Audioguide", Snackbar.LENGTH_LONG).setAction("Play", null).show();
                }
            });
        }

        int placeholderId = R.mipmap.photo_placeholder;
        Glide.with(localContext)
                .load(pointOfInterest.photoUri)
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(poi_cover_photo);

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
