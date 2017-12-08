package com.uowee.charon.cache;

/**
 * Created by Sim.G on 2017/12/8.
 */

public interface ICache {

    void put(String key, Object value);

    Object get(String key);

    boolean contains(String key);

    void remove(String key);

    void clear();
}
