package com.tutorial.rizvish.swipingswiper;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

/**
 * Created by rizvish on 9/17/16.
 */
public class Splashscreen extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        Thread timer = new Thread(){
            public void run(){
            try {
                sleep(5000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        finally {
            Intent openStartingPoint = new Intent("com.tutorial.rizvish.SWIPINGSWIPER"); //looks thru manfiest to find this reference
            startActivity(openStartingPoint); //takes intent as parameters
        }
        }
        };
        timer.start();
    }
}
