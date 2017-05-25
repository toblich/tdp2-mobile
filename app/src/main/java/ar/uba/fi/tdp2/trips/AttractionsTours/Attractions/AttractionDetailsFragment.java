package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.Common.OnFragmentInteractionListener;
import ar.uba.fi.tdp2.trips.Multimedia.EMVideoViewActivity;
import ar.uba.fi.tdp2.trips.Multimedia.FullScreenGalleryActivity;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.AttractionsTours.Tours.RV_TourAdapter;
import ar.uba.fi.tdp2.trips.Reviews.AllReviewsActivity;
import ar.uba.fi.tdp2.trips.Reviews.Review;
import ar.uba.fi.tdp2.trips.Reviews.WriteReviewFragment;
import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.Common.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttractionDetailsFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_ATTRACTION_ID = "attractionId";

    private int attractionId;
    public Attraction attraction;
    private OnFragmentInteractionListener mListener;
    private Context localContext;
    private View header;
    private View footer;

    public AttractionDetailsFragment() {
        // Required empty public constructor
    }

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
        Call<Attraction> call;
        User user = User.getInstance(getContext().getSharedPreferences("user", 0));
        if (user != null) {
            String bearer = "Bearer " + user.token;
            call = backendService.getAttractionWithAuth(attractionId, bearer);
        } else {
            call = backendService.getAttraction(attractionId);
        }

        call.enqueue(new Callback<Attraction>() {
            @Override
            public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                if (response.body() == null) {
                    return;
                }
                attraction = response.body();
                setViewContent(lw);
                /* Enable audioguide floating button if the attraction has one */
                FrameLayout rl = (FrameLayout) getActivity().findViewById(R.id.floating_action_button_relative_layout);
                FloatingActionButton fab = (FloatingActionButton) rl.findViewById(R.id.attraction_details_audioguide_button);
                if (Utils.isNotBlank(attraction.audioguide)) {
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(localContext, EMVideoViewActivity.class);
                            intent.putExtra("path", attraction.audioguide);
                            startActivity(intent);
                        }
                    });
                }
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

        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mMapFragment)
                .commit();
    }

    private void addFooter(final Context context, LayoutInflater inflater, ListView informationList) {
        footer = inflater.inflate(R.layout.attraction_details_footer, informationList, false);

        /* Set description */
        TextView description = (TextView) footer.findViewById(R.id.attraction_description);
        description.setText(attraction.description);

        /* Set own rating/review value and behaviour */
        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) footer.findViewById(R.id.own_review_rating);
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
                if (!fromUser) {
                    return;
                }

                if (attraction.ownReview == null) {
                    attraction.ownReview = new Review(0, "", "", "");
                }

                User user = User.getInstance(context.getSharedPreferences("user", 0));
                if (user != null) {
                    openWriteReviewDialog();
                } else {
                    Intent intent = new Intent(localContext, SessionActivity.class);
                    startActivityForResult(intent, SessionActivity.RequestCode.REVIEW);
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
        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) footer.findViewById(R.id.own_review_rating);
        WriteReviewFragment writeReviewFragment = (attraction.ownReview == null)
                ? WriteReviewFragment.newInstance("", 0)
                : WriteReviewFragment.newInstance(attraction.ownReview.text, (int)ratingBar.getRating());

        writeReviewFragment.setTargetFragment(this, -1);
        writeReviewFragment.show(getFragmentManager(), "writeReviewDialog");
    }

    private void addHeader(final Context context, LayoutInflater inflater, ListView informationList) {
        header = inflater.inflate(R.layout.attraction_details_header, informationList, false);

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
                .error(placeholderId)
                .into(coverPhoto);

        coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenGalleryActivity.class);
                intent.putExtra("imageURL", attraction.photoUri);
                context.startActivity(intent);
            }
        });

        informationList.addHeaderView(header);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SessionActivity.RequestCode.REVIEW:
                User user = User.getInstance(localContext.getSharedPreferences("user", 0));
                if (user != null) {
                    openWriteReviewDialog();
                } else {
                    AppCompatRatingBar ratingBar = (AppCompatRatingBar) footer.findViewById(R.id.own_review_rating);
                    ratingBar.setRating(0);
                    Toast.makeText(localContext, R.string.login_required_for_review, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(attraction.latitude, attraction.longitude), 14));

        map.addMarker(new MarkerOptions()
                .position(new LatLng(attraction.latitude, attraction.longitude))
                .title(attraction.name));

        // Disables scroll for map
        map.getUiSettings().setAllGesturesEnabled(false);

        // Redirects to GoogleMaps
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                Uri gmmIntentUri = Uri.parse("geo:<0>,<0>?q=" +
                        attraction.getFullAddress().replace(" ", "+"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(localContext, R.string.google_maps_not_installed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
