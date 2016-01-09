package com.shaubert.liftago.navigation;

import android.os.Parcelable;
import com.shaubert.liftago.uiroot.BaseActivity;

public interface ExecutionExtra extends Parcelable {

    /**
     * Called from Activity.onCreate() or Activity.onNewIntent()
     * @param activity activity
     */
    void execute(BaseActivity activity);

    void onResume();

    void onPause();

    /**
     * @return true if {@link #execute execute()} should not be called again,
     * false if {@link #execute execute()} should be called in next Activity instance;
     */
    boolean isFinished();

}
