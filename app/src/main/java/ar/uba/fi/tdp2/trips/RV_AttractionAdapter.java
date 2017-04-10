package ar.uba.fi.tdp2.trips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.uba.fi.tdp2.trips.AttractionDetails.AttractionTabsActivity;

public class RV_AttractionAdapter extends RecyclerView.Adapter<RV_AttractionAdapter.AttractionViewHolder> {

    List<Attraction> attractions;
    Context activityContext;
    private User user;

    public RV_AttractionAdapter(List<Attraction> attractions, Context activityContext) {
        this.attractions = attractions;
        this.activityContext = activityContext; // TODO validate it's correct activityContext
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView attractionName;
        TextView attractionDescription;
        ImageView attractionPhoto;
        ImageView attractionCardFavIcon;
        ImageView attractionCardVisitedIcon;
        ImageView attractionCardDirectionsIcon;

        AttractionViewHolder(View itemView) {
            super(itemView);
            cardView              = (CardView)itemView.findViewById(R.id.attraction_card);
            attractionName        = (TextView) itemView.findViewById(R.id.attraction_name);
            attractionDescription = (TextView) itemView.findViewById(R.id.attraction_description);
            attractionPhoto       = (ImageView) itemView.findViewById(R.id.attraction_photo);
            attractionCardFavIcon =
                    (ImageView) itemView.findViewById(R.id.attraction_card_fav_icon);
            attractionCardVisitedIcon =
                    (ImageView) itemView.findViewById(R.id.attraction_card_visited_icon);
            attractionCardDirectionsIcon =
                    (ImageView) itemView.findViewById(R.id.attraction_card_directions_icon);
        }
    }

    @Override
    public AttractionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attraction_card, parent, false);
        return new AttractionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AttractionViewHolder holder, int position) {
        final Attraction attraction = attractions.get(position);
        holder.attractionName.setText(attraction.name);
        holder.attractionDescription.setText(attraction.description);

        int placeholderId = R.mipmap.photo_placeholder;

        if (attraction.photoUri != null && !attraction.photoUri.equals("")) {
            Glide.with(activityContext)
                .load(attraction.photoUri)
                .placeholder(placeholderId)
                .error(placeholderId) // TODO see if it possible to log the error
                .into(holder.attractionPhoto);
        } else { // No picture, load only placeholder
            Glide.with(activityContext)
                .load(placeholderId)
                .error(placeholderId) // TODO this is kind of redundant...
                .into(holder.attractionPhoto);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityContext, AttractionTabsActivity.class);
                intent.putExtra("attractionId", attraction.id);
                intent.putExtra("attractionName", attraction.name);
                activityContext.startActivity(intent);
            }
        });

        holder.attractionCardFavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainActivity mainActivity = (MainActivity) activityContext;
                user = User.getInstance(mainActivity.getSharedPreferences("user", 0));
                if (user != null) {
                    Toast.makeText(activityContext, "Marking as favorite", Toast.LENGTH_SHORT).show();
                } else {
                    User.loginWithSocialNetwork(mainActivity,
                            mainActivity.callbackManager,
                            mainActivity.getSharedPreferences("user", 0),
                            new User.Callback() {
                                @Override
                                public void onSuccess(User user) {
                                    holder.attractionCardFavIcon.performClick();
                                }
                            });
                }
            }
        });
    }

    public void setFilter(List<Attraction> attractions){
        this.attractions = attractions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

}
