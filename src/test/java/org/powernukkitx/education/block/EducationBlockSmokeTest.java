package org.powernukkitx.education.block;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the non-element education blocks - chemistry stations, cameras,
 * colored/underwater torches and the underwater tnt. Each block is constructed
 * directly through its public API and its basic accessors are checked.
 */
class EducationBlockSmokeTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.BLOCK.init();
    }

    private void assertBasics(Block block, String expectedId) {
        assertNotNull(block, "block instance");
        assertNotNull(block.getProperties(), "properties");
        assertEquals(expectedId, block.getId(), "identifier");
        assertFalse(block.getName().isEmpty(), "name");
    }

    @Test
    void plainBlocks() {
        assertBasics(new BlockCamera(), Block.CAMERA);
        assertBasics(new BlockChemicalHeat(), Block.CHEMICAL_HEAT);
    }

    @Test
    void directionalStations() {
        BlockCompoundCreator compound = new BlockCompoundCreator();
        assertBasics(compound, Block.COMPOUND_CREATOR);
        compound.setBlockFace(BlockFace.WEST);
        int direction = compound.getPropertyValue(
                org.powernukkitx.block.property.CommonBlockProperties.DIRECTION);
        assertEquals(BlockFace.WEST.getHorizontalIndex(), direction);

        BlockElementConstructor constructor = new BlockElementConstructor();
        assertBasics(constructor, Block.ELEMENT_CONSTRUCTOR);
        constructor.setBlockFace(BlockFace.EAST);

        BlockLabTable lab = new BlockLabTable();
        assertBasics(lab, Block.LAB_TABLE);
        lab.setBlockFace(BlockFace.NORTH);

        BlockMaterialReducer reducer = new BlockMaterialReducer();
        assertBasics(reducer, Block.MATERIAL_REDUCER);
        reducer.setBlockFace(BlockFace.SOUTH);
    }

    @Test
    void coloredTorches() {
        assertBasics(new BlockColoredTorchBlue(), Block.COLORED_TORCH_BLUE);
        assertBasics(new BlockColoredTorchGreen(), Block.COLORED_TORCH_GREEN);
        assertBasics(new BlockColoredTorchPurple(), Block.COLORED_TORCH_PURPLE);
        assertBasics(new BlockColoredTorchRed(), Block.COLORED_TORCH_RED);
    }

    @Test
    void underwaterBlocks() {
        BlockUnderwaterTorch torch = new BlockUnderwaterTorch();
        assertBasics(torch, Block.UNDERWATER_TORCH);
        assertFalse(torch.canBeFlowedInto(), "underwater torch is not flowed into");

        BlockUnderwaterTNT tnt = new BlockUnderwaterTNT();
        assertBasics(tnt, Block.UNDERWATER_TNT);
    }

    @Test
    void stateConstructorsRoundTrip() {
        Supplier<Block>[] fromState = new Supplier[] {
                (Supplier<Block>) () -> new BlockCamera(BlockCamera.PROPERTIES.getDefaultState()),
                (Supplier<Block>) () -> new BlockLabTable(BlockLabTable.PROPERTIES.getDefaultState()),
                (Supplier<Block>) () -> new BlockUnderwaterTorch(BlockUnderwaterTorch.PROPERTIES.getDefaultState()),
        };
        int checked = 0;
        for (Supplier<Block> supplier : fromState) {
            Block block = supplier.get();
            assertNotNull(block.getProperties());
            checked++;
        }
        assertTrue(checked > 0, "state constructors exercised");
    }
}
