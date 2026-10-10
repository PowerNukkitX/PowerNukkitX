package org.powernukkitx.block.customblock;

import org.powernukkitx.TestUtils;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockSolid;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.item.Item;
import org.powernukkitx.registry.BlockRegistry;
import org.powernukkitx.registry.Registries;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * Covers the runtime side of {@link CustomBlockDefinition}: builder defaults, id allocation and that the
 * definition registered through {@link CustomBlock#getDefinition()} drives the {@link Block} getters.
 */
public class CustomBlockRuntimeDefinitionTest {

    /** Minimal custom block, only feeds the definition builder. */
    private record TinyCustomBlock(String id) implements CustomBlock {
        @Override
        public double getFrictionFactor() {
            return 0.6;
        }

        @Override
        public double getResistance() {
            return 3.0;
        }

        @Override
        public int getLightFilter() {
            return 15;
        }

        @Override
        public int getLightLevel() {
            return 7;
        }

        @Override
        public double getHardness() {
            return 2.5;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Item toItem() {
            return null;
        }

        @Override
        public CustomBlockDefinition getDefinition() {
            return CustomBlockDefinition.builder(this).build();
        }
    }

    public static class RegisteredCustomBlock extends BlockSolid implements CustomBlock {
        public static final BlockProperties PROPERTIES = new BlockProperties("test:runtime_definition_block");

        public RegisteredCustomBlock() {
            this(PROPERTIES.getDefaultState());
        }

        public RegisteredCustomBlock(BlockState blockState) {
            super(blockState);
        }

        @Override
        @NotNull
        public BlockProperties getProperties() {
            return PROPERTIES;
        }

        @Override
        public CustomBlockDefinition getDefinition() {
            return CustomBlockDefinition.builder(this)
                    .destructibleByExplosion(17)
                    .friction(0.5)
                    .lightEmission(7)
                    .lightDampening(0)
                    .hasEntityStepSensor(true)
                    .build();
        }
    }

    /** New style: the definition is built statically and handed to the constructor, nothing is looked up. */
    public static class ConstructorFedCustomBlock extends BlockSolid implements CustomBlock {
        public static final BlockProperties PROPERTIES = new BlockProperties("test:constructor_fed_block");
        public static final CustomBlockDefinition DEFINITION = CustomBlockDefinition.builder(PROPERTIES)
                .destructibleByExplosion(23)
                .lightEmission(9)
                .build();

        public ConstructorFedCustomBlock(BlockState blockState) {
            super(blockState, DEFINITION);
        }

        @Override
        @NotNull
        public BlockProperties getProperties() {
            return PROPERTIES;
        }

        @Override
        public CustomBlockDefinition getDefinition() {
            return DEFINITION;
        }
    }

    @BeforeAll
    static void init() {
        Registries.BLOCK.init();
    }

    @Test
    void minimallyConfiguredBuilderKeepsSaneDefaults() {
        CustomBlockDefinition def = CustomBlockDefinition.builder(new TinyCustomBlock("test:defaults_block")).build();

        Assertions.assertTrue(def.isCanBePlaced());
        Assertions.assertEquals(64, def.getMaxStackSize());
        Assertions.assertTrue(def.isCanBePushed());
        Assertions.assertTrue(def.isCanBePulled());
        Assertions.assertTrue(def.isCanHarvestWithHand());
        Assertions.assertEquals(15, def.getLightDampening());
        Assertions.assertEquals(Block.DEFAULT_FRICTION_FACTOR, def.getFriction());
    }

    @Test
    void serializedBlockIdMatchesAllocatedRuntimeId() {
        CustomBlockDefinition first = CustomBlockDefinition.builder(new TinyCustomBlock("test:id_block_a")).build();
        CustomBlockDefinition second = CustomBlockDefinition.builder(new TinyCustomBlock("test:id_block_b")).build();

        Assertions.assertEquals(first.getRuntimeId(),
                first.nbt().getCompound("vanilla_block_data").getInt("block_id"));
        Assertions.assertEquals(second.getRuntimeId(),
                second.nbt().getCompound("vanilla_block_data").getInt("block_id"));
        Assertions.assertNotEquals(first.getRuntimeId(), second.getRuntimeId());
    }

    @Test
    void legacyBuilderEntryPointsStillWork() {
        CustomBlockDefinition def = CustomBlockDefinition.builder(new TinyCustomBlock("test:legacy_block"))
                .friction(0.5f)
                .isStepSensor(true)
                .build();

        Assertions.assertTrue(def.isStepSensor());
        CustomBlockDefinition legacy = new CustomBlockDefinition(def.identifier(), def.nbt(), null, true);
        Assertions.assertTrue(legacy.isStepSensor());
        Assertions.assertEquals(def.identifier(), legacy.identifier());
    }

    @Test
    @SuppressWarnings("unchecked")
    void registeredDefinitionDrivesBlockGetters() {
        // The plugin registration path needs a plugin class loader; publish the definition the same way
        // BlockRegistry#registerCustomBlock does. Instances created afterwards carry it.
        ((Map<String, CustomBlockDefinition>) TestUtils.getField(BlockRegistry.class, null, "CUSTOM_BLOCK_DEFINITION_BY_ID"))
                .put(RegisteredCustomBlock.PROPERTIES.getIdentifier(), new RegisteredCustomBlock().getDefinition());
        RegisteredCustomBlock block = new RegisteredCustomBlock();

        Assertions.assertEquals(17, block.getResistance());
        Assertions.assertEquals(0.5, block.getFrictionFactor(), 1e-6);
        Assertions.assertEquals(7, block.getLightLevel());
        Assertions.assertTrue(block.hasEntityStepSensor());
        // an explicit zero is a valid light filter, not "unset"
        Assertions.assertEquals(0, block.getLightFilter());
    }

    @Test
    void constructorFedDefinitionDrivesBlockGetters() {
        ConstructorFedCustomBlock block = new ConstructorFedCustomBlock(ConstructorFedCustomBlock.PROPERTIES.getDefaultState());

        Assertions.assertSame(ConstructorFedCustomBlock.DEFINITION, block.getDefinition());
        Assertions.assertEquals("test:constructor_fed_block", ConstructorFedCustomBlock.DEFINITION.identifier());
        Assertions.assertEquals(23, block.getResistance());
        Assertions.assertEquals(9, block.getLightLevel());
    }
}
