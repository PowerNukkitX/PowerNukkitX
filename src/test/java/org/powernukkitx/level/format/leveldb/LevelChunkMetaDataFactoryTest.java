package org.powernukkitx.level.format.leveldb;

import org.cloudburstmc.nbt.NbtMap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.network.NetworkConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LevelChunkMetaDataFactoryTest {

    @Test
    void createsMetadataForAllVanillaDimensions() {
        NbtMap overworld = LevelChunkMetaDataFactory.createForChunk(
                mockChunk(DimensionEnum.OVERWORLD.getDimensionData(), 123), 1);
        NbtMap nether = LevelChunkMetaDataFactory.createForChunk(
                mockChunk(DimensionEnum.NETHER.getDimensionData(), 123), 1);
        NbtMap theEnd = LevelChunkMetaDataFactory.createForChunk(
                mockChunk(DimensionEnum.THE_END.getDimensionData(), 123), 1);

        assertEquals("Overworld", overworld.getString("DimensionName"));
        assertEquals("Nether", nether.getString("DimensionName"));
        assertEquals("TheEnd", theEnd.getString("DimensionName"));

        assertEquals(1, overworld.getInt("GeneratorType"));
        assertEquals(1, nether.getInt("GeneratorType"));
        assertEquals(1, theEnd.getInt("GeneratorType"));

        assertFalse(overworld.containsKey("LastSavedBaseGameVersion"));
        assertFalse(overworld.containsKey("LastSavedDimensionHeightRange"));
        assertFalse(nether.containsKey("LastSavedBaseGameVersion"));
        assertFalse(nether.containsKey("LastSavedDimensionHeightRange"));
        assertFalse(theEnd.containsKey("LastSavedBaseGameVersion"));
        assertFalse(theEnd.containsKey("LastSavedDimensionHeightRange"));

        assertEquals("1.18.0", overworld.getString("BiomeBaseGameVersion"));
        assertEquals("1.18.0", nether.getString("BiomeBaseGameVersion"));
        assertEquals("1.18.0", theEnd.getString("BiomeBaseGameVersion"));

        assertEquals(1, overworld.getShort("Overworld1_18HeightExtended"));
        assertEquals(1, overworld.getShort("UnderwaterLavaLakeFixed"));
        assertEquals(1, overworld.getShort("WorldGenBelowZeroFixed"));
        assertFalse(nether.containsKey("Overworld1_18HeightExtended"));
        assertFalse(nether.containsKey("UnderwaterLavaLakeFixed"));
        assertFalse(nether.containsKey("WorldGenBelowZeroFixed"));
        assertFalse(theEnd.containsKey("Overworld1_18HeightExtended"));
        assertFalse(theEnd.containsKey("UnderwaterLavaLakeFixed"));
        assertFalse(theEnd.containsKey("WorldGenBelowZeroFixed"));

        assertEquals(1, overworld.getShort("SkullFlatteningPerformed"));
        assertEquals(1, nether.getShort("SkullFlatteningPerformed"));
        assertEquals(1, theEnd.getShort("SkullFlatteningPerformed"));
        assertEquals(1, overworld.getInt("NeighborAwareBlockUpgradeVersion"));
        assertEquals(1, nether.getInt("NeighborAwareBlockUpgradeVersion"));
        assertEquals(1, theEnd.getInt("NeighborAwareBlockUpgradeVersion"));

        assertFalse(overworld.containsKey("BlendingVersion"));
        assertFalse(nether.containsKey("BlendingVersion"));
        assertFalse(theEnd.containsKey("BlendingVersion"));
    }

    @Test
    void saveUpdatePreservesGenerationProvenanceAndFixState() {
        NbtMap oldHeightRange = NbtMap.builder()
                .putShort("max", (short) 256)
                .putShort("min", (short) 0)
                .build();

        NbtMap metadata = NbtMap.builder()
                .putString("BiomeBaseGameVersion", "1.17.40")
                .putString("DimensionName", "Overworld")
                .putLong("GenerationSeed", 987654321L)
                .putInt("GeneratorType", 2)
                .putString("LastSavedBaseGameVersion", "1.26.44")
                .putCompound("LastSavedDimensionHeightRange", oldHeightRange)
                .putInt("NeighborAwareBlockUpgradeVersion", 0)
                .putString("OriginalBaseGameVersion", "1.21.120")
                .putCompound("OriginalDimensionHeightRange", oldHeightRange)
                .putShort("Overworld1_18HeightExtended", (short) 0)
                .putShort("SkullFlatteningPerformed", (short) 0)
                .putShort("UnderwaterLavaLakeFixed", (short) 0)
                .putShort("WorldGenBelowZeroFixed", (short) 0)
                .build();

        IChunk chunk = mockChunk(DimensionEnum.OVERWORLD.getDimensionData(), 123);
        NbtMap updated = LevelChunkMetaDataFactory.updateForSave(metadata, chunk);

        assertEquals("1.21.120", updated.getString("OriginalBaseGameVersion"));
        assertEquals("1.17.40", updated.getString("BiomeBaseGameVersion"));
        assertEquals("Overworld", updated.getString("DimensionName"));
        assertEquals(987654321L, updated.getLong("GenerationSeed"));
        assertEquals(2, updated.getInt("GeneratorType"));
        assertEquals(oldHeightRange, updated.getCompound("OriginalDimensionHeightRange"));

        assertEquals(0, updated.getInt("NeighborAwareBlockUpgradeVersion"));
        assertEquals(0, updated.getShort("Overworld1_18HeightExtended"));
        assertEquals(0, updated.getShort("SkullFlatteningPerformed"));
        assertEquals(0, updated.getShort("UnderwaterLavaLakeFixed"));
        assertEquals(0, updated.getShort("WorldGenBelowZeroFixed"));

        assertEquals(NetworkConstants.CODEC.getMinecraftVersion(), updated.getString("LastSavedBaseGameVersion"));
        assertEquals(-64, updated.getCompound("LastSavedDimensionHeightRange").getShort("min"));
        assertEquals(320, updated.getCompound("LastSavedDimensionHeightRange").getShort("max"));
        assertFalse(updated.containsKey("BlendingVersion"));
    }

    private static IChunk mockChunk(DimensionData dimensionData, long seed) {
        IChunk chunk = Mockito.mock(IChunk.class);
        LevelProvider provider = Mockito.mock(LevelProvider.class);

        Mockito.when(chunk.getProvider()).thenReturn(provider);
        Mockito.when(provider.getDimensionData()).thenReturn(dimensionData);
        Mockito.when(provider.getSeed()).thenReturn(seed);
        return chunk;
    }
}
