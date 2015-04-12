package com.finalproject.luggageguard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tawsif.finalproject.R;

public class SetBlueTooth extends Activity{

	ListView btList;
	TextView review , title,setAlarm;
	CheckBox chkbx;
	public static TextView deviceName;
	Button btSettings;
	LinearLayout setLL;
	public static Switch btSwitch;
	private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	BluetoothAdapter mBluetoothAdapter;
	ArrayList<String> addressList;
	ProgressDialog dial;
	ArrayList<String> s;
	CustomDeviceListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.set_bluetooth);
		
		review = (TextView)findViewById(R.id.review);
		title = (TextView)findViewById(R.id.title);
		deviceName = (TextView)findViewById(R.id.deviceName);
//		btSettings = (Button)findViewById(R.id.btsettings);
		chkbx = (CheckBox)findViewById(R.id.checkBox);
		setLL = (LinearLayout)findViewById(R.id.ll);
		btSwitch = (Switch)findViewById(R.id.btSwitch);
		btList = (ListView)findViewById(R.id.list);


		addressList = new ArrayList<String>();
		s = new ArrayList<String>();

		adapter = new CustomDeviceListAdapter(SetBlueTooth.this, s);
		btList.setAdapter(adapter);


		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
		if (mBluetoothAdapter.isEnabled()) {
			btSwitch.setChecked(true);
			showPairedDevices();
		} 


		
		SharedPreferences alarm = getSharedPreferences("alarm", MODE_PRIVATE);
//		SharedPreferences.Editor editor = alarm.edit();
		
		String alarmValue = alarm.getString("alarm", "");
		
		if(alarmValue.equals("on")){
			chkbx.setChecked(true);
		}
		review.setText("Go to the Bluetooth settings to connect your bluetooth device.Make sure this is not any mobile device,Because mobile bluetooth is not stable.");
		

		
		btList.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) 
	        {
	            mBluetoothAdapter.cancelDiscovery();

	            String mDeviceAddress = addressList.get(mPosition);
				Toast.makeText(getApplicationContext(), "connecting", Toast.LENGTH_LONG).show();
				
                BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                pairToDevice(mBluetoothDevice);

	        }
		});
		
		btSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub

//				progressbar.setVisibility(View.VISIBLE);
				if (mBluetoothAdapter.isEnabled()) {
				    mBluetoothAdapter.disable(); 
				    
					do{
						
					}while(mBluetoothAdapter.isEnabled());
					s.clear();
					adapter.notifyDataSetChanged();
					

				}else{
					mBluetoothAdapter.enable();
					do{
					}while(!mBluetoothAdapter.isEnabled());
					showPairedDevices();
				}
			}
		});
		
		chkbx.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences alarm = getSharedPreferences("alarm", MODE_PRIVATE);
				SharedPreferences.Editor editor = alarm.edit();
				
				if(!chkbx.isChecked()){
					editor.putString("alarm", "off");
					chkbx.setChecked(false);
					editor.putString("device",  "");
					}else{
						if(!alarm.getString("connectedDevice", "").equals("")){
						chkbx.setChecked(true);
						editor.putString("alarm", "on");
						editor.putString("device", alarm.getString("connectedDevice", ""));
					}else{
							Toast.makeText(getApplicationContext(), "No device is connected yet.", Toast.LENGTH_LONG).show();
							chkbx.setChecked(false);
					}
					}
				editor.commit();
			}
		});
		
		setLL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferences alarm = getSharedPreferences("alarm", MODE_PRIVATE);
				SharedPreferences.Editor editor = alarm.edit();
				
				if(chkbx.isChecked()){
				editor.putString("alarm", "off");
				editor.putString("device", "");
				chkbx.setChecked(false);
				}else{
					if(!alarm.getString("connectedDevice", "").equals("")){
						chkbx.setChecked(true);
						editor.putString("alarm", "on");
						editor.putString("device", alarm.getString("connectedDevice", ""));
					}else
							Toast.makeText(getApplicationContext(), "No device is connected yet.", Toast.LENGTH_LONG).show();
					
				}
				editor.commit();
			}
		});
		
	}
	
	public void showPairedDevices(){
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		
		for(BluetoothDevice bt : pairedDevices){
		   s.add(bt.getName());
		   addressList.add(bt.getAddress());
//			Toast.makeText(getApplicationContext(), "list", 1000).show();
			
		}

		adapter.notifyDataSetChanged();
		

//		progressbar.setVisibility(View.INVISIBLE);
	}
	
	private void pairToDevice(BluetoothDevice nBluetoothDevice) 
    {
     //   Log.v(TAG, "InsidepairToDeviceCalled");
        openSocket(nBluetoothDevice);
       // Log.v(TAG, "LeavingpairToDeviceCalled");
    }
	
	private void openSocket(BluetoothDevice nBluetoothDevice) 
    {
        try 
        {

            final ProgressDialog dialog = new ProgressDialog(this);

//            dialog.show(SetBlueTooth.this, "Connecting...", nBluetoothDevice.getName() + " : " + nBluetoothDevice.getAddress(),
//                true, true);
            final ConnectRunnable connector = new ConnectRunnable(nBluetoothDevice, dialog);

//            dialog.dismiss();
            new Thread(connector).start();
        } 
        catch (IOException ex) 
        {

        }
    }
	
	private class ConnectRunnable implements Runnable 
    {
        private final ProgressDialog dialog;
        private final BluetoothSocket socket;

        public ConnectRunnable(BluetoothDevice device, ProgressDialog dialog) throws IOException 
        {
            socket = device.createRfcommSocketToServiceRecord(applicationUUID);
            this.dialog = dialog;
            
        }

        public void run() 
        {
            try 
            {
//                Log.v(TAG, "InsideRunnableCalled");
                mBluetoothAdapter.cancelDiscovery();
                socket.connect();
//                Log.v(TAG, "InsideRunnableSocketConnectCalled");
            }
            catch (IOException connectException) 
            {
//                Log.d(TAG, "Could not connect to socket", connectException);
                closeSocket(socket);
                return;
            }
            Log.v("TAG", "Connected");

            dismissDialog(dialog);
            closeSocket(socket);
        }

        public void cancel() 
        {
            try 
            {
                socket.close();
            }
            catch (IOException e) 
            {
//                Log.d(TAG, "Canceled connection", e);
            }
        }
    }
	
	private void dismissDialog(final Dialog dialog) 
    {
        runOnUiThread(new Runnable() 
        {
            public void run() 
            {
                dialog.dismiss();
                Log.v("TAG", "DialogClosed");
            }
        });
    }

    private void closeSocket(BluetoothSocket nOpenSocket) 
    {
        try 
        {
            nOpenSocket.close();
//            Log.v(TAG, "SockectClosed");
        } 
        catch (IOException ex) 
        {
//            Log.d(TAG, "Could not close exisiting socket", ex);
        }
    }

}
