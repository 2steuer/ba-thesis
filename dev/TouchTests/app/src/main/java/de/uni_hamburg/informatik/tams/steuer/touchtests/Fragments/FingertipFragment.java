package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_hamburg.informatik.tams.steuer.touchtests.FingerTipMapping.DftmProxy;
import de.uni_hamburg.informatik.tams.steuer.touchtests.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FingertipFragment extends Fragment {
    DftmProxy proxy;

    public FingertipFragment() {
        // Required empty public constructor
        proxy = DftmProxy.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fingertip, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        DisplayMetrics met = getResources().getDisplayMetrics();
        proxy.setScreenMetrics(getView().getWidth(), getView().getHeight(), met.densityDpi);
    }


}
