package org.powernukkitx.level.redstone.circuit.components;

/**
 * Represents a powered block tracked by the circuit system.
 *
 * <p>Powered blocks use the common component state to retain their current
 * signal strength and power-consumption direction behavior.</p>
 *
 * @author Curse
 */
public class PoweredBlockComponent extends BaseCircuitComponent {

    /**
     * Returns the powered-block component type.
     *
     * @return the powered-block component type
     */
    @Override
    public CircuitComponentType getCircuitComponentType() {
        return CircuitComponentType.POWERED_BLOCK;
    }
}
