package com.roopre.oracletutorial;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    Button btn ;
    TextView tv;
    EditText userid_et,name_et;
    String userid = "", name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btn = (Button) findViewById(R.id.start_button);
        btn.setOnClickListener(this);

        tv = (TextView) findViewById(R.id.resulttv);

        userid_et = (EditText) findViewById(R.id.userid_et);
        name_et = (EditText) findViewById(R.id.name_et);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start_button:
                userid = userid_et.getText().toString();
                name = name_et.getText().toString();
                if(userid.length()<=1 || name.length()<=1){
                    Log.d(TAG, "데이터를 입력하세요");
                }
                else
                {
                    ConnectServer();
                }

                break;
            default:
                break;
        }

    }
    private void ConnectServer(){

        final String SIGNIN_URL = "http://본인서버IP:8080/connection_test.jsp";
        final String urlSuffix = "?userid=" + userid + "&name=" + name;
        Log.d("urlSuffix", urlSuffix);
        class SignupUser extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d(TAG,s);
                if (s != null) {
                    try{

                        JSONArray jArr = new JSONArray(s);;
                        JSONObject json = new JSONObject();

                        for (int i = 0; i < jArr.length(); i++) {
                            json = jArr.getJSONObject(i);
                            tv.setText("USERID : "+json.getString("userid")+"\nNAME : "+json.getString("name")+"\nAGE : "+json.getString("age"));

                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "서버와의 통신에 문제가 발생했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                BufferedReader bufferedReader = null;

                try {

                    HttpClient client = new DefaultHttpClient();  // 보낼 객체 만들기
                    HttpPost post = new HttpPost(SIGNIN_URL + urlSuffix);  // 주소 뒤에 데이터를 넣기

                    HttpResponse response = client.execute(post); // 데이터 보내기

                    BufferedReader bufreader = new BufferedReader(
                            new InputStreamReader(
                                    response.getEntity().getContent(), "utf-8"));

                    String line = null;
                    String page = "";

                    while ((line = bufreader.readLine()) != null) {
                        page += line;
                    }
                    Log.d(TAG, page);
                    return page;
                } catch (Exception e) {
                    return null;
                }
            }
        }

        SignupUser su = new SignupUser();
        su.execute(urlSuffix);
    }
}
