package alexandervbarkov.android.bnr.photogallery;

public class GalleryItem {
	private String mTitle;
	private String mId;
	private String mUrl;
	private String mOwner;

	public String toString() {
		return mTitle;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
	
	public String getOwner() {
		return mOwner;
	}

	public void setOwner(String owner) {
		mOwner = owner;
	}

	public String getPhotoPageUrl() {
		return "https://www.flickr.com/photos/" + mOwner + "/" + mId;
	}
}