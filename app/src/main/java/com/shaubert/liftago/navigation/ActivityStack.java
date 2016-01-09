package com.shaubert.liftago.navigation;

import android.app.Activity;
import android.os.Bundle;

import java.util.Stack;

public class ActivityStack {
    public static final String EXTRA_ACTIVITY_STACK = "extra_activity_stack";
    public static final String EXTRA_REDIRECT_TO_ACTIVITY_CLASS = "extra_redirect_to_activity_class_name";
    public static final String EXTRA_REDIRECT_TO_ACTIVITY_BUNDLE = "extra_redirect_to_activity_bundle";
    
    private Stack<Record> recordStack = new Stack<Record>();

    public static ActivityStack restore(Bundle bundle) {
        ActivityStack result = new ActivityStack();
        int size = bundle.getInt("size", 0);
        for (int i = size - 1; i >= 0; i--) {
            Record record = Record.restore(bundle.getBundle(String.valueOf(i)));
            result.recordStack.add(record);
        }
        return result;
    }

    public Record pop() {
        return recordStack.pop();
    }

    public Record peek() {
        return recordStack.peek();
    }

    public boolean isEmpty() {
        return recordStack.isEmpty();
    }

    public void push(Class<? extends Activity> actClass, Bundle bundle) {
        push(new Record(actClass, bundle));
    }

    public void push(Record record) {
        recordStack.push(record);
    }

    public Bundle toBundle() {
        Bundle result = new Bundle();
        int size = recordStack.size();
        result.putInt("size", size);

        int pos = 0;
        for (Record record : recordStack) {
            result.putBundle(String.valueOf(pos), record.toBundle());
            pos++;
        }
        return result;
    }

    public static class Record {
        private Class<? extends Activity> actClass;
        private Bundle bundle;

        public static Record restore(Bundle fromBundle) {
            @SuppressWarnings("unchecked")
            Class<? extends Activity> actClass =
                    (Class<? extends Activity>) fromBundle.getSerializable(EXTRA_REDIRECT_TO_ACTIVITY_CLASS);
            Bundle args = fromBundle.getBundle(EXTRA_REDIRECT_TO_ACTIVITY_BUNDLE);
            return new Record(actClass, args);
        }

        public Record(Class<? extends Activity> actClass, Bundle bundle) {
            this.actClass = actClass;
            this.bundle = bundle;
        }

        public Class<? extends Activity> getActClass() {
            return actClass;
        }

        public void setActClass(Class<? extends Activity> actClass) {
            this.actClass = actClass;
        }

        public Bundle getBundle() {
            return bundle;
        }

        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        public Bundle toBundle() {
            Bundle result = new Bundle();
            result.putSerializable(EXTRA_REDIRECT_TO_ACTIVITY_CLASS, actClass);
            result.putBundle(EXTRA_REDIRECT_TO_ACTIVITY_BUNDLE, bundle);
            return result;
        }

    }

}
