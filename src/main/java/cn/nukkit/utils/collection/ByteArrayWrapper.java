package cn.nukkit.utils.collection;


public interface ByteArrayWrapper {
    byte[] getRawBytes();

    void setRawBytes(byte[] bytes);

    @ShouldThaw
    byte getByte(int index);

    @ShouldThaw
    void setByte(int index, byte b);
}
