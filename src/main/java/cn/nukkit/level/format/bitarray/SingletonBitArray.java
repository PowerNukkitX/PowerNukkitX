package cn.nukkit.level.format.bitarray;

import io.netty.buffer.ByteBuf;

/**
 * Allay Project 2023/4/14
 *
 * @author JukeboxMC | daoge_cmd
 */
public record SingletonBitArray() implements BitArray {
    private static final int[] EMPTY_ARRAY = new int[0];

    @Override
    public void set(int index, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int get(int index) {
        return 0;
    }

    @Override
    public void writeSizeToNetwork(ByteBuf buffer, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int[] words() {
        return EMPTY_ARRAY;
    }

    @Override
    public BitArrayVersion version() {
        return BitArrayVersion.V0;
    }

    @Override
    public SingletonBitArray copy() {
        return new SingletonBitArray();
    }
}
