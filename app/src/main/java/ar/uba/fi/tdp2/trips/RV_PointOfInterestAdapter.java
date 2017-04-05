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

import java.util.List;

public class RV_PointOfInterestAdapter extends RecyclerView.Adapter<RV_PointOfInterestAdapter.PointOfInterestViewHolder> {

    List<PointOfInterest> pointsOfInterests;
    Context actualContext;

    public RV_PointOfInterestAdapter(List<PointOfInterest> pointsOfInterests, Context context) {
        this.pointsOfInterests     = pointsOfInterests;
        this.actualContext         = context; // TODO validate it's correct context
    }

    public static class PointOfInterestViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView poiOrder;
        TextView poiName;
        TextView poiDescription;

        PointOfInterestViewHolder(View itemView) {
            super(itemView);
            cardView        = (CardView) itemView.findViewById(R.id.point_of_interest_card);
            poiOrder        = (TextView) itemView.findViewById(R.id.poi_order);
            poiName         = (TextView) itemView.findViewById(R.id.poi_name);
            poiDescription  = (TextView) itemView.findViewById(R.id.poi_description);
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
    }

    @Override
    public int getItemCount() {
        return pointsOfInterests.size();
    }

}
