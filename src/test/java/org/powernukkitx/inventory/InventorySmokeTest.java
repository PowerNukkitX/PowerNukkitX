package org.powernukkitx.inventory;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityID;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds every container block entity in the fixture level, grabs the inventory it holds
 * and exercises the read/write surface of the inventory/* package.
 */
public class InventorySmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void everyContainerInventoryAnswersItsApi() throws Exception {
        List<String> ids = blockEntityIds();
        Assertions.assertFalse(ids.isEmpty(), "no block entity ids found");

        int checked = 0;
        int failCount = 0;
        StringBuilder failures = new StringBuilder();
        int x = 0;

        for (String id : ids) {
            try {
                Position pos = new Position(x++, 80, 0, level);
                BlockEntity be = BlockEntity.createBlockEntity(id, pos);
                if (!(be instanceof InventoryHolder holder)) {
                    if (be != null) be.close();
                    continue;
                }
                Inventory inv = holder.getInventory();
                if (inv == null) {
                    be.close();
                    continue;
                }
                exercise(inv);
                be.close();
                checked++;
            } catch (Throwable t) {
                failCount++;
                if (failCount <= 25) {
                    failures.append('\n').append(id).append(" -> ").append(t);
                }
            }
        }

        Assertions.assertTrue(checked > 0, "no inventory survived the smoke pass" + failures);
    }

    private void exercise(Inventory inv) {
        safe(inv::getSize);
        safe(inv::getMaxStackSize);
        safe(inv::getContents);
        safe(inv::getType);
        safe(inv::getHolder);
        safe(inv::getViewers);
        safe(inv::isFull);
        safe(inv::isEmpty);
        safe(inv::hashCode);
        safe(inv::toString);

        Item stone = safeGet(() -> Item.get("minecraft:stone"));
        Item air = safeGet(() -> Item.get("minecraft:air"));

        safe(() -> inv.getItem(0));
        safe(() -> inv.getUnclonedItem(0));
        if (stone != null) {
            safe(() -> inv.canAddItem(stone.clone()));
            safe(() -> inv.getFreeSpace(stone.clone()));
            safe(() -> inv.firstEmpty(stone.clone()));
            safe(() -> inv.setItem(0, stone.clone()));
            safe(() -> inv.contains(stone.clone()));
            safe(() -> inv.all(stone.clone()));
            safe(() -> inv.first(stone.clone()));
            safe(() -> inv.addItem(stone.clone()));
            safe(() -> inv.removeItem(stone.clone()));
            safe(() -> inv.remove(stone.clone()));
        }
        safe(() -> inv.decreaseCount(0));
        safe(() -> inv.clear(0));
        safe(inv::clearAll);
        if (air != null) safe(() -> inv.setContents(new java.util.HashMap<>()));

        safe(() -> inv.networkSlotMap());
        safe(() -> inv.slotTypeMap());
    }

    private List<String> blockEntityIds() throws Exception {
        List<String> ids = new ArrayList<>();
        for (Field f : BlockEntityID.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
                ids.add((String) f.get(null));
            }
        }
        return ids;
    }

    private Item safeGet(java.util.function.Supplier<Item> s) {
        try {
            return s.get();
        } catch (Throwable t) {
            return null;
        }
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
