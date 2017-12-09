package dv606.sb223ce.assignment1;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class BeerPagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_pager);
        ((ViewPager) findViewById(R.id.beerPagerViewPager)).setAdapter(new BeerPagerAdapter(getFragmentManager()));
    }

    class BeerPagerAdapter extends FragmentPagerAdapter {

        BeerPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            return BeerPagerFragment.create(BeerKit.IMAGES[i], BeerKit.NAMES[i], BeerKit.RATINGS[i],
                    BeerKit.BREWERY[i], BeerKit.STYLES[i], BeerKit.ABV[i], BeerKit.REVIEWS[i], i + 1);
        }

        @Override
        public int getCount() {
            return BeerKit.NAMES.length;
        }
    }

    public static class BeerPagerFragment extends Fragment {

        private static final String IMAGE = "image", NAME = "name", RATING = "rating", BREWERY = "brewery", STYLE = "style",
                ABV = "abv", REVIEW = "review", POSITION = "position";
        private ViewGroup rootView;

        static BeerPagerFragment create(int image, String name, String rating, String brewery, String style, String abv, String review, int position) {
            BeerPagerFragment fragment = new BeerPagerFragment();
            Bundle args = new Bundle();
            args.putInt(IMAGE, image);
            args.putString(NAME, name);
            args.putString(RATING, rating);
            args.putString(BREWERY, brewery);
            args.putString(STYLE, style);
            args.putString(ABV, abv);
            args.putString(REVIEW, review);
            args.putInt(POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_beer_pager, container, false);
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            // Set values
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.beerPagerTitleTextView)).setText("Beer " + args.getInt(POSITION) + " out of " + BeerKit.NAMES.length);
            ((TextView) rootView.findViewById(R.id.beerPagerNameTextView)).setText(args.getString(NAME));
            ((TextView) rootView.findViewById(R.id.beerPagerStyleTextView)).setText(args.getString(STYLE) + " (" + args.getString(ABV) + ")");
            ((ImageView) rootView.findViewById(R.id.beerPagerImageView)).setImageResource(args.getInt(IMAGE));
            ((TextView) rootView.findViewById(R.id.beerPagerBreweryTextView)).setText(args.getString(BREWERY));
            ((RatingBar) rootView.findViewById(R.id.beerPagerRatingBar)).setRating(Float.valueOf(args.getString(RATING)));
            ((TextView) rootView.findViewById(R.id.beerPagerReviewTextView)).setText(args.getString(REVIEW));
        }
    }
}