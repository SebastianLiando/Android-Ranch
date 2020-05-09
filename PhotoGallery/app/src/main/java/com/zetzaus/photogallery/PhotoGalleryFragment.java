package com.zetzaus.photogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;

/**
 * This <code>Fragment</code> displays a grid of images taken from Flickr.
 */
public class PhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private static final float ITEM_COLUMN_WIDTH = 400f;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private PhotoPagedAdapter mPhotoPagedAdapter;
    private ThumbnailDownloader<PhotoPagedAdapter.ViewHolder> mDownloader;

    private PhotoGalleryViewModel mViewModel;

    private PagedList<GalleryItem> mItems;
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

        mViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);

        // Start downloader thread
        Handler handler = new Handler();
        mDownloader = new ThumbnailDownloader<>(handler);
        mDownloader.setDownloadListener((target, thumbnail) -> {
            if (target != null) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
            }
        });
        getLifecycle().addObserver(mDownloader.fragmentObserver);

        getViewLifecycleOwnerLiveData().observe(this, lifecycleOwner -> {
            if (lifecycleOwner != null) {
                lifecycleOwner.getLifecycle().addObserver(mDownloader.viewObserver);
            }
        });
    }

    /**
     * Stops the downloader thread.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(mDownloader.fragmentObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        // Setup RecyclerView
        mRecyclerView = v.findViewById(R.id.recycler_view_photos);
        if (mPhotoPagedAdapter == null) {
            mPhotoPagedAdapter = new PhotoPagedAdapter();
        }
        mRecyclerView.setAdapter(mPhotoPagedAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (mItems == null) return;

                // Preloading
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    GridLayoutManager manager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    int start = Math.max(manager.findFirstVisibleItemPosition() - 10, 0);
                    int end = Math.min(manager.findLastVisibleItemPosition() + 10, mItems.size() - 1);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getLiveDataGalleryItems().observe(getViewLifecycleOwner(), galleryItems -> {
            mItems = galleryItems;
            mPhotoPagedAdapter.submitList(galleryItems);
        });
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
                mViewModel.setPhotoQuery(query);
                mProgressBar.setVisibility(View.VISIBLE);
                closeSearchView();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(v -> {
            String lastQuery = QueryPreferences.getStoredQuery(getActivity());
            mSearchView.setQuery(lastQuery, false);
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
                mViewModel.setPhotoQuery(null);
                mProgressBar.setVisibility(View.VISIBLE);
//                resetItems();
//                updateItems(mPage);
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

    private class PhotoPagedAdapter extends PagedListAdapter<GalleryItem, PhotoPagedAdapter.ViewHolder> {

        public PhotoPagedAdapter() {
            super(new DiffUtil.ItemCallback<GalleryItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull GalleryItem oldItem, @NonNull GalleryItem newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull GalleryItem oldItem, @NonNull GalleryItem newItem) {
                    return oldItem.equals(newItem);
                }
            });
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
            mProgressBar.setVisibility(GONE);
            GalleryItem item = getItem(position);
            // Display placeholder image
            Drawable drawable = getResources().getDrawable(R.drawable.ic_placeholder);
            holder.bindDrawable(drawable);

            holder.bindGalleryItem(item);

            String url = item.getURL();
            Bitmap cacheBitmap = mDownloader.retrieveCache(holder, url);

            if (cacheBitmap != null) {
                holder.bindDrawable(new BitmapDrawable(getResources(), cacheBitmap));
            } else {
                mDownloader.queueThumbnail(holder, url);
            }
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
                mTextView.setVisibility(GONE);
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
                handler.postDelayed(() -> mTextView.setVisibility(GONE), 2000);
                return true;
            }
        }
    }
}
