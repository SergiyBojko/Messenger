package com.rammstein.messenger.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by user on 06.06.2017.
 */

public class MenuDialog extends DialogFragment {
    private final static String ITEM_IDS = "item_ids";
    private final static String SELECTED_ITEM = "selected_item";
    private final static String DIALOG_TITLE = "dialog_title";

    private OnClickListener mOnClickListener;

    public static DialogFragment newInstance(int[] itemResIds, int itemId, String title){
        Bundle args = new Bundle();
        args.putIntArray(ITEM_IDS, itemResIds);
        args.putInt(SELECTED_ITEM, itemId);
        args.putString(DIALOG_TITLE, title);

        MenuDialog fragment = new MenuDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int[] itemIds = getArguments().getIntArray(ITEM_IDS);
        final int item = getArguments().getInt(SELECTED_ITEM);
        String title = getArguments().getString(DIALOG_TITLE);
        CharSequence[] items = new CharSequence[itemIds.length];
        for(int i = 0; i < itemIds.length; i++){
            items[i] = getContext().getResources().getString(itemIds[i]);
        }
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnClickListener.onOptionSelected(getTag(), itemIds[which], item);
                    }
                }).create();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof MenuDialog.OnClickListener)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + MenuDialog.OnClickListener.class.getSimpleName());
        }

        mOnClickListener = (MenuDialog.OnClickListener) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnClickListener = null;
    }

    public interface OnClickListener {
        void onOptionSelected(String tag, int optionId, int itemId);
    }
}
