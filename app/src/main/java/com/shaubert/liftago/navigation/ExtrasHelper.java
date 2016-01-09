package com.shaubert.liftago.navigation;

import android.os.Bundle;

import java.util.Set;

public class ExtrasHelper {

    public static final String INTENT_EXTRA_EXECUTION = "extra_execution";

    public static Bundle addRedirectStack(ActivityStack activityStack, Bundle args) {
        if (args == null) {
            args = new Bundle();
        }
        args.putBundle(ActivityStack.EXTRA_ACTIVITY_STACK, activityStack.toBundle());
        return args;
    }

    public static Bundle executionExtra(ExecutionExtra execution) {
        return addExecutionExtra(execution, null);
    }

    public static Bundle addExecutionExtra(ExecutionExtra execution, Bundle to) {
        if (to == null) {
            to = new Bundle();
        }

        if (execution != null) {
            to.putParcelable(INTENT_EXTRA_EXECUTION, execution);
        }

        return to;
    }

    public static boolean equals(Bundle one, Bundle two) {
        if (one == two) return true;
        if (one == null || two == null) return false;
        if (one.size() != two.size()) return false;

        Set<String> setOne = one.keySet();
        Object valueOne;
        Object valueTwo;

        for (String key : setOne) {
            valueOne = one.get(key);
            valueTwo = two.get(key);
            if (valueOne instanceof Bundle
                    && valueTwo instanceof Bundle
                    && !equals((Bundle) valueOne, (Bundle) valueTwo)) {
                return false;
            } else if (valueOne == null) {
                if (valueTwo != null || !two.containsKey(key)) {
                    return false;
                }
            } else if (!valueOne.equals(valueTwo)) {
                return false;
            }
        }

        return true;
    }

}
