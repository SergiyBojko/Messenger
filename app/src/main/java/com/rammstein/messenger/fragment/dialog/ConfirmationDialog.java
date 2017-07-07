package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.rammstein.messenger.R;

/**
 * Created by user on 24.06.2017.
 */

public class ConfirmationDialog extends DialogFragment {
    private static final String TITLE = "title";
    private static final String TEXT = "text";

    private OnClickListener mOnClickListener;

    public static ConfirmationDialog newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmationDialog newInstance(String title, String text) {
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof ConfirmationDialog.OnClickListener)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + ConfirmationDialog.OnClickListener.class.getSimpleName());
        }

        mOnClickListener = (ConfirmationDialog.OnClickListener) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String text = getArguments().getString(TEXT);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnClickListener.onConfirm(getTag());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        return dialog;
    }

    public interface OnClickListener {
        public void onConfirm (String tag);
    }
}
