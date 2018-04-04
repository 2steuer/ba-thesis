package de.uni_hamburg.informatik.tams.steuer.touchtests.FingerTipMapping.Material;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;

/**
 * Created by merlin on 04.04.18.
 */

public class FingertipPointer {
    Location screenLocation;
    Location worldLocation;
    boolean present;

    public String getEffectorName() {
        return effectorName;
    }

    String effectorName;
    int pointerId;

    public FingertipPointer(String effectorName, Location screenLoc, int id) {
        screenLocation = screenLoc;
        present = true;
        pointerId = id;
        worldLocation = new Location();
        this.effectorName = effectorName;
    }

    public int getPointerId() {
        return pointerId;
    }

    public void setPointerId(int pointerId) {
        this.pointerId = pointerId;
    }


    public Location getScreenLocation() {
        return screenLocation;
    }

    public void setScreenLocation(Location screenLocation) {
        this.screenLocation = screenLocation;
    }

    public Location getWorldLocation() {
        return worldLocation;
    }

    public void setWorldLocation(Location worldLocation) {
        this.worldLocation = worldLocation;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
