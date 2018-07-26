package com.missile.charon.cache;

/**
 * Created by GuoWee on 2018/6/26.
 */

public interface ICache {
    void put(String key, Object value);

    Object get(String key);

    boolean contains(String key);

    void remove(String key);

    void clear();

}
