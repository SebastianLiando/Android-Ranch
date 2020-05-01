package com.zetzaus.photogallery;

import android.content.Context;

import androidx.preference.PreferenceManager;

/**
 * This class is used to store and retrieve query in the <code>SharedPreference</code>.
 */
public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "prefSearchQuery";

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
}
