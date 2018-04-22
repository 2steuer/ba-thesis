package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot;

import android.os.SystemClock;
import android.util.Log;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;
import org.ros.rosjava_geometry.Quaternion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bio_ik_msgs.GetIKResponse;
import bio_ik_msgs.IKResponse;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AngleRadianConverter;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.PointInSpace;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.C5LwrNode;
import geometry_msgs.Point;
import moveit_msgs.MoveItErrorCodes;
import sensor_msgs.JointState;

/**
 * Created by merlin on 22.03.18.
 */

public class CartesianArmManager implements ServiceResponseListener<bio_ik_msgs.GetIKResponse> {
    public static final double Y_MIN = -1.2;
    public static final double Y_MAX = -0.8;
    public static final double X_MIN = -0.2;
    public static final double X_MAX = 0.4;
    public static final double Z_MIN = 1.10;
    public static final double Z_MAX = 1.35;

    public static final int MAX_AXIS_CHANGE = 15;

    private static CartesianArmManager instance = null;
    public static CartesianArmManager getInstance() {
        if(instance == null) {
            instance = new CartesianArmManager();
        }

        return instance;
    }

    private Set<String> lockedAxes = new HashSet<String>();

    C5LwrNode node = null;
    AxisManager mgr = AxisManager.getInstance();

    PointInSpace currentPos = new PointInSpace();
    PointInSpace homePos = new PointInSpace(0, Y_MIN, Z_MAX);

    boolean waiting = false;

    long timeCounter = 0;

    Lock runningLock = new ReentrantLock();
    private boolean running = false;
    private boolean posChanged = false;

    private boolean bigChangesAllowed = false;

    private CartesianArmManager() {

        lockedAxes.add("THJ1");
        lockedAxes.add("THJ2");
        lockedAxes.add("THJ3");
        lockedAxes.add("THJ4");
        lockedAxes.add("THJ5");

        lockedAxes.add("FFJ1");
        lockedAxes.add("FFJ2");
        lockedAxes.add("FFJ3");
        lockedAxes.add("FFJ4");

        lockedAxes.add("MFJ1");
        lockedAxes.add("MFJ2");
        lockedAxes.add("MFJ3");
        lockedAxes.add("MFJ4");

        lockedAxes.add("RFJ1");
        lockedAxes.add("RFJ2");
        lockedAxes.add("RFJ3");
        lockedAxes.add("RFJ4");

        lockedAxes.add("LFJ1");
        lockedAxes.add("LFJ2");
        lockedAxes.add("LFJ3");
        lockedAxes.add("LFJ4");
        lockedAxes.add("LFJ5");

        lockedAxes.add("WRJ1");
        lockedAxes.add("WRJ2");

        setPos(0, 0, 0);
    }

    public void setNode(C5LwrNode node) {
        this.node = node;
    }

    private double clip(double min, double max, double val) {
        return Math.max(min, Math.min(max, val));
    }

    private void setPos(double x, double y, double z) {
        currentPos.setX(clip(X_MIN, X_MAX, x));
        currentPos.setY(clip(Y_MIN, Y_MAX, y));
        currentPos.setZ(clip(Z_MIN, Z_MAX, z));
    }

    public boolean goHome() {
        bigChangesAllowed = true;
        return movePalmTo(homePos);
    }

    public boolean movePalm(PointInSpace offset)
    {
        double newX = currentPos.getX() + offset.getX();
        double newY = currentPos.getY() + offset.getY();
        double newZ = currentPos.getZ() + offset.getZ();


        return movePalmTo(new PointInSpace(newX, newY, newZ));
    }

    public boolean movePalmTo(PointInSpace position) {
        if(node == null) {
            return false;
        }

        setPos(position.getX(), position.getY(), position.getZ());

        runningLock.lock();
        posChanged = true;
        if(!running) {

            timeCounter = SystemClock.elapsedRealtime();
            node.GetIkJointsPalm(mgr.getRobotState(), lockedAxes.toArray(new String[0]), currentPos.getX(), currentPos.getY(), currentPos.getZ(), 0.7071, 0.0, 0.0, 0.7071, this);
            running = true;
        }
        runningLock.unlock();


        return true;
    }

    public PointInSpace getPosition() {
        return currentPos;
    }

    public void stop() {
        runningLock.lock();
        running = false;
        runningLock.unlock();
    }

    @Override
    public void onSuccess(GetIKResponse getikResponse) {
        IKResponse ikResponse = getikResponse.getIkResponse();

        long diff = SystemClock.elapsedRealtime() - timeCounter;
        Log.i("CART_IK", "After " + diff + " ms");

        if(ikResponse.getErrorCode().getVal() != MoveItErrorCodes.SUCCESS) {
            Log.w("CART_IK", "IK Failed, Error: " + ikResponse.getErrorCode().getVal());
            waiting = false;

            runningLock.lock();
            running = false;
            runningLock.unlock();
            return;
        }

        Map<String, Double> oldState = mgr.getRobotState();
        Map<String, Double> newState = new HashMap<>();
        boolean passToRobot = true;


        AngleRadianConverter c = new AngleRadianConverter();
        JointState st = ikResponse.getSolution().getJointState();
        List<String> name = st.getName();
        double[] val = st.getPosition();
        int count = Math.min(name.size(), val.length);

        // check for maximum joint movements
        for(int i = 0; i < count && passToRobot; i++) {
            if(!lockedAxes.contains(name.get(i))) {
                double dgVal = c.toRawValue(val[i]);
                double rVal = c.toRawValue(oldState.get(name.get(i)));

                if(bigChangesAllowed || Math.abs(dgVal - rVal) <  MAX_AXIS_CHANGE) {
                    newState.put(name.get(i), dgVal);
                }
                else {
                    passToRobot = false;
                    posChanged = true; // retry to find a solution
                    Log.w("CART_IK", "Too big value change for joint " + name.get(i) + " " + rVal + " > " + dgVal);
                }
            }
        }

        bigChangesAllowed = false;

        if(passToRobot) {
            int notifyAt = count - 1;

            for(int i = 0; i < count; i++) {
                // notify on last value only
                if(newState.containsKey(name.get(i))) {
                    mgr.setTargetValue(name.get(i), newState.get(name.get(i)), false, i == notifyAt);

                }

            }

        }


        runningLock.lock();
        // if still running is requested, restart with the current position
        // this is an unsynchronized free-running endless loop
        // until running is set to false.
        if(running && posChanged) {
            posChanged = false;
            timeCounter = SystemClock.elapsedRealtime();
            node.GetIkJointsPalm(mgr.getRobotState(), lockedAxes.toArray(new String[0]), currentPos.getX(), currentPos.getY(), currentPos.getZ(), 0.7071, 0.0, 0.0, 0.7071, this);
        }
        else {
            running = false;
        }
        runningLock.unlock();
    }

    @Override
    public void onFailure(RemoteException e) {
        waiting = false;

        runningLock.lock();
        running = false;
        runningLock.unlock();
    }
}
