package org.powernukkitx.utils.collection;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public final class FreezableByteArray implements ByteArrayWrapper, AutoFreezable {
    private static final long TEMPERATURE_MASK = 0xFFFFFFFFL;

    private final FreezableArrayManager manager;
    private final AtomicReference<FreezeStatus> freezeStatus = new AtomicReference<>(FreezeStatus.NONE);
    private volatile long thermalState;
    private final int rawLength;
    private byte[] data;

    FreezableByteArray(int length, @NotNull FreezableArrayManager manager) {
        this.rawLength = length;
        this.data = new byte[length];
        this.manager = manager;
        this.thermalState = pack(manager.getCoolingCycle(), manager.getDefaultTemperature());
    }

    FreezableByteArray(@NotNull byte[] src, @NotNull FreezableArrayManager manager) {
        this.rawLength = src.length;
        this.data = src;
        this.manager = manager;
        this.thermalState = pack(manager.getCoolingCycle(), manager.getDefaultTemperature());
    }

    private static long pack(int cycle, int temperature) {
        return ((long) cycle << 32) | (temperature & TEMPERATURE_MASK);
    }

    private void setTemperature(int temperature) {
        this.thermalState = pack(manager.getCoolingCycle(), temperature);
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
        long state = this.thermalState;
        int base = (int) state;
        long elapsed = (long) manager.getCoolingCycle() - (int) (state >>> 32);
        if (elapsed <= 0) return base;
        return (int) Math.max(manager.getAbsoluteZero(), base - elapsed);
    }

    @Override
    public void warmer(int temperature) {
        int current = getTemperature();
        int boilingPoint = manager.getBoilingPoint();
        if (current >= boilingPoint) return;
        setTemperature(Math.min(boilingPoint, current + temperature));
    }

    @Override
    public void colder(int temperature) {
        // Guarded so an array already at absolute zero does not take a volatile write. The freeze
        // cycle cools every tracked array on every pass, and on a server with many worlds most of
        // them are permanently at the floor - writing the same value back dirtied a cache line for
        // each one, on every pass, across every compute thread.
        int current = getTemperature();
        int absoluteZero = manager.getAbsoluteZero();
        if (current <= absoluteZero) return;
        setTemperature(Math.max(absoluteZero, current - temperature));
    }

    @Override
    public synchronized void freeze() {
        if (getTemperature() > manager.getFreezingPoint()) return;
        if (!freezeStatus.compareAndSet(FreezeStatus.NONE, FreezeStatus.FREEZING)) return;
        try {
            data = LZ4Freezer.compressor.compress(data.clone());
            freezeStatus.set(FreezeStatus.FREEZE);
        } catch (RuntimeException | Error e) {
            freezeStatus.set(FreezeStatus.NONE);
            throw e;
        }
    }

    @Override
    public synchronized void deepFreeze() {
        if (getTemperature() > manager.getAbsoluteZero()) return;
        // Both frozen states use the same compression format.
        if (freezeStatus.compareAndSet(FreezeStatus.FREEZE, FreezeStatus.DEEP_FREEZE)) return;
        if (!freezeStatus.compareAndSet(FreezeStatus.NONE, FreezeStatus.DEEP_FREEZING)) return;
        try {
            data = LZ4Freezer.deepCompressor.compress(data.clone());
            freezeStatus.set(FreezeStatus.DEEP_FREEZE);
        } catch (RuntimeException | Error e) {
            freezeStatus.set(FreezeStatus.NONE);
            throw e;
        }
    }

    @Override
    public synchronized void thaw() {
        FreezeStatus status = freezeStatus.get();
        if (status == FreezeStatus.FREEZE || status == FreezeStatus.DEEP_FREEZE) {
            freezeStatus.set(FreezeStatus.THAWING);
            try {
                data = LZ4Freezer.decompressor.decompress(data, rawLength);
                freezeStatus.set(FreezeStatus.NONE);
            } catch (RuntimeException | Error e) {
                freezeStatus.set(status);
                throw e;
            }
        }
        if (getTemperature() < manager.getMeltingHeat()) setTemperature(manager.getMeltingHeat());
    }

    @Override
    public synchronized byte[] getRawBytes() {
        if (freezeStatus.get() != FreezeStatus.NONE) thaw();
        warmer(manager.getBatchOperationHeat());
        return data;
    }

    @Override
    public synchronized void setRawBytes(byte[] bytes) {
        data = bytes;
        freezeStatus.set(FreezeStatus.NONE);
    }

    @Override
    public synchronized byte getByte(int index) {
        if (freezeStatus.get() != FreezeStatus.NONE) thaw();
        warmer(manager.getSingleOperationHeat());
        return data[index];
    }

    @Override
    public synchronized void setByte(int index, byte b) {
        if (freezeStatus.get() != FreezeStatus.NONE) thaw();
        warmer(manager.getSingleOperationHeat());
        data[index] = b;
    }
}
