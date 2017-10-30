package com.nsahukar.android.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.nsahukar.android.builditbigger.idlingresource.SimpleIdlingResource;
import com.nsahukar.android.jokepresenterlib.JokePresenterActivity;
import com.nsahukar.backend.builditbigger.jokeApi.JokeApi;
import com.udacity.gradle.builditbigger.R;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_JOKE = 11;

    private ProgressBar mProgressBar;
    private String mJoke;
    private InterstitialAd mInterstitialAd;
    @Nullable private SimpleIdlingResource mIdlingResource;     // For testing only


    /**
     *  Methods
     */

    private void showProgressBar() {
        if (mProgressBar == null) {
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        }
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    private void loadInterstitialAdd() {
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    loadInterstitialAdd();
                    // Show joke in JokePresenterActivity
                    showJoke(mJoke);
                }
            });
        } else {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    public void tellJoke(View view) {
        // For testing only
        getIdlingResource();

        // Use loader to get the joke from backend library
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> weatherDataLoader = loaderManager.getLoader(LOADER_JOKE);
        if (weatherDataLoader == null) {
            loaderManager.initLoader(LOADER_JOKE, null, this);
        }
    }

    private void showJoke(final String joke) {
        if (!TextUtils.isEmpty(joke)) {
            Intent jokePresenterIntent = JokePresenterActivity.getPreparedIntent(this, joke);
            startActivity(jokePresenterIntent);
        } else {
            Toast.makeText(this, getString(R.string.err_joke_retrieval_failed), Toast.LENGTH_SHORT).show();
        }

        // For testing only
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }


    /**
     *  Lifecycle methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInterstitialAdd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *  LoaderManager callbacks
     */

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(MainActivity.this) {

            private JokeApi aJokeApiService;
            private String aJoke;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                // For testing only
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(false);
                }

                // Show progress bar
                showProgressBar();

                // If joke already present then deliverResult
                if (aJoke != null) {
                    deliverResult(aJoke);
                }
                // Else forceLoad
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                if(aJokeApiService == null) {  // Only do this once
                    JokeApi.Builder builder = new JokeApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            // options for running against local devappserver
                            // - 10.0.2.2 is localhost's IP address in Android emulator
                            // - turn off compression when running against local devappserver
                            .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                            .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                @Override
                                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                    abstractGoogleClientRequest.setDisableGZipContent(true);
                                }
                            });
                    aJokeApiService = builder.build();
                }

                try {
                    // Call JokeApiService to get a joke
                    return aJokeApiService.getJoke().execute().getData();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    return null;
                }
            }

            @Override
            public void deliverResult(String joke) {
                super.deliverResult(joke);
                aJoke = joke;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String joke) {
        mJoke = joke;

        // hide progress bar
        hideProgressBar();

        // Destroy the current loader, so that no further actions take place using this loader
        getSupportLoaderManager().destroyLoader(LOADER_JOKE);

        // If interstitial ad is loaded, show it
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        // else show the joke
        else {
            showJoke(joke);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
