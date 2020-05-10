package com.zetzaus.photogallery;

import android.content.Context;
import android.util.Log;

import com.zetzaus.photogallery.api.PhotoResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;

public class PollWorker extends Worker {

    private static final String TAG = PollWorker.class.getSimpleName();
    private Context mContext;

    public PollWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Worker request triggered!");

        String query = QueryPreferences.getStoredQuery(mContext);
        String lastPictureId = QueryPreferences.getLastResultId(mContext);

        Call<PhotoResponse> request;
        if (query != null) {
            request = new FlickrRepository().searchPhotoRequest(query);
        } else {
            request = new FlickrRepository().fetchPhotoRequest();
        }

        try {
            List<GalleryItem> items = request.execute().body().getGalleryItems();

            if (items.size() == 0) return Result.success();

            String newItemId = items.get(0).getId();
            if (!Objects.equals(lastPictureId, newItemId)) {
                Log.i(TAG, "New pictures found!");
                QueryPreferences.setLastResultId(mContext, newItemId);
                NotificationUtils.notifyNewPicture(mContext);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Result.success();
        }

        return Result.success();
    }
}
