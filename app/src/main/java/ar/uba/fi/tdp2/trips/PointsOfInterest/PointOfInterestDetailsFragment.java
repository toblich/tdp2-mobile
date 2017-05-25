package ar.uba.fi.tdp2.trips.PointsOfInterest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.Common.OnFragmentInteractionListener;
import ar.uba.fi.tdp2.trips.Multimedia.EMVideoViewActivity;
import ar.uba.fi.tdp2.trips.Multimedia.FullScreenGalleryActivity;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                if (response.body() == null) {
                    return;
                }
                pointOfInterest = response.body();
                setContentView(ll, rl);
            }
            @Override
            public void onFailure(Call<PointOfInterest> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }

    private void setContentView(LinearLayout ll, RelativeLayout rl) {
        ImageView poi_cover_photo = (ImageView) ll.findViewById(R.id.poi_details_image);
        TextView poi_description = (TextView) ll.findViewById(R.id.poi_details_description);
        FloatingActionButton fab = (FloatingActionButton) rl.findViewById(R.id.poi_details_audioguide_button);

        poi_description.setText(pointOfInterest.description);

        if (Utils.isNotBlank(pointOfInterest.audioguide)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(localContext, EMVideoViewActivity.class);
                    intent.putExtra("path", pointOfInterest.audioguide);
                    startActivity(intent);
                }
            });
        }

        int placeholderId = R.mipmap.photo_placeholder;
        Glide.with(localContext)
                .load(pointOfInterest.photoUri)
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(poi_cover_photo);

        poi_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(localContext, FullScreenGalleryActivity.class);
                intent.putExtra("imageURL", pointOfInterest.photoUri);
                localContext.startActivity(intent);
            }
        });

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
}
