package org.powernukkitx.block.customblock.data;

import org.powernukkitx.nbt.tag.CompoundTag;

import com.google.common.base.Preconditions;

/**
 * Defines the piston movement and sticky behavior of a custom block.
 *
 * @param movementType the movement behavior of the block
 * @param sticky the sticky behavior of the block
 *
 * @author Curse
 */
public record Movable(MovementType movementType, StickyType sticky) implements NBTData {

    /**
     * The default movable behavior, allowing the block to be pushed and pulled
     * without sticking to other blocks.
     */
    public static final Movable DEFAULT = new Movable(MovementType.PUSH_PULL, StickyType.NONE);

    /**
     * Creates a movable configuration.
     *
     * @param movementType the movement behavior of the block
     * @param sticky the sticky behavior of the block
     * @throws NullPointerException if {@code movementType} or {@code sticky} is {@code null}
     * @throws IllegalArgumentException if {@link StickyType#SAME} is used with a movement type other than
     *                                  {@link MovementType#PUSH_PULL}
     */
    public Movable {
        Preconditions.checkNotNull(movementType, "movementType cannot be null");
        Preconditions.checkNotNull(sticky, "sticky cannot be null");
        Preconditions.checkArgument(
                sticky != StickyType.SAME || movementType == MovementType.PUSH_PULL,
                "sticky SAME requires movement type PUSH_PULL");
    }

    /**
     * Creates a movable configuration with no sticky behavior.
     *
     * @param movementType the movement behavior of the block
     */
    public Movable(MovementType movementType) {
        this(movementType, StickyType.NONE);
    }

    /**
     * Checks whether this configuration represents the default movable behavior.
     *
     * @return {@code true} if the block can be pushed and pulled and has no sticky behavior;
     *         {@code false} otherwise
     */
    public boolean isDefault() {
        return this.movementType == MovementType.PUSH_PULL && this.sticky == StickyType.NONE;
    }

    /**
     * Serializes this movable configuration to an NBT compound tag.
     *
     * @return the serialized movable configuration
     */
    @Override
    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag().putString("movement_type", this.movementType.serializedName);
        if (this.sticky != StickyType.NONE) {
            tag.putString("sticky", this.sticky.serializedName);
        }
        return tag;
    }

    /**
     * Creates a movable configuration from an NBT compound tag.
     *
     * @param tag the compound tag containing the movable configuration
     * @return the deserialized movable configuration
     */
    public static Movable fromCompoundTag(CompoundTag tag) {
        return new Movable(
                MovementType.fromSerializedName(tag.getString("movement_type", "push_pull")),
                StickyType.fromSerializedName(tag.getString("sticky", "none")));
    }

    /**
     * Defines how a block behaves when affected by piston movement.
     * <p>
     * The available movement types are:
     * <ul>
     *     <li>{@link #PUSH_PULL} - the block can be both pushed and pulled.</li>
     *     <li>{@link #PUSH} - the block can be pushed but cannot be pulled.</li>
     *     <li>{@link #POPPED} - the block breaks when moved.</li>
     *     <li>{@link #IMMOVABLE} - the block cannot be moved.</li>
     * </ul>
     */
    public enum MovementType {
        PUSH_PULL("push_pull"),
        PUSH("push"),
        POPPED("popped"),
        IMMOVABLE("immovable");
        private final String serializedName;
        MovementType(String serializedName) {
            this.serializedName = serializedName;
        }

        /**
         * Gets the serialized name of this movement type.
         *
         * @return the serialized name
         */
        public String getSerializedName() {
            return this.serializedName;
        }

        /**
         * Gets the movement type corresponding to the specified serialized name.
         *
         * @param value the serialized name
         * @return the matching movement type, or {@link #PUSH_PULL} if no match is found
         */
        public static MovementType fromSerializedName(String value) {
            for (MovementType type : values()) {
                if (type.serializedName.equals(value)) return type;
            }
            return PUSH_PULL;
        }
    }

    /**
     * Defines how a movable block sticks to other blocks when affected by piston movement.
     * <p>
     * The available sticky types are:
     * <ul>
     *     <li>{@link #NONE} - the block does not stick to other blocks.</li>
     *     <li>{@link #SAME} - the block sticks to compatible blocks of the same type.</li>
     * </ul>
     */
    public enum StickyType {
        NONE("none"),
        SAME("same");

        private final String serializedName;

        StickyType(String serializedName) {
            this.serializedName = serializedName;
        }

        /**
         * Gets the serialized name of this sticky type.
         *
         * @return the serialized name
         */
        public String getSerializedName() {
            return this.serializedName;
        }

        /**
         * Gets the sticky type corresponding to the specified serialized name.
         *
         * @param value the serialized name
         * @return the matching sticky type, or {@link #NONE} if no match is found
         */
        public static StickyType fromSerializedName(String value) {
            for (StickyType type : values()) {
                if (type.serializedName.equals(value)) return type;
            }
            return NONE;
        }
    }
}
