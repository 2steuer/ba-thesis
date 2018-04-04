package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.CartesianArmManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.PointInSpace;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.GestureState;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.LinearEquation;

/**
 * Created by merlin on 08.03.18.
 */

public class AbsoluteSynergyProxy extends SynergyProxyBase {
    private static final int HAND_GEST_POINTER_COUNT = 2;
    private static final int ARM_GEST_POINTER_COUNT = 3;

    private static final int SIZE_AMPLITUDE = 0;
    private static final int XPOS_AMPLITUDE = 1;
    private static final int ROT_AMPLITUDE = 2;

    private LinearEquation[] _eqsHand = new LinearEquation[] {
            new LinearEquation(1200, 50, 300, -50, -50, 50),
            new LinearEquation(0, 50, 1000, -50, -50, 50),
            new LinearEquation(-(Math.PI / 2.0), 50, Math.PI / 2.0, -50, -50, 50)
    };

    private static final int ARM_X_EQ = 0;
    private static final int ARM_Z_EQ = 1;

    private LinearEquation[] _eqsArm = new LinearEquation[] {
            new LinearEquation(300, CartesianArmManager.X_MIN, 1200, CartesianArmManager.X_MAX),
            new LinearEquation(0, CartesianArmManager.Z_MIN, 1000, CartesianArmManager.Z_MAX)
    };

    private double _canvasWidth = 0;
    private double _canvasHeight = 0;

    private CartesianArmManager arm = CartesianArmManager.getInstance();

    public void setCanvasSize(float width, float height) {
        _canvasHeight = height;
        _canvasWidth = width;

        LinearEquation leq = _eqsHand[XPOS_AMPLITUDE];
        leq.calculateParameters(_canvasWidth * 0.25, 50, _canvasWidth * 0.75, -50);

        _eqsArm[ARM_X_EQ].calculateParameters(_canvasWidth * 0.25, CartesianArmManager.X_MIN, _canvasWidth * 0.75, CartesianArmManager.X_MAX);
        _eqsArm[ARM_X_EQ].setLimits(CartesianArmManager.X_MIN, CartesianArmManager.X_MAX);

        _eqsArm[ARM_Z_EQ].calculateParameters(_canvasHeight * 0.75, CartesianArmManager.Z_MIN, _canvasHeight * 0.25, CartesianArmManager.Z_MAX);
        _eqsArm[ARM_Z_EQ].setLimits(CartesianArmManager.Z_MIN, CartesianArmManager.Z_MAX);

    }


    protected void handleSizeChange(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() != HAND_GEST_POINTER_COUNT) {
            return;
        }

        double newSize = gesture.getSize();

        setAmplitude(SIZE_AMPLITUDE, _eqsHand[SIZE_AMPLITUDE].calculateClipped(newSize));

    }

    protected void handleLocationChanged(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() == HAND_GEST_POINTER_COUNT) {
            setAmplitude(XPOS_AMPLITUDE, _eqsHand[XPOS_AMPLITUDE].calculateClipped(gesture.getCenter().getX()));
        }
        else if(gesture.getPointerCount() == ARM_GEST_POINTER_COUNT) {
            Location p = gesture.getCenter();
            double x = _eqsArm[ARM_X_EQ].calculateClipped(p.getX());
            double z = _eqsArm[ARM_Z_EQ].calculateClipped(p.getY());

            PointInSpace pos = arm.getPosition();

            PointInSpace newPos = new PointInSpace(x, pos.getY(), z);
            arm.movePalmTo(newPos);
        }



    }

    protected void handleOrientationChanges(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() != HAND_GEST_POINTER_COUNT) {
            return;
        }

        setAmplitude(ROT_AMPLITUDE, _eqsHand[ROT_AMPLITUDE].calculateClipped(gesture.getOrientation()));
    }


}
