package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material;

/**
 * Created by merlin on 22.03.18.
 */

public class PointInSpace {
    double x,y,z;

    public PointInSpace() {
        x = y = z = 0;
    }

    public PointInSpace(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public PointInSpace add(PointInSpace pw) {
        return new PointInSpace(
            x + pw.getX(),
                y + pw.getY(),
                z + pw.getZ()
        );
    }

    public PointInSpace multiply(double v) {
        return new PointInSpace(x * v, y * v, z * v);
    }
}
