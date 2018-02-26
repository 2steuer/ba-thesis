package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;

/**
 * Created by merlin on 20.02.18.
 */

public class GestureState {
    private Gesture gesture;
    private int pointerCount;
    private double size;
    private Location center;

    private double orientation;

    public GestureState() {
        gesture = null;
        pointerCount = 0;
        size = 0;
        orientation = 0;
    }

    public Gesture getGesture() {
        return gesture;
    }

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
        if(gesture != null) {
            pointerCount = gesture.getPointerCount();
            size = gesture.getSize();
            center = gesture.getCenter();
            orientation = gesture.getOrientation();
        }
        else {
            pointerCount = 0;
            size = 0;
            center = new Location();
            orientation = 0;
        }

    }

    public int getPointerCount() {
        return pointerCount;
    }

    public void setPointerCount(int pointerCount) {
        this.pointerCount = pointerCount;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

}
