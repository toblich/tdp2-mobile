package ar.uba.fi.tdp2.trips.AttractionDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;

import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.User;

public class AttractionShareActivity extends AppCompatActivity {

    public CallbackManager callbackManager;
    private CharSequence attractionName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attraction_share);

        Bundle bundle = getIntent().getExtras();
        attractionName = bundle.getCharSequence("attractionName");
        setTitle(getString(R.string.attraction_share_title, attractionName));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callbackManager = CallbackManager.Factory.create();

        final EditText message = (EditText) findViewById(R.id.message);
        message.setText(getString(R.string.attraction_post_message) + " " + attractionName + "!");
    }

    public void cancel(View view) {
        finish();
    }

    public void send(View view) {
        User user = User.getInstance(getSharedPreferences("user", 0));
        System.out.println(user);
        final Button button = (Button) view;
        button.setClickable(false);
        button.setText(R.string.sending);
        if (user != null) {
            final EditText message = (EditText) findViewById(R.id.message);
            final Activity activity = this;
            user.postInSocialNetwork(message.getText().toString(), new User.Callback() {
                @Override
                public void onSuccess(User user) {
                    button.setClickable(true);
                    button.setText(R.string.send);
                    Toast.makeText(activity, R.string.attraction_share_ok,
                            Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onError(User user) {
                    button.setClickable(true);
                    button.setText(R.string.send);
                    Toast.makeText(activity, R.string.attraction_share_error,
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            System.out.println("Error grave");
        }
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText message = (EditText) view.findViewById(R.id.message);
        message.setText(getString(R.string.attraction_post_message) + getArguments().getCharSequence("attractionName") + "!");
        final AttractionShareActivity fragment = this;
        builder.setView(view)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        User user = User.getInstance(getActivity().getSharedPreferences("user", 0));
                        System.out.println(user);
                        if (user != null) {
                            user.postInSocialNetwork(fragment,
                                    fragment.callbackManager,
                                    message.getText().toString());
                        } else {
                            System.out.println("Error grave");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
