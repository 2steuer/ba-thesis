package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies;

import java.util.HashMap;

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
    GraspSynergy _currentSynergy = null;

    HashMap<Integer, GestureState> _gestures = new HashMap<>();

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

    }

    private void handleLocationChanged(GestureState oldState, Location newLocation) {

    }
}
