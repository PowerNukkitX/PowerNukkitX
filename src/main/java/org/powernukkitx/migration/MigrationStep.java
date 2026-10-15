package org.powernukkitx.migration;

import java.io.IOException;

/**
 * Defines one version-specific transformation for a migration format.
 *
 * @param <T> migration data type transformed by the step
 * @author Curse
 */
public interface MigrationStep<T> {
    /**
     * Returns the storage format transformed by this step.
     */
    MigrationFormat format();

    /**
     * Returns the version produced by this step.
     */
    MigrationVersion targetVersion();

    /**
     * Applies this version-specific transformation.
     */
    T migrate(MigrationContext context, T value) throws IOException;
}
