package com.zetzaus.photogallery;

import android.content.Context;

import androidx.preference.PreferenceManager;

/**
 * This class is used to store and retrieve query in the <code>SharedPreference</code>.
 */
public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "prefSearchQuery";
    private static final String PREF_LAST_RESULT_ID = "prefLastResultId";
    private static final String PREF_ALARM = "prefAlarm";

    /**
     * Returns the saved query in the <code>SharedPreference</code>. If nothing is saved, <code>null</code> will be returned.
     *
     * @param context the context.
     * @return the saved query in the <code>SharedPreference</code>.
     */
    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    /**
     * Saves the query to the <code>SharedPreference</code>.
     *
     * @param context the context.
     * @param query   the query to save.
     */
    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    /**
     * Returns the last fetched image Id.
     *
     * @param context the context used.
     * @return the last fetched image Id.
     */
    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_RESULT_ID, null);
    }

    /**
     * Sets the last fetched image Id.
     *
     * @param context the context used.
     * @param id      the last fetched image Id.
     */
    public static void setLastResultId(Context context, String id) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_LAST_RESULT_ID, id)
                .apply();
    }

    /**
     * Returns the state of the alarm persisted.
     *
     * @param context the context used.
     * @return the state of the alarm persisted.
     */
    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_ALARM, false);
    }

    /**
     * Sets the state of the alarm to the storage.
     *
     * @param context   the context used.
     * @param isAlarmOn the state of the alarm.
     */
    public static void setAlarmOn(Context context, boolean isAlarmOn) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_ALARM, isAlarmOn)
                .apply();
    }
}
