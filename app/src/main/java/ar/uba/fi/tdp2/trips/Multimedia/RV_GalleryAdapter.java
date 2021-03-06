package ar.uba.fi.tdp2.trips.Multimedia;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Common.Utils;

public class RV_GalleryAdapter extends RecyclerView.Adapter<RV_GalleryAdapter.GalleryViewHolder> {

    Gallery gallery;
    Context actualContext;
    private int placeholderId = R.mipmap.photo_placeholder;

    public RV_GalleryAdapter(Gallery gallery, Context context) {
        this.gallery = gallery;
        this.actualContext  = context;
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView playImageView;

        GalleryViewHolder(View itemView) {
            super(itemView);
            imageView     = (ImageView) itemView.findViewById(R.id.image_view);
            playImageView = (ImageView) itemView.findViewById(R.id.play_image_view);
        }
    }

    @Override
    public RV_GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery, parent, false);
        return new RV_GalleryAdapter.GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RV_GalleryAdapter.GalleryViewHolder holder, int position) {
        int galleryImageSize = gallery.images.size();

        if (position < galleryImageSize) {
            final Gallery.GalleryImage image = gallery.images.get(position);

            loadImage(image.imageURL, holder.imageView);

            holder.playImageView.setVisibility(View.GONE);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(actualContext, FullScreenGalleryActivity.class);
                    intent.putExtra("imageURL", image.imageURL);
                    actualContext.startActivity(intent);
                }
            });
        } else {
            int actualPos = position - galleryImageSize;
            final Gallery.GalleryVideo video = gallery.videos.get(actualPos);

            loadImage(video.thumbnail, holder.imageView);

            holder.playImageView.setVisibility(View.VISIBLE);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(actualContext, EMVideoViewActivity.class);
                    intent.putExtra("path", video.videoURL);
                    actualContext.startActivity(intent);
                }
            });
        }
    }

    private void loadImage(String url, ImageView imageView) {
        Glide.with(actualContext)
                .load(url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return (gallery.images.size() + gallery.videos.size());
    }

}
