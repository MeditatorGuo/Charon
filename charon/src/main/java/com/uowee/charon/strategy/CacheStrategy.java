package com.uowee.charon.strategy;

import com.uowee.charon.cache.CharonCache;
import com.uowee.charon.mode.CacheResult;
import com.uowee.charon.utils.JsonUtils;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by GuoWee on 2018/1/10.
 */

public abstract class CacheStrategy<T> implements ICacheStrategy<T> {

    <T> Observable<CacheResult<T>> loadCache(final CharonCache apiCache, final String key, final Class<T>
            clazz) {
        return apiCache.<T>get(key).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s != null;
            }
        }).map(new Func1<String, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(String s) {
                T t = JsonUtils.gson().fromJson(s, clazz);
                return new CacheResult<T>(true, t);
            }
        });
    }

    <T> Observable<CacheResult<T>> loadRemote(final CharonCache apiCache, final String key, Observable<T> source) {
        return source.map(new Func1<T, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(T t) {
                apiCache.put(key, t).subscribeOn(Schedulers.io()).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean status) {
                    }
                });
                return new CacheResult<T>(false, t);
            }
        });
    }
}
