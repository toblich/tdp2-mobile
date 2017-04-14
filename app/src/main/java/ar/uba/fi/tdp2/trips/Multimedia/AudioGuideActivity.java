package ar.uba.fi.tdp2.trips.Multimedia;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import ar.uba.fi.tdp2.trips.R;

public class AudioGuideActivity extends AppCompatActivity implements OnPreparedListener, OnErrorListener {

    private EMVideoView emVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_guide);

        Bundle bundle           = getIntent().getExtras();
        String name             = bundle.getString("name");
        String audioguidePath   = bundle.getString("audioguidePath");

        this.setTitle(name + " Audioguide");

        emVideoView = (EMVideoView) findViewById(R.id.audioguide_view);
        setupVideoView(audioguidePath);
    }

    private void setupVideoView(String audioguidePath) {
        emVideoView.setOnPreparedListener(this);
        emVideoView.setOnErrorListener(this);
        emVideoView.setVideoURI(Uri.parse(audioguidePath));
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
        Toast.makeText(this, getString(R.string.audioguide_error), Toast.LENGTH_SHORT).show();
        this.finish();
        return false;
    }
}
