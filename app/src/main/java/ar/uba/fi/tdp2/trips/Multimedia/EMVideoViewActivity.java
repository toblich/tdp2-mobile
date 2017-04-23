package ar.uba.fi.tdp2.trips.Multimedia;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import ar.uba.fi.tdp2.trips.R;

public class EMVideoViewActivity extends AppCompatActivity implements OnPreparedListener, OnErrorListener {

    private EMVideoView emVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emvideoview);

        Bundle bundle = getIntent().getExtras();
        String name  = bundle.getString("name");
        String path  = bundle.getString("path");

        this.setTitle(name);

        emVideoView = (EMVideoView) findViewById(R.id.emvideo_view);
        setupVideoView(path);
    }

    private void setupVideoView(String path) {
        emVideoView.setOnPreparedListener(this);
        emVideoView.setOnErrorListener(this);
        emVideoView.setVideoURI(Uri.parse(path));
        emVideoView.getVideoControls().setCanHide(false);
    }

    @Override
    public void onPrepared() {
        emVideoView.start();
    }

    @Override
    public void onPause() {
        emVideoView.pause();
        super.onPause();
    }

    @Override
    public boolean onError() {
        Toast.makeText(this, getString(R.string.emvideoview_error), Toast.LENGTH_SHORT).show();
        this.finish();
        return false;
    }
}
