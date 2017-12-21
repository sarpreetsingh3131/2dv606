package dv606.sb223ce.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.net.URL;

class Article {

    String source;
    String title;
    String description;
    String url;
    String publishedAt;
    Bitmap image;

    Article(JSONObject article, JSONObject response) {
        this.source = getSource(article, response);
        this.title = getValue(article, "title");
        this.description = getValue(article, "description");
        this.url = getValue(article, "url");
        this.image = getImage(article);
        this.publishedAt = getValue(article, "publishedAt");
        this.publishedAt = publishedAt.equalsIgnoreCase("null") ? "" : publishedAt.split("T")[0];
    }

    private Bitmap getImage(JSONObject article) {
        try {
            return BitmapFactory.decodeStream(new URL(article.getString("urlToImage")).openStream());
        } catch (Exception e) {
            return null;
        }
    }

    private String getSource(JSONObject article, JSONObject response) {
        try {
            return response.getString("source");
        } catch (Exception e) {
            try {
                return article.getJSONObject("source").getString("name");
            } catch (Exception e1) {
                return "";
            }
        }
    }

    private String getValue(JSONObject article, String key) {
        try {
            return article.getString(key);
        } catch (Exception e) {
            return key.equalsIgnoreCase("url") ? "http://www.smjfb.com/" : "";
        }
    }
}