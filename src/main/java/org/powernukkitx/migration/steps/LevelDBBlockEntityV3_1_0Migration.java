package org.powernukkitx.migration.steps;

import org.powernukkitx.blockentity.BlockEntityLodestone;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.DyeColor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * Converts legacy PNX Block Entity storage to the BDS-compatible 3.1.0 representation.
 *
 * @author Curse
 */
@Slf4j
public final class LevelDBBlockEntityV3_1_0Migration implements MigrationStep<LevelDBBlockEntityV3_1_0Migration.Data> {
    private static final int LEGACY_CAMPFIRE_COOK_TIME = 600;

    @Override
    public MigrationFormat format() {
        return MigrationFormat.BLOCK_ENTITY;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public Data migrate(MigrationContext context, Data value) throws IOException {
        List<CompoundTag> blockEntities = value.blockEntities();
        boolean changed = value.changed();
        var iterator = blockEntities.iterator();

        while (iterator.hasNext()) {
            CompoundTag blockEntity = iterator.next();

            if (context.hasLegacyPositionTrackingRemap()) {
                PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                        blockEntity,
                        context.getLegacyPositionTrackingSourceLastId(),
                        context.getLegacyPositionTrackingOffset()
                );
            }

            String blockIdentifier = value.chunk().getBlockState(
                    blockEntity.getInt("x") & 0x0f,
                    blockEntity.getInt("y"),
                    blockEntity.getInt("z") & 0x0f
            ).getIdentifier();

            LegacyBlockEntityMigrationResult result = migrateLegacyBlockEntity(blockEntity, blockIdentifier, value.migrationContext());

            if (result == LegacyBlockEntityMigrationResult.REMOVE) {
                iterator.remove();
                changed = true;
                continue;
            }

            if (result == LegacyBlockEntityMigrationResult.CHANGED) {
                changed = true;
            }
        }

        Set<Position> occupiedBlockEntityPositions = new HashSet<>();
        for (CompoundTag blockEntity : blockEntities) {
            occupiedBlockEntityPositions.add(
                    new Position(
                            blockEntity.getInt("x"),
                            blockEntity.getInt("y"),
                            blockEntity.getInt("z")
                    )
            );
        }

        Set<Position> netherPortalBlocks = new HashSet<>(value.netherPortalBlocks());

        for (ChunkSection section : value.chunk().getSections()) {
            if (section == null || section.isEmpty()) continue;

            int sectionBaseY = section.y() << 4;

            for (int blockIndex = 0; blockIndex < ChunkSection.SIZE; blockIndex++) {
                String blockIdentifier = section.blockLayer()[0].get(blockIndex).getIdentifier();
                boolean sculkCatalyst = "minecraft:sculk_catalyst".equals(blockIdentifier);
                boolean copperGolemStatue = isCopperGolemStatueBlock(blockIdentifier);
                boolean endPortal = "minecraft:end_portal".equals(blockIdentifier);
                boolean endGateway = "minecraft:end_gateway".equals(blockIdentifier);
                boolean sporeBlossom = "minecraft:spore_blossom".equals(blockIdentifier);
                boolean netherPortal = "minecraft:portal".equals(blockIdentifier);

                if (!sculkCatalyst && !copperGolemStatue && !endPortal && !endGateway && !sporeBlossom && !netherPortal) continue;

                int x = (value.chunk().getX() << 4) + ((blockIndex >> 8) & 0x0f);
                int y = sectionBaseY + (blockIndex & 0x0f);
                int z = (value.chunk().getZ() << 4) + ((blockIndex >> 4) & 0x0f);
                Position position = new Position(x, y, z);

                if (netherPortal) {
                    netherPortalBlocks.add(position);
                    continue;
                }

                if (!occupiedBlockEntityPositions.add(position)) continue;

                if (endPortal) {
                    blockEntities.add(createEndPortalBlockEntity(x, y, z));
                } else if (endGateway) {
                    blockEntities.add(createEndGatewayBlockEntity(x, y, z));
                } else if (sporeBlossom) {
                    blockEntities.add(createSporeBlossomBlockEntity(x, y, z));
                } else if (sculkCatalyst) {
                    blockEntities.add(createSculkCatalystBlockEntity(x, y, z));
                } else {
                    blockEntities.add(createCopperGolemStatueBlockEntity(x, y, z));
                }

                changed = true;
            }
        }

        return new Data(
                blockEntities,
                value.chunk(),
                value.migrationContext(),
                changed,
                netherPortalBlocks
        );
    }

    /**
     * Migrates one legacy Block Entity compound and reports whether it changed or should be removed.
     */
    public static LegacyBlockEntityMigrationResult migrateLegacyBlockEntity(
            CompoundTag tag,
            String blockIdentifier,
            LegacyStorageMigrationContext migrationContext
    ) throws IOException {
        String expectedBlockIdentifier =
                switch (tag.getString("id")) {
                    case "Target" -> "minecraft:target";
                    case "NetherReactor" -> "minecraft:netherreactor";
                    default -> null;
                };

        if (expectedBlockIdentifier != null && expectedBlockIdentifier.equals(blockIdentifier)) {
            return LegacyBlockEntityMigrationResult.REMOVE;
        }

        boolean changed = migrateBlockEntity(tag, blockIdentifier, migrationContext);
        return changed ? LegacyBlockEntityMigrationResult.CHANGED : LegacyBlockEntityMigrationResult.UNCHANGED;
    }

    private static boolean migrateBlockEntity(
            CompoundTag tag,
            String blockIdentifier,
            LegacyStorageMigrationContext migrationContext
    ) throws IOException {
        String id = tag.getString("id");
        boolean changed = false;

        /*
         * BlockEntityVersion is part of the current BDS-compatible
         * block-entity base storage format.
         *
         * Add it when it did not exist in legacy PNX data.
         *
         * If a pre-existing/custom value exists, preserve it here rather
         * than overwriting plugin data during migration.
         */
        if (!tag.contains("BlockEntityVersion")) {
            tag.putInt("BlockEntityVersion", 0);
            changed = true;
        }

        /*
         * Legacy PNX injected lowercase isMovable into every block entity
         * from the BlockEntity base class.
         *
         * BDS does not persist this as generic block-entity state.
         *
         * PistonArm is the confirmed exception and its isMovable field must
         * be preserved exactly.
         */
        if (!"PistonArm".equals(id) && tag.contains("isMovable")) {
            tag.remove("isMovable");
            changed = true;
        }

        boolean schemaChanged =
                switch (id) {
                    case "Bed", "Bell", "DaylightDetector" -> false;
                    case "Banner" -> migrateBanner(tag);
                    case "Beacon" -> migrateBeacon(tag);
                    case "Campfire" -> migrateCampfire(tag);
                    case "Comparator" -> migrateComparator(tag);
                    case "Conduit" -> migrateConduit(tag);
                    case "EndPortal" -> migrateEndPortal(tag);
                    case "EndGateway" -> migrateEndGateway(tag, migrationContext);
                    case "EnderChest" -> migrateEnderChest(tag);
                    case "SporeBlossom" -> migrateSporeBlossom(tag);
                    case "FlowerPot" -> migrateFlowerPot(tag);
                    case "Lodestone" -> migrateLodestone(tag, migrationContext);
                    case "Chest", "Barrel", "ShulkerBox" -> migrateFindableContainer(tag);
                    case "Dispenser", "Dropper", "Hopper", "BrewingStand" -> migrateItemContainer(tag);
                    case "Crafter" -> migrateCrafter(tag);
                    case "ChiseledBookshelf" -> migrateChiseledBookshelf(tag);
                    case "Shelf" -> migrateShelf(tag);
                    case "Jukebox" -> migrateJukebox(tag);
                    case "DecoratedPot" -> migrateDecoratedPot(tag);
                    case "SculkSensor", "CalibratedSculkSensor", "SculkShrieker" -> migrateVibrationListener(tag, id);
                    case "Beehive" -> migrateBeehive(tag, migrationContext);
                    case "TrialSpawner" -> migrateTrialSpawner(tag);
                    case "Furnace", "BlastFurnace", "Smoker" -> migrateFurnace(tag);
                    case "MobSpawner" -> migrateMobSpawner(tag);
                    case "Music" -> migrateMusic(tag);
                    case "PotentSulfur" -> migratePotentSulfur(tag);
                    case "Cauldron" -> migrateCauldron(tag);
                    case "Lectern" -> migrateLectern(tag);
                    case "ItemFrame", "GlowItemFrame" -> migrateItemFrame(tag);
                    case "PistonArm" -> migratePistonArm(tag);
                    case "MovingBlock" -> migrateMovingBlock(tag, migrationContext);
                    case "BrushableBlock" -> migrateBrushableBlock(tag, blockIdentifier);
                    case "CommandBlock" -> migrateCommandBlock(tag);
                    case "EnchantTable" -> migrateEnchantTable(tag);
                    case "Sign", "HangingSign" -> migrateSign(tag);
                    case "CreakingHeart" -> migrateCreakingHeart(tag);
                    case "CopperGolemStatue" -> migrateCopperGolemStatue(tag);
                    case "Vault" -> migrateVault(tag, migrationContext);
                    case "StructureBlock" -> migrateStructureBlock(tag);
                    case "Skull" -> migrateSkull(tag);

                    default -> false;
                };

        return changed || schemaChanged;
    }

