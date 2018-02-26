package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material;

/**
 * Created by merlin on 26.02.18.
 */

public class LinearEquation {
    private double m;
    private double b;

    private double min;
    private double max;

    public LinearEquation(double m, double b) {
        setParameters(m, b);
    }

    public LinearEquation(double x1, double y1, double x2, double y2) {
        calculateParameters(x1, y1, x2, y2);
    }

    public LinearEquation(double x1, double y1, double x2, double y2, double min, double max) {
        calculateParameters(x1, y1, x2, y2);
        setLimits(min, max);
    }

    public void setParameters(double m, double b) {
        this.m = m;
        this.b = b;
    }

    public void calculateParameters(double x1, double y1, double x2, double y2) {
        double nm = (y2 - y1) / (x2 - x1);
        double nb = y2 - (nm * x2);

        setParameters(nm, nb);
    }

    public void setLimits(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double calculate(double x) {
        return m * x + b;
    }

    public double calculateClipped(double x) {
        double val = calculate(x);

        return Math.max(Math.min(min, max), Math.min(val, Math.max(min, max)));
    }
}
