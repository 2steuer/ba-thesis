package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;

import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.GestureView;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.GestureParser;
import de.uni_hamburg.informatik.tams.steuer.touchtests.R;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.SynergyProxy;
import hdbt.shadow.GraspSynergy;

/**
 * Created by merlin on 25.11.17.
 */

public class TouchFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    GestureParser _gestures;
    AxisManager _axes;
    SynergyProxy _synergyProxy = new SynergyProxy();

    List<String> synergyNames = new LinkedList<>();
    Map<String, GraspSynergy> synergies = new HashMap<>();

    public TouchFragment() {
        _axes = AxisManager.getInstance();



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_touch, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        final GestureView gview = ((GestureView)getView().findViewById(R.id.gestView));
        _gestures = gview.getGestureParser();
        _gestures.addObserver(_synergyProxy);
        final FloatingActionButton lockButton = ((FloatingActionButton)getView().findViewById(R.id.lockButton));

        _synergyProxy.setCanvasSize(gview.getWidth(), gview.getHeight());

        gview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                _synergyProxy.setCanvasSize(gview.getWidth(), gview.getHeight());
            }
        });

        lockButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        _axes.setLocked(false);
                        _synergyProxy.setLocked(false);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posOk)));

                        lockButton.setImageResource(android.R.drawable.ic_media_pause);
                        break;

                    case MotionEvent.ACTION_UP:
                        _axes.setLocked(true);
                        _synergyProxy.setLocked(true);
                        lockButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.posNOk)));
                        lockButton.setImageResource(android.R.drawable.ic_media_play);

                        break;
                }

                return true;
            }


        });



        loadSynergy("Grasp 1", R.raw.g1mean_n, R.raw.g1vecs_n);
        loadSynergy("Grasp 2", R.raw.g2mean_n, R.raw.g2vecs_n);
        loadSynergy("Grasp 3", R.raw.g3mean_n, R.raw.g3vecs_n);
        loadSynergy("Grasp 4", R.raw.g4mean_n, R.raw.g4vecs_n);
        loadSynergy("Grasp 5", R.raw.g5mean_n, R.raw.g5vecs_n);
        loadSynergy("Grasp 6", R.raw.g6mean_n, R.raw.g6vecs_n);
        loadSynergy("Grasp 7", R.raw.g7mean_n, R.raw.g7vecs_n);
        loadSynergy("Grasp 8", R.raw.g8mean_n, R.raw.g8vecs_n);

        _synergyProxy.addListener(gview);

        Spinner sp = (Spinner)(getView().findViewById(R.id.gest_spinner));

        String[] array = new String[synergyNames.size()];
        array = synergyNames.toArray(array);

        ArrayAdapter<String> entries = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, array);
        sp.setAdapter(entries);

        sp.setOnItemSelectedListener(this);

        sp.setSelection(0);

        ((Button) (getView().findViewById(R.id.amp_all_zero))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _synergyProxy.allAmplitudesZero();
            }
        });

        ((Button) (getView().findViewById(R.id.amp_all_extend))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _synergyProxy.allAmplitudesZeroValue();
            }
        });
    }

    private GraspSynergy loadSynergy(String name, int mean_res, int vec_res) {
        try {
            GraspSynergy synergy = new GraspSynergy(21);
            synergy.parseMatlabSynergyMean(getResources().openRawResource(mean_res));
            synergy.parseMatlabSynergyVecs(getResources().openRawResource(vec_res));

            synergyNames.add(name);
            synergies.put(name, synergy);

            return synergy;
        } catch (Exception ex) {
            Log.e("TouchFragment", "Error while parsing test synergy.");
            Log.e("TouchFragment", ex.getMessage());
            return null;
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        _synergyProxy.setAxisManager(_axes);

    }

    @Override
    public void onPause() {
        super.onPause();
        _synergyProxy.setAxisManager(null);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        String name = synergyNames.get(pos);
        _synergyProxy.setGraspSynergy(synergies.get(name));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        _synergyProxy.setGraspSynergy(null);
    }
}
