package com.uowee.sample.app.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.missile.fastadapter.BaseQuickAdapter;
import com.missile.fastadapter.BaseViewHolder;
import com.uowee.sample.app.R;
import com.uowee.sample.app.domain.model.MovieModel;
import com.uowee.sample.app.util.StringUtil;

import java.util.List;

/**
 * Created by GuoWee on 2018/7/17.
 */

public class MovieAdapter extends BaseQuickAdapter<MovieModel.SubjectsBean, BaseViewHolder> {


    public MovieAdapter(@Nullable List<MovieModel.SubjectsBean> data) {
        super(data);
    }

    public MovieAdapter(int layoutResId) {
        super(layoutResId);
    }

    public MovieAdapter(int layoutResId, @Nullable List<MovieModel.SubjectsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, MovieModel.SubjectsBean item) {
        holder.setText(R.id.lmi_title, item.getTitle());
        holder.setText(R.id.lmi_grade, item.getRating().getAverage() + " 分");
        holder.setText(R.id.lmi_describe, StringUtil.listToString(item.getGenres(), ','));
        holder.setText(R.id.lmi_actor, item.getDirectors().get(0).getName());
        Glide.with(mContext).load(item.getImages().getMedium()).into((ImageView) holder.getView(R.id.lmi_avatar));
    }
}
