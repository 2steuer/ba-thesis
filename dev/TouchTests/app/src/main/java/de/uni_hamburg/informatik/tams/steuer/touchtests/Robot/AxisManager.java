package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot;

import android.content.res.Resources;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import de.uni_hamburg.informatik.tams.steuer.touchtests.R;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.AxisInformationImpl;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AngleRadianConverter;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AxisInformation;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.InitStateListener;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.ValueConverter;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.Interfaces.RobotJointDataReceiver;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.ValueTypes.JointType;

/**
 * Created by merlin on 25.11.17.
 */

public class AxisManager implements RobotJointDataReceiver {
    private static AxisManager instance = null;
    public static AxisManager getInstance() {
        if(instance == null) {
            instance = new AxisManager();
        }

        return instance;
    }


    public static final int UPDATE_FREQ = 10;

    public static final int INIT_SAMPLES = 20;

    private InitStateListener initListener = null;
    private boolean init = false;
    private int initSampleCounter = 0;

    private boolean _locked = true;

    private final TimerTask timerCaller = new TimerTask() {
        @Override
        public void run() {
            timerTick();
        }
    };

    private Timer timer;

    private HashMap<String, AxisInformationImpl> axes = new HashMap<String, AxisInformationImpl>();

    private List<Observer> observers = new ArrayList<Observer>();

    private boolean running = false;

    private RobotJointDataReceiver robotNode = null;

    private AxisManager() {
        _locked = true;

        AngleRadianConverter c = new AngleRadianConverter();

        // Arm's joints
        addAxis("lwr_arm_0_joint", -168, 168, 10, c, JointType.HAND);
        addAxis("lwr_arm_1_joint", -118, 118, 10, c, JointType.HAND);
        addAxis("lwr_arm_2_joint", -168, 168, 10, c, JointType.HAND);
        addAxis("lwr_arm_3_joint", -118, 118, 10, c, JointType.HAND);
        addAxis("lwr_arm_4_joint", -168, 168, 10, c, JointType.HAND);
        addAxis("lwr_arm_5_joint", -118, 118, 10, c, JointType.HAND);
        addAxis("lwr_arm_6_joint", -168, 168, 10, c, JointType.HAND);

        // Wrist
        addAxis("WRJ1", 0, 90, 10, c, JointType.HAND);
        addAxis("WRJ2", 0, 90, 10, c, JointType.HAND);

        // Thumb
        addAxis("THJ1", 0, 90, 10, c, JointType.HAND);
        addAxis("THJ2", -40, 40, 10, c, JointType.HAND);
        addAxis("THJ3", -12, 12, 10, c, JointType.HAND);
        addAxis("THJ4", 0, 70, 10, c, JointType.HAND);
        addAxis("THJ5", -60, 60, 10, c, JointType.HAND);

        // First Finger
        addAxis("FFJ1", 0, 90, 10, c, JointType.HAND, false);
        addAxis("FFJ2", 0, 90, 10, c, JointType.HAND);
        addAxis("FFJ3", 0, 90, 10, c, JointType.HAND);
        addAxis("FFJ4", -20, 20, 10, c, JointType.HAND);

        // Middle Finger
        addAxis("MFJ1", 0, 90, 10, c, JointType.HAND, false);
        addAxis("MFJ2", 0, 90, 10, c, JointType.HAND);
        addAxis("MFJ3", 0, 90, 10, c, JointType.HAND);
        addAxis("MFJ4", -20, 20, 10, c, JointType.HAND);

        // Ring Finger
        addAxis("RFJ1", 0, 90, 10, c, JointType.HAND, false);
        addAxis("RFJ2", 0, 90, 10, c, JointType.HAND);
        addAxis("RFJ3", 0, 90, 10, c, JointType.HAND);
        addAxis("RFJ4", -20, 20, 10, c, JointType.HAND);

        // Last Finger
        addAxis("LFJ1", 0, 45, 10, c, JointType.HAND, false);
        addAxis("LFJ2", 0, 90, 10, c, JointType.HAND);
        addAxis("LFJ3", 0, 90, 10, c, JointType.HAND);
        addAxis("LFJ4", -20, 20, 10, c, JointType.HAND);
        addAxis("LFJ5", 0, 45, 10, c, JointType.HAND);
    }

