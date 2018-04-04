package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.CartesianArmManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.PointInSpace;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.GestureState;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.LinearEquation;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.RelativeChanger;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.SynergyProxyBase;

/**
 * Created by merlin on 08.03.18.
 */

public class RelativeSynergyProxy extends SynergyProxyBase {
    private static final int HAND_GEST_POINTER_COUNT = 2;
    private static final int ARM_GEST_POINTER_COUNT = 3;

    private static final int SIZE_AMPLITUDE = 0;
    private static final int XPOS_AMPLITUDE = 1;
    private static final int ROT_AMPLITUDE = 2;

    RelativeChanger[] _changers = new RelativeChanger[] {
            new RelativeChanger(50, 1200, false, -50, 50),
            new RelativeChanger(50, 1200, false, -50, 50),
            new RelativeChanger(40, Math.PI, false, -50, 50)

    };

    LinearEquation armXEq = new LinearEquation(0, 0, 2200, CartesianArmManager.X_MAX - CartesianArmManager.X_MIN);
    LinearEquation armZEq = new LinearEquation(0, 0, -1000, CartesianArmManager.Z_MAX - CartesianArmManager.Z_MIN);

    protected void handleSizeChange(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() != HAND_GEST_POINTER_COUNT) {
            return;
        }

        double sizeChange = gesture.getSize() - oldState.getSize();

        setAmplitude(SIZE_AMPLITUDE, _changers[SIZE_AMPLITUDE].getChangedClipped(getAmplitude(SIZE_AMPLITUDE), sizeChange));

    }

    protected void handleLocationChanged(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() == HAND_GEST_POINTER_COUNT) {
            double xChange = gesture.getCenter().getX() - oldState.getCenter().getX();

            setAmplitude(XPOS_AMPLITUDE, _changers[XPOS_AMPLITUDE].getChangedClipped(getAmplitude(XPOS_AMPLITUDE), xChange));
        }
        else if(gesture.getPointerCount() == ARM_GEST_POINTER_COUNT) {
            Location old = oldState.getCenter();
            Location newLoc = gesture.getCenter();

            Location offset = newLoc.substract(old);

            PointInSpace armoffset = new PointInSpace(
                    armXEq.calculate(offset.getX()),
                    0,
                    armZEq.calculate(offset.getY())

            );

            CartesianArmManager.getInstance().movePalm(armoffset);
        }
    }

    protected void handleOrientationChanges(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() != HAND_GEST_POINTER_COUNT) {
            return;
        }

        double oriChange = gesture.getOrientation() - oldState.getOrientation();

        setAmplitude(ROT_AMPLITUDE, _changers[ROT_AMPLITUDE].getChangedClipped(getAmplitude(ROT_AMPLITUDE), oriChange));
    }


}
