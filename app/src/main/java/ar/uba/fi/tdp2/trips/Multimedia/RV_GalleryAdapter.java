package ar.uba.fi.tdp2.trips.Multimedia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Utils;

public class RV_GalleryAdapter extends RecyclerView.Adapter<RV_GalleryAdapter.GalleryViewHolder> {

    Gallery gallery;
    Context actualContext;

    public RV_GalleryAdapter(Gallery gallery, Context context) {
        this.gallery = gallery;
        this.actualContext  = context;
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        GalleryViewHolder(View itemView) {
            super(itemView);
            imageView   = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }

    @Override
    public RV_GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery, parent, false);
        return new RV_GalleryAdapter.GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RV_GalleryAdapter.GalleryViewHolder holder, int position) {
        final Gallery.GalleryImage image = gallery.images.get(position);

        int placeholderId = R.mipmap.photo_placeholder;

        if (Utils.isNotBlank(image.imageURL)) {
            Glide.with(actualContext)
                    .load(image.imageURL)
                    .placeholder(placeholderId)
                    .error(placeholderId)
                    .into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(actualContext, "Tap on image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (gallery.images.size());
    }

}
