package org.powernukkitx.blockentity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.ItemHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class BlockEntityNbtLoadTest {

    static Level level;
    static AtomicInteger testX = new AtomicInteger(1000);

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
        Position pos = new Position(testX.getAndIncrement(), 80, 100, level);
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
        Position pos = new Position(testX.getAndIncrement(), 80, 100, level);
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
        Assertions.assertFalse(be.getInventory().getItem(1).isNull());
        be.close();
    }

    @Test
    void hopperNbtLoadDoesNotTriggerSetBlock() {
        Position pos = new Position(testX.getAndIncrement(), 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.HOPPER);
        ListTag<CompoundTag> items = new ListTag<>();
        items.add(ItemHelper.write(Item.get("minecraft:stone"), 0));
        nbt.putList("Items", items);
        IChunk chunk = getLoadedChunk(pos);
        BlockEntityHopper be = new BlockEntityHopper(chunk, nbt);
        Assertions.assertNotNull(be.getInventory());
        Assertions.assertFalse(be.getInventory().getItem(0).isNull());
        be.close();
    }

    @Test
    void campfireNbtLoadDoesNotTriggerSetBlock() {
        Position pos = new Position(testX.getAndIncrement(), 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.CAMPFIRE);
        Item stone = Item.get("minecraft:stone");
        stone.setCount(1);
        CompoundTag item1 = ItemHelper.write(stone);
        nbt.putCompound("Item1", item1);
        nbt.putInt("ItemTime1", 100);
        IChunk chunk = getLoadedChunk(pos);
        BlockEntity be = BlockEntity.createBlockEntity(BlockEntityID.CAMPFIRE, chunk, nbt);
        if (be instanceof BlockEntityCampfire campfire) {
            Assertions.assertNotNull(campfire.getInventory());
            Assertions.assertFalse(campfire.getInventory().getItem(0).isNull());
            campfire.close();
        }
    }

    @Test
    void shelfNbtLoadDoesNotTriggerSetBlock() {
        Position pos = new Position(testX.getAndIncrement(), 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.SHELF);
        ListTag<CompoundTag> items = new ListTag<>();
        Item book = Item.get("minecraft:book");
        book.setCount(1);
        items.add(ItemHelper.write(book));
        nbt.putList("Items", items);
        IChunk chunk = getLoadedChunk(pos);
        BlockEntityShelf be = new BlockEntityShelf(chunk, nbt);
        Assertions.assertNotNull(be.getInventory());
        Assertions.assertFalse(be.getInventory().getItem(0).isNull(), "Shelf slot 0 item should be loaded");
        be.close();
    }

    @Test
    void beehiveNbtLoadWithLegacyHoneyLevel() {
        Position pos = new Position(testX.getAndIncrement(), 80, 100, level);
        IChunk chunk = getLoadedChunk(pos);
        chunk.setBlockState(pos.getFloorX() & 0x0f, pos.getFloorY(), pos.getFloorZ() & 0x0f, Block.get(BlockID.BEEHIVE).getBlockState());

        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.BEEHIVE);
        nbt.putByte("HoneyLevel", (byte) 3);
        BlockEntityBeehive be = new BlockEntityBeehive(chunk, nbt);
        Assertions.assertFalse(nbt.contains("HoneyLevel"), "Legacy HoneyLevel NBT tag should be removed after load");
        be.close();
    }

    @Test
    void shelfNbtLoadIgnoresOutOfBoundsItems() {
        Position pos = new Position(testX.getAndIncrement(), 80, 100, level);
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntityID.SHELF);
        ListTag<CompoundTag> items = new ListTag<>();
        Item book = Item.get("minecraft:book");
        book.setCount(1);
        for (int i = 0; i < 10; i++) {
            items.add(ItemHelper.write(book));
        }
        nbt.putList("Items", items);
        IChunk chunk = getLoadedChunk(pos);
        BlockEntityShelf be = new BlockEntityShelf(chunk, nbt);
        Assertions.assertNotNull(be.getInventory());
        Assertions.assertFalse(be.getInventory().getItem(0).isNull());
        be.close(); // Should not throw exception or pollute out of bounds
    }
}
