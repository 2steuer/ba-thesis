package de.uni_hamburg.informatik.tams.steuer.touchtests.Fragments.Views.dftm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.uni_hamburg.informatik.tams.steuer.touchtests.FingerTipMapping.DftmProxy;
import de.uni_hamburg.informatik.tams.steuer.touchtests.FingerTipMapping.Material.FingertipPointer;
import de.uni_hamburg.informatik.tams.steuer.touchtests.GestureParsing.Material.Location;

/**
 * Created by merlin on 04.04.18.
 */

public class FingertipView extends View {
    DftmProxy proxy = DftmProxy.getInstance();

    Paint orangePaint = new Paint();
    Paint blackPaint = new Paint();
    Paint text = new Paint();

    public FingertipView(Context context) {
        super(context);
        init();
    }

    public FingertipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FingertipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        orangePaint.setStyle(Paint.Style.FILL);
        orangePaint.setColor(Color.argb(128, 255,165,0));

        blackPaint.setStyle(Paint.Style.STROKE);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(4);

        text.setStyle(Paint.Style.FILL);
        text.setTextSize(24);
        text.setColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        proxy.onTouch(this, event);
        this.invalidate();

        // we need to return true to get following touch events, too.
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i = 0; i < DftmProxy.MAX_CONTROLLABLE_FINGERS; i++) {
            FingertipPointer pointer = proxy.getFingertipPointer(i);

            if(pointer != null) {
                Location sl = pointer.getScreenLocation();
                Location sw = pointer.getWorldLocation();
                String name = pointer.getEffectorName();
                canvas.drawCircle(sl.getX(), sl.getY(), 100, pointer.isPresent() ? orangePaint : blackPaint);

                float x = sl.getX() - 300;
                float y = sl.getY();

                canvas.drawText(name, x, y, text);
                canvas.drawText(String.format("X: %.0f Y: %.0f", sl.getX(), sl.getY()), x, y + 30, text);
                canvas.drawText(String.format("wX: %.2f wY: %.2f", sw.getX() * 100, sw.getY() * 100), x, y + 60, text);
            }
        }
    }
}
