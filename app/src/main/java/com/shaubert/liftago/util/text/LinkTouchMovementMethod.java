package com.shaubert.liftago.util.text;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;
import com.shaubert.liftago.App;

public class LinkTouchMovementMethod extends LinkMovementMethod {
    private TouchableSpan mPressedSpan;
    private final int touchSlop;

    public LinkTouchMovementMethod() {
        touchSlop = ViewConfiguration.get(App.get()).getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPressedSpan = getPressedSpan(textView, spannable, event);
            if (mPressedSpan != null) {
                mPressedSpan.setPressed(true);
                Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                        spannable.getSpanEnd(mPressedSpan));
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mPressedSpan != null) {
                TouchableSpan touchedSpan;
                float x = event.getX();
                float y = event.getY();
                if (x < -touchSlop || x > textView.getWidth() + touchSlop
                        || y < -touchSlop || y > textView.getHeight() + touchSlop) {
                    touchedSpan = null;
                } else {
                    touchedSpan = getPressedSpan(textView, spannable, event);
                }
                if (touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            }
        } else {
            if (mPressedSpan != null) {
                mPressedSpan.setPressed(false);
                super.onTouchEvent(textView, spannable, event);
            }
            mPressedSpan = null;
            Selection.removeSelection(spannable);
        }
        return true;
    }

    private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
        TouchableSpan touchedSpan = null;
        if (link.length > 0) {
            touchedSpan = link[0];
        }
        return touchedSpan;
    }

    private static LinkTouchMovementMethod instance;

    public static LinkTouchMovementMethod get() {
        if (instance == null) {
            instance = new LinkTouchMovementMethod();
        }
        return instance;
    }

}
