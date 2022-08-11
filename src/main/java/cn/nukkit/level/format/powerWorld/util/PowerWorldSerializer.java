package cn.nukkit.level.format.powerWorld.util;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class PowerWorldSerializer {
    private PowerWorldSerializer() {

    }

    /**
     * 反序列化地图版本
     *
     * @param buffer 二进制数据
     * @return 地图版本
     */
    public static int deserializeWorldVersion(@NotNull ByteBuffer buffer) {
        return buffer.getInt();
    }

    /**
     * 序列化地图版本
     *
     * @param buffer  二进制数据
     * @param version 地图版本
     */
    public static void serializeWorldVersion(@NotNull ByteBuffer buffer, int version) {
        buffer.putInt(version);
    }

    /**
     * 序列化地图版本
     *
     * @param version 地图版本
     * @return 二进制数据
     */
    @NotNull
    public static ByteBuffer serializeWorldVersion(int version) {
        var buffer = ByteBuffer.allocateDirect(4);
        serializeWorldVersion(buffer, version);
        return buffer;
    }

    @NotNull
    public static CompoundTag deserializeNBT(@NotNull ByteBuffer buffer) {
        try (var input = new ByteBufferBackedInputStream(buffer)) {
            return NBTIO.readCompressed(new ByteBufferBackedInputStream(buffer));
        } catch (IOException e) {
            e.printStackTrace();
            return new CompoundTag();
        }
    }

    @NotNull
    public static ByteBuffer serializeNBT(@NotNull CompoundTag nbt) {
        try {
            var bytes = NBTIO.writeGZIPCompressed(nbt);
            var buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return ByteBuffer.allocateDirect(0);
        }
    }

    public static void serializeNBT(@NotNull ByteBuffer buffer, @NotNull CompoundTag tag) {
        try (var output = new ByteBufferBackedOutputStream(buffer)) {
            NBTIO.writeGZIPCompressed(tag, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
