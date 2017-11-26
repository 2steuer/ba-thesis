package de.uni_hamburg.informatik.tams.steuer.touchtests;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.AxisControlFragment;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.TouchFragment;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.GestureView;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Util.ViewPagerAdapter;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.C5LwrNode;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatDelegate;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {
    AxisManager axisManager;

    C5LwrNode node;

    public MainActivity() {
        super("ROS", "ROS TEST");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        axisManager = AxisManager.getInstance();
        node = new C5LwrNode("/joint_states", "/config/fake_controller_joint_states");

        node.addJointDataListener(axisManager);
        axisManager.setRobotNode(node);

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new TouchFragment(), "Touch");
        adapter.addFragment(new AxisControlFragment(), "Axis Control");

        pager.setAdapter(adapter);

        TabLayout tab = (TabLayout)findViewById(R.id.tabs);
        tab.setupWithViewPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        axisManager.start();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration cfg = NodeConfiguration.newPublic(getRosHostname(), getMasterUri());

        nodeMainExecutor.execute(node, cfg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        axisManager.stop();
    }
}
