package com.zetzaus.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This fragment displays a list of apps installed in the Android device.
 */
public class NerdLauncherFragment extends Fragment {

    private static final String TAG = NerdLauncherFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    /**
     * Returns a new instance of this fragment.
     *
     * @return a new instance of this fragment.
     */
    public static NerdLauncherFragment newInstance() {

        NerdLauncherFragment fragment = new NerdLauncherFragment();
        return fragment;
    }

    /**
     * Inflates the layout and setups the fragment
     *
     * @param inflater           the layout inflater.
     * @param container          the container.
     * @param savedInstanceState the saved system state.
     * @return the inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();

        return v;
    }

    /**
     * Sets up the <code>RecyclerView</code>'s adapter.
     */
    private void setupAdapter() {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(launcherIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER
                        .compare(o1.loadLabel(pm).toString(), o2.loadLabel(pm).toString());
            }
        });

        Log.i(TAG, activities.size() + " activities.");

        mRecyclerView.setAdapter(new LauncherAdapter(activities));
    }

    /**
     * This class is a custom adapter for the <code>RecyclerView</code>.
     */
    private class LauncherAdapter extends RecyclerView.Adapter<LauncherAdapter.ViewHolder> {

        private List<ResolveInfo> mActivities;

        /**
         * Creates a <code>LauncherAdapter</code>.
         *
         * @param activities
         */
        public LauncherAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        /**
         * Creates a <code>ViewHolder</code>.
         *
         * @param parent   the parent layout.
         * @param viewType the view type. Not used.
         * @return the <code>ViewHolder</code>.
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_app,
                    parent, false);
            return new ViewHolder(v);
        }

        /**
         * Binds the <code>ViewHolder</code> with the data.
         *
         * @param holder   the <code>ViewHolder</code>.
         * @param position the binding position.
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mActivities.get(position));
        }

        /**
         * Returns the number of items in the <code>RecyclerView</code>.
         *
         * @return the number of items in the <code>RecyclerView</code>.
         */
        @Override
        public int getItemCount() {
            return mActivities.size();
        }

        /**
         * This class is a custom <code>ViewHolder</code> for the <code>LauncherAdapter</code>.
         */
        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ResolveInfo mResolveInfo;
            private TextView mTextView;
            private ImageView mImageView;

            /**
             * Creates a <code>ViewHolder</code>.
             *
             * @param itemView the layout.
             */
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.text_view);
                mImageView = itemView.findViewById(R.id.image_icon);
            }

            /**
             * Binds the data to the <code>View</code>s.
             *
             * @param resolveInfo the data to be bound.
             */
            public void bind(ResolveInfo resolveInfo) {
                mResolveInfo = resolveInfo;
                PackageManager pm = getActivity().getPackageManager();
                String label = resolveInfo.loadLabel(pm).toString();
                Drawable icon = resolveInfo.loadIcon(pm);
                mTextView.setText(label);
                Glide.with(getActivity()).load(icon).into(mImageView);

                mTextView.setOnClickListener(this);
            }

            /**
             * Callback method for click.
             *
             * @param v the clicked item.
             */
            @Override
            public void onClick(View v) {
                ActivityInfo activityInfo = mResolveInfo.activityInfo;

                Intent intent = new Intent(Intent.ACTION_MAIN)
                        .setClassName(activityInfo.packageName, activityInfo.name)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        }
    }
}
