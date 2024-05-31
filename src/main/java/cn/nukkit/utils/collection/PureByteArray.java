package cn.nukkit.utils.collection;

import org.jetbrains.annotations.NotNull;

public final class PureByteArray implements ByteArrayWrapper {
    private byte[] data;

    PureByteArray(@NotNull byte[] src) {
        this.data = src;
    }

    PureByteArray(int length) {
        this.data = new byte[length];
    }

    @Override
    public byte[] getRawBytes() {
        return data;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setRawBytes(byte[] bytes) {
        this.data = bytes;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getByte(int index) {
        return data[index];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setByte(int index, byte b) {
        data[index] = b;
    }
}
