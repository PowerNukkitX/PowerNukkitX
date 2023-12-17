package cn.nukkit.utils.collection;


public interface ArrayWrapper<T> {
    @ShouldThaw
    T[] getRawData();

    @ShouldThaw
    void setRawData(T[] data);

    @ShouldThaw
    T get(int index);

    @ShouldThaw
    void set(int index, T t);
}
