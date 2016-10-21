package com.yjz.welcomelogin;

import android.app.Application;

import com.hqjy.opensdk.HengQianSDK;

/**
 * Created by Administrator on 2016/8/23.
 */

public class App extends Application{
   @Override
   public void onCreate() {
      //初始化SDK
      HengQianSDK.getInstance().initSDK(this);
   }
}
