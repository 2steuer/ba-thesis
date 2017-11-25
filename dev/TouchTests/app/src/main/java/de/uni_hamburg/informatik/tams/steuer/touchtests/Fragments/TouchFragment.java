package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_hamburg.informatik.tams.steuer.touchtests.R;

/**
 * Created by merlin on 25.11.17.
 */

public class TouchFragment extends Fragment {

    public TouchFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_touch, container, false);
    }


}
