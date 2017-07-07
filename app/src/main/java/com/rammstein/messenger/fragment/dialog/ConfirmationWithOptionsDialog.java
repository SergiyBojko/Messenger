package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;

/**
 * Created by user on 24.06.2017.
 */

public class ConfirmationWithOptionsDialog extends DialogFragment {
    private static final String TITLE = "title";
    private static final String OPTIONS = "options";

    private OnClickListener mOnClickListener;

    public static ConfirmationWithOptionsDialog newInstance(String title, int[] optionIds) {

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putIntArray(OPTIONS, optionIds);
        ConfirmationWithOptionsDialog fragment = new ConfirmationWithOptionsDialog();
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
        final int[] optionIds = getArguments().getIntArray(OPTIONS);
        final LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        for (int optionId : optionIds){
            String optionTitle = getActivity().getResources().getString(optionId);
            View v = getActivity().getLayoutInflater().inflate(R.layout.checkbox_layout, null);
            v.setId(optionId);
            TextView optionLabel = (TextView) v.findViewById(R.id.text);
            optionLabel.setText(optionTitle);
            ll.addView(v);
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(ll)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle result = new Bundle();
                        for (int optionId : optionIds){
                            View v = ll.findViewById(optionId);
                            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);
                            result.putBoolean(Integer.toString(optionId), checkBox.isChecked());
                        }
                        mOnClickListener.onConfirm(getTag(), result);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        return dialog;
    }

    public interface OnClickListener{
        void onConfirm (String tag, Bundle args);
    }
}
