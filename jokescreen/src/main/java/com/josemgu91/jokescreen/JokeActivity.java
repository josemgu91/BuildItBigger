package com.josemgu91.jokescreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

    public static final String PARAM_JOKE = "com.josemgu91.jokescreen.JOKE";

    public static void start(final Context context, final String joke) {
        context.startActivity(new Intent(context, JokeActivity.class)
                .putExtra(PARAM_JOKE, joke));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);
        if (getIntent().hasExtra(PARAM_JOKE)) {
            final String joke = getIntent().getStringExtra(PARAM_JOKE);
            ((TextView) findViewById(R.id.textViewJoke))
                    .setText(joke);
        } else {
            throw new RuntimeException("PARAM_JOKE intent value missing!");
        }
    }
}
