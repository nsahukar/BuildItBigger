package com.nsahukar.java.jokeslib;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class JokeProvider {

    private static final String JOKES_FILE_NAME = "/jokes.json";
    private ArrayList<String> mJokes;

    private void initJokes() {
        // Get jokes.json file from the resources folder
        InputStream jokesJsonInputStream = getClass().getResourceAsStream(JOKES_FILE_NAME);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(jokesJsonInputStream));
        Gson gson = new Gson();
        mJokes = gson.fromJson(jsonReader, ArrayList.class);
    }

    public String getJoke() {
        // initialize jokes if not already present
        if (mJokes == null) {
            initJokes();
        }
        // Pick up a random joke from the list of jokes
        Random random = new Random();
        int randomIndex = random.nextInt(mJokes.size());
        return mJokes.get(randomIndex);
    }

}