    private static boolean migrateLodestone(
            CompoundTag tag,
            LegacyStorageMigrationContext migrationContext
    ) throws IOException {
        boolean hasTrackingHandle = tag.contains("trackingHandle");
        boolean hasTrackingHandler = tag.contains("trackingHandler");

        if (!hasTrackingHandle && !hasTrackingHandler) {
            return false;
        }

        if (hasTrackingHandle && !tag.containsNumber("trackingHandle")) {
            throw new IOException("Lodestone has invalid trackingHandle at "
                    + tag.getInt("x") + "," + tag.getInt("y") + "," + tag.getInt("z"));
        }

        if (hasTrackingHandler && !tag.containsNumber("trackingHandler")) {
            throw new IOException("Lodestone has invalid trackingHandler at "
                    + tag.getInt("x") + "," + tag.getInt("y") + "," + tag.getInt("z"));
        }

        int trackingHandle = hasTrackingHandle ? tag.getInt("trackingHandle") : 0;
        int trackingHandler = hasTrackingHandler ? tag.getInt("trackingHandler") : 0;

        if (trackingHandle < 0 || trackingHandler < 0) {
            throw new IOException("Lodestone has negative tracking handle at "
                    + tag.getInt("x") + "," + tag.getInt("y") + "," + tag.getInt("z"));
        }

        if (trackingHandle > 0 && trackingHandler > 0 && trackingHandle != trackingHandler) {
            throw new IOException("Lodestone trackingHandle/trackingHandler conflict at "
                    + tag.getInt("x") + "," + tag.getInt("y") + "," + tag.getInt("z"));
        }

        int resolvedTrackingHandle = trackingHandle > 0 ? trackingHandle : trackingHandler;

        if (resolvedTrackingHandle > 0) {
            CompoundTag extraData = migrationContext.getPnxExtraData();
            if (extraData == null) {
                throw new IOException("No PNX extra-data destination for Lodestone tracking handle");
            }

            BlockEntityLodestone.setPnxTrackingHandle(
                    extraData,
                    tag.getInt("x"),
                    tag.getInt("y"),
                    tag.getInt("z"),
                    resolvedTrackingHandle
            );
        }

        tag.remove("trackingHandle");
        tag.remove("trackingHandler");
        return true;
    }

    private static boolean migrateCopperGolemStatue(CompoundTag tag) {
        if (tag.contains("Pose") && !tag.containsNumber("Pose")) {
            logSkippedBlockEntityMigration(tag, "unexpected CopperGolemStatue Pose tag type");
            return false;
        }

        if (tag.contains("Actor") && !tag.containsCompound("Actor")) {
            logSkippedBlockEntityMigration(tag, "unexpected CopperGolemStatue Actor tag type");
            return false;
        }

        CompoundTag actor = tag.containsCompound("Actor") ? tag.getCompound("Actor") : null;

        if (actor != null && actor.contains("ActorIdentifier") && !actor.containsString("ActorIdentifier")) {
            logSkippedBlockEntityMigration(tag, "unexpected CopperGolemStatue ActorIdentifier tag type");
            return false;
        }

        if (actor != null && actor.contains("SaveData") && !actor.containsCompound("SaveData")) {
            logSkippedBlockEntityMigration(tag, "unexpected CopperGolemStatue SaveData tag type");
            return false;
        }

        boolean changed = false;
        int pose = tag.containsNumber("Pose") ? Math.max(0, Math.min(3, tag.getInt("Pose"))) : 0;

        if (!tag.containsInt("Pose") || tag.getInt("Pose") != pose) {
            tag.putInt("Pose", pose);
            changed = true;
        }

        if (actor == null) {
            actor = new CompoundTag();
            tag.putCompound("Actor", actor);
            changed = true;
        }

        if (!"minecraft:copper_golem<>".equals(actor.getString("ActorIdentifier"))) {
            actor.putString("ActorIdentifier", "minecraft:copper_golem<>");
            changed = true;
        }

        if (!actor.containsCompound("SaveData")) {
            actor.putCompound("SaveData", new CompoundTag());
            changed = true;
        }

        return changed;
    }

    private static boolean migrateEndPortal(CompoundTag tag) {
        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        replaceBlockEntityNbt(tag, createEndPortalBlockEntity(x, y, z));
        return true;
    }

    private static boolean migrateSporeBlossom(CompoundTag tag) {
        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        replaceBlockEntityNbt(tag, createSporeBlossomBlockEntity(x, y, z));
        return true;
    }

    private static boolean migrateEndGateway(
            CompoundTag tag,
            LegacyStorageMigrationContext migrationContext
    ) throws IOException {
        if (tag.contains("Age") && !tag.containsNumber("Age")) {
            throw new IOException("EndGateway has invalid Age at "
                    + tag.getInt("x") + "," + tag.getInt("y") + "," + tag.getInt("z"));
        }

        if (tag.contains("ExitPortal") && !tag.containsList("ExitPortal", Tag.TAG_Int)) {
            throw new IOException("EndGateway has invalid ExitPortal at "
                    + tag.getInt("x") + "," + tag.getInt("y") + "," + tag.getInt("z"));
        }

        int age = tag.containsNumber("Age") ? tag.getInt("Age") : 0;
        int exitX = 0;
        int exitY = 0;
        int exitZ = 0;

        if (tag.containsList("ExitPortal", Tag.TAG_Int)) {
            ListTag<IntTag> exitPortal = tag.getList("ExitPortal", IntTag.class);

            if (exitPortal.size() != 3) {
                throw new IOException("EndGateway has invalid ExitPortal size at "
                        + tag.getInt("x") + "," + tag.getInt("y") + "," + tag.getInt("z"));
            }

            exitX = exitPortal.get(0).getData();
            exitY = exitPortal.get(1).getData();
            exitZ = exitPortal.get(2).getData();

            if ((exitX != 0 || exitY != 0 || exitZ != 0)
                    && !migrationContext.hasPersistedEndGatewayNear(exitX, exitZ)) {
                exitX = 0;
                exitY = 0;
                exitZ = 0;
            }
        }

        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        replaceBlockEntityNbt(
                tag,
                createEndGatewayBlockEntity(x, y, z, age, exitX, exitY, exitZ)
        );
        return true;
    }

    private static void replaceBlockEntityNbt(CompoundTag tag, CompoundTag canonical) {
        for (String name : tag.getTags().keySet()) {
            tag.remove(name);
        }

        tag.putAll(canonical);
    }

