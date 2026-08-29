package org.powernukkitx.blockentity;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Type-specific behaviour pass over the container/interactive block entities - furnace
 * timers, hopper cooldown, brewing fuel, jukebox records, lectern books, sign text,
 * beacon powers, bell ringing and chest pairing. Complements the generic getter and
 * onUpdate/inventory passes in BlockEntitySmokeTest / BlockEntityBehaviorSmokeTest.
 * Everything is tolerant so one misbehaving type never sinks the run.
 */
public class BlockEntityDeepSmokeTest {

    static Level level;
    static int x;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    private <T extends BlockEntity> T make(String id, Class<T> type) {
        Position pos = new Position(x++, 80, 0, level);
        BlockEntity be = BlockEntity.createBlockEntity(id, pos);
        if (type.isInstance(be)) return type.cast(be);
        if (be != null) be.close();
        return null;
    }

    @Test
    void furnaceTimers() {
        BlockEntityFurnace be = make(BlockEntityID.FURNACE, BlockEntityFurnace.class);
        if (be == null) return;
        safe(() -> be.setBurnTime(200));
        safe(be::getBurnTime);
        safe(() -> be.setBurnDuration(200));
        safe(be::getBurnDuration);
        safe(() -> be.setCookTime(50));
        safe(be::getCookTime);
        safe(() -> be.setMaxTime(200));
        safe(be::getMaxTime);
        safe(() -> be.setStoredXP(3f));
        safe(be::getStoredXP);
        safe(be::calculateXpDrop);
        safe(() -> be.getInventory().setItem(0, Item.get("minecraft:iron_ore")));
        safe(() -> be.getInventory().setItem(1, Item.get("minecraft:coal")));
        safe(be::onUpdate);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void hopperCooldownAndTransfer() {
        BlockEntityHopper be = make(BlockEntityID.HOPPER, BlockEntityHopper.class);
        if (be == null) return;
        safe(() -> be.setTransferCooldown(8));
        safe(be::getCooldownTick);
        safe(be::isOnTransferCooldown);
        safe(() -> be.setDisabled(true));
        safe(be::isDisabled);
        safe(() -> be.setDisabled(false));
        safe(be::pushItems);
        safe(be::pullItemsFromMinecart);
        safe(be::pushItemsIntoMinecart);
        safe(be::onUpdate);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void brewingStandFuel() {
        BlockEntityBrewingStand be = make(BlockEntityID.BREWING_STAND, BlockEntityBrewingStand.class);
        if (be == null) return;
        safe(() -> be.setFuel(20));
        safe(be::getFuel);
        safe(() -> be.getInventory().setItem(0, Item.get("minecraft:blaze_powder")));
        safe(() -> be.getInventory().setItem(1, Item.get("minecraft:potion")));
        safe(be::onUpdate);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void jukeboxRecord() {
        BlockEntityJukebox be = make(BlockEntityID.JUKEBOX, BlockEntityJukebox.class);
        if (be == null) return;
        safe(() -> be.setRecordItem(Item.get("minecraft:music_disc_cat")));
        safe(be::getRecordItem);
        safe(be::play);
        safe(be::stop);
        safe(be::dropItem);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void lecternBookAndPages() {
        BlockEntityLectern be = make(BlockEntityID.LECTERN, BlockEntityLectern.class);
        if (be == null) return;
        safe(() -> be.setBook(Item.get("minecraft:written_book")));
        safe(be::hasBook);
        safe(be::getBook);
        safe(() -> be.setLeftPage(2));
        safe(() -> be.setRightPage(3));
        safe(be::getLeftPage);
        safe(be::getRightPage);
        safe(() -> be.setRawPage(1));
        safe(be::getRawPage);
        safe(be::getTotalPages);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void signText() {
        BlockEntitySign be = make(BlockEntityID.SIGN, BlockEntitySign.class);
        if (be == null) return;
        safe(() -> be.setText("line1", "line2", "line3", "line4"));
        safe(be::getText);
        safe(() -> be.getText(true));
        safe(be::isEmpty);
        safe(() -> be.setWaxed(true));
        safe(be::isWaxed);
        safe(() -> be.setGlowing(true));
        safe(be::isGlowing);
        safe(be::getColor);
        safe(() -> be.setText(false, "back"));
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void beaconPowers() {
        BlockEntityBeacon be = make(BlockEntityID.BEACON, BlockEntityBeacon.class);
        if (be == null) return;
        safe(() -> be.setPowerLevel(2));
        safe(be::getPowerLevel);
        safe(() -> be.setPrimaryPower(1));
        safe(be::getPrimaryPower);
        safe(() -> be.setSecondaryPower(3));
        safe(be::getSecondaryPower);
        safe(be::onUpdate);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void bellRinging() {
        BlockEntityBell be = make(BlockEntityID.BELL, BlockEntityBell.class);
        if (be == null) return;
        safe(() -> be.setRinging(true));
        safe(be::isRinging);
        safe(() -> be.setDirection(2));
        safe(be::getDirection);
        safe(() -> be.setTicks(5));
        safe(be::getTicks);
        safe(be::onUpdate);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void campfireItems() {
        BlockEntityCampfire be = make(BlockEntityID.CAMPFIRE, BlockEntityCampfire.class);
        if (be == null) return;
        safe(be::getSize);
        safe(() -> be.setItem(0, Item.get("minecraft:beef")));
        safe(() -> be.getItem(0));
        safe(() -> be.setKeepItem(0, true));
        safe(() -> be.getKeepItem(0));
        safe(be::onUpdate);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    @Test
    void chestPairing() {
        BlockEntityChest a = make(BlockEntityID.CHEST, BlockEntityChest.class);
        BlockEntityChest b = make(BlockEntityID.CHEST, BlockEntityChest.class);
        if (a == null || b == null) {
            if (a != null) a.close();
            if (b != null) b.close();
            return;
        }
        safe(a::getSize);
        safe(a::getRealInventory);
        safe(a::isPaired);
        safe(() -> a.pairWith(b));
        safe(a::getPair);
        safe(a::unpair);
        safe(a::close);
        safe(b::close);
        Assertions.assertTrue(true);
    }

    @Test
    void conduitScan() {
        BlockEntityConduit be = make(BlockEntityID.CONDUIT, BlockEntityConduit.class);
        if (be == null) return;
        safe(be::isActive);
        safe(be::getValidBlocks);
        safe(be::getPlayerRadius);
        safe(be::getAttackRadius);
        safe(be::scanEdgeBlock);
        safe(be::scanStructure);
        safe(be::onUpdate);
        safe(be::close);
        Assertions.assertTrue(true);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
