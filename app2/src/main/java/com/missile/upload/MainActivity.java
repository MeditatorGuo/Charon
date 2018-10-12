package com.missile.upload;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.google.gson.GsonBuilder;
import com.missile.charon.Charon;
import com.missile.charon.callback.ApiCallback;
import com.missile.charon.convert.GsonConverterFactory;
import com.missile.charon.exception.ApiException;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    AppCompatButton uploadBtn;

    private static String fileUrl = "/sdcard/Pictures/test_cloud.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        uploadBtn = findViewById(R.id.uppload_btn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Charon charon = new Charon.Builder(mContext).baseUrl("http://192.168.6.210:8080/springboot/").converterFactory(GsonConverterFactory.create(new GsonBuilder().create())).build();

                String descriptionString = "hello, this is description.";
                RequestBody description =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), descriptionString);


                File file = new File(fileUrl);

                // create RequestBody instance from file
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);


                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", file.getName(), requestFile);


                charon.uploadFile("upload", description, body, new ApiCallback<ResultData>() {
                    @Override
                    public void onStart() {
                        Log.e("TAG", "-------------onStart--------------");
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.e("TAG", "-------------onError--------------"+ e.getDisplayMessage() );
                    }

                    @Override
                    public void onCompleted() {
                        Log.e("TAG", "-------------onCompleted--------------");
                    }

                    @Override
                    public void onNext(ResultData response) {

                        Log.e("TAG", "----------------CODE------:" + response.getCode());

                    }
                });
            }
        });
    }


}
