package com.nsahukar.android.builditbigger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.udacity.gradle.builditbigger.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Futura-Medium.ttf");

        TextView instructionsTextView = (TextView) root.findViewById(R.id.instructions_text_view);
        instructionsTextView.setTypeface(typeface);

        Button tellJokeButton = (Button) root.findViewById(R.id.tell_joke_button);
        tellJokeButton.setTypeface(typeface);

        return root;
    }
}
