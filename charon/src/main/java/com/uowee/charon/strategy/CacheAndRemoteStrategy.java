package com.uowee.charon.strategy;

import com.uowee.charon.cache.CharonCache;
import com.uowee.charon.mode.CacheResult;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by GuoWee on 2018/1/10.
 */

public class CacheAndRemoteStrategy<T> extends CacheStrategy<T> {

    @Override
    public <T> Observable<CacheResult<T>> execute(CharonCache apiCache, String cacheKey, Observable<T> source, final Class<T> clazz) {
        Observable<CacheResult<T>> cache = loadCache(apiCache, cacheKey, clazz);
        final Observable<CacheResult<T>> remote = loadRemote(apiCache, cacheKey, source);
        return Observable.concat(cache, remote).filter(new Func1<CacheResult<T>, Boolean>() {
            @Override
            public Boolean call(CacheResult<T> result) {
                return result.getCacheData() != null;
            }
        });
    }
}
