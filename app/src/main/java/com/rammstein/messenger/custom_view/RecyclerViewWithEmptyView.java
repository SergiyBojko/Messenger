package com.rammstein.messenger.custom_view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by user on 14.06.2017.
 */

public class RecyclerViewWithEmptyView extends RecyclerView {
    private View mEmptyView;

    private AdapterDataObserver mEmptyObserver = new AdapterDataObserver() {


        @Override
        public void onChanged() {
            Adapter<?> adapter =  getAdapter();
            if(adapter != null && mEmptyView != null) {
                if(adapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    RecyclerViewWithEmptyView.this.setVisibility(View.GONE);
                }
                else {
                    mEmptyView.setVisibility(View.GONE);
                    RecyclerViewWithEmptyView.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    public RecyclerViewWithEmptyView(Context context) {
        super(context);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerAdapterDataObserver(mEmptyObserver);
        }

        mEmptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }
}
