package cn.nukkit.level.format.leveldb.util;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.format.leveldb.datas.LDBLevelData;
import cn.nukkit.level.format.leveldb.datas.LDBPlayerAbilities;
import cn.nukkit.level.format.leveldb.palette.Palette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BinaryStream;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.nukkit.level.GameRule.*;

public final class LDBIO {
    public static Palette<BlockState> readPalette(BinaryStream stream) {
        Palette<BlockState> palette = new Palette<>();
        int paletteLength = stream.getLInt();
        for (int i = 0; i < paletteLength; i++) {
            CompoundTag compound = stream.getLTag();
            palette.addEntry(LDBBlockUtils.nbt2BlockState(compound));
        }
        return palette;
    }

    public static void writePalette(BinaryStream stream, Palette<BlockState> palette) {
        Set<BlockState> entries = palette.getEntries();
        stream.putLInt(entries.size());
        for (BlockState data : entries) {
            stream.putLTag(LDBBlockUtils.blockState2Nbt(data));
        }
    }

    public static LDBLevelData readLevelData(File levelDataFile) throws IOException {
        var levelData = new LDBLevelData();
        try (InputStream levelDatStream = new FileInputStream(levelDataFile)) {
            //noinspection ResultOfMethodCallIgnored
            levelDatStream.skip(8); // useless header bytes

            CompoundTag compound = (CompoundTag) NBTIO.readTag(levelDatStream, ByteOrder.LITTLE_ENDIAN, false);

            levelData.setCommandsEnabled(compound.getBoolean("commandsEnabled"));
            levelData.setCurrentTick(compound.getLong("currentTick"));
            levelData.setHasBeenLoadedInCreative(compound.getBoolean("hasBeenLoadedInCreative"));
            levelData.setHasLockedResourcePack(compound.getBoolean("hasLockedResourcePack"));
            levelData.setHasLockedBehaviorPack(compound.getBoolean("hasLockedBehaviorPack"));
            levelData.setExperiments(compound.getCompound("experiments"));
            levelData.setForcedGamemode(compound.getBoolean("ForceGameType"));
            levelData.setImmutable(compound.getBoolean("immutableWorld"));
            levelData.setConfirmedPlatformLockedContent(compound.getBoolean("ConfirmedPlatformLockedContent"));
            levelData.setFromWorldTemplate(compound.getBoolean("isFromWorldTemplate"));
            levelData.setFromLockedTemplate(compound.getBoolean("isFromLockedTemplate"));
            levelData.setIsMultiplayerGame(compound.getBoolean("MultiplayerGame"));
            levelData.setIsSingleUseWorld(compound.getBoolean("isSingleUseWorld"));
            levelData.setIsWorldTemplateOptionsLocked(compound.getBoolean("isWorldTemplateOptionLocked"));
            levelData.setLanBroadcast(compound.getBoolean("LANBroadcast"));
            levelData.setLanBroadcastIntent(compound.getBoolean("LANBroadcastIntent"));
            levelData.setMultiplayerGameIntent(compound.getBoolean("MultiplayerGameIntent"));
            levelData.setPlatformBroadcastIntent(compound.getInt("PlatformBroadcastIntent"));
            levelData.setRequiresCopiedPackRemovalCheck(compound.getBoolean("requiresCopiedPackRemovalCheck"));
            levelData.setServerChunkTickRange(compound.getInt("serverChunkTickRange"));
            levelData.setSpawnOnlyV1Villagers(compound.getBoolean("SpawnV1Villagers"));
            levelData.setStorageVersion(compound.getInt("StorageVersion"));
            levelData.setTexturePacksRequired(compound.getBoolean("texturePacksRequired"));
            levelData.setUseMsaGamerTagsOnly(compound.getBoolean("useMsaGamertagsOnly"));
            levelData.setName(compound.getString("LevelName"));
            levelData.setWorldStartCount(compound.getLong("worldStartCount"));
            levelData.setXboxLiveBroadcastIntent(compound.getInt("XBLBroadcastIntent"));
            levelData.setEduOffer(compound.getInt("eduOffer"));
            levelData.setEduEnabled(compound.getBoolean("educationFeaturesEnabled"));
            levelData.setBiomeOverride(compound.getString("BiomeOverride"));
            levelData.setBonusChestEnabled(compound.getBoolean("bonusChestEnabled"));
            levelData.setBonusChestSpawned(compound.getBoolean("bonusChestSpawned"));
            levelData.setCenterMapsToOrigin(compound.getBoolean("CenterMapsToOrigin"));
            levelData.setDefaultGamemode(compound.getInt("GameType"));
            levelData.setDifficulty(compound.getInt("Difficulty"));
            levelData.setFlatWorldLayers(compound.getString("FlatWorldLayers"));
            levelData.setLightningLevel(compound.getFloat("lightningLevel"));
            levelData.setLightningTime(compound.getInt("lightningTime"));
            levelData.setLimitedWorldCoordinates(new Vector3(
                    compound.getInt("LimitedWorldOriginX"),
                    compound.getInt("LimitedWorldOriginY"),
                    compound.getInt("LimitedWorldOriginZ")));
            levelData.setLimitedWorldWidth(compound.getInt("limitedWorldWidth"));
            levelData.setNetherScale(compound.getInt("NetherScale"));
            levelData.setRainLevel(compound.getFloat("rainLevel"));
            levelData.setRainTime(compound.getInt("rainTime"));
            levelData.setSeed(compound.getLong("RandomSeed"));
            levelData.setWorldSpawn(new Vector3(
                    compound.getInt("SpawnX"),
                    compound.getInt("SpawnY"),
                    compound.getInt("SpawnZ")
            ));
            levelData.setStartWithMapEnabled(compound.getBoolean("startWithMapEnabled"));
            levelData.setTime(compound.getLong("Time"));
            levelData.setWorldType(compound.getInt("Generator"));
            levelData.setBaseGameVersion(compound.getString("baseGameVersion"));
            levelData.setInventoryVersion(compound.getString("InventoryVersion"));
            levelData.setLastPlayed(compound.getLong("LastPlayed"));
            levelData.setMinimumCompatibleClientVersion(compound.getList("MinimumCompatibleClientVersion").getAll().stream().mapToInt(value -> ((IntTag) value).getData()).toArray());
            levelData.setLastOpenedWithVersion(compound.getList("lastOpenedWithVersion").getAll().stream().mapToInt(value -> ((IntTag) value).getData()).toArray());
            levelData.setPlatform(compound.getInt("Platform"));
            levelData.setProtocol(compound.getInt("NetworkVersion"));
            levelData.setPrid(compound.getString("prid"));

            var abilities = compound.getCompound("abilities");
            levelData.setPlayerAbilities(
                    new LDBPlayerAbilities()
                            .setCanAttackMobs(abilities.getBoolean("attackmobs"))
                            .setCanAttackPlayers(abilities.getBoolean("attackplayers"))
                            .setCanBuild(abilities.getBoolean("build"))
                            .setCanFly(abilities.getBoolean("mayfly"))
                            .setCanInstaBuild(abilities.getBoolean("instabuild"))
                            .setCanMine(abilities.getBoolean("mine"))
                            .setCanOpenContainers(abilities.getBoolean("opencontainers"))
                            .setCanTeleport(abilities.getBoolean("teleport"))
                            .setCanUseDoorsAndSwitches(abilities.getBoolean("doorsandswitches"))
                            .setFlySpeed(abilities.getFloat("flySpeed"))
                            .setIsFlying(abilities.getBoolean("flying"))
                            .setIsInvulnerable(abilities.getBoolean("invulnerable"))
                            .setIsOp(abilities.getBoolean("op"))
                            .setIsLightning(abilities.getBoolean("lightning"))
                            .setPermissionsLevel(abilities.getInt("permissionsLevel"))
                            .setPlayerPermissionsLevel(abilities.getInt("playerPermissionsLevel"))
                            .setWalkSpeed(abilities.getFloat("walkSpeed"))
            );

            GameRules gameRules = GameRules.getDefault();
            gameRules.setGameRule(COMMAND_BLOCK_OUTPUT, compound.getBoolean("commandblockoutput"));
            gameRules.setGameRule(COMMAND_BLOCKS_ENABLED, compound.getBoolean("commandblocksenabled"));
            gameRules.setGameRule(DO_DAYLIGHT_CYCLE, compound.getBoolean("dodaylightcycle"));
            gameRules.setGameRule(DO_ENTITY_DROPS, compound.getBoolean("doentitydrops"));
            gameRules.setGameRule(DO_FIRE_TICK, compound.getBoolean("dofiretick"));
            gameRules.setGameRule(DO_IMMEDIATE_RESPAWN, compound.getBoolean("doimmediaterespawn"));
            gameRules.setGameRule(DO_INSOMNIA, compound.getBoolean("doinsomnia"));
            gameRules.setGameRule(DO_MOB_LOOT, compound.getBoolean("domobloot"));
            gameRules.setGameRule(DO_MOB_SPAWNING, compound.getBoolean("domobspawning"));
            gameRules.setGameRule(DO_TILE_DROPS, compound.getBoolean("dotiledrops"));
            gameRules.setGameRule(DO_WEATHER_CYCLE, compound.getBoolean("doweathercycle"));
            gameRules.setGameRule(DROWNING_DAMAGE, compound.getBoolean("drowningdamage"));
            gameRules.setGameRule(FALL_DAMAGE, compound.getBoolean("falldamage"));
            gameRules.setGameRule(FIRE_DAMAGE, compound.getBoolean("firedamage"));
            gameRules.setGameRule(KEEP_INVENTORY, compound.getBoolean("keepinventory"));
            gameRules.setGameRule(MAX_COMMAND_CHAIN_LENGTH, compound.getInt("maxcommandchainlength"));
            gameRules.setGameRule(MOB_GRIEFING, compound.getBoolean("mobgriefing"));
            gameRules.setGameRule(NATURAL_REGENERATION, compound.getBoolean("naturalregeneration"));
            gameRules.setGameRule(PVP, compound.getBoolean("pvp"));
            gameRules.setGameRule(RANDOM_TICK_SPEED, compound.getInt("randomtickspeed"));
            gameRules.setGameRule(SEND_COMMAND_FEEDBACK, compound.getBoolean("sendcommandfeedback"));
            gameRules.setGameRule(SHOW_COORDINATES, compound.getBoolean("showcoordinates"));
            gameRules.setGameRule(SHOW_DEATH_MESSAGES, compound.getBoolean("showdeathmessages"));
            gameRules.setGameRule(SHOW_TAGS, compound.getBoolean("showtags"));
            gameRules.setGameRule(SPAWN_RADIUS, compound.getInt("spawnradius"));
            gameRules.setGameRule(TNT_EXPLODES, compound.getBoolean("tntexplodes"));
            levelData.setGameRules(gameRules);
        }

        return levelData;
    }

