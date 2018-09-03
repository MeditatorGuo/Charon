package com.uowee.sample.app.contract;


import com.uowee.sample.app.domain.model.MovieModel;
import com.uowee.sample.app.mvp.BasePresenter;
import com.uowee.sample.app.mvp.BaseView;

import java.util.List;

/**
 * Created by GuoWee on 2018/7/17.
 */

public interface MovieContract {

    interface View extends BaseView {
        void showTopMovies(List<MovieModel.SubjectsBean> beans);

        void showError(String str);
    }


    interface Presenter extends BasePresenter {
        void loadTopMovies(String url, int start, int offset);
    }
}
