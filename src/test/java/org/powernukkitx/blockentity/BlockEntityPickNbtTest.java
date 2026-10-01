package org.powernukkitx.blockentity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.ItemHelper;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class BlockEntityPickNbtTest {

    private static final AtomicInteger NEXT_X = new AtomicInteger(3000);

    private static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    private static CompoundTag defaultCompound(String id) {
        return BlockEntity.getDefaultCompound(new Position(NEXT_X.getAndIncrement(), 80, 300, level), id);
    }

    private static IChunk chunkOf(CompoundTag nbt) {
        int chunkX = nbt.getInt("x") >> 4;
        int chunkZ = nbt.getInt("z") >> 4;
        level.loadChunk(chunkX, chunkZ);
        return level.getChunk(chunkX, chunkZ);
    }

    private static Player player(boolean op) {
        Player player = mock(Player.class);
        doReturn(op).when(player).isOp();
        return player;
    }

    @Test
    void chestItemsAreNotCopied() {
        CompoundTag nbt = defaultCompound(BlockEntityID.CHEST);
        nbt.putList("Items", new ListTag<CompoundTag>().add(ItemHelper.write(Item.get("minecraft:diamond"), 0)));
        BlockEntityChest chest = new BlockEntityChest(chunkOf(nbt), nbt);

        assertNull(chest.getPickNBT(player(true)));
        chest.close();
    }

    @Test
    void jukeboxRecordIsNotCopied() {
        CompoundTag nbt = defaultCompound(BlockEntityID.JUKEBOX);
        nbt.putCompound("RecordItem", ItemHelper.write(Item.get("minecraft:music_disc_cat")));
        BlockEntityJukebox jukebox = new BlockEntityJukebox(chunkOf(nbt), nbt);

        assertNull(jukebox.getPickNBT(player(true)));
        jukebox.close();
    }

    @Test
    void campfireFoodIsNotCopied() {
        CompoundTag nbt = defaultCompound(BlockEntityID.CAMPFIRE);
        nbt.putCompound("Item1", ItemHelper.write(Item.get("minecraft:beef")));
        nbt.putInt("ItemTime1", 100);
        BlockEntityCampfire campfire = new BlockEntityCampfire(chunkOf(nbt), nbt);

        assertNull(campfire.getPickNBT(player(true)));
        campfire.close();
    }

    @Test
    void signDataIsCopied() {
        CompoundTag nbt = defaultCompound(BlockEntityID.SIGN);
        BlockEntitySign sign = new BlockEntitySign(chunkOf(nbt), nbt);

        CompoundTag picked = sign.getPickNBT(player(false));

        assertNotNull(picked);
        assertEquals(sign.getCleanedNBT(), picked);
        sign.close();
    }

    @Test
    void commandBlockDataIsCopiedForOperatorsOnly() {
        CompoundTag nbt = defaultCompound(BlockEntityID.COMMAND_BLOCK);
        nbt.putString(ICommandBlock.TAG_COMMAND, "say hello");
        BlockEntityCommandBlock commandBlock = new BlockEntityCommandBlock(chunkOf(nbt), nbt);

        assertNull(commandBlock.getPickNBT(player(false)));
        CompoundTag picked = commandBlock.getPickNBT(player(true));
        assertNotNull(picked);
        assertEquals("say hello", picked.getString(ICommandBlock.TAG_COMMAND));
        commandBlock.close();
    }

    @Test
    void structureBlockDataIsCopiedForOperatorsOnly() {
        CompoundTag nbt = defaultCompound(BlockEntityID.STRUCTURE_BLOCK);
        BlockEntityStructureBlock structureBlock = new BlockEntityStructureBlock(chunkOf(nbt), nbt);

        assertNull(structureBlock.getPickNBT(player(false)));
        assertNotNull(structureBlock.getPickNBT(player(true)));
        structureBlock.close();
    }
}
