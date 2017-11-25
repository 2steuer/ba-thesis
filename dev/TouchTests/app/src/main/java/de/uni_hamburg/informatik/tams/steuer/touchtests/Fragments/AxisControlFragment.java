package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.AxisControlView;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.Interfaces.AxisControllerObserver;
import de.uni_hamburg.informatik.tams.steuer.touchtests.R;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.AxisInformationImpl;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AxisInformation;

/**
 * Created by merlin on 25.11.17.
 */

public class AxisControlFragment extends Fragment {
    HashMap<String, AxisControlView> controls = new HashMap<String, AxisControlView>();
    AxisManager axisManager;

    AxisControllerObserver axisCallback = new AxisControllerObserver() {
        @Override
        public void onStartMoving(String axisIdentifier, int direction) {
            axisManager.startMoving(axisIdentifier, 2 * Math.signum(direction));
        }

        @Override
        public void onStopMoving(String axisIdentifier) {
            axisManager.stopMoving(axisIdentifier);
        }
    };

    public AxisControlFragment() {
        axisManager = AxisManager.getInstance();

        axisManager.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                updateAllControllers();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_axiscontrol, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //only populate the map once
        if(controls.size() == 0 && getView() instanceof ViewGroup) {
            ViewGroup grp = (ViewGroup)getView();
            populateAxisControllerMap(grp);
        }


    }

    @Override
    public void onStop() {
        super.onStop();

    }


    private void populateAxisControllerMap(ViewGroup grp) {
        for(int i = 0; i < grp.getChildCount(); i++) {
            View v = grp.getChildAt(i);
            if(v instanceof AxisControlView) {
                AxisControlView acv = (AxisControlView)v;
                controls.put(acv.getAxisIdentifier(), acv);
                acv.setObserver(axisCallback);
            }
            else if(v instanceof ViewGroup) {
                populateAxisControllerMap((ViewGroup)v);
            }
        }
    }

    private void updateAllControllers() {
        for(AxisInformation i : axisManager.getAllAxisInfos()) {
            if(controls.containsKey(i.getIdentifier())) {
                AxisControlView acv = controls.get(i.getIdentifier());
                acv.setTargetAngle(i.getTargetValue());
                acv.setCurrentAngle(i.getCurrentValue());
            }
        }
    }
}
