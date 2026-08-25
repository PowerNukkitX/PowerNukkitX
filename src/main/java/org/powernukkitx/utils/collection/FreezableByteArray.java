package org.powernukkitx.utils.collection;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public final class FreezableByteArray implements ByteArrayWrapper, AutoFreezable {
    private final FreezableArrayManager manager;
    private final AtomicReference<FreezeStatus> freezeStatus = new AtomicReference<>(FreezeStatus.NONE);
    private int temperature;
    private final int rawLength;
    private byte[] data;

    FreezableByteArray(int length, @NotNull FreezableArrayManager manager) {
        this.rawLength = length;
        this.data = new byte[length];
        this.manager = manager;
        this.temperature = manager.getDefaultTemperature();
    }

    FreezableByteArray(@NotNull byte[] src, @NotNull FreezableArrayManager manager) {
        this.rawLength = src.length;
        this.data = src;
        this.manager = manager;
        this.temperature = manager.getDefaultTemperature();
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
        this.temperature = Math.min(manager.getBoilingPoint(), this.temperature + temperature);
    }

    @Override
    public void colder(int temperature) {
        this.temperature = Math.max(manager.getAbsoluteZero(), this.temperature - temperature);
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
        // An already frozen array carries the same LZ4 block a deep freeze would produce, now that
        // both stages share a compressor, so promote it instead of paying a decompress plus a
        // recompress for an identical result.
        if (freezeStatus.compareAndSet(FreezeStatus.FREEZE, FreezeStatus.DEEP_FREEZE)) return;
        if (!freezeStatus.compareAndSet(FreezeStatus.NONE, FreezeStatus.DEEP_FREEZING)) return;
        data = LZ4Freezer.deepCompressor.compress(data);
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
            freezeStatus.set(FreezeStatus.NONE);
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
    public void setRawBytes(byte[] bytes) {
        while (freezeStatus.get() == FreezeStatus.THAWING || freezeStatus.get() == FreezeStatus.FREEZING || freezeStatus.get() == FreezeStatus.DEEP_FREEZING) {
            try {
                //noinspection BusyWait
                Thread.sleep(0); // Put a safe-point here
            } catch (InterruptedException ignore) {

            }
        }
        data = bytes;
        freezeStatus.set(FreezeStatus.NONE);
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
