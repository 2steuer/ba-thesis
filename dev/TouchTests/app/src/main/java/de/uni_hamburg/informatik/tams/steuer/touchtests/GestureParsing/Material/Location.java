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

    public Location substract(Location loc) {
        return new Location(this.x - loc.x, this.y - loc.y);
    }

    public double getVectorLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double scalarProduct(Location loc) {
        return x*loc.x + y * loc.y;
    }

    public double getAngleTo(Location loc) {
        double val = Math.acos(scalarProduct(loc) / (getVectorLength() * loc.getVectorLength()));
        if(x * loc.getY() - y * loc.getX() < 0) {
            val *= -1;
        }

        return val;
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


    public Location getTurned(double angleRad) {
        double newX = x * Math.cos(angleRad) - y * Math.sin(angleRad);
        double newY = y * Math.cos(angleRad) + x * Math.sin(angleRad);

        return new Location((float)newX, (float)newY);
    }

    public boolean isSame(Location l2) {
        return l2.getX() == x && l2.getY() == y;
    }
}
