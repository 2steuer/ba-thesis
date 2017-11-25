package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.AxisInformationImpl;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AngleRadianConverter;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AxisInformation;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.ValueConverter;

/**
 * Created by merlin on 25.11.17.
 */

public class AxisManager {
    private static AxisManager instance = null;
    public static AxisManager getInstance() {
        if(instance == null) {
            instance = new AxisManager();
        }

        return instance;
    }


    public static final int UPDATE_FREQ = 20;

    private final Runnable timerCaller = new Runnable() {
        @Override
        public void run() {
            timerTick();
            timerHandler.postDelayed(timerCaller, 1000 / UPDATE_FREQ);
        }
    };

    private Handler timerHandler;

    private HashMap<String, AxisInformationImpl> axes = new HashMap<String, AxisInformationImpl>();

    private List<Observer> observers = new ArrayList<Observer>();

    private boolean running = false;

    private AxisManager() {
        timerHandler = new Handler();

        AngleRadianConverter c = new AngleRadianConverter();

        // Arm's joints
        addAxis("lwr_arm_0_joint", 0, 180, 5, c);
        addAxis("lwr_arm_1_joint", 0, 180, 5, c);
        addAxis("lwr_arm_2_joint", 0, 180, 5, c);
        addAxis("lwr_arm_3_joint", 0, 180, 5, c);
        addAxis("lwr_arm_4_joint", 0, 180, 5, c);
        addAxis("lwr_arm_5_joint", 0, 180, 5, c);
        addAxis("lwr_arm_6_joint", 0, 180, 5, c);
        addAxis("lwr_arm_7_joint", 0, 180, 5, c);

        // Wrist
        addAxis("WRJ1", 0, 90, 5, c);
        addAxis("WRJ2", 0, 90, 5, c);

        // Thumb
        addAxis("THJ1", 0, 90, 5, c);
        addAxis("THJ2", 0, 90, 5, c);
        addAxis("THJ3", 0, 90, 5, c);
        addAxis("THJ4", 0, 90, 5, c);
        addAxis("THJ5", 0, 90, 5, c);

        // First Finger
        addAxis("FFJ1", 0, 90, 5, c);
        addAxis("FFJ2", 0, 90, 5, c);
        addAxis("FFJ3", 0, 90, 5, c);
        addAxis("FFJ4", 0, 90, 5, c);

        // Middle Finger
        addAxis("MFJ1", 0, 90, 5, c);
        addAxis("MFJ2", 0, 90, 5, c);
        addAxis("MFJ3", 0, 90, 5, c);
        addAxis("MFJ4", 0, 90, 5, c);

        // Ring Finger
        addAxis("RFJ1", 0, 90, 5, c);
        addAxis("RFJ2", 0, 90, 5, c);
        addAxis("RFJ3", 0, 90, 5, c);
        addAxis("RFJ4", 0, 90, 5, c);

        // Last Finger
        addAxis("LFJ1", 0, 90, 5, c);
        addAxis("LFJ2", 0, 90, 5, c);
        addAxis("LFJ3", 0, 90, 5, c);
        addAxis("LFJ4", 0, 90, 5, c);
        addAxis("LFJ5", 0, 90, 5, c);
    }

    public void start() {
        timerHandler.postDelayed(timerCaller, 1000 / UPDATE_FREQ);
        running = true;
    }

    public void stop() {
        timerHandler.removeCallbacksAndMessages(null);
        running = false;
    }

    private void timerTick() {
        // process constantly moving axes
        for(AxisInformationImpl axis : axes.values()) {
            // Handle movement
            if(axis.isMoving()) {
                double spd = Math.min(axis.getMaxSpeed(), axis.getSpeed());
                double step = spd / UPDATE_FREQ;
                axis.setTargetValue(axis.getTargetValue() + step);
            }

            // handle slope to target value
            double at = axis.getTargetValue();
            double ct = axis.getCurrentTargetValue();
            double maxStep = axis.getMaxSpeed() / UPDATE_FREQ;

            if(Math.abs(at - ct) <= maxStep) {
                axis.setCurrentTargetValue(axis.getTargetValue());
            }
            else {
                if(at > ct) {
                    ct += maxStep;
                }
                else if(at < ct) {
                    ct -= maxStep;
                }
            }

        }

        notifyObservers();

        // send data:
    }


    private void addAxis(String identifier, double minValue, double maxValue, double maxSpeed, ValueConverter conv) {
        axes.put(identifier, new AxisInformationImpl(identifier, minValue, maxValue, maxSpeed, conv));
    }

    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    private void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    public boolean startMoving(String identifier, double speed) {
        if(!running) {
            return false;
        }

        if(!axes.containsKey(identifier)) {
            return false;
        }

        AxisInformationImpl a = axes.get(identifier);

        a.setMoving(true);
        a.setSpeed(speed);

        return true;
    }

    public boolean stopMoving(String identifier) {
        if(!running) {
            return false;
        }

        if(!axes.containsKey(identifier)) {
            return false;
        }

        AxisInformationImpl a = axes.get(identifier);

        a.setMoving(false);
        a.setSpeed(0);

        return true;
    }

    public boolean copyCurrentValuesToTarget() {
        if(!running) {
            return false;
        }

        for(AxisInformationImpl i : axes.values()) {
            setTargetValue(i.getIdentifier(), i.getCurrentValue(), true, false);
        }

        notifyObservers();
        return true;
    }

    public boolean setTargetValue(String identifier, double value, boolean force, boolean notifyObservers) {
        if(!running || !axes.containsKey(identifier)) {
            return false;
        }

        AxisInformationImpl ai = axes.get(identifier);
        ai.setTargetValue(value);
        if(force) {
            ai.setCurrentTargetValue(value);
        }

        if(notifyObservers) {
            notifyObservers();
        }

        return true;
    }

    public boolean setTargetValue(String identifier, double value) {
        return setTargetValue(identifier, value, false);
    }

    public boolean setTargetValue(String identifier, double value, boolean force) {
        return setTargetValue(identifier, value, force, true);
    }

    public Collection<AxisInformation> getAllAxisInfos() {
        LinkedList<AxisInformation> lst = new LinkedList<AxisInformation>();
        for(AxisInformationImpl ai : axes.values()) {
            lst.add(ai);
        }
        return lst;
    }

    public AxisInformation getAxisInfo(String identifier) {
        return axes.containsKey(identifier) ? axes.get(identifier) : null;
    }

    private void notifyObservers() {
        for(Observer o : observers) {
            o.update(null, this);
        }
    }
}
