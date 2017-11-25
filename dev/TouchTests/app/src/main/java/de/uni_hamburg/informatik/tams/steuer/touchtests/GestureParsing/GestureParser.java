package de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing;

import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Pointer;

/**
 * Created by merlin on 22.11.17.
 */

public class GestureParser {
    private HashMap<Integer, Pointer> pointers = new HashMap<Integer, Pointer>();

    private HashMap<Pointer, Gesture> pointToGestures = new HashMap<Pointer, Gesture>();

    private List<Gesture> gestures = new ArrayList<Gesture>();

    public void handleTouchEvent(MotionEvent e)
    {
        int action = e.getActionMasked();
        int index = e.getActionIndex();
        int id = e.getPointerId(index);

        float x = e.getX(index);
        float y = e.getY(index);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if(!pointers.containsKey(id)) {
                    Log.d("Gesture", "New Pointer: " + id);
                    Pointer newP = new Pointer(id, x, y);
                    pointers.put(id, newP);

                    Gesture gest = null;
                    float curDist = Float.MAX_VALUE;

                    for(Gesture g : gestures) {
                        if(g.catchesPointer(newP)) {
                            float dist = g.getDistanceToCenter(newP);
                            if(dist < curDist) {
                                gest = g;
                                curDist = dist;
                            }

                        }
                    }

                    if(gest != null) {
                        gest.addPointer(newP);
                        pointToGestures.put(newP, gest);
                    }
                    else {
                        Gesture newGesture = new Gesture();
                        newGesture.addPointer(newP);
                        gestures.add(newGesture);
                        pointToGestures.put(newP, newGesture);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if(pointers.containsKey(id)) {
                    Log.d("Gesture", "Removed Pointer: " + id);
                    Pointer p = pointers.get(id);
                    pointers.remove(id);

                    Gesture g = pointToGestures.get(p);
                    g.removePointer(p);

                    if(g.getPointerCount() == 0) {
                        gestures.remove(g);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                for(int i = 0; i < e.getPointerCount(); i++) {
                    int pid = e.findPointerIndex(i);

                    if(pointers.containsKey(pid)) {
                        pointers.get(pid).setLocation(e.getX(i), e.getY(i));
                    }
                }


                break;
        }
    }

    public Collection<Pointer> getPointers() {
        return pointers.values();
    }

    public Collection<Gesture> getGestures() {
        return gestures;
    }
}
