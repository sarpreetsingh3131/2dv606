package dv606.sb223ce.news;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class ArticleAdapter extends ArrayAdapter<Article> {

    private LayoutInflater layoutInflater;

    ArticleAdapter(LayoutInflater layoutInflater) {
        super(layoutInflater.getContext(), R.layout.activity_main);
        this.layoutInflater = layoutInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.activity_main, parent, false);

        final Article article = getItem(position);
        if (article.image == null)
            ((ImageView) convertView.findViewById(R.id.articleImage)).setImageResource(R.mipmap.no_image);
        else
            ((ImageView) convertView.findViewById(R.id.articleImage)).setImageBitmap(article.image);
        ((TextView) convertView.findViewById(R.id.articleTitle)).setText(article.title);
        ((TextView) convertView.findViewById(R.id.articleSource)).setText(article.source);
        ((TextView) convertView.findViewById(R.id.articlePublishedAt)).setText(article.publishedAt);
        ((TextView) convertView.findViewById(R.id.articleDescription)).setText(article.description);
        ((ImageView) convertView.findViewById(R.id.articleShare)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
                intent.putExtra("sms_body", article.url);
                view.getContext().startActivity(Intent.createChooser(intent, "Share via"));
            }
        });
        return convertView;
    }
}