package alexandervbarkov.android.bnr.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class FlickrFetchr {
	public static final String TAG = "FlickrFetchr";
	public static final String PREF_SEARCH_QUERY = "search_query";
	public static final String PREF_NEW_SEARCH = "new_search";
	public static final String PREF_TOTAL = "total";
	public static final String PREF_PAGES = "pages";
	public static final String PREF_LAST_ITEM_ID = "id";
	private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
	private static final String API_KEY = "cc068a2dabf6f9e32c6db35023a88abd";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
	private static final String METHOD_SEARCH = "flickr.photos.search";
	private static final String PARAM_EXTRAS = "extras";
	private static final String PARAM_TEXT = "text";
	private static final String EXTRA_SMALL_URL = "url_s";
	private static final String XML_PHOTO = "photo";
	private static final String XML_PHOTOS = "photos";
	
	public byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) 
				return null;
			
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while((bytesRead = in.read(buffer)) > 0)
				out.write(buffer, 0, bytesRead);
			out.close();
			return out.toByteArray();
		}
		finally {
			connection.disconnect();
		}
	}
	
	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}
	
	public ArrayList<GalleryItem> downloadGalleryItems(String url, int page, Context context) {
		ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
		try {
			String xmlString = getUrl(url);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));

			parseItems(items, parser, context);
		}
		catch (IOException ioe) {
			Log.e(TAG, "Failed to fetch items", ioe);
		} 
		catch (XmlPullParserException xppe) {
			Log.e(TAG, "Failed to prase items", xppe);
		}

		return items;
	}
	
	public ArrayList<GalleryItem> getRecent(int page, Context context) {
		String url = Uri.parse(ENDPOINT).buildUpon().
				appendQueryParameter("method", METHOD_GET_RECENT).
				appendQueryParameter("api_key", API_KEY).
				appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL).
				appendQueryParameter("page", Integer.toString(page)).
				build().toString();
		return downloadGalleryItems(url, page, context);
	}
	
	public ArrayList<GalleryItem> searchItems(String query, int page, Context context) {
		String url = Uri.parse(ENDPOINT).buildUpon().
				appendQueryParameter("method", METHOD_SEARCH).
				appendQueryParameter("api_key", API_KEY).
				appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL).
				appendQueryParameter(PARAM_TEXT, query).
				appendQueryParameter("page", Integer.toString(page)).
				appendQueryParameter("sort", "relevance").
				build().toString();
		return downloadGalleryItems(url, page, context);
	}
	
	public void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser, Context context) throws XmlPullParserException, IOException {
		int eventType = parser.next();
		while(eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
				String id = parser.getAttributeValue(null, "id");
				String title = parser.getAttributeValue(null, "title");
				String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
				String owner = parser.getAttributeValue(null, "owner");
				
				GalleryItem item = new GalleryItem();
				item.setId(id);
				item.setTitle(title);
				item.setUrl(smallUrl);
				item.setOwner(owner);
				items.add(item);
			}
			if(eventType == XmlPullParser.START_TAG && XML_PHOTOS.equals(parser.getName())) {
				int total = Integer.parseInt(parser.getAttributeValue(null, "total"));
				PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_TOTAL, total).commit();
				int pages = Integer.parseInt(parser.getAttributeValue(null, "pages"));
				PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_PAGES, pages).commit();
				Log.d("pg", "total images found=" + total + " pages=" + pages);
			}
			eventType = parser.next();
		}
	}
}