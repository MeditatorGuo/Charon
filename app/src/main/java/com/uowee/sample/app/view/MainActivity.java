package com.uowee.sample.app.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.missile.fastadapter.BaseQuickAdapter;
import com.uowee.sample.app.R;
import com.uowee.sample.app.adapter.MovieAdapter;
import com.uowee.sample.app.contract.MovieContract;
import com.uowee.sample.app.domain.model.MovieModel;
import com.uowee.sample.app.presenter.MoviePresenter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieContract.View {
    private static int start = 0;
    private static int PAGE_SIZE = 8;
    private static int offset = PAGE_SIZE;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
        initAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.loadTopMovies("movie/top250", start, offset);
    }

    private MovieContract.Presenter presenter = new MoviePresenter(this);

    @Override
    public void showTopMovies(List<MovieModel.SubjectsBean> beans) {
        mMovieAdapter.addData(beans);
        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void initAdapter() {
        mMovieAdapter = new MovieAdapter(R.layout.movie_item);
        mMovieAdapter.openAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mMovieAdapter.bind2RecyclerView(mRecyclerView);
    }

}
