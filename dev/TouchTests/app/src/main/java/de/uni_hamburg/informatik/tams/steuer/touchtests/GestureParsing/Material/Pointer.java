package de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material;

/**
 * Created by merlin on 22.11.17.
 */

public class Pointer {
    private int id;

    private Location location;

    public Pointer(int id, float x, float y)
    {
        this.id = id;
        this.location = new Location(x, y);
    }

    public Location getLocation()
    {
        return this.location;
    }

    public void setLocation(float x, float y) {
        this.location = new Location(x, y);
    }

    public int getId()
    {
        return this.id;
    }
}
