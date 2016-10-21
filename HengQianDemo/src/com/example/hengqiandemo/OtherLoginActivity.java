package com.example.hengqiandemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hqjy.hqutilslibrary.common.NetworkUtil;
import com.hqjy.hqutilslibrary.common.http.HttpCallback;
import com.hqjy.hqutilslibrary.common.http.HttpResult;
import com.hqjy.hqutilslibrary.common.http.OkHttpUtil;
import com.hqjy.hqutilslibrary.common.http.RequestBuilder;
import com.hqjy.opensdk.HQAuth;
import com.hqjy.opensdk.HengQianSDK;
import com.hqjy.opensdk.hqinterface.IUiListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/9.
 */

public class OtherLoginActivity extends Activity implements View.OnClickListener {
   private TextView mOtherRegister;

   private TextView mYouXueRegister;
   private TextView mApiRegister;

   private TextView mAccreditBackInfo;
   private TextView mApiBackInfo;

   public HQAuth mHQAuth;
   public String mToken;
   public String mSecret;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.otherlogin_activity);
      mOtherRegister = (TextView) findViewById(R.id.other_register_txt);
      mYouXueRegister = (TextView) findViewById(R.id.youxue_register_txt);
      mApiRegister = (TextView) findViewById(R.id.api_register_txt);
      mAccreditBackInfo = (TextView) findViewById(R.id.accredit_backinfo);
      mApiBackInfo = (TextView) findViewById(R.id.api_backinfo);

      mOtherRegister.setOnClickListener(this);
      mYouXueRegister.setOnClickListener(this);
      mApiRegister.setOnClickListener(this);
      mHQAuth = HengQianSDK.getInstance().createAuth(this);
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
         case R.id.youxue_register_txt:
            //使用优e学堂登录按钮的点击事件
            if (NetworkUtil.isNetworkAvaliable(this)) {
               mHQAuth.login();
            } else {
               Toast.makeText(this, "网络不可用，请检查网络……", Toast.LENGTH_SHORT).show();
            }
            break;
         case R.id.other_register_txt:
            Intent intent = new Intent(OtherLoginActivity.this, MainActivity.class);
            startActivity(intent);
            break;
         case R.id.api_register_txt:
            //使用返回得token获取开放接口的数据
            Map<String, String> params = new HashMap<String, String>();
            String URL = "http://api.hengqian.net/openApi/users/me.json";
            String APIKey = "FVui6P7SVhx7zdWX4TAU5Ypktgu1g9ud";
            if (!TextUtils.isEmpty(mToken)) {
               params.put("consumerKey", APIKey);
               params.put("token", mToken);
               params.put("timeTamp", Long.toString(System.currentTimeMillis() / 1000));
               params.put("field", "district");
               try {
                  String requesURL = HengQianSDK.getInstance()
                        .joinRequestUrl(URL, params, mSecret);
                  OkHttpUtil.getInstance()
                        .execute(RequestBuilder.create()
                              .setRequestMethod(RequestBuilder.Method.GET)
                              .setUrl(requesURL).setHttpCallback(new HttpCallback() {
                                 @Override
                                 public void onFinish(final HttpResult result) {
                                    OtherLoginActivity.this.runOnUiThread(new Runnable() {
                                       public void run() {
                                          Log.e("info", "info = " + result.getResult());
                                          mApiBackInfo.setText("开放接口数据：\n" + result.getResult());
                                       }
                                    });
                                 }
                              }));
               } catch (Exception e) {
                  e.printStackTrace();
               }
            } else {
               Toast.makeText(getApplicationContext(),
                     "请先授权登录", Toast.LENGTH_SHORT).show();
            }

            break;

      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      mHQAuth.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
         @Override
         public void onComplete(String result) {
            //授权成功信息返回值处理
            if (!TextUtils.isEmpty(result)) {
               mAccreditBackInfo.setText("授权登录返回数据：\n" + result);
               try {
                  JSONObject jsb = new JSONObject(result);
                  mToken = jsb.getString("token");
                  mSecret = jsb.getString("secret");
               } catch (Exception e) {
                  // TODO: handle exception
               }
            }

         }

         @Override
         public void onError(String errorCode) {
            //授权失败信息返回处理
            mAccreditBackInfo.setText(errorCode);
         }
      });
   }
}
