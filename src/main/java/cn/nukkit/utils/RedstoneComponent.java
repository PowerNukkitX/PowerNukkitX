package cn.nukkit.utils;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Interface, all redstone components implement, containing redstone related methods.
 */

public interface RedstoneComponent {
    /**
     * Send a redstone update to all blocks around this block.
     *
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAroundRedstone(@Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        this.updateAroundRedstone(Sets.newHashSet(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around this block.
     *
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAroundRedstone(@NotNull Set<BlockFace> ignoredFaces) {
        if (this instanceof Position) updateAroundRedstone((Position) this, ignoredFaces);
    }

    /**
     * Send a redstone update to all blocks around the given position.
     *
     * @param pos          The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAroundRedstone(@NotNull Position pos, @Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        updateAroundRedstone(pos, Sets.newHashSet(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around the given position.
     *
     * @param pos          The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAroundRedstone(@NotNull Position pos, @NotNull Set<BlockFace> ignoredFaces) {
        for (BlockFace face : BlockFace.values()) {
            if (ignoredFaces.contains(face)) continue;
            pos.getLevelBlock().getSide(face).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
        }
    }

    /**
     * Send a redstone update to all blocks around this block.
     *
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAllAroundRedstone(@Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        this.updateAllAroundRedstone(Sets.newHashSet(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around this block and also around the blocks of those updated blocks.
     *
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAllAroundRedstone(@NotNull Set<BlockFace> ignoredFaces) {
        if (this instanceof Position pos) updateAllAroundRedstone(pos, ignoredFaces);
    }

    /**
     * Send a redstone update to all blocks around the given position and also around the blocks of those updated blocks.
     *
     * @param pos          The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAllAroundRedstone(@NotNull Position pos, @Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        updateAllAroundRedstone(pos, Sets.newHashSet(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around the given position and also around the blocks of those updated blocks.
     *
     * @param pos          The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAllAroundRedstone(@NotNull Position pos, @NotNull Set<BlockFace> ignoredFaces) {
        updateAroundRedstone(pos, ignoredFaces);

        for (BlockFace face : BlockFace.values()) {
            if (ignoredFaces.contains(face)) continue;

            updateAroundRedstone(pos.getSide(face), face.getOpposite());
        }
    }
}
