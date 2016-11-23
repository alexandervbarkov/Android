package alexandervbarkov.android.practice.imagedownloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {
	public static byte[] getBytes(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		try {
			InputStream in = connection.getInputStream();
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				return null;
			int bytesRead;
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			return out.toByteArray();
		}
		finally {
			connection.disconnect();
		}
	}

	public static String getString(String url) throws IOException {
		return new String(getBytes(url));
	}

}
