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
import android.widget.Toast;

import java.util.List;

import ar.uba.fi.tdp2.trips.Attraction;
import ar.uba.fi.tdp2.trips.BackendService;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Utils;
import retrofit2.Call;

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
        Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = inflater.inflate(R.layout.fragment_write_review, null);

        AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setRating(rating);

        final EditText message = (EditText) view.findViewById(R.id.message);
        message.setText(text);

        builder.setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(Utils.LOGTAG, "confirm review");
                        Toast.makeText(context, "CONFIRM!", Toast.LENGTH_LONG).show();
                        BackendService backendService = BackendService.retrofit.create(BackendService.class);
                        Call<Review> call = backendService.postReview();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(Utils.LOGTAG, "cancel review");
                        Toast.makeText(context, "CANCEL!", Toast.LENGTH_LONG).show();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
