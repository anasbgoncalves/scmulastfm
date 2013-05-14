package com.example.mobilelastfm;

import java.util.Iterator;
import java.util.List;

import ormdroid.Entity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import database_entities.Friend;

public class FriendsToConnectActivity extends Activity {

	protected static final String EXTRA_DEVICE_ADDRESS = "DEVICE_EXTRA";
	// Member fields
	private BluetoothAdapter mBtAdapter;
	private DevicesListAdapter arrayAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_friends_to_connect);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);

		arrayAdapter = new DevicesListAdapter(this, R.layout.device_name);

		// Find and set up the ListView for paired devices
		ListView pairedListView = (ListView) findViewById(R.id.friends_list);
		pairedListView.setAdapter(arrayAdapter);
		
		List<Friend> f = Entity.query(Friend.class).executeMulti();

		if (f.isEmpty())
		{
			TextView txt = (TextView) findViewById(R.id.empty_friends);
			txt.setVisibility(View.VISIBLE);
		}
		else
		{
			Iterator<Friend> it = f.iterator();
			while (it.hasNext())
			{
				Friend next = it.next();
				arrayAdapter.add(next);
			}
		}

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		setProgressBarIndeterminateVisibility(false);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId())
		{
		case android.R.id.home:
			intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.action_book:
			intent = new Intent(this, BookmarkTabActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.action_events:
			intent = new Intent(this, EventsTabActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.action_friends:
			intent = new Intent(this, FriendsTabActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.action_chat:
			intent = new Intent(this, FriendsToConnectActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void connect(View view) {
		Intent intent = new Intent(this, BluetoothChatActivity.class);
		CheckBox box = (CheckBox) view.findViewById(R.id.connect);
		String mac_address = box.getContentDescription().toString();
		intent.putExtra(MainActivity.EXTRA_MESSAGE, mac_address);
		startActivity(intent);
	}

	private class DevicesListAdapter extends ArrayAdapter<Friend> {

		class ViewHolder{
			public TextView text;
			public CheckBox box;
		}

		public DevicesListAdapter(Context context, int rowResource) {
			super(context, rowResource);
		}

		@Override
		public void add(Friend object) {
			super.add(object);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_name, null);
				holder.text = (TextView) convertView.findViewById(R.id.device_name);
				holder.box = (CheckBox) convertView.findViewById(R.id.connect);
				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();
			final Friend item = getItem(position);		
			holder.text.setText(item.device_name);
			holder.box.setContentDescription(item.mac_address);
//			convertView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					onItemClicked(item);
//				}
//			});
			return convertView;
		}
	}
}
