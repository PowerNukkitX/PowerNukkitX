package org.powernukkitx.level;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.math.BlockFace;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

public interface ChunkObfuscator {

    /**
     * Real ore runtime id -> the runtime id it is replaced with when serialized to the client
     */
    Int2IntMap getRawRealOreToReplacedRuntimeIdMap();

    /**
     * Filler block runtime id -> list of fake ore runtime ids that may be sprinkled into it
     */
    Int2ObjectMap<IntList> getRawFakeOreToPutRuntimeIdMap();

    /**
     * Runtime ids of blocks considered transparent; a block adjacent to any of these is never
     * obfuscated so the client cannot detect the fake through an exposed face
     */
    IntSet getTransparentBlockRuntimeIds();

    /**
     * Denominator controlling how densely fake ores are inserted (higher = fewer fakes)
     */
    int getFakeOreDenominator();

    /**
     * Whether real ores should be de-obfuscated to the interacting player before they mine them
     */
    boolean isPreDeObfuscate();

    /**
     * Obfuscate a batch of block updates before they are sent to the given players
     *
     * @param level       the level the blocks belong to
     * @param index       the chunk hash of the section being flushed
     * @param players     the players to send the (obfuscated) updates to
     * @param blocks      the pending block changes, keyed by local block hash
     */
    void obfuscateSendBlocks(Level level, long index, Player[] players, Int2ObjectOpenHashMap<Object> blocks);

    /**
     * Reveal the real blocks around {@code target} to a single player when they start interacting
     * with it, so mining a real ore does not lag behind the fake data previously sent
     *
     * @param level  the level the block belongs to
     * @param player the interacting player
     * @param face   the face being interacted with (skipped when refreshing neighbours)
     * @param target the block being interacted with
     */
    void deObfuscateBlock(Level level, Player player, BlockFace face, Block target);
}
