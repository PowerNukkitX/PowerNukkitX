package org.powernukkitx.migration.steps;

import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.StructureMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.IntArrayTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.network.NetworkConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        int formatVersion = root.getInt("format_version");

        if (formatVersion != 1 && formatVersion != 2) {
            throw new IOException("Unsupported structure format version in " + path);
        }

        CompoundTag structure = root.getCompound("structure");
        if (formatVersion == 2) {
            recoverLegacyActorOrigin(root, structure, path, true);
            rejectUnrecoverableActorOrigin(root, structure, path);
            return value;
        }

        if (!isLegacyPnx(structure)) return value;

        migratePalette(structure, path);
        normalizeBlockIndices(root, structure, path);
        normalizeSecondaryLayer(structure, path);
        migrateEntities(structure, path);
        recoverLegacyActorOrigin(root, structure, path, false);
        rejectUnrecoverableActorOrigin(root, structure, path);
        migrateBlockEntities(root, structure, path);

        root.putCompound("structure", structure);
        root.putInt("format_version", 2);
        return value;
    }

    private static boolean isLegacyPnx(CompoundTag structure) {
        if (structure.containsList("block_indices", Tag.TAG_List)) return true;
        CompoundTag palette = structure.getCompound("palette").getCompound("default");
        for (CompoundTag state : palette.getList("block_palette", CompoundTag.class).getAll()) {
            if (!state.containsInt("version")) return true;
        }
        for (Tag data : palette.getCompound("block_position_data").getAllTags()) {
            if (data instanceof CompoundTag entry && entry.containsString("id")) return true;
        }
        for (Tag data : structure.getList("entities").getAll()) {
            if (data instanceof CompoundTag entity && entity.containsString("uuid")) return true;
        }
        return false;
    }

    private static void migratePalette(CompoundTag structure, Path path) throws IOException {
        CompoundTag palette = structure.getCompound("palette").getCompound("default");
        if (!palette.containsList("block_palette", Tag.TAG_Compound)) {
            throw new IOException("Missing block_palette in " + path);
        }

        ListTag<CompoundTag> blockPalette = palette.getList("block_palette", CompoundTag.class);
        for (CompoundTag entry : blockPalette.getAll()) {
            if (!entry.containsString("name") || !entry.containsCompound("states")) {
                throw new IOException("Invalid block palette entry in " + path);
            }
            if (entry.contains("version") && !entry.containsInt("version")) {
                throw new IOException("Invalid block palette version in " + path);
            }

            String identifier = entry.getString("name");
            CompoundTag states = entry.getCompound("states");

            if (identifier.startsWith("minecraft:")) {
                if (identifier.endsWith("_stairs")
                        && states.containsInt("weirdo_direction")
                        && states.containsByte("upside_down_bit")
                        && !states.contains("minecraft:corner")) {
                    states.putString("minecraft:corner", "none");
                }

                boolean connections = identifier.endsWith("_fence")
                        || identifier.endsWith("_stained_glass_pane")
                        || identifier.equals("minecraft:glass_pane")
                        || identifier.equals("minecraft:iron_bars")
                        || identifier.endsWith("_copper_bars");

                if (connections) {
                    for (String direction : new String[]{"east", "north", "south", "west"}) {
                        String key = "minecraft:connection_" + direction;
                        if (!states.contains(key)) states.putByte(key, 0);
                    }
                }
            }

            entry.putCompound("states", states);
            if (!entry.contains("version")) {
                entry.putInt("version", NetworkConstants.BLOCK_STATE_VERSION_NO_REVISION);
            }
        }
    }

    private static void normalizeBlockIndices(CompoundTag root, CompoundTag structure, Path path) throws IOException {
        if (!root.containsList("size", Tag.TAG_Int) || !structure.containsList("block_indices")) {
            throw new IOException("Missing structure dimensions or block_indices in " + path);
        }

        ListTag<IntTag> size = root.getList("size", IntTag.class);
        if (size.size() != 3 || size.get(0).getData() <= 0
                || size.get(1).getData() <= 0 || size.get(2).getData() <= 0) {
            throw new IOException("Invalid structure dimensions in " + path);
        }

        int volume;
        try {
            volume = Math.multiplyExact(Math.multiplyExact(
                    size.get(0).getData(), size.get(1).getData()), size.get(2).getData());
        } catch (ArithmeticException e) {
            throw new IOException("Structure dimensions overflow in " + path, e);
        }

        ListTag<? extends Tag> source = structure.getList("block_indices");
        if (source.size() < 1 || source.size() > 2) {
            throw new IOException("Invalid block_indices layer count in " + path);
        }

        ListTag<IntArrayTag> converted = new ListTag<>(Tag.TAG_Int_Array);
        boolean legacy = false;

        for (Tag layer : source.getAll()) {
            int[] indices;

            if (layer instanceof IntArrayTag array) {
                indices = array.getData().clone();
            } else if (layer instanceof ListTag<?> list && list.type == Tag.TAG_Int) {
                indices = new int[list.size()];
                for (int i = 0; i < indices.length; i++) {
                    if (!(list.get(i) instanceof IntTag entry)) {
                        throw new IOException("Invalid legacy block index at " + i + " in " + path);
                    }
                    indices[i] = entry.getData();
                }
                legacy = true;
            } else {
                throw new IOException("Unsupported block_indices layer type in " + path);
            }

            if (indices.length != volume) {
                throw new IOException("Invalid block_indices length in " + path
                        + ": expected " + volume + ", got " + indices.length);
            }

            converted.add(new IntArrayTag(indices));
        }

        if (legacy) {
            structure.putList("block_indices", converted);
        } else if (!structure.containsList("block_indices", Tag.TAG_Int_Array)) {
            throw new IOException("Invalid block_indices element type in " + path);
        }
    }

    private static void normalizeSecondaryLayer(CompoundTag structure, Path path) throws IOException {
        if (!structure.containsList("block_indices", Tag.TAG_Int_Array)) {
            throw new IOException("Invalid block_indices type in " + path);
        }

        ListTag<IntArrayTag> layers = structure.getList("block_indices", IntArrayTag.class);
        if (layers.size() != 2) return;

        ListTag<CompoundTag> palette = structure.getCompound("palette").getCompound("default")
                .getList("block_palette", CompoundTag.class);
        int[] secondary = layers.get(1).getData().clone();
        boolean hasBlocks = false;

        for (int i = 0; i < secondary.length; i++) {
            int index = secondary[i];
            if (index == -1) continue;
            if (index < 0 || index >= palette.size()) {
                throw new IOException("Invalid secondary palette index " + index + " in " + path);
            }

            CompoundTag state = palette.get(index);
            if ("minecraft:air".equals(state.getString("name"))
                    && state.getCompound("states").isEmpty()) {
                secondary[i] = -1;
            } else {
                hasBlocks = true;
            }
        }

        if (hasBlocks) {
            layers.get(1).setData(secondary);
        } else {
            ListTag<IntArrayTag> compact = new ListTag<>(Tag.TAG_Int_Array);
            compact.add(new IntArrayTag(layers.get(0).getData().clone()));
            structure.putList("block_indices", compact);
        }
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
        if (sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) {
            throw new IOException("Invalid structure dimensions in " + path);
        }

        int volume = Math.multiplyExact(Math.multiplyExact(sizeX, sizeY), sizeZ);
        ListTag<IntArrayTag> blockIndices = structure.getList("block_indices", IntArrayTag.class);
        if (!structure.containsList("block_indices", Tag.TAG_Int_Array)
                || blockIndices.size() < 1 || blockIndices.size() > 2) {
            throw new IOException("Invalid block_indices in structure " + path);
        }

        int[] layer0 = blockIndices.get(0).getData();
        int[] layer1 = blockIndices.size() == 2 ? blockIndices.get(1).getData() : null;
        if (layer0.length != volume || layer1 != null && layer1.length != volume) {
            throw new IOException("Invalid block_indices size in structure " + path);
        }

        CompoundTag palette = structure.getCompound("palette");
        CompoundTag defaultPalette = palette.getCompound("default");
        ListTag<CompoundTag> blockPalette =
                defaultPalette.getList("block_palette", CompoundTag.class);

        for (int[] layer : new int[][]{layer0, layer1}) {
            if (layer == null) continue;
            for (int index : layer) {
                if (index < -1 || index >= blockPalette.size()) {
                    throw new IOException("Invalid palette index " + index + " in " + path);
                }
            }
        }

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
            int[] layer0,
            int[] layer1,
            ListTag<CompoundTag> blockPalette) {
        int paletteIndex = layer0[index];
        if (paletteIndex < 0 && layer1 != null) {
            paletteIndex = layer1[index];
        }

        if (paletteIndex < 0 || paletteIndex >= blockPalette.size()) {
            return null;
        }

        String identifier = blockPalette.get(paletteIndex).getString("name");
        return identifier.isEmpty() ? null : identifier;
    }

    private static void recoverLegacyActorOrigin(
            CompoundTag root, CompoundTag structure, Path path, boolean alreadyMigrated) {
        ListTag<IntTag> origin = root.getList("structure_world_origin", IntTag.class);
        ListTag<IntTag> size = root.getList("size", IntTag.class);
        if (origin.size() != 3 || size.size() != 3) return;
        if (origin.get(0).getData() != 0 || origin.get(1).getData() != 0 || origin.get(2).getData() != 0) return;

        int sx = size.get(0).getData(), sy = size.get(1).getData(), sz = size.get(2).getData();
        if (sx <= 0 || sy <= 0 || sz <= 0) return;

        ListTag<CompoundTag> actors = structure.getList("entities", CompoundTag.class);
        if (actors.size() == 0 || actorsWithin(actors, new Origin(0, 0, 0), sx, sy, sz)) return;

        if (!structure.containsList("block_indices", Tag.TAG_Int_Array)) return;
        ListTag<IntArrayTag> layers = structure.getList("block_indices", IntArrayTag.class);
        if (layers.size() == 0) return;

        int[] primary = layers.get(0).getData();
        ListTag<CompoundTag> blocks = structure.getCompound("palette").getCompound("default")
                .getList("block_palette", CompoundTag.class);
        CompoundTag positions = structure.getCompound("palette").getCompound("default")
                .getCompound("block_position_data");

        List<Origin> nests = new ArrayList<>();
        long volume = (long) sx * sy * sz;
        for (var entry : positions.getEntrySet()) {
            if (!(entry.getValue() instanceof CompoundTag data)) continue;

            String id = data.containsString("id")
                    ? data.getString("id") : data.getCompound("block_entity_data").getString("id");
            if (!"Beehive".equals(id)) continue;

            int index;
            try {
                index = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException ignored) {
                continue;
            }

            if (index < 0 || index >= volume || index >= primary.length) continue;
            int paletteIndex = primary[index];
            if (paletteIndex < 0 || paletteIndex >= blocks.size()) continue;

            String block = blocks.get(paletteIndex).getString("name");
            if (!"minecraft:bee_nest".equals(block) && !"minecraft:beehive".equals(block)) continue;

            int plane = sy * sz;
            nests.add(new Origin(index / plane, index % plane / sz, index % sz));
        }

        if (nests.size() == 0) return;

        Set<Origin> candidates = new HashSet<>();
        for (CompoundTag actor : actors.getAll()) {
            if (!"minecraft:bee".equals(actor.getString("identifier"))
                    || !actor.containsList("HomePos", Tag.TAG_Float)) continue;

            ListTag<FloatTag> home = actor.getList("HomePos", FloatTag.class);
            if (home.size() != 3) continue;

            float hx = home.get(0).getData(), hy = home.get(1).getData(), hz = home.get(2).getData();
            if (!Float.isFinite(hx) || !Float.isFinite(hy) || !Float.isFinite(hz)) continue;

            for (Origin nest : nests) {
                double x = (double) hx - nest.x();
                double y = (double) hy - nest.y();
                double z = (double) hz - nest.z();

                if (x != Math.rint(x) || y != Math.rint(y) || z != Math.rint(z)) continue;
                if (x < Integer.MIN_VALUE || x > Integer.MAX_VALUE
                        || y < Integer.MIN_VALUE || y > Integer.MAX_VALUE
                        || z < Integer.MIN_VALUE || z > Integer.MAX_VALUE) continue;

                Origin candidate = new Origin((int) x, (int) y, (int) z);
                if (actorsWithin(actors, candidate, sx, sy, sz)
                        && beeHomesMatch(actors, nests, candidate)) {
                    candidates.add(candidate);
                }
            }
        }

        if (candidates.size() != 1) return;

        Origin recovered = candidates.iterator().next();
        root.putList("structure_world_origin", new ListTag<IntTag>(Tag.TAG_Int)
                .add(new IntTag(recovered.x()))
                .add(new IntTag(recovered.y()))
                .add(new IntTag(recovered.z())));

        if (alreadyMigrated) {
            for (var entry : positions.getEntrySet()) {
                if (!(entry.getValue() instanceof CompoundTag data)
                        || !data.containsCompound("block_entity_data")) continue;

                int index;
                try {
                    index = Integer.parseInt(entry.getKey());
                } catch (NumberFormatException ignored) {
                    continue;
                }
                if (index < 0 || index >= volume) continue;

                int plane = sy * sz;
                int lx = index / plane, ly = index % plane / sz, lz = index % sz;
                CompoundTag blockEntity = data.getCompound("block_entity_data");
                if (!blockEntity.containsInt("x") || !blockEntity.containsInt("y")
                        || !blockEntity.containsInt("z")) continue;

                if (blockEntity.getInt("x") == lx && blockEntity.getInt("y") == ly
                        && blockEntity.getInt("z") == lz) {
                    blockEntity.putInt("x", recovered.x() + lx);
                    blockEntity.putInt("y", recovered.y() + ly);
                    blockEntity.putInt("z", recovered.z() + lz);
                }
            }
        }
    }

    private static boolean actorsWithin(
            ListTag<CompoundTag> actors, Origin origin, int sx, int sy, int sz) {
        for (CompoundTag actor : actors.getAll()) {
            if (!actor.containsList("Pos", Tag.TAG_Float)) return false;
            ListTag<FloatTag> pos = actor.getList("Pos", FloatTag.class);
            if (pos.size() != 3) return false;

            float x = pos.get(0).getData(), y = pos.get(1).getData(), z = pos.get(2).getData();
            if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) return false;
            if (x < origin.x() || x >= (double) origin.x() + sx
                    || y < origin.y() || y >= (double) origin.y() + sy
                    || z < origin.z() || z >= (double) origin.z() + sz) return false;
        }
        return true;
    }

    private static boolean beeHomesMatch(
            ListTag<CompoundTag> actors, List<Origin> nests, Origin origin) {
        for (CompoundTag actor : actors.getAll()) {
            if (!"minecraft:bee".equals(actor.getString("identifier"))
                    || !actor.containsList("HomePos", Tag.TAG_Float)) continue;

            ListTag<FloatTag> home = actor.getList("HomePos", FloatTag.class);
            if (home.size() != 3) return false;

            double hx = home.get(0).getData(), hy = home.get(1).getData(), hz = home.get(2).getData();
            boolean matches = false;

            for (Origin nest : nests) {
                if (hx == (double) origin.x() + nest.x()
                        && hy == (double) origin.y() + nest.y()
                        && hz == (double) origin.z() + nest.z()) {
                    matches = true;
                    break;
                }
            }
            if (!matches) return false;
        }
        return true;
    }

    private static void rejectUnrecoverableActorOrigin(CompoundTag root, CompoundTag structure, Path path)
            throws IOException {
        ListTag<IntTag> origin = root.getList("structure_world_origin", IntTag.class);
        ListTag<IntTag> size = root.getList("size", IntTag.class);
        if (origin.size() != 3 || size.size() != 3) {
            throw new IOException("Invalid structure origin or dimensions in " + path);
        }

        Origin source = new Origin(origin.get(0).getData(), origin.get(1).getData(), origin.get(2).getData());
        if (source.x() != 0 || source.y() != 0 || source.z() != 0) return;

        ListTag<CompoundTag> actors = structure.getList("entities", CompoundTag.class);
        if (actors.size() == 0) return;

        int sx = size.get(0).getData(), sy = size.get(1).getData(), sz = size.get(2).getData();
        if (sx <= 0 || sy <= 0 || sz <= 0) {
            throw new IOException("Invalid structure dimensions in " + path);
        }

        if (!actorsWithin(actors, source, sx, sy, sz)) {
            throw new UnrecoverableActorOriginException(path);
        }
    }

    /**
     * Indicates that a legacy structure has actor positions but no recoverable capture origin.
     *
     * @author Curse
     */
    public static final class UnrecoverableActorOriginException extends IOException {
        public UnrecoverableActorOriginException(Path path) {
            super("Missing or incorrect legacy actor capture origin: " + path);
        }
    }

    private record Origin(int x, int y, int z) {}

    private static void validateCanonicalFloatList(
            CompoundTag tag, String name, int expectedSize, Path path) throws IOException {
        if (!tag.containsList(name, Tag.TAG_Float) || tag.getList(name).size() != expectedSize) {
            throw new IOException("Structure entity has invalid canonical " + name + " in " + path);
        }
    }
}
