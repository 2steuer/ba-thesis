package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.Interfaces;

/**
 * Created by merlin on 25.11.17.
 */

public interface AxisControllerObserver {
    void onStartMoving(String axisIdentifier, int direction);
    void onStopMoving(String axisIdentifier);
}
