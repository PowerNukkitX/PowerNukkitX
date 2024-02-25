package cn.nukkit.inventory.fake;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryType;

public enum FakeInventoryType {
    CHEST(InventoryType.CONTAINER, new SingleFakeBlock(BlockID.CHEST, BlockEntity.CHEST), 27),
    DOUBLE_CHEST(InventoryType.CONTAINER, new DoubleFakeBlock(BlockID.CHEST, BlockEntity.CHEST), 27 * 2),
    ENDER_CHEST(InventoryType.CONTAINER, new SingleFakeBlock(BlockID.ENDER_CHEST, BlockEntity.ENDER_CHEST), 27),
    FURNACE(InventoryType.FURNACE, new SingleFakeBlock(BlockID.FURNACE, BlockEntity.FURNACE), 3),
    BREWING_STAND(InventoryType.BREWING_STAND, new SingleFakeBlock(BlockID.BREWING_STAND, BlockEntity.BREWING_STAND), 5),
    DISPENSER(InventoryType.DISPENSER, new SingleFakeBlock(BlockID.DISPENSER, BlockEntity.DISPENSER), 9),
    DROPPER(InventoryType.DROPPER, new SingleFakeBlock(BlockID.DROPPER, BlockEntity.DROPPER), 9),
    HOPPER(InventoryType.HOPPER, new SingleFakeBlock(BlockID.HOPPER, BlockEntity.HOPPER), 5),
    SHULKER_BOX(InventoryType.CONTAINER, new SingleFakeBlock(BlockID.UNDYED_SHULKER_BOX, BlockEntity.SHULKER_BOX), 27),
    WORKBENCH(InventoryType.WORKBENCH, new SingleFakeBlock(BlockID.CRAFTING_TABLE, "default"), 9);

    final InventoryType inventoryType;
    final FakeBlock fakeBlock;
    final int size;

    FakeInventoryType(InventoryType inventoryType, FakeBlock fakeBlock, int size) {
        this.inventoryType = inventoryType;
        this.fakeBlock = fakeBlock;
        this.size = size;
    }
}
