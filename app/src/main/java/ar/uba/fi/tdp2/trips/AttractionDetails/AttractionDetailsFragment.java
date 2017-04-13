package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import ar.uba.fi.tdp2.trips.Attraction;
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
//        LinearLayout ll = (LinearLayout) fragment.findViewById(R.id.attraction_details_linear_layout);
        ListView lw = (ListView) fragment.findViewById(R.id.attraction_information_list);
        getAttractionDetails(lw);
        return fragment;
    }

    public void getAttractionDetails(final ListView lw) {
        BackendService backendService = BackendService.retrofit.create(BackendService.class);
        Call<Attraction> call  = backendService.getAttraction(attractionId);

        call.enqueue(new Callback<Attraction>() {
            @Override
            public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                Log.d(Utils.LOGTAG, "Got Attraction: " + response.body().toString());
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
        if (getContext() == null) {
            return;
        }

        /* Set useful information details */
//        ListView informationList = (ListView) ll.findViewById(R.id.attraction_information_list);

        InformationListAdapter adapter = new InformationListAdapter(getContext(), attraction);
        informationList.setAdapter(adapter);
//        setListViewHeightBasedOnChildren(informationList);
        informationList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });



        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View header = inflater.inflate(R.layout.attraction_cover_photo_header, informationList, false);

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

        /* Set Footer */
        View footer = inflater.inflate(R.layout.footer, informationList, false);

        /* Set description */
        TextView description = (TextView) footer.findViewById(R.id.attraction_description);
        description.setText(attraction.description);

        /* Enable audioguide floating button if the attraction has one */
        RelativeLayout rl = (RelativeLayout) footer.findViewById(R.id.floating_action_button_relative_layout);
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
        final EditText reviewText = (EditText) footer.findViewById(R.id.own_review_text);
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
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) footer.findViewById(R.id.own_review_rating);
        ratingBar.setOnRatingBarChangeListener(new AppCompatRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                reviewText.setVisibility(View.VISIBLE);

                // TODO send rating to backend
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
            otherRatingBar.setRating(rev.qualification);
            otherRatingBar.setVisibility(View.VISIBLE);

            TextView otherUser = (TextView) footer.findViewById(R.id.review_author_name);
            otherUser.setText(rev.user);

            TextView date = (TextView) footer.findViewById(R.id.review_date);
            date.setText(rev.date);

            if (rev.text != null && !rev.text.equals("")) {
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

        informationList.addFooterView(footer);
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
