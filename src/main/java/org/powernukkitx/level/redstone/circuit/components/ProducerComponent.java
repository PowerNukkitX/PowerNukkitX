package org.powernukkitx.level.redstone.circuit.components;

/**
 * Represents a circuit component that produces redstone power.
 *
 * <p>The produced signal strength and direction are stored in the common
 * component state.</p>
 *
 * @author Curse
 */
public class ProducerComponent extends BaseCircuitComponent {

    /**
     * Returns the producer component type.
     *
     * @return the producer component type
     */
    @Override
    public CircuitComponentType getCircuitComponentType() {
        return CircuitComponentType.PRODUCER;
    }
}
