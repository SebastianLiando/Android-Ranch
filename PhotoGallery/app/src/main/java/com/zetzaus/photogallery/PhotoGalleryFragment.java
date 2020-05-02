package com.zetzaus.photogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * This <code>Fragment</code> displays a grid of images taken from Flickr.
 */
public class PhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private static final float ITEM_COLUMN_WIDTH = 400f;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private PhotoAdapter mPhotoAdapter;
    private ThumbnailDownloader<PhotoAdapter.ViewHolder> mDownloader;

    private List<GalleryItem> mItems = new ArrayList<>();
    private int mPage = 1;

    /**
     * Returns an instance of this fragment.
     *
     * @return an instance of this fragment.
     */
    public static PhotoGalleryFragment newInstance() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        return fragment;
    }

    /**
     * Retains this fragment on device configuration change and start the downloader thread.
     *
     * @param savedInstanceState the saved system state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems(mPage);

        // Start downloader thread
        Handler handler = new Handler();
        mDownloader = new ThumbnailDownloader<>(handler);
        mDownloader.setDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoAdapter.ViewHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoAdapter.ViewHolder target, Bitmap thumbnail) {
                if (target != null) {
                    Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                    target.bindDrawable(drawable);
                }
            }
        });
        mDownloader.start();
        // Block main thread until ready
        mDownloader.getLooper();
        Log.i(TAG, "Thumbnail downloader started");
    }

    /**
     * Clear the Handler's message queue when the <code>View</code> is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Handle rotation
        mDownloader.clearQueue();
    }

    /**
     * Stops the downloader thread.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloader.quit();
        Log.i(TAG, "Thumbnail downloader stopped");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        // Setup RecyclerView
        mRecyclerView = v.findViewById(R.id.recycler_view_photos);
        setupAdapter();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!mRecyclerView.canScrollVertically(1)) {
                    // Get the next page
                    updateItems(++mPage);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    GridLayoutManager manager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    int start = max(manager.findFirstVisibleItemPosition() - 10, 0);
                    int end = min(manager.findLastVisibleItemPosition() + 10, mItems.size() - 1);
                    // Preload previous 10
                    for (int i = start; i < manager.findFirstVisibleItemPosition(); i++) {
                        String url = mItems.get(i).getURL();
                        if (mDownloader.retrieveCache(url) == null)
                            mDownloader.queuePreload(url);

                    }
                    // Preload next 10
                    for (int i = manager.findLastVisibleItemPosition() + 1; i <= end; i++) {
                        String url = mItems.get(i).getURL();
                        if (mDownloader.retrieveCache(url) == null)
                            mDownloader.queuePreload(url);

                    }
                }
            }
        });
        mRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int cols = Math.round(mRecyclerView.getWidth() / ITEM_COLUMN_WIDTH);
                        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), cols));
                        // Done and remove
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        mProgressBar = v.findViewById(R.id.progress_bar);

        return v;
    }

    /**
     * Inflates menu actions to the toolbar.
     *
     * @param menu     the menu.
     * @param inflater the inflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryPreferences.setStoredQuery(getActivity(), query);
                resetItems();
                updateItems(mPage);
                closeSearchView();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SearchView clicked");
                String lastQuery = QueryPreferences.getStoredQuery(getActivity());
                mSearchView.setQuery(lastQuery, false);
            }
        });

        MenuItem pollItem = menu.findItem(R.id.menu_toggle_poll);
        boolean scheduled = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            scheduled = PollJobScheduler.isScheduled(getActivity());
        else
            scheduled = PollService.isAlarmOn(getActivity());

        if (scheduled) {
            pollItem.setTitle(getString(R.string.menu_stop_poll));
        } else {
            pollItem.setTitle(getString(R.string.menu_start_poll));
        }
    }

    /**
     * Handles option items when selected.
     *
     * @param item the selected option item.
     * @return true if the action has been handled.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                resetItems();
                updateItems(mPage);
                closeSearchView();
                return true;
            case R.id.menu_toggle_poll:
                boolean active;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    active = !PollJobScheduler.isScheduled(getActivity());
                    PollJobScheduler.scheduleJob(getActivity(), active);
                } else {
                    active = !PollService.isAlarmOn(getActivity());
                    PollService.setServiceAlarm(getActivity(), active);
                }

                // Change the toolbar text
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Attaches <code>PhotoAdapter</code> to the <code>RecyclerView</code>.
     */
    private void setupAdapter() {
        // AsyncTask callbacks may call when the fragment is not attached
        if (isAdded()) {
            if (mRecyclerView.getAdapter() == null) {
                mPhotoAdapter = new PhotoAdapter(mItems);
                mRecyclerView.setAdapter(mPhotoAdapter);
            }
        }
    }

    /**
     * Updates the list of items by downloading new items.
     *
     * @param page the page number to be downloaded.
     */
    private void updateItems(int page) {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemTask(query, page).execute();
    }

    /**
     * Clears the list and sets the page to the first.
     */
    private void resetItems() {
        mItems.clear();
        mPhotoAdapter = new PhotoAdapter(mItems);
        mRecyclerView.setAdapter(mPhotoAdapter);
        mPage = 1;

        // Enable progress bar
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Collapses the SearchView and hide keyboard input.
     */
    private void closeSearchView() {
        if (!mSearchView.isIconified()) {
            // Close keyboard
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(mSearchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            // Close SearchView
            mSearchView.onActionViewCollapsed();
        }
    }

    private class FetchItemTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private int mPage;
        private String mQuery;

        /**
         * Creates a <code>FetchItemTask</code> for a page number.
         *
         * @param page the page number.
         */
        public FetchItemTask(String query, int page) {
            mQuery = query;
            mPage = page;
        }

        /**
         * Fetches photo data from Flickr.
         *
         * @param voids nothing.
         * @return list of photo data.
         */
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            // Query
            if (mQuery == null)
                return new FlickrFetchr().fetchRecentPhotos(mPage);
            else
                return new FlickrFetchr().searchPhotos(mQuery, mPage);
        }

        /**
         * Adds the photo data to the list and notify adapter of such change.
         *
         * @param galleryItems the new photo data to be added.
         */
        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems.addAll(galleryItems);
            mPhotoAdapter.notifyDataSetChanged();

            // Disable progress bar
            mProgressBar.setVisibility(GONE);
        }
    }

    /**
     * Custom <code>RecyclerView</code> adapter for displaying images.
     */
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
        private List<GalleryItem> mGalleryItems;

        /**
         * Create an adapter.
         *
         * @param galleryItems the list of items to display.
         */
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        /**
         * Creates a <code>ViewHolder</code>.
         *
         * @param parent   the parent layout.
         * @param viewType the view type (not used).
         * @return a <code>ViewHolder</code>.
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_photo, parent, false);
            return new ViewHolder(v);
        }

        /**
         * Binds the <code>ViewHolder</code> with the data.
         *
         * @param holder   the <code>ViewHolder</code>.
         * @param position the position.
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Display placeholder image
            Drawable drawable = getResources().getDrawable(R.drawable.ic_placeholder);
            holder.bindDrawable(drawable);

            holder.bindGalleryItem(mGalleryItems.get(position));

            String url = mGalleryItems.get(position).getURL();
            Bitmap cacheBitmap = mDownloader.retrieveCache(holder, url);

            if (cacheBitmap != null) {
                holder.bindDrawable(new BitmapDrawable(getResources(), cacheBitmap));
            } else {
                mDownloader.queueThumbnail(holder, url);
            }
        }

        /**
         * Returns the number of items to display.
         *
         * @return the number of items to display.
         */
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        /**
         * Custom <code>ViewHolder</code>.
         */
        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            private ImageView mImageView;
            private TextView mTextView;
            private GalleryItem mGalleryItem;

            /**
             * Creates a <code>VewHolder</code>.
             *
             * @param itemView the layout.
             */
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.image_view_photo);
                mTextView = itemView.findViewById(R.id.text_caption);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            /**
             * Sets the image.
             *
             * @param drawable the image.
             */
            public void bindDrawable(Drawable drawable) {
                mImageView.setImageDrawable(drawable);
            }

            /**
             * Sets the item data.
             *
             * @param galleryItem the item data.
             */
            public void bindGalleryItem(GalleryItem galleryItem) {
                mGalleryItem = galleryItem;
                mImageView.setContentDescription(mGalleryItem.getCaption());
            }

            /**
             * Starts <code>PhotoPageActivity</code> when the image is clicked.
             *
             * @param v the clicked item.
             */
            @Override
            public void onClick(View v) {
                Intent webIntent = PhotoPageActivity.newIntent(getActivity(), mGalleryItem.getPhotoPageUri());
                startActivity(webIntent);
            }

            /**
             * Displays caption when the image is held.
             *
             * @param v the held item.
             * @return true (handled).
             */
            @Override
            public boolean onLongClick(View v) {
                // Display caption and darken
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(mGalleryItem.getCaption());
                // Disappear after 3 seconds
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setVisibility(GONE);
                    }
                }, 2000);
                return true;
            }
        }
    }
}
