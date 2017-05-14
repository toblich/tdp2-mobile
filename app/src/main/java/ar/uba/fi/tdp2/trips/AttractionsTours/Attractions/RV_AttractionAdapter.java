package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import ar.uba.fi.tdp2.trips.Common.BackendService;
import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RV_AttractionAdapter extends RecyclerView.Adapter<RV_AttractionAdapter.AttractionViewHolder> {

    List<Attraction> attractions;
    Context activityContext;
    private User user;
    private FragmentActivity fragment;

    public RV_AttractionAdapter(List<Attraction> attractions, Context activityContext, FragmentActivity fragment) {
        this.attractions = attractions;
        this.activityContext = activityContext;
        this.fragment = fragment;
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView attractionName;
        TextView attractionDescription;
        ImageView attractionPhoto;
        ImageView attractionCardFavIcon;
        ImageView attractionCardFavIconRed;
        ImageView attractionCardVisitedIcon;
        ImageView attractionCardVisitedIconBlack;
        ImageView attractionCardDirectionsIcon;


        AttractionViewHolder(View itemView) {
            super(itemView);
            cardView              = (CardView)itemView.findViewById(R.id.attraction_card);
            attractionName        = (TextView) itemView.findViewById(R.id.attraction_name);
            attractionDescription = (TextView) itemView.findViewById(R.id.attraction_description);
            attractionPhoto       = (ImageView) itemView.findViewById(R.id.attraction_photo);
            attractionCardFavIcon = (ImageView) itemView.findViewById(R.id.attraction_card_fav_icon);
            attractionCardFavIconRed = (ImageView) itemView.findViewById(R.id.attraction_card_fav_icon_red);
            attractionCardVisitedIcon = (ImageView) itemView.findViewById(R.id.attraction_card_visited_icon);
            attractionCardVisitedIconBlack = (ImageView) itemView.findViewById(R.id.attraction_card_visited_icon_black);
            attractionCardDirectionsIcon = (ImageView) itemView.findViewById(R.id.attraction_card_directions_icon);
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
        //Obtengo el user si es que hizo LogIn
        user = User.getInstance(activityContext.getSharedPreferences("user", 0));

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

        //Si el user hizo LogIn y tiene marcada como favorita la atraccion es otro icono.
        if (user != null && attraction.favorite) {
            holder.attractionCardFavIcon.setVisibility(View.GONE);
            holder.attractionCardFavIconRed.setVisibility(View.VISIBLE);
        }

        holder.attractionCardFavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = User.getInstance(activityContext.getSharedPreferences("user", 0));
                if (user != null) {
                    //Marco como favorito
                    String bearer = "Bearer " + user.token;
                    BackendService backendService = BackendService.retrofit.create(BackendService.class);
                    Call<Attraction> call = backendService.markFavoriteAttraction(attraction.id, bearer);
                    call.enqueue(new Callback<Attraction>() {
                        @Override
                        public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                            if (response.code() != 201) {
                                Toast.makeText(activityContext, R.string.error_marked_as_favorite, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Cambio el icono
                            holder.attractionCardFavIcon.setVisibility(View.GONE);
                            holder.attractionCardFavIconRed.setVisibility(View.VISIBLE);
                            //Aviso al User
                            Toast.makeText(activityContext, R.string.marked_as_favorite, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<Attraction> call, Throwable t) {
                            Log.d(Utils.LOGTAG, t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(activityContext, R.string.error_marked_as_favorite, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //Iniciar Sesión
                    Intent intent = new Intent(activityContext, SessionActivity.class);
                    fragment.startActivityForResult(intent, SessionActivity.RequestCode.FAVORITE);
                }
            }
        });

        holder.attractionCardFavIconRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = User.getInstance(activityContext.getSharedPreferences("user", 0));
                if (user != null) {
                    //Desmarco como favorito
                    String bearer = "Bearer " + user.token;
                    BackendService backendService = BackendService.retrofit.create(BackendService.class);
                    Call<Attraction> call = backendService.unmarkFavoriteAttraction(attraction.id, bearer);
                    call.enqueue(new Callback<Attraction>() {
                        @Override
                        public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                            if (response.code() != 204) {
                                Toast.makeText(activityContext, R.string.error_unmarked_as_favorite, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Cambio el icono
                            holder.attractionCardFavIcon.setVisibility(View.VISIBLE);
                            holder.attractionCardFavIconRed.setVisibility(View.GONE);
                            //Aviso al User
                            Toast.makeText(activityContext, R.string.unmarked_as_favorite, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<Attraction> call, Throwable t) {
                            Log.d(Utils.LOGTAG, t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(activityContext, R.string.error_unmarked_as_favorite, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //Si el user hizo LogIn y tiene marcada como visitada la atraccion es otro icono.
        if (user != null && attraction.visited) {
            holder.attractionCardVisitedIcon.setVisibility(View.GONE);
            holder.attractionCardVisitedIconBlack.setVisibility(View.VISIBLE);
        }

        holder.attractionCardVisitedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = User.getInstance(activityContext.getSharedPreferences("user", 0));
                if (user != null) {
                    //Marco como visitado
                    String bearer = "Bearer " + user.token;
                    BackendService backendService = BackendService.retrofit.create(BackendService.class);
                    Call<Attraction> call = backendService.markVisitedAttraction(attraction.id, bearer);
                    call.enqueue(new Callback<Attraction>() {
                        @Override
                        public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                            if (response.code() != 201) {
                                Toast.makeText(activityContext, R.string.error_marked_as_visited, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Cambio el icono
                            holder.attractionCardVisitedIcon.setVisibility(View.GONE);
                            holder.attractionCardVisitedIconBlack.setVisibility(View.VISIBLE);
                            //Aviso al User
                            Toast.makeText(activityContext, R.string.marked_as_visited, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<Attraction> call, Throwable t) {
                            Log.d(Utils.LOGTAG, t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(activityContext, R.string.error_marked_as_visited, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //Iniciar Sesión
                    Intent intent = new Intent(activityContext, SessionActivity.class);
                    fragment.startActivityForResult(intent, SessionActivity.RequestCode.VISITED);
                }
            }
        });

        holder.attractionCardVisitedIconBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = User.getInstance(activityContext.getSharedPreferences("user", 0));
                if (user != null) {
                    //Desmarco como visitado
                    String bearer = "Bearer " + user.token;
                    BackendService backendService = BackendService.retrofit.create(BackendService.class);
                    Call<Attraction> call = backendService.unmarkVisitedAttraction(attraction.id, bearer);
                    call.enqueue(new Callback<Attraction>() {
                        @Override
                        public void onResponse(Call<Attraction> call, Response<Attraction> response) {
                            if (response.code() != 204) {
                                Toast.makeText(activityContext, R.string.error_unmarked_as_visited, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Cambio el icono
                            holder.attractionCardVisitedIcon.setVisibility(View.VISIBLE);
                            holder.attractionCardVisitedIconBlack.setVisibility(View.GONE);
                            //Aviso al User
                            Toast.makeText(activityContext, R.string.unmarked_as_visited, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<Attraction> call, Throwable t) {
                            Log.d(Utils.LOGTAG, t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(activityContext, R.string.error_unmarked_as_visited, Toast.LENGTH_SHORT).show();
                        }
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
