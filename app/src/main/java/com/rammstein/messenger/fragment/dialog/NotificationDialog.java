package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.rammstein.messenger.R;

/**
 * Created by user on 30.06.2017.
 */

public class NotificationDialog extends DialogFragment {
    private final static String TITLE = "title";
    private final static String TEXT = "text";

    public static NotificationDialog newInstance(String title, String text) {

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        NotificationDialog fragment = new NotificationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String text = getArguments().getString(TEXT);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(R.string.ok, null)
                .create();
        return dialog;
    }
}
