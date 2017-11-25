package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.Interfaces.AxisControllerObserver;
import de.uni_hamburg.informatik.tams.steuer.touchtests.R;

/**
 * Created by merlin on 25.11.17.
 */

public class AxisControlView extends LinearLayout {
    public static final int DIRECTION_PLUS = 1;
    public static final int DIRECTION_MINUS = -1;

    private static final double ANGLE_WARN_DELTA = 0.5f;
    private static final double ANGLE_ERR_DELTA = 2f;

    Button _plusButton;
    Button _minusButton;
    TextView _targetTextView;
    TextView _currentTextView;
    TextView _axisNameView;

    String _axisName;
    String _axisIdentifier;

    double targetAngle;
    double currentAngle;

    AxisControllerObserver observer = null;

    View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(view == _plusButton) {
                        if(observer != null) {
                            observer.onStartMoving(_axisIdentifier, AxisControlView.DIRECTION_PLUS);
                        }

                        _minusButton.setEnabled(false);
                    }
                    else if(view == _minusButton) {
                        if(observer != null) {
                            observer.onStartMoving(_axisIdentifier, AxisControlView.DIRECTION_MINUS);
                        }

                        _plusButton.setEnabled(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if(observer != null) {
                        observer.onStopMoving(_axisIdentifier);
                    }

                    _plusButton.setEnabled(true);
                    _minusButton.setEnabled(true);
                    break;
            }

            return false;
        }
    };

    public AxisControlView(Context context) {
        super(context);
        init(context,null);
    }

    public AxisControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        this.setOrientation(LinearLayout.VERTICAL);
        inflate(getContext(), R.layout.axis_controller, this);

        _plusButton = (Button)findViewById(R.id.plus_button);
        _plusButton.setOnTouchListener(touchListener);
        _minusButton = (Button)findViewById(R.id.minus_button);
        _minusButton.setOnTouchListener(touchListener);
        _targetTextView = (TextView)findViewById(R.id.target_value);
        _currentTextView = (TextView)findViewById(R.id.current_value);
        _axisNameView = (TextView)findViewById(R.id.name_text);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AxisControlView,
                0, 0);


        if(attrs != null) {
            setAxisName(a.getString(R.styleable.AxisControlView_axisName));
            setAxisIdentifier(a.getString(R.styleable.AxisControlView_axisIdentifier));
        }


        setCurrentAngle(0);
        setTargetAngle(0);
    }

    public void setObserver(AxisControllerObserver obs) {
        observer = obs;
    }

    public String getAxisName() {
        return _axisName;
    }

    public void setAxisName(String _axisName) {
        this._axisName = _axisName;
        this._axisNameView.setText(_axisName);
    }

    public String getAxisIdentifier() {
        return _axisIdentifier;
    }

    public void setAxisIdentifier(String _axisIdentifier) {
        this._axisIdentifier = _axisIdentifier;
    }

    public void setTargetAngle(double angle) {
        targetAngle = angle;
        _targetTextView.setText(String.format("%.2f", angle));
        updateAngleColor();
    }

    public void setCurrentAngle(double angle) {
        currentAngle = angle;
        _currentTextView.setText(String.format("%.2f", angle));
        updateAngleColor();
    }

    private void updateAngleColor() {
        double delta = Math.abs(currentAngle - targetAngle);
        LinearLayout field = (LinearLayout)findViewById(R.id.angle_field);

        if(delta <= ANGLE_WARN_DELTA) {
            field.setBackgroundResource(R.color.posOk);
        }
        else if(delta < ANGLE_ERR_DELTA) {
            field.setBackgroundResource(R.color.posWarn);
        }
        else {
            field.setBackgroundResource(R.color.posNOk);
        }

    }
}
