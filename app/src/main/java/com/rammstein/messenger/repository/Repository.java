package com.rammstein.messenger.repository;

import com.rammstein.messenger.model.core.Entity;

import java.util.ArrayList;

/**
 * Created by user on 12.05.2017.
 */

public interface Repository<T extends Entity> {
    public void add(T t);
    public T get (int index);
    public T getById (int id);
    public ArrayList<T> getAll();
    public void remove (T t);
    public void update (T t);
}
