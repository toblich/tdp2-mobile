package ar.uba.fi.tdp2.trips.Reviews;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.AttractionDetailsFragment;
import ar.uba.fi.tdp2.trips.AttractionsTours.Attractions.AttractionTabsActivity;
import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.Common.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewFragment extends DialogFragment {

    private static final String ARG_TEXT = "text";
    private static final String ARG_RATING = "rating";
    private String text;
    private int rating;
    private Context context;
    private AlertDialog dialog;
    private EditText message;
    private AppCompatRatingBar ratingBar;
    private AttractionTabsActivity activity;
    private AttractionDetailsFragment attractionDetailsFragment;

    public static WriteReviewFragment newInstance(String text, int rating) {
        WriteReviewFragment f = new WriteReviewFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_RATING, rating);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Bundle args = getArguments();
        if (args != null) {
            text = args.getString(ARG_TEXT);
            rating = args.getInt(ARG_RATING);
        }
        context = getContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        activity = (AttractionTabsActivity) getActivity();
        attractionDetailsFragment = (AttractionDetailsFragment) getTargetFragment();

        LayoutInflater inflater = activity.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = inflater.inflate(R.layout.fragment_write_review, null);

        ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setRating(rating);

        message = (EditText) view.findViewById(R.id.message);
        message.setText(text);

        builder.setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        Log.d(Utils.LOGTAG, "confirm review");

                        float finalRating = ratingBar.getRating();
                        String finalText = message.getText().toString();

                        final Review previousReview = attractionDetailsFragment.attraction.ownReview.clone();
                        final Review newReview = new Review((int)finalRating, finalText);

                        updateDetailsFragmentReview(newReview);

                        BackendService backendService = BackendService.retrofit.create(BackendService.class);
                        User user = User.getInstance(context.getSharedPreferences("user", 0));
                        if (user == null) {
                            Toast.makeText(context, R.string.not_signed_in, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        System.out.println(user.toString());
                        String bearer = "Bearer " + user.token;
                        Call<Review> call = backendService.postReview(activity.attractionId, bearer, newReview);
                        call.enqueue(new Callback<Review>() {
                            @Override
                            public void onResponse(Call<Review> call, Response<Review> response) {
                                if (response.code() == 403) {
                                    AlertDialog.Builder blockedDialogBuilder = new AlertDialog.Builder(activity)
                                            .setTitle(R.string.user_blocked)
                                            .setCancelable(true)
                                            .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface blockedDialog, int which) {
                                                    updateDetailsFragmentReview(previousReview);
                                                    dialog.cancel();
                                                }
                                            });
                                    blockedDialogBuilder.create().show();
                                }
                                else if (response.body() == null) {
                                    Toast.makeText(context, R.string.review_posting_failure, Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    Toast.makeText(context, R.string.review_posted, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Review> call, Throwable t) {
                                Log.d(Utils.LOGTAG, t.getMessage());
                                t.printStackTrace();
                                Toast.makeText(context, R.string.review_posting_failure, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetDetailsFragmentRating();
                        Log.d(Utils.LOGTAG, "cancel review");
                    }
                })
        ;

        // Create the AlertDialog object
        dialog = builder.create();
        dialog.show();

        System.out.println("onCreateDialog ending");

        // Initial positive button state
        calculatePositiveButtonEnabledState();

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update positive button state
                calculatePositiveButtonEnabledState();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Update positive button state
                calculatePositiveButtonEnabledState();
            }
        });

        // Return the AlertDialog object
        return dialog;
    }

    public void calculatePositiveButtonEnabledState() {
        String newText = message.getEditableText().toString();
        boolean hasRatingChanged = (attractionDetailsFragment.attraction.ownReview.rating != (int)ratingBar.getRating());
        boolean enabled = Utils.isNotBlank(newText) && (!newText.equals(text) || hasRatingChanged);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        resetDetailsFragmentRating();
        Log.d(Utils.LOGTAG, "onCancel dialogFragment");
    }

    // this method could be changed into a callback on the details fragment
    private void updateDetailsFragmentReview(Review newReview) {
        int finalRating = newReview.rating;
        String finalText = newReview.text;

        attractionDetailsFragment.attraction.ownReview.rating = finalRating;
        AppCompatRatingBar ownReviewRatingBar = (AppCompatRatingBar) activity.findViewById(R.id.own_review_rating);
        ownReviewRatingBar.setRating(finalRating);

        attractionDetailsFragment.attraction.ownReview.text = finalText;
        TextView ownReviewText = (TextView) activity.findViewById(R.id.own_review_text);
        if (Utils.isNotBlank(finalText)) {
            ownReviewText.setText(finalText);
            ownReviewText.setVisibility(View.VISIBLE);
        } else {
            ownReviewText.setText("");
            ownReviewText.setVisibility(View.GONE);
        }
    }

    private void resetDetailsFragmentRating() {
        AppCompatRatingBar ownReviewRatingBar = (AppCompatRatingBar) activity.findViewById(R.id.own_review_rating);
        int originalRating = attractionDetailsFragment.attraction.ownReview.rating;
        System.out.println(originalRating);
        ownReviewRatingBar.setRating(originalRating);
    }
}
