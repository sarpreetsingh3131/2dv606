package dv606.sb223ce.news;

import android.content.Context;
import android.content.SharedPreferences;

class NewsPreferences {

    private static final String SOURCE_PREFS_NAME = "news.Source";
    private static final String SOURCE_PREF_PREFIX_KEY = "name";
    private static final String DEFAULT_SOURCE = "bbc-news";
    private static final String SORT_PREFS_NAME = "news.Source.SortBy";
    private static final String SORT_PREF_PREFIX_KEY = "sortBy";
    private static final String DEFAULT_SORT = "top";

    static void saveSortPref(Context context, String sort) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(SORT_PREFS_NAME, 0).edit();
        prefs.putString(SORT_PREF_PREFIX_KEY, sort);
        prefs.apply();
    }

    static String loadSortPref(Context context) {
        return context.getSharedPreferences(SORT_PREFS_NAME, 0).getString(SORT_PREF_PREFIX_KEY, DEFAULT_SORT);
    }

    static void saveSourcePref(Context context, String sourceName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(SOURCE_PREFS_NAME, 0).edit();
        prefs.putString(SOURCE_PREF_PREFIX_KEY, sourceName + ";" +
                context.getSharedPreferences(SOURCE_PREFS_NAME, 0).getString(SOURCE_PREF_PREFIX_KEY, DEFAULT_SOURCE));
        prefs.apply();
    }

    static String[] loadSourcePref(Context context) {
        return context.getSharedPreferences(SOURCE_PREFS_NAME, 0).getString(SOURCE_PREF_PREFIX_KEY, DEFAULT_SOURCE).split(";");
    }

    static void deleteSourcePref(Context context, String sourceName) {
        String[] oldSources = context.getSharedPreferences(SOURCE_PREFS_NAME, 0).getString(SOURCE_PREF_PREFIX_KEY, "").split(";");
        String newSources = "";
        for (String source : oldSources)
            if (!source.equalsIgnoreCase(sourceName)) newSources += source + ";";

        SharedPreferences.Editor prefs = context.getSharedPreferences(SOURCE_PREFS_NAME, 0).edit();
        prefs.putString(SOURCE_PREF_PREFIX_KEY, newSources);
        prefs.apply();
    }
}