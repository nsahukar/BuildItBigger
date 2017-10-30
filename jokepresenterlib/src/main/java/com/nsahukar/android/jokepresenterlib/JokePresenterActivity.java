package com.nsahukar.android.jokepresenterlib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class JokePresenterActivity extends AppCompatActivity {

    private static final String TAG = JokePresenterActivity.class.getSimpleName();
    private static final String EXTRA_JOKE = "extra_joke";
    private String mJoke;

    /**
     *  Methods
     */

    public static Intent getPreparedIntent(Context context, final String joke) {
        Intent intent = new Intent(context, JokePresenterActivity.class);
        intent.putExtra(EXTRA_JOKE, joke);
        return intent;
    }

    private void initJoke() {
        TextView jokeTextView = (TextView) findViewById(R.id.joke_text_view);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Futura-Medium.ttf");
        jokeTextView.setTypeface(typeface);
        if (!TextUtils.isEmpty(mJoke)) {
            jokeTextView.setText(mJoke);
        } else {
            jokeTextView.setText(getString(R.string.text_no_joke));
        }
    }

    /**
     *  Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_presenter);

        // get joke from intent extra
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_JOKE)) {
                mJoke = extras.getString(EXTRA_JOKE);
                Log.e(TAG, "Joke: " + mJoke);
            }
        }

        // init joke
        initJoke();
    }

}
