package com.uowee.charon.strategy;

import com.uowee.charon.cache.CharonCache;
import com.uowee.charon.mode.CacheResult;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by GuoWee on 2018/1/10.
 */

public class FirstRemoteStrategy<T> extends CacheStrategy<T> {

    @Override
    public <T> Observable<CacheResult<T>> execute(CharonCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz) {

        Observable<CacheResult<T>> remote = loadRemote(apiCache, cacheKey, source);
        remote.onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(Throwable throwable) {
                return null;
            }
        });
        Observable<CacheResult<T>> cache = loadCache(apiCache, cacheKey, clazz);
        return Observable.concat(cache, cache).firstOrDefault(null, new Func1<CacheResult<T>, Boolean>() {
            @Override
            public Boolean call(CacheResult<T> tResultData) {
                return tResultData != null && tResultData.getCacheData() != null;
            }
        });
    }
}
