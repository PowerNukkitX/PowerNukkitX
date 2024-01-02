package cn.nukkit.registry;

import cn.nukkit.blockentity.*;
import cn.nukkit.utils.OK;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class BlockEntityRegistry extends BaseRegistry<String, Class<? extends BlockEntity>, Class<? extends BlockEntity>> implements BlockEntityID {
    private static final BiMap<String, Class<? extends BlockEntity>> knownBlockEntities = HashBiMap.create(35);

    @Override
    public void init() {
        register(FURNACE, BlockEntityFurnace.class);
        register(CHEST, BlockEntityChest.class);
        register(SIGN, BlockEntitySign.class);
        register(ENCHANT_TABLE, BlockEntityEnchantTable.class);
        register(SKULL, BlockEntitySkull.class);
        register(FLOWER_POT, BlockEntityFlowerPot.class);
        register(BREWING_STAND, BlockEntityBrewingStand.class);
        register(ITEM_FRAME, BlockEntityItemFrame.class);
        register(CAULDRON, BlockEntityCauldron.class);
        register(ENDER_CHEST, BlockEntityEnderChest.class);
        register(BEACON, BlockEntityBeacon.class);
        register(PISTON_ARM, BlockEntityPistonArm.class);
        register(COMPARATOR, BlockEntityComparator.class);
        register(HOPPER, BlockEntityHopper.class);
        register(BED, BlockEntityBed.class);
        register(JUKEBOX, BlockEntityJukebox.class);
        register(SHULKER_BOX, BlockEntityShulkerBox.class);
        register(BANNER, BlockEntityBanner.class);
        register(MUSIC, BlockEntityMusic.class);
        register(LECTERN, BlockEntityLectern.class);
        register(BLAST_FURNACE, BlockEntityBlastFurnace.class);
        register(SMOKER, BlockEntitySmoker.class);
        register(BEEHIVE, BlockEntityBeehive.class);
        register(CONDUIT, BlockEntityConduit.class);
        register(BARREL, BlockEntityBarrel.class);
        register(CAMPFIRE, BlockEntityCampfire.class);
        register(BELL, BlockEntityBell.class);
        register(DAYLIGHT_DETECTOR, BlockEntityDaylightDetector.class);
        register(DISPENSER, BlockEntityDispenser.class);
        register(DROPPER, BlockEntityDropper.class);
        register(MOVING_BLOCK, BlockEntityMovingBlock.class);
        register(NETHER_REACTOR, BlockEntityNetherReactor.class);
        register(LODESTONE, BlockEntityLodestone.class);
        register(TARGET, BlockEntityTarget.class);
        register(END_PORTAL, BlockEntityEndPortal.class);
        register(END_GATEWAY, BlockEntityEndGateway.class);
        register(COMMAND_BLOCK, BlockEntityCommandBlock.class);
        register(SCULK_SENSOR, BlockEntitySculkSensor.class);
        register(SCULK_CATALYST, BlockEntitySculkCatalyst.class);
        register(SCULK_SHRIEKER, BlockEntitySculkShrieker.class);
        register(STRUCTURE_BLOCK, BlockEntityStructBlock.class);
        register(GLOW_ITEM_FRAME, BlockEntityGlowItemFrame.class);
        register(HANGING_SIGN, BlockEntityHangingSign.class);
        register(CHISELED_BOOKSHELF, BlockEntityChiseledBookshelf.class);
        register(DECORATED_POT, BlockEntityDecoratedPot.class);
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
    public OK<?> register(String key, Class<? extends BlockEntity> value) {
        if (knownBlockEntities.putIfAbsent(key, value) == null) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("This BlockEntity has already been registered with the identifier: " + key));
        }
    }
}
