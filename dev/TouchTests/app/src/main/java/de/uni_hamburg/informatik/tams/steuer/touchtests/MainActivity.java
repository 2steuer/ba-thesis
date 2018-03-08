package de.uni_hamburg.informatik.tams.steuer.touchtests;

import android.app.ProgressDialog;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.AxisControlFragment;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.AbsoluteSynergyTouchFragment;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.RelativeSynergyTouchFragment;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Util.ViewPagerAdapter;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Material.Interfaces.InitStateListener;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.C5LwrNode;

import android.support.design.widget.TabLayout;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {
    AxisManager axisManager;

    C5LwrNode node;

    ProgressDialog diag = null;

    public MainActivity() {
        super("ROS", "ROS TEST");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new RelativeSynergyTouchFragment(), "Relative");
        adapter.addFragment(new AbsoluteSynergyTouchFragment(), "Absolute");
        adapter.addFragment(new AxisControlFragment(), "Axis Control");

        pager.setAdapter(adapter);

        TabLayout tab = (TabLayout)findViewById(R.id.tabs);
        tab.setupWithViewPager(pager);

        diag = new ProgressDialog(this);
        diag.setCancelable(false);
        diag.setIndeterminate(true);
        diag.setMessage("Initializing. Please wait.");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        axisManager = AxisManager.getInstance();
        node = new C5LwrNode("/joint_states", "/config/fake_controller_joint_states");

        node.addJointDataListener(axisManager);
        axisManager.setRobotNode(node);

        axisManager.setInitStateListener(new InitStateListener() {
            @Override
            public void onInitBegin() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        diag.show();
                    }
                });
            }

            @Override
            public void onInitFinished() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        diag.hide();
                    }
                });
            }
        });

        NodeConfiguration cfg = NodeConfiguration.newPublic(getRosHostname(), getMasterUri());

        nodeMainExecutor.execute(node, cfg);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(axisManager != null) {
            axisManager.stop();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(axisManager != null) {
            axisManager.start();
            axisManager.init();
        }
    }
}
