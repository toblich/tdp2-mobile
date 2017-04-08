package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AttractionDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AttractionDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttractionDetailsFragment extends Fragment {
    private static final String ARG_ATTRACTION_ID = "attractionId";

    private int attractionId;
    private Attraction attraction;

    private OnFragmentInteractionListener mListener;

    public AttractionDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param attractionId The id of the attraction whose details will be shown.
     * @return A new instance of fragment AttractionDetailsFragment.
     */
    public static AttractionDetailsFragment newInstance(int attractionId) {
        AttractionDetailsFragment fragment = new AttractionDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ATTRACTION_ID, attractionId);
        fragment.setArguments(args);
        return fragment;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_attraction_details, container, false);
        LinearLayout ll = (LinearLayout) fragment.findViewById(R.id.attraction_details_linear_layout);
        getAttractionDetails(ll);
        return fragment;
    }

    public void getAttractionDetails(final LinearLayout ll) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        // TODO fetch from real server with real attractionId
        Call<Attraction> call  = backendService.getAttraction(/*attractionId*/);

        call.enqueue(new Callback<Attraction>() {
            @Override
            public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                Log.d("TRIPS", "got attraction: " + response.body().toString());
                attraction = response.body();

                setViewContent(ll);
            }

            @Override
            public void onFailure(Call<Attraction> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show(); // TODO internationalize
                Log.d("TRIPS", t.toString());
            }
        });
    }

    public void setViewContent(LinearLayout ll) {
        final Context context = getContext();
        if (getContext() == null) {
            return;
        }

        ImageView coverPhoto = (ImageView) ll.findViewById(R.id.attraction_cover_photo);
        TextView description = (TextView) ll.findViewById(R.id.attraction_description);

        ListView informationList = (ListView) ll.findViewById(R.id.attraction_information_list);

        InformationListAdapter adapter = new InformationListAdapter(getContext(), attraction);
        informationList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(informationList);
        informationList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        final EditText reviewText = (EditText) ll.findViewById(R.id.own_review_text);
        reviewText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO send review to backend
                    reviewText.clearFocus();
                    reviewText.setBackgroundColor(getResources().getColor(R.color.transparent));

                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); // close keyboard
                }
                return false;
            }
        });

        AppCompatRatingBar ratingBar = (AppCompatRatingBar) ll.findViewById(R.id.own_review_rating);
        ratingBar.setOnRatingBarChangeListener(new AppCompatRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                reviewText.setVisibility(View.VISIBLE);

                // TODO send rating to backend
            }
        });

        int placeholderId = R.mipmap.photo_placeholder;
        Glide.with(context)
                .load(attraction.photoUri)
                .placeholder(placeholderId)
                .error(placeholderId) // TODO see if it possible to log the error
                .into(coverPhoto);

        description.setText(attraction.description);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    // TODO: I _think_ all the following code is for having a floating button that sends something to the activity
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


    public class InformationListAdapter extends BaseAdapter {
        public class InfoItem {
            String value;
            int iconId;
            public InfoItem(String value, int iconId) {
                this.value = value;
                this.iconId = iconId;
            }
        }
        private Context context;
        private Attraction attraction;
        private List<InfoItem> items;

        public InformationListAdapter(Context context, Attraction attraction) {
            this.context = context;
            this.attraction = attraction;
            this.items = new ArrayList<>();
            loadItems();
        }

        private void loadItems() {
            add(R.drawable.ic_place_black_24dp, attraction.address);
            add(R.drawable.ic_public_black_24dp, attraction.url);
            add(R.drawable.ic_phone_black_24dp, attraction.phone);
            add(R.drawable.ic_attach_money_black_24dp, attraction.price, "dólares"); // TODO intenationalize
            add(R.drawable.ic_timer_black_24dp, attraction.duration, "minutos"); // TODO internationalize
        }

        private void add(int iconId, String string) {
            if (string != null && !string.equals("")) {
                items.add(new InfoItem(string, iconId));
            }
        }

        private void add(int iconId, Number num, String string) {
            if (num != null) { // TODO make string pretty
                items.add(new InfoItem(num.toString() + " " + string, iconId));
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            View infoItem = inflater.inflate(R.layout.attraction_info_item, parent, false);
            TextView value = (TextView) infoItem.findViewById(R.id.value);
            ImageView icon = (ImageView) infoItem.findViewById(R.id.icon);

            InfoItem item = items.get(position);

            value.setText(item.value);
            icon.setImageResource(item.iconId);

            return infoItem;
        }
    }
}
