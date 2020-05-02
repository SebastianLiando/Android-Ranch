package com.zetzaus.photogallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This fragment displays a web page from Flickr.
 */
public class PhotoPageFragment extends VisibleFragment {

    private static final String EXTRA_URI = "Extra Uri";

    private Uri mImageUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    /**
     * Returns an instance of this fragment.
     *
     * @param imageUri the image Uri.
     * @return an instance of this fragment.
     */
    public static PhotoPageFragment newInstance(Uri imageUri) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_URI, imageUri);

        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Retrieves the image Uri.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageUri = getArguments().getParcelable(EXTRA_URI);
    }

    /**
     * Sets the fragment up.
     *
     * @param inflater           the layout inflater.
     * @param container          the container to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the inflated layout.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mWebView = v.findViewById(R.id.web_view);
        mProgressBar = v.findViewById(R.id.progress_bar);

        // Setup WebView
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
                    loadViewIntent(Uri.parse(url));
                } else {
                    super.onLoadResource(view, url);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Uri uri = request.getUrl();
                    if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
                        return false;
                    } else {
                        loadViewIntent(uri);
                        return true;
                    }
                }

                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });

        mWebView.loadUrl(mImageUri.toString());

        return v;
    }

    /**
     * Returns true if the <code>WebView</code> can still go back.
     *
     * @return true if the <code>WebView</code> can still go back.
     */
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    /**
     * Starts another activity via implicit intent.
     *
     * @param uri the uri.
     */
    private void loadViewIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (getActivity().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.toast_no_activity, Toast.LENGTH_SHORT).show();
        }

    }
}
