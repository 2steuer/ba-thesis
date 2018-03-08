package de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.GestureState;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Material.LinearEquation;

/**
 * Created by merlin on 08.03.18.
 */

public class AbsoluteSynergyProxy extends SynergyProxyBase {
    private static final int HAND_GEST_POINTER_COUNT = 2;

    private static final int SIZE_AMPLITUDE = 0;
    private static final int XPOS_AMPLITUDE = 1;
    private static final int ROT_AMPLITUDE = 2;

    private LinearEquation[] _eqs = new LinearEquation[] {
            new LinearEquation(1200, 50, 300, -50, -50, 50),
            new LinearEquation(0, 50, 1000, -50, -50, 50),
            new LinearEquation(-(Math.PI / 2.0), 50, Math.PI / 2.0, -50, -50, 50)
    };

    private double _canvasWidth = 0;
    private double _canvasHeight = 0;


    public void setCanvasSize(float width, float height) {
        _canvasHeight = height;
        _canvasWidth = width;

        LinearEquation leq = _eqs[XPOS_AMPLITUDE];
        leq.calculateParameters(_canvasWidth * 0.25, 50, _canvasWidth * 0.75, -50);
    }


    protected void handleSizeChange(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() != HAND_GEST_POINTER_COUNT) {
            return;
        }

        double newSize = gesture.getSize();

        setAmplitude(SIZE_AMPLITUDE, _eqs[SIZE_AMPLITUDE].calculateClipped(newSize));

    }

    protected void handleLocationChanged(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() != HAND_GEST_POINTER_COUNT) {
            return;
        }

        setAmplitude(XPOS_AMPLITUDE, _eqs[XPOS_AMPLITUDE].calculateClipped(gesture.getCenter().getX()));

    }

    protected void handleOrientationChanges(GestureState oldState, Gesture gesture) {
        if(gesture.getPointerCount() != HAND_GEST_POINTER_COUNT) {
            return;
        }

        setAmplitude(ROT_AMPLITUDE, _eqs[ROT_AMPLITUDE].calculateClipped(gesture.getOrientation()));
    }


}
