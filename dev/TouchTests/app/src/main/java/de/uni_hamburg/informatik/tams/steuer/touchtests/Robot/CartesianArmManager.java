package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot;

import android.os.SystemClock;
import android.util.Log;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;
import org.ros.rosjava_geometry.Quaternion;

import java.util.LinkedList;
import java.util.List;

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
    private static final double Y_MIN = -1.2;
    private static final double Y_MAX = -0.8;
    private static final double X_MIN = -0.2;
    private static final double X_MAX = 0.4;
    private static final double Z_MIN = 1.0;
    private static final double Z_MAX = 1.2;

    private static CartesianArmManager instance = null;
    public static CartesianArmManager getInstance() {
        if(instance == null) {
            instance = new CartesianArmManager();
        }

        return instance;
    }

    private List<String> lockedAxes = new LinkedList<String>();

    C5LwrNode node = null;
    AxisManager mgr = AxisManager.getInstance();

    PointInSpace currentPos = new PointInSpace();
    PointInSpace homePos = new PointInSpace(0, Y_MIN, Z_MAX);

    boolean waiting = false;

    long timeCounter = 0;

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
        if(waiting || node == null) {
            return false;
        }

        waiting = true;
        timeCounter = SystemClock.elapsedRealtime();
        node.GetIkJointsPalm(mgr.getRobotState(), lockedAxes, homePos.getX(), homePos.getY(), homePos.getZ(), 0.7071, 0.0, 0.0, 0.7071, this);

        setPos(homePos.getX(), homePos.getY(), homePos.getZ());

        return true;
    }

    public boolean movePalm(PointInSpace offset)
    {

        if(waiting || node == null) {
            return false;
        }

        double newX = currentPos.getX() + offset.getX();
        double newY = currentPos.getY() + offset.getY();
        double newZ = currentPos.getZ() + offset.getZ();

        setPos(newX, newY, newZ);

        waiting = true;
        timeCounter = SystemClock.elapsedRealtime();
        node.GetIkJointsPalm(mgr.getRobotState(), lockedAxes, currentPos.getX(), currentPos.getY(), currentPos.getZ(), 0.7071, 0, 0, 0.7071, this);

        return true;
    }

    @Override
    public void onSuccess(GetIKResponse getikResponse) {
        IKResponse ikResponse = getikResponse.getIkResponse();

        long elapsed = SystemClock.elapsedRealtime() - timeCounter;
        Log.i("IK", "Calculations took " + elapsed + "ms");

        if(ikResponse.getErrorCode().getVal() != MoveItErrorCodes.SUCCESS) {
            Log.w("IK", "IK Failed, Error: " + ikResponse.getErrorCode().getVal());
            return;
        }

        JointState st = ikResponse.getSolution().getJointState();
        List<String> name = st.getName();
        double[] val = st.getPosition();
        int count = Math.min(name.size(), val.length);
        int notifyAt = count - 1;




        AngleRadianConverter c = new AngleRadianConverter();

        for(int i = 0; i < count; i++) {
            // notify on last value only
            mgr.setTargetValue(name.get(i), c.toRawValue(val[i]), false, i == notifyAt);
        }


        waiting = false;
    }

    @Override
    public void onFailure(RemoteException e) {
        waiting = false;
    }
}
