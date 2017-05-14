package ar.uba.fi.tdp2.trips.Multimedia;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.tdp2.trips.Common.Utils;

public class Gallery {
    public @SerializedName("images") List<GalleryImage> images;
    public @SerializedName("videos") List<GalleryVideo> videos;

    public Gallery(List<GalleryImage> images, List<GalleryVideo> videos) {
        this.images = images;
        this.videos = videos;
    }

    public void setImagesAndVideosWithFilter() {
        this.images = filterImagesWithEmptyURL(images);
        this.videos = filterVideosWithEmptyURL(videos);
    }

    private List<Gallery.GalleryImage> filterImagesWithEmptyURL(List<Gallery.GalleryImage> images) {
        final List<Gallery.GalleryImage> filterList = new ArrayList<>();
        for (Gallery.GalleryImage image : images) {
            if (Utils.isNotBlank(image.imageURL)) {
                filterList.add(image);
            }
        }
        return filterList;
    }

    private List<Gallery.GalleryVideo> filterVideosWithEmptyURL(List<Gallery.GalleryVideo> videos) {
        final List<Gallery.GalleryVideo> filterList = new ArrayList<>();
        for (Gallery.GalleryVideo video : videos) {
            if (Utils.isNotBlank(video.videoURL)) {
                filterList.add(video);
            }
        }
        return filterList;
    }

    @Override
    public String toString() {
        return "Gallery {\n  videos: " + videos.toString() + "\n  images: " + images.toString() + "\n}";
    }

    public class GalleryImage {
        public int id;
        public int order;
        public @SerializedName("image") String imageURL;

        public GalleryImage(int id, int order, String imageURL){
            this.id = id;
            this.order = order;
            this.imageURL = imageURL;
        }

        @Override
        public String toString() {
            return "GalleryImage {\n  id: " + String.valueOf(id) + "\n  order: " + String.valueOf(order) +
                    "\n  imageUrl: " + imageURL + "\n}";
        }
    }

    public class GalleryVideo {
        public int id;
        public String language;
        public int order;
        public @SerializedName("video") String videoURL;
        public String thumbnail;

        public GalleryVideo(int id, String language, int order, String videoURL, String thumbnail){
            this.id = id;
            this.language = language;
            this.order = order;
            this.videoURL = videoURL;
            this.thumbnail = thumbnail;
        }

        @Override
        public String toString() {
            return "GalleryVideo {\n  id: " + String.valueOf(id) + "\n language: " + language +
                    "\n  order: " + String.valueOf(order) + "\n  videoURL: " + videoURL + "\n}";
        }
    }
}
