package org.powernukkitx.level.redstone.circuit.components;

public enum CircuitComponentType {
    UNDEFINED(1L),
    BASE_RAIL_TRANSPORTER(0x10000L),
    CONSUMER(0x20000L),
    POWERED_BLOCK(0x40000L),
    PRODUCER(0x80000L),
    TRANSPORTER(0x100000L),
    CAPACITOR(0x200000L),
    PISTON_CONSUMER(0x20001L),
    COMPARATOR_CAPACITOR(0x200001L),
    PULSE_CAPACITOR(0x200002L),
    REDSTONE_TORCH_CAPACITOR(0x200003L),
    REPEATER_CAPACITOR(0x200004L);

    private final long id;

    CircuitComponentType(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }
}
