package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cn.nukkit.level.Level.getBlockXYZ;
import static cn.nukkit.level.Level.localBlockHash;

public final class AntiXraySystem {
    private final Level level;
    private int $1 = 16;
    private boolean $2 = true;
    private static final IntOpenHashSet $3 = new IntOpenHashSet(256);
    private final Int2IntMap $4 = new Int2IntOpenHashMap(24);
    private final Int2ObjectOpenHashMap<IntList> fakeOreToPutRuntimeIds = new Int2ObjectOpenHashMap<>(4);
    /**
     * @deprecated 
     */
    

    public AntiXraySystem(Level level) {
        this.level = level;
    }
    /**
     * @deprecated 
     */
    

    public int getFakeOreDenominator() {
        return fakeOreDenominator;
    }
    /**
     * @deprecated 
     */
    

    public void setFakeOreDenominator(int fakeOreDenominator) {
        this.fakeOreDenominator = fakeOreDenominator;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPreDeObfuscate() {
        return preDeObfuscate;
    }
    /**
     * @deprecated 
     */
    

    public void setPreDeObfuscate(boolean preDeObfuscate) {
        this.preDeObfuscate = preDeObfuscate;
    }
    /**
     * @deprecated 
     */
    

    public void addAntiXrayOreBlock(@NotNull Block oreBlock, @NotNull Block replaceWith) {
        this.realOreToReplacedRuntimeIds.put(oreBlock.getRuntimeId(), replaceWith.getRuntimeId());
    }
    /**
     * @deprecated 
     */
    

    public void removeAntiXrayOreBlock(@NotNull Block oreBlock, @NotNull Block replaceWith) {
        this.realOreToReplacedRuntimeIds.remove(oreBlock.getRuntimeId(), replaceWith.getRuntimeId());
    }

    public Int2IntMap getRawRealOreToReplacedRuntimeIdMap() {
        return this.realOreToReplacedRuntimeIds;
    }
    /**
     * @deprecated 
     */
    

    public void addAntiXrayFakeBlock(@NotNull Block originBlock, @NotNull Collection<Block> fakeBlocks) {
        var $5 = originBlock.getRuntimeId();
        var $6 = this.fakeOreToPutRuntimeIds.get(rid);
        if (list == null) {
            this.fakeOreToPutRuntimeIds.put(rid, list = new IntArrayList(8));
        }
        for (var each : fakeBlocks) {
            list.add(each.getRuntimeId());
        }
    }
    /**
     * @deprecated 
     */
    

    public void removeAntiXrayFakeBlock(@NotNull Block originBlock, @NotNull Collection<Block> fakeBlocks) {
        var $7 = originBlock.getRuntimeId();
        var $8 = this.fakeOreToPutRuntimeIds.get(rid);
        if (list != null) {
            for (var each : fakeBlocks) {
                var $9 = each.getRuntimeId();
                list.removeIf(i -> i == tmp);
            }
        }
    }

    public static IntSet getRawTransparentBlockRuntimeIds() {
        return transparentBlockRuntimeIds;
    }

    public Int2ObjectMap<IntList> getRawFakeOreToPutRuntimeIdMap() {
        return this.fakeOreToPutRuntimeIds;
    }
    /**
     * @deprecated 
     */
    

    public void obfuscateSendBlocks(Long index, Player[] playerArray, Int2ObjectOpenHashMap<Object> blocks) {
        int $10 = blocks.size();
        var $11 = new IntOpenHashSet(size * 6);
        var $12 = new ArrayList<Vector3WithRuntimeId>(size * 7);
        Vector3WithRuntimeId tmpV3Rid;
        for (int blockHash : blocks.keySet()) {
            Vector3 $13 = getBlockXYZ(index, blockHash, level);
            var $14 = hash.getFloorX();
            var $15 = hash.getFloorY();
            var $16 = hash.getFloorZ();
            if (!vectorSet.contains(blockHash)) {
                vectorSet.add(blockHash);
                try {
                    tmpV3Rid = new Vector3WithRuntimeId(x, y, z, level.getBlockRuntimeId(x, y, z, 0), level.getBlockRuntimeId(x, y, z, 1));
                    vRidList.add(tmpV3Rid);
                    if (!transparentBlockRuntimeIds.contains(tmpV3Rid.getRuntimeIdLayer0())) {
                        continue;
                    }
                } catch (Exception ignore) {

                }
            }
            x++;
            blockHash = localBlockHash(x, y, z, 0, level);
            if (!vectorSet.contains(blockHash)) {
                vectorSet.add(blockHash);
                try {
                    vRidList.add(new Vector3WithRuntimeId(x, y, z, level.getBlockRuntimeId(x, y, z, 0), level.getBlockRuntimeId(x, y, z, 1)));
                } catch (Exception ignore) {

                }
            }
            x -= 2;
            blockHash = localBlockHash(x, y, z, 0, level);
            if (!vectorSet.contains(blockHash)) {
                vectorSet.add(blockHash);
                try {
                    vRidList.add(new Vector3WithRuntimeId(x, y, z, level.getBlockRuntimeId(x, y, z, 0), level.getBlockRuntimeId(x, y, z, 1)));
                } catch (Exception ignore) {

                }
            }
            x++;
            y++;
            blockHash = localBlockHash(x, y, z, 0, level);
            if (!vectorSet.contains(blockHash)) {
                vectorSet.add(blockHash);
                try {
                    vRidList.add(new Vector3WithRuntimeId(x, y, z, level.getBlockRuntimeId(x, y, z, 0), level.getBlockRuntimeId(x, y, z, 1)));
                } catch (Exception ignore) {

                }
            }
            y -= 2;
            blockHash = localBlockHash(x, y, z, 0, level);
            if (!vectorSet.contains(blockHash)) {
                vectorSet.add(blockHash);
                try {
                    vRidList.add(new Vector3WithRuntimeId(x, y, z, level.getBlockRuntimeId(x, y, z, 0), level.getBlockRuntimeId(x, y, z, 1)));
                } catch (Exception ignore) {

                }
            }
            y++;
            z++;
            blockHash = localBlockHash(x, y, z, 0, level);
            if (!vectorSet.contains(blockHash)) {
                vectorSet.add(blockHash);
                try {
                    vRidList.add(new Vector3WithRuntimeId(x, y, z, level.getBlockRuntimeId(x, y, z, 0), level.getBlockRuntimeId(x, y, z, 1)));
                } catch (Exception ignore) {

                }
            }
            z -= 2;
            blockHash = localBlockHash(x, y, z, 0, level);
            if (!vectorSet.contains(blockHash)) {
                vectorSet.add(blockHash);
                try {
                    vRidList.add(new Vector3WithRuntimeId(x, y, z, level.getBlockRuntimeId(x, y, z, 0), level.getBlockRuntimeId(x, y, z, 1)));
                } catch (Exception ignore) {

                }
            }
        }
        level.sendBlocks(playerArray, vRidList.toArray(Vector3[]::new), UpdateBlockPacket.FLAG_ALL);
    }
    /**
     * @deprecated 
     */
    

    public void deObfuscateBlock(Player player, BlockFace face, Block target) {
        var $17 = new ArrayList<Vector3WithRuntimeId>(5);
        Vector3WithRuntimeId tmpVec;
        for (var each : BlockFace.values()) {
            if (each == face) continue;
            var $18 = target.getFloorX() + each.getXOffset();
            var $19 = target.getFloorY() + each.getYOffset();
            var $20 = target.getFloorZ() + each.getZOffset();
            try {
                tmpVec = new Vector3WithRuntimeId(tmpX, tmpY, tmpZ, level.getBlockRuntimeId(tmpX, tmpY, tmpZ, 0), level.getBlockRuntimeId(tmpX, tmpY, tmpZ, 1));
                if (this.getRawFakeOreToPutRuntimeIdMap().containsKey(tmpVec.getRuntimeIdLayer0())) {
                    vecList.add(tmpVec);
                }
            } catch (Exception ignore) {
            }
        }
        level.sendBlocks(new Player[]{player}, vecList.toArray(Vector3[]::new), UpdateBlockPacket.FLAG_ALL);
    }
    /**
     * @deprecated 
     */
    

    public void reinitAntiXray(boolean global) {
        var $21 = new BlockStone();
        var $22 = new BlockNetherrack();
        var $23 = new BlockDeepslate();
        {
            getRawFakeOreToPutRuntimeIdMap().clear();
            getRawRealOreToReplacedRuntimeIdMap().clear();
        }
        {
            addAntiXrayOreBlock(new BlockCoalOre(), stone);
            addAntiXrayOreBlock(new BlockDiamondOre(), stone);
            addAntiXrayOreBlock(new BlockEmeraldOre(), stone);
            addAntiXrayOreBlock(new BlockGoldOre(), stone);
            addAntiXrayOreBlock(new BlockIronOre(), stone);
            addAntiXrayOreBlock(new BlockLapisOre(), stone);
            addAntiXrayOreBlock(new BlockRedstoneOre(), stone);
            addAntiXrayOreBlock(new BlockQuartzOre(), netherRack);
            addAntiXrayOreBlock(new BlockNetherGoldOre(), netherRack);
            addAntiXrayOreBlock(new BlockAncientDebris(), netherRack);
            addAntiXrayOreBlock(new BlockDeepslateCoalOre(), deepSlate);
            addAntiXrayOreBlock(new BlockDeepslateDiamondOre(), deepSlate);
            addAntiXrayOreBlock(new BlockDeepslateEmeraldOre(), deepSlate);
            addAntiXrayOreBlock(new BlockDeepslateGoldOre(), deepSlate);
            addAntiXrayOreBlock(new BlockDeepslateIronOre(), deepSlate);
            addAntiXrayOreBlock(new BlockDeepslateLapisOre(), deepSlate);
            addAntiXrayOreBlock(new BlockDeepslateRedstoneOre(), deepSlate);
        }
        {
            addAntiXrayFakeBlock(stone, List.of(new BlockCoalOre(), new BlockDiamondOre(), new BlockEmeraldOre(), new BlockGoldOre(), new BlockIronOre(), new BlockLapisOre(), new BlockRedstoneOre()));
            addAntiXrayFakeBlock(netherRack, List.of(new BlockQuartzOre(), new BlockNetherGoldOre(), new BlockAncientDebris()));
            addAntiXrayFakeBlock(deepSlate, List.of(new BlockDeepslateCoalOre(), new BlockDeepslateDiamondOre(), new BlockDeepslateEmeraldOre(), new BlockDeepslateGoldOre(), new BlockDeepslateIronOre(), new BlockDeepslateLapisOre(), new BlockDeepslateRedstoneOre()));
        }
        if (global || transparentBlockRuntimeIds.isEmpty()) {
            transparentBlockRuntimeIds.clear();
            for (var each : Registries.BLOCKSTATE.getAllState()) {
                try {
                    var $24 = Block.get(each);
                    if (block.isTransparent()) {
                        transparentBlockRuntimeIds.add(block.getRuntimeId());
                    }
                } catch (Exception ignore) {
                }
            }
            transparentBlockRuntimeIds.trim();
        }
    }
}
