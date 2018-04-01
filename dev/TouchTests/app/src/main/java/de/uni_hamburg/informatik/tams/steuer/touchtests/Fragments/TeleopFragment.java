package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.uni_hamburg.informatik.tams.steuer.touchtests.R;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.CartesianArmManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.PointInSpace;

public class TeleopFragment extends Fragment implements View.OnClickListener {

    AxisManager axes;
    CartesianArmManager arm;

    Button home = null;

    Button left = null;
    Button right = null;
    Button up = null;
    Button down = null;

    Button forward = null;
    Button back = null;

    public TeleopFragment() {
        axes = AxisManager.getInstance();
        arm = CartesianArmManager.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teleop, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        home = ((Button)getView().findViewById(R.id.button_home));
        home.setOnClickListener(this);

        left = ((Button)getView().findViewById(R.id.button_left));
        left.setOnClickListener(this);

        right = ((Button)getView().findViewById(R.id.button_right));
        right.setOnClickListener(this);

        up = ((Button)getView().findViewById(R.id.button_up));
        up.setOnClickListener(this);

        down = ((Button)getView().findViewById(R.id.button_down));
        down.setOnClickListener(this);

        forward = ((Button)getView().findViewById(R.id.button_forward));
        forward.setOnClickListener(this);

        back = ((Button)getView().findViewById(R.id.button_back));
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == home) {
            arm.goHome();
        }
        else if(view == left) {
            arm.movePalm(new PointInSpace(-0.01, 0, 0));
        }
        else if(view == right) {
            arm.movePalm(new PointInSpace(0.01, 0, 0));
        }
        else if(view == up) {
            arm.movePalm(new PointInSpace(0, 0, 0.01));
        }
        else if(view == down) {
            arm.movePalm(new PointInSpace(0, 0, -0.01));
        }
        else if(view == forward) {
            arm.movePalm(new PointInSpace(0, 0.01, 0));
        }
        else if(view == back) {
            arm.movePalm(new PointInSpace(0, -0.01, 0));
        }
    }
}
