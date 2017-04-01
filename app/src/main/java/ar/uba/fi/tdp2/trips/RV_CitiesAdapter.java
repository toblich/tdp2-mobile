package ar.uba.fi.tdp2.trips;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RV_CitiesAdapter extends RecyclerView.Adapter<RV_CitiesAdapter.CityViewHolder> {

    List<City> cities;
    Context actualContext;

    public RV_CitiesAdapter(List<City> cities, Context context) {
        this.cities     = cities;
        this.actualContext    = context; // TODO validate it's correct context
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cityName;
        TextView cityCountry;

        CityViewHolder(View itemView) {
            super(itemView);
            cardView    = (CardView) itemView.findViewById(R.id.city_card);
            cityName    = (TextView) itemView.findViewById(R.id.city_name);
            cityCountry = (TextView) itemView.findViewById(R.id.city_country);
        }
    }
    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_card, parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        final City city = cities.get(position);
        holder.cityName.setText(city.getName());
        holder.cityCountry.setText(city.getCountry());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actualContext, MainActivity.class);
                intent.putExtra("locality", city.getName());
                intent.putExtra("latitude", city.getLatitude());
                intent.putExtra("longitude", city.getLongitude());
                actualContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

}
