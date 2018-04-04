package de.uni_hamburg.informatik.tams.steuer.touchtests.FingerTipMapping;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bio_ik_msgs.GetIKResponse;
import bio_ik_msgs.IKResponse;
import de.uni_hamburg.informatik.tams.steuer.touchtests.FingerTipMapping.Material.FingertipPointer;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.PointInSpace;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.C5LwrNode;

/**
 * Created by merlin on 04.04.18.
 */

public class DftmProxy implements View.OnTouchListener, ServiceResponseListener<GetIKResponse> {
    public static final int MAX_CONTROLLABLE_FINGERS = 3;
    public static final int CATCH_RADIUS = 200;

    private static final String[] effectorNames = {
        "thtip",
        "fftip",
        "mftip"
    };

    private FingertipPointer[] _pointers = new FingertipPointer[MAX_CONTROLLABLE_FINGERS];

    PointInSpace surfaceBase = new PointInSpace(-1.0, 1.1, 1.2);
    PointInSpace surfaceYBaseVect = new PointInSpace(0, -1, 0);
    PointInSpace surfaceXBaseVect = new PointInSpace(1, 0, 0);

    private int _dpi = 0;
    private int _width = 0;
    private int _height = 0;

    private static DftmProxy instance = null;
    public static DftmProxy getInstance() {
        if(instance == null)
        {
            instance = new DftmProxy();
        }

        return instance;
    }

    private C5LwrNode node = null;
    private AxisManager axes = AxisManager.getInstance();

    boolean running = false;
    Lock runningLock = new ReentrantLock();

    private DftmProxy() {
        for(int i = 0; i < _pointers.length; i++) {
            _pointers[i] = null;
        }
    }

    public void setNode(C5LwrNode n) {
        node = n;
    }

    public void setScreenMetrics(int width, int height, int dpi) {
        _width = width;
        _height = height;
        _dpi = dpi;
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        int action = e.getActionMasked();
        int index = e.getActionIndex();
        int id = e.getPointerId(index);

        float x = e.getX(index);
        float y = e.getY(index);

        Location loc = new Location(x, y);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                int pIndex = 0;
                boolean found = false;

                while (!found && pIndex < MAX_CONTROLLABLE_FINGERS) {
                    if (_pointers[pIndex] == null) {
                        _pointers[pIndex] = new FingertipPointer(effectorNames[pIndex], loc, id);
                        fillWorldLocation(_pointers[pIndex]);
                        Log.i("Tip", "Adding pointer " + id + " to " + pIndex);
                        found = true;
                    } else if (!_pointers[pIndex].isPresent() &&
                            _pointers[pIndex].getScreenLocation().distanceTo(loc) <= CATCH_RADIUS) {
                        _pointers[pIndex].setPresent(true);
                        _pointers[pIndex].setPointerId(id);
                        _pointers[pIndex].setScreenLocation(loc);
                        fillWorldLocation(_pointers[pIndex]);

                        Log.i("Tip", "Reviving pointer " + id + " to " + pIndex);
                        found = true;
                    }

                    pIndex++;
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                // find index of the pointer that was lift
                int pIndex = -1;
                for (int i = 0; i < MAX_CONTROLLABLE_FINGERS; i++) {
                    if (_pointers[i] != null &&
                            _pointers[i].getPointerId() == id) {
                        pIndex = i;
                    }
                }

                // not in the registered pointers, so probably just another pointer
                if (pIndex == -1) {
                    break;
                }

                // check if it is the last pointer in the array
                // that was != null, if so, set null, otherwise set present = false
                boolean last = true;
                for (int i = pIndex + 1; i < MAX_CONTROLLABLE_FINGERS && last; i++) {
                    last = _pointers[i] == null;
                }

                if (last) {
                    _pointers[pIndex] = null;

                    boolean moveOn = true;
                    for(int i = (pIndex - 1); i >= 0 && moveOn; i--) {
                        if(!_pointers[i].isPresent()) {
                            _pointers[i] = null;
                        }
                        else {
                            moveOn = false;
                        }

                    }

                } else {
                    _pointers[pIndex].setPresent(false);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                for(int i = 0; i < e.getPointerCount(); i++) {
                    int pid = e.getPointerId(i);

                    int pIndex = -1;
                    for (int j = 0; j < MAX_CONTROLLABLE_FINGERS; j++) {
                        if (_pointers[j] != null &&
                                _pointers[j].getPointerId() == pid) {
                            pIndex = j;
                        }
                    }

                    // not in the registered pointers, so probably just another pointer
                    if (pIndex != -1) {
                        _pointers[pIndex].setScreenLocation(new Location(
                                e.getX(i),
                                e.getY(i)
                        ));
                        fillWorldLocation(_pointers[pIndex]);
                    }
                }
                // find index of the pointer that was lift

                break;
            }
        }



        return true;
    }

    public FingertipPointer getFingertipPointer(int index) {
        return index < MAX_CONTROLLABLE_FINGERS ? _pointers[index] : null;
    }

    private void fillWorldLocation(FingertipPointer p) {
        Location l = p.getScreenLocation();
        Location wl = new Location(
                (l.getX() / _dpi) * 0.0254f,
                (l.getY() / _dpi) * 0.0254f
        );

        p.setWorldLocation(wl);
    }

    private void updateRobot() {
        Map<String, PointInSpace> goals = new HashMap<String, PointInSpace>();

        for(int i = 0; i < MAX_CONTROLLABLE_FINGERS; i++) {
            if(_pointers[i] == null) {
                continue;
            }

            FingertipPointer p = _pointers[i];

            PointInSpace wloc = surfaceBase
                    .add(surfaceXBaseVect.multiply(p.getWorldLocation().getX()))
                    .add(surfaceYBaseVect.multiply(p.getWorldLocation().getY()));

            goals.put(p.getEffectorName(), wloc);
        }

        if(goals.size() == 0) {
            return;
        }

        node.GetIKJointsFingertips(axes.getRobotState(), goals, this);
    }

    @Override
    public void onSuccess(GetIKResponse ikResponse) {
        
    }

    @Override
    public void onFailure(RemoteException e) {

    }
}
