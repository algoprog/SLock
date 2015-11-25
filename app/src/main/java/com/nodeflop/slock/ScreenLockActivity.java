package com.nodeflop.slock;

import com.nodeflop.slock.lockutil.HomeKeyLocker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenLockActivity extends Activity {
	private static final String TAG = "ScreenLockActivity";
	public static boolean isShown = false;
	private static Context instance = null;

	private HomeKeyLocker home_locker;
	private customViewGroup sview;
	private WindowManager manager;
	private DrawingView gview;
	private ArrayList<Point> saved_gesture, current_gesture;
	private int fails;
	private Calendar calendar;
	private String time_str, date_str;
	private TextView time, date, battery;
	private ImageView charge_cable, wifi, bluetooth;
	private Locale locale;
	private Intent batteryIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isShown = true;
		instance = ScreenLockActivity.this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setContentView(R.layout.activity_screen_lock);

		init();

		updateTime();

		fails = 0;

		gview.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				gview.onTouch(motionEvent);
				if (gview.isTouch_up()) {
					current_gesture = gview.getGesture();
					boolean later = tryLater();
					if (!later && (saved_gesture == null || saved_gesture.size() == 0 || GestureChecker.check(saved_gesture, current_gesture))) {
						home_locker.unlock();
						manager.removeView(sview);
						gview.reset();

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

						finish();
					} else if (!later) {
						fails++;
						if (fails == 3) {
							try {
								long timestamp = getTime();
								FileOutputStream fos = getApplicationContext().openFileOutput("SLCKNDFLPF", Context.MODE_PRIVATE);
								ObjectOutputStream os = new ObjectOutputStream(fos);
								os.writeObject(timestamp);
								os.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						//Toast.makeText(getApplicationContext(),"Wrong gesture!",Toast.LENGTH_SHORT).show();
					}
					gview.reset();
				}
				return false;
			}
		});

		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ScreenLockActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateTime();
						getBatteryInfo();
					}
				});
			}
		}, 0, 2000);
	}

	private long getTime(){
		return System.currentTimeMillis() / 1000L;
	}

	private void updateTime(){
		calendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("h:mm", locale);
		time_str = df.format(calendar.getTime());
		time.setText(time_str);
		df = new SimpleDateFormat("EEEE MMMM d", locale);
		date_str = df.format(calendar.getTime());
		date.setText(date_str);
	}

	private void getBatteryInfo() {
		batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		battery.setText((int)(((float)level / (float)scale) * 100.0f)+"%");

		int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;
		int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		if(usbCharge){
			charge_cable.setImageResource(R.drawable.usb);
		}else if(acCharge){
			charge_cable.setImageResource(R.drawable.ac_power);
		}else if(batteryCharge){
			charge_cable.setImageResource(R.drawable.charging);
		}else{
			charge_cable.setImageResource(R.drawable.battery);
		}
	}

	private boolean tryLater(){
		try {
			FileInputStream fos = getApplicationContext().openFileInput("SLCKNDFLPF");
			ObjectInputStream os = new ObjectInputStream(fos);
			long last_try = (Long)os.readObject();
			os.close();
			long diff = getTime()-last_try;
			if(diff<300){
				int m = Math.round((300 - diff)/60)+1;
				String minutes_str;
				if(300-diff>60){
					minutes_str = "minutes";
				}else{
					m = (int)(300-diff);
					minutes_str = "seconds";
				}
				Toast.makeText(getApplicationContext(),"Try again in "+m+" "+minutes_str,Toast.LENGTH_SHORT).show();
				return true;
			}
		}catch (IOException e){
			//Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void init() {
		home_locker = new HomeKeyLocker();
		home_locker.lock(this);

		manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
		localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		localLayoutParams.gravity = Gravity.TOP;
		localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		localLayoutParams.height = (int) (28 * getResources().getDisplayMetrics().scaledDensity);
		localLayoutParams.format = PixelFormat.TRANSPARENT;
		sview = new customViewGroup(this);
		manager.addView(sview, localLayoutParams);

		saved_gesture = new ArrayList<Point>();
		current_gesture = new ArrayList<Point>();

		gview = (DrawingView)findViewById(R.id.gview);
		gview.showLine(false);
		time = (TextView)findViewById(R.id.time);
		date = (TextView)findViewById(R.id.date);
		battery = (TextView)findViewById(R.id.batteryLevel);
		charge_cable = (ImageView)findViewById(R.id.charge_cable);
		wifi = (ImageView)findViewById(R.id.wifi);
		bluetooth = (ImageView)findViewById(R.id.bluetooth);

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			wifi.setImageAlpha(255);
		}else{
			wifi.setImageAlpha(70);
		}

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter == null) {
			bluetooth.setVisibility(View.INVISIBLE);
		}else if(bluetoothAdapter.isEnabled()){
			bluetooth.setImageAlpha(255);
		}else{
			bluetooth.setImageAlpha(70);
		}

		getBatteryInfo();

		locale = getResources().getConfiguration().locale;

		load_gesture();
	}

	private boolean load_gesture(){
		try {
			FileInputStream fos = getApplicationContext().openFileInput("SLCKNDFLP");
			ObjectInputStream os = new ObjectInputStream(fos);
			saved_gesture = (ArrayList<Point>)os.readObject();
			os.close();
			return true;
		}catch (IOException e){
			Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		isShown = false;
		instance = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_lock, menu);
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

	
	public static Context getInstance() {
		return instance;
	}
}
