package org.powernukkitx.block;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.ItemUsePredictedResult;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.ItemUseInventoryTransaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.powernukkitx.Player;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdventureBlockPermissionsTest {
    private static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @ParameterizedTest
    @CsvSource({
            "minecraft:beetroot, minecraft:beetroot",
            "beetroot, minecraft:beetroot",
            "minecraft:grass, minecraft:grass_block",
            "minecraft:oak_sign, minecraft:standing_sign"
    })
    void canDestroyMatchesBlockIdsAndLegacyEntries(String entry, String blockId) {
        Player player = adventurePlayer();
        HumanInventory inventory = mock(HumanInventory.class);
        Item tool = Item.get(ItemID.STONE_PICKAXE);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getItemInMainHand()).thenReturn(tool);
        Vector3 position = new Vector3(88, level.getMinHeight(), 88);
        level.setBlock(position, Block.get(blockId), false, false);
        try {
            tool.setCanDestroyOn(List.of(entry));
            assertTrue(level.getBlock(position).isBlockChangeAllowed(player));
        } finally {
            level.setBlock(position, Block.get(BlockID.AIR), false, false);
        }
    }

    @ParameterizedTest
    @CsvSource({"beetroot", "minecraft:beetroot"})
    void canPlaceOnMatchesTheClickedBlock(String entry) {
        Player player = adventurePlayer();
        Vector3 target = new Vector3(88, 80, 88);
        level.setBlock(target.down(), Block.get(BlockID.FARMLAND), false, false);
        level.setBlock(target, Block.get(BlockID.BEETROOT), false, false);
        level.setBlock(target.up(), Block.get(BlockID.AIR), false, false);
        try {
            Item item = Item.get(BlockID.STONE);
            item.setCanPlaceOn(List.of(entry));

            assertNotNull(placeOn(target, item, player));
            assertEquals(BlockID.STONE, level.getBlock(target.up()).getId());
        } finally {
            level.setBlock(target.up(), Block.get(BlockID.AIR), false, false);
            level.setBlock(target, Block.get(BlockID.AIR), false, false);
            level.setBlock(target.down(), Block.get(BlockID.AIR), false, false);
        }
    }

    @Test
    void canPlaceOnRejectsMissingWrongAndMalformedEntries() {
        Player player = adventurePlayer();
        Vector3 target = new Vector3(88, 80, 88);
        level.setBlock(target, Block.get(BlockID.STONE), false, false);
        level.setBlock(target.up(), Block.get(BlockID.AIR), false, false);
        try {
            Item item = Item.get(BlockID.GLASS);
            assertNull(placeOn(target, item, player));

            item.setCanPlaceOn(List.of(BlockID.DIRT, "minecraft:unknown_block"));
            assertNull(placeOn(target, item, player));

            item.setNbt(new CompoundTag().putCompound("CanPlaceOn", new CompoundTag()));
            assertNull(placeOn(target, item, player));

            item.setNbt(new CompoundTag().putList("CanPlaceOn", new ListTag<IntTag>().add(new IntTag(1))));
            assertNull(placeOn(target, item, player));
            assertEquals(BlockID.AIR, level.getBlock(target.up()).getId());
        } finally {
            level.setBlock(target.up(), Block.get(BlockID.AIR), false, false);
            level.setBlock(target, Block.get(BlockID.AIR), false, false);
        }
    }

    @Test
    void canPlaceOnDoesNotOverrideDenyBlocks() {
        Player player = adventurePlayer();
        Vector3 target = new Vector3(88, 80, 88);
        level.setBlock(target.down(), Block.get(BlockID.DENY), false, false);
        level.setBlock(target, Block.get(BlockID.STONE), false, false);
        level.setBlock(target.up(), Block.get(BlockID.AIR), false, false);
        try {
            Item item = Item.get(BlockID.GLASS);
            item.setCanPlaceOn(List.of(BlockID.STONE));
            assertNull(placeOn(target, item, player));
            assertEquals(BlockID.AIR, level.getBlock(target.up()).getId());
        } finally {
            level.setBlock(target.up(), Block.get(BlockID.AIR), false, false);
            level.setBlock(target, Block.get(BlockID.AIR), false, false);
            level.setBlock(target.down(), Block.get(BlockID.AIR), false, false);
        }
    }

    private static Player adventurePlayer() {
        Player player = mock(Player.class);
        when(player.isAdventure()).thenReturn(true);
        when(player.getGamemode()).thenReturn(Player.ADVENTURE);
        when(player.isOp()).thenReturn(true);
        return player;
    }

    private static Item placeOn(Vector3 target, Item item, Player player) {
        ItemUseInventoryTransaction data = new ItemUseInventoryTransaction();
        data.setClickPosition(Vector3f.from(0.5f, 1, 0.5f));
        data.setClientInteractPrediction(ItemUsePredictedResult.SUCCESS);
        return level.useItemOn(target, item, BlockFace.UP, data, player, false);
    }
}
