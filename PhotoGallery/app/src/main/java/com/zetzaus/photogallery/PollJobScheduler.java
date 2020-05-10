package com.zetzaus.photogallery;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;

/**
 * This class is the <code>JobService</code> version for checking new images.
 * @deprecated use {@link PollWorker}
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobScheduler extends JobService {

    private static final String TAG = PollJobScheduler.class.getSimpleName();

    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15);
    private static final int JOB_ID = 1000;

    private CheckNewAsyncTask mAsyncTask;

    /**
     * Checks for new image.
     *
     * @param params the job parameter.
     * @return true (background thread).
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        String query = QueryPreferences.getStoredQuery(PollJobScheduler.this);
        mAsyncTask = new CheckNewAsyncTask(query, params);
        mAsyncTask.execute();
        return true;
    }

    /**
     * No reschedule job.
     *
     * @param params the job parameter.
     * @return false for no reschedule.
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        if (mAsyncTask != null) mAsyncTask.cancel(true);
        return false;
    }

    /**
     * Returns a <code>JobInfo</code> containing the constraints. The user must be connected to the WiFi or
     * other un-metered network.
     *
     * @param context the context used.
     * @return a <code>JobInfo</code> containing the constraints.
     */
    private static JobInfo buildJobInfo(Context context) {
        ComponentName service = new ComponentName(context.getPackageName(), PollJobScheduler.class.getName());
        return new JobInfo.Builder(JOB_ID, service)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(POLL_INTERVAL_MS)
                .setPersisted(true)
                .build();
    }

    /**
     * Schedules the Job to be fired or cancels the scheduled Job.
     *
     * @param context  the context.
     * @param schedule true to schedule Job, otherwise false.
     */
    public static void scheduleJob(Context context, boolean schedule) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = buildJobInfo(context);
        if (schedule) {
            scheduler.schedule(jobInfo);
        } else {
            scheduler.cancel(JOB_ID);
        }
    }

    /**
     * <code>True</code> if the job has been scheduled.
     *
     * @param context the context used.
     * @return <code>True</code> if the job has been scheduled.
     */
    public static boolean isScheduled(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        for (JobInfo job : scheduler.getAllPendingJobs()) {
            if (job.getId() == JOB_ID) return true;
        }

        return false;
    }

    /**
     * Custom <code>AsyncTask</code> to check for new images.
     */
    private class CheckNewAsyncTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private String mQuery;
        private JobParameters mJobParameters;

        /**
         * Creates a <code>CheckNewAsyncTask</code>.
         *
         * @param query      the search query if any.
         * @param parameters the job parameter.
         */
        public CheckNewAsyncTask(String query, JobParameters parameters) {
            mQuery = query;
            mJobParameters = parameters;
        }

        /**
         * Fetches images list from the Flickr website.
         *
         * @param voids nothing.
         * @return images list from the Flickr website.
         */
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos(1);
            } else {
                return new FlickrFetchr().searchPhotos(mQuery, 1);
            }
        }

        /**
         * Sends notification to the user's device if there is a new image available.
         *
         * @param galleryItems the images list.
         */
        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            if (galleryItems.size() > 0) {
                String lastResultId = QueryPreferences.getLastResultId(PollJobScheduler.this);

                String resultId = galleryItems.get(0).getId();
                if (resultId.equals(lastResultId)) {
                    Log.i(TAG, "Fetched an old one");
                } else {
                    Log.i(TAG, "Fetched a new one");
                    NotificationUtils.notifyNewPicture(PollJobScheduler.this);
                }

                QueryPreferences.setLastResultId(PollJobScheduler.this, resultId);
            }
        }
    }
}
