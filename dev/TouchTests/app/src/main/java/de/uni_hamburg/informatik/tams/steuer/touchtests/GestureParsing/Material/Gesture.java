package de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by merlin on 22.11.17.
 */

public class Gesture {
    private final int SINGLE_POINTER_SIZE = 1200;
    private final float SIZE_MULTIPLICATOR_CATCH_RADIUS = 2.5f;

    private HashSet<Pointer> pointers = new HashSet<Pointer>();

    private Pointer _thumbPointer = null;

    private boolean isLocked = true;

    public Collection<Pointer> getPointers() {
        return pointers;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void addPointer(Pointer p) {
        pointers.add(p);
        initPointers();
    }

    public void removePointer(Pointer p) {
        pointers.remove(p);
        initPointers();
    }

    private void initPointers() {
        switch(pointers.size()) {
            case 0:
                _thumbPointer = null;
                break;

            case 1:
                _thumbPointer = pointers.iterator().next();
                break;

            case 2:
                // note the confusing signes because of y axis growing downwards
                // when 2 pointers, find the lowest and assume it's the thumb
                Pointer lowest = new Pointer(99, 0, Float.MIN_VALUE);

                for(Pointer p : pointers) {
                    if(p.getLocation().getY() > lowest.getLocation().getY()) {
                        lowest = p;
                    }
                }

                _thumbPointer = lowest;
                break;

            // more than 2 pointers
            default:
                // when 3 or more pointers, assume the one furthest away
                // from the gesture center as the thumb
                Location c = getCenter();
                Pointer currentP = null;
                float currentDistance = Float.MIN_VALUE;

                for(Pointer p : pointers) {
                    float dst = c.distanceTo(p.getLocation());
                    if(dst > currentDistance) {
                        currentP = p;
                    }
                }

                _thumbPointer = currentP;
                break;


        }
    }

    public int getPointerCount() {
        return pointers.size();
    }

    public boolean catchesPointer(Pointer p) {
        Location com = getCenter();
        float distance = p.getLocation().distanceTo(com);

        return distance <= getCatchRadius();
    }

    public float getCatchRadius() {
        return pointers.size() == 1 ? SINGLE_POINTER_SIZE : (getSize() * SIZE_MULTIPLICATOR_CATCH_RADIUS);
    }

    public float getDistanceToCenter(Pointer p) {
        return p.getLocation().distanceTo(getCenter());
    }

    public Location getCenter() {
        Location loc = new Location();

        for (Pointer p:pointers) {
            loc = loc.add(p.getLocation());
        }

        return loc.divide(getPointerCount());
    }

    public float getSize() {
        float size = 0;
        Location com = getCenter();

        for(Pointer p : pointers) {
            size += com.distanceTo(p.getLocation());
        }

        size /= getPointerCount();

        return size * 2;
    }

    public double getOrientation() {
        // calculate the angle betwen the
        // vector center - thumbPointer and the normal
        // vector (0, -1). -1 is taken here because y axis grows downwards
        // and we want to calculate the naturally intuitive angle.
        Location diffVect = getCenter().substract(_thumbPointer.getLocation());
        Location normalVect = new Location(0, -1);
        return normalVect.getAngleTo(diffVect);
    }
}
