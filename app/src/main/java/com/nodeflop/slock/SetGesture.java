package com.nodeflop.slock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SetGesture extends Activity {

    private int step;
    private ArrayList<Point> gesture1, gesture2;
    private DrawingView gview;
    private TextView tip;
    private boolean wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gesture);

        step = 1;
        gview = (DrawingView)findViewById(R.id.sgview);
        tip = (TextView)findViewById(R.id.tip);

        wait = false;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SetGesture.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(gview.isTouch_up() && !wait){
                            wait = true;
                            if(step==1){
                                gesture1 = gview.getGesture();
                                tip.setText("Draw your gesture again");
                                step++;
                                gview.reset();
                            }else if(step==2){
                                gesture2 = gview.getGesture();
                                if(GestureChecker.check(gesture1,gesture2)){
                                    tip.setText("");
                                    step = 1;
                                    if(save()){
                                        Toast.makeText(getApplicationContext(),"Gesture changed!",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }else{
                                    tip.setText("Draw your gesture");
                                    step = 1;
                                    Toast.makeText(getApplicationContext(),"Gestures don't match",Toast.LENGTH_SHORT).show();
                                }
                                gview.reset();
                            }
                            wait = false;
                        }
                    }
                });
            }
        },0,300);
    }

    private boolean save(){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("SLCKNDFLP", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(gesture1);
            os.close();
            return true;
        }catch (IOException e){
            Toast.makeText(getApplicationContext(),"An error occurred!",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_gesture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
