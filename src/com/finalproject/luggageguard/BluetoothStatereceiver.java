package com.finalproject.luggageguard;

import java.util.List;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class BluetoothStatereceiver extends BroadcastReceiver {

	public static Vibrator vib;
	public static Ringtone r;
	public static MediaPlayer mp = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		SharedPreferences alarm = context.getSharedPreferences("alarm",
				context.MODE_PRIVATE);
		SharedPreferences.Editor editor = alarm.edit();

		String action = intent.getAction();

		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

			if (currentRunningActivity(context).equals(
					"com.finalproject.luggageguard.SetBlueTooth")) {
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
					SetBlueTooth.btSwitch.setChecked(false);
				} else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON)
					SetBlueTooth.btSwitch.setChecked(true);

			}
		}

		if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {

			String deviceName = device.getName();
			if (currentRunningActivity(context).equals(
					"com.finalproject.luggageguard.SetBlueTooth"))
				SetBlueTooth.deviceName.setText(deviceName);

			editor.putString("connectedDevice", device.getName());
			editor.commit();
			Toast.makeText(context,
					"Connected to " + alarm.getString("connectedDevice", ""),
					Toast.LENGTH_LONG).show();
		}
		if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")
				|| action
						.equals("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED")) {
			if (currentRunningActivity(context).equals(
					"com.finalproject.luggageguard.SetBlueTooth"))
				SetBlueTooth.deviceName.setText("No Device");
			WakeLock screenLock = ((PowerManager) context
					.getSystemService(context.POWER_SERVICE)).newWakeLock(
					PowerManager.SCREEN_BRIGHT_WAKE_LOCK
							| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
			screenLock.acquire();
			Toast.makeText(context, "DisConnected " + device.getName(),
					Toast.LENGTH_LONG).show();
			editor.putString("connectedDevice", "");
			if (device.getName().equals(alarm.getString("device", ""))) {

				Uri notification = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_ALARM);
				mp = MediaPlayer.create(context, notification);
				mp.start();

				// vib =
				// (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
				// vib.vibrate(3);

				Intent in = new Intent(context, AlarmBox.class);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(in);
			}
		}
		editor.commit();
		Log.d("Z", action);
	}

	public String currentRunningActivity(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		Log.d("topActivity", "CURRENT Activity ::"
				+ taskInfo.get(0).topActivity.getClassName());

		ComponentName componentInfo = taskInfo.get(0).topActivity;
		String active = componentInfo.getClassName();
		return active;
	}

}
