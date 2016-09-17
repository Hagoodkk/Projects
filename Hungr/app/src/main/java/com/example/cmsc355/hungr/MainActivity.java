package com.example.cmsc355.hungr;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pingYelpAPI apiResponse = new pingYelpAPI();
        apiResponse.execute();
    }

    private class pingYelpAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiResponse;
            YelpAPI apiCall = new YelpAPI("4gMlTiNc9lXpqP9YIwmbJg",
                    "yKuJ3AV6pkw4ITB4WBfHuZq-JL0",
                    "Eeiax8f5iIYnh6IMWmHLVTKYWSGATfr1",
                    "FtZpcHv3NFy3gKd-XxvvSDmN3Gk");
            apiResponse = apiCall.searchForBusinessesByLocation("food", "Richmond, VA");
            try{
                JSONObject apiR = new JSONObject(apiResponse);
                System.out.println(apiR);
            }
            catch(Exception e){
                System.out.println("Fail 1");
            }

            return null;
        }
    }

}
