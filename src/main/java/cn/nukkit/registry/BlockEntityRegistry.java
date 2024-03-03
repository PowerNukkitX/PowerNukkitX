package cn.nukkit.registry;

import cn.nukkit.blockentity.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.concurrent.atomic.AtomicBoolean;

public class BlockEntityRegistry implements BlockEntityID, IRegistry<String, Class<? extends BlockEntity>, Class<? extends BlockEntity>> {
    private static final BiMap<String, Class<? extends BlockEntity>> knownBlockEntities = HashBiMap.create(35);
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        register0(FURNACE, BlockEntityFurnace.class);
        register0(CHEST, BlockEntityChest.class);
        register0(SIGN, BlockEntitySign.class);
        register0(ENCHANT_TABLE, BlockEntityEnchantTable.class);
        register0(SKULL, BlockEntitySkull.class);
        register0(FLOWER_POT, BlockEntityFlowerPot.class);
        register0(BREWING_STAND, BlockEntityBrewingStand.class);
        register0(ITEM_FRAME, BlockEntityItemFrame.class);
        register0(CAULDRON, BlockEntityCauldron.class);
        register0(ENDER_CHEST, BlockEntityEnderChest.class);
        register0(BEACON, BlockEntityBeacon.class);
        register0(PISTON_ARM, BlockEntityPistonArm.class);
        register0(COMPARATOR, BlockEntityComparator.class);
        register0(HOPPER, BlockEntityHopper.class);
        register0(BED, BlockEntityBed.class);
        register0(JUKEBOX, BlockEntityJukebox.class);
        register0(SHULKER_BOX, BlockEntityShulkerBox.class);
        register0(BANNER, BlockEntityBanner.class);
        register0(MUSIC, BlockEntityMusic.class);
        register0(LECTERN, BlockEntityLectern.class);
        register0(BLAST_FURNACE, BlockEntityBlastFurnace.class);
        register0(SMOKER, BlockEntitySmoker.class);
        register0(BEEHIVE, BlockEntityBeehive.class);
        register0(CONDUIT, BlockEntityConduit.class);
        register0(BARREL, BlockEntityBarrel.class);
        register0(CAMPFIRE, BlockEntityCampfire.class);
        register0(BELL, BlockEntityBell.class);
        register0(DAYLIGHT_DETECTOR, BlockEntityDaylightDetector.class);
        register0(DISPENSER, BlockEntityDispenser.class);
        register0(DROPPER, BlockEntityDropper.class);
        register0(MOVING_BLOCK, BlockEntityMovingBlock.class);
        register0(NETHER_REACTOR, BlockEntityNetherReactor.class);
        register0(LODESTONE, BlockEntityLodestone.class);
        register0(TARGET, BlockEntityTarget.class);
        register0(END_PORTAL, BlockEntityEndPortal.class);
        register0(END_GATEWAY, BlockEntityEndGateway.class);
        register0(COMMAND_BLOCK, BlockEntityCommandBlock.class);
        register0(SCULK_SENSOR, BlockEntitySculkSensor.class);
        register0(SCULK_CATALYST, BlockEntitySculkCatalyst.class);
        register0(SCULK_SHRIEKER, BlockEntitySculkShrieker.class);
        register0(STRUCTURE_BLOCK, BlockEntityStructBlock.class);
        register0(GLOW_ITEM_FRAME, BlockEntityGlowItemFrame.class);
        register0(HANGING_SIGN, BlockEntityHangingSign.class);
        register0(CHISELED_BOOKSHELF, BlockEntityChiseledBookshelf.class);
        register0(DECORATED_POT, BlockEntityDecoratedPot.class);
    }

    @Override
    public Class<? extends BlockEntity> get(String key) {
        return knownBlockEntities.get(key);
    }

    public String getSaveId(Class<? extends BlockEntity> c) {
        return knownBlockEntities.inverse().get(c);
    }

    @Override
    public void trim() {
    }

    @Override
    public void reload() {
        isLoad.set(false);
        knownBlockEntities.clear();
        init();
    }

    @Override
    public void register(String key, Class<? extends BlockEntity> value) throws RegisterException {
        if (knownBlockEntities.putIfAbsent(key, value) == null) {
        } else {
            throw new RegisterException("This BlockEntity has already been registered with the identifier: " + key);
        }
    }

    private void register0(String key, Class<? extends BlockEntity> value){
        try {
            register(key,value);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
