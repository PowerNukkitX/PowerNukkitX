package org.powernukkitx.utils.collection;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/** Weakly-held freezable arrays scanned in bounded, rotating slices. */
final class FreezableBucket {

    private static final int INITIAL_CAPACITY = 16;

    private WeakReference<AutoFreezable>[] entries;
    private int size;
    private int cursor;

    @SuppressWarnings("unchecked")
    FreezableBucket() {
        this.entries = new WeakReference[INITIAL_CAPACITY];
    }

    public synchronized void add(@NotNull AutoFreezable value) {
        if (this.size == this.entries.length) {
            this.entries = Arrays.copyOf(this.entries, this.size << 1);
        }
        this.entries[this.size++] = new WeakReference<>(value);
    }

    public synchronized int scan(int maxScan, @NotNull Predicate<AutoFreezable> candidate,
                                 @NotNull Collection<AutoFreezable> out) {
        if (maxScan <= 0 || this.size == 0) return 0;

        int scanned = 0;
        int limit = Math.min(maxScan, this.size);
        while (scanned < limit && this.size > 0) {
            if (this.cursor >= this.size) this.cursor = 0;

            AutoFreezable value = this.entries[this.cursor].get();
            if (value == null) {
                this.entries[this.cursor] = this.entries[--this.size];
                this.entries[this.size] = null;
            } else {
                if (candidate.test(value)) out.add(value);
                this.cursor++;
            }
            scanned++;
        }

        shrinkIfSparse();
        return scanned;
    }

    private void shrinkIfSparse() {
        int capacity = this.entries.length;
        if (capacity <= INITIAL_CAPACITY || this.size > capacity >> 2) return;
        this.entries = Arrays.copyOf(this.entries, Math.max(INITIAL_CAPACITY, this.size << 1));
    }
}
