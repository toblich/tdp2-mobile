package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RV_TourAdapter extends RecyclerView.Adapter<RV_TourAdapter.TourViewHolder> {

    List<Tour> tours;
    Context actualContext;

    public RV_TourAdapter(List<Tour> tours, Context context) {
        this.tours         = tours;
        this.actualContext = context;
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tourName;
        TextView tourDescription;
        TextView tourEstimatedTime;

        TourViewHolder(View itemView) {
            super(itemView);
            cardView    = (CardView) itemView.findViewById(R.id.tour_card);
            tourName    = (TextView) itemView.findViewById(R.id.tour_name);
            tourDescription    = (TextView) itemView.findViewById(R.id.tour_description);
            tourEstimatedTime    = (TextView) itemView.findViewById(R.id.tour_estimated_time);

        }
    }

    @Override
    public RV_TourAdapter.TourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_card, parent, false);
        return new RV_TourAdapter.TourViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RV_TourAdapter.TourViewHolder holder, int position) {
        final Tour tour = tours.get(position);
        holder.tourName.setText(tour.getName());
        holder.tourDescription.setText(tour.getDescription());
        holder.tourEstimatedTime.setText(Utils.prettyShortTimeStr(tour.getEstimatedTime()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(actualContext, "Selected" + tour.getName(), Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(actualContext, TourDetailsActivity.class);
//                intent.putExtra("tourId", tour.getTourId());
//                actualContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public void setFilter(List<Tour> filter) {
        this.tours = filter;
        notifyDataSetChanged();
    }
}
