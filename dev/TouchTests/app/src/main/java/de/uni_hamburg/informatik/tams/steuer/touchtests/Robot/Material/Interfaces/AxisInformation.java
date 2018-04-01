package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces;

/**
 * Created by merlin on 25.11.17.
 */

public interface AxisInformation {
    double getMaxSpeed();
    double getTargetValue();

    String getIdentifier();

    double getMaxValue();
    double getMinValue();
    double getCurrentTargetValue();
    double getCurrentValue();
    boolean isMoving();
    double getSpeed();
    boolean isEnabled();
    int getJointType();
}
