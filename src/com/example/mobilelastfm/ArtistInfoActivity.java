package com.example.mobilelastfm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import webimageview.WebImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Tag;

public class ArtistInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_info);

		Artist artist = C.artist;
		TextView text = (TextView) findViewById(R.id.artist_name);
		text.setText(artist.getName());
		
		WebImageView image = (WebImageView) findViewById(R.id.image);
		image.setImageWithURL(getApplicationContext(), artist.getImageURL(ImageSize.LARGE));
		
		new SumaryTask().execute(artist.getName());
		new TagsTask().execute(artist.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artist_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;	
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public class SumaryTask extends AsyncTask<String, Void, Artist> {

		protected Artist doInBackground(String... artist) {
			try {
				Caller.getInstance().setCache(null);
				Caller.getInstance().setUserAgent("tst");
				Artist artist_result = Artist.getInfo(artist[0], MainActivity.API_KEY);
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

		protected void onPostExecute(Artist artist) {
			TextView txt = (TextView) findViewById(R.id.sumary);
			txt.setText(Html.fromHtml(artist.getWikiSummary()));
		}
	}
	
	public class TagsTask extends AsyncTask<String, Void, Collection<Tag>> {

		protected Collection<Tag> doInBackground(String... artist) {
			try {
				Caller.getInstance().setCache(null);
				Caller.getInstance().setUserAgent("tst");
				Collection<Tag> tags = Artist.getTopTags(artist[0], MainActivity.API_KEY);
				return tags;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected void onPostExecute(Collection<Tag> tags) {
			List<Tag> list = new ArrayList<Tag>();
			Iterator<Tag> it = tags.iterator();
			int counter = 0;
			while (it.hasNext() && counter < 5)
			{
				Tag next = it.next();
				if (next != null)
				{
					list.add(it.next());
					counter++;
				}
			}
			ListView lv = (ListView) findViewById(R.id.tags_list);
			TagListAdapter adapter = new TagListAdapter(getApplicationContext(), R.layout.tag_item, list);
			lv.setDivider(null);
			lv.setAdapter(adapter);
			
			setProgressBarIndeterminateVisibility(false);
		}
	}
	
	private class TagListAdapter extends ArrayAdapter<Tag> {

		class ViewHolder{
			public TextView text;
		}

		public TagListAdapter(Context context, int rowResource, List<Tag> list){
			super(context, rowResource, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_item, null);
				holder.text = (TextView) convertView.findViewById(R.id.artist_tag_item);
				convertView.setTag(holder);
			}

			final Tag item = getItem(position);
			holder = (ViewHolder) convertView.getTag();
			holder.text.setText(item.getName());
			return convertView;
		}
	}
}