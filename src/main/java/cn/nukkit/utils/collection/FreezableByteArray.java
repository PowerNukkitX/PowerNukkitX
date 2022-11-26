package cn.nukkit.utils.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class FreezableByteArray implements ByteArrayWrapper, AutoFreezable {
    private final FreezableArrayManager manager;
    private final AtomicReference<FreezeStatus> freezeStatus = new AtomicReference<>(FreezeStatus.NONE);
    private int temperature = 0;
    private final int rawLength;
    private byte[] data;

    FreezableByteArray(int length, @NotNull FreezableArrayManager manager) {
        this.rawLength = length;
        this.data = new byte[length];
        this.manager = manager;
    }

    FreezableByteArray(@NotNull byte[] src, @NotNull FreezableArrayManager manager) {
        this.rawLength = src.length;
        this.data = Arrays.copyOf(src, rawLength);
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
    public void warmer(int temperature) {
        this.temperature += temperature;
    }

    @Override
    public void colder(int temperature) {
        setTemperature(this.temperature - temperature);
    }

    private void setTemperature(int temperature) {
        this.temperature = Math.max(manager.getAbsoluteZero(), temperature);
    }

    @Override
    public void freeze() {
        if (temperature > manager.getFreezingPoint()) return;
        if (freezeStatus.get() != FreezeStatus.NONE) return;
        freezeStatus.set(FreezeStatus.FREEZING);
        data = LZ4Freezer.compressor.compress(data);
        freezeStatus.set(FreezeStatus.FREEZE);
    }

    @Override
    public void deepFreeze() {
        if (temperature > manager.getAbsoluteZero()) return;
        if (freezeStatus.get() != FreezeStatus.NONE || freezeStatus.get() != FreezeStatus.FREEZE) return;
        var needDecompressFirst = freezeStatus.get() == FreezeStatus.FREEZE;
        freezeStatus.set(FreezeStatus.DEEP_FREEZING);
        var tmp = needDecompressFirst ? LZ4Freezer.decompressor.decompress(data, rawLength) : data;
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
        if (temperature < manager.getMeltingHeat()) temperature = manager.getMeltingHeat();
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
        warmer(manager.getBatchOperationHeat());
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
        warmer(manager.getSingleOperationHeat());
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
        warmer(manager.getSingleOperationHeat());
        data[index] = b;
    }
}
