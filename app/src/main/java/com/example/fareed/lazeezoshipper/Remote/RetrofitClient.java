package com.example.fareed.lazeezoshipper.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by fareed on 19/04/2018.
 */

public class RetrofitClient {

    private static Retrofit retrofit=null;


    public static Retrofit getClient(String baseUrl){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
