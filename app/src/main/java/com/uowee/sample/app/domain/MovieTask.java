package com.uowee.sample.app.domain;

import com.uowee.sample.app.clean.UseCase;
import com.uowee.sample.app.data.source.TasksDataSource;
import com.uowee.sample.app.data.source.TasksRepository;
import com.uowee.sample.app.domain.model.MovieModel;

import java.util.List;


public class MovieTask extends UseCase<MovieTask.RequestValues, MovieTask.ResponseValues> {
    private TasksRepository mTasksRepository;

    public MovieTask(TasksRepository repository) {
        this.mTasksRepository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        String host = requestValues.getHost();
        int start = requestValues.getStart();
        int offset = requestValues.getOffset();

        mTasksRepository.getTopMovie(host, start, offset, new TasksDataSource.Callback() {
            @Override
            public void onSuccess(List t) {
                List<MovieModel.SubjectsBean> list = t;
                ResponseValues responseValues = new ResponseValues(list);
                getUseCaseCallback().onSuccess(responseValues);
            }

            @Override
            public void onFailure(Exception e) {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private String host;
        private int start;
        private int offset;

        public RequestValues(String host, int start, int offset) {
            this.host = host;
            this.start = start;
            this.offset = offset;
        }

        public String getHost() {
            return host;
        }

        public int getStart() {
            return start;
        }

        public int getOffset() {
            return offset;
        }
    }

    public static final class ResponseValues implements UseCase.ResponseValue {
        private List<MovieModel.SubjectsBean> mMovies;

        public ResponseValues(List<MovieModel.SubjectsBean> movies) {
            this.mMovies = movies;
        }

        public List<MovieModel.SubjectsBean> getMovies() {
            return mMovies;
        }
    }
}
