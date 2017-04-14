package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ar.uba.fi.tdp2.trips.R;

public class RV_ReviewsAdapter extends RecyclerView.Adapter<RV_ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public RV_ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        AppCompatRatingBar ratingBar;
        TextView userName;
        TextView date;
        TextView reviewText;

        ReviewViewHolder(View itemView) {
            super(itemView);
            ratingBar  = (AppCompatRatingBar) itemView.findViewById(R.id.rating_stars);
            userName   = (TextView) itemView.findViewById(R.id.review_author_name);
            date       = (TextView) itemView.findViewById(R.id.review_date);
            reviewText = (TextView) itemView.findViewById(R.id.review_text);
        }
    }
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.ratingBar.setRating(review.rating);
        holder.userName.setText(review.user);
        holder.date.setText(review.date);
        holder.reviewText.setText(review.text);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
