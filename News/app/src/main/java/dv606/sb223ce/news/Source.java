package dv606.sb223ce.news;

import android.content.Context;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class Source {

    String name;
    int imageId;

    Source(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    static boolean isSubscribing(Context context, String name) {
        for (String names : NewsPreferences.loadSourcePref(context))
            if (names.equalsIgnoreCase(name)) return true;
        return false;
    }

    static final List<Source> SOURCES = new LinkedList<>(Arrays.asList(
            new Source("abc-news-au", R.mipmap.abc_news_au),
            new Source("al-jazeera-english", R.mipmap.al_jazeera_english),
            new Source("bbc-sport", R.mipmap.bbc_sport),
            new Source("cnn", R.mipmap.cnn),
            new Source("daily-mail", R.mipmap.daily_mail),
            new Source("espn", R.mipmap.espn),
            new Source("the-new-york-times", R.mipmap.the_new_york_times),
            new Source("the-wall-street-journal", R.mipmap.the_wall_street_journal)
    ));
}