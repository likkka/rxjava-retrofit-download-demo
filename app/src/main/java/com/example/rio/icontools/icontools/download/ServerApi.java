package com.example.rio.icontools.icontools.download;

import com.example.rio.icontools.icontools.bean.FlymeIconBean;
import com.squareup.okhttp.ResponseBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by rio on 17-6-20.
 */

public interface ServerApi {
    @GET
    Call<FlymeIconBean> fetchInfos(@Query("package") String pkg, @Query("dpi") int dpi);

    @GET
    Call<ResponseBody> getIcon(@Url String url);
}
