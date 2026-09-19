package org.powernukkitx.level.vibration;


import java.util.Locale;

public enum VibrationType {
    INVALID(0, 0),
    BLOCK_ACTIVATE(1, 10),
    BLOCK_ATTACH(2, 10),
    BLOCK_CHANGE(3, 11),
    BLOCK_CLOSE(4, 9),
    BLOCK_DEACTIVATE(5, 9),
    BLOCK_DESTROY(6, 12),
    BLOCK_DETACH(7, 9),
    BLOCK_OPEN(8, 10),
    BLOCK_PLACE(9, 13),
    BOUNCE(10, 2),
    CONTAINER_CLOSE(11, 9),
    CONTAINER_OPEN(12, 10),
    DISPENSE_FAIL(13, 10),
    DRINKING(14, 8, "drink"),
    EAT(15, 8),
    ELYTRA_GLIDE(16, 4),
    ENTITY_DAMAGE(17, 7),
    ENTITY_DIE(18, 15),
    ENTITY_DISMOUNT(19, 5),
    ENTITY_INTERACT(20, 6),
    ENTITY_MOUNT(21, 6),
    STEP(22, 1, "entity_move"),
    ENTITY_PLACE(23, 14),
    ENTITY_ROAR(24, 4),
    ENTITY_SHAKING(25, 4, "entity_act"),
    EQUIP(26, 5),
    EXPLODE(27, 15),
    FLAP(28, 1),
    FLUID_PICKUP(29, 12),
    FLUID_PLACE(30, 13),
    HIT_GROUND(31, 2),
    ITEM_INTERACT_FINISH(32, 3),
    ITEM_INTERACT_START(33, 0),
    LIGHTNING_STRIKE(34, 14),
    MULTI_ITEM_SWAP(35, 6),
    NOTE_BLOCK_PLAY(36, 10),
    PISTON_CONTRACT(37, 9),
    PISTON_EXTEND(38, 10),
    PRIME_FUSE(39, 10),
    PROJECTILE_LAND(40, 2),
    PROJECTILE_SHOOT(41, 3),
    RESONATE_1(42, 1),
    RESONATE_2(43, 2),
    RESONATE_3(44, 3),
    RESONATE_4(45, 4),
    RESONATE_5(46, 5),
    RESONATE_6(47, 6),
    RESONATE_7(48, 7),
    RESONATE_8(49, 8),
    RESONATE_9(50, 9),
    RESONATE_10(51, 10),
    RESONATE_11(52, 11),
    RESONATE_12(53, 12),
    RESONATE_13(54, 13),
    RESONATE_14(55, 14),
    RESONATE_15(56, 15),
    SCULK_TOUCH(57, 1),
    SCULK_SENSOR_TENDRILS_CLICKING(58, 0),
    SHEAR(59, 6),
    SHRIEK(60, 0),
    SINGLE_ITEM_SWAP(61, 3),
    SPLASH(62, 2),
    SWIM(63, 1),
    TELEPORT(64, 14),
    UNEQUIP(65, 4),

    @Deprecated
    INSTRUMENT_PLAY(36, 10, "note_block_play");

    public final int eventId;
    public final int frequency;
    public final String identifier;

    VibrationType(int eventId, int frequency) {
        this(eventId, frequency, null);
    }

    VibrationType(int eventId, int frequency, String identifier) {
        if (frequency < 0 || frequency > 15) throw new IllegalArgumentException("frequency must be between 0 and 15");
        this.eventId = eventId;
        this.frequency = frequency;
        this.identifier = "minecraft:" + (identifier == null ? this.name().toLowerCase(Locale.ENGLISH) : identifier);
    }

    /**
     * Returns whether this event type participates in normal vibration propagation.
     *
     * @return whether this type is a vibration
     */
    public boolean isVibration() {
        return switch (this) {
            case INVALID, ITEM_INTERACT_START, SCULK_SENSOR_TENDRILS_CLICKING, SHRIEK -> false;
            default -> true;
        };
    }

    /**
     * Resolves a vibration type from its persisted event ID.
     *
     * @param eventId vibration event ID
     * @return matching vibration type, or {@link #INVALID} when unknown
     */
    public static VibrationType fromEventId(int eventId) {
        for (VibrationType type : values()) {
            if (type.eventId == eventId) return type;
        }
        return INVALID;
    }
}
