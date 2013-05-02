package com.example.mobilelastfm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import webimageview.WebImageView;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.ImageSize;


public class ArtistsActivity extends ListActivity {

	public static String EXTRA_MESSAGE = "Artist name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_artists);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("Search results");

		Intent intent = getIntent();
		String result = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		//		getListView().setOnItemClickListener(this);

		new SearchTask().execute(result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artists, menu);
		return true;
	}

	@Override
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
			intent = new Intent(this, EventsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.action_friends:
			intent = new Intent(this, FriendsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	public void onItemClicked(Artist artist) {
		Intent intent = new Intent(getApplicationContext(), ArtistTabActivity.class);
		ActiveData.artist = artist;
		startActivity(intent);
	}

	public class SearchTask extends AsyncTask<String, Void, Collection<Artist>> {

		protected Collection<Artist> doInBackground(String... artist) {
			try {
				Caller.getInstance().setCache(null);
				Caller.getInstance().setUserAgent("tst");
				Collection<Artist> artist_result = Artist.search(artist[0], MainActivity.API_KEY);
				return artist_result;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected void onPostExecute(Collection<Artist> artists) {
			List<Artist> list = null;
			if (artists.isEmpty())
			{
				TextView txt = (TextView) findViewById(R.id.artists_not_found);
				txt.setVisibility(View.VISIBLE);
			}
			else
			{
				if(artists instanceof List){
					list = (List<Artist>) artists;
				}else{
					list = new ArrayList<Artist>(artists);
				}
				final ArtistsListAdapter adapter = new ArtistsListAdapter(ArtistsActivity.this, R.layout.artist_row, list);
				setListAdapter(adapter);
			}
			setProgressBarIndeterminateVisibility(false);
		}

	}

	private class ArtistsListAdapter extends ArrayAdapter<Artist> {

		class ViewHolder{
			public WebImageView image;
			public TextView text;
		}

		public ArtistsListAdapter(Context context, int rowResource, List<Artist> list){
			super(context, rowResource, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.artist_row, null);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.image = (WebImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();
			final Artist artist = getItem(position);

			holder.text.setText(artist.getName());
			holder.image.setImageWithURL(getContext(), artist.getImageURL(ImageSize.MEDIUM));

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClicked(artist);
				}
			});

			return convertView;
		}
	}

}
