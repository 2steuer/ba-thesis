package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material;

/**
 * Created by merlin on 25.11.17.
 */

public interface ValueConverter {
    double toRobotValue(double rawValue);

    double toRawValue(double robotValue);
}
