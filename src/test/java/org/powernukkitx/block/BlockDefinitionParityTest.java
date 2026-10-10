package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Guards the migration of per-class overrides to {@link org.powernukkitx.block.definition.BlockDefinition}:
 * values must not depend on how a block instance was created and the flags the old overrides carried
 * (activation, hand harvesting, tool tiers, silk touch, solidity...) must survive.
 */
public class BlockDefinitionParityTest {

    @BeforeAll
    static void init() {
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
    }

    private static String value(Supplier<Object> getter) {
        try {
            return String.valueOf(getter.get());
        } catch (RuntimeException e) {
            return "ERR:" + e.getClass().getSimpleName();
        }
    }

    private static String signature(Block block) {
        List<Supplier<Object>> getters = List.of(
                block::getHardness, block::getResistance, block::getLightLevel, block::getLightFilter,
                block::getToolType, block::getToolTier, block::getBurnChance, block::getBurnAbility,
                block::getFrictionFactor, block::getWaterloggingLevel, block::canBeActivated,
                block::canHarvestWithHand, block::canSilkTouch, block::isSolid, block::isTransparent,
                block::isPowerSource, block::canBePushed, block::canBePulled, block::breaksWhenMoved,
                block::sticksToPiston, block::canBeReplaced, block::hasEntityCollision, block::canPassThrough,
                () -> block.canHarvest(Item.AIR));
        StringBuilder sb = new StringBuilder();
        getters.forEach(getter -> sb.append(value(getter)).append('|'));
        for (BlockFace face : BlockFace.values()) {
            sb.append(value(() -> block.isSolid(face))).append('|');
        }
        return sb.toString();
    }

    @Test
    void noArgConstructorMatchesRegistryConstruction() {
        Set<Class<?>> checked = new HashSet<>();
        StringBuilder mismatches = new StringBuilder();
        for (BlockState state : Registries.BLOCKSTATE.getAllState()) {
            Block fromRegistry;
            try {
                fromRegistry = state.toBlock();
            } catch (Throwable t) {
                continue;
            }
            if (!state.equals(fromRegistry.getProperties().getDefaultState()) || !checked.add(fromRegistry.getClass())) {
                continue;
            }
            Block direct;
            try {
                direct = fromRegistry.getClass().getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                continue;
            } catch (ReflectiveOperationException e) {
                mismatches.append('\n').append(fromRegistry.getClass().getSimpleName()).append(" failed: ").append(e);
                continue;
            }
            String expected = signature(fromRegistry);
            String actual = signature(direct);
            if (!expected.equals(actual)) {
                mismatches.append('\n').append(fromRegistry.getClass().getSimpleName())
                        .append(" registry=").append(expected).append(" direct=").append(actual);
            }
        }
        Assertions.assertTrue(mismatches.isEmpty(), "constructors disagree with registry construction:" + mismatches);
    }

    @Test
    void directConstructionKeepsBlockSpecificValues() {
        Assertions.assertEquals(15, new BlockGlowstone().getLightLevel());
        Assertions.assertEquals(6000, new BlockObsidian().getResistance());
        Assertions.assertEquals(0.5, new BlockDirt().getHardness());
    }

    @Test
    void doorsAreActivatableAndNeverSolidOnAnyFace() {
        BlockWoodenDoor door = new BlockWoodenDoor();
        Assertions.assertTrue(door.canBeActivated());
        Assertions.assertFalse(door.isSolid());
        for (BlockFace face : BlockFace.values()) {
            Assertions.assertFalse(door.isSolid(face), "door solid on " + face);
        }
    }

    @Test
    void handMineableWoodRequiresNoToolTier() {
        Assertions.assertEquals(0, new BlockOakFence().getToolTier());
        Assertions.assertTrue(new BlockOakFence().canHarvest(Item.AIR));
        Assertions.assertTrue(new BlockWoodenDoor().canHarvest(Item.AIR));
        Assertions.assertFalse(new BlockIronDoor().canHarvest(Item.AIR));
    }

    @Test
    void poweredAndActivatorRailsAreNotPowerSources() {
        Assertions.assertFalse(new BlockGoldenRail().isPowerSource());
        Assertions.assertFalse(new BlockActivatorRail().isPowerSource());
        Assertions.assertTrue(new BlockDetectorRail().isPowerSource());
    }

    @Test
    void thinBlocksAreNotSolid() {
        Assertions.assertFalse(new BlockGlassPane().isSolid());
        Assertions.assertFalse(new BlockIronBars().isSolid());
    }

    @Test
    void harvestAndSilkTouchRestrictionsSurvive() {
        Assertions.assertFalse(new BlockStoneBrickWall().canHarvestWithHand());
        Assertions.assertTrue(new BlockGrassBlock(BlockGrassBlock.PROPERTIES.getDefaultState()).canSilkTouch());
        Assertions.assertTrue(new BlockGlassPane().canSilkTouch());
        Assertions.assertFalse(new BlockTintedGlass().canSilkTouch());
    }

    @Test
    void slabResistancesFollowTheirDefinitions() {
        Assertions.assertEquals(6, new BlockStoneBrickSlab(BlockStoneBrickSlab.PROPERTIES.getDefaultState()).getResistance());
        Assertions.assertEquals(3, new BlockMudBrickSlab(BlockMudBrickSlab.PROPERTIES.getDefaultState()).getResistance());
        Assertions.assertEquals(15, new BlockOakDoubleSlab(BlockOakDoubleSlab.PROPERTIES.getDefaultState()).getResistance());
    }
}
