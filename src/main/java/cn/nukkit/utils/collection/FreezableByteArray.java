package cn.nukkit.utils.collection;

import java.util.concurrent.atomic.AtomicReference;

public final class FreezableByteArray implements ByteArrayWrapper, AutoFreezable {
    private final FreezableArrayManager manager;
    private final AtomicReference<FreezeStatus> freezeStatus = new AtomicReference<>(FreezeStatus.NONE);
    private int temperature = 0;
    private final int rawLength;
    private byte[] data;

    public FreezableByteArray(int length, FreezableArrayManager manager) {
        this.rawLength = length;
        this.data = new byte[length];
        this.manager = manager;
    }

    public FreezableArrayManager getManager() {
        return manager;
    }

    @Override
    public FreezeStatus getFreezeStatus() {
        return freezeStatus.get();
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public void freeze() {
        if (freezeStatus.get() != FreezeStatus.NONE) return;
        freezeStatus.set(FreezeStatus.FREEZING);
        data = LZ4Freezer.compressor.compress(data);
        freezeStatus.set(FreezeStatus.FREEZE);
    }

    @Override
    public void deepFreeze() {
        if (freezeStatus.get() != FreezeStatus.NONE || freezeStatus.get() != FreezeStatus.FREEZE) return;
        freezeStatus.set(FreezeStatus.DEEP_FREEZING);
        var tmp = LZ4Freezer.decompressor.decompress(data, rawLength);
        data = LZ4Freezer.deepCompressor.compress(tmp);
        freezeStatus.set(FreezeStatus.DEEP_FREEZE);
    }

    @Override
    public void thaw() {
        while (freezeStatus.get() == FreezeStatus.THAWING || freezeStatus.get() == FreezeStatus.FREEZING || freezeStatus.get() == FreezeStatus.DEEP_FREEZING) {
            try {
                //noinspection BusyWait
                Thread.sleep(0); // Put a safe-point here
            } catch (InterruptedException ignore) {

            }
        }
        if (freezeStatus.get() == FreezeStatus.FREEZE || freezeStatus.get() == FreezeStatus.DEEP_FREEZE) {
            data = LZ4Freezer.decompressor.decompress(data, rawLength);
        }
    }

    @Override
    public byte[] getRawBytes() {
        while (freezeStatus.get() == FreezeStatus.THAWING || freezeStatus.get() == FreezeStatus.FREEZING || freezeStatus.get() == FreezeStatus.DEEP_FREEZING) {
            try {
                //noinspection BusyWait
                Thread.sleep(0); // Put a safe-point here
            } catch (InterruptedException ignore) {

            }
        }
        if (freezeStatus.get() != FreezeStatus.NONE) {
            thaw();
        }
        return data;
    }

    @Override
    public byte getByte(int index) {
        while (freezeStatus.get() == FreezeStatus.THAWING || freezeStatus.get() == FreezeStatus.FREEZING || freezeStatus.get() == FreezeStatus.DEEP_FREEZING) {
            try {
                //noinspection BusyWait
                Thread.sleep(0); // Put a safe-point here
            } catch (InterruptedException ignore) {

            }
        }
        if (freezeStatus.get() != FreezeStatus.NONE) {
            thaw();
        }
        return data[index];
    }

    @Override
    public void setByte(int index, byte b) {
        while (freezeStatus.get() == FreezeStatus.THAWING || freezeStatus.get() == FreezeStatus.FREEZING || freezeStatus.get() == FreezeStatus.DEEP_FREEZING) {
            try {
                //noinspection BusyWait
                Thread.sleep(0); // Put a safe-point here
            } catch (InterruptedException ignore) {

            }
        }
        if (freezeStatus.get() != FreezeStatus.NONE) {
            thaw();
        }
        data[index] = b;
    }
}
