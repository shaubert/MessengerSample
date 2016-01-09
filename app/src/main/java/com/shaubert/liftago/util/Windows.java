package com.shaubert.liftago.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class Windows {

    public static boolean isTranslucentStatusBar(Activity activity) {
        if (!Versions.hasKitKatApi()) {
            return false;
        }

        Window w = activity.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        int flags = lp.flags;
        if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
            return true;
        }

        return false;
    }

}
