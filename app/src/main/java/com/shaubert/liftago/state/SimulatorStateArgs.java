package com.shaubert.liftago.state;

import com.shaubert.liftago.simulator.SimulatorState;
import com.shaubert.ui.jumper.Args;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SimulatorStateArgs extends Args {
    private SimulatorState state;
}
