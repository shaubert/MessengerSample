package com.shaubert.liftago.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.Bind;
import com.shaubert.liftago.R;
import com.shaubert.liftago.simulator.SimulatorConnectionService;

public class WaitingStateFragment extends BaseSimulatorStateFragment {

    @Bind(R.id.button) View startBroadcastButton;

    @Override
    protected int getLayoutResId() {
        return R.layout.simulator_waiting_state_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimulatorConnectionService.startBroadcast(getContext());
            }
        });
    }

}
