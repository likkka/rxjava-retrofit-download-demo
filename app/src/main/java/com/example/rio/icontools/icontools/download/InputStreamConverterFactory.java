package com.example.rio.icontools.icontools.download;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by rio on 17-6-26.
 */

public class InputStreamConverterFactory extends Converter.Factory {


    public static InputStreamConverterFactory create() {
        return new InputStreamConverterFactory();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //进行条件判断，如果传进来的Type不是class，则匹配失败
        if (!(type instanceof Class<?>)) {
            return null;
        }
        Class<?> c = (Class<?>) type;
        //进行条件判断，如果传进来的Type不是Bitmap的子类，则也匹配失败
        if (!InputStream.class.isAssignableFrom(c)) {
            return null;
        }
        return new Converter<ResponseBody, InputStream>() {
            @Override
            public InputStream convert(ResponseBody value) throws IOException {
                return value.byteStream();
            }
        };
    }
}
