package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material;

/**
 * Created by merlin on 08.03.18.
 */

public class RelativeChanger {
    private double rate;

    private double min;
    private double max;

    public RelativeChanger(double valueChange, double inputChange, boolean invert, double min, double max) {
        setRateBySpan(valueChange, inputChange, invert, min, max);
    }

    public void setRateBySpan(double valueChange, double inputChange, boolean invert, double min, double max) {
        rate = valueChange / inputChange * (invert ? -1 : 1);
        this.min = min;
        this.max = max;
    }

    public double getChangedClipped(double oldValue, double inputChange) {
        double val = oldValue + (inputChange * rate);
        return Math.max(Math.min(min, max), Math.min(val, Math.max(min, max)));
    }
}
