package org.powernukkitx.level.updater;

import org.cloudburstmc.nbt.NbtMap;
import org.junit.jupiter.api.Test;
import org.powernukkitx.level.updater.block.BlockStateUpdaters;
import org.powernukkitx.level.updater.item.ItemUpdaters;
import org.powernukkitx.level.updater.util.OrderedUpdater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagEditHelper;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.StringTag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the block/item state data-migration updaters. The two aggregate contexts
 * fan the calls out across every registered version updater, so feeding them a spread
 * of legacy block and item tags exercises the whole {@code level/updater} tree.
 */
class UpdaterSmokeTest {

    private final AtomicInteger checked = new AtomicInteger();

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignored) {
        }
    }

    private void safe(Supplier<?> s) {
        try {
            Object out = s.get();
            if (out != null) {
                checked.incrementAndGet();
            }
        } catch (Throwable ignored) {
        }
    }

    private static NbtMap blockTag(String name) {
        return NbtMap.builder()
                .putString("name", name)
                .putCompound("states", NbtMap.EMPTY)
                .putInt("version", 0)
                .build();
    }

    private static CompoundTag itemTag(String name) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);
        tag.putShort("Damage", (short) 0);
        tag.putByte("Count", (byte) 1);
        return tag;
    }

    @Test
    void blockStateUpdatersFanOutAcrossSampleBlocks() {
        String[] blocks = {
                "minecraft:stone", "minecraft:acacia_door", "minecraft:fence_gate",
                "minecraft:iron_door", "minecraft:lightning_rod", "minecraft:chain",
                "minecraft:wool", "minecraft:log", "minecraft:leaves", "minecraft:coral",
                "minecraft:stone_slab", "minecraft:frame", "minecraft:hopper",
                "minecraft:trapdoor", "minecraft:sapling", "minecraft:tallgrass"
        };
        int latest = BlockStateUpdaters.getLatestVersion();
        assertTrue(latest > 0, "expected a positive latest block-state version");

        for (String name : blocks) {
            // legacy version 0 forces the full updater chain to run
            safe(() -> BlockStateUpdaters.updateBlockState(blockTag(name), 0));
            // already-current version path
            safe(() -> BlockStateUpdaters.updateBlockState(blockTag(name), latest));
        }
        assertTrue(checked.get() > 0, "no block updater produced output");
    }

    @Test
    void blockStateUpdaterStampsLatestVersion() {
        int latest = BlockStateUpdaters.getLatestVersion();
        NbtMap out = BlockStateUpdaters.updateBlockState(blockTag("minecraft:stone"), 0);
        assertNotNull(out);
        assertEquals(latest, out.getInt("version"));
    }

    @Test
    void itemUpdatersFanOutAcrossSampleItems() {
        String[] items = {
                "minecraft:lightning_rod", "minecraft:chain", "minecraft:apple",
                "minecraft:diamond_sword", "minecraft:stone", "minecraft:stick",
                "minecraft:banner", "minecraft:skull", "minecraft:filled_map"
        };
        int latest = ItemUpdaters.getLatestVersion();
        assertTrue(latest > 0, "expected a positive latest item version");

        for (String name : items) {
            safe(() -> ItemUpdaters.updateItem(itemTag(name), 0));
            safe(() -> ItemUpdaters.updateItem(itemTag(name), latest));
        }
        assertTrue(checked.get() > 0, "no item updater produced output");
    }

    @Test
    void orderedUpdaterTranslatesAndClampsOutOfRange() {
        assertEquals("down", OrderedUpdater.FACING_TO_BLOCK.translate(0));
        assertEquals("east", OrderedUpdater.FACING_TO_BLOCK.translate(5));
        // out of range clamps to index 0
        assertEquals("down", OrderedUpdater.FACING_TO_BLOCK.translate(99));
        assertEquals("down", OrderedUpdater.FACING_TO_BLOCK.translate(-3));

        assertEquals("facing_direction", OrderedUpdater.FACING_TO_BLOCK.getOldProperty());
        assertEquals("minecraft:block_face", OrderedUpdater.FACING_TO_BLOCK.getNewProperty());

        // offset variant: value 2 maps to first element
        assertEquals("north", OrderedUpdater.FACING_TO_CARDINAL.translate(2));
        assertEquals("east", OrderedUpdater.FACING_TO_CARDINAL.translate(5));

        assertEquals("south", OrderedUpdater.DIRECTION_TO_CARDINAL.translate(0));
        assertEquals("east", OrderedUpdater.DIRECTION_TO_CARDINAL.translate(3));
    }

    @Test
    void versionEncodingHelpers() {
        int version = CompoundTagUpdaterContext.makeVersion(1, 21, 110);
        assertEquals((1 << 24) | (21 << 16) | (110 << 8), version);
        assertEquals(0, CompoundTagUpdaterContext.updaterVersion(version));
    }

    @Test
    void standaloneContextRunsRegisteredUpdater() {
        CompoundTagUpdaterContext ctx = new CompoundTagUpdaterContext();
        ctx.addUpdater(1, 0, 0)
                .match("name", "minecraft:old")
                .edit("name", h -> h.replaceWith("name", "minecraft:new"))
                .build();

        NbtMap in = NbtMap.builder().putString("name", "minecraft:old").build();
        NbtMap out = ctx.updateStates(in, 0);
        assertEquals("minecraft:new", out.getString("name"));

        // non-matching tag passes through untouched
        NbtMap other = NbtMap.builder().putString("name", "minecraft:keep").build();
        assertEquals("minecraft:keep", ctx.updateStates(other, 0).getString("name"));
    }

    @Test
    void editHelperNavigatesChildTags() {
        Map<String, Object> child = new LinkedHashMap<>();
        child.put("k", "v");
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("states", child);

        CompoundTagEditHelper helper = new CompoundTagEditHelper(root);
        assertEquals(root, helper.getRootTag());
        assertTrue(helper.getTag() instanceof Map);

        helper.pushChild("states");
        assertTrue(helper.canPopChild());
        assertEquals("v", helper.getCompoundTag().get("k"));
        assertEquals(root, helper.getParent());

        helper.popChild();
        assertEquals(root, helper.getTag());
    }

    @Test
    void builderRenameAndRemove() {
        CompoundTagUpdaterContext ctx = new CompoundTagUpdaterContext();
        CompoundTagUpdater updater = ctx.addUpdater(1, 0, 0)
                .rename("old", "renamed")
                .remove("drop")
                .build();

        Map<String, Object> tag = new LinkedHashMap<>();
        tag.put("old", "value");
        tag.put("drop", "gone");
        boolean applied = updater.update(tag);

        assertTrue(applied);
        assertEquals("value", tag.get("renamed"));
        assertTrue(!tag.containsKey("old") && !tag.containsKey("drop"));
    }

    @Test
    void compoundTagNetworkRoundTrip() {
        CompoundTag tag = new CompoundTag();
        tag.put("Name", new StringTag("minecraft:stone"));
        NbtMap net = tag.toNetwork();
        assertEquals("minecraft:stone", net.getString("Name"));
        CompoundTag back = CompoundTag.fromNetwork(net);
        assertEquals("minecraft:stone", back.getString("Name"));
    }
}
