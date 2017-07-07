package com.rammstein.messenger.repository;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by user on 19.06.2017.
 */

public class RealmRepository {

    private final static String TAG = "RealmRepository";
    private final static String ID = "mId";


    private static RealmRepository sInstance;
    private Realm mRealm;

    private RealmRepository(Context context){
        buildConfiguration(context);
        initRealm();
        getRealm().close();
    }

    private void buildConfiguration(Context context) {
        Realm.init(context);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private void initRealm(){
        mRealm = Realm.getDefaultInstance();
    }

    public static void createInstance(Context context){
        if (sInstance == null){
            sInstance = new RealmRepository(context);
        }
    }

    public Realm getRealm(){
        if (mRealm.isClosed()){
            Realm realm = Realm.getDefaultInstance();
            mRealm = realm;
            return realm;
        }
        return mRealm;
    }

    public Realm getRealmInstance(){
        return Realm.getDefaultInstance();
    }

    public static RealmRepository getInstance(){
        if (sInstance != null){
            return sInstance;
        }
        return null;
    }

    public void beginTransaction(){
        Realm realm = getRealm();
        realm.beginTransaction();
    }

    public void commitTransaction(){
        Realm realm = getRealm();
        realm.commitTransaction();
    }

    public void closeRealm() {
        if (!getRealm().isClosed()) {
            getRealm().close();
        }
    }

    public <T extends RealmObject> T add(T t) {
        T managedObj;

        if (hasPrimaryKey(t.getClass())){
            managedObj = getRealm().copyToRealmOrUpdate(t);
        } else {
            managedObj = getRealm().copyToRealm(t);
        }

        return managedObj;
    }

    public <T extends RealmObject> T getById(Class<T> clazz, int id) {

        if (hasPrimaryKey(clazz)){
            return getRealm().where(clazz).equalTo(ID, id).findFirst();
        }

        return null;
    }

    public <T extends RealmObject> RealmResults<T> getAll(Class<T> clazz) {
        return getRealm().where(clazz).findAll();
    }

    public <T extends RealmObject> void remove(T t) {
        t.deleteFromRealm();
    }

    private <T extends RealmObject> boolean hasPrimaryKey(Class<T> clazz) {
        return getRealm().getSchema().get(clazz.getSimpleName()).hasPrimaryKey();
    }
}
