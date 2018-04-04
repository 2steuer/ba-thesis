package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies;

import android.util.Log;
import android.util.Size;

import java.util.HashMap;
import java.util.HashSet;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Interfaces.GestureObserver;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Interfaces.SynergyAmplitudeListener;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.GestureState;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.LinearEquation;
import hdbt.shadow.GraspSynergy;

/**
 * Created by merlin on 20.02.18.
 */

public abstract class SynergyProxyBase implements GestureObserver {
    protected static final int CONTROLLED_AMPLITUDES = 3;

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

    private static final double _zeroAmplitudeSize = 1200;
    private static final double _fullAmplitudeSize = 300;

    private static final double _zeroAmplitudeValue = 50;
    private static final double _fullAmplitudeValue = -50;

    GraspSynergy _currentSynergy = null;

    HashMap<Gesture, GestureState> _stateByGesture = new HashMap<>();

    private double[] _amplitudes = new double[JointMapping.length];

    AxisManager _axes = null;

    private HashSet<SynergyAmplitudeListener> listeners = new HashSet<>();

    private boolean locked = true;

    public SynergyProxyBase() {
    }

    public void setAxisManager(AxisManager manager) {
        _axes = manager;
    }

    public void setGraspSynergy(GraspSynergy grasp) {
        _currentSynergy = grasp;

        if(_currentSynergy != null) {
            allAmplitudesZero();
        }
    }


    public void addListener(SynergyAmplitudeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SynergyAmplitudeListener listener) {
        listeners.remove(listener);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void onGestureAdd(Gesture g) {
        int pc = g.getPointerCount();
        GestureState gs = new GestureState();
        gs.setGesture(g);

        _stateByGesture.put(g, gs);
    }

    @Override
    public void onGestureRemove(Gesture g) {
            _stateByGesture.remove(g);
    }

    @Override
    public void onGestureChanged(Gesture g) {
        int pc = g.getPointerCount();
        GestureState gs = _stateByGesture.get(g);
        if(gs == null || gs.getGesture() != g || g.isLocked()) {
            return;
        }

        double newSize = g.getSize();
        Location newLoc = g.getCenter();
        double orientation = g.getOrientation();

        if(!locked && _axes != null && _currentSynergy != null) {

            if (gs.getSize() != newSize) {
                handleSizeChange(gs, g);
            }


            if(!gs.getCenter().isSame(newLoc)) {
                handleLocationChanged(gs, g);
            }


            if(orientation != gs.getOrientation()) {
                handleOrientationChanges(gs, g);
            }
        }

        gs.setCenter(newLoc);
        gs.setSize(newSize);
        gs.setOrientation(orientation);

        updateJoints();
    }

    protected abstract void handleSizeChange(GestureState oldState, Gesture gesture);
    protected abstract void handleLocationChanged(GestureState oldState, Gesture gesture);
    protected abstract void handleOrientationChanges(GestureState oldState, Gesture gesture);

    // Set all amplitudes to literally zero
    public void allAmplitudesZero() {
        for(int i = 0; i < _amplitudes.length; i++) {
            _amplitudes[i] = 0;
        }

        updateJoints();
    }


    protected void setAmplitude(int amplitude, double value) {
        if(amplitude < 0 || amplitude >= _amplitudes.length) {
            return;
        }

        _amplitudes[amplitude] = value;
    }

    protected double getAmplitude(int amplitude) {
        if(amplitude < 0 || amplitude >= _amplitudes.length) {
            return Double.NaN;
        }

        return _amplitudes[amplitude];
    }

    // Set all amplitudes to the value for "Zero Grasp".
    public void allAmplitudesZeroValue() {
        for(int i = 0; i < Math.min(_amplitudes.length, CONTROLLED_AMPLITUDES); i++) {
            _amplitudes[i] = _zeroAmplitudeValue;
        }

        updateJoints();
    }

    private void updateJoints() {
        if(_axes == null)
        {
            return;
        }

        double[] jointData = _currentSynergy.toSafeAbduction(_currentSynergy.toJoints(_amplitudes));

        if(jointData.length > JointMapping.length) {
            Log.e("SynergyProxy", "Too many joints / not enaugh joint names.");
            return;
        }

        for(int i = 0; i < jointData.length; i++) {
            // only notify observers on last axis everytime
            // should increase performance
            _axes.setTargetValue(JointMapping[i], jointData[i], false, (i + 1 == jointData.length));
        }

        for(SynergyAmplitudeListener l : listeners) {
            l.setAmplitudes(_amplitudes, CONTROLLED_AMPLITUDES);
        }
    }


}
