package de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Interfaces;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;

/**
 * Created by merlin on 20.02.18.
 */

public interface GestureObserver {
    void onGestureAdd(Gesture g);
    void onGestureRemove(Gesture g);
    void onGestureChanged(Gesture g);
}
