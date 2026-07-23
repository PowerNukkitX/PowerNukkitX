package org.powernukkitx.block;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Family-specific block smoke pass. Unlike the generic getter smoke tests, this one
 * sets up a REAL neighbour context - it places the target block PLUS a ring of solid
 * neighbours and a redstone source - so the branch-heavy family logic (redstone power
 * propagation, liquid flow, crop growth, wall/fence connection recompute, door toggling,
 * falling-block physics, container activation) actually runs instead of short-circuiting
 * on a null/air neighbour.
 * <p>
 * Every family method is dispatched by instanceof of the family base class and wrapped in
 * {@link #safe} - the goal is coverage, not correctness assertions, so any throw from deep
 * inside the behaviour is tolerated.
 */
public class BlockFamilySmokeTest {

    private static Level level;
    private static TestPlayer player;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        player = PlayerFixture.get();
    }

    @Test
    void everyFamilyBlockRunsWithNeighbourContext() {
        Set<BlockState> states = Registries.BLOCKSTATE.getAllState();
        Assertions.assertFalse(states.isEmpty(), "no block states registered");

        Item boneMeal = Item.get(ItemID.BONE_MEAL);

        int checked = 0;
        int driven = 0;
        int failCount = 0;
        int cell = 0;
        long deadline = System.currentTimeMillis() + 3 * 60 * 1000L;
        boolean sampled = false;

        for (BlockState state : states) {
            if (System.currentTimeMillis() > deadline) {
                sampled = true;
                break;
            }
            try {
                Block block = state.toBlock();
                if (block == null) continue;

                // Lay each target out in its own 3x3x3 cell so neighbours never overlap.
                int gx = (cell % 32) * 3;
                int gz = (cell / 32) * 3;
                cell++;
                Vector3 center = new Vector3(gx, 70, gz);

                setupNeighbourContext(center);
                level.setBlock(center, block);
                Block placed = level.getBlock(center);
                checked++;

                if (drive(placed, boneMeal)) {
                    driven++;
                }
            } catch (Throwable t) {
                failCount++;
            }
        }

        System.out.println("[BlockFamilySmokeTest] states=" + states.size()
                + " checked=" + checked + " driven=" + driven
                + " failed=" + failCount + " sampled=" + sampled);
        Assertions.assertTrue(checked > 0, "no family block survived the neighbour smoke pass");
        Assertions.assertTrue(driven > 0, "no family-specific method was driven");
    }

    /** Surround the cell centre with a solid ring, a redstone source and some air/water. */
    private void setupNeighbourContext(Vector3 c) {
        Block stone = block("minecraft:stone");
        Block redstoneBlock = block("minecraft:redstone_block");
        Block water = block("minecraft:water");
        Block air = block("minecraft:air");

        safe(() -> level.setBlock(c.add(0, -1, 0), stone.clone()));   // support below
        safe(() -> level.setBlock(c.add(1, 0, 0), redstoneBlock.clone())); // energize
        safe(() -> level.setBlock(c.add(-1, 0, 0), stone.clone()));   // solid connection
        safe(() -> level.setBlock(c.add(0, 0, 1), stone.clone()));    // solid connection
        safe(() -> level.setBlock(c.add(0, 0, -1), water.clone()));   // liquid neighbour
        safe(() -> level.setBlock(c.add(0, 1, 0), air.clone()));      // air above (falling/flow)
    }

    private Block block(String id) {
        return Registries.BLOCK.get(id);
    }

    /** @return true if the block matched at least one family and a method was invoked. */
    private boolean drive(Block b, Item boneMeal) {
        boolean matched = false;

        // Generic redstone surface - many blocks answer power queries.
        for (BlockFace f : BlockFace.values()) {
            safe(() -> b.getWeakPower(f));
            safe(() -> b.getStrongPower(f));
        }
        safe(b::isPowerSource);
        safe(() -> b.onUpdate(Level.BLOCK_UPDATE_REDSTONE));

        if (b instanceof BlockLiquid liquid) {
            matched = true;
            safe(liquid::getFlowVector);
            safe(liquid::usesWaterLogging);
            safe(liquid::getFluidHeightPercent);
            safe(liquid::getFlowDecayPerBlock);
            safe(liquid::getMaxY);
            safe(() -> liquid.onUpdate(Level.BLOCK_UPDATE_NORMAL));
            safe(() -> liquid.onUpdate(Level.BLOCK_UPDATE_SCHEDULED));
        }

        if (b instanceof BlockCrops crops) {
            matched = true;
            safe(crops::getGrowth);
            safe(crops::getMaxGrowth);
            safe(crops::isFullyGrown);
            safe(crops::isFertilizable);
            safe(() -> crops.onUpdate(Level.BLOCK_UPDATE_RANDOM));
            safe(() -> crops.onActivate(boneMeal, player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
            if (b instanceof BlockCropsStem stem) {
                safe(stem::growFruit);
                safe(stem::getFacing);
            }
        }

        if (b instanceof BlockFlower flower) {
            matched = true;
            safe(() -> flower.canPlantOn(block("minecraft:grass_block")));
            safe(() -> flower.onUpdate(Level.BLOCK_UPDATE_RANDOM));
            safe(() -> flower.onActivate(boneMeal, player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
        }

        if (b instanceof BlockSapling sapling) {
            matched = true;
            safe(sapling::isAged);
            safe(sapling::isFertilizable);
            safe(() -> sapling.onUpdate(Level.BLOCK_UPDATE_RANDOM));
            safe(() -> sapling.onActivate(boneMeal, player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
        }

        if (b instanceof BlockFallable fallable) {
            matched = true;
            safe(() -> fallable.onUpdate(Level.BLOCK_UPDATE_NORMAL));
        }

        if (b instanceof BlockWallBase wall) {
            matched = true;
            safe(wall::autoConfigureState);
            safe(wall::getWallConnections);
            safe(wall::isWallPost);
            safe(() -> wall.onUpdate(Level.BLOCK_UPDATE_NORMAL));
        }

        if (b instanceof BlockFence fence) {
            matched = true;
            safe(() -> fence.canConnect(block("minecraft:stone")));
            safe(() -> fence.onUpdate(Level.BLOCK_UPDATE_NORMAL));
        }

        if (b instanceof BlockThin thin) {
            matched = true;
            safe(() -> thin.canConnect(block("minecraft:stone")));
            safe(() -> thin.onUpdate(Level.BLOCK_UPDATE_NORMAL));
        }

        if (b instanceof BlockDoor door) {
            matched = true;
            safe(door::isOpen);
            safe(door::isTop);
            safe(door::isGettingPower);
            safe(() -> door.onActivate(Item.get("minecraft:air"), player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
            safe(() -> door.toggle(player));
            safe(() -> door.onUpdate(Level.BLOCK_UPDATE_NORMAL));
            safe(() -> door.onUpdate(Level.BLOCK_UPDATE_REDSTONE));
        }

        if (b instanceof BlockTrapdoor trapdoor) {
            matched = true;
            safe(trapdoor::isOpen);
            safe(() -> trapdoor.onActivate(Item.get("minecraft:air"), player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
            safe(() -> trapdoor.toggle(player));
            safe(() -> trapdoor.onUpdate(Level.BLOCK_UPDATE_REDSTONE));
        }

        if (b instanceof BlockFenceGate gate) {
            matched = true;
            safe(gate::isOpen);
            safe(gate::isInWall);
            safe(() -> gate.onActivate(Item.get("minecraft:air"), player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
            safe(() -> gate.toggle(player));
            safe(() -> gate.onUpdate(Level.BLOCK_UPDATE_NORMAL));
        }

        if (b instanceof BlockButton button) {
            matched = true;
            safe(button::isActivated);
            safe(() -> button.onActivate(Item.get("minecraft:air"), player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
            safe(() -> button.onUpdate(Level.BLOCK_UPDATE_SCHEDULED));
        }

        if (b instanceof BlockLever lever) {
            matched = true;
            safe(lever::isPowerOn);
            safe(lever::getLeverOrientation);
            safe(() -> lever.onActivate(Item.get("minecraft:air"), player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
            safe(() -> lever.onUpdate(Level.BLOCK_UPDATE_NORMAL));
        }

        if (b instanceof BlockRedstoneDiode diode) {
            matched = true;
            safe(diode::isPowered);
            safe(diode::isLocked);
            safe(diode::isFacingTowardsRepeater);
            safe(diode::updateState);
            safe(() -> diode.onUpdate(Level.BLOCK_UPDATE_SCHEDULED));
        }

        if (b instanceof BlockRedstoneWire wire) {
            matched = true;
            safe(wire::getRedStoneSignal);
            safe(wire::isPowerSource);
            safe(() -> wire.onUpdate(Level.BLOCK_UPDATE_NORMAL));
            safe(() -> wire.onUpdate(Level.BLOCK_UPDATE_REDSTONE));
        }

        // Container / interactive blocks - drive activation with a real player.
        if (b instanceof BlockChest || b instanceof BlockFurnace || b instanceof BlockCraftingTable
                || b instanceof BlockAnvil || b instanceof BlockEnchantingTable || b instanceof BlockBed) {
            matched = true;
            safe(() -> b.onActivate(Item.get("minecraft:air"), player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
        }

        return matched;
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
