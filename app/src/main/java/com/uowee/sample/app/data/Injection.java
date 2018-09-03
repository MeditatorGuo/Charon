package com.uowee.sample.app.data;


import android.content.Context;

import com.uowee.sample.app.data.source.TasksRepository;
import com.uowee.sample.app.data.source.remote.TasksRemoteDataSource;
import com.uowee.sample.app.domain.MovieTask;

public class Injection {

    public static TasksRepository provideTasksRepository(Context context) {
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance());
    }

    public static MovieTask provideMovieTask(Context context) {
        return new MovieTask(provideTasksRepository(context));
    }

}
