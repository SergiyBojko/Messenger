package com.rammstein.messenger.repository;

import android.app.Activity;

import com.rammstein.messenger.model.Gender;
import com.rammstein.messenger.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by user on 12.05.2017.
 */

public class TestUserDetailRepository implements Repository<UserDetails> {

    ArrayList<UserDetails> mUserDetails = new ArrayList<>();
    private static TestUserDetailRepository mInstance = new TestUserDetailRepository();

    public TestUserDetailRepository (){
        long oneYear = 1000L*60*60*24*365;
        long now = System.currentTimeMillis();
        mUserDetails.add(new UserDetails(8, "Ann", "Green", now-oneYear*20, Gender.FEMALE));
        mUserDetails.add(new UserDetails(7, "Alex", "White", now-oneYear*30, Gender.MALE ));
        mUserDetails.add(new UserDetails(6, "Carl", "Black", now-oneYear*25, Gender.MALE ));
        mUserDetails.add(new UserDetails(5, "Dave", "Black", now-oneYear*32, Gender.MALE ));
        mUserDetails.add(new UserDetails(4, "Dan", "Green", now-oneYear*11, Gender.MALE ));
        mUserDetails.add(new UserDetails(3, "Frank", "Black", now-oneYear*15, Gender.MALE ));
        mUserDetails.add(new UserDetails(2, "Felix", "Green", now-oneYear*33, Gender.MALE ));
        mUserDetails.add(new UserDetails(1, "Harry", "Black", now-oneYear*44, Gender.MALE ));
    }

    public static Repository<UserDetails> getInstance() {
        return mInstance;
    }

    @Override
    public void add(UserDetails userDetails) {

    }

    @Override
    public UserDetails get(int index) {
        return mUserDetails.get(index);
    }

    @Override
    public UserDetails getById(int id) {
        for (UserDetails user: mUserDetails) {
            if (user.getId() == id){
                return user;
            }
        }
        return null;
    }

    @Override
    public ArrayList<UserDetails> getAll() {
        return mUserDetails;
    }

    @Override
    public void remove(UserDetails userDetails) {

    }

    @Override
    public void update(UserDetails userDetails) {

    }
}
