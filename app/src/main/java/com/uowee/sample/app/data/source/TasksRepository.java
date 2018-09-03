package com.uowee.sample.app.data.source;

import java.util.List;

/**
 * Created by GuoWee on 2018/7/15.
 */

public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;
    private final TasksDataSource mTasksRemoteDataSource;

    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource) {

        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource);
        }
        return INSTANCE;
    }

    private TasksRepository(TasksDataSource tasksRemoteDataSource) {
        this.mTasksRemoteDataSource = tasksRemoteDataSource;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void getTopMovie(String host, int start, int offset, final Callback callback) {
        mTasksRemoteDataSource.getTopMovie(host, start, offset, new Callback() {
            @Override
            public void onSuccess(List t) {
                callback.onSuccess(t);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


}
