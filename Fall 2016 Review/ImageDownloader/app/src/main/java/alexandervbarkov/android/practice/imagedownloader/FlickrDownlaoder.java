package alexandervbarkov.android.practice.imagedownloader;

import android.net.Uri;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class FlickrDownlaoder {
	private static final String ENDPOINT = "https://api.flickr.com/services/rest";
	private static final String PARAM_API_KEY = "api_key";
	private static final String API_KEY = "cc068a2dabf6f9e32c6db35023a88abd";
	private static final String PARAM_METHOD = "method";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
	private static final String PARAM_PER_PAGE = "per_page";
	private static final String PARAM_EXTRAS = "extras";
	private static final String URL_N = "url_n";
	private static final String XML_PHOTO = "photo";
	private static final String XML_URL_N = "url_n";
	private static final String XML_TITLE = "title";
	public static final int MAX_IMAGE_SIZE = 320;

	public static List<Image> getRecentImages(int numberOfImages) throws IOException, XmlPullParserException {
		return parseImagesXml(getRecentImagesXml(numberOfImages));
	}

	/**
	 * Downloads an XML containing recent images.
	 *
	 * @param numberOfImages
	 * @return
	 * @throws IOException
	 */
	private static String getRecentImagesXml(int numberOfImages) throws IOException {
		String uri = Uri.parse(ENDPOINT)
				.buildUpon()
				.appendQueryParameter(PARAM_API_KEY, API_KEY)
				.appendQueryParameter(PARAM_METHOD, METHOD_GET_RECENT)
				.appendQueryParameter(PARAM_PER_PAGE, Integer.toString(numberOfImages))
				.appendQueryParameter(PARAM_EXTRAS, URL_N)
				.build()
				.toString();
		String xml = Downloader.getString(uri);
		return xml;
	}

	/**
	 * Parses an XML containing images into a list of images.
	 *
	 * @param imagesXml
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static List<Image> parseImagesXml(String imagesXml) throws XmlPullParserException, IOException {
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(new StringReader(imagesXml));
		int eventType = parser.next();
		List<Image> images = new LinkedList<>();
		while((eventType) != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_TAG && parser.getName().equals(XML_PHOTO)) {
				Image image = new Image();
				image.setTitle(parser.getAttributeValue(null, XML_TITLE));
				image.setImage(Downloader.getBytes(parser.getAttributeValue(null, XML_URL_N)));
				images.add(image);
			}
			eventType = parser.next();
		}
		return images;
	}
}
