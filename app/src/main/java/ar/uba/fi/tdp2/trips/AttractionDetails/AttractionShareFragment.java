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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.facebook.CallbackManager;

import ar.uba.fi.tdp2.trips.R;
import ar.uba.fi.tdp2.trips.User;

public class AttractionShareFragment extends DialogFragment {

    public CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.fragment_attraction_share, null);
        final EditText message = (EditText) view.findViewById(R.id.message);
        message.setText("Trips me ayudó a conocer más de " + getArguments().getCharSequence("attractionName") + "!");
        final AttractionShareFragment fragment = this;
        builder.setView(view)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
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
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
