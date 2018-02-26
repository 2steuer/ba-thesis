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

public class SynergyProxy implements GestureObserver {
    private static final int CONTROLLED_AMPLITUDES = 3;

    private static final int HAND_GEST_POINTER_COUNT = 2;

    private static final int SIZE_AMPLITUDE = 0;
    private static final int XPOS_AMPLITUDE = 1;
    private static final int ROT_AMPLITUDE = 2;

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

    private LinearEquation[] _eqs = new LinearEquation[] {
        new LinearEquation(1200, 50, 300, -50, -50, 50),
        new LinearEquation(0, 50, 1000, -50, -50, 50),
        new LinearEquation(-(Math.PI / 2.0), 50, Math.PI / 2.0, -50, -50, 50)
    };

    AxisManager _axes = null;

    private HashSet<SynergyAmplitudeListener> listeners = new HashSet<>();

    private boolean locked = true;

    private double _canvasWidth = 0;
    private double _canvasHeight = 0;

    public SynergyProxy() {
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

    public void setCanvasSize(float width, float height) {
        _canvasHeight = height;
        _canvasWidth = width;

        LinearEquation leq = _eqs[XPOS_AMPLITUDE];
        leq.calculateParameters(_canvasWidth * 0.25, 50, _canvasWidth * 0.75, -50);
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
        if(gs == null || gs.getGesture() != g) {
            return;
        }

        double newSize = g.getSize();

        if (gs.getSize() != newSize) {
            handleSizeChange(gs, g);
        }

        Location newLoc = g.getCenter();

        if(!gs.getCenter().isSame(newLoc)) {
            handleLocationChanged(gs, g);
        }

        double orientation = g.getOrientation();

        if(orientation != gs.getOrientation()) {
            handleOrientationChanges(gs, g);
        }

        gs.setCenter(newLoc);
        gs.setSize(newSize);
        gs.setOrientation(orientation);

        updateJoints();
    }

    private void handleSizeChange(GestureState oldState, Gesture gesture) {
        if(locked || _currentSynergy == null || _axes == null) {
            return;
        }

        if(gesture.getPointerCount() != 2) {
            return;
        }

        double newSize = gesture.getSize();

        _amplitudes[SIZE_AMPLITUDE] = _eqs[SIZE_AMPLITUDE].calculateClipped(newSize);

    }

    // Set all amplitudes to literally zero
    public void allAmplitudesZero() {
        for(int i = 0; i < _amplitudes.length; i++) {
            _amplitudes[i] = 0;
        }

        updateJoints();
    }

    private void handleLocationChanged(GestureState oldState, Gesture gesture) {
        if(_currentSynergy == null || _axes == null) {
            return;
        }

        if(gesture.getPointerCount() != 2) {
            return;
        }

        _amplitudes[XPOS_AMPLITUDE] = _eqs[XPOS_AMPLITUDE].calculateClipped(gesture.getCenter().getX());

    }

    private void handleOrientationChanges(GestureState oldState, Gesture gesture) {
        if(_currentSynergy == null || _axes == null) {
            return;
        }

        if(gesture.getPointerCount() != 2) {
            return;
        }

        _amplitudes[ROT_AMPLITUDE] = _eqs[ROT_AMPLITUDE].calculateClipped(gesture.getOrientation());
    }

    // Set all amplitudes to the value for "Zero Grasp".
    public void allAmplitudesZeroValue() {
        for(int i = 0; i < Math.min(_amplitudes.length, CONTROLLED_AMPLITUDES); i++) {
            _amplitudes[i] = _zeroAmplitudeValue;
        }

        updateJoints();
    }

    private void updateJoints() {
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
