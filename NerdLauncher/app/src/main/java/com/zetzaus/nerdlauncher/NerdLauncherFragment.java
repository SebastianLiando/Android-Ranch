package com.zetzaus.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NerdLauncherFragment extends Fragment {

    private static final String TAG = NerdLauncherFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {

        NerdLauncherFragment fragment = new NerdLauncherFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();

        return v;
    }

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

    private class LauncherAdapter extends RecyclerView.Adapter<LauncherAdapter.ViewHolder> {

        private List<ResolveInfo> mActivities;

        public LauncherAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mActivities.get(position));
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ResolveInfo mResolveInfo;
            private TextView mTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mTextView = (TextView) itemView;
            }

            public void bind(ResolveInfo resolveInfo) {
                mResolveInfo = resolveInfo;
                PackageManager pm = getActivity().getPackageManager();
                String label = resolveInfo.loadLabel(pm).toString();
                mTextView.setText(label);

                mTextView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                ActivityInfo activityInfo = mResolveInfo.activityInfo;

                Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(
                        activityInfo.packageName,
                        activityInfo.name);

                startActivity(intent);
            }
        }
    }
}
