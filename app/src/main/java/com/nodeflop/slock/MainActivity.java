package com.nodeflop.slock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = MainActivity.this;

		Intent lock_service = new Intent();
		lock_service.setAction("com.nodeflop.slock.service.LocalService");
		MainActivity.this.startService(lock_service);

		try {
			long timestamp = 0;
			FileOutputStream fos = getApplicationContext().openFileOutput("SLCKNDFLPF", Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(timestamp);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TextView b = (TextView)findViewById(R.id.set);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this,SetGesture.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(i);
			}
		});

		b = (TextView)findViewById(R.id.reset);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					FileOutputStream fos = getApplicationContext().openFileOutput("SLCKNDFLP", Context.MODE_PRIVATE);
					ObjectOutputStream os = new ObjectOutputStream(fos);
					os.writeObject(null);
					os.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(),"Gesture cleared",Toast.LENGTH_SHORT).show();
			}
		});

		b = (TextView)findViewById(R.id.lock);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, ScreenLockActivity.class);
				startActivity(i);
			}
		});

		b = (TextView)findViewById(R.id.exit);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
