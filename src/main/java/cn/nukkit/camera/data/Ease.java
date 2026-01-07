package cn.nukkit.camera.data;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Represents an easing configuration for camera transitions.
 * <p>
 * The Ease record stores the duration and type of easing to be used for camera movement or animation.
 * It can be serialized to NBT format for persistence or network transmission.
 *
 * Example usage:
 * <pre>
 *     Ease ease = new Ease(1.5f, EaseType.LINEAR);
 *     CompoundTag tag = ease.serialize();
 * </pre>
 *
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */
public record Ease(float time, EaseType easeType) implements SerializableData {
    /**
     * Serializes the Ease configuration to a CompoundTag.
     * <p>
     * The tag contains the time and the type of easing as string.
     *
     * @return a CompoundTag representing this Ease configuration
     */
    @Override
    public CompoundTag serialize() {
        return new CompoundTag() // ease
                .putFloat("time", time)
                .putString("type", easeType.getType());
    }
}
