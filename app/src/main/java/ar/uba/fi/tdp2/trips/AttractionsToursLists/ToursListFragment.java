package ar.uba.fi.tdp2.trips.AttractionsToursLists;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

import ar.uba.fi.tdp2.trips.Attraction;

public class ToursListFragment extends Fragment {

    private static final String ARG_CITY_ID = "cityId";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private List<Attraction> attractions;

    private Double latitude;
    private Double longitude;

    private int cityId;
    private ToursListFragment.OnFragmentInteractionListener mListener;
    private Context localContext = getActivity();

    public ToursListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cityId The id of the attraction whose details will be shown.
     * @return A new instance of fragment AttractionDetailsFragment.
     */

    public static ToursListFragment newInstance(Double latitude, Double longitude, int cityId) {
        ToursListFragment fragment = new ToursListFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putInt(ARG_CITY_ID, cityId);
        fragment.setArguments(args);
        return fragment;
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
