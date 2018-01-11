package com.uowee.charon.cache;

import android.content.Context;

import com.uowee.charon.Charon;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Sim.G on 2017/12/8.
 */

public class CharonCache {

    private final DiskCache diskCache;
    private String cacheKey;

    private CharonCache(Context context, String cacheKey, long time) {
        this.cacheKey = cacheKey;
        this.diskCache = new DiskCache(context).setCacheTime(time);
    }

    private CharonCache(Context context, File diskDir, long diskMaxSize, String cacheKey, long time) {
        this.cacheKey = cacheKey;
        diskCache = new DiskCache(context, diskDir, diskMaxSize).setCacheTime(time);
    }

    public Observable<String> get(final String key) {
        return Observable.create(new SimpleSubscribe<String>() {
            @Override
            String execute() throws Throwable {
                return diskCache.get(key);
            }
        });
    }

    public <T> Observable<Boolean> put(final String key, final T value) {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                diskCache.put(key, value);
                return true;
            }
        });
    }

    public boolean containsKey(final String key) {
        return diskCache.contains(key);
    }

    public void remove(final String key) {
        diskCache.remove(key);
    }

    public Subscription clear() {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                diskCache.clear();
                return true;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean status) {
            }
        });
    }



    
    private static abstract class SimpleSubscribe<T> implements Observable.OnSubscribe<T> {
        @Override
        public void call(Subscriber<? super T> subscriber) {
            try {
                T data = execute();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(data);
                }
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
                return;
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }

        abstract T execute() throws Throwable;
    }


    public static final class Builder {
        private final Context context;
        private File diskDir;
        private long diskMaxSize;
        private long cacheTime = DiskCache.CACHE_NEVER_EXPIRE;
        private String cacheKey;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Context context, File diskDir, long diskMaxSize) {
            this.context = context;
            this.diskDir = diskDir;
            this.diskMaxSize = diskMaxSize;
        }
        public Builder cacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        public Builder cacheTime(long cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        public CharonCache build() {
            if (diskDir == null || diskMaxSize == 0) {
                return new CharonCache(context, cacheKey, cacheTime);
            } else {
                return new CharonCache(context, diskDir, diskMaxSize, cacheKey, cacheTime);
            }
        }



    }
}
