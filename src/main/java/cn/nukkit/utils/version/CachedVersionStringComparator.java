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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A comparator that compares two strings as two {@link Version} objects but caching the object
 * to avoid parsing the same string multiple times. The cache have a limited size and have
 * strong reference to the version objects.
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a this comparator concurrently it <em>must</em> be
 * synchronized externally.  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the comparator or the
 * object that is using it.
 * @author joserobjr
 * @since 0.1.0
 */
public class CachedVersionStringComparator extends VersionStringComparator {
    /**
     * The map that holds the cached objects, the implementation must remove the old entries when it reaches
     * the given {@code cacheSize}.
     * @since 0.1.0
     */
    private final Map<String, Version> cache;

    /**
     * Creates a comparator with an empty cache that will have a population limited by the given size.
     * @param cacheSize The max size of the cache. When the size is reached, older entries are removed.
     * @since 0.1.0
     */
    public CachedVersionStringComparator(final int cacheSize) {
        cache = new LinkedHashMap<String, Version>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Version> eldest) {
                return size() > cacheSize;
            }
        };
    }

    @Override
    public int compare(@Nonnull String o1, @Nonnull String o2) {
        return cache.computeIfAbsent(o1, Version::new).compareTo(cache.computeIfAbsent(o2, Version::new));
    }

    /**
     * Remove all the cached objects.
     * @since 0.1.0
     */
    public void clearCache() {
        cache.clear();
    }
}