    public void start() {
        if(running) {
            return;
        }

        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerCaller.run();
            }
        }, 1000 / this.UPDATE_FREQ, 1000 / this.UPDATE_FREQ);
        running = true;
    }

    public void stop() {
        if(!running) {
            return;
        }

        timer.cancel();
        timer = null;
        running = false;
    }

    private void timerTick() {
        if(init) {
            return;
        }


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
                ct = at;
            }
            else {
                if(at > ct) {
                    ct += maxStep;
                }
                else if(at < ct) {
                    ct -= maxStep;
                }


            }

            axis.setCurrentTargetValue(ct);

        }

        notifyObservers();

        // send data:
        sendJointData(JointType.ARM);
        sendJointData(JointType.HAND);
    }

    private void sendJointData(int jointType) {
        if(robotNode != null) {
            HashMap<String, Double> map = new HashMap<>();

            for(AxisInformationImpl ai : axes.values()) {
                if(/*ai.isEnabled() && */ai.getJointType() == jointType)
                {
                    map.put(ai.getIdentifier(), ai.getCurrentTargetValueAsRobotValue());
                }
            }

            robotNode.handleJointData(jointType, map);
        }
    }

    private void addAxis(String identifier, double minValue, double maxValue, double maxSpeed, ValueConverter conv, int jointType, boolean enabled) {
        axes.put(identifier, new AxisInformationImpl(identifier, minValue, maxValue, maxSpeed, conv, jointType, enabled));
    }

    private void addAxis(String identifier, double minValue, double maxValue, double maxSpeed, ValueConverter conv, int jointType) {
        addAxis(identifier, minValue, maxValue, maxSpeed, conv, jointType, true);
    }

    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    public void setRobotNode(RobotJointDataReceiver node) {
        robotNode = node;
    }

    public boolean startMoving(String identifier, double speed) {
        if(!running || _locked) {
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
            stopMoving(i.getIdentifier());
            setTargetValue(i.getIdentifier(), i.getCurrentValue(), true, false);
        }

        notifyObservers();
        return true;
    }

    public boolean setAllZero(boolean force) {
        if(!running) {
            return false;
        }

        for(AxisInformationImpl i : axes.values()) {
            stopMoving(i.getIdentifier());
            i.setTargetValue(0.0);
        }

        notifyObservers();
        return true;
    }

    public boolean setTargetValue(String identifier, double value, boolean force, boolean notifyObservers) {
        if(!running || !axes.containsKey(identifier)) {
            return false;
        }

        if(_locked && !force)
        {
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

    @Override
    public void handleJointData(int jointType, HashMap<String, Double> data) {
        for(Map.Entry<String, Double> e : data.entrySet()) {
            if(axes.containsKey(e.getKey())) {
                axes.get(e.getKey()).setCurrentValueFromRobotValue(e.getValue());
            }
        }

        if(init) {
            if(initSampleCounter >= INIT_SAMPLES) {
                for(AxisInformationImpl i : axes.values()) {
                    i.setTargetValue(i.getCurrentValue());
                    i.setCurrentTargetValue(i.getCurrentValue());
                }

                init = false;
                if(initListener != null) {
                    initListener.onInitFinished();
                }
            }

            initSampleCounter++;
        }
    }

    public void init() {
        init = true;
        initSampleCounter = 0;
        if(initListener != null) {
            initListener.onInitBegin();
        }
    }

    public void setInitStateListener(InitStateListener lstnr) {
        initListener = lstnr;
    }

    public boolean getLocked()
    {
        return _locked;
    }

    public void setLocked(boolean locked)
    {
        boolean transitsToLocked = !_locked && locked;
        _locked = locked;

        if(transitsToLocked)
        {
            copyCurrentValuesToTarget();
        }
    }

    public Map<String, Double> getRobotState() {
        HashMap<String, Double> ret = new HashMap<String, Double>();

        for(AxisInformationImpl i : axes.values()) {
            ret.put(i.getIdentifier(), i.getCurrentValueAsRobotValue());
        }

        return ret;
    }
}
