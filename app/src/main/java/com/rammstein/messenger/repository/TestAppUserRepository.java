package com.rammstein.messenger.repository;

import com.rammstein.messenger.model.AppUser;
import com.rammstein.messenger.model.Gender;
import com.rammstein.messenger.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by user on 26.05.2017.
 */

public class TestAppUserRepository implements Repository<AppUser> {

    AppUser mAppUser;
    private static TestAppUserRepository mInstance = new TestAppUserRepository();



    public TestAppUserRepository(){
        UserDetails ud = new UserDetails(1, "firtname", "lastname");
        ud.setBirthday(System.currentTimeMillis()-(long)1000*60*60*24*50);
        ud.setGender(Gender.OTHER);
        mAppUser = new AppUser(ud);
    }

    public static Repository getInstance() {
        return mInstance;
    }

    @Override
    public void add(AppUser appUser) {

    }

    @Override
    public AppUser get(int id) {
        return mAppUser;
    }

    @Override
    public AppUser getById(int id) {
        return null;
    }

    @Override
    public ArrayList<AppUser> getAll() {
        return null;
    }

    @Override
    public void remove(AppUser appUser) {

    }

    @Override
    public void update(AppUser appUser) {

    }
}
