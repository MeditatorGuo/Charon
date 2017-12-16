package com.uowee.sample.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.uowee.charon.Charon;
import com.uowee.charon.exception.CharonThrowable;
import com.uowee.charon.subscriber.BaseSubscriber;
import com.uowee.sample.app.model.MovieModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {

    private TextView mTextMessage;
    private Button getBtn, getBtn2;
    private Context mContext;

    private Charon charon;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getBtn = (Button) findViewById(R.id.bt_get);

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perform();
            }
        });
        getBtn2 = (Button) findViewById(R.id.bt_get2);
        getBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void perform() {


        parameters.clear();
        parameters.put("start", "0");
        parameters.put("count", "3");
        charon = new Charon.Builder(mContext)
                .baseUrl("https://api.douban.com/v2/")
                .converterFactory(GsonConverterFactory.create(new GsonBuilder().create())).build();
        charon.get("movie/top250", parameters, new BaseSubscriber<ResponseBody>(mContext) {

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String jstr = new String(responseBody.bytes());
                    Type type = new TypeToken<MovieModel>() {
                    }.getType();

                    MovieModel response = new Gson().fromJson(jstr, type);
                    List<MovieModel.SubjectsBean> list = response.getSubjects();
                    for (MovieModel.SubjectsBean bean: list){
                        Log.e("TAG","Title:" + bean.getTitle() + ",Directors:" + bean.getDirectors().get(0).getName());
                    }
                    Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(CharonThrowable e) {

            }
        });

    }


}
