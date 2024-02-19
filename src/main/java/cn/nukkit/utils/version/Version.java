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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a version string allowing to compare.
 * <p>
 * The version must contains letters, numbers or ideograms. Other characters are considered separators.
 * If a string contains only separators than it will be considered the same as an empty string.
 * <p>
 * The versions are case insensitive but the original case sensitive version can be retrieved from {@link #toString()}.
 * @author joserobjr
 * @since 0.1.0
 */
public class Version implements Comparable<Version>, Serializable {
    private static final long serialVersionUID = 1L;
    private final @Nonnull String original;
    private transient @Nullable List<Comparable<?>> list;

    /**
     * Creates a version object from the given String. The content will not be parsed, this operation is fast.
     * @param version The version string. Case insensitive.
     * @see Version
     * @since 0.1.0
     */
    public Version(@Nonnull String version) {
        this.original = version;
    }

    /**
     * Returns the parts that was identified in the version string. It parses the version on the first call,
     * the next calls will return a cached result.
     * @return An immutable list containing a mix of {@link Integer} and {@link String} objects
     * @since 0.1.0
     */
    @SuppressWarnings("null")
    public @Nonnull List<Comparable<?>> getParts() {
        if (list == null) {
            list = parse(original);
        }
        return list;
    }

    /**
     * Compare this version with another to determine which one is newer.
     * Both version strings will be parsed if necessary and if they are not cached.
     * @param o The other version to compare
     * @throws NullPointerException If {@code o} is {@code null}
     * @throws ClassCastException If {@code o} is not an instance of {@link Version}
     * @return Negative number if this version is older than {@code o}, {@code 0} if it is the same, 
     *         or a positive number if {@code o} is newer
     * @since 0.1.0
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public int compareTo(@Nonnull Version o) {
        if (original.equalsIgnoreCase(o.original)) {
            return 0;
        }
        
        List<Comparable<?>> partsB = o.getParts();
        List<Comparable<?>> partsA = getParts();
        int sizeA = partsA.size();
        int sizeB = partsB.size();
        for (int i = 0; i < Math.max(sizeA, sizeB); i++) {
            Comparable a = (i < sizeA)? partsA.get(i) : 0;
            Comparable b = (i < sizeB)? partsB.get(i) : 0;
            if (!a.getClass().equals(b.getClass())) {
                return Integer.class.equals(a.getClass())? 1 : -1;
            }
            int cmp = a.compareTo(b);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    /**
     * Returns the original string that was used to create this instance.
     * @return The original version string.
     * @since 0.1.0
     */
    @Override
    public String toString() {
        return original;
    }

    /**
     * Compares if the other object is a {@link Version} object and it represents the same version.
     * The comparison is case insensitive.
     * Both version strings will be parsed if necessary and if they are not cached.
     * @param o The object to be compared
     * @return If the object represents the same version
     * @since 0.1.0
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version other = (Version) o;
        if (original.equalsIgnoreCase(other.original)) return true;
        return compareTo(other) == 0;
    }

    /**
     * Generates a hash code based on the processed parts.
     * The original string will be parsed if it was not parsed yet.
     * @return A hashcode compatible with the {@link #equals(Object)} and {@link #compareTo(Version)} implementations
     * @since 0.1.0
     */
    @Override
    public int hashCode() {
        return getParts().hashCode();
    }

    /**
     * Split the version into parts, separating the integers and the words from the version.
     * @param version The full version string
     * @return An immutable list containing {@link Integer} and {@link String} objects
     * @since 0.1.0
     */
    @SuppressWarnings("null")
    private static @Nonnull List<Comparable<?>> parse(@Nonnull String version) {
        ArrayList<Comparable<?>> parts = new ArrayList<>(5);
        int len = version.length();
        StringBuilder pending = new StringBuilder(len);
        byte type;
        byte previous = 0;
        for (int i = 0; i < len; i++) {
            char c = version.charAt(i);
            if (c >= '0' && c <= '9') {
                type = 1;
            } else if (Character.isDigit(c)) {
                type = 2;
            } else if (Character.isLetter(c) || Character.isIdeographic(c)) {
                type = 3;
            } else {
                type = 0;
            }
            if (type != previous) {
                addPendingPart(parts, pending, previous);
                previous = type;
            }
            switch (type) {
                case 1:
                    pending.append(c);
                    break;
                case 2:
                case 3:
                    pending.append(Character.toLowerCase(c));
                    break;
                default:
            }
        }
        addPendingPart(parts, pending, previous);
        parts.trimToSize();
        return Collections.unmodifiableList(parts);
    }

    /**
     * Adds an {@link Integer} or a {@link String} into {@code parts} built from the {@code pending} param
     * depending on the type given in the {@code type} param.
     * @param parts The list that will receive the object. Must support the {@link List#add(Object)} operation.
     * @param pending A builder containing the value of the object that will be added. The content will be cleared when used.
     * @param type One of the type bellow to indicate what is inside the {@code pending} param:
     *             <ol>
     *             <li>Contains only ASCII decimal digits</li>
     *             <li>Contains only numbers but they are not ASCII</li>
     *             <li>Contains letters and/or ideograms</li>
     *             </ol>
     * @since 0.1.0
     */
    private static void addPendingPart(@Nonnull List<Comparable<?>> parts, @Nonnull StringBuilder pending, byte type) {
        switch (type) {
            case 1:
                parts.add(Integer.parseInt(pending.toString()));
                pending.setLength(0);
                break;
            case 2:
            case 3:
                parts.add(pending.toString());
                pending.setLength(0);
                break;
            default:
        }
    }
}
