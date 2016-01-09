package com.shaubert.liftago.util;

import android.os.Parcel;

import java.util.Map;

public class Parcelables {

    public static void writeStringMap(Map<String, String> map, Parcel out) {
        out.writeInt(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            out.writeString(entry.getKey());
            out.writeString(entry.getValue());
        }
    }

    public static void readStringMap(Map<String, String> map, Parcel in) {
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            map.put(in.readString(), in.readString());
        }
    }

}
