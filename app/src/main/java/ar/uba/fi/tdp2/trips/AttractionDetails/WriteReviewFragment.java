package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.User;
import ar.uba.fi.tdp2.trips.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewFragment extends DialogFragment {

    private static final String ARG_TEXT = "text";
    private static final String ARG_RATING = "rating";
    private String text;
    private int rating;
    private Context context;

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
        final AttractionTabsActivity activity = (AttractionTabsActivity) getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = inflater.inflate(R.layout.fragment_write_review, null);

        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setRating(rating);

        final EditText message = (EditText) view.findViewById(R.id.message);
        message.setText(text);

        builder.setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(Utils.LOGTAG, "confirm review");

                        float finalRating = ratingBar.getRating();
                        String finalText = message.getText().toString();

                        updateDetailsFragmentReview(activity, finalRating, finalText);

                        Toast.makeText(context, "CONFIRM!", Toast.LENGTH_LONG).show(); // TODO sacar
                        BackendService backendService = BackendService.retrofit.create(BackendService.class);
                        User user = User.getInstance(context.getSharedPreferences("user", 0));
                        if (user == null) {
                            Toast.makeText(context, R.string.not_signed_in, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String bearer = "Bearer " + user.token;
                        Review review = new Review((int)finalRating, finalText);
                        Call<Review> call = backendService.postReview(activity.attractionId, bearer, review);
                        call.enqueue(new Callback<Review>() {
                            @Override
                            public void onResponse(Call<Review> call, Response<Review> response) {
                                if (response.body() == null) {
                                    Toast.makeText(context, R.string.review_posting_failure, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(context, R.string.review_posted, Toast.LENGTH_SHORT).show();
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
                        Log.d(Utils.LOGTAG, "cancel review");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    // this method could be changed into a callback on the details fragment
    private void updateDetailsFragmentReview(AttractionTabsActivity activity, float finalRating, String finalText) {
        AttractionDetailsFragment attractionDetailsFragment = (AttractionDetailsFragment) getTargetFragment();

        attractionDetailsFragment.attraction.ownReview.rating = (int)finalRating;
        AppCompatRatingBar ownReviewRatingBar = (AppCompatRatingBar) activity.findViewById(R.id.own_review_rating);
        ownReviewRatingBar.setRating(finalRating);

        attractionDetailsFragment.attraction.ownReview.text = finalText;
        if (Utils.isNotBlank(finalText)) {
            TextView ownReviewText = (TextView) activity.findViewById(R.id.own_review_text);
            ownReviewText.setText(finalText);
            ownReviewText.setVisibility(View.VISIBLE);
        }
    }
}
