package com.uowee.sample.app.data.source;


import java.util.List;

public interface TasksDataSource {

    interface Callback<T> {
        void onSuccess(List<T> t);

        void onFailure(Exception e);
    }

    void getTopMovie(String host, int start, int offset, Callback callback);
}
