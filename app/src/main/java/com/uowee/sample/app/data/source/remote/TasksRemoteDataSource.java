package com.uowee.sample.app.data.source.remote;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.missile.charon.Charon;
import com.missile.charon.callback.ApiCallback;
import com.missile.charon.convert.GsonConverterFactory;
import com.missile.charon.exception.ApiException;
import com.uowee.sample.app.App;
import com.uowee.sample.app.data.source.TasksDataSource;
import com.uowee.sample.app.domain.model.MovieModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by GuoWee on 2018/7/15.
 */

public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE = null;

    private TasksRemoteDataSource() {
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }


    @Override
    public void getTopMovie(String host, int start, int offset, final Callback callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.clear();
        parameters.put("start", start);
        parameters.put("count", offset);

        Log.e("TAG", "-----start: " + start + ", offset: " + offset + " -----");
        Charon charon = new Charon.Builder(App.getInstance()).baseUrl("https://api.douban.com/v2/")
                .converterFactory(GsonConverterFactory.create(new GsonBuilder().create())).build();

        charon.get(host, parameters, new ApiCallback<MovieModel>() {

            @Override
            public void onStart() {
                Log.e("TAG", "-----START-----");
            }

            @Override
            public void onError(ApiException e) {
                Log.e("TAG", "-----ERROR-----");
                callback.onFailure(e);
            }

            @Override
            public void onCompleted() {
                Log.e("TAG", "-----COMPLETE-----");
            }

            @Override
            public void onNext(MovieModel response) {
                Log.e("TAG", "-----NEXT-----");
                List<MovieModel.SubjectsBean> subjectsBeanList = response.getSubjects();
                callback.onSuccess(subjectsBeanList);
            }
        });
    }

}
