package alexandervbarkov.android.bnr.photogallery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class FragmentPhotoPage extends FragmentVisible {
	private WebView mWebView;
	private String mUrl;
	private ProgressBar mProgress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mUrl = getActivity().getIntent().getStringExtra(FragmentPhotoGallery.EXTRA_URL);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_page, root, false);
		
		mWebView = (WebView)v.findViewById(R.id.wv_frag_photo_page);
		mProgress = (ProgressBar)v.findViewById(R.id.pb_frag_photo_page);
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView webView, int progress) {
				if(progress == 100)
					mProgress.setVisibility(View.INVISIBLE);
				else
					mProgress.setProgress(progress);
			}
		});
		
		mProgress.setMax(100);
		
		mWebView.loadUrl(mUrl);
		
		return v;
	}
}