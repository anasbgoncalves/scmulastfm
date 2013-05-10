package com.example.mobilelastfm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ormdroid.Entity;
import webimageview.WebImageView;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import database_entities.AlbumBookmark;
import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.ImageSize;

public class ArtistAlbunsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_artist_albuns);

		Artist artist = ActiveData.artist;
		new GetAlbunsTask().execute(artist.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artist_albuns, menu);
		return true;
	}

	private void onItemClicked(Album item) {
		Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
		ActiveData.album = item;
		startActivity(intent);
	}

	public class GetAlbunsTask extends AsyncTask<String, Void, Collection<Album>> {

		protected Collection<Album> doInBackground(String... artist) {
			try {
				Caller.getInstance().setCache(null);
				Caller.getInstance().setUserAgent("tst");
				Collection<Album> albuns_result = Artist.getTopAlbums(artist[0], MainActivity.API_KEY);
				return albuns_result;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected void onPostExecute(Collection<Album> albuns) {
			List<Album> list = null;
			if(albuns instanceof List){
				list = (List<Album>) albuns;
			}else{
				list = new ArrayList<Album>(albuns);
			}

			if (!list.isEmpty())
			{
				setListAdapter(new AlbumListAdapter(ArtistAlbunsActivity.this, R.layout.row_layout, list));
			}
			else
			{
				TextView txt = (TextView) findViewById(R.id.albuns_not_found);
				txt.setVisibility(View.VISIBLE);
			}

			setProgressBarIndeterminateVisibility(false);
		}

	}

	private class AlbumListAdapter extends ArrayAdapter<Album> {

		class ViewHolder{
			public WebImageView image;
			public TextView text;
			public CheckBox box;
		}

		public AlbumListAdapter(Context context, int rowResource, List<Album> list){
			super(context, rowResource, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;


			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, null);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.image = (WebImageView) convertView.findViewById(R.id.image);
				holder.box = (CheckBox) convertView.findViewById(R.id.favorite);
				convertView.setTag(holder);
			}


			final Album item = getItem(position);
			holder = (ViewHolder) convertView.getTag();
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClicked(item);
				}
			});
			holder.text.setText(item.getName());
			holder.image.setImageWithURL(getContext(), item.getImageURL(ImageSize.MEDIUM));
			

			final AlbumBookmark a = Entity.query(AlbumBookmark.class).where("mbid").eq(item.getMbid()).execute();
			if (a == null)
				holder.box.setChecked(false);
			else
				holder.box.setChecked(true);
			

			holder.box.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					bookmark(v, item, a);
				}
			});
			
			return convertView;
		}
	}
	
	public void bookmark(View view, Album album, AlbumBookmark a) {
		CheckBox box = (CheckBox) findViewById(R.id.favorite);
		boolean checked = box.isChecked();
		System.out.println("1");
		if (checked) {
			System.out.println("2");
			a = new AlbumBookmark();
			a.mbid = album.getMbid();
			a.title = album.getName();
			a.cover = album.getImageURL(ImageSize.MEDIUM);
			a.artist = album.getArtist();
			a.save();
		} else {
			System.out.println("3");
			a.delete();
			a.save();
		}
	}

}
