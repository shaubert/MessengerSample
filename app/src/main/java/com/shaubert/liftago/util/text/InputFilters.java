package com.shaubert.liftago.util.text;

import android.text.InputFilter;
import android.widget.TextView;

public class InputFilters {

    public static void appendFilter(TextView textView, InputFilter ... inputFilters) {
        InputFilter[] oldFilters = textView.getFilters();
        InputFilter[] filters = new InputFilter[(oldFilters != null ? oldFilters.length : 0) + inputFilters.length];
        if (oldFilters != null) {
            System.arraycopy(oldFilters, 0, filters, 0, filters.length - inputFilters.length);
        }
        System.arraycopy(inputFilters, 0, filters, filters.length - inputFilters.length, inputFilters.length);
        textView.setFilters(filters);
    }

    public static void removeFilter(TextView textView, InputFilter inputFilter) {
        InputFilter[] oldFilters = textView.getFilters();
        if (oldFilters == null || oldFilters.length == 0) {
            return;
        }

        int filterPos = 0;
        for (; filterPos < oldFilters.length; filterPos++) {
            if (oldFilters[filterPos] == inputFilter) {
                break;
            }
        }
        if (filterPos < oldFilters.length) {
            InputFilter[] filters = new InputFilter[oldFilters.length - 1];
            System.arraycopy(oldFilters, 0, filters, 0, filterPos);
            System.arraycopy(oldFilters, filterPos + 1, filters, filterPos, oldFilters.length - filterPos - 1);
            textView.setFilters(filters);
        }
    }
}
