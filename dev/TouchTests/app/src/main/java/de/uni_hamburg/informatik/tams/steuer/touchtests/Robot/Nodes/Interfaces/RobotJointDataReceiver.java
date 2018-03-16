package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.Interfaces;

import java.util.HashMap;

/**
 * Created by merlin on 26.11.17.
 */

public interface RobotJointDataReceiver {
    void handleJointData(int type, HashMap<String, Double> data);
}
