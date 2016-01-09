package com.shaubert.liftago.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shaubert.liftago.R;
import com.shaubert.liftago.simulator.SimulatorConnectionService;
import com.shaubert.liftago.simulator.SimulatorState;
import com.shaubert.liftago.uiroot.BaseFragment;
import com.shaubert.liftago.util.Images;
import com.shaubert.ui.jumper.Args;

public class BaseSimulatorStateFragment extends BaseFragment {

    @Nullable @Bind(R.id.state_message) TextView stateMessage;
    @Nullable @Bind(R.id.frame) FrameLayout frame;

    private SimulatorStateArgs args;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        args = Args.fromArgs(getArguments());
        if (args == null) {
            args = new SimulatorStateArgs();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected int getLayoutResId() {
        return R.layout.simulator_simple_state_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimulatorState state = getArgs().getState();
        if (stateMessage != null) {
            stateMessage.setText(getStateNameResId(state));
        }
        if (frame != null) {
            frame.setBackgroundColor(getStateColor(state));
        }
    }

    public SimulatorStateArgs getArgs() {
        return args;
    }

    public int getStateNameResId(@Nullable SimulatorState state) {
        if (state == null) {
            if (SimulatorConnectionService.singleRemoteServiceExists(getContext())) {
                return R.string.simulator_state_initializing;
            } else {
                return R.string.simulator_servis_doesnt_exists;
            }
        }

        switch (state) {
            case WAITING:
                return R.string.simulator_state_waiting;
            case BROADCASTING:
                return R.string.simulator_state_broadcasting;
            case RIDE:
                return R.string.simulator_state_ride;

            case UNKNOWN:
            default:
                return R.string.simulator_state_unknown;
        }
    }

    public int getStateColor(@Nullable SimulatorState state) {
        int colorResId = R.color.simulator_state_initializing;
        if (state != null) {
            switch (state) {
                case WAITING:
                    colorResId = R.color.simulator_state_waiting;
                    break;

                case BROADCASTING:
                    colorResId = R.color.simulator_state_broadcasting;
                    break;

                case RIDE:
                    colorResId = R.color.simulator_state_ride;
                    break;

                case UNKNOWN:
                default:
                    colorResId = R.color.simulator_state_unknown;
                    break;
            }
        } else {
            if (!SimulatorConnectionService.singleRemoteServiceExists(getContext())) {
                colorResId = R.color.simulator_state_unknown;
            }
        }

        return Images.getColor(getContext(), colorResId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}