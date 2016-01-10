package com.shaubert.liftago.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import com.shaubert.liftago.R;
import com.shaubert.liftago.simulator.SimulatorConnectionService;

public class UnknownStateFragment extends BaseSimulatorStateFragment {

    @Bind(R.id.button) TextView refreshStateButton;

    @Override
    protected int getLayoutResId() {
        return R.layout.simulator_state_fragment_with_button;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshStateButton.setText(R.string.refresh_state_label);
        refreshStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimulatorConnectionService.refreshState(getContext());
            }
        });
    }

}
