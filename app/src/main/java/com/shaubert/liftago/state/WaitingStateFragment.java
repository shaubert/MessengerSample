package com.shaubert.liftago.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import com.shaubert.liftago.R;
import com.shaubert.liftago.simulator.SimulatorConnectionService;

public class WaitingStateFragment extends BaseSimulatorStateFragment {

    @Bind(R.id.button) TextView startBroadcastButton;

    @Override
    protected int getLayoutResId() {
        return R.layout.simulator_state_fragment_with_button;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startBroadcastButton.setText(R.string.start_broadcast_label);
        startBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimulatorConnectionService.startBroadcast(getContext());
            }
        });
    }

}
