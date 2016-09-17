package com.example.cmsc355.hungr;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pingYelpAPI instance = new pingYelpAPI();
        instance.execute();
    }

    private class pingYelpAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            YelpAPI apiCall = new YelpAPI("4gMlTiNc9lXpqP9YIwmbJg",
                    "yKuJ3AV6pkw4ITB4WBfHuZq-JL0",
                    "Eeiax8f5iIYnh6IMWmHLVTKYWSGATfr1",
                    "FtZpcHv3NFy3gKd-XxvvSDmN3Gk");
            System.out.println(apiCall.searchForBusinessesByLocation("food", "Richmond, VA"));
            return null;
        }
    }

}
