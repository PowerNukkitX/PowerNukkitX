/*
 Copyright (C) 2020  powernukkit.org - José Roberto de Araújo Júnior
 
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package cn.nukkit.utils.version;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A comparator that compares two strings as two {@link Version} objects but caching the object
 * to avoid parsing the same string multiple times. The cache size is not limited but the cached objects will
 * referenced weakly and will be collected by the garbage collector when not used.
 * <p><strong>This implementation is thread safe</strong> because the cache is stored in a {@link ConcurrentHashMap}.
 * @author joserobjr
 * @since 0.1.0
 */
public class ConcurrentCachedVersionStringComparator extends VersionStringComparator {
    /**
     * The map that holds all cached instances. It may contains values that got removed by the garbage collector.
     * @since 0.1.0
     */
    private final ConcurrentHashMap<String, WeakReference<Version>> cache = new ConcurrentHashMap<>();

    /**
     * The number of comparisons that was done since the last cleanup.
     * @since 0.1.0
     */
    private final AtomicInteger comparisons = new AtomicInteger();

    /**
     * How frequent the comparator will remove the objects that got cleared by the garbage collector from the cache,
     * {@code 0} makes it clear in all calls and negative disables the automatic cleanup.
     * @since 0.1.0
     */
    private final int gcFrequency;
    
    /**
     * Creates a comparator with an empty cache that will have a population limited by the given size.
     * @param gcFrequency How frequent the comparator will remove the objects that got cleared by the garbage collector
     *                   from the cache. {@code 0} makes it clear in all calls and negative disables the automatic cleanup.
     * @since 0.1.0
     */
    public ConcurrentCachedVersionStringComparator(int gcFrequency) {
        this.gcFrequency = gcFrequency;
    }

    @Override
    public int compare(@Nonnull String o1, @Nonnull String o2) {
        if (gcFrequency >= 0 && comparisons.getAndIncrement() == gcFrequency) {
            removeGarbageCollected();
        }
        return getVersion(o1).compareTo(getVersion(o2));
    }

    /**
     * Remove all cached objects that was already cleared by garbage collector.
     */
    public void removeGarbageCollected() {
        cache.values().removeIf(ref-> ref.get() == null);
        comparisons.set(0);
    }

    /**
     * Gets or create the cached {@link Version} object for the given version string
     * @param versionString The version string
     * @return A cached {@link Version} object
     */
    private @Nonnull Version getVersion(@Nonnull String versionString) {
        Version version = null;
        WeakReference<Version> reference = cache.get(versionString);
        if (reference != null) {
            version = reference.get();
        }
        if (version == null) {
            version = new Version(versionString);
            cache.put(versionString, new WeakReference<>(version));
        }
        return version;
    }
    
    /**
     * Remove all the cached objects.
     * @since 0.1.0
     */
    public void clearCache() {
        cache.clear();
    }
}
