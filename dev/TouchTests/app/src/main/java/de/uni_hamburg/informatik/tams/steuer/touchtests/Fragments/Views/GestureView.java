package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.GestureParser;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Gesture;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Pointer;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Synergies.Interfaces.SynergyAmplitudeListener;

/**
 * Created by merlin on 22.11.17.
 */

public class GestureView extends View implements SynergyAmplitudeListener {
    GestureParser gestures = new GestureParser();

    Paint paintRed = new Paint();
    Paint paintBlack = new Paint();
    Paint paintOrange = new Paint();

    double[] _amplitudes = new double[0];
    int _controlledAmplitudes = 0;

    public GestureView(Context context) {
        super(context);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GestureParser getGestureParser() {
        return gestures;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestures.handleTouchEvent(event);
        this.invalidate();

        // we need to return true to get following touch events, too.
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.paintRed.setColor(Color.RED);
        this.paintRed.setStyle(Paint.Style.FILL);

        this.paintBlack.setColor(Color.BLACK);
        this.paintBlack.setStyle(Paint.Style.FILL);

        this.paintOrange.setARGB(128, 255,165,0);
        this.paintOrange.setStyle(Paint.Style.FILL);

        for (Gesture g : gestures.getGestures()) {
            Location com = g.getCenter();

            canvas.drawCircle(com.getX(), com.getY(), g.getSize() / 2, paintOrange);

            for (Pointer p : g.getPointers()) {
                Location ploc = p.getLocation();
                canvas.drawLine(ploc.getX(), ploc.getY(), com.getX(), com.getY(), paintBlack);
                canvas.drawCircle(ploc.getX(), ploc.getY(), 40, this.paintRed);
            }

            canvas.drawCircle(com.getX(), com.getY(), 20, paintBlack);


        }

        int textOffset = 100;

        paintBlack.setTextSize(32);

        for(int i = 0; i < Math.min(_amplitudes.length, _controlledAmplitudes); i++) {
            String str = String.format("%d: %f", i, _amplitudes[i]);
            canvas.drawText(str, 10, textOffset, paintBlack);
            textOffset += 50;
        }

    }

    @Override
    public void setAmplitudes(double[] amplitudes, int controlledAmplitudes) {
        _amplitudes = amplitudes;
        _controlledAmplitudes = controlledAmplitudes;
    }
}
