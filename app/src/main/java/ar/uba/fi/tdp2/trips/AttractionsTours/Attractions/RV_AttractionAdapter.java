package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import ar.uba.fi.tdp2.trips.Common.ActivityWithCallbackManager;
import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.User;

public class RV_AttractionAdapter extends RecyclerView.Adapter<RV_AttractionAdapter.AttractionViewHolder> {

    List<Attraction> attractions;
    Context activityContext;
    private User user;

    public RV_AttractionAdapter(List<Attraction> attractions, Context activityContext) {
        this.attractions = attractions;
        this.activityContext = activityContext;
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

        if (Utils.isNotBlank(attraction.photoUri)) {
            Glide.with(activityContext)
                .load(attraction.photoUri)
                .placeholder(placeholderId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(placeholderId)
                .into(holder.attractionPhoto);
        } else { // No picture, load only placeholder
            Glide.with(activityContext)
                .load(placeholderId)
                .error(placeholderId)
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
                final ActivityWithCallbackManager activity = (ActivityWithCallbackManager) activityContext;
                System.out.println("Shared preferencies user: " + activity.getSharedPreferences("user", 0));
                user = User.getInstance(activity.getSharedPreferences("user", 0));
                if (user != null) {
                    Toast.makeText(activityContext, "Marking as favorite", Toast.LENGTH_SHORT).show();
                } else {
                    User.loginWithSocialNetwork(activity,
                            activity.callbackManager,
                            activity.getSharedPreferences("user", 0),
                            new User.Callback() {
                                @Override
                                public void onSuccess(User user) {
                                    holder.attractionCardFavIcon.performClick();
                                }
                                @Override
                                public void onError(User user) {}
                            });
                }
            }
        });

        holder.attractionCardDirectionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                        attraction.getFullAddress().replace(" ", "+"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(activityContext.getPackageManager()) != null) {
                    activityContext.startActivity(mapIntent);
                } else {
                    Toast.makeText(activityContext, R.string.google_maps_not_installed, Toast.LENGTH_LONG).show();
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
