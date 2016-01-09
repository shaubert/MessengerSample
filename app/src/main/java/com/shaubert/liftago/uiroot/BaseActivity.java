package com.shaubert.liftago.uiroot;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.shaubert.liftago.App;
import com.shaubert.liftago.R;
import com.shaubert.liftago.navigation.ExecutionExtra;
import com.shaubert.liftago.navigation.ExtrasHelper;
import com.shaubert.liftago.navigation.Jumper;
import com.shaubert.liftago.util.*;
import com.shaubert.lifecycle.objects.LifecycleDelegate;
import com.shaubert.lifecycle.objects.LifecycleDispatcher;
import com.shaubert.lifecycle.objects.LifecycleObjectsGroup;
import com.shaubert.ui.jumper.JumperFactory;
import com.shaubert.ui.slidingtab.SlidingTabLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseActivity extends AppCompatActivity implements LifecycleDelegate {

    private static final String IGNORE_INTENT = "__ignore_intent";

    private LifecycleObjectsGroup objectsGroup = new LifecycleObjectsGroup();

    private boolean suppressToolbar;
    private boolean hideUpButton;
    private boolean resumed;
    private boolean ignoreIntent;

    private Toast toast;

    private boolean showHomeAsUp;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private SlidingTabLayout tabLayout;

    private ExecutionExtra executionExtra;

    private Jumper jumper;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        markIntentAsIgnoredIfNeeded(savedInstanceState);
        super.onCreate(savedInstanceState);

        attachToLifecycle(getJumper());
        resolveSuppressToolbarFromTheme();

        if (Versions.hasLollipopApi()) {
            int color = Images.getColor(this, R.color.primary_800);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            String label = getResources().getString(R.string.app_name);
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(label, bm, color);
            setTaskDescription(td);
        }

        Intent intent = getIntent();
        if (savedInstanceState == null) {
            if (!ignoreIntent && intent != null && intent.hasExtra(ExtrasHelper.INTENT_EXTRA_EXECUTION)) {
                executionExtra = intent.getParcelableExtra(ExtrasHelper.INTENT_EXTRA_EXECUTION);
            }
        } else {
            executionExtra = savedInstanceState.getParcelable(ExtrasHelper.INTENT_EXTRA_EXECUTION);
        }
    }

    protected void markIntentAsIgnoredIfNeeded(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (savedInstanceState == null) {
            ignoreIntent = NavUtils.isLaunchedFromHistory(intent);
        } else {
            ignoreIntent = savedInstanceState.getBoolean(IGNORE_INTENT, false);
        }
    }

    public boolean isIgnoreIntent() {
        return ignoreIntent;
    }

    public void showToast(int resId) {
        showToast(getText(resId));
    }

    public void showToast(int resId, Object ... args) {
        showToast(getString(resId, args));
    }

    public void showToast(CharSequence text) {
        if (toast != null) {
            toast.cancel();
        }

        if (TextUtils.isEmpty(text)) {
            return;
        }

        toast = Toast.makeText(this, text, text.length() > 50 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        dispatchOnCreate(savedInstanceState);
        EdgeEffect.brandGlowEffectFromThemeIfNeeded(this);

        if (executionExtra != null) {
            executionExtra.execute(this);
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        getJumper().handleIntent(intent);

        if (intent != null && intent.hasExtra(ExtrasHelper.INTENT_EXTRA_EXECUTION)) {
            executionExtra = intent.getParcelableExtra(ExtrasHelper.INTENT_EXTRA_EXECUTION);
            if (executionExtra != null) {
                executionExtra.execute(this);
                if (resumed) {
                    executionExtra.onResume();
                }
            }
        }

        iterateFragments(new Func<Fragment, Boolean>() {
            @Override
            public Boolean perform(Fragment fragment) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onNewIntent(intent);
                }
                return false;
            }
        });
    }

    private void iterateFragments(Func<Fragment, Boolean> func) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList == null) return;

        Queue<Fragment> fragments = new LinkedList<>(fragmentList);
        while (true) {
            if (fragments.isEmpty()) return;

            Fragment fragment = fragments.poll();
            if (fragment == null) continue;

            if (func.perform(fragment)) {
                break;
            }

            List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments();
            if (childFragments != null) {
                fragments.addAll(childFragments);
            }
        }
    }

    public Jumper getJumper() {
        if (jumper == null) {
            JumperFactory<Jumper> jumperFactory = App.get().getJumperFactory();
            jumper = jumperFactory.createFor(this);
        }
        return jumper;
    }

    public void suppressUpNavigation() {
        this.hideUpButton = true;
    }

    public void suppressToolbar() {
        this.suppressToolbar = true;
    }

    public void showUpNavigation() {
        showHomeAsUp = true;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public SlidingTabLayout getTabsLayout() {
        return tabLayout;
    }

    private void resolveSuppressToolbarFromTheme() {
        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[]{R.attr.suppressToolbar});
        suppressToolbar = typedArray.getBoolean(0, suppressToolbar);
        typedArray.recycle();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupToolbar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupToolbar();
    }

    @SuppressLint("NewApi")
    protected void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.ab_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        if (toolbar != null) {
            if (suppressToolbar) {
                toolbar.setVisibility(View.GONE);
            } else {
                setSupportActionBar(toolbar);
            }
        }
        tabLayout = (SlidingTabLayout) findViewById(R.id.toolbar_tabs);
        if (tabLayout != null && appBarLayout == null) {
            if (Versions.hasLollipopApi() && tabLayout.getElevation() < 1) {
                int elevation = Sizes.dpToPx(this, 4);
                tabLayout.setElevation(elevation);
            }
        }
    }

    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (hideUpButton) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dispatchOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        dispatchOnResume();

        if (executionExtra != null) {
            executionExtra.onResume();
        }
    }

    @Override
    public final void onBackPressed() {
        if (!handleOnBackPressed()) {
            super.onBackPressed();
        }
    }

    protected boolean handleOnBackPressed() {
        final AtomicBoolean handled = new AtomicBoolean(false);
        iterateFragments(new Func<Fragment, Boolean>() {
            @Override
            public Boolean perform(Fragment fragment) {
                if (fragment instanceof BaseFragment) {
                    if (((BaseFragment) fragment).onBackPressed()) {
                        handled.set(true);
                        return true;
                    }
                }
                return false;
            }
        });

        return handled.get();
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumed = false;
        dispatchOnPause();

        if (executionExtra != null) {
            executionExtra.onPause();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Context context = this;
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            context = supportActionBar.getThemedContext();
        }
        MenuIconsColorizer.colorize(menu, context);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                boolean superResult = super.onOptionsItemSelected(item);
                if (!superResult && showHomeAsUp) {
                    Keyboard.hideKeyboard(this);
                    onBackPressed();
                }
                return superResult;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dispatchOnSaveInstanceState(outState);
        outState.putBoolean(IGNORE_INTENT, ignoreIntent);
        if (executionExtra != null && !executionExtra.isFinished()) {
            outState.putParcelable(ExtrasHelper.INTENT_EXTRA_EXECUTION, executionExtra);
        }
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
        objectsGroup.dispatchOnPause(isFinishing());
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
