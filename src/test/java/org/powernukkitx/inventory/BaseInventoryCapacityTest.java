package org.powernukkitx.inventory;

import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;

/**
 * canAddItem() guards addItem() all over the block and entity code, so the two have to agree:
 * a single free slot takes one stack of the item being added, not one stack of 64.
 */
class BaseInventoryCapacityTest {

    private static final int SIZE = 27;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    /** A container with slot 0 free and every other slot full. */
    private static Inventory containerWithOneFreeSlot() {
        Inventory inventory = new BaseInventory(null, ContainerType.CONTAINER, SIZE) {
        };
        for (int i = 1; i < SIZE; i++) {
            Item stone = Item.get("minecraft:stone");
            stone.setCount(stone.getMaxStackSize());
            inventory.setItem(i, stone);
        }
        return inventory;
    }

    @Test
    void oneFreeSlotDoesNotHoldMoreThanOneStack() {
        Inventory inventory = containerWithOneFreeSlot();
        Item pearls = Item.get(ItemID.ENDER_PEARL);
        pearls.setCount(pearls.getMaxStackSize() + 4);

        Assertions.assertFalse(inventory.canAddItem(pearls.clone()));
        Assertions.assertEquals(4, inventory.addItem(pearls.clone())[0].getCount());
    }

    @Test
    void oneFreeSlotStillAcceptsASingleStack() {
        Inventory inventory = containerWithOneFreeSlot();
        Item pearls = Item.get(ItemID.ENDER_PEARL);
        pearls.setCount(pearls.getMaxStackSize());

        Assertions.assertTrue(inventory.canAddItem(pearls.clone()));
        Assertions.assertEquals(0, inventory.addItem(pearls.clone()).length);
    }
}
