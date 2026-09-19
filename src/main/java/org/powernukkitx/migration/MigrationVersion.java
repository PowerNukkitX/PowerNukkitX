package org.powernukkitx.migration;

/**
 * Represents a comparable semantic version used to order storage migration steps.
 *
 * @param major major version component
 * @param minor minor version component
 * @param patch patch version component
 * @author Curse
 */
public record MigrationVersion(int major, int minor, int patch) implements Comparable<MigrationVersion> {
    /**
     * Initial migration version for the current migration framework.
     */
    public static final MigrationVersion V3_1_0 = new MigrationVersion(3, 1, 0);

    public MigrationVersion {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Migration version components cannot be negative");
        }
    }

    /**
     * Parses a semantic migration version.
     */
    public static MigrationVersion parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Migration version cannot be null");
        }

        String[] parts = value.split("\\.", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid migration version: " + value);
        }

        try {
            return new MigrationVersion(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid migration version: " + value, e);
        }
    }

    /**
     * Compares migration versions by major, minor and patch components.
     */
    @Override
    public int compareTo(MigrationVersion other) {
        int result = Integer.compare(this.major, other.major);
        if (result != 0) return result;

        result = Integer.compare(this.minor, other.minor);
        if (result != 0) return result;

        return Integer.compare(this.patch, other.patch);
    }

    /**
     * Returns this migration version in semantic version form.
     */
    @Override
    public String toString() {
        return this.major + "." + this.minor + "." + this.patch;
    }
}
