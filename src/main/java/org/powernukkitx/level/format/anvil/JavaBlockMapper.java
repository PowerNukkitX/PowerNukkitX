package org.powernukkitx.level.format.anvil;

import org.powernukkitx.block.BlockState;

/**
 * @author iYozem
 */
public interface JavaBlockMapper {

    /**
     * @param javaId the Java numeric block id ({@code 0..4095}; the high 4 bits come from the sections {@code Add} array)
     * @param javaMeta the 4 bits block metadata ({@code 0..15})
     * @return the corresponding Bedrock block state or {@code null} if this id/meta pair is unmapped
     */
    BlockState map(int javaId, int javaMeta);

    /**
     * The state substituted whenever {@link #map(int, int)} returns {@code null} for a non-air block so an
     * unmapped block is visible in world instead of silently becoming air
     *
     * @return a placeholder state (never {@code null})
     */
    BlockState fallback();
}
