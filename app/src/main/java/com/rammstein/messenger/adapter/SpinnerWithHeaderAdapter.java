package com.rammstein.messenger.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rammstein.messenger.R;

/**
 * Created by user on 22.05.2017.
 */

public class SpinnerWithHeaderAdapter extends BaseAdapter {

    Context mContext;
    String mHeader;
    String[] mOptions;
    int mHeaderColorRes;

    public SpinnerWithHeaderAdapter(Context context, String header, int headerColorRes, String[] options){
        mContext = context;
        mHeader = header;
        mOptions = options;
        mHeaderColorRes = headerColorRes;
    }

    @Override
    public int getCount() {
        return mOptions.length + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0){
            return mHeader;
        } else {
            return mOptions[position - 1];
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_spinner, null);
        TextView gender = (TextView) v.findViewById(R.id.tv_item_text);
        gender.setPadding(0,0,0,0);
        gender.setText((String)getItem(position));

        if (position == 0){
            gender.setTextColor(mContext.getResources().getColor(mHeaderColorRes));
        }

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_spinner, null);
        TextView gender = (TextView) v.findViewById(R.id.tv_item_text);
        gender.setText((String)getItem(position));

        if (position == 0){
            gender.setTextColor(mContext.getResources().getColor(mHeaderColorRes));
        }

        return v;
    }
}
