package de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by merlin on 22.11.17.
 */

public class Gesture {
    private final int SINGLE_POINTER_SIZE = 1200;
    private final float SIZE_MULTIPLICATOR_CATCH_RADIUS = 1.5f;

    private HashSet<Pointer> pointers = new HashSet<Pointer>();

    public Collection<Pointer> getPointers() {
        return pointers;
    }

    public void addPointer(Pointer p) {
        pointers.add(p);
    }

    public void removePointer(Pointer p) {
        pointers.remove(p);
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
}
