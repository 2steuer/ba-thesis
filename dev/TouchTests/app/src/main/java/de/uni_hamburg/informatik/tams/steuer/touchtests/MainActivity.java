package de.uni_hamburg.informatik.tams.steuer.touchtests;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.AxisControlFragment;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.TouchFragment;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.GestureView;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Util.ViewPagerAdapter;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.AxisManager;

import android.support.design.widget.TabLayout;

public class MainActivity extends AppCompatActivity {
    AxisManager axisManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        axisManager = AxisManager.getInstance();

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
    protected void onStop() {
        super.onStop();
        axisManager.stop();
    }


}
