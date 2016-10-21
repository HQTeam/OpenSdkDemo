package com.yjz.welcomelogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.hqjy.hqutilslibrary.common.http.HttpCode.HTTP_EXCEPTION;
import static com.hqjy.hqutilslibrary.common.http.HttpCode.RESPONSE_ERROR;
import static com.hqjy.hqutilslibrary.common.http.HttpCode.RESPONSE_SUCCESS;

public class MainActivity extends AppCompatActivity {

    private HQAuth mHQAuth;
    private TextView show_tv;
    private Button logout_btn;
    private Button login_btn;
    private Button getdata_btn;
    public String mSecret;
    public String mToken;
    private Button getClassData_btn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //创建授权对象
        mHQAuth = HengQianSDK.getInstance().createAuth(this);

        logout_btn = (Button) findViewById(R.id.logout_btn);
        show_tv = (TextView) findViewById(R.id.show_tv);
        login_btn = (Button) findViewById(R.id.login_btn);
        getdata_btn = (Button) findViewById(R.id.getdata_btn);
        getClassData_btn = (Button) findViewById(R.id.getclassdata_btn);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        setListeners();
    }

    private void setListeners() {
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.isNetworkAvaliable(MainActivity.this)) {
                    //授权登录请求
                    mHQAuth.login();
                } else {
                    Toast.makeText(MainActivity.this, "网络不可用，请检查网络……", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HengQianSDK.getInstance().unInit();
                login_btn.setText("登录");
                show_tv.setText("Welcom Login!");
            }
        });

        getdata_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mSecret)) {
                    Toast.makeText(MainActivity.this, "请先进行登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取当前登录用户的资料接口请求测试
                String url = "http://api.hengqian.net/openApi/users/me.json";
                long time = (System.currentTimeMillis() / 1000L);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("consumerKey", "E1J6jYYbnc1r84VQk0g3Y76TiYma2htm");
                parameters.put("token", mToken);
                parameters.put("timeTamp", "" + time);
                parameters.put("field", "class");
                try {
                    /*对于获取用户相关信息的接口，Secret是用户进行登录授权时返回的secret数据*/
                    String requestUrl = HengQianSDK.getInstance().joinRequestUrl(url, parameters, mSecret);
                    progressBar.setVisibility(View.VISIBLE);
                    OkHttpUtil.getInstance().execute(RequestBuilder.create()
                            .setRequestMethod(RequestBuilder.Method.GET)
                            .setUrl(requestUrl)
                            .setHttpCallback(new HttpCallback() {
                                @Override
                                public void onFinish(HttpResult result) {
                                    parseResult(result);
                                }
                            }));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        getClassData_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mSecret)) {
                    Toast.makeText(MainActivity.this, "请先进行登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取班级信息接口测试
                String url = "http://api.hengqian.net/openapi/classes/show.json";
                long time = (System.currentTimeMillis() / 1000L);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("consumerKey", "E1J6jYYbnc1r84VQk0g3Y76TiYma2htm");
                parameters.put("cid", "fe4aace0-fbab-43d3-bac6-93acc6c6b5c2");
                parameters.put("timeTamp", "" + time);
                parameters.put("field", "");
                try {
                    /*对于非用户信息类接口，Secret是在开放平台申请应用时，生成的Secret Key*/
                    String requestUrl = HengQianSDK.getInstance().joinRequestUrl(url, parameters, "239282fb3da5a463dc8d8e8fa0952791");
                    progressBar.setVisibility(View.VISIBLE);
                    OkHttpUtil.getInstance().execute(RequestBuilder.create()
                            .setRequestMethod(RequestBuilder.Method.GET)
                            .setUrl(requestUrl)
                            .setHttpCallback(new HttpCallback() {
                                @Override
                                public void onFinish(final HttpResult result) {
                                    parseResult(result);
                                }
                            }));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private void parseResult(final HttpResult result){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                switch (result.getResultCode()) {
                    case RESPONSE_SUCCESS:
                        showInfo(result.getResult());
                        break;
                    case RESPONSE_ERROR:
                    case HTTP_EXCEPTION:
                        showInfo("系统忙碌中，请稍后在试……");
                        break;
                }
            }
        });
    }

    private void showInfo(final String text){
        show_tv.setText(text);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*获取授权成功后返回的数据*/
        mHQAuth.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
            @Override
            public void onComplete(String result) {
                show_tv.setText(result);
                login_btn.setText("登录成功");
                try {
                    JSONObject obj = new JSONObject(result);
                    if (0 == obj.getInt("errcode")) {
                        mToken = obj.getString("token");
                        mSecret = obj.getString("secret");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorCode) {
                Toast.makeText(MainActivity.this, "errorCode===" + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*应用登录界面退出时，结束授权对象*/
        mHQAuth.logout();
    }
}
