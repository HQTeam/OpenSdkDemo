package com.example.hengqiandemo;

import com.hqjy.opensdk.HengQianSDK;

import android.app.Application;


/**
 * Created by Administrator on 2016/10/9.
 */

public class MyApplication extends Application {
   @Override
   public void onCreate() {
      //初始化SDK
      HengQianSDK.getInstance().initSDK(this);
   }
}

