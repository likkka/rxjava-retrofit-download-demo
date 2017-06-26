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
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

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
                i.setAction(IconEventReceiver.ACTION_ICONPULL);
                sendBroadcast(i);
            }
        });


        image = (ImageView) findViewById(R.id.image);
        loadpic = (Button) findViewById(R.id.loadpic);
        loadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo load icon test
            }
        });

    }
}
