package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

        final FloatingActionButton lockButton = ((FloatingActionButton)getView().findViewById(R.id.lockButton));

        lockButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        proxy.setEnabled(true);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posOk)));

                        lockButton.setImageResource(android.R.drawable.ic_media_pause);
                        break;

                    case MotionEvent.ACTION_UP:
                        proxy.setEnabled(false);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posNOk)));
                        lockButton.setImageResource(android.R.drawable.ic_media_play);

                        break;
                }

                return true;
            }


        });
    }


}
