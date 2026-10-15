package org.powernukkitx.migration.steps;

import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.WorldStorageMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.DynamicProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Converts legacy world-global storage into the canonical 3.1.0 LevelDB representation.
 *
 * @author Curse
 */
public final class WorldStorageV3_1_0Migration implements MigrationStep<WorldStorageMigrationData> {

    @Override
    public MigrationFormat format() {
        return MigrationFormat.WORLD_STORAGE;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public WorldStorageMigrationData migrate(MigrationContext context, WorldStorageMigrationData value) throws IOException {
        CompoundTag dynamicProperties = value.dynamicProperties();
        boolean dynamicPropertiesChanged = value.dynamicPropertiesChanged();
        CompoundTag legacyRoot = value.legacyDynamicPropertiesRoot();

        if (legacyRoot != null) {
            if (!legacyRoot.containsCompound(DynamicProperties.ROOT)) {
                throw new IOException("Legacy '~' Dynamic Properties record does not contain 'DynamicProperties'");
            }

            CompoundTag legacyDynamicProperties = legacyRoot.getCompound(DynamicProperties.ROOT);
            for (String groupId : legacyDynamicProperties.getTags().keySet()) {
                if (!(legacyDynamicProperties.get(groupId) instanceof CompoundTag)) {
                    throw new IOException("Legacy world Dynamic Properties group '" + groupId + "' is not a compound");
                }
            }

            if (dynamicProperties == null) {
                dynamicProperties = new CompoundTag();
            }

            for (String groupId : dynamicProperties.getTags().keySet()) {
                if (!(dynamicProperties.get(groupId) instanceof CompoundTag)) {
                    throw new IOException("Canonical world Dynamic Properties group '" + groupId + "' is not a compound");
                }
            }

            for (String groupId : legacyDynamicProperties.getTags().keySet()) {
                CompoundTag legacyGroup = legacyDynamicProperties.getCompound(groupId);
                if (!dynamicProperties.containsCompound(groupId)) {
                    dynamicProperties.putCompound(groupId, legacyGroup.copy());
                    continue;
                }

                CompoundTag group = dynamicProperties.getCompound(groupId);
                for (var property : legacyGroup.getTags().entrySet()) {
                    group.put(property.getKey(), property.getValue().copy());
                }
                dynamicProperties.putCompound(groupId, group);
            }

            dynamicPropertiesChanged = true;
        }

        CompoundTag portals = value.portals();
        boolean portalsChanged = value.portalsChanged();

        if (portals != null) {
            if (portals.contains("data") && !portals.containsCompound("data")) {
                throw new IOException("Invalid portals data compound");
            }

            CompoundTag data = portals.containsCompound("data") ? portals.getCompound("data") : new CompoundTag();
            ListTag<CompoundTag> records;
            boolean changed = !portals.containsCompound("data");

            if (data.contains("PortalRecords")) {
                if (!data.containsList("PortalRecords")) {
                    throw new IOException("Invalid PortalRecords tag");
                }

                ListTag<?> storedRecords = data.getList("PortalRecords");
                if (storedRecords.type != Tag.TAG_Compound) {
                    if (storedRecords.size() != 0) {
                        throw new IOException("PortalRecords contains non-compound entries");
                    }

                    records = new ListTag<>(Tag.TAG_Compound);
                    changed = true;
                } else {
                    records = data.getList("PortalRecords", CompoundTag.class);
                }
            } else {
                records = new ListTag<>(Tag.TAG_Compound);
                changed = true;
            }

            if (changed) {
                data.putList("PortalRecords", records);
                portals.putCompound("data", data);
                portalsChanged = true;
            }
        }

        Map<UUID, CompoundTag> tickingAreas = value.tickingAreas();
        boolean tickingAreasChanged = value.tickingAreasChanged();

        if (value.legacyTickingAreas() != null) {
            tickingAreas = tickingAreas == null ? new HashMap<>() : new HashMap<>(tickingAreas);
            Map<String, UUID> names = new HashMap<>();

            for (var entry : tickingAreas.entrySet()) {
                CompoundTag area = entry.getValue();
                if (area.containsString("Name") && area.containsInt("Dimension")) {
                    names.put(getTickingAreaNameKey(area.getInt("Dimension"), area.getString("Name")), entry.getKey());
                }
            }

            for (WorldStorageMigrationData.LegacyTickingArea legacyArea : value.legacyTickingAreas()) {
                CompoundTag migratedArea = migrateLegacyTickingArea(legacyArea);
                UUID uuid = getLegacyTickingAreaUuid(legacyArea);
                String nameKey = getTickingAreaNameKey(legacyArea.dimensionId(), legacyArea.name());
                UUID existingUuid = names.get(nameKey);

                if (existingUuid != null && !existingUuid.equals(uuid)) {
                    throw new IOException("Legacy ticking area name conflicts with canonical ticking area: " + legacyArea.name());
                }

                CompoundTag existing = tickingAreas.get(uuid);
                if (existing != null && (!existing.containsString("Name") || !legacyArea.name().equalsIgnoreCase(existing.getString("Name")))) {
                    throw new IOException("Legacy ticking area UUID conflicts with canonical ticking area: " + uuid);
                }

                tickingAreas.put(uuid, migratedArea);
                names.put(nameKey, uuid);
            }

            tickingAreasChanged = true;
        }

        CompoundTag biomeData = value.biomeData();
        boolean biomeDataChanged = value.biomeDataChanged();

        if (biomeData == null) {
            biomeData = createDefaultBiomeData();
            biomeDataChanged = biomeData != null;
        }

        long worldStartCount = value.worldStartCount();
        if (worldStartCount == 0) {
            worldStartCount = 0xffffffffL;
        }

        return new WorldStorageMigrationData(
                null,
                dynamicProperties,
                portals,
                biomeData,
                worldStartCount,
                value.legacyTickingAreas(),
                tickingAreas,
                dynamicPropertiesChanged,
                portalsChanged,
                biomeDataChanged,
                tickingAreasChanged
        );
    }

    private static CompoundTag createDefaultBiomeData() {
        ListTag<CompoundTag> entries = new ListTag<>();
        int[] biomeIds = Registries.BIOME.getBiomeDefinitions().stream()
                .mapToInt(definition -> Registries.BIOME.getBiomeId(
                        Registries.BIOME.getFromBiomeStringList(definition.first())))
                .sorted()
                .toArray();

        for (int biomeId : biomeIds) {
            var registeredBiome = Registries.BIOME.get(biomeId);
            if (registeredBiome == null) continue;

            var definition = registeredBiome.second();
            var chunkGenData = definition.getChunkGenData();
            float snowAccumulation = chunkGenData == null || chunkGenData.getClimate() == null
                    ? 0.0f
                    : chunkGenData.getClimate().getSnowAccumulationMin();
            float foliageSnow = definition.getFoliageSnow();

            if (snowAccumulation <= 0.0f && foliageSnow <= 0.0f) continue;

            CompoundTag entry = new CompoundTag().putShort("id", (short) biomeId);
            if (snowAccumulation > 0.0f) entry.putFloat("snowAccumulation", snowAccumulation);
            if (foliageSnow > 0.0f) entry.putFloat("foliageSnow", foliageSnow);
            entries.add(entry);
        }

        return entries.size() == 0 ? null : new CompoundTag().putList("list", entries);
    }

    private static CompoundTag migrateLegacyTickingArea(WorldStorageMigrationData.LegacyTickingArea area) throws IOException {
        if (area.name() == null || area.name().isEmpty()) {
            throw new IOException("Legacy ticking area has no name");
        }
        if (area.name().length() > 255) {
            throw new IOException("Legacy ticking area name cannot be longer than 255 characters: " + area.name());
        }
        if (area.levelName() == null || area.levelName().isEmpty()) {
            throw new IOException("Legacy ticking area '" + area.name() + "' has no level name");
        }
        if (area.chunks() == null || area.chunks().size() == 0) {
            throw new IOException("Legacy ticking area '" + area.name() + "' contains no chunks");
        }

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (WorldStorageMigrationData.LegacyTickingAreaChunk chunk : area.chunks()) {
            if (chunk == null) {
                throw new IOException("Legacy ticking area '" + area.name() + "' contains a null chunk");
            }
            minX = Math.min(minX, chunk.x());
            minZ = Math.min(minZ, chunk.z());
            maxX = Math.max(maxX, chunk.x());
            maxZ = Math.max(maxZ, chunk.z());
        }

        return new CompoundTag()
                .putInt("Dimension", area.dimensionId())
                .putByte("IsCircle", 0)
                .putInt("MaxX", maxX << 4)
                .putInt("MaxZ", maxZ << 4)
                .putInt("MinX", minX << 4)
                .putInt("MinZ", minZ << 4)
                .putString("Name", area.name())
                .putByte("Preload", 1);
    }

    private static String getTickingAreaNameKey(int dimensionId, String name) {
        return dimensionId + ":" + name.toLowerCase(Locale.ROOT);
    }

    private static UUID getLegacyTickingAreaUuid(WorldStorageMigrationData.LegacyTickingArea area) {
        String identity = "pnx-legacy-ticking-area:" + area.dimensionId() + ":" + area.levelName() + ":" + area.name();
        return UUID.nameUUIDFromBytes(identity.getBytes(StandardCharsets.UTF_8));
    }
}
