package cn.nukkit.inventory.fake;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

public enum FakeInventoryType {
    CHEST(ContainerType.CONTAINER, new SingleFakeBlock(BlockID.CHEST, BlockEntity.CHEST), 27),
    DOUBLE_CHEST(ContainerType.CONTAINER, new DoubleFakeBlock(BlockID.CHEST, BlockEntity.CHEST), 27 * 2),
    ENDER_CHEST(ContainerType.CONTAINER, new SingleFakeBlock(BlockID.ENDER_CHEST, BlockEntity.ENDER_CHEST), 27),
    FURNACE(ContainerType.FURNACE, new SingleFakeBlock(BlockID.FURNACE, BlockEntity.FURNACE), 3),
    BREWING_STAND(ContainerType.BREWING_STAND, new SingleFakeBlock(BlockID.BREWING_STAND, BlockEntity.BREWING_STAND), 5),
    DISPENSER(ContainerType.DISPENSER, new SingleFakeBlock(BlockID.DISPENSER, BlockEntity.DISPENSER), 9),
    DROPPER(ContainerType.DROPPER, new SingleFakeBlock(BlockID.DROPPER, BlockEntity.DROPPER), 9),
    HOPPER(ContainerType.HOPPER, new SingleFakeBlock(BlockID.HOPPER, BlockEntity.HOPPER), 5),
    SHULKER_BOX(ContainerType.CONTAINER, new SingleFakeBlock(BlockID.UNDYED_SHULKER_BOX, BlockEntity.SHULKER_BOX), 27),
    WORKBENCH(ContainerType.WORKBENCH, new SingleFakeBlock(BlockID.CRAFTING_TABLE, "default"), 9),
    ENTITY(ContainerType.CONTAINER, null, -1, EntityFakeBlock::new);

    final ContainerType inventoryType;
    final FakeBlock fakeBlock;
    final FakeBlockBuilder builder;
    final int size;

    FakeInventoryType(ContainerType inventoryType, FakeBlock fakeBlock, int size) {
        this(inventoryType, fakeBlock, size, null);
    }

    FakeInventoryType(ContainerType inventoryType, FakeBlock fakeBlock, int size, FakeBlockBuilder builder) {
        this.inventoryType = inventoryType;
        this.fakeBlock = fakeBlock;
        this.builder = builder;
        this.size = size;
    }

    public boolean isCraftType() {
        return this == WORKBENCH || this == FURNACE || this == BREWING_STAND;
    }
}
