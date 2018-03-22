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

public class TeleopFragment extends Fragment implements View.OnClickListener {

    AxisManager axes;
    CartesianArmManager arm;

    Button home = null;

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
    }

    @Override
    public void onClick(View view) {
        if(view == home) {
            arm.goHome();
        }
    }
}
