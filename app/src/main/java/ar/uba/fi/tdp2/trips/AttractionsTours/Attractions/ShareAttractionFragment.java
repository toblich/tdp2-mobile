package ar.uba.fi.tdp2.trips.AttractionsTours.Attractions;


import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ar.uba.fi.tdp2.trips.Common.User;
import ar.uba.fi.tdp2.trips.Common.Utils;
import ar.uba.fi.tdp2.trips.R;


public class ShareAttractionFragment extends DialogFragment {

    private static final String ARG_TEXT = "text";
    private String text;
    private Context context;

    public static ShareAttractionFragment newInstance(String text) {
        ShareAttractionFragment fragment = new ShareAttractionFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Bundle args = getArguments();
        if (args != null) {
            text = args.getString(ARG_TEXT);
        }
        context = getContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        final AttractionTabsActivity activity = (AttractionTabsActivity) getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = inflater.inflate(R.layout.fragment_share_attraction, null); // TODO MAKE NEW LAYOUT

        final EditText message = (EditText) view.findViewById(R.id.message);
        message.setText(text);

        builder.setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO POST SHARE
                        Toast.makeText(context, "SHARE ATTRACTION", Toast.LENGTH_SHORT).show();
                        User user = User.getInstance(activity.getSharedPreferences("user", 0));
                        System.out.println(user);
                        user.postInSocialNetwork(message.getText().toString(), new User.Callback() {
                            @Override
                            public void onSuccess(User user) {
                                Toast.makeText(activity, R.string.attraction_share_ok,
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(User user) {
                                Toast.makeText(activity, R.string.attraction_share_error,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(Utils.LOGTAG, "cancel review");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

