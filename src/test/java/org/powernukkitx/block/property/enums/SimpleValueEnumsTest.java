package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Covers the trivial value-only block property enums - the ones that are just
 * a list of constants with no extra state or lookup methods. For each we verify
 * values() is populated and valueOf round-trips every constant.
 */
public class SimpleValueEnumsTest {

    static Stream<Class<? extends Enum<?>>> enums() {
        return Stream.of(
                Attachment.class,
                BambooLeafSize.class,
                BambooStalkThickness.class,
                BigDripleafTilt.class,
                CauldronLiquid.class,
                ChemistryTableType.class,
                ChiselType.class,
                Color.class,
                CoralColor.class,
                CreakingHeartState.class,
                DirtType.class,
                DoublePlantType.class,
                DripstoneThickness.class,
                FlowerType.class,
                MinecraftVerticalHalf.class,
                MonsterEggStoneType.class,
                NewLeafType.class,
                NewLogType.class,
                OldLeafType.class,
                OldLogType.class,
                OxidizationLevel.class,
                PaleMossCarpetSide.class,
                PortalAxis.class,
                PotentSulfurState.class,
                PrismarineBlockType.class,
                SandStoneType.class,
                SandType.class,
                SeaGrassType.class,
                SpongeType.class,
                StoneBrickType.class,
                StoneSlabType.class,
                StoneSlabType2.class,
                StoneSlabType3.class,
                StoneSlabType4.class,
                StoneType.class,
                StructureVoidType.class,
                TallGrassType.class,
                VaultState.class,
                WallBlockType.class,
                WallConnectionType.class
        );
    }

    @ParameterizedTest
    @MethodSource("enums")
    <E extends Enum<E>> void valuesAndRoundTrip(Class<E> type) {
        E[] constants = type.getEnumConstants();
        Assertions.assertNotNull(constants);
        Assertions.assertTrue(constants.length > 0, type.getSimpleName() + " has no constants");
        for (E c : constants) {
            Assertions.assertSame(c, Enum.valueOf(type, c.name()));
            Assertions.assertEquals(c.name(), c.toString());
            Assertions.assertTrue(c.ordinal() >= 0);
        }
    }
}
