package com.example.rio.icontools.icontools.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.rio.icontools.R;
import com.example.rio.icontools.icontools.utils.IconUtils;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.http.Url;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by rio on 17-6-23.
 */

public class MainActivity extends Activity {
    ImageView image;
    Button loadpic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(IconEventReceiver.ACTION_ICONCHECK);
                sendBroadcast(i);
            }
        });


        image = (ImageView) findViewById(R.id.image);
        loadpic = (Button) findViewById(R.id.loadpic);
        loadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rx.
                String url = "http://i3.res.meizu.com/fileserver/app_icon/7984/c85a4e6daa86414a9a18ad3230ec6744.png";

                Observable
                        .just(url)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<String, InputStream>() {
                            @Override
                            public InputStream call(String s) {
                                InputStream b = null;
                                try {
                                    Call<InputStream> response = IconEventController.getInstance().getServerSingleton().getIcon(s);
                                    b = response.execute().body();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return b;
                            }
                        })
                        .doOnNext(new Action1<InputStream>() {
                            @Override
                            public void call(InputStream inputStream) {
                                IconUtils.saveBitmap(0, "com.riko.rio", inputStream);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<InputStream>() {
                            @Override
                            public void call(InputStream inputStream) {

                            }
                        });


            }
        });

    }
}
