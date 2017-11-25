package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material;

import android.renderscript.Sampler;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AxisInformation;

/**
 * Created by merlin on 25.11.17.
 */

public class AxisInformationImpl implements AxisInformation {
    public static final int DIRECTION_PLUS = 1;
    public static final int DIRECTION_NONE = 0;
    public static final int DIRECTION_MINUS = -1;

    private final ValueConverter converter;

    private final String identifier;

    // Angles in degrees!
    private final double maxValue;
    private final double minValue;

    // degrees / second
    private final double maxSpeed;

    private double targetValue;
    private double currentTargetValue;
    private double currentValue;
    private boolean moving;
    private double speed; // positive or negative

    public AxisInformationImpl(String identifier,
                               double minvalue,
                               double maxvalue,
                               double maxspeed,
                               ValueConverter converter) {
        this.identifier = identifier;
        this.maxValue = maxvalue;
        this.minValue = minvalue;
        this.maxSpeed = maxspeed;
        this.converter = converter;
    }


    public String getIdentifier() {
        return identifier;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = Math.max(this.minValue, Math.min(targetValue, this.maxValue));
    }

    public double getCurrentTargetValue() {
        return currentTargetValue;
    }

    public void setCurrentTargetValue(double currentTargetValue) {
        this.currentTargetValue = currentTargetValue;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if(Math.abs(speed) > this.maxSpeed) {
            // So complicated to clip to positive and negative
            speed = speed * (Math.abs(speed) / this.maxSpeed);
        }

        this.speed = speed;
    }
}
