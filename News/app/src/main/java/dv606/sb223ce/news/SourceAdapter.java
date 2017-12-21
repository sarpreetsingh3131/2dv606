package dv606.sb223ce.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

class SourceAdapter extends ArrayAdapter<Source> {

    private LayoutInflater inflater;

    SourceAdapter(Context context, LayoutInflater inflater) {
        super(context, R.layout.activity_settings, Source.SOURCES);
        this.inflater = inflater;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_settings, parent, false);
        final Source source = getItem(position);
        ((ImageView) convertView.findViewById(R.id.sourceImage)).setImageResource(source.imageId);
        final Switch s = convertView.findViewById(R.id.sourceSwitch);
        s.setChecked(Source.isSubscribing(getContext(), source.name));
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    NewsPreferences.saveSourcePref(getContext(), getItem(position).name);
                else NewsPreferences.deleteSourcePref(getContext(), getItem(position).name);
            }
        });
        return convertView;
    }
}