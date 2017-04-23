package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.Multimedia.EMVideoViewActivity;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.RV_TourAdapter;
import ar.uba.fi.tdp2.trips.User;
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
    Attraction attraction; // Accessed by review modal
    private OnFragmentInteractionListener mListener;
    private Context localContext;
    public CallbackManager callbackManager;

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
        callbackManager = ((AttractionTabsActivity) getActivity()).callbackManager;
        localContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_attraction_details, container, false);
        ListView lw = (ListView) fragment.findViewById(R.id.attraction_information_list);
        getAttractionDetails(lw);
        return fragment;
    }

    public void getAttractionDetails(final ListView lw) {
        if (!Utils.isNetworkAvailable()) {
            Toast.makeText(localContext, getString(R.string.no_internet_error), Toast.LENGTH_LONG).show();
        }

        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<Attraction> call = backendService.getAttraction(attractionId);

        call.enqueue(new Callback<Attraction>() {
            @Override
            public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                if (response.body() == null) {
                    return;
                }
                attraction = response.body();

                setViewContent(lw);
            }

            @Override
            public void onFailure(Call<Attraction> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(localContext, getString(R.string.no_server_error), Toast.LENGTH_LONG).show();
                Log.d(Utils.LOGTAG, t.toString());
            }
        });
    }

    public void setViewContent(ListView informationList) {
        final Context context = getContext();
        if (context == null) {
            return;
        }

        InformationListAdapter adapter = new InformationListAdapter(context, attraction);
        informationList.setAdapter(adapter);
        informationList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        addHeader(context, inflater, informationList);
        addFooter(context, inflater, informationList);
    }

    private void addFooter(final Context context, LayoutInflater inflater, ListView informationList) {
        View footer = inflater.inflate(R.layout.attraction_details_footer, informationList, false);

        /* Set description */
        TextView description = (TextView) footer.findViewById(R.id.attraction_description);
        description.setText(attraction.description);

        /* Enable audioguide floating button if the attraction has one */
        RelativeLayout rl = (RelativeLayout) footer.findViewById(R.id.floating_action_button_relative_layout);
        FloatingActionButton fab = (FloatingActionButton) rl.findViewById(R.id.attraction_details_audioguide_button);
        if (Utils.isNotBlank(attraction.audioguide)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(localContext, EMVideoViewActivity.class);
                    intent.putExtra("name", attraction.name + " Audioguide");
                    intent.putExtra("path", attraction.audioguide);
                    startActivity(intent);
                }
            });
        }

        final Context activityContext = getActivity();

        /* Set own rating/review value and behaviour */
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) footer.findViewById(R.id.own_review_rating);
        TextView ownRatingText = (TextView) footer.findViewById(R.id.own_review_text);

        if (attraction.ownReview != null) {
            ratingBar.setRating(attraction.ownReview.rating);

            if (Utils.isNotBlank(attraction.ownReview.text)) {
                ownRatingText.setText(attraction.ownReview.text);
                ownRatingText.setVisibility(View.VISIBLE);
            }
        }

        ownRatingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user must already be logged in, otherwise this TextView has visibility GONE
                // (since there cannot be an "own" review for an unauthenticated user.
                openWriteReviewDialog();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new AppCompatRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (attraction.ownReview == null) {
                    attraction.ownReview = new Review((int)rating, "", "", "");
                } else {
                    attraction.ownReview.rating = (int)rating;
                }

                User user = User.getInstance(context.getSharedPreferences("user", 0));
                if (user != null) {
                    openWriteReviewDialog();
                } else {
                    User.loginWithSocialNetwork((Activity) activityContext,
                            callbackManager,
                            localContext.getSharedPreferences("user", 0),
                            new User.Callback() {
                                @Override
                                public void onSuccess(User user) {
                                    Toast.makeText(localContext, R.string.wait_a_second, Toast.LENGTH_LONG).show();
                                    openWriteReviewDialog();
                                }
                            });
                }


            }
        });

        /* Set other people's reviews */
        if (attraction.reviews.isEmpty()) {
            TextView otherReviewsTitle = (TextView) footer.findViewById(R.id.other_reviews_title);
            otherReviewsTitle.setVisibility(View.GONE);
            AppCompatRatingBar otherRatingBar = (AppCompatRatingBar) footer.findViewById(R.id.rating_stars);
            otherRatingBar.setVisibility(View.GONE);
        } else {
            System.out.println("Renderizando primera review ajena");
            Review rev = attraction.reviews.get(0);
            AppCompatRatingBar otherRatingBar = (AppCompatRatingBar) footer.findViewById(R.id.rating_stars);
            otherRatingBar.setRating(rev.rating);
            otherRatingBar.setVisibility(View.VISIBLE);

            TextView otherUser = (TextView) footer.findViewById(R.id.review_author_name);
            otherUser.setText(rev.user);

            TextView date = (TextView) footer.findViewById(R.id.review_date);
            date.setText(rev.date);

            if (Utils.isNotBlank(rev.text)) {
                System.out.println("Renderizando texto de review: " + rev.text);
                TextView otherReviewText = (TextView) footer.findViewById(R.id.review_text);
                otherReviewText.setText(rev.text);
                otherReviewText.setVisibility(View.VISIBLE);
            }
        }

        /* Add "see more reviews" button */
        TextView seeMoreReviewsLink = (TextView) footer.findViewById(R.id.see_more_reviews_link);
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

        /* Tours containing this attraction */
        if (attraction.tours != null && !attraction.tours.isEmpty()) {
            footer.findViewById(R.id.tours_containing_attraction_title).setVisibility(View.VISIBLE);
            RecyclerView toursRV = (RecyclerView) footer.findViewById(R.id.tours_containing_attraction);
            toursRV.setLayoutManager(new LinearLayoutManager(localContext));
            RV_TourAdapter tourAdapter = new RV_TourAdapter(attraction.tours, context);
            toursRV.setAdapter(tourAdapter);
        }

        informationList.addFooterView(footer);
    }

    private void openWriteReviewDialog() {
        WriteReviewFragment writeReviewFragment = (attraction.ownReview == null)
                ? WriteReviewFragment.newInstance("", 0)
                : WriteReviewFragment.newInstance(attraction.ownReview.text, attraction.ownReview.rating);
        writeReviewFragment.setTargetFragment(this, -1);
        writeReviewFragment.show(getFragmentManager(), "writeReviewDialog");
    }

    private void addHeader(Context context, LayoutInflater inflater, ListView informationList) {
        View header = inflater.inflate(R.layout.attraction_details_header, informationList, false);

        /* Set cover photo */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int placeholderId = R.mipmap.photo_placeholder;
        ImageView coverPhoto = (ImageView) header.findViewById(R.id.attraction_cover_photo);
        Glide.with(context)
                .load(attraction.photoUri)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
