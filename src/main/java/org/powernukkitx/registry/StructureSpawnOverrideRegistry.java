package org.powernukkitx.registry;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.level.structure.StructureBoundingBoxType;
import org.powernukkitx.level.structure.spawn.SpawnCategory;
import org.powernukkitx.level.structure.spawn.SpawnOverrideState;
import org.powernukkitx.level.structure.spawn.StructureSpawnDefinition;
import org.powernukkitx.level.structure.spawn.StructureSpawnOverride;
import org.powernukkitx.level.structure.spawn.StructureSpawnerData;
import org.powernukkitx.level.structure.spawn.StructureSpawnerEntry;

import java.util.List;
import java.util.Map;

/**
 * Registry of structure-specific spawn category overrides.
 *
 * @author Curse
 */
public final class StructureSpawnOverrideRegistry implements IRegistry<String, StructureSpawnDefinition, StructureSpawnDefinition> {
    private final Object2ObjectOpenHashMap<String, StructureSpawnDefinition> registry = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {
        registerInternal("minecraft:fortress", fortressDefinition());
        registerInternal("minecraft:monument", monumentDefinition());
        registerInternal("minecraft:pillager_outpost", pillagerOutpostDefinition());
        registerInternal("minecraft:swamp_hut", swampHutDefinition());
        registerInternal("minecraft:village", villageDefinition());
    }

    private static StructureSpawnDefinition villageDefinition() {
        return new StructureSpawnDefinition(Map.of());
    }

    private static StructureSpawnDefinition swampHutDefinition() {
        return monsterPiece(
                spawner(
                        EntityID.WITCH,
                        "",
                        1,
                        1,
                        1,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        SpawnOverrideState.UNSET,
                        null,
                        new StructureSpawnerData.Brightness(0, 8, false)
                )
        );
    }

    private static StructureSpawnDefinition pillagerOutpostDefinition() {
        return monsterPiece(
                spawner(
                        EntityID.PILLAGER,
                        "",
                        1,
                        1,
                        2,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        new StructureSpawnerData.Population(7, 7),
                        new StructureSpawnerData.Brightness(0, 8, true)
                ),
                spawner(
                        EntityID.PILLAGER,
                        "minecraft:spawn_as_illager_captain",
                        1,
                        1,
                        1,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        new StructureSpawnerData.Population(1, 1),
                        new StructureSpawnerData.Brightness(0, 8, true)
                )
        );
    }

    private static StructureSpawnDefinition fortressDefinition() {
        return monsterPiece(
                spawner(
                        EntityID.ZOMBIE_PIGMAN,
                        "",
                        5,
                        2,
                        3,
                        SpawnOverrideState.UNSET,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        null,
                        null
                ),
                spawner(
                        EntityID.MAGMA_CUBE,
                        "",
                        3,
                        2,
                        3,
                        SpawnOverrideState.UNSET,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        null,
                        null
                ),
                spawner(
                        EntityID.BLAZE,
                        "",
                        10,
                        2,
                        3,
                        SpawnOverrideState.UNSET,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        null,
                        null
                ),
                spawner(
                        EntityID.SKELETON,
                        "",
                        2,
                        2,
                        3,
                        SpawnOverrideState.UNSET,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        null,
                        null
                ),
                spawner(
                        EntityID.WITHER_SKELETON,
                        "",
                        8,
                        2,
                        3,
                        SpawnOverrideState.UNSET,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.UNSET,
                        null,
                        null
                )
        );
    }

    private static StructureSpawnDefinition monumentDefinition() {
        return monsterPiece(
                spawner(
                        EntityID.GUARDIAN,
                        "",
                        1,
                        2,
                        4,
                        SpawnOverrideState.UNSET,
                        SpawnOverrideState.YES,
                        SpawnOverrideState.YES,
                        new StructureSpawnerData.Population(0, 40),
                        null
                )
        );
    }

    private static StructureSpawnDefinition monsterPiece(StructureSpawnerEntry... spawns) {
        return new StructureSpawnDefinition(Map.of(
                SpawnCategory.MONSTER,
                new StructureSpawnOverride(StructureBoundingBoxType.PIECE, List.of(spawns))
        ));
    }

    private static StructureSpawnerEntry spawner(
            String entityId,
            String initializationEvent,
            int probabilityWeight,
            int minCount,
            int maxCount,
            SpawnOverrideState surface,
            SpawnOverrideState underground,
            SpawnOverrideState underwater,
            @Nullable StructureSpawnerData.Population population,
            @Nullable StructureSpawnerData.Brightness brightness
    ) {
        return StructureSpawnerEntry.of(
                new StructureSpawnerData(
                        entityId,
                        initializationEvent,
                        probabilityWeight,
                        minCount,
                        maxCount,
                        surface,
                        underground,
                        underwater,
                        population,
                        brightness
                )
        );
    }

    @Override
    @Nullable
    public StructureSpawnDefinition get(String key) {
        return registry.get(key);
    }

    /**
     * Returns a category override for a structure type, or null when none exists.
     */
    @Nullable
    public StructureSpawnOverride get(String structureType, SpawnCategory category) {
        StructureSpawnDefinition definition = get(structureType);
        return definition == null ? null : definition.get(category);
    }

    @Override
    public void trim() {
        registry.trim();
    }

    @Override
    public void reload() {
        registry.clear();
    }

    @Override
    public void register(String key, StructureSpawnDefinition value) throws RegisterException {
        Preconditions.checkNotNull(key, "key");
        Preconditions.checkNotNull(value, "value");

        if (registry.putIfAbsent(key, value) != null) {
            throw new RegisterException("Structure spawn definition %s has already been registered", key);
        }
    }

    /**
     * Registers a built-in structure spawn definition.
     */
    @ApiStatus.Internal
    public void registerInternal(String key, StructureSpawnDefinition value) {
        try {
            register(key, value);
        } catch (RegisterException e) {
            throw new IllegalStateException(e);
        }
    }
}
