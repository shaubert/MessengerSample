package com.shaubert.liftago.util;

import android.view.View;
import android.view.ViewTreeObserver;

public class ViewTreeObserverHelper {

    public static void registerOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    public static void unregisterOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Versions.hasJellyBeanApi()) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

}