    public static void writeLevelData(File levelDatFile, LDBLevelData data) throws IOException {
        try (var fileStream = new FileOutputStream(levelDatFile);
             var payloadStream = new ByteArrayOutputStream()) {

            var outputStream = new BinaryStream();

            var payload = new CompoundTag()
                    .putBoolean("commandsEnabled", data.isCommandsEnabled())
                    .putLong("currentTick", data.getCurrentTick())
                    .putBoolean("hasBeenLoadedInCreative", data.hasBeenLoadedInCreative())
                    .putBoolean("hasLockedResourcePack", data.hasLockedResourcePack())
                    .putBoolean("hasLockedBehaviorPack", data.hasLockedBehaviorPack())
                    .putCompound("experiments", data.getExperiments())
                    .putBoolean("ForceGameType", data.isForcedGamemode())
                    .putBoolean("immutableWorld", data.isImmutable())
                    .putBoolean("ConfirmedPlatformLockedContent", data.isConfirmedPlatformLockedContent())
                    .putBoolean("isFromWorldTemplate", data.isFromWorldTemplate())
                    .putBoolean("isFromLockedTemplate", data.isFromLockedTemplate())
                    .putBoolean("MultiplayerGame", data.isMultiplayerGame())
                    .putBoolean("isSingleUseWorld", data.isSingleUseWorld())
                    .putBoolean("isWorldTemplateOptionLocked", data.isWorldTemplateOptionsLocked())
                    .putBoolean("LANBroadcast", data.isLanBroadcast())
                    .putBoolean("LANBroadcastIntent", data.isLanBroadcastIntent())
                    .putBoolean("MultiplayerGameIntent", data.isMultiplayerGameIntent())
                    .putInt("PlatformBroadcastIntent", data.getPlatformBroadcastIntent())
                    .putBoolean("requiresCopiedPackRemovalCheck", data.requiresCopiedPackRemovalCheck())
                    .putInt("serverChunkTickRange", data.getServerChunkTickRange())
                    .putBoolean("SpawnV1Villagers", data.spawnOnlyV1Villagers())
                    .putInt("StorageVersion", data.getStorageVersion())
                    .putBoolean("texturePacksRequired", data.isTexturePacksRequired())
                    .putBoolean("useMsaGamertagsOnly", data.useMsaGamerTagsOnly())
                    .putString("LevelName", data.getName())
                    .putLong("worldStartCount", data.getWorldStartCount())
                    .putInt("XBLBroadcastIntent", data.getXboxLiveBroadcastIntent())
                    .putInt("eduOffer", data.getEduOffer())
                    .putBoolean("educationFeaturesEnabled", data.isEduEnabled())
                    .putString("BiomeOverride", data.getBiomeOverride())
                    .putBoolean("bonusChestEnabled", data.isBonusChestEnabled())
                    .putBoolean("bonusChestSpawned", data.isBonusChestSpawned())
                    .putBoolean("CenterMapsToOrigin", data.isCenterMapsToOrigin())
                    .putInt("GameType", data.getDefaultGamemode())
                    .putInt("Difficulty", data.getDifficulty())
                    .putString("FlatWorldLayers", data.getFlatWorldLayers())
                    .putFloat("lightningLevel", data.getLightningLevel())
                    .putInt("lightningTime", data.getLightningTime())
                    .putInt("LimitedWorldOriginX", (int) data.getLimitedWorldCoordinates().getX())
                    .putInt("LimitedWorldOriginY", (int) data.getLimitedWorldCoordinates().getY())
                    .putInt("LimitedWorldOriginZ", (int) data.getLimitedWorldCoordinates().getZ())
                    .putInt("limitedWorldWidth", data.getLimitedWorldWidth())
                    .putInt("NetherScale", data.getNetherScale())
                    .putFloat("rainLevel", data.getRainLevel())
                    .putInt("rainTime", data.getRainTime())
                    .putLong("RandomSeed", data.getSeed())
                    .putInt("SpawnX", (int) data.getWorldSpawn().getX())
                    .putInt("SpawnY", (int) data.getWorldSpawn().getY())
                    .putInt("SpawnZ", (int) data.getWorldSpawn().getZ())
                    .putBoolean("startWithMapEnabled", data.startWithMapEnabled())
                    .putLong("Time", data.getTime())
                    .putInt("Generator", data.getWorldType())
                    .putString("baseGameVersion", data.getBaseGameVersion())
                    .putString("InventoryVersion", data.getInventoryVersion())
                    .putLong("LastPlayed", data.getLastPlayed())
                    .putList(new ListTag<IntTag>("MinimumCompatibleClientVersion").addAll(Arrays.stream(data.getMinimumCompatibleClientVersion()).mapToObj(value -> new IntTag("", value)).collect(Collectors.toList())))
                    .putList(new ListTag<IntTag>("lastOpenedWithVersion").addAll(Arrays.stream(data.getMinimumCompatibleClientVersion()).mapToObj(value -> new IntTag("", value)).collect(Collectors.toList())))
                    .putInt("Platform", data.getPlatform())
                    .putInt("NetworkVersion", data.getProtocol())
                    .putString("prid", data.getPrid())
                    .putCompound("abilities", new CompoundTag()
                            .putBoolean("attackmobs", data.getPlayerAbilities().canAttackMobs())
                            .putBoolean("attackplayers", data.getPlayerAbilities().canAttackPlayers())
                            .putBoolean("build", data.getPlayerAbilities().canBuild())
                            .putBoolean("mayfly", data.getPlayerAbilities().canFly())
                            .putBoolean("instabuild", data.getPlayerAbilities().canInstaBuild())
                            .putBoolean("mine", data.getPlayerAbilities().canMine())
                            .putBoolean("opencontainers", data.getPlayerAbilities().canOpenContainers())
                            .putBoolean("teleport", data.getPlayerAbilities().canTeleport())
                            .putBoolean("doorsandswitches", data.getPlayerAbilities().canUseDoorsAndSwitches())
                            .putFloat("flySpeed", data.getPlayerAbilities().getFlySpeed())
                            .putBoolean("flying", data.getPlayerAbilities().isFlying())
                            .putBoolean("invulnerable", data.getPlayerAbilities().isInvulnerable())
                            .putBoolean("op", data.getPlayerAbilities().isOp())
                            .putBoolean("lightning", data.getPlayerAbilities().isLightning())
                            .putInt("permissionsLevel", data.getPlayerAbilities().getPermissionsLevel())
                            .putInt("playerPermissionsLevel", data.getPlayerAbilities().getPlayerPermissionsLevel())
                            .putFloat("walkSpeed", data.getPlayerAbilities().getWalkSpeed()))
                    .putBoolean("commandblockoutput", data.getGameRules().getBoolean(COMMAND_BLOCK_OUTPUT))
                    .putBoolean("commandblocksenabled", data.getGameRules().getBoolean(COMMAND_BLOCKS_ENABLED))
                    .putBoolean("dodaylightcycle", data.getGameRules().getBoolean(DO_DAYLIGHT_CYCLE))
                    .putBoolean("doentitydrops", data.getGameRules().getBoolean(DO_ENTITY_DROPS))
                    .putBoolean("dofiretick", data.getGameRules().getBoolean(DO_FIRE_TICK))
                    .putBoolean("doimmediaterespawn", data.getGameRules().getBoolean(DO_IMMEDIATE_RESPAWN))
                    .putBoolean("doinsomnia", data.getGameRules().getBoolean(DO_INSOMNIA))
                    .putBoolean("domobloot", data.getGameRules().getBoolean(DO_MOB_LOOT))
                    .putBoolean("domobspawning", data.getGameRules().getBoolean(DO_MOB_SPAWNING))
                    .putBoolean("dotiledrops", data.getGameRules().getBoolean(DO_TILE_DROPS))
                    .putBoolean("doweathercycle", data.getGameRules().getBoolean(DO_WEATHER_CYCLE))
                    .putBoolean("drowningdamage", data.getGameRules().getBoolean(DROWNING_DAMAGE))
                    .putBoolean("falldamage", data.getGameRules().getBoolean(FALL_DAMAGE))
                    .putBoolean("firedamage", data.getGameRules().getBoolean(FIRE_DAMAGE))
                    .putBoolean("keepinventory", data.getGameRules().getBoolean(KEEP_INVENTORY))
                    .putInt("maxcommandchainlength", data.getGameRules().getInteger(MAX_COMMAND_CHAIN_LENGTH))
                    .putBoolean("mobgriefing", data.getGameRules().getBoolean(MOB_GRIEFING))
                    .putBoolean("naturalregeneration", data.getGameRules().getBoolean(NATURAL_REGENERATION))
                    .putBoolean("pvp", data.getGameRules().getBoolean(PVP))
                    .putInt("randomtickspeed", data.getGameRules().getInteger(RANDOM_TICK_SPEED))
                    .putBoolean("sendcommandfeedback", data.getGameRules().getBoolean(SEND_COMMAND_FEEDBACK))
                    .putBoolean("showcoordinates", data.getGameRules().getBoolean(SHOW_COORDINATES))
                    .putBoolean("showdeathmessages", data.getGameRules().getBoolean(SHOW_DEATH_MESSAGES))
                    .putBoolean("showtags", data.getGameRules().getBoolean(SHOW_TAGS))
                    .putInt("spawnradius", data.getGameRules().getInteger(SPAWN_RADIUS))
                    .putBoolean("tntexplodes", data.getGameRules().getBoolean(TNT_EXPLODES));

            NBTIO.write(payload, payloadStream, ByteOrder.LITTLE_ENDIAN, false);
            outputStream.putLInt(3);
            outputStream.putLInt(payloadStream.size());
            outputStream.put(payloadStream.toByteArray());

            fileStream.flush();
        }
    }
}
