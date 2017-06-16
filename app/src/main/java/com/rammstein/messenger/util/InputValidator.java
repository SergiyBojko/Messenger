package com.rammstein.messenger.util;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

import com.rammstein.messenger.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 21.05.2017.
 */

public class InputValidator{
    private final static String NOT_A_WORD_CHAR = "[\\W]";
    private final static String FORBIDDEN_NAME_CHARS = "[^\\w'-]|[0-9]";
    private final static String SPACE_CHAR = "[\\s]";
    private Context mContext;

    public InputValidator (Context context){
        mContext = context;
    }

    public boolean validateUsername(TextInputEditText et) {
        String input = et.getText().toString();
        input = input.trim();
        if (input.length() > 20 || input.length() < 3){
            et.setError(mContext.getString(R.string.incorrect_username_length));
            return false;
        }
        Pattern p = Pattern.compile(NOT_A_WORD_CHAR);
        Matcher m = p.matcher(input);
        if (m.find()){
            et.setError(String.format(mContext.getString(R.string.char_x_not_allowed), m.group()));
            return false;
        }
        return true;
    }

    public boolean validateName(TextInputEditText et)
    {
        return validateName(et, null);
    }

    public boolean validateName(TextInputEditText et, TextInputLayout container)
    {
        String input = et.getText().toString();
        if (input.length() == 0){
            return true;
        }
        if (input.length() > 30){
            if (container != null) {
                container.setError(mContext.getString(R.string.name_field_too_big));
            } else {
                et.setError(mContext.getString(R.string.name_field_too_big));
            }

            return false;
        }
        Pattern p = Pattern.compile(FORBIDDEN_NAME_CHARS);
        Matcher m = p.matcher(input);
        if (m.find()){
            if (container != null) {
                container.setError(String.format(mContext.getString(R.string.char_x_not_allowed), m.group()));
            } else {
                et.setError(String.format(mContext.getString(R.string.char_x_not_allowed), m.group()));
            }

            return false;
        }

        return true;
    }

    public boolean validatePassword(TextInputEditText et) {
        String input = et.getText().toString();
        if (input.length() > 20 || input.length() < 6){
            et.setError(mContext.getString(R.string.incorrect_password_length));
            return false;
        }
        Pattern p = Pattern.compile(SPACE_CHAR);
        Matcher m = p.matcher(input);
        if (m.find()){
            et.setError(String.format(mContext.getString(R.string.char_x_not_allowed), m.group()));
            return false;
        }
        return true;
    }

    public boolean validateDateOfBirth(long timeInMillis, TextInputEditText et) {
        if (timeInMillis > System.currentTimeMillis()){
            et.setError(mContext.getString(R.string.incorrect_date_of_birth));
            return false;
        }
        return true;
    }
}
