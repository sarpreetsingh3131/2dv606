package dv606.sb223ce.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

class ArticleRetriever extends AsyncTask<String, List<Article>, List<Article>> {

    private final String KEY = "&apiKey=71f12b1d877b4b0882f1e2c2ce163a34";
    private final String URL = "https://newsapi.org/v1/articles?source=";
    private final String SORT_BY = "&sortBy=";
    private final String SEARCH = "https://newsapi.org/v2/everything?q=";
    private ArticleAdapter adapter;
    private ProgressDialog dialog;

    ArticleRetriever(ArticleAdapter adapter) throws UnsupportedOperationException {
        this.adapter = adapter;
        if (!isNetworkAvailable()) throw new UnsupportedOperationException();
        dialog = new ProgressDialog(adapter.getContext());
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Loading, Please Wait...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
    }

    @Override
    protected List<Article> doInBackground(String... strings) {
        return fetchHeadlines(strings[0], strings[1], strings[2]);
    }

    @Override
    protected void onPostExecute(List<Article> articles) {
        dialog.dismiss();
        adapter.addAll(articles);
        if (articles.isEmpty())
            Toast.makeText(adapter.getContext(), "No Result found...", Toast.LENGTH_LONG).show();
    }

    List<Article> fetchHeadlines(String sortBy, String source, String query) {
        List<Article> articles = new LinkedList<>();
        try {
            URL url = query.equals("") ? new URL(URL + source + SORT_BY + sortBy + KEY) : new URL(SEARCH + query + KEY);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            http.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while (((line = reader.readLine()) != null)) stringBuilder.append(line + "\n");

            reader.close();
            http.disconnect();

            JSONObject response = new JSONObject(stringBuilder.toString());
            JSONArray jsonArticles = response.getJSONArray("articles");

            for (int j = 0; j < jsonArticles.length(); j++)
                articles.add(new Article(jsonArticles.getJSONObject(j), response));
        } catch (Exception e) {
            return articles;
        }
        return articles;
    }

    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) adapter.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}