package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Interfaces.GestureObserver;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.GestureState;
import hdbt.shadow.GraspSynergy;

/**
 * Created by merlin on 20.02.18.
 */

public class SynergyProxy implements GestureObserver {
    private static final int CONTROLLED_AMPLITUDES = 3;

    // Mappings from synergy output index to axis name
    private static final String[] JointMapping = {
           "FFJ1",
           "FFJ2",
           "FFJ3",
           "FFJ4",

            "MFJ1",
            "MFJ2",
            "MFJ3",
            "MFJ4",

            "RFJ1",
            "RFJ2",
            "RFJ3",
            "RFJ4",

            "LFJ1",
            "LFJ2",
            "LFJ3",
            "LFJ4",

            "THJ1",
            "THJ2",
            "THJ3",
            "THJ4",
            "THJ5",
    };

    private static final int _zeroAmplitudeSize = 200;
    private static final int _fullAmplitudeSize = 10;

    GraspSynergy _currentSynergy = null;

    HashMap<Integer, GestureState> _gestures = new HashMap<>();

    private double[] _amplitudes = new double[CONTROLLED_AMPLITUDES];

    AxisManager _axes = null;

    public SynergyProxy() {
        _gestures.put(1, new GestureState());
        _gestures.put(2, new GestureState());
        _gestures.put(3, new GestureState());
        _gestures.put(4, new GestureState());
        _gestures.put(5, new GestureState());
    }

    public void setAxisManager(AxisManager manager) {
        _axes = manager;
    }

    public void setGraspSynergy(GraspSynergy grasp) {
        _currentSynergy = grasp;
    }

    @Override
    public void onGestureAdd(Gesture g) {
        int pc = g.getPointerCount();
        GestureState gs = _gestures.get(pc);

        if(gs == null) {
            return;
        }

        if(_gestures.get(pc).getGesture() == null)
        {
            _gestures.get(pc).setGesture(g);
        }
    }

    @Override
    public void onGestureRemove(Gesture g) {
        int pc = g.getPointerCount();
        GestureState gs = _gestures.get(pc);

        if(gs == null) {
            return;
        }

        if(_gestures.get(pc).getGesture() == g)
        {
            _gestures.get(pc).setGesture(null);
        }
    }

    @Override
    public void onGestureChanged(Gesture g) {
        int pc = g.getPointerCount();
        GestureState gs = _gestures.get(pc);
        if(gs == null || gs.getGesture() != g) {
            return;
        }

        if(gs.getPointerCount() != g.getPointerCount()) {
            gs.setGesture(null);

            GestureState newGs = _gestures.get(pc);
            if(newGs != null && newGs.getGesture() == null) {
                newGs.setGesture(g);
            }

            // If the pointer count has changed, we do want to
            // exit further execution and do not react to size or position changes.
            return;
        }

        double newSize = g.getSize();

        if (gs.getSize() != newSize) {
            handleSizeChange(gs, newSize);
        }

        Location newLoc = g.getCenter();

        if(!gs.getCenter().isSame(newLoc)) {
            handleLocationChanged(gs, newLoc);
        }

        gs.setCenter(g.getCenter());
        gs.setSize(g.getSize());
    }

    private void handleSizeChange(GestureState oldState, double newSize) {
        if(_currentSynergy != null || _axes != null) {
            return;
        }

        int amplitudeIndex = oldState.getPointerCount() - 2;
        if(amplitudeIndex < 0 || amplitudeIndex >= CONTROLLED_AMPLITUDES) {
            return;
        }

        double m = 1.0 / (_fullAmplitudeSize - _zeroAmplitudeSize);
        double b = 1.0 - (m * _fullAmplitudeSize);

        double val = m * newSize + b;

        val = Math.max(0.0, Math.min(val, 1.0)); // Clip between 0 .. 1.

        _amplitudes[amplitudeIndex] = val;

        double[] jointData = _currentSynergy.toSafeAbduction(_currentSynergy.toJoints(_amplitudes));

        if(jointData.length > JointMapping.length) {
            Log.e("SynergyProxy", "Too many joints / not enaugh joint names.");
            return;
        }

        for(int i = 0; i < jointData.length; i++) {
            _axes.setTargetValue(JointMapping[i], jointData[i]);
        }
    }

    private void handleLocationChanged(GestureState oldState, Location newLocation) {
        if(_currentSynergy != null || _axes != null) {
            return;
        }
    }
}
