package com.shaubert.liftago.uiroot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.shaubert.liftago.App;
import com.shaubert.liftago.navigation.Jumper;
import com.shaubert.liftago.util.EdgeEffect;
import com.shaubert.liftago.util.Fragments;
import com.shaubert.lifecycle.objects.LifecycleDelegate;
import com.shaubert.lifecycle.objects.LifecycleDispatcher;
import com.shaubert.lifecycle.objects.LifecycleObjectsGroup;
import com.shaubert.ui.jumper.JumperFactory;

import java.util.List;

public class BaseFragment extends Fragment implements LifecycleDelegate {

    private LifecycleObjectsGroup objectsGroup = new LifecycleObjectsGroup();

    private boolean suppressAnimation;

    private Jumper jumper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachToLifecycle(getJumper());
    }

    public void onNewIntent(Intent intent) {
    }

    public Jumper getJumper() {
        if (jumper == null) {
            JumperFactory<Jumper> jumperFactory = App.get().getJumperFactory();
            jumper = jumperFactory.createFor(this);
        }
        return jumper;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dispatchOnCreate(savedInstanceState);
        EdgeEffect.brandGlowEffectFromThemeIfNeeded(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dispatchOnActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatchOnResume();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        dispatchOnPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dispatchOnSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) super.getActivity();
    }

    public void moveToNext(Fragment newFragment, boolean backStack) {
        moveTo(newFragment, backStack, true);
    }

    public void moveToPrev(Fragment newFragment, boolean backStack) {
        moveTo(newFragment, backStack, false);
    }

    private void moveTo(Fragment newFragment, boolean backStack, boolean forward) {
        View view = getView();
        if (view == null) return;

        int id = ((ViewGroup) view.getParent()).getId();
        if (forward) {
            Fragments.moveToNext(getBaseActivity().getSupportFragmentManager(), newFragment, backStack, id);
        } else {
            Fragments.moveToPrev(getBaseActivity().getSupportFragmentManager(), newFragment, backStack, id);
        }
    }

    public void showToast(int resId) {
        getBaseActivity().showToast(resId);
    }

    public void showToast(int resId, Object ... args) {
        getBaseActivity().showToast(resId, args);
    }

    public void showToast(CharSequence text) {
        getBaseActivity().showToast(text);
    }

    public void setSuppressAnimation(boolean suppressAnimation) {
        this.suppressAnimation = suppressAnimation;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (suppressAnimation) {
            Animation animation = new AlphaAnimation(1, 0);
            if (!enter) {
                animation.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
            } else {
                animation.setDuration(0);
            }
            return animation;

        }
        return super.onCreateAnimation(transit, enter, nextAnim);

    }

    @Override
    public void attachToLifecycle(LifecycleDispatcher object) {
        objectsGroup.attachToLifecycle(object);
    }

    @Override
    public void detachFromLifecycle(LifecycleDispatcher object) {
        objectsGroup.detachFromLifecycle(object);
    }

    @Override
    public boolean isAttached(LifecycleDispatcher object) {
        return objectsGroup.isAttached(object);
    }

    private void dispatchOnResume() {
        objectsGroup.dispatchOnResume();
    }

    private void dispatchOnPause() {
        objectsGroup.dispatchOnPause(isRemoving() || getActivity().isFinishing());
        throwIfNotPaused();
    }

    private void throwIfNotPaused() {
        if (!objectsGroup.isPaused()) {
            throw new IllegalStateException("object group " + objectsGroup + " is not paused after onPause() call");
        }
    }

    public boolean isPaused() {
        return objectsGroup.isPaused();
    }

    private void dispatchOnCreate(Bundle savedInstanceState) {
        objectsGroup.dispatchOnAttach();
        objectsGroup.dispatchOnCreate(savedInstanceState);
    }

    private void dispatchOnSaveInstanceState(Bundle outState) {
        objectsGroup.dispatchOnSaveInstanceState(outState);
    }

    private void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
        objectsGroup.dispatchOnActivityResult(requestCode, resultCode, data);
    }

}
