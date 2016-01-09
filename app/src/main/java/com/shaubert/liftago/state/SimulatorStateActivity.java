package com.shaubert.liftago.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.shaubert.liftago.App;
import com.shaubert.liftago.R;
import com.shaubert.liftago.simulator.SimulatorConnectionService;
import com.shaubert.liftago.simulator.SimulatorState;
import com.shaubert.liftago.simulator.StateEvent;
import com.shaubert.liftago.uiroot.BaseActivity;
import com.shaubert.ui.jumper.Args;

public class SimulatorStateActivity extends BaseActivity {

    private SimulatorStateArgs args;
    private boolean keepServiceAlive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);

        args = Args.fromArgs(getIntent());
        if (args == null) {
            args = new SimulatorStateArgs();
        }

        if (savedInstanceState == null) {
            SimulatorState state = args.getState();
            BaseSimulatorStateFragment fragment = createFragmentForState(state);
            fragment.setArguments(args.toBundle());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, fragment)
                    .commit();
        }

        SimulatorConnectionService.connect(this);
    }

    private BaseSimulatorStateFragment createFragmentForState(@Nullable SimulatorState state) {
        if (state == null) {
            if (SimulatorConnectionService.singleRemoteServiceExists(this)) {
                return new InitializingStateFragment();
            } else {
                return new BaseSimulatorStateFragment();
            }
        }

        switch (state) {
            case WAITING:
                return new WaitingStateFragment();

            case BROADCASTING:
            case RIDE:
            case UNKNOWN:
            default:
                return new BaseSimulatorStateFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.get().getBus().registerSticky(this);
    }

    public void onEvent(StateEvent stateEvent) {
        if (stateEvent.getState() != args.getState()) {
            startActivityForNewState(stateEvent.getState());
        }
    }

    private void startActivityForNewState(SimulatorState newState) {
        keepServiceAlive = true;
        getJumper().toSimulatorState()
                .withArgs(new SimulatorStateArgs(newState))
                .jump();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.get().getBus().unregister(this);

        if (isFinishing() && !keepServiceAlive) {
            SimulatorConnectionService.disconnect(this);
        }
    }

}
