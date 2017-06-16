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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Spinner;

import com.rammstein.messenger.R;
import com.rammstein.messenger.activity.SearchActivity;
import com.rammstein.messenger.adapter.SpinnerWithHeaderAdapter;
import com.rammstein.messenger.model.Gender;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rammstein.messenger.activity.SearchActivity.UNSPECIFIED;

/**
 * Created by user on 14.06.2017.
 */

public class SearchFilterDialog extends DialogFragment {
    private final static String MAX_AGE = "max_age";
    private final static String MIN_AGE = "min_age";
    private final static String GENDER_INDEX = "gender";

    @BindView(R.id.sp_gender) Spinner mGenderSpinner;
    @BindView(R.id.tiet_min_age) TextInputEditText mMinAgeET;
    @BindView(R.id.tiet_max_age) TextInputEditText mMaxAgeET;
    @BindView(R.id.til_min_age) TextInputLayout mMinAgeTILayout;
    @BindView(R.id.til_max_age) TextInputLayout mMaxAgeTILayout;



    OnClickListener mOnClickListener;

    public static SearchFilterDialog newInstance(int minAge, int maxAge, Gender gender) {

        Bundle args = new Bundle();
        args.putInt(MAX_AGE, maxAge);
        args.putInt(MIN_AGE, minAge);
        if (gender != null){
            args.putInt(GENDER_INDEX, gender.ordinal());
        }

        SearchFilterDialog fragment = new SearchFilterDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof SearchFilterDialog.OnClickListener)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + SearchFilterDialog.OnClickListener.class.getSimpleName());
        }

        mOnClickListener = (SearchFilterDialog.OnClickListener) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int minAge = getArguments().getInt(MIN_AGE, UNSPECIFIED);
        int maxAge = getArguments().getInt(MAX_AGE, UNSPECIFIED);
        int genderIndex = getArguments().getInt(GENDER_INDEX, UNSPECIFIED);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_search_filter, null);
        ButterKnife.bind(this, v);

        initGenderSpinner(genderIndex);
        initAgeInput(minAge, maxAge);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.filter)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Gender gender = null;
                        int minAge;
                        int maxAge;
                        try {
                            minAge = Integer.parseInt(mMinAgeET.getText().toString());
                        } catch (NumberFormatException e){
                            minAge = UNSPECIFIED;
                        }

                        try {
                            maxAge = Integer.parseInt(mMaxAgeET.getText().toString());
                        } catch (NumberFormatException e){
                            maxAge = UNSPECIFIED;
                        }

                        int genderIndex = mGenderSpinner.getSelectedItemPosition();
                        if (genderIndex > 0){
                            gender = Gender.values()[genderIndex-1];
                        }

                        mOnClickListener.onOkClicked(minAge, maxAge, gender);
                    }
                })
                .create();
        return dialog;
    }

    private void initAgeInput(int minAge, int maxAge) {

        mMinAgeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int minAge = Integer.parseInt(s.toString());
                    int maxAge = Integer.parseInt(mMaxAgeET.getText().toString());
                    if (minAge > maxAge){
                        mMaxAgeET.setText(Integer.toString(minAge));
                    }
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMaxAgeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int maxAge = Integer.parseInt(s.toString());
                    int minAge = Integer.parseInt(mMinAgeET.getText().toString());
                    if (maxAge < minAge){
                        mMinAgeET.setText(Integer.toString(maxAge));
                    }
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (minAge != UNSPECIFIED){
            mMinAgeET.setText(Integer.toString(minAge));
        }

        if (maxAge != UNSPECIFIED){
            mMaxAgeET.setText(Integer.toString(maxAge));
        }
    }

    private void initGenderSpinner(int genderIndex) {
        String header = getResources().getString(R.string.gender);
        ArrayList<String> genderList = new ArrayList<>();
        for (Gender gender : Gender.values()){
            String g = getResources().getString(gender.getTextResId());
            genderList.add(g);
        }
        String[] genders = genderList.toArray(new String[genderList.size()]);
        SpinnerWithHeaderAdapter spinnerAdapter = new SpinnerWithHeaderAdapter(getActivity(), header, R.color.transparentGrey, genders);
        mGenderSpinner.setAdapter(spinnerAdapter);

        if (genderIndex != UNSPECIFIED){
            mGenderSpinner.setSelection(genderIndex+1);
        }
    }

    public interface OnClickListener{
        void onOkClicked(int minAge, int maxAge, Gender gender);
    }
}
