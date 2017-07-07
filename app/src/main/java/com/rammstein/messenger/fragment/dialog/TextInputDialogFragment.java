package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rammstein.messenger.R;
import com.rammstein.messenger.util.InputValidator;

import static com.rammstein.messenger.activity.ChatActivity.CHAT_TITLE_INPUT_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.FIRST_NAME_INPUT_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.LAST_NAME_INPUT_DIALOG;

/**
 * Created by user on 27.05.2017.
 */

public class TextInputDialogFragment extends DialogFragment {
    final static String KEY_HINT = "hint";
    final static String KEY_TEXT = "text";
    TextInputLayout mInputContainer;
    TextInputEditText mInput;

    OnClickListener mOnClickListener;

    public static DialogFragment newInstance(String hint, String text){
        DialogFragment fragment = new TextInputDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString(KEY_HINT, hint);
        bundle.putString(KEY_TEXT, text);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof TextInputDialogFragment.OnClickListener)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + TextInputDialogFragment.OnClickListener.class.getSimpleName());
        }

        mOnClickListener = (TextInputDialogFragment.OnClickListener) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.text_input, null);
        mInputContainer = (TextInputLayout) v.findViewById(R.id.til_container);
        mInput = (TextInputEditText) v.findViewById(R.id.tiet_input);
        mInputContainer.setHint(getArguments().getString(KEY_HINT));
        mInput.setText(getArguments().getString(KEY_TEXT));

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                final DialogInterface dialogInterface = dialog;

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (isValid()){
                            mOnClickListener.onOkClicked(getTag(), mInput.getText().toString());
                            dialogInterface.dismiss();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

    private boolean isValid() {
        boolean isValid;
        InputValidator iv = new InputValidator(getContext());
        switch (getTag()){

            case FIRST_NAME_INPUT_DIALOG:
            case LAST_NAME_INPUT_DIALOG:
                isValid = iv.validateName(mInput, mInputContainer);
                break;
            case CHAT_TITLE_INPUT_DIALOG:
                isValid = true;
                break;
            default:
                isValid = false;
                mInputContainer.setError("unknown dialog tag");
                break;
        }
        return isValid;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnClickListener = null;
    }

    public interface OnClickListener {
        void onOkClicked(String tag, String textOutput);
    }
}
