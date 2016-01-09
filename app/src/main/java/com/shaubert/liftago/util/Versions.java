package com.shaubert.liftago.util;

import android.os.Build.VERSION;

import static android.os.Build.VERSION_CODES.*;

public class Versions {

    public static boolean isApiLevelAvailable(int level) {
        return VERSION.SDK_INT >= level;
    }

    public static boolean hasHoneycombApi() {
        return isApiLevelAvailable(HONEYCOMB);
    }

    public static boolean hasJellyBeanApi() {
        return isApiLevelAvailable(JELLY_BEAN);
    }

    public static boolean hasGingerbreadApi() {
        return isApiLevelAvailable(GINGERBREAD);
    }

    public static boolean hasICSApi() {
        return isApiLevelAvailable(ICE_CREAM_SANDWICH);
    }

    public static boolean hasKitKatApi() {
        return isApiLevelAvailable(KITKAT);
    }

    public static boolean hasLollipopApi() {
        return isApiLevelAvailable(LOLLIPOP);
    }

    public static boolean hasM() {
        return isApiLevelAvailable(M);
    }
}
