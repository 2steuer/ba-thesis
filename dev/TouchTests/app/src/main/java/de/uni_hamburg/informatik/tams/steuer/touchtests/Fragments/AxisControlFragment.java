package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.AxisControlView;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.Interfaces.AxisControllerObserver;
import de.uni_hamburg.informatik.tams.steuer.touchtests.R;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.AxisInformationImpl;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.AxisInformation;

/**
 * Created by merlin on 25.11.17.
 */

public class AxisControlFragment extends Fragment implements View.OnClickListener {
    HashMap<String, AxisControlView> controls = new HashMap<String, AxisControlView>();
    AxisManager axisManager;

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateAllControllers();
        }
    };

    Button zeroButton;
    Button stopButton;

    AxisControllerObserver axisCallback = new AxisControllerObserver() {
        @Override
        public void onStartMoving(String axisIdentifier, int direction) {
            axisManager.startMoving(axisIdentifier, 5 * Math.signum(direction));
        }

        @Override
        public void onStopMoving(String axisIdentifier) {
            axisManager.stopMoving(axisIdentifier);
        }
    };

    public AxisControlFragment() {
        axisManager = AxisManager.getInstance();

        axisManager.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
               Activity a = getActivity();
               if(a != null)
               {
                   a.runOnUiThread(updateRunnable);
               }

            }
        });


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_axiscontrol, container, false);


    }

    @Override
    public void onStart() {
        super.onStart();

        //only populate the map once
        if(controls.size() == 0 && getView() instanceof ViewGroup) {
            ViewGroup grp = (ViewGroup)getView();
            populateAxisControllerMap(grp);
        }

        zeroButton = (Button)getView().findViewById(R.id.zeroButton);
        zeroButton.setOnClickListener(this);
        stopButton = (Button)getView().findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        final FloatingActionButton lockButton = ((FloatingActionButton)getView().findViewById(R.id.lockButton));

        lockButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        axisManager.setLocked(false);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posOk)));

                        lockButton.setImageResource(android.R.drawable.ic_media_pause);
                        break;

                    case MotionEvent.ACTION_UP:
                        axisManager.setLocked(true);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posNOk)));
                        lockButton.setImageResource(android.R.drawable.ic_media_play);

                        break;
                }

                return true;
            }


        });
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    private void populateAxisControllerMap(ViewGroup grp) {
        for(int i = 0; i < grp.getChildCount(); i++) {
            View v = grp.getChildAt(i);
            if(v instanceof AxisControlView) {
                AxisControlView acv = (AxisControlView)v;

                AxisInformation in = axisManager.getAxisInfo(acv.getAxisIdentifier());
                acv.setDisplayOnly(!in.isEnabled());

                controls.put(acv.getAxisIdentifier(), acv);
                acv.setObserver(axisCallback);
            }
            else if(v instanceof ViewGroup) {
                populateAxisControllerMap((ViewGroup)v);
            }
        }
    }

    private void updateAllControllers() {
        for(AxisInformation i : axisManager.getAllAxisInfos()) {
            if(controls.containsKey(i.getIdentifier())) {
                AxisControlView acv = controls.get(i.getIdentifier());
                acv.setTargetAngle(i.getTargetValue());
                acv.setCurrentAngle(i.getCurrentValue());
            }
        }
    }

    /**
     * STOP and ZERO button handler!
     * @param view the view
     */
    @Override
    public void onClick(View view) {
        Button b = (Button)view;

        if(b == zeroButton) {
            AlertDialog.Builder diag = new AlertDialog.Builder(getContext());
            diag.setMessage("Sind Sie sicher? Dies kannn großen Schaden am Gerät verursachen.")
                    .setNegativeButton("NEIN!", null)
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            axisManager.setAllZero(false);
                        }
                    }).create().show();
        }
        else if(b == stopButton) {
            axisManager.copyCurrentValuesToTarget();
        }
    }
}
