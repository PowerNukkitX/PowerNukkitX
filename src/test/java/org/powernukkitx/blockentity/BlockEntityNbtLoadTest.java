package org.powernukkitx.blockentity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

public class BlockEntityNbtLoadTest {

    static Level level;
    static int testX = 1000;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    private IChunk getLoadedChunk(Position pos) {
        int chunkX = pos.getFloorX() >> 4;
        int chunkZ = pos.getFloorZ() >> 4;
        level.loadChunk(chunkX, chunkZ);
        return level.getChunk(chunkX, chunkZ);
    }

    @Test
    void brewingStandNbtLoadDoesNotTriggerSetBlock() {
        Position pos = new Position(testX++, 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.BREWING_STAND);
        ListTag<CompoundTag> items = new ListTag<>();
        CompoundTag potionItem = new CompoundTag()
                .putByte("Slot", (byte) 1)
                .putString("Name", "minecraft:potion")
                .putShort("Damage", (short) 0)
                .putByte("Count", (byte) 1);
        items.add(potionItem);
        nbt.putList("Items", items);

        IChunk chunk = getLoadedChunk(pos);
        BlockEntityBrewingStand be = new BlockEntityBrewingStand(chunk, nbt);
        Assertions.assertNotNull(be.getInventory());
        Item loadedSlot1 = be.getInventory().getItem(1);
        Assertions.assertFalse(loadedSlot1.isNull(), "Slot 1 item should be loaded from NBT");
        be.close();
    }

    @Test
    void furnaceNbtLoadDoesNotTriggerSetBlock() {
        Position pos = new Position(testX++, 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.FURNACE);
        ListTag<CompoundTag> items = new ListTag<>();
        CompoundTag coalItem = new CompoundTag()
                .putByte("Slot", (byte) 1)
                .putString("Name", "minecraft:coal")
                .putShort("Damage", (short) 0)
                .putByte("Count", (byte) 1);
        items.add(coalItem);
        nbt.putList("Items", items);

        IChunk chunk = getLoadedChunk(pos);
        BlockEntityFurnace be = new BlockEntityFurnace(chunk, nbt);
        Assertions.assertNotNull(be.getInventory());
        be.close();
    }

    @Test
    void hopperNbtLoadDoesNotTriggerSetBlock() {
        Position pos = new Position(testX++, 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.HOPPER);
        IChunk chunk = getLoadedChunk(pos);
        BlockEntityHopper be = new BlockEntityHopper(chunk, nbt);
        Assertions.assertNotNull(be.getInventory());
        be.close();
    }

    @Test
    void campfireNbtLoadDoesNotTriggerSetBlock() {
        Position pos = new Position(testX++, 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.CAMPFIRE);
        IChunk chunk = getLoadedChunk(pos);
        BlockEntityCampfire be = new BlockEntityCampfire(chunk, nbt);
        Assertions.assertNotNull(be.getInventory());
        be.close();
    }

    @Test
    void beehiveNbtLoadWithLegacyHoneyLevel() {
        Position pos = new Position(testX++, 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.BEEHIVE);
        nbt.putByte("HoneyLevel", (byte) 3);
        IChunk chunk = getLoadedChunk(pos);
        BlockEntityBeehive be = new BlockEntityBeehive(chunk, nbt);
        Assertions.assertFalse(nbt.contains("HoneyLevel"), "Legacy HoneyLevel NBT tag should be removed after load");
        be.close();
    }
}
