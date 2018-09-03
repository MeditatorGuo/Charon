package com.uowee.sample.app.presenter;

import android.app.Activity;

import com.uowee.sample.app.clean.UseCase;
import com.uowee.sample.app.clean.UseCaseHandler;
import com.uowee.sample.app.contract.MovieContract;
import com.uowee.sample.app.data.Injection;
import com.uowee.sample.app.domain.MovieTask;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by GuoWee on 2018/7/17.
 */

public class MoviePresenter implements MovieContract.Presenter {

    private MovieContract.View mMovieView;
    private CompositeSubscription mSubscriptions;
    private Activity activity;

    public MoviePresenter(MovieContract.View view) {
        this.mMovieView = view;
        this.activity = (Activity) view;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {
        mSubscriptions.unsubscribe();
        if (activity != null) {
            activity = null;
        }
    }

    @Override
    public void loadTopMovies(String url, int start, int offset) {
        MovieTask.RequestValues requestValues = new MovieTask.RequestValues(url, start, offset);
        MovieTask movieTask = Injection.provideMovieTask(activity);
        UseCaseHandler.getInstance().execute(movieTask, requestValues, new UseCase.UseCaseCallback<MovieTask.ResponseValues>() {
            @Override
            public void onSuccess(MovieTask.ResponseValues response) {
                mMovieView.showTopMovies(response.getMovies());
            }

            @Override
            public void onError() {
                mMovieView.showError("Error !!!");
            }
        });
    }
}
