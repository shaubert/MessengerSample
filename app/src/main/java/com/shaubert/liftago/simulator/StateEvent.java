package com.shaubert.liftago.simulator;

import android.support.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateEvent {
    private @Nullable SimulatorState state;

    public boolean isConnected() {
        return state != null;
    }
}
