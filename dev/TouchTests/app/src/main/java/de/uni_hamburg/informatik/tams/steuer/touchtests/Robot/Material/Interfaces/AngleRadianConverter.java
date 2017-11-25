package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.ValueConverter;

/**
 * Created by merlin on 25.11.17.
 */

public class AngleRadianConverter implements ValueConverter {

    @Override
    public double toRobotValue(double rawValue) {
        return (rawValue / 360) * 2 * Math.PI;
    }

    @Override
    public double toRawValue(double robotValue) {
        return (robotValue / (2 * Math.PI)) * 360;
    }
}
