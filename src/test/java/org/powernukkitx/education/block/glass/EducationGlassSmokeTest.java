package org.powernukkitx.education.block.glass;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.registry.Registries;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Instantiates every hard glass and hard stained glass pane education block.
 * Most of these expose only a BlockState constructor, so each is built from its
 * public PROPERTIES default state and its accessors are exercised.
 */
class EducationGlassSmokeTest {

    private static final AtomicInteger checked = new AtomicInteger();

    private static final String[] CLASS_NAMES = {
            "BlockHardGlass", "BlockHardGlassPane",
            "BlockHardBlackStainedGlass", "BlockHardBlackStainedGlassPane",
            "BlockHardBlueStainedGlass", "BlockHardBlueStainedGlassPane",
            "BlockHardBrownStainedGlass", "BlockHardBrownStainedGlassPane",
            "BlockHardCyanStainedGlass", "BlockHardCyanStainedGlassPane",
            "BlockHardGrayStainedGlass", "BlockHardGrayStainedGlassPane",
            "BlockHardGreenStainedGlass", "BlockHardGreenStainedGlassPane",
            "BlockHardLightBlueStainedGlass", "BlockHardLightBlueStainedGlassPane",
            "BlockHardLightGrayStainedGlass", "BlockHardLightGrayStainedGlassPane",
            "BlockHardLimeStainedGlass", "BlockHardLimeStainedGlassPane",
            "BlockHardMagentaStainedGlass", "BlockHardMagentaStainedGlassPane",
            "BlockHardOrangeStainedGlass", "BlockHardOrangeStainedGlassPane",
            "BlockHardPinkStainedGlass", "BlockHardPinkStainedGlassPane",
            "BlockHardPurpleStainedGlass", "BlockHardPurpleStainedGlassPane",
            "BlockHardRedStainedGlass", "BlockHardRedStainedGlassPane",
            "BlockHardWhiteStainedGlass", "BlockHardWhiteStainedGlassPane",
            "BlockHardYellowStainedGlass", "BlockHardYellowStainedGlassPane",
    };

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.BLOCK.init();
    }

    @Test
    void instantiateAllGlass() throws Exception {
        for (String name : CLASS_NAMES) {
            Class<?> clazz = Class.forName(
                    "org.powernukkitx.education.block.glass." + name);
            Field propertiesField = clazz.getField("PROPERTIES");
            BlockProperties properties = (BlockProperties) propertiesField.get(null);
            assertNotNull(properties, name + " PROPERTIES");

            BlockState defaultState = properties.getDefaultState();
            Block block = (Block) clazz.getConstructor(BlockState.class).newInstance(defaultState);

            assertNotNull(block.getProperties(), name + " properties");
            assertFalse(block.getId().isEmpty(), name + " id");
            assertFalse(block.getName().isEmpty(), name + " name");
            checked.incrementAndGet();
        }
        assertTrue(checked.get() >= CLASS_NAMES.length, "all glass blocks constructed");
    }

    @Test
    void noArgGlassVariants() {
        assertFalse(new BlockHardGlass().getId().isEmpty());
        assertFalse(new BlockHardGlassPane().getId().isEmpty());
    }
}
