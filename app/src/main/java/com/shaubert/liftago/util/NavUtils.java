package com.shaubert.liftago.util;

import android.content.Intent;

public class NavUtils {

    public static boolean isLaunchedFromHistory(Intent intent) {
        return intent != null && (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;
    }

}