    private static CompoundTag createEndPortalBlockEntity(int x, int y, int z) {
        return new CompoundTag()
                .putInt("BlockEntityVersion", 0)
                .putString("id", "EndPortal")
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);
    }

    private static CompoundTag createEndGatewayBlockEntity(int x, int y, int z) {
        return createEndGatewayBlockEntity(x, y, z, 0, 0, 0, 0);
    }

    private static CompoundTag createEndGatewayBlockEntity(
            int x,
            int y,
            int z,
            int age,
            int exitX,
            int exitY,
            int exitZ
    ) {
        return new CompoundTag()
                .putInt("Age", age)
                .putInt("BlockEntityVersion", 0)
                .putByte("EndGatewayBadPosChecked", (byte) 1)
                .putList("ExitPortal", new ListTag<IntTag>(Tag.TAG_Int).add(new IntTag(exitX)).add(new IntTag(exitY)).add(new IntTag(exitZ)))
                .putString("id", "EndGateway")
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);
    }

    private static CompoundTag createSporeBlossomBlockEntity(int x, int y, int z) {
        return new CompoundTag()
                .putInt("BlockEntityVersion", 0)
                .putString("id", "SporeBlossom")
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);
    }

    private static CompoundTag createSculkCatalystBlockEntity(int x, int y, int z) {
        return new CompoundTag()
                .putInt("BlockEntityVersion", 0)
                .putString("id", "SculkCatalyst")
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);
    }

    private static boolean isCopperGolemStatueBlock(String blockIdentifier) {
        return switch (blockIdentifier) {
            case "minecraft:copper_golem_statue",
                 "minecraft:exposed_copper_golem_statue",
                 "minecraft:weathered_copper_golem_statue",
                 "minecraft:oxidized_copper_golem_statue",
                 "minecraft:waxed_copper_golem_statue",
                 "minecraft:waxed_exposed_copper_golem_statue",
                 "minecraft:waxed_weathered_copper_golem_statue",
                 "minecraft:waxed_oxidized_copper_golem_statue" -> true;

            default -> false;
        };
    }

    private static CompoundTag createCopperGolemStatueBlockEntity(int x, int y, int z) {
        return new CompoundTag()
                .putCompound(
                        "Actor",
                        new CompoundTag()
                                .putString("ActorIdentifier", "minecraft:copper_golem<>")
                                .putCompound("SaveData", new CompoundTag())
                )
                .putInt("BlockEntityVersion", 0)
                .putInt("Pose", 0)
                .putString("id", "CopperGolemStatue")
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);
    }

    private static boolean migrateCreakingHeart(CompoundTag tag) {
        if (tag.contains("Cooldown") && !tag.containsNumber("Cooldown")) {
            logSkippedBlockEntityMigration(tag, "unexpected CreakingHeart Cooldown tag type");
            return false;
        }

        if (tag.contains("SpawnedCreakingID") && !tag.containsNumber("SpawnedCreakingID")) {
            logSkippedBlockEntityMigration(tag, "unexpected CreakingHeart SpawnedCreakingID tag type");
            return false;
        }

        boolean changed = false;

        if (!tag.containsInt("Cooldown")) {
            tag.putInt("Cooldown", tag.containsNumber("Cooldown") ? tag.getInt("Cooldown") : 0);
            changed = true;
        }

        if (tag.contains("SpawnedCreakingID") && !(tag.get("SpawnedCreakingID") instanceof LongTag)) {
            tag.putLong("SpawnedCreakingID", tag.getLong("SpawnedCreakingID"));
            changed = true;
        }

        return changed;
    }

    private static boolean migrateVault(
            CompoundTag tag,
            LegacyStorageMigrationContext migrationContext
    ) throws IOException {
        if (!tag.containsCompound("server_data")) {
            return false;
        }

        if (tag.contains("config") && !tag.containsCompound("config")) {
            logSkippedBlockEntityMigration(tag, "unexpected Vault config tag type");
            return false;
        }

        CompoundTag config = tag.getCompound("config");
        CompoundTag serverData = tag.getCompound( "server_data");

        if (tag.contains("data") && !tag.containsCompound("data")) {
            logSkippedBlockEntityMigration(tag, "unexpected Vault data tag type");
            return false;
        }

        CompoundTag legacyData = tag.getCompound("data");

        if (serverData.contains("rewarded_players") && !serverData.containsList("rewarded_players")) {
            logSkippedBlockEntityMigration(tag, "unexpected Vault rewarded_players tag type");
            return false;
        }

        if (serverData.contains("state_updating_resumes_at") && !serverData.containsNumber("state_updating_resumes_at")) {
            logSkippedBlockEntityMigration(tag, "unexpected Vault state_updating_resumes_at tag type");
            return false;
        }

        if (serverData.contains("items_to_eject") && !serverData.containsList("items_to_eject")) {
            logSkippedBlockEntityMigration(tag, "unexpected Vault items_to_eject tag type");
            return false;
        }

        ListTag<LongTag> rewardedPlayers = new ListTag<>();

        for (StringTag rewardedPlayer : serverData.getList("rewarded_players", StringTag.class).getAll()) {
            UUID uuid;

            try {
                uuid = UUID.fromString(rewardedPlayer.data);
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid legacy Vault rewarded player UUID: " + rewardedPlayer.data, e);
            }

            long playerUniqueId = migrationContext.resolvePlayerUniqueId(uuid);

            if (playerUniqueId != 0L) {
                rewardedPlayers.add(new LongTag(playerUniqueId));
            }
        }

        String lootTable = config.getString("loot_table");

        if (lootTable.isEmpty() || "minecraft:chests/trial_chambers/reward".equals(lootTable) || "chests/trial_chambers/reward".equals(lootTable)) {
            config.putString("loot_table", "loot_tables/chests/trial_chambers/reward.json");
        }

        config.putFloat("activation_range", config.containsNumber("activation_range") ? config.getFloat("activation_range") : 4.0f);
        config.putFloat("deactivation_range", config.containsNumber("deactivation_range") ? config.getFloat("deactivation_range") : 4.5f);

        if (config.containsCompound("key_item")) {
            migrateStorageItem(config.getCompound("key_item"));
        } else {
            config.putCompound(
                    "key_item",
                    new CompoundTag()
                            .putByte(
                                    "Count",
                                    1
                            )
                            .putShort(
                                    "Damage",
                                    0
                            )
                            .putString(
                                    "Name",
                                    "minecraft:trial_key"
                            )
                            .putByte(
                                    "WasPickedUp",
                                    0
                            )
            );
        }

        serverData.putList("rewarded_players", rewardedPlayers);

        if (!serverData.contains("state_updating_resumes_at")) {
            serverData.putLong("state_updating_resumes_at", 0L);
        } else if (!(serverData.get("state_updating_resumes_at") instanceof LongTag)) {
            serverData.putLong("state_updating_resumes_at", serverData.getLong("state_updating_resumes_at"));
        }

        if (!serverData.contains("items_to_eject")) {
            serverData.putList("items_to_eject", new ListTag<CompoundTag>());
        } else {
            for (CompoundTag item : serverData.getList("items_to_eject", CompoundTag.class).getAll()) {
                migrateStorageItem(item);
            }
        }

        if (legacyData.contains("display_item")) {
            if (!legacyData.containsCompound("display_item")) {
                logSkippedBlockEntityMigration(tag, "unexpected Vault display_item tag type");
                return false;
            }

            CompoundTag displayItem = legacyData.getCompound("display_item");

            if (isDiscardableEmptyStorageItem(displayItem)) {
                serverData.remove("display_item");
            } else {
                migrateStorageItem(displayItem);
                serverData.putCompound("display_item", displayItem);
            }
        }

        /*
         * Legacy PNX persisted this private/runtime counter.
         * It is absent from the authoritative BDS LevelDB Vault schema.
         */
        serverData.remove("total_ejections_needed");

        /*
         * Legacy PNX:
         *
         *     server_data = persistent state
         *     data        = shared state
         *
         * BDS:
         *
         *     data = persistent Vault state
         *
         * display_item was migrated above because it is canonical
         * persistent BDS state. Legacy connected_players is transient.
         */
        tag.putCompound("data", serverData);
        tag.remove("server_data");

        return true;
    }

    private static boolean migrateStructureBlock(CompoundTag tag) {
        if (tag.contains("data") && !tag.containsNumber("data")) {
            logSkippedBlockEntityMigration(tag, "unexpected StructureBlock data tag type");
            return false;
        }

        if (tag.contains("redstoneSaveMode") && !tag.containsNumber("redstoneSaveMode")) {
            logSkippedBlockEntityMigration(tag, "unexpected StructureBlock redstoneSaveMode tag type");
            return false;
        }

        if (tag.contains("lastTouchedPlayerId") && !(tag.get("lastTouchedPlayerId") instanceof LongTag)) {
            logSkippedBlockEntityMigration(tag, "unexpected StructureBlock lastTouchedPlayerId tag type");
            return false;
        }

        boolean changed = false;

        if (!tag.containsInt("data")) {
            tag.putInt("data", tag.containsNumber("data") ? tag.getInt("data") : 0);
            changed = true;
        }

        if (!tag.containsInt("redstoneSaveMode")) {
            tag.putInt("redstoneSaveMode", tag.containsNumber("redstoneSaveMode" ) ? tag.getInt("redstoneSaveMode") : 0);
            changed = true;
        }

        if (!tag.contains("lastTouchedPlayerId")) {
            tag.putLong("lastTouchedPlayerId", 0L);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateBanner(CompoundTag tag) {
        if (tag.contains("Base")) {
            if (!tag.containsInt("Base")) {
                logSkippedBlockEntityMigration(tag, "unexpected Banner Base tag type");
                return false;
            }

            if (!tag.contains("color")) return false;

            tag.remove("color");
            return true;
        }

        if (!tag.contains("color")) {
            logSkippedBlockEntityMigration(tag, "Banner has neither Base nor legacy color");
            return false;
        }

        if (!tag.containsByte("color")) {
            logSkippedBlockEntityMigration(tag, "unexpected Banner color tag type");
            return false;
        }

        tag.putInt("Base", DyeColor.getByWoolData(tag.getByte("color")).getDyeData() & 0x0f);
        tag.remove("color");
        return true;
    }

    private static boolean migrateBeacon(CompoundTag tag) {
        if (tag.contains("primary") && !tag.containsInt("primary")) {
            logSkippedBlockEntityMigration(tag, "unexpected Beacon primary tag type");
            return false;
        }

        if (tag.contains("secondary") && !tag.containsInt("secondary")) {
            logSkippedBlockEntityMigration(tag, "unexpected Beacon secondary tag type");
            return false;
        }

        if (!tag.containsInt("primary") && tag.contains("Primary") && !tag.containsInt("Primary")) {
            logSkippedBlockEntityMigration(tag, "unexpected Beacon Primary tag type");
            return false;
        }

        if (!tag.containsInt("secondary")
                && tag.contains("Secondary")
                && !tag.containsInt("Secondary")) {
            logSkippedBlockEntityMigration(tag, "unexpected Beacon Secondary tag type");
            return false;
        }

        if (tag.contains("Levels") && !tag.containsInt("Levels")) {
            logSkippedBlockEntityMigration(tag, "unexpected Beacon Levels tag type");
            return false;
        }

        boolean changed = false;
        if (!tag.containsInt("primary")) {
            tag.putInt("primary", tag.containsInt("Primary") ? tag.getInt("Primary") : 0);
            changed = true;
        }

        if (!tag.containsInt("secondary")) {
            tag.putInt("secondary", tag.containsInt("Secondary") ? tag.getInt("Secondary") : 0);
            changed = true;
        }

        if (tag.contains("Primary")) {
            tag.remove("Primary");
            changed = true;
        }

        if (tag.contains("Secondary")) {
            tag.remove("Secondary");
            changed = true;
        }

        if (tag.contains("Levels")) {
            tag.remove("Levels");
            changed = true;
        }

        if (tag.containsString("Lock") && tag.getString("Lock").isEmpty()) {
            tag.remove("Lock");
            changed = true;
        }

        return changed;
    }

    private static boolean migrateCampfire(CompoundTag tag) {
        for (int i = 1; i <= 4; i++) {
            String itemKey = "Item" + i;
            String itemTimeKey = "ItemTime" + i;
            String keepItemKey = "KeepItem" + i;
            if (tag.contains(itemKey) && !tag.containsCompound(itemKey)) {
                logSkippedBlockEntityMigration(tag, "unexpected Campfire " + itemKey + " tag type");
                return false;
            }

            if (tag.contains(itemTimeKey) && !tag.containsInt(itemTimeKey)) {
                logSkippedBlockEntityMigration(
                        tag, "unexpected Campfire " + itemTimeKey + " tag type");
                return false;
            }

            if (tag.contains(keepItemKey) && !tag.containsByte(keepItemKey)) {
                logSkippedBlockEntityMigration(
                        tag, "unexpected Campfire " + keepItemKey + " tag type");
                return false;
            }

            if (tag.containsInt(itemTimeKey)) {
                int legacyTime = tag.getInt(itemTimeKey);
                if (legacyTime < 0 || legacyTime > LEGACY_CAMPFIRE_COOK_TIME) {
                    logSkippedBlockEntityMigration(
                            tag, "unexpected Campfire " + itemTimeKey + " value " + legacyTime);
                    return false;
                }
            }

            if (tag.containsCompound(itemKey)) {
                CompoundTag item = tag.getCompound(itemKey);
                if (!validateStorageItem(tag, item, itemKey)) {
                    return false;
                }
            }
        }

        boolean changed = false;
        for (int i = 1; i <= 4; i++) {
            String itemKey = "Item" + i;
            String itemTimeKey = "ItemTime" + i;
            String keepItemKey = "KeepItem" + i;
            boolean hasItem = tag.containsCompound(itemKey);
            if (hasItem) {
                CompoundTag item = tag.getCompound(itemKey);
                if (isDiscardableEmptyStorageItem(item)) {
                    tag.remove(itemKey);
                    hasItem = false;
                    changed = true;
                } else {
                    changed |= migrateStorageItem(item);
                }
            }

            int canonicalTime = 0;
            if (hasItem) {
                boolean keepItem = tag.containsByte(keepItemKey) && tag.getBoolean(keepItemKey);
                int legacyTime = tag.containsInt(itemTimeKey) ? tag.getInt(itemTimeKey) : 0;
                if (keepItem) {
                    canonicalTime = LEGACY_CAMPFIRE_COOK_TIME;
                } else if (legacyTime > 0) {
                    canonicalTime = LEGACY_CAMPFIRE_COOK_TIME - legacyTime;
                }
            }

            if (!tag.containsInt(itemTimeKey) || tag.getInt(itemTimeKey) != canonicalTime) {
                tag.putInt(itemTimeKey, canonicalTime);
                changed = true;
            }

            if (tag.contains(keepItemKey)) {
                tag.remove(keepItemKey);
                changed = true;
            }
        }

        return changed;
    }

    private static boolean migrateComparator(CompoundTag tag) {
        if (tag.contains("Items")) {
            if (!tag.containsList("Items")) {
                logSkippedBlockEntityMigration(tag, "unexpected Comparator Items tag type");
                return false;
            }

            ListTag<?> items = tag.getList("Items");
            if (!items.getAll().isEmpty()) {
                /*
                 * The legacy PNX specimen only carries an empty Items list
                 * here. A non-empty list may belong to plugin/custom state,
                 * so do not touch this block entity instance.
                 */
                logSkippedBlockEntityMigration(tag, "non-empty Comparator Items tag");
                return false;
            }
        }

        boolean changed = false;
        if (tag.containsList("Items")) {
            tag.remove("Items");
            changed = true;
        }

        return changed;
    }

    private static boolean migrateConduit(CompoundTag tag) {
        if (!tag.contains("IsMovable")) {
            return false;
        }

        tag.remove("IsMovable");
        return true;
    }

    private static boolean migrateEnderChest(CompoundTag tag) {
        if (tag.contains("Findable") && !tag.containsByte("Findable")) {
            logSkippedBlockEntityMigration(tag, "unexpected EnderChest Findable tag type");
            return false;
        }

        if (tag.contains("Items")) {
            if (!tag.containsList("Items")) {
                logSkippedBlockEntityMigration(tag, "unexpected EnderChest Items tag type");
                return false;
            }

            ListTag<?> items = tag.getList("Items");
            if (!items.getAll().isEmpty()) {
                logSkippedBlockEntityMigration(tag, "non-empty EnderChest Items tag");
                return false;
            }
        }

        boolean changed = false;
        if (!tag.contains("Findable")) {
            tag.putByte("Findable", 0);
            changed = true;
        }

        if (!tag.contains("Items")) {
            tag.putList("Items", new ListTag<>());
            changed = true;
        }

        return changed;
    }

    private static boolean migrateFlowerPot(CompoundTag tag) {
        if (!tag.contains("PlantBlock")) {
            return false;
        }

        if (!tag.containsCompound("PlantBlock")) {
            logSkippedBlockEntityMigration(tag, "unexpected FlowerPot PlantBlock tag type");
            return false;
        }

        CompoundTag plantBlock = tag.getCompound("PlantBlock");
        if (!plantBlock.containsString("name")) {
            logSkippedBlockEntityMigration(tag, "FlowerPot PlantBlock is missing canonical name");
            return false;
        }

        if (!plantBlock.containsCompound("states")) {
            logSkippedBlockEntityMigration(tag, "FlowerPot PlantBlock is missing canonical states");
            return false;
        }

        if (!plantBlock.containsInt("version")) {
            logSkippedBlockEntityMigration(
                    tag, "FlowerPot PlantBlock is missing canonical version");
            return false;
        }

        boolean changed = false;
        if (plantBlock.contains("itemId")) {
            plantBlock.remove("itemId");
            changed = true;
        }

        if (plantBlock.contains("itemMeta")) {
            plantBlock.remove("itemMeta");
            changed = true;
        }

        return changed;
    }

    private static boolean migrateFindableContainer(CompoundTag tag) {
        if (tag.contains("Findable") && !tag.containsByte("Findable")) {
            logSkippedBlockEntityMigration(tag, "unexpected Findable tag type");
            return false;
        }

        if (!validateStorageItemList(tag, "Items")) {
            return false;
        }

        boolean changed = migrateStorageItemList(tag, "Items", true);
        if (!tag.contains("Findable")) {
            tag.putByte("Findable", 0);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateItemContainer(CompoundTag tag) {
        if (!validateStorageItemList(tag, "Items")) {
            return false;
        }

        return migrateStorageItemList(tag, "Items", true);
    }

    private static boolean migrateCrafter(CompoundTag tag) {
        if (tag.contains("disabled_slots") && !tag.containsShort("disabled_slots")) {
            logSkippedBlockEntityMigration(tag, "unexpected Crafter disabled_slots tag type");
            return false;
        }

        if (!tag.contains("disabled_slots") && tag.contains("disabledSlots") && !tag.containsShort("disabledSlots")) {
            logSkippedBlockEntityMigration(tag, "unexpected Crafter disabledSlots tag type");
            return false;
        }

        if (!validateStorageItemList(tag, "Items")) {
            return false;
        }

        boolean changed = migrateStorageItemList(tag, "Items", true);
        if (tag.contains("disabledSlots")) {
            if (!tag.contains("disabled_slots")) {
                tag.putShort("disabled_slots", tag.getShort("disabledSlots"));
            }

            tag.remove("disabledSlots");
            changed = true;
        }

        if (!tag.contains("disabled_slots")) {
            tag.putShort("disabled_slots", 0);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateShelf(CompoundTag tag) {
        if (!validateStorageItemList(tag, "Items")) return false;
        if (!tag.containsList("Items")) return false;

        ListTag<CompoundTag> items = tag.getList("Items", CompoundTag.class);
        if (items.size() == 0) {
            tag.remove("Items");
            return true;
        }

        if (items.size() > 3) {
            logSkippedBlockEntityMigration(tag, "Shelf contains more than three item entries");
            return false;
        }

        CompoundTag[] slots = new CompoundTag[3];
        for (int i = 0; i < items.size(); i++) {
            CompoundTag item = items.get(i);
            if (!item.containsByte("Slot")) {
                logSkippedBlockEntityMigration(tag, "Shelf legacy item is missing Slot");
                return false;
            }

            int slot = item.getByte("Slot") & 0xff;
            if (slot >= 3) {
                logSkippedBlockEntityMigration(tag, "Shelf contains invalid Slot " + slot);
                return false;
            }

            if (slots[slot] != null) {
                logSkippedBlockEntityMigration(tag, "Shelf contains duplicate Slot " + slot);
                return false;
            }

            migrateStorageItem(item);
            item.remove("Slot");
            slots[slot] = isDiscardableEmptyStorageItem(item) ? createEmptyStorageItem() : item;
        }

        boolean hasItems = false;
        for (CompoundTag item : slots) {
            if (item != null && !isDiscardableEmptyStorageItem(item)) {
                hasItems = true;
                break;
            }
        }

        if (!hasItems) {
            tag.remove("Items");
            return true;
        }

        ListTag<CompoundTag> canonicalItems = new ListTag<>();
        for (int slot = 0; slot < 3; slot++) {
            canonicalItems.add(slots[slot] != null ? slots[slot] : createEmptyStorageItem());
        }

        tag.putList("Items", canonicalItems);
        return true;
    }

    private static boolean migrateChiseledBookshelf(CompoundTag tag) {
        if (tag.contains("LastInteractedSlot") && !tag.containsInt("LastInteractedSlot")) {
            logSkippedBlockEntityMigration(tag, "unexpected ChiseledBookshelf LastInteractedSlot tag type");
            return false;
        }

        if (!validateStorageItemList(tag, "Items")) {
            return false;
        }

        if (tag.containsList("Items") && tag.getList("Items").size() > 6) {
            logSkippedBlockEntityMigration(tag, "ChiseledBookshelf contains more than six item entries");
            return false;
        }

        boolean changed = migrateStorageItemList(tag, "Items", false);
        if (!tag.contains("LastInteractedSlot") && tag.containsList("Items")) {
            ListTag<CompoundTag> items = tag.getList("Items", CompoundTag.class);
            boolean allEmpty = true;
            for (CompoundTag item : items.getAll()) {
                if (!isDiscardableEmptyStorageItem(item)) {
                    allEmpty = false;
                    break;
                }
            }

            if (allEmpty) {
                tag.remove("Items");
                changed = true;
            }
        }

        return changed;
    }

    private static boolean migrateJukebox(CompoundTag tag) {
        if (!tag.contains("RecordItem")) {
            return false;
        }

        if (!tag.containsCompound("RecordItem")) {
            logSkippedBlockEntityMigration(tag, "unexpected Jukebox RecordItem tag type");
            return false;
        }

        CompoundTag item = tag.getCompound("RecordItem");
        if (!validateStorageItem(tag, item, "RecordItem")) {
            return false;
        }

        if (isDiscardableEmptyStorageItem(item)) {
            tag.remove("RecordItem");
            return true;
        }

        return migrateStorageItem(item);
    }

    private static boolean migrateDecoratedPot(CompoundTag tag) {
        if (tag.contains("animation") && !tag.containsByte("animation")) {
            logSkippedBlockEntityMigration(tag, "unexpected DecoratedPot animation tag type");
            return false;
        }

        boolean hasLegacyItem = tag.contains("Item");
        boolean hasCanonicalItem = tag.contains("item");
        if (!hasCanonicalItem && hasLegacyItem && !tag.containsCompound("Item")) {
            logSkippedBlockEntityMigration(tag, "unexpected DecoratedPot Item tag type");
            return false;
        }

        if (hasCanonicalItem && !tag.containsCompound("item")) {
            logSkippedBlockEntityMigration(tag, "unexpected DecoratedPot item tag type");
            return false;
        }

        CompoundTag legacyItem = !hasCanonicalItem && hasLegacyItem ? tag.getCompound("Item") : null;
        CompoundTag canonicalItem = hasCanonicalItem ? tag.getCompound("item") : null;

        if (legacyItem != null && !validateStorageItem(tag, legacyItem, "Item")) return false;
        if (canonicalItem != null && !validateStorageItem(tag, canonicalItem, "item")) return false;

        boolean changed = false;
        CompoundTag item = null;
        boolean legacyName = false;
        if (canonicalItem != null) {
            item = canonicalItem;
            if (hasLegacyItem) {
                tag.remove("Item");
                changed = true;
            }
        } else if (legacyItem != null) {
            item = legacyItem;
            legacyName = true;
        }

        if (item == null) {
            item = createEmptyStorageItem();
            tag.putCompound("item", item);
            changed = true;
        } else {
            changed |= migrateStorageItem(item);
            String name = item.getString("Name");
            if (name.isEmpty() || "minecraft:air".equals(name)) {
                item.putByte("Count", 0);
                item.putShort("Damage", 0);
                item.putString("Name", "");
                item.putByte("WasPickedUp", 0);
                if (item.containsCompound("Block")
                        && "minecraft:air".equals(item.getCompound("Block").getString("name"))) {
                    item.remove("Block");
                }

                changed = true;
            }

            if (legacyName) {
                tag.remove("Item");
                tag.putCompound("item", item);
                changed = true;
            }
        }

        if (!tag.contains("animation")) {
            tag.putByte("animation", 0);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateBeehive(
            CompoundTag tag,
            LegacyStorageMigrationContext migrationContext
    ) throws IOException {
        if (tag.contains("ShouldSpawnBees") && !tag.containsByte("ShouldSpawnBees")) {
            logSkippedBlockEntityMigration(tag, "unexpected Beehive ShouldSpawnBees tag type");
            return false;
        }

        if (tag.contains("Occupants") && !tag.containsList("Occupants", Tag.TAG_Compound)) {
            logSkippedBlockEntityMigration(tag, "unexpected Beehive Occupants tag type");
            return false;
        }

        boolean changed = false;
        if (!tag.contains("ShouldSpawnBees")) {
            tag.putByte("ShouldSpawnBees", 0);
            changed = true;
        }

        if (!tag.containsList("Occupants", Tag.TAG_Compound)) {
            return changed;
        }

        ListTag<CompoundTag> occupants = tag.getList("Occupants", CompoundTag.class);
        if (occupants.getAll().isEmpty()) {
            tag.remove("Occupants");
            return true;
        }

        for (CompoundTag occupant : occupants.getAll()) {
            if (!occupant.containsString("ActorIdentifier")
                    || !occupant.containsInt("TicksLeftToStay")
                    || !occupant.containsCompound("SaveData")) {
                logSkippedBlockEntityMigration(tag, "unexpected Beehive occupant schema");
                return false;
            }

            if (occupant.contains("HasNectar") && !occupant.containsNumber("HasNectar")) {
                logSkippedBlockEntityMigration(tag, "unexpected Beehive occupant HasNectar tag type");
                return false;
            }

            CompoundTag saveData = occupant.getCompound("SaveData");
            boolean hasLegacyHome =
                    saveData.contains("HomeHiveX")
                            || saveData.contains("HomeHiveY")
                            || saveData.contains("HomeHiveZ");
            boolean hasCompleteLegacyHome =
                    saveData.containsInt("HomeHiveX")
                            && saveData.containsInt("HomeHiveY")
                            && saveData.containsInt("HomeHiveZ");
            boolean hasCanonicalHome =
                    saveData.containsList("HomePos", Tag.TAG_Float)
                            && saveData.getList("HomePos").size() == 3
                            && saveData.containsInt("HomeDimensionId");
            if (hasLegacyHome
                    && !hasCompleteLegacyHome
                    && !hasCanonicalHome
                    && !migrationContext.discardLegacyHomeWithoutDimension()) {
                logSkippedBlockEntityMigration(tag, "incomplete Beehive occupant HomeHive coordinates");
                return false;
            }
        }

        for (CompoundTag occupant : occupants.getAll()) {
            String actorIdentifier = occupant.getString("ActorIdentifier");
            boolean legacyOccupant =
                    !actorIdentifier.endsWith("<>")
                            || occupant.contains("WorkSound")
                            || occupant.contains("WorkSoundPitch")
                            || occupant.contains("HasNectar")
                            || occupant.contains("Muted");
            if (!actorIdentifier.endsWith("<>")) {
                occupant.putString("ActorIdentifier", actorIdentifier + "<>");
                changed = true;
            }

            CompoundTag saveData = occupant.getCompound("SaveData");
            if (saveData.contains("IntProperties")
                    || saveData.contains("FloatProperties")
                    || saveData.contains("HomeHiveX")) {
                legacyOccupant = true;
            }

            if (legacyOccupant && !saveData.isEmpty()) {
                LevelDBActorV3_1_0Migration.migrateLegacyPnxActor(saveData);
                long uniqueId = migrationContext.nextActorUniqueId();
                if (uniqueId != 0) {
                    saveData.putLong("UniqueID", uniqueId);
                    changed = true;
                }

                changed |= migrateBeeSaveDataProperties(saveData, occupant);
                boolean hasLegacyHome =
                        saveData.contains("HomeHiveX")
                                || saveData.contains("HomeHiveY")
                                || saveData.contains("HomeHiveZ");
                boolean hasCompleteLegacyHome =
                        saveData.containsInt("HomeHiveX")
                                && saveData.containsInt("HomeHiveY")
                                && saveData.containsInt("HomeHiveZ");
                boolean hasCanonicalHome =
                        saveData.containsList("HomePos", Tag.TAG_Float)
                                && saveData.getList("HomePos").size() == 3
                                && saveData.containsInt("HomeDimensionId");
                Integer dimensionId = migrationContext.getDimensionId();
                if (hasCompleteLegacyHome && !hasCanonicalHome && dimensionId != null) {
                    ListTag<FloatTag> homePos = new ListTag<>();
                    homePos.add(new FloatTag(saveData.getInt("HomeHiveX")));
                    homePos.add(new FloatTag(saveData.getInt("HomeHiveY")));
                    homePos.add(new FloatTag(saveData.getInt("HomeHiveZ")));
                    saveData.putList("HomePos", homePos);
                    saveData.putInt("HomeDimensionId", dimensionId);
                    changed = true;
                }

                if (hasLegacyHome
                        && (hasCanonicalHome
                                || dimensionId != null
                                || migrationContext.discardLegacyHomeWithoutDimension())) {
                    saveData.remove("HomeHiveX", "HomeHiveY", "HomeHiveZ");
                    changed = true;
                }
            }

            if (occupant.contains("WorkSound")
                    || occupant.contains("WorkSoundPitch")
                    || occupant.contains("HasNectar")
                    || occupant.contains("Muted")) {
                occupant.remove("WorkSound", "WorkSoundPitch", "HasNectar", "Muted");
                changed = true;
            }
        }

        return changed;
    }

    private static boolean migrateBeeSaveDataProperties(
            CompoundTag saveData,
            CompoundTag occupant
    ) {
        if (!occupant.containsNumber("HasNectar")) return false;

        CompoundTag properties =
                saveData.containsCompound("properties")
                        ? saveData.getCompound("properties")
                        : new CompoundTag();
        properties.putByte("minecraft:has_nectar", occupant.getBoolean("HasNectar") ? 1 : 0);
        saveData.putCompound("properties", properties);
        return true;
    }

    private static boolean migrateTrialSpawner(CompoundTag tag) {
        boolean legacy =
                tag.contains("SpawnRange")
                        || tag.contains("RequiredPlayerRange")
                        || tag.contains("TypeId")
                        || tag.contains("ticks_between_spawn")
                        || tag.contains("target_cooldown_length")
                        || tag.contains("total_mobs")
                        || tag.contains("total_mobs_added_per_player")
                        || tag.contains("simultaneous_mobs")
                        || tag.contains("simultaneous_mobs_added_per_player")
                        || tag.contains("spawn_baby")
                        || tag.contains("next_ominous_projectile_tick")
                        || tag.contains("ominous_lingering_potion")
                        || tag.contains("ominous_projectile_kind");
        if (!legacy) {
            return false;
        }

        if (tag.contains("normal_config")
                && !tag.containsCompound("normal_config")
                && !tag.containsString("normal_config")) {
            logSkippedBlockEntityMigration(tag, "unexpected TrialSpawner normal_config tag type");
            return false;
        }

        if (tag.contains("ominous_config")
                && !tag.containsCompound("ominous_config")
                && !tag.containsString("ominous_config")) {
            logSkippedBlockEntityMigration(tag, "unexpected TrialSpawner ominous_config tag type");
            return false;
        }

        if (tag.contains("spawn_data") && !tag.containsCompound("spawn_data")) {
            logSkippedBlockEntityMigration(tag, "unexpected TrialSpawner spawn_data tag type");
            return false;
        }

        boolean needsLegacyConfig =
                !tag.contains("normal_config") || !tag.contains("ominous_config");
        if (needsLegacyConfig && tag.contains("SpawnRange") && !tag.containsNumber("SpawnRange")) {
            logSkippedBlockEntityMigration(tag, "unexpected TrialSpawner SpawnRange tag type");
            return false;
        }

        if (!tag.contains("required_player_range")
                && tag.contains("RequiredPlayerRange")
                && !tag.containsNumber("RequiredPlayerRange")) {
            logSkippedBlockEntityMigration(
                    tag, "unexpected TrialSpawner RequiredPlayerRange tag type");
            return false;
        }

        if (!tag.contains("spawn_data")
                && tag.contains("TypeId")
                && !tag.containsString("TypeId")) {
            logSkippedBlockEntityMigration(tag, "unexpected TrialSpawner TypeId tag type");
            return false;
        }

        boolean changed = false;
        if (!tag.contains("normal_config")) {
            tag.putCompound("normal_config", createLegacyTrialSpawnerConfig(tag));
            changed = true;
        }

        if (!tag.contains("ominous_config")) {
            tag.putCompound("ominous_config", createLegacyTrialSpawnerConfig(tag));
            changed = true;
        }

        if (!tag.contains("required_player_range")) {
            tag.putInt(
                    "required_player_range",
                    tag.containsNumber("RequiredPlayerRange")
                            ? tag.getInt("RequiredPlayerRange")
                            : 14);
            changed = true;
        }

        if (!tag.contains("spawn_data")) {
            String typeId = tag.getString("TypeId");
            if (!typeId.isEmpty()) {
                tag.putCompound(
                        "spawn_data",
                        new CompoundTag().putString("TypeId", typeId).putInt("Weight", 1));
                changed = true;
            }
        }

        if (!tag.contains("cooldown_end_at")) {
            tag.putLong("cooldown_end_at", 0L);
            changed = true;
        }

        if (!tag.contains("next_mob_spawns_at")) {
            tag.putLong("next_mob_spawns_at", 0L);
            changed = true;
        }

        if (!tag.contains("total_mobs_spawned")) {
            tag.putInt("total_mobs_spawned", 0);
            changed = true;
        }

        if (!tag.contains("current_mobs")) {
            tag.putList("current_mobs", new ListTag<>());
            changed = true;
        }

        if (!tag.contains("registered_players")) {
            tag.putList("registered_players", new ListTag<>());
            changed = true;
        }

        if (tag.contains("SpawnRange")
                || tag.contains("RequiredPlayerRange")
                || tag.contains("TypeId")
                || tag.contains("ticks_between_spawn")
                || tag.contains("target_cooldown_length")
                || tag.contains("total_mobs")
                || tag.contains("total_mobs_added_per_player")
                || tag.contains("simultaneous_mobs")
                || tag.contains("simultaneous_mobs_added_per_player")
                || tag.contains("spawn_baby")
                || tag.contains("next_ominous_projectile_tick")
                || tag.contains("ominous_lingering_potion")
                || tag.contains("ominous_projectile_kind")) {
            tag.remove(
                    "SpawnRange",
                    "RequiredPlayerRange",
                    "TypeId",
                    "ticks_between_spawn",
                    "target_cooldown_length",
                    "total_mobs",
                    "total_mobs_added_per_player",
                    "simultaneous_mobs",
                    "simultaneous_mobs_added_per_player",
                    "spawn_baby",
                    "next_ominous_projectile_tick",
                    "ominous_lingering_potion",
                    "ominous_projectile_kind");
            changed = true;
        }

        return changed;
    }

    private static CompoundTag createLegacyTrialSpawnerConfig(CompoundTag tag) {
        ListTag<CompoundTag> lootTables = new ListTag<>();
        lootTables.add(
                new CompoundTag()
                        .putString("data", "loot_tables/spawners/trial_chamber/key.json")
                        .putInt("weight", 1));
        lootTables.add(
                new CompoundTag()
                        .putString("data", "loot_tables/spawners/trial_chamber/consumables.json")
                        .putInt("weight", 1));
        return new CompoundTag()
                .putString(
                        "items_to_drop_when_ominous",
                        "loot_tables/spawners/trial_chamber/items_to_drop_when_ominous.json")
                .putList("loot_tables_to_eject", lootTables)
                .putFloat(
                        "simultaneous_mobs",
                        tag.containsNumber("simultaneous_mobs")
                                ? tag.getFloat("simultaneous_mobs")
                                : 2.0f)
                .putFloat(
                        "simultaneous_mobs_added_per_player",
                        tag.containsNumber("simultaneous_mobs_added_per_player")
                                ? tag.getFloat("simultaneous_mobs_added_per_player")
                                : 1.0f)
                .putInt(
                        "spawn_range",
                        tag.containsNumber("SpawnRange") ? tag.getInt("SpawnRange") : 4)
                .putInt(
                        "target_cooldown_length",
                        tag.containsNumber("target_cooldown_length")
                                ? tag.getInt("target_cooldown_length")
                                : 36000)
                .putInt(
                        "ticks_between_spawn",
                        tag.containsNumber("ticks_between_spawn")
                                ? tag.getInt("ticks_between_spawn")
                                : 40)
                .putFloat(
                        "total_mobs",
                        tag.containsNumber("total_mobs") ? tag.getFloat("total_mobs") : 6.0f)
                .putFloat(
                        "total_mobs_added_per_player",
                        tag.containsNumber("total_mobs_added_per_player")
                                ? tag.getFloat("total_mobs_added_per_player")
                                : 2.0f);
    }

    private static boolean migrateFurnace(CompoundTag tag) {
        boolean legacy = tag.contains("MaxTime") || tag.contains("StoredXpInt");
        if (!legacy) {
            return false;
        }

        if (tag.contains("MaxTime") && !tag.containsNumber("MaxTime")) {
            logSkippedBlockEntityMigration(tag, "unexpected Furnace MaxTime tag type");
            return false;
        }

        if (!tag.contains("MaxTime") && !tag.containsShort("BurnDuration")) {
            logSkippedBlockEntityMigration(tag, "legacy Furnace has no convertible burn duration");
            return false;
        }

        if (tag.contains("StoredXpInt") && !tag.containsNumber("StoredXpInt")) {
            logSkippedBlockEntityMigration(tag, "unexpected Furnace StoredXpInt tag type");
            return false;
        }

        if (!validateStorageItemList(tag, "Items")) {
            return false;
        }

        boolean changed = migrateStorageItemList(tag, "Items", true);
        if (tag.containsNumber("MaxTime")) {
            tag.putShort("BurnDuration", tag.getInt("MaxTime"));
            tag.remove("MaxTime");
            changed = true;
        }

        if (tag.containsNumber("StoredXpInt")) {
            tag.putInt("StoredXPInt", tag.getInt("StoredXpInt"));
            tag.remove("StoredXpInt");
            changed = true;
        } else if (!tag.contains("StoredXPInt")) {
            tag.putInt("StoredXPInt", 0);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateMobSpawner(CompoundTag tag) {
        boolean legacy = tag.contains("EntityId")
                                || tag.contains("MinimumSpawnerCount")
                                || tag.contains("MaximumSpawnerCount");

        if (!legacy) return false;

        if (tag.contains("EntityIdentifier") && !tag.containsString("EntityIdentifier")) {
            logSkippedBlockEntityMigration(tag, "unexpected MobSpawner EntityIdentifier tag type");
            return false;
        }

        String identifier = tag.getString("EntityIdentifier");
        if (identifier.isEmpty() && tag.contains("EntityId") && !tag.containsNumber("EntityId")) {
            logSkippedBlockEntityMigration(tag, "unexpected MobSpawner EntityId tag type");
            return false;
        }

        if (identifier.isEmpty() && tag.containsNumber("EntityId")) {
            String resolved = Registries.ENTITY.getEntityIdentifier(tag.getInt("EntityId"));
            if (resolved != null) {
                identifier = resolved;
            }
        }

        tag.putString("EntityIdentifier", identifier);
        tag.putShort("Delay", 20);
        tag.putFloat("DisplayEntityHeight", 1.8f);
        tag.putFloat("DisplayEntityScale", 1.0f);
        tag.putFloat("DisplayEntityWidth", 0.8f);
        tag.putShort("SpawnCount", tag.containsShort("MaximumSpawnerCount") ? tag.getShort("MaximumSpawnerCount") : 4);
        tag.remove("EntityId", "MinimumSpawnerCount", "MaximumSpawnerCount", "SpawnData");
        return true;
    }

    private static boolean migrateMusic(CompoundTag tag) {
        if (tag.contains("note") && !tag.containsByte("note")) {
            logSkippedBlockEntityMigration(tag, "unexpected Music note tag type");
            return false;
        }

        if (tag.contains("powered") && !tag.containsByte("powered")) {
            logSkippedBlockEntityMigration(tag, "unexpected Music powered tag type");
            return false;
        }

        boolean changed = false;
        if (!tag.contains("note")) {
            tag.putByte("note", 0);
            changed = true;
        }

        if (tag.contains("powered")) {
            tag.remove("powered");
            changed = true;
        }

        return changed;
    }

    private static boolean migratePotentSulfur(CompoundTag tag) {
        if (tag.contains("countdown") && !tag.containsInt("countdown")) {
            logSkippedBlockEntityMigration(tag, "unexpected PotentSulfur countdown tag type");
            return false;
        }

        if (tag.contains("eruptionTick") && !(tag.get("eruptionTick") instanceof LongTag)) {
            logSkippedBlockEntityMigration(tag, "unexpected PotentSulfur eruptionTick tag type");
            return false;
        }

        tag.putString("id", "PotentSulfurBlock");
        tag.remove("countdown", "eruptionTick");
        return true;
    }

    private static boolean migrateCauldron(CompoundTag tag) {
        if (!tag.containsShort("PotionType")
                && tag.contains("SplashPotion")
                && !tag.containsNumber("SplashPotion")) {
            logSkippedBlockEntityMigration(tag, "unexpected Cauldron SplashPotion tag type");
            return false;
        }

        if (tag.contains("Items")) {
            if (!tag.containsList("Items") || !tag.getList("Items").getAll().isEmpty()) {
                logSkippedBlockEntityMigration(tag, "unexpected non-empty Cauldron Items tag");
                return false;
            }
        }

        boolean changed = false;
        if (!tag.containsShort("PotionId")) {
            tag.putShort("PotionId", -1);
            changed = true;
        }

        if (!tag.containsShort("PotionType")
                && tag.containsNumber("SplashPotion")
                && tag.getBoolean("SplashPotion")) {
            tag.putShort("PotionType", 1);
            changed = true;
        } else if (!tag.containsShort("PotionType")) {
            tag.putShort("PotionType", (tag.getShort("PotionId") & 0xffff) == 0xffff ? -1 : 0);
            changed = true;
        }

        if (tag.contains("SplashPotion")) {
            tag.remove("SplashPotion");
            changed = true;
        }

        if (!tag.contains("Items")) {
            tag.putList("Items", new ListTag<>());
            changed = true;
        }

        return changed;
    }

    private static boolean migrateLectern(CompoundTag tag) {
        if (!tag.contains("book")) return false;

        if (!tag.containsCompound("book")) {
            logSkippedBlockEntityMigration(tag, "unexpected Lectern book tag type");
            return false;
        }

        CompoundTag book = tag.getCompound("book");
        if (!validateStorageItem(tag, book, "book")) {
            return false;
        }

        boolean changed = migrateStorageItem(book);
        if (!tag.containsByte("hasBook") || !tag.getBoolean("hasBook")) {
            tag.putByte("hasBook", 1);
            changed = true;
        }

        if (!tag.containsInt("page")) {
            tag.putInt("page", 0);
            changed = true;
        }

        if (!tag.containsInt("totalPages")) {
            int totalPages = 0;
            if (book.containsCompound("tag")) {
                CompoundTag bookData = book.getCompound("tag");
                if (bookData.containsList("pages")) {
                    totalPages = bookData.getList("pages").size();
                }
            }

            tag.putInt("totalPages", totalPages);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateItemFrame(CompoundTag tag) {
        if (tag.contains("ItemRotation")
                && !tag.containsByte("ItemRotation")
                && !tag.containsFloat("ItemRotation")) {
            logSkippedBlockEntityMigration(tag, "unexpected ItemFrame ItemRotation tag type");
            return false;
        }

        if (tag.contains("Item") && !tag.containsCompound("Item")) {
            logSkippedBlockEntityMigration(tag, "unexpected ItemFrame Item tag type");
            return false;
        }

        boolean changed = false;
        if (tag.containsCompound("Item")) {
            CompoundTag item = tag.getCompound("Item");
            if (!validateStorageItem(tag, item, "Item")) {
                return false;
            }

            changed |= migrateStorageItem(item);
        }

        if (tag.containsByte("ItemRotation")) {
            int rotation = tag.getByte("ItemRotation") & 0xff;
            tag.putFloat("ItemRotation", rotation * 45.0f);
            changed = true;
        }

        if (!tag.containsFloat("ItemDropChance")) {
            tag.putFloat("ItemDropChance", 1.0f);
            changed = true;
        }

        return changed;
    }

    private static boolean migratePistonArm(CompoundTag tag) {
        boolean changed = false;

        if (!tag.contains("BreakBlocks")) {
            tag.putList("BreakBlocks", new ListTag<>(Tag.TAG_Int));
            changed = true;
        }

        if (tag.contains("powered")) {
            tag.remove("powered");
            changed = true;
        }

        if (tag.contains("hasPendingPower")) {
            tag.remove("hasPendingPower");
            changed = true;
        }

        if (tag.contains("pendingPowered")) {
            tag.remove("pendingPowered");
            changed = true;
        }

        if (tag.contains("facing")) {
            tag.remove("facing");
            changed = true;
        }

        if (tag.contains("Extending")) {
            tag.remove("Extending");
            changed = true;
        }

        return changed;
    }

    private static boolean migrateMovingBlock(
            CompoundTag tag,
            LegacyStorageMigrationContext migrationContext
    ) throws IOException {
        if (!tag.contains("movingEntity")) return false;

        if (!tag.containsCompound("movingEntity")) {
            logSkippedBlockEntityMigration(tag, "unexpected MovingBlock movingEntity tag type");
            return false;
        }

        CompoundTag movingEntity = tag.getCompound("movingEntity");
        String movingBlockIdentifier = tag.containsCompound("movingBlock")
                                                ? tag.getCompound("movingBlock").getString("name")
                                                : null;
        LegacyBlockEntityMigrationResult migrationResult =
                migrateLegacyBlockEntity(movingEntity, movingBlockIdentifier, migrationContext);
        boolean changed = migrationResult != LegacyBlockEntityMigrationResult.UNCHANGED;
        if (migrationResult == LegacyBlockEntityMigrationResult.REMOVE) {
            tag.remove("movingEntity");
            return true;
        }

        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");
        if (movingEntity.getInt("x") != x) {
            movingEntity.putInt("x", x);
            changed = true;
        }

        if (movingEntity.getInt("y") != y) {
            movingEntity.putInt("y", y);
            changed = true;
        }

        if (movingEntity.getInt("z") != z) {
            movingEntity.putInt("z", z);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateVibrationListener(CompoundTag tag, String blockEntityId) {
        if (tag.contains("VibrationListener") && !tag.containsCompound("VibrationListener")) {
            logSkippedBlockEntityMigration(tag, "unexpected " + blockEntityId + " VibrationListener tag type");
            return false;
        }

        if (tag.containsCompound("VibrationListener")) {
            CompoundTag listener = tag.getCompound("VibrationListener");
            if (listener.getAllTags().size() == 1 && listener.containsCompound("selector")
                    && listener.getCompound("selector").getAllTags().isEmpty()) {
                return false;
            }
        }

        tag.putCompound("VibrationListener", new CompoundTag().putCompound("selector", new CompoundTag()));
        return true;
    }

    private static boolean migrateBrushableBlock(CompoundTag tag, String type) {
        if (!"minecraft:suspicious_sand".equals(type) && !"minecraft:suspicious_gravel".equals(type)) {
            logSkippedBlockEntityMigration(tag, "BrushableBlock is not backed by suspicious sand or suspicious gravel");
            return false;
        }

        boolean changed = false;
        if (tag.contains("item")) {
            if (!tag.containsCompound("item")) {
                logSkippedBlockEntityMigration(tag, "unexpected BrushableBlock item tag type");
                return false;
            }

            CompoundTag item = tag.getCompound("item");
            if (isDiscardableEmptyStorageItem(item)) {
                tag.remove("item");
                changed = true;
            } else {
                changed |= migrateStorageItem(item);
            }
        }

        if (!tag.containsString("type") || !type.equals(tag.getString("type"))) {
            tag.putString("type", type);
            changed = true;
        }

        if (!tag.containsInt("brush_count")) {
            tag.putInt("brush_count", 0);
            changed = true;
        }

        if (!tag.containsByte("brush_direction")) {
            tag.putByte("brush_direction", 6);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateCommandBlock(CompoundTag tag) {
        boolean changed = false;
        if (tag.contains("conditionalMode")) {
            tag.remove("conditionalMode");
            changed = true;
        }

        if (!tag.containsString("Command")) {
            tag.putString("Command", "");
            changed = true;
        }

        if (!tag.containsString("CustomName")) {
            tag.putString("CustomName", "");
            changed = true;
        }

        if (!tag.containsByte("ExecuteOnFirstTick")) {
            tag.putByte("ExecuteOnFirstTick", 0);
            changed = true;
        }

        if (!tag.containsInt("LPCommandMode")) {
            tag.putInt("LPCommandMode", 0);
            changed = true;
        }

        if (!tag.containsByte("LPCondionalMode")) {
            tag.putByte("LPCondionalMode", 0);
            changed = true;
        }

        if (!tag.containsByte("LPRedstoneMode")) {
            tag.putByte("LPRedstoneMode", 0);
            changed = true;
        }

        if (!(tag.get("LastExecution") instanceof LongTag)) {
            tag.putLong("LastExecution", 0L);
            changed = true;
        }

        if (!tag.containsString("LastOutput")) {
            tag.putString("LastOutput", "");
            changed = true;
        }

        if (!tag.containsList("LastOutputParams")) {
            tag.putList("LastOutputParams", new ListTag<>());
            changed = true;
        }

        if (!tag.containsInt("SuccessCount")) {
            tag.putInt("SuccessCount", 0);
            changed = true;
        }

        if (!tag.containsInt("TickDelay")) {
            tag.putInt("TickDelay", 0);
            changed = true;
        }

        if (!tag.containsByte("TrackOutput")) {
            tag.putByte("TrackOutput", 1);
            changed = true;
        }

        if (!tag.containsInt("Version") || tag.getInt("Version") != 50) {
            tag.putInt("Version", 50);
            changed = true;
        }

        if (!tag.containsByte("auto")) {
            tag.putByte("auto", 0);
            changed = true;
        }

        if (!tag.containsByte("conditionMet")) {
            tag.putByte("conditionMet", 0);
            changed = true;
        }

        if (!tag.containsByte("powered")) {
            tag.putByte("powered", 0);
            changed = true;
        }

        return changed;
    }

    private static boolean migrateEnchantTable(CompoundTag tag) {
        if (tag.containsFloat("rott")) return false;

        tag.putFloat("rott", 0.0f);
        return true;
    }

    private static boolean migrateSign(CompoundTag tag) {
        boolean changed = false;
        String legacyText = "";
        if (tag.containsString("Text")) {
            legacyText = tag.getString("Text");
        } else if (tag.containsString("Text1")
                || tag.containsString("Text2")
                || tag.containsString("Text3")
                || tag.containsString("Text4")) {
            legacyText =
                    tag.getString("Text1")
                            + "\n"
                            + tag.getString("Text2")
                            + "\n"
                            + tag.getString("Text3")
                            + "\n"
                            + tag.getString("Text4");
        }

        if (!tag.containsCompound("FrontText")) {
            tag.putCompound("FrontText", createCanonicalSignText(legacyText));
            changed = true;
        } else {
            changed |= migrateSignText(tag.getCompound("FrontText"), tag, true);
        }

        if (!tag.containsCompound("BackText")) {
            tag.putCompound("BackText", createCanonicalSignText(""));
            changed = true;
        } else {
            changed |= migrateSignText(tag.getCompound("BackText"), tag, false);
        }

        if (!tag.containsByte("IsWaxed")) {
            tag.putByte("IsWaxed", 0);
            changed = true;
        }

        if (tag.contains("Text")
                || tag.contains("Text1")
                || tag.contains("Text2")
                || tag.contains("Text3")
                || tag.contains("Text4")
                || tag.contains("IgnoreLighting")
                || tag.contains("SignTextColor")
                || tag.contains("Creator")
                || tag.contains("TextIgnoreLegacyBugResolved")
                || tag.contains("LockedForEditingBy")) {
            tag.remove(
                    "Text",
                    "Text1",
                    "Text2",
                    "Text3",
                    "Text4",
                    "IgnoreLighting",
                    "SignTextColor",
                    "Creator",
                    "TextIgnoreLegacyBugResolved",
                    "LockedForEditingBy");
            changed = true;
        }

        return changed;
    }

    private static boolean migrateSignText(CompoundTag text, CompoundTag root, boolean front) {
        boolean changed = false;
        if (!text.containsString("Text")) {
            text.putString("Text", "");
            changed = true;
        } else if ("\n\n\n".equals(text.getString("Text"))) {
            text.putString("Text", "");
            changed = true;
        }

        if (!text.containsString("FilteredText")) {
            text.putString("FilteredText", "");
            changed = true;
        }

        if (!text.containsByte("HideGlowOutline")) {
            text.putByte("HideGlowOutline", 0);
            changed = true;
        }

        if (!text.containsByte("IgnoreLighting")) {
            text.putByte(
                    "IgnoreLighting",
                    front && root.containsByte("IgnoreLighting")
                            ? root.getByte("IgnoreLighting")
                            : 0);
            changed = true;
        }

        if (!text.containsByte("PersistFormatting")) {
            text.putByte("PersistFormatting", 1);
            changed = true;
        }

        if (!text.containsInt("SignTextColor")) {
            text.putInt(
                    "SignTextColor",
                    front && root.containsInt("SignTextColor")
                            ? root.getInt("SignTextColor")
                            : -16777216);
            changed = true;
        }

        if (!text.containsString("TextOwner")) {
            text.putString("TextOwner", "");
            changed = true;
        }

        return changed;
    }

    private static CompoundTag createCanonicalSignText(String text) {
        if ("\n\n\n".equals(text)) {
            text = "";
        }

        return new CompoundTag()
                .putString("FilteredText", "")
                .putByte("HideGlowOutline", 0)
                .putByte("IgnoreLighting", 0)
                .putByte("PersistFormatting", 1)
                .putInt("SignTextColor", -16777216)
                .putString("Text", text == null ? "" : text)
                .putString("TextOwner", "");
    }

    private static boolean migrateSkull(CompoundTag tag) {
        boolean changed = false;
        if (!tag.containsFloat("Rotation")) {
            float rotation = tag.containsByte("Rot") ? (tag.getByte("Rot") & 0x0f) * 22.5f : 0.0f;
            tag.putFloat("Rotation", rotation);
            changed = true;
        }

        if (!tag.containsByte("DoingAnimation")) {
            tag.putByte(
                    "DoingAnimation",
                    tag.containsByte("MouthMoving") && tag.getBoolean("MouthMoving") ? 1 : 0);
            changed = true;
        }

        if (!tag.containsInt("MouthTickCount")) {
            tag.putInt("MouthTickCount", 0);
            changed = true;
        }

        if (!tag.containsByte("SkullType")) {
            tag.putByte("SkullType", 0);
            changed = true;
        }

        if (tag.contains("Rot") || tag.contains("MouthMoving") || tag.contains("Creator")) {
            tag.remove("Rot", "MouthMoving", "Creator");
            changed = true;
        }

        return changed;
    }

    private static boolean validateStorageItemList(CompoundTag owner, String name) {
        if (!owner.contains(name)) return true;

        if (!owner.containsList(name, Tag.TAG_Compound)) {
            logSkippedBlockEntityMigration(owner, "unexpected " + name + " list type");
            return false;
        }

        ListTag<CompoundTag> items = owner.getList(name, CompoundTag.class);
        for (int i = 0; i < items.size(); i++) {
            if (!validateStorageItem(owner, items.get(i), name + "[" + i + "]")) {
                return false;
            }
        }

        return true;
    }

    private static boolean validateStorageItem(CompoundTag owner, CompoundTag item, String path) {
        if (!item.containsByte("Count")) {
            logSkippedBlockEntityMigration(owner, path + " has unexpected or missing Count type");
            return false;
        }

        if (!item.containsShort("Damage") && !item.containsByte("Damage")) {
            logSkippedBlockEntityMigration(owner, path + " has unexpected or missing Damage type");
            return false;
        }

        if (!item.containsString("Name")) {
            logSkippedBlockEntityMigration(owner, path + " has unexpected or missing Name type");
            return false;
        }

        if (item.contains("Slot") && !item.containsByte("Slot")) {
            logSkippedBlockEntityMigration(owner, path + " has unexpected Slot type");
            return false;
        }

        if (item.contains("WasPickedUp") && !item.containsByte("WasPickedUp")) {
            logSkippedBlockEntityMigration(owner, path + " has unexpected WasPickedUp type");
            return false;
        }

        if (item.contains("Block") && !item.containsCompound("Block")) {
            logSkippedBlockEntityMigration(owner, path + " has unexpected Block type");
            return false;
        }

        if (item.contains("tag") && !item.containsCompound("tag")) {
            logSkippedBlockEntityMigration(owner, path + " has unexpected item tag type");
            return false;
        }

        return true;
    }

    private static boolean migrateStorageItemList(CompoundTag owner, String name, boolean createIfMissing) {
        if (!owner.contains(name)) {
            if (!createIfMissing) return false;
            owner.putList(name, new ListTag<>(Tag.TAG_Compound));
            return true;
        }

        boolean changed = false;
        ListTag<CompoundTag> items = owner.getList(name, CompoundTag.class);
        for (CompoundTag item : items.getAll()) {
            changed |= migrateStorageItem(item);
        }

        return changed;
    }

    private static boolean migrateStorageItem(CompoundTag item) {
        boolean changed = false;
        if (item.containsByte("Damage")) {
            int damage = item.getByte("Damage") & 0xff;
            item.putShort("Damage", damage);
            changed = true;
        }

        if (!item.contains("WasPickedUp")) {
            item.putByte("WasPickedUp", 0);
            changed = true;
        }

        if (item.contains("version")) {
            item.remove("version");
            changed = true;
        }

        return changed;
    }

    private static boolean isDiscardableEmptyStorageItem(CompoundTag item) {
        if (!item.containsByte("Count") || !item.containsString("Name")) return false;
        if (item.getByte("Count") > 0) return false;

        String name = item.getString("Name");
        if (!name.isEmpty() && !"minecraft:air".equals(name)) {
            return false;
        }

        Set<String> allowedTags = Set.of("Count", "Damage", "Name", "WasPickedUp", "version", "Block");
        for (String key : item.getTags().keySet()) {
            if (!allowedTags.contains(key)) return false;
        }

        if (item.contains("Block")) {
            if (!item.containsCompound("Block")) return false;
            if (!"minecraft:air".equals(item.getCompound("Block").getString("name"))) return false;
        }

        return true;
    }

    private static CompoundTag createEmptyStorageItem() {
        return new CompoundTag()
                .putByte("Count", 0)
                .putShort("Damage", 0)
                .putString("Name", "")
                .putByte("WasPickedUp", 0);
    }

    private static void logSkippedBlockEntityMigration(CompoundTag tag, String reason) {
        log.warn(
                "[LevelDB Migration] Skipping block entity {} at {},{},{}: {}",
                tag.getString("id"),
                tag.getInt("x"),
                tag.getInt("y"),
                tag.getInt("z"),
                reason);
    }

    /**
     * Provides storage-dependent services required while converting legacy Block Entities.
     */
    public interface LegacyStorageMigrationContext {
        /**
         * Returns the current dimension ID when available.
         */
        default Integer getDimensionId() {
            return null;
        }

        /**
         * Allocates the next canonical ActorUniqueID when required.
         */
        default long nextActorUniqueId() {
            return 0L;
        }

        /**
         * Resolves a player UUID to its canonical ActorUniqueID.
         */
        default long resolvePlayerUniqueId(UUID uuid) throws IOException {
            throw new IOException("No player ActorUniqueID resolver is available");
        }

        /**
         * Returns the mutable PNX chunk extra-data destination when available.
         */
        default CompoundTag getPnxExtraData() {
            return null;
        }

        /**
         * Returns whether persisted storage contains an End Gateway near the supplied position.
         */
        default boolean hasPersistedEndGatewayNear(int blockX, int blockZ) throws IOException {
            return false;
        }

        /**
         * Returns whether legacy home data without dimension context should be discarded.
         */
        default boolean discardLegacyHomeWithoutDimension() {
            return false;
        }
    }

    /**
     * Identifies the result of migrating one legacy Block Entity.
     */
    public enum LegacyBlockEntityMigrationResult {
        UNCHANGED,
        CHANGED,
        REMOVE
    }

    /**
     * Carries one chunk's Block Entity state through the Block Entity migration step.
     */
    public record Data(
            List<CompoundTag> blockEntities,
            IChunk chunk,
            LegacyStorageMigrationContext migrationContext,
            boolean changed,
            Set<Position> netherPortalBlocks
    ) {
    }

    /**
     * Represents an absolute block position used during Block Entity migration and reconciliation.
     */
    public record Position(int x, int y, int z) {
    }
}
