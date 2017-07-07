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
 * Created by user on 04.07.2017.
 */

public class Confirmation2ChoicesDialog extends DialogFragment{
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String CHOICE = "choice";

    private OnClickListener mOnClickListener;

    public static Confirmation2ChoicesDialog newInstance(String title, String text, String choice) {

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        args.putString(CHOICE, choice);
        Confirmation2ChoicesDialog fragment = new Confirmation2ChoicesDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof OnClickListener)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + OnClickListener.class.getSimpleName());
        }

        mOnClickListener = (OnClickListener) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String text = getArguments().getString(TEXT);
        String choice = getArguments().getString(CHOICE);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnClickListener.onConfirm(getTag());
                    }
                })
                .setNeutralButton(choice, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnClickListener.onChoiceSelected(getTag());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();



        return dialog;
    }

    public interface OnClickListener {
        public void onConfirm (String tag);
        public void onChoiceSelected(String tag);
    }
}
