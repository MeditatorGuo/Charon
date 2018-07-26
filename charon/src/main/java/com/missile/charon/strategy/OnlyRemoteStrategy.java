package com.missile.charon.strategy;

import com.missile.charon.cache.ApiCache;
import com.missile.charon.mode.CacheResult;
import rx.Observable;

public class OnlyRemoteStrategy<T> extends CacheStrategy<T> {
    @Override
    public <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz) {
        return loadRemote(apiCache, cacheKey, source);
    }
}
