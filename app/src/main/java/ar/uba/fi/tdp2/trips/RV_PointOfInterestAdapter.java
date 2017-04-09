package ar.uba.fi.tdp2.trips;

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

import java.util.List;

import ar.uba.fi.tdp2.trips.PointOfInterestDetails.PointOfInterestTabsActivity;

public class RV_PointOfInterestAdapter extends RecyclerView.Adapter<RV_PointOfInterestAdapter.PointOfInterestViewHolder> {

    List<PointOfInterest> pointsOfInterests;
    Context actualContext;

    public RV_PointOfInterestAdapter(List<PointOfInterest> pointsOfInterests, Context context) {
        this.pointsOfInterests     = pointsOfInterests;
        this.actualContext         = context;
    }

    public static class PointOfInterestViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView poiOrder;
        TextView poiName;
        TextView poiDescription;
        ImageView poiAudioguide;

        PointOfInterestViewHolder(View itemView) {
            super(itemView);
            cardView        = (CardView)  itemView.findViewById(R.id.point_of_interest_card);
            poiOrder        = (TextView)  itemView.findViewById(R.id.poi_order);
            poiName         = (TextView)  itemView.findViewById(R.id.poi_name);
            poiDescription  = (TextView)  itemView.findViewById(R.id.poi_description);
            poiAudioguide   = (ImageView) itemView.findViewById(R.id.point_of_interest_card_audioguide_icon);
        }
    }
    @Override
    public PointOfInterestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_of_interest_card, parent, false);
        return new PointOfInterestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PointOfInterestViewHolder holder, int position) {
        final PointOfInterest pointOfInterest = pointsOfInterests.get(position);
        holder.poiOrder.setText(pointOfInterest.getOrder());
        holder.poiName.setText(pointOfInterest.name);
        holder.poiDescription.setText(pointOfInterest.description);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actualContext, PointOfInterestTabsActivity.class);
                intent.putExtra("attractionId", pointOfInterest.id);
                intent.putExtra("poiName", pointOfInterest.name);
                intent.putExtra("poiOrder", pointOfInterest.getOrder());
                intent.putExtra("poiId", pointOfInterest.id);
                actualContext.startActivity(intent);
            }
        });
        if (pointOfInterest.audioguide != null) {
            holder.poiAudioguide.setVisibility(View.VISIBLE);
            holder.poiAudioguide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(actualContext, "Audioguide", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pointsOfInterests.size();
    }

}
