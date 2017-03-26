package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RV_AttractionAdapter extends RecyclerView.Adapter<RV_AttractionAdapter.AttractionViewHolder> {

    List<Attraction> attractions;
    Context context;

    public RV_AttractionAdapter(List<Attraction> attractions, Context context) {
        this.attractions = attractions;
        this.context     = context; // TODO validate it's correct context
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView attractionName;
        TextView attractionDescription;
        ImageView attractionPhoto;

        AttractionViewHolder(View itemView) {
            super(itemView);
            cardView              = (CardView)itemView.findViewById(R.id.attraction_card);
            attractionName        = (TextView) itemView.findViewById(R.id.attraction_name);
            attractionDescription = (TextView) itemView.findViewById(R.id.attraction_description);
            attractionPhoto       = (ImageView) itemView.findViewById(R.id.attraction_photo);
        }
    }

    @Override
    public AttractionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attraction_card, parent, false);
        return new AttractionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AttractionViewHolder holder, int position) {
        Attraction attraction = attractions.get(position);
        holder.attractionName.setText(attraction.name);
        holder.attractionDescription.setText(attraction.description);

        int placeholderId = R.mipmap.photo_placeholder;

        if (attraction.photoUri != null && !attraction.photoUri.equals("")) {
            Glide.with(context)
                .load(attraction.photoUri)
                .placeholder(placeholderId)
                .error(placeholderId) // TODO see if it possible to log the error
                .into(holder.attractionPhoto);
        } else { // No picture, load only placeholder
            Glide.with(context)
                .load(placeholderId)
                .error(placeholderId) // TODO this is kind of redundant...
                .into(holder.attractionPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

}
