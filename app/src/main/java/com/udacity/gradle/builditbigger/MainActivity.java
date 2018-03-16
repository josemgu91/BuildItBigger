package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.josemgu91.jokescreen.JokeActivity;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;
import com.udacity.gradle.builditbigger.test.SimpleIdlingResource;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.MainActivityFragmentInterface {

    @Nullable
    private SimpleIdlingResource simpleIdlingResource;

    private RemoteJokeAsyncTask remoteJokeAsyncTask;

    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleIdlingResource = (SimpleIdlingResource) getIdlingResource();
        simpleIdlingResource.setIdleState(false);
        ((MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment))
                .setMainActivityFragmentInterface(this);
        progress = findViewById(R.id.progress);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (remoteJokeAsyncTask != null) {
            remoteJokeAsyncTask.cancel(true);
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (simpleIdlingResource == null) {
            simpleIdlingResource = new SimpleIdlingResource();
        }
        return simpleIdlingResource;
    }

    @Override
    public void onShowJokeButtonClick() {
        remoteJokeAsyncTask = new RemoteJokeAsyncTask();
        remoteJokeAsyncTask.execute();
    }

    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    private void dismissProgress() {
        progress.setVisibility(View.GONE);
    }

    private class RemoteJokeAsyncTask extends AsyncTask<Void, Void, Bundle> {
        private MyApi myApiService = null;

        private static final String KEY_JOKE = "joke";
        private static final String KEY_HAS_ERROR = "has_error";
        private static final String KEY_ERROR_MESSAGE = "error_message";

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected Bundle doInBackground(Void... params) {
            if (myApiService == null) {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                myApiService = builder.build();
            }
            final Bundle response = new Bundle();
            try {
                final String serverResponse = myApiService.sayJoke().execute().getData();
                response.putString(KEY_JOKE, serverResponse);
                response.putBoolean(KEY_HAS_ERROR, false);
            } catch (IOException e) {
                e.printStackTrace();
                final String exceptionMessage = e.getMessage();
                response.putBoolean(KEY_HAS_ERROR, true);
                response.putString(KEY_ERROR_MESSAGE, exceptionMessage);
            }
            return response;
        }

        @Override
        protected void onPostExecute(Bundle response) {
            dismissProgress();
            simpleIdlingResource.setIdleState(true);
            if (response.getBoolean(KEY_HAS_ERROR)) {
                Toast.makeText(MainActivity.this, R.string.joke_server_error, Toast.LENGTH_SHORT).show();
            } else {
                JokeActivity.start(MainActivity.this, response.getString(KEY_JOKE));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dismissProgress();
        }
    }

}