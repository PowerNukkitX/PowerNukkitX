package org.powernukkitx.block.customblock;

import org.powernukkitx.block.customblock.data.CollisionBox;
import org.powernukkitx.block.customblock.data.Component;
import org.powernukkitx.block.customblock.data.CraftingTable;
import org.powernukkitx.block.customblock.data.Geometry;
import org.powernukkitx.block.customblock.data.Materials;
import org.powernukkitx.block.customblock.data.Permutation;
import org.powernukkitx.block.customblock.data.SelectionBox;
import org.powernukkitx.block.customblock.data.Transformation;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Coverage for the custom block definition builder and the data/* NBT POJOs. A minimal
 * non-Block {@link CustomBlock} implementation is enough for the builder - it only reads
 * the identifier and produces NBT. Block-registry-backed permutation properties are not
 * touched (getPropertiesNBT() returns null for a non-Block custom block).
 */
public class CustomBlockSmokeTest {

    /** Minimal custom block - not a real Block, only feeds the definition builder. */
    private static final class TinyCustomBlock implements CustomBlock {
        private final String id;

        TinyCustomBlock(String id) {
            this.id = id;
        }

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

    @Test
    void definitionBuilderProducesNbt() {
        TinyCustomBlock block = new TinyCustomBlock("test:tiny_block");
        CustomBlockDefinition def = CustomBlockDefinition.builder(block)
                .name("Tiny Block")
                .friction(0.5f)
                .lightEmission(5)
                .lightDampening(2)
                .destructibleByExplosion(30)
                .breakTime(1.5f)
                .isPlayerInteractable(true)
                .isHiddenInCommands(false)
                .blockTick(20, 40, true)
                .isStepSensor(true)
                .build();

        Assertions.assertEquals("test:tiny_block", def.identifier());
        Assertions.assertNotNull(def.nbt());
        Assertions.assertTrue(def.isStepSensor());
        Assertions.assertNotNull(def.tickSettings());
        Assertions.assertTrue(def.nbt().getCompound("components").contains("minecraft:display_name"));

        int runtimeId = def.getRuntimeId();
        Assertions.assertEquals(runtimeId, CustomBlockDefinition.getRuntimeId("test:tiny_block"));
        Assertions.assertNotNull(def.getComponents());
    }

    @Test
    void customBuildAllowsNbtMutation() {
        TinyCustomBlock block = new TinyCustomBlock("test:custom_build");
        CustomBlockDefinition def = CustomBlockDefinition.builder(block)
                .name("Custom")
                .customBuild(nbt -> nbt.putString("marker", "hit"));
        Assertions.assertEquals("hit", def.nbt().getString("marker"));
    }

    @Test
    void collisionAndSelectionBoxes() {
        CollisionBox cb = new CollisionBox(-8, 0, -8, 16, 16, 16);
        Assertions.assertEquals(-8, cb.originX());
        Assertions.assertEquals(16, cb.sizeY());
        CompoundTag cbTag = cb.toCompoundTag();
        Assertions.assertNotNull(cbTag);

        SelectionBox sb = new SelectionBox(0, 0, 0, 8, 8, 8);
        Assertions.assertEquals(8, sb.sizeX());
        Assertions.assertNotNull(sb.toCompoundTag());
    }

    @Test
    void geometryBuilder() {
        Geometry geo = new Geometry("geometry.tiny")
                .boneVisibility("bone1", true)
                .boneVisibility("bone2", "query.block_state('x') == 1")
                .culling("cull")
                .cullingShape("shape");
        CompoundTag tag = geo.toCompoundTag();
        Assertions.assertEquals("geometry.tiny", tag.getString("identifier"));
        Assertions.assertTrue(tag.contains("bone_visibility"));
        Assertions.assertEquals("cull", tag.getString("culling"));
    }

    @Test
    void transformationAndPermutation() {
        Transformation transform = new Transformation(
                new Vector3(0, 1, 0), new Vector3(1, 1, 1), new Vector3(0, 90, 0));
        Assertions.assertNotNull(transform.toCompoundTag());
        Assertions.assertEquals(90, transform.rotation().y);

        Component component = Component.builder()
                .friction(0.4f)
                .lightEmission(3)
                .collisionBox(new CollisionBox(0, 0, 0, 16, 16, 16))
                .build();
        Assertions.assertNotNull(component.toCompoundTag());

        Permutation perm = new Permutation(component, "query.block_state('lit') == 1");
        CompoundTag permTag = perm.toCompoundTag();
        Assertions.assertNotNull(permTag);
        Assertions.assertEquals("query.block_state('lit') == 1", perm.condition());
    }

    @Test
    void craftingTableData() {
        CraftingTable table = new CraftingTable("Tiny Bench", List.of("tiny", "wood"));
        Assertions.assertEquals("Tiny Bench", table.tableName());
        Assertions.assertNotNull(table.toCompoundTag());
    }

    @Test
    void materialsBuilder() {
        Materials materials = Materials.builder()
                .up(Materials.RenderMethod.OPAQUE, "top_texture")
                .down(Materials.RenderMethod.OPAQUE, "bottom_texture", Materials.TintMethod.GRASS)
                .north(Materials.RenderMethod.ALPHA_TEST, "side_texture");
        CompoundTag tag = materials.toCompoundTag();
        Assertions.assertNotNull(tag);

        Materials.PackedBools bools = new Materials.PackedBools(true, false, true);
        Assertions.assertTrue(bools.faceDimming());
        Assertions.assertFalse(bools.randomUVRotation());
        Assertions.assertTrue(bools.textureVariation());

        Assertions.assertEquals(Materials.TintMethod.GRASS, Materials.TintMethod.fromString("grass"));
        Assertions.assertNull(Materials.TintMethod.fromString(""));
    }
}
