package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;

import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.GestureView;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.GestureParser;
import de.uni_hamburg.informatik.tams.steuer.touchtests.R;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.SynergyProxy;
import hdbt.shadow.GraspSynergy;

/**
 * Created by merlin on 25.11.17.
 */

public class TouchFragment extends Fragment {

    GestureParser _gestures;
    AxisManager _axes;
    SynergyProxy _synergyProxy = new SynergyProxy();

    public TouchFragment() {
        _axes = AxisManager.getInstance();



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

    @Override
    public void onStart() {
        super.onStart();

        _gestures = ((GestureView)getView().findViewById(R.id.gestView)).getGestureParser();
        _gestures.addObserver(_synergyProxy);
        final FloatingActionButton lockButton = ((FloatingActionButton)getView().findViewById(R.id.lockButton));

        lockButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        _axes.setLocked(false);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posOk)));

                        lockButton.setImageResource(android.R.drawable.ic_media_pause);
                        break;

                    case MotionEvent.ACTION_UP:
                        _axes.setLocked(false);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posNOk)));
                        lockButton.setImageResource(android.R.drawable.ic_media_play);

                        break;
                }

                return true;
            }


        });

        GraspSynergy testSyn = new GraspSynergy(21);
        try {
            testSyn.parseMatlabSynergyMean(getResources().openRawResource(R.raw.g1mean));
            testSyn.parseMatlabSynergyVecs(getResources().openRawResource(R.raw.g1vecs));
        } catch (Exception ex) {
            Log.e("TouchFragment", "Error while parsing test synergy.");
            Log.e("TouchFragment", ex.getMessage());
        }

        _synergyProxy.setGraspSynergy(testSyn);

    }

    @Override
    public void onResume() {
        super.onResume();
        _synergyProxy.setAxisManager(_axes);

    }

    @Override
    public void onPause() {
        super.onPause();
        _synergyProxy.setAxisManager(null);
    }


}
