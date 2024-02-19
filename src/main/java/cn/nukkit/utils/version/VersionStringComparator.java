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
import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * A comparator that compares two strings as two {@link cn.nukkit.utils.version.Version} objects.
 * @author joserobjr
 * @since 0.1.0
 */
public class VersionStringComparator implements Comparator<String> {
    /**
     * A common instance that will be created when necessary.
     * @since 0.1.0
     */
    private static @Nullable VersionStringComparator instance;

    /**
     * Compare two strings as two {@link cn.nukkit.utils.version.Version} objects.
     * @param o1 The first version string
     * @param o2 The second version string
     * @return {@code -1}, {@code 0}, or {@code 1} if the first is older, equals, or newer than the second version
     * @since 0.1.0
     */
    @Override
    public int compare(@Nonnull String o1, @Nonnull String o2) {
        return new Version(o1).compareTo(new Version(o2));
    }

    /**
     * A common instance of a simple version comparator
     * @return The common instance, creates it on first call 
     * @since 0.1.0
     */
    @SuppressWarnings("null")
    public static @Nonnull VersionStringComparator getInstance() {
        if (instance == null) {
            instance = new VersionStringComparator();
        }
        return instance;
    }
}
