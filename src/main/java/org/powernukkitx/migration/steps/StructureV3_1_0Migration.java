package org.powernukkitx.migration.steps;

import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.StructureMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts legacy PNX structure NBT to the canonical 3.1.0 structure representation.
 *
 * @author Curse
 */
public final class StructureV3_1_0Migration implements MigrationStep<StructureMigrationData> {
    private static final LevelDBBlockEntityV3_1_0Migration.LegacyStorageMigrationContext STRUCTURE_CONTEXT =
            new LevelDBBlockEntityV3_1_0Migration.LegacyStorageMigrationContext() {
                @Override
                public boolean discardLegacyHomeWithoutDimension() {
                    return true;
                }
            };

    @Override
    public MigrationFormat format() {
        return MigrationFormat.STRUCTURE;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public StructureMigrationData migrate(MigrationContext context, StructureMigrationData value) throws IOException {
        CompoundTag root = value.root();
        Path path = value.path();

        if (root.getInt("format_version") != 1) {
            throw new IOException("Unsupported structure format version in " + path);
        }

        CompoundTag structure = root.getCompound("structure");
        migrateEntities(structure, path);
        migrateBlockEntities(root, structure, path);
        root.putCompound("structure", structure);

        return value;
    }

    private static void migrateEntities(CompoundTag structure, Path path) throws IOException {
        ListTag<? extends Tag> entities = structure.getList("entities");
        for (Tag value : entities.getAll()) {
            if (!(value instanceof CompoundTag entity)) {
                throw new IOException("Invalid entity entry in structure " + path);
            }

            LevelDBActorV3_1_0Migration.migrateLegacyPnxActor(entity);
            validateCanonicalFloatList(entity, "Pos", 3, path);
            if (entity.contains("Motion")) {
                validateCanonicalFloatList(entity, "Motion", 3, path);
            }

            validateCanonicalFloatList(entity, "Rotation", 2, path);
        }
    }

    private static void migrateBlockEntities(CompoundTag root, CompoundTag structure, Path path)
            throws IOException {
        ListTag<IntTag> size = root.getList("size", IntTag.class);
        ListTag<IntTag> origin = root.getList("structure_world_origin", IntTag.class);
        if (size.size() != 3 || origin.size() != 3) {
            throw new IOException("Invalid structure dimensions in " + path);
        }

        int sizeX = size.get(0).getData();
        int sizeY = size.get(1).getData();
        int sizeZ = size.get(2).getData();
        int originX = origin.get(0).getData();
        int originY = origin.get(1).getData();
        int originZ = origin.get(2).getData();
        int volume = sizeX * sizeY * sizeZ;
        ListTag<ListTag> blockIndices = structure.getList("block_indices", ListTag.class);
        if (blockIndices.size() != 2) {
            throw new IOException("Invalid block_indices in structure " + path);
        }

        @SuppressWarnings("unchecked")
        ListTag<IntTag> layer0 = (ListTag<IntTag>) blockIndices.get(0);
        @SuppressWarnings("unchecked")
        ListTag<IntTag> layer1 = (ListTag<IntTag>) blockIndices.get(1);
        if (layer0.size() != volume || layer1.size() != volume) {
            throw new IOException("Invalid block_indices size in structure " + path);
        }

        CompoundTag palette = structure.getCompound("palette");
        CompoundTag defaultPalette = palette.getCompound("default");
        ListTag<CompoundTag> blockPalette =
                defaultPalette.getList("block_palette", CompoundTag.class);
        CompoundTag blockPositionData = defaultPalette.getCompound("block_position_data");
        List<String> indices = new ArrayList<>(blockPositionData.getTags().keySet());
        for (String indexName : indices) {
            int index;
            try {
                index = Integer.parseInt(indexName);
            } catch (NumberFormatException e) {
                throw new IOException(
                        "Invalid block_position_data index " + indexName + " in structure " + path,
                        e);
            }

            if (index < 0 || index >= volume) {
                throw new IOException(
                        "Out-of-range block_position_data index "
                                + index
                                + " in structure "
                                + path);
            }

            int localX = index / (sizeY * sizeZ);
            int remaining = index % (sizeY * sizeZ);
            int localY = remaining / sizeZ;
            int localZ = remaining % sizeZ;
            CompoundTag entry = blockPositionData.getCompound(indexName);
            CompoundTag blockEntity;
            if (entry.containsCompound("block_entity_data")) {
                blockEntity = entry.getCompound("block_entity_data");
            } else if (entry.containsString("id")) {
                blockEntity = entry.copy();
                entry = new CompoundTag().putCompound("block_entity_data", blockEntity);
            } else {
                continue;
            }

            blockEntity.putInt("x", originX + localX);
            blockEntity.putInt("y", originY + localY);
            blockEntity.putInt("z", originZ + localZ);
            String blockIdentifier = resolveBlockIdentifier(index, layer0, layer1, blockPalette);
            LevelDBBlockEntityV3_1_0Migration.LegacyBlockEntityMigrationResult result =
                    LevelDBBlockEntityV3_1_0Migration.migrateLegacyBlockEntity(
                            blockEntity, blockIdentifier, STRUCTURE_CONTEXT);
            if (result == LevelDBBlockEntityV3_1_0Migration.LegacyBlockEntityMigrationResult.REMOVE) {
                entry.remove("block_entity_data");
                if (entry.isEmpty()) {
                    blockPositionData.remove(indexName);
                } else {
                    blockPositionData.putCompound(indexName, entry);
                }

                continue;
            }

            entry.putCompound("block_entity_data", blockEntity);
            blockPositionData.putCompound(indexName, entry);
        }

        defaultPalette.putCompound("block_position_data", blockPositionData);
        palette.putCompound("default", defaultPalette);
        structure.putCompound("palette", palette);
    }

    private static String resolveBlockIdentifier(
            int index,
            ListTag<IntTag> layer0,
            ListTag<IntTag> layer1,
            ListTag<CompoundTag> blockPalette) {
        int paletteIndex = layer0.get(index).getData();
        if (paletteIndex < 0) {
            paletteIndex = layer1.get(index).getData();
        }

        if (paletteIndex < 0 || paletteIndex >= blockPalette.size()) {
            return null;
        }

        String identifier = blockPalette.get(paletteIndex).getString("name");
        return identifier.isEmpty() ? null : identifier;
    }

    private static void validateCanonicalFloatList(
            CompoundTag tag, String name, int expectedSize, Path path) throws IOException {
        if (!tag.containsList(name, Tag.TAG_Float) || tag.getList(name).size() != expectedSize) {
            throw new IOException("Structure entity has invalid canonical " + name + " in " + path);
        }
    }
}
