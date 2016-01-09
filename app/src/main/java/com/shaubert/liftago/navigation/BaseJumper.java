package com.shaubert.liftago.navigation;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.shaubert.liftago.uiroot.BaseActivity;
import com.shaubert.ui.jumper.AbstractJumper;

public abstract class BaseJumper extends AbstractJumper implements Jumper {

    private boolean fragment;
    private RedirectListener redirectListener;

    public BaseJumper(Context context) {
        super(context);
    }

    public BaseJumper(Fragment fragment) {
        super(fragment);
        this.fragment = true;
    }

    public BaseJumper(android.support.v4.app.Fragment fragment) {
        super(fragment);
        this.fragment = true;
    }

    public boolean isFragment() {
        return fragment;
    }

    @Override
    public void setRedirectListener(RedirectListener redirectListener) {
        this.redirectListener = redirectListener;
    }

    @Override
    public void dispatchOnCreate(Bundle bundle) {
        super.dispatchOnCreate(bundle);

        Context context = getStarter().getContext();
        if (!fragment && context instanceof BaseActivity) {
            processRedirectStack((BaseActivity) context);
        }
    }

    private void processRedirectStack(BaseActivity activity) {
        Intent intent = activity.getIntent();
        if (intent == null) {
            return;
        }

        if (activity.isIgnoreIntent()) {
            return;
        }

        if (!intent.hasExtra(ActivityStack.EXTRA_ACTIVITY_STACK)) {
            return;
        }

        Bundle bundleExtra = intent.getBundleExtra(ActivityStack.EXTRA_ACTIVITY_STACK);
        intent.removeExtra(ActivityStack.EXTRA_ACTIVITY_STACK);
        if (bundleExtra == null) {
            return;
        }

        ActivityStack activityStack = ActivityStack.restore(bundleExtra);
        processRedirectStack(activityStack);
    }

    @Override
    public void processRedirectStack(ActivityStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        ActivityStack.Record record = stack.pop();
        if (record == null) {
            return;
        }

        if (redirectListener != null && redirectListener.onRedirect(this, record, stack)) {
            return;
        }

        Bundle bundle = record.getBundle();
        if (!stack.isEmpty()) {
            bundle = ExtrasHelper.addRedirectStack(stack, bundle);
        }

        to(record.getActClass()).withExtras(bundle).jump();
    }

}
