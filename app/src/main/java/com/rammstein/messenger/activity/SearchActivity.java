package com.rammstein.messenger.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;


import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.ContactListAdapter;
import com.rammstein.messenger.adapter.SearchResultsAdapter;
import com.rammstein.messenger.adapter.base.BasicContactsAdapter;
import com.rammstein.messenger.custom_view.RecyclerViewWithEmptyView;
import com.rammstein.messenger.fragment.dialog.ConfirmationDialog;
import com.rammstein.messenger.fragment.dialog.ConnectionDialog;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.fragment.dialog.NotificationDialog;
import com.rammstein.messenger.fragment.dialog.ProfileDialog;
import com.rammstein.messenger.fragment.dialog.SearchFilterDialog;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Gender;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.util.ListFilter;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rammstein.messenger.activity.MainActivity.ACTION_UPDATE_VIEW;
import static com.rammstein.messenger.activity.MainActivity.CONTACT_MENU_DIALOG;
import static com.rammstein.messenger.activity.MainActivity.PROFILE_DIALOG;

/**
 * Created by user on 11.06.2017.
 */

public class SearchActivity extends AppCompatActivity
        implements MenuDialog.OnClickListener, View.OnClickListener, SearchFilterDialog.OnClickListener {
    public final static String USER_MENU_DIALOG = "user_menu_dialog";
    public final static String FILTER_DIALOG = "filter_dialog";
    public final static String CONNECTION_DIALOG = "connection_dialog";
    public final static int UNSPECIFIED = -1;

    @BindView(R.id.rv_list) RecyclerViewWithEmptyView mSearchResultList;
    @BindView(R.id.tv_place_holder) TextView mPlaceHolder;
    SearchView mSearchView;
    ImageView mFilter;

    private BasicContactsAdapter mAdapter;
    private RealmResults<UserDetails> mContactList;
    private ArrayList<UserDetails> mFilteredList;
    private boolean mIsLocalSearch;
    private int mMinAgeFilter = UNSPECIFIED;
    private int mMaxAgeFilter = UNSPECIFIED;
    private Gender mGenderFilter;
    BroadcastReceiver mReceiver;
    private Callback<List<UserDetailsResponse>> mSearchCallback;
    private Callback<Void> mAddToFriendsCallback;
    private int mSelectedUserId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mContactList = RealmHelper.getSortedContactList();
        mSearchResultList.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultList.setEmptyView(mPlaceHolder);
        mIsLocalSearch = true;
        initRetrofitCallbacks();

    }

    private void initRetrofitCallbacks() {
        mSearchCallback = new Callback<List<UserDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<UserDetailsResponse>> call, Response<List<UserDetailsResponse>> response) {
                AppUser appUser = RealmHelper.getCurrentUser();
                if (response.code()/100 == 2){
                    mFilteredList = new ArrayList<>();
                    for (UserDetailsResponse userResponse : response.body()){
                        UserDetails user = new UserDetails(userResponse);
                        if (user.getId() == appUser.getId()){
                            continue;
                        }
                        mFilteredList.add(user);
                    }
                    setSearchData();
                }
            }

            @Override
            public void onFailure(Call<List<UserDetailsResponse>> call, Throwable t) {
                DialogFragment alert = NotificationDialog.newInstance(getResources().getString(R.string.internet_not_available), null);
                alert.show(getSupportFragmentManager(), "internet_not_available");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_VIEW);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mIsLocalSearch){
                    setLocalSearchData();
                } else {
                    setSearchData();
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);

        if (mIsLocalSearch){
            setLocalSearchData();
        } else {
            setSearchData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void filterDataSet(String query) {
        mFilteredList.clear();
        ArrayList<UserDetails> result = ListFilter.filterUserDetails(new ArrayList<>(mContactList), query);
        result = ListFilter.filterUsersByAge(result, mMinAgeFilter, mMaxAgeFilter);
        result = ListFilter.filterUsersByGender(result, mGenderFilter);
        mFilteredList.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(Intent.ACTION_SEARCH)){
            if (InternetHelper.isConnected()){
                String query = intent.getExtras().getString(SearchManager.QUERY);
                performSearch(query);
            } else {
                DialogFragment alert = NotificationDialog.newInstance(getResources().getString(R.string.internet_not_available), null);
                alert.show(getSupportFragmentManager(), "internet_not_available");
            }
        }
    }

    private void setLocalSearchData() {
        mFilteredList = new ArrayList<>(mContactList);
        if (mAdapter == null){
            mAdapter = new ContactListAdapter(this, mFilteredList);
            mSearchResultList.setAdapter(mAdapter);
        } else {
            mAdapter.setData(mFilteredList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setSearchData() {
        Log.i("searchActivity", "setSearchData");
        if (mIsLocalSearch){
            mIsLocalSearch = false;
            mAdapter = new SearchResultsAdapter(this, mFilteredList);
            mSearchResultList.setAdapter(mAdapter);
        } else {
            mAdapter.setData(mFilteredList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void performSearch(String query){
        RetrofitHelper helper = new RetrofitHelper();
        Map<String, String> filters = new HashMap<>();
        filters.put("Name", query);
        if (mGenderFilter != null){
            filters.put("Gender", mGenderFilter.name());
        }
        if (mMaxAgeFilter != UNSPECIFIED){
            filters.put("MaxAge", Integer.toString(mMaxAgeFilter));
        }
        if (mMinAgeFilter != UNSPECIFIED){
            filters.put("MinAge", Integer.toString(mMinAgeFilter));
        }
        helper.getAllUsers(filters, mSearchCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("searchActivity", "create options menu");
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
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
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
        //apply filter if activity created with non empty query
        filterDataSet(mSearchView.getQuery().toString());
    }

    @Override
    public void onOptionSelected(String tag, int optionId, int itemId, int itemIndex) {
        switch (tag){
            case CONTACT_MENU_DIALOG:
                RetrofitHelper retrofitHelper = new RetrofitHelper(this);
                UserDetails user = mFilteredList.get(itemIndex);
                switch (optionId){
                    case R.string.show_information:
                        DialogFragment dialog = ProfileDialog.newInstance(itemId);
                        dialog.show(getSupportFragmentManager(), PROFILE_DIALOG);
                        break;
                    case R.string.add_to_contacts:
                        retrofitHelper.addToContacts(this, user.getId());
                        break;
                    case R.string.delete_contact:
                        retrofitHelper.removeFromContacts(this, user.getId());
                        break;
                    case R.string.show_connection:
                        DialogFragment connDialog = ConnectionDialog.newInstance(itemId, mFilteredList.get(itemIndex).getName());
                        connDialog.show(getSupportFragmentManager(), CONNECTION_DIALOG);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
