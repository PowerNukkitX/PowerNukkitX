package cn.nukkit.level.format.bitarray;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.common.util.VarInts;

/**
 * Allay Project 2023/4/14
 *
 * @author JukeboxMC | daoge_cmd
 */
public interface BitArray {

    void set(int index, int value);

    int get(int index);

    default void writeSizeToNetwork(ByteBuf buffer, int size) {
        VarInts.writeInt(buffer, size);
    }

    default int readSizeFromNetwork(ByteBuf buffer) {
        return VarInts.readInt(buffer);
    }

    int size();

    int[] words();

    BitArrayVersion version();

    BitArray copy();

}