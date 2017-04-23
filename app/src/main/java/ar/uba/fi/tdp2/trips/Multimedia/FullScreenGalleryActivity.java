package ar.uba.fi.tdp2.trips.Multimedia;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.Utils;

public class FullScreenGalleryActivity extends AppCompatActivity {

    private Context actualContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen_gallery);

        Bundle bundle = getIntent().getExtras();
        String imageURL  = bundle.getString("imageURL");

        ImageView imageView = (ImageView) findViewById(R.id.fullscreen_image);

        int placeholderId = R.mipmap.photo_placeholder;

        if (Utils.isNotBlank(imageURL)) {
            Glide.with(actualContext)
                    .load(imageURL)
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(placeholderId)
                    .error(placeholderId)
                    .into(imageView);
        }
    }
}
