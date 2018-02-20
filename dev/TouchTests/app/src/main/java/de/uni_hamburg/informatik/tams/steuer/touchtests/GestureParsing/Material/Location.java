package de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material;

/**
 * Created by merlin on 22.11.17.
 */

public class Location {
    private final float x;
    private final float y;

    public Location() {
        this(0,0);
    }

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Location add(Location loc) {
        return new Location(this.x + loc.x, this.y + loc.y);
    }

    public Location multiply(float c) {
        return new Location(this.x * c, this.y * c);
    }

    public Location divide(float c) {
        return this.multiply(1.0f/c);
    }

    public float distanceTo(Location loc) {
        return (float)Math.sqrt(Math.pow(loc.x - this.x, 2) + Math.pow(loc.y - this.y, 2));
    }

    public boolean isSame(Location l2) {
        return l2.getX() == x && l2.getY() == y;
    }
}
