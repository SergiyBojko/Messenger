package com.rammstein.messenger.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.ContactListAdapter;
import com.rammstein.messenger.adapter.SearchResultsAdapter;
import com.rammstein.messenger.adapter.base.BasicContactsAdapter;
import com.rammstein.messenger.custom_view.RecyclerViewWithEmptyView;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.fragment.dialog.ProfileDialog;
import com.rammstein.messenger.fragment.dialog.SearchFilterDialog;
import com.rammstein.messenger.model.Gender;
import com.rammstein.messenger.model.UserDetails;
import com.rammstein.messenger.repository.TestUserDetailRepository;
import com.rammstein.messenger.util.ListFilter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rammstein.messenger.activity.MainActivity.PROFILE_DIALOG;

/**
 * Created by user on 11.06.2017.
 */

public class SearchActivity extends AppCompatActivity
        implements MenuDialog.OnClickListener, View.OnClickListener, SearchFilterDialog.OnClickListener {
    public final static String USER_MENU_DIALOG = "user_menu_dialog";
    public final static String FILTER_DIALOG = "filter_dialog";
    public final static int UNSPECIFIED = -1;

    @BindView(R.id.rv_list) RecyclerViewWithEmptyView mSearchResultList;
    @BindView(R.id.tv_place_holder) TextView mPlaceHolder;
    SearchView mSearchView;
    ImageView mFilter;

    private BasicContactsAdapter mAdapter;
    private ArrayList<UserDetails> mUserDetailsList;
    private ArrayList<UserDetails> mFilteredList;
    private boolean mIsLocalSearch;
    private int mMinAgeFilter = UNSPECIFIED;
    private int mMaxAgeFilter = UNSPECIFIED;
    private Gender mGenderFilter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mSearchResultList.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultList.setEmptyView(mPlaceHolder);

        mIsLocalSearch = true;
        mUserDetailsList = TestUserDetailRepository.getInstance().getAll();
        mFilteredList = new ArrayList<>(mUserDetailsList);
        mAdapter = new ContactListAdapter(this, mFilteredList);
        mSearchResultList.setAdapter(mAdapter);
    }

    private void filterDataSet(String query) {
        mFilteredList.clear();
        ArrayList<UserDetails> result = ListFilter.filterUserDetails(mUserDetailsList, query);
        result = ListFilter.filterUserDetails(result, mMinAgeFilter, mMaxAgeFilter);
        result = ListFilter.filterUserDetails(result, mGenderFilter);
        mFilteredList.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(Intent.ACTION_SEARCH)){
            String query = intent.getExtras().getString(SearchManager.QUERY);
            performSearch(query);
            if (mIsLocalSearch){
                mIsLocalSearch = false;
                mAdapter = new SearchResultsAdapter(this, mFilteredList);
                mSearchResultList.setAdapter(mAdapter);
            }
        }
    }

    private void performSearch(String query){
        mFilteredList = new ArrayList<>();
        mFilteredList.add(new UserDetails(100, "David", "McDerpface"));
        //TODO
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        View v = menu.findItem(R.id.search).getActionView();
        if (v != null){
            mSearchView = (SearchView) v.findViewById(R.id.search_view);
            mFilter = (ImageView) v.findViewById(R.id.iv_filter);
        }

        initSearchView();
        mFilter.setOnClickListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    private void initSearchView() {

        mSearchView.setIconifiedByDefault(false);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.requestFocus();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mIsLocalSearch){
                    filterDataSet(newText);
                }

                return false;
            }
        });
    }

    @Override
    public void onOptionSelected(String tag, int optionId, int itemId) {
        switch (tag){
            case USER_MENU_DIALOG:

                switch (optionId){
                    case R.string.show_information:
                        DialogFragment dialog = ProfileDialog.newInstance(itemId);
                        dialog.show(getSupportFragmentManager(), PROFILE_DIALOG);
                        break;
                    case R.string.add_to_contacts:
                        //TODO
                        break;
                }

                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.iv_filter:
                DialogFragment dialog = SearchFilterDialog.newInstance(mMinAgeFilter, mMaxAgeFilter, mGenderFilter);
                dialog.show(getSupportFragmentManager(), FILTER_DIALOG);
        }
    }

    @Override
    public void onOkClicked(int minAge, int maxAge, Gender gender) {
        mMinAgeFilter = minAge;
        mMaxAgeFilter = maxAge;
        mGenderFilter = gender;
        String query =  mSearchView.getQuery().toString();
        if (mIsLocalSearch){
            filterDataSet(query);
        }
        if (!query.isEmpty()){
            performSearch(query);
        }
    }
}
