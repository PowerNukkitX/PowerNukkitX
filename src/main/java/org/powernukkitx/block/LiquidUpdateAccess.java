package org.powernukkitx.block;

import org.powernukkitx.Server;
import org.powernukkitx.event.block.BlockFromToEvent;
import org.powernukkitx.event.block.LiquidFlowEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.Nullable;

/**
 * Provides block access used while processing scheduled liquid updates.
 *
 * @author Curse
 */
public interface LiquidUpdateAccess {

    Server getServer();

    Block getBlock(int x, int y, int z, int layer);

    boolean setBlock(Vector3 pos, int layer, Block block, boolean direct, boolean update);

    void scheduleUpdate(Block block, int delay);

    Item useBreakOn(Vector3 vector, @Nullable Item item);

    void addSound(Vector3 pos, Sound sound);

    /**
     * Returns whether the required block-source area is available.
     *
     * @param x center block X
     * @param z center block Z
     * @param radius block radius
     * @return whether the complete area is available
     */
    default boolean isAreaAvailable(int x, int z, int radius) {
        return true;
    }

    /**
     * Applies the runtime block-from-to event gate.
     *
     * @return resulting block, or null when cancelled
     */
    @Nullable
    default Block handleBlockFromTo(Block from, Block to) {
        BlockFromToEvent event = new BlockFromToEvent(from, to);
        getServer().getPluginManager().callEvent(event);
        return event.isCancelled() ? null : event.getTo();
    }

    /**
     * Applies the runtime liquid-flow event gate.
     */
    default boolean handleLiquidFlow(Block target, BlockLiquid source, int newFlowDecay) {
        LiquidFlowEvent event = new LiquidFlowEvent(target, source, newFlowDecay);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
}
