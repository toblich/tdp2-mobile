package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.InputType;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.Attraction.OpeningHour;
import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Utils;
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
    private Context localContext;

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
        localContext = getContext();
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
        Call<Attraction> call  = backendService.getAttraction(attractionId);

        call.enqueue(new Callback<Attraction>() {
            @Override
            public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                Log.d(Utils.getLOGTAG(localContext), "Got Attraction: " + response.body().toString());
                attraction = response.body();

                setViewContent(ll);
            }

            @Override
            public void onFailure(Call<Attraction> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.getLOGTAG(localContext), t.toString());
            }
        });
    }

    public void setViewContent(LinearLayout ll) {
        final Context context = getContext();
        if (getContext() == null) {
            return;
        }

        /* Set useful information details */
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

        /* Set cover photo */
        int placeholderId = R.mipmap.photo_placeholder;
        ImageView coverPhoto = (ImageView) ll.findViewById(R.id.attraction_cover_photo);
        Glide.with(context)
                .load(attraction.photoUri)
                .placeholder(placeholderId)
                .error(placeholderId) // TODO see if it possible to log the error
                .into(coverPhoto);

        /* Set description */
        TextView description = (TextView) ll.findViewById(R.id.attraction_description);
        description.setText(attraction.description);

        /* Enable audioguide floating button if the attraction has one */
        RelativeLayout rl = (RelativeLayout) ll.findViewById(R.id.floating_action_button_relative_layout);
        FloatingActionButton fab = (FloatingActionButton) rl.findViewById(R.id.attraction_details_audioguide_button);
        if (attraction.audioguide != null) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Audioguide", Snackbar.LENGTH_LONG).setAction("Play", null).show();
                }
            });
        }

        /* Set own review content and behaviour */
        final EditText reviewText = (EditText) ll.findViewById(R.id.own_review_text);
        reviewText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO send review to backend
                    reviewText.clearFocus();
                }
                return false;
            }
        });
        reviewText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (hasFocus) {
                    System.out.println("has focus");
                    imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                    reviewText.setRawInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    reviewText.setCursorVisible(true);
                } else {
                    System.out.println("no focus");
                    reviewText.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_FLAG_MULTI_LINE); // Hide correction underline
                    reviewText.setBackgroundColor(getResources().getColor(R.color.transparent)); // hide focus line
                    reviewText.setCursorVisible(false);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); // close keyboard
                }
            }
        });

        /* Set own rating value and behaviour */
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) ll.findViewById(R.id.own_review_rating);
        ratingBar.setOnRatingBarChangeListener(new AppCompatRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                reviewText.setVisibility(View.VISIBLE);

                // TODO send rating to backend
            }
        });

        /* Set other people's reviews */
        if (attraction.reviews.isEmpty()) {
            TextView otherReviewsTitle = (TextView) ll.findViewById(R.id.other_reviews_title);
            otherReviewsTitle.setVisibility(View.GONE);
            AppCompatRatingBar otherRatingBar = (AppCompatRatingBar) ll.findViewById(R.id.rating_stars);
            otherRatingBar.setVisibility(View.GONE);
        } else {
            System.out.println("Renderizando primera review ajena");
            Review rev = attraction.reviews.get(0);
            AppCompatRatingBar otherRatingBar = (AppCompatRatingBar) ll.findViewById(R.id.rating_stars);
            otherRatingBar.setRating(rev.qualification);
            otherRatingBar.setVisibility(View.VISIBLE);

            TextView otherUser = (TextView) ll.findViewById(R.id.review_author_name);
            otherUser.setText(rev.user);

            TextView date = (TextView) ll.findViewById(R.id.review_date);
            date.setText(rev.date);

            if (rev.text != null && !rev.text.equals("")) {
                System.out.println("Renderizando texto de review: " + rev.text);
                TextView otherReviewText = (TextView) ll.findViewById(R.id.review_text);
                otherReviewText.setText(rev.text);
                otherReviewText.setVisibility(View.VISIBLE);
            }
        }

        /* Add "see more reviews" button */
        TextView seeMoreReviewsLink = (TextView) ll.findViewById(R.id.see_more_reviews_link);
        if (attraction.reviews.size() <= 1) {
            seeMoreReviewsLink.setVisibility(View.GONE);
        } else {
            seeMoreReviewsLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getContext();
                    if (context == null) {
                        return;
                    }
                    Intent intent = new Intent(context, AllReviewsActivity.class);
                    intent.putExtra("attractionName", attraction.name);
                    intent.putExtra("attractionId", attraction.id);
                    System.out.println("Request reviews for attraction: " + attraction.toString());
                    context.startActivity(intent);
                }
            });
        }
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

    public interface OnClickCallback {
        void call(View view, TextView days, TextView hours, ImageView icon);
    }

    public class InformationListAdapter extends BaseAdapter {
        public class InfoItem {
            String value;
            int iconId;
            OnClickCallback callback;

            public InfoItem(String value, int iconId, OnClickCallback callback) {
                this.value = value;
                this.iconId = iconId;
                this.callback = callback;
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
            add(R.drawable.ic_access_time_black_24dp, attraction.openingHours);
            add(R.drawable.ic_attach_money_black_24dp, attraction.price, getString(R.string.dollars));
            add(R.drawable.ic_timer_black_24dp, attraction.duration, getString(R.string.minutes));
        }

        private void add(int iconId, String string) {
            if (string != null && !string.equals("")) {
                items.add(new InfoItem(string, iconId, null));
            }
        }

        private void add(int iconId, Number num, String string) {
            if (num != null) { // TODO make string pretty
                items.add(new InfoItem(num.toString() + " " + string, iconId, null));
            }
        }

        private void add(int iconId, final List<OpeningHour> openingHours) {
            if (openingHours == null || openingHours.isEmpty()) {
                return;
            }

            OnClickCallback callback = openingHours.size() == 1 ? null : new OnClickCallback() {
                @Override
                public void call(View view, TextView days, TextView hours, ImageView icon) {
                    // TODO
                    System.out.println("ON CLICK CALLBACK");
                    StringBuilder daysBuilder = new StringBuilder();
                    StringBuilder hoursBuilder = new StringBuilder();
                    for (OpeningHour op: openingHours) {
                        daysBuilder.append(op.day + '\n');
                        hoursBuilder.append("    " + (op.start == null ? (getString(R.string.all_day_open) + "\n") : op.start + " - " + op.end));
                    }
                    days.setText(daysBuilder.toString());
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
                    days.setLayoutParams(layoutParams);
                    hours.setVisibility(View.VISIBLE);
                    hours.setText(hoursBuilder.toString());
                }
            };

            OpeningHour first = openingHours.get(0);
            String initial = first.day + "    " + (first.start == null ? getString(R.string.all_day_open) : (first.start + " - " + first.end));
            items.add(new InfoItem(initial, iconId, callback));
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

            final View infoItem = inflater.inflate(R.layout.attraction_info_item, parent, false);
            final TextView value = (TextView) infoItem.findViewById(R.id.value);
            final ImageView icon = (ImageView) infoItem.findViewById(R.id.icon);

            final InfoItem item = items.get(position);

            value.setText(item.value);
            icon.setImageResource(item.iconId);

            if (item.callback != null) {
                value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView hours = (TextView) infoItem.findViewById(R.id.hours);
                        item.callback.call(infoItem, value, hours, icon);
                    }
                });
            }

            return infoItem;
        }
    }
}
