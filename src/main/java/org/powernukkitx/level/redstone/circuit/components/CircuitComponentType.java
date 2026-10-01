package org.powernukkitx.level.redstone.circuit.components;

/**
 * Identifies the roles that components can have in a redstone circuit.
 *
 * <p>Each role has a numeric identifier that can be used to distinguish both
 * general component categories and their specialized variants.</p>
 *
 * @author Curse
 */
public enum CircuitComponentType {
    /** An unclassified circuit component. */
    UNDEFINED(1L),
    /** A base rail signal transporter. */
    BASE_RAIL_TRANSPORTER(0x10000L),
    /** A component that consumes power. */
    CONSUMER(0x20000L),
    /** A block receiving circuit power. */
    POWERED_BLOCK(0x40000L),
    /** A component that produces power. */
    PRODUCER(0x80000L),
    /** A component that transports power. */
    TRANSPORTER(0x100000L),
    /** A component that stores or delays circuit state. */
    CAPACITOR(0x200000L),
    /** A piston-specific power consumer. */
    PISTON_CONSUMER(0x20001L),
    /** A comparator-specific capacitor. */
    COMPARATOR_CAPACITOR(0x200001L),
    /** A capacitor that emits a pulse. */
    PULSE_CAPACITOR(0x200002L),
    /** A redstone-torch-specific capacitor. */
    REDSTONE_TORCH_CAPACITOR(0x200003L),
    /** A repeater-specific capacitor. */
    REPEATER_CAPACITOR(0x200004L);

    private final long id;

    CircuitComponentType(long id) {
        this.id = id;
    }

    /**
     * Returns the numeric component type identifier.
     *
     * @return the component type identifier
     */
    public long getId() {
        return this.id;
    }
}
