package ar.uba.fi.tdp2.trips.Multimedia;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {
    private static final String ARG_ATTRACTION_ID = "attractionId";
    private static final String ARG_POI_ID = "poiId";
    private static final int IMAGES_PER_ROW = 2;

    private int attractionId;
    private int poiId;
    private Gallery gallery;
    private Context localContext;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager glm;

    private GalleryFragment.OnFragmentInteractionListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param attractionId The id of the attraction whose details will be shown.
     * @return A new instance of fragment PointOfInterestFragment.
     */
    public static GalleryFragment newInstance(int attractionId, int poiId) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ATTRACTION_ID, attractionId);
        args.putInt(ARG_POI_ID, poiId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setMenuVisibility(boolean b) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            attractionId = getArguments().getInt(ARG_ATTRACTION_ID);
            poiId = getArguments().getInt(ARG_POI_ID);
        }
        localContext = getContext();
        glm = new GridLayoutManager(localContext, IMAGES_PER_ROW);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = (RecyclerView) fragment.findViewById(R.id.rvGallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(glm);
        getGallery();
        return fragment;
    }

    public void getGallery() {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<Gallery> call = (poiId != Utils.NO_POINT_OF_INTEREST) ? backendService.getPointOfInterestGallery(attractionId, poiId) : backendService.getAttractionGallery(attractionId);

        call.enqueue(new Callback<Gallery>() {
            @Override
            public void onResponse(Call<Gallery> call, Response<Gallery> response) {
                if (response.body() == null) {
                    return;
                }
                Log.d(Utils.LOGTAG, "Got Gallery: " + response.body().toString());
                gallery = response.body();

                RV_GalleryAdapter adapter = new RV_GalleryAdapter(gallery, localContext);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Gallery> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
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
