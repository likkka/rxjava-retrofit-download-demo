package com.example.rio.icontools.icontools.download;

import android.graphics.Bitmap;

import com.example.rio.icontools.icontools.bean.BaseEntity;
import com.example.rio.icontools.icontools.bean.FlymeIconBean;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by rio on 17-6-20.
 */

public interface ServerApi {
    @GET("flymeIcons")
    @Headers("MZ-AppId:909be9ff-1f9e-4eda-842f-7bb7b922e855")
    Call<BaseEntity> fetchInfos(@Header("MZ-Sign") String appkey, @Query("where") String s);

    @GET
    Call<InputStream> getIcon(@Url String url);
}
