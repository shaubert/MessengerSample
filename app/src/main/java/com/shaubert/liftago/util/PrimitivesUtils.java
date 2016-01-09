package com.shaubert.liftago.util;

public class PrimitivesUtils {

    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(float x, float y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(double x, double y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static long parseLongSilent(String strLong) {
        try {
            return Long.parseLong(strLong);
        } catch (NumberFormatException ex) {
            Logger.warn("PrimitivesUtils", "error parsing long \"" + strLong + "\"", ex);
        }
        return -1;
    }

    public static String toStringOrNull(Object x) {
        return x == null ? null : String.valueOf(x);
    }

    public static boolean equalsBy001(float a, float b) {
        return a == b || Math.abs(a - b) <= 0.001f;
    }
}
