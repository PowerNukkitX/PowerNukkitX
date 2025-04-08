package cn.nukkit.utils;

import io.netty.util.internal.EmptyArrays;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static cn.nukkit.entity.data.Skin.DOUBLE_SKIN_SIZE;
import static cn.nukkit.entity.data.Skin.SINGLE_SKIN_SIZE;
import static cn.nukkit.entity.data.Skin.SKIN_128_128_SIZE;
import static cn.nukkit.entity.data.Skin.SKIN_128_64_SIZE;
import static cn.nukkit.entity.data.Skin.SKIN_64_32_SIZE;

@ToString(exclude = {"data"})
@EqualsAndHashCode
@Slf4j
public class SerializedImage {
    public static final SerializedImage EMPTY = new SerializedImage(0, 0, EmptyArrays.EMPTY_BYTES);

    public final int width;
    public final int height;
    public final byte[] data;

    public SerializedImage(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public static SerializedImage fromLegacy(byte[] skinData) {
        Objects.requireNonNull(skinData, "skinData");
        return switch (skinData.length) {
            case SINGLE_SKIN_SIZE -> new SerializedImage(32, 32, skinData);
            case SKIN_64_32_SIZE -> new SerializedImage(64, 32, skinData);
            case DOUBLE_SKIN_SIZE -> new SerializedImage(64, 64, skinData);
            case SKIN_128_64_SIZE -> new SerializedImage(128, 64, skinData);
            case SKIN_128_128_SIZE -> new SerializedImage(128, 128, skinData);
            default -> throw new IllegalArgumentException("Unknown legacy skin size");
        };
    }
}
