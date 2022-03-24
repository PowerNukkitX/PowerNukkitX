package cn.nukkit.level.format.leveldb.util;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.format.leveldb.LDBChunk;
import cn.nukkit.level.format.leveldb.LDBChunkSection;
import cn.nukkit.level.format.leveldb.datas.*;
import cn.nukkit.level.format.leveldb.palette.IntPalette;
import cn.nukkit.level.format.leveldb.palette.Palette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import io.netty.buffer.*;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.nukkit.level.GameRule.*;

public final class LDBIO {
    public static Palette<BlockState> readPalette(BinaryStream stream) {
        var palette = new Palette<BlockState>();
        int paletteLength = stream.getLInt();
        for (int i = 0; i < paletteLength; i++) {
            var compound = stream.getLTag();
            palette.addEntry(LDBBlockUtils.nbt2BlockState(compound));
        }
        return palette;
    }

    public static Palette<BlockState> readPalette(ByteBuf buffer) throws IOException {
        var palette = new Palette<BlockState>();
        int paletteLength = buffer.readIntLE();
        for (int i = 0; i < paletteLength; i++) {
            var compound = (CompoundTag) NBTIO.readTag(new ByteBufInputStream(buffer), ByteOrder.LITTLE_ENDIAN, false);
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

    public static void writePalette(ByteBuf buffer, Palette<BlockState> palette) throws IOException {
        var entries = palette.getEntries();
        buffer.writeIntLE(entries.size());
        for (BlockState data : entries) {
            NBTIO.write(LDBBlockUtils.blockState2Nbt(data), new ByteBufOutputStream(buffer), ByteOrder.LITTLE_ENDIAN, false);
        }
    }

    public static LDBChunkSection readSubChunk(ByteBuf buffer) throws IOException {
        var subChunk = new LDBChunkSection(-1);
        int subChunkVersion = buffer.readByte();

        int amountOfLayers;
        switch (subChunkVersion) {
            case 9 -> {
                amountOfLayers = buffer.readByte();
                buffer.readByte();  // data driven heights
            }
            case 8 -> amountOfLayers = buffer.readByte();
            case 1 -> amountOfLayers = 1;
            default -> throw new ChunkException("Unknown sub chunk version: v" + subChunkVersion);
        }

        for (int layerI = 0; layerI < amountOfLayers; layerI++) {
            var layer = readLayer(buffer);
            subChunk.addLayer(layer);
        }

        return subChunk;
    }

    public static void writeSubChunk(ByteBuf buffer, LDBChunkSection subChunk) throws IOException {
        buffer.writeByte(8);
        buffer.writeByte(subChunk.getLayers().size());

        var layers = subChunk.getLayers();
        for (var layer : layers) {
            writeLayer(buffer, layer);
        }
    }

    public static LDBLayer readLayer(ByteBuf buffer) throws IOException {
        int bitsPerBlock = buffer.readByte() >> 1;
        int blocksPerWord = 32 / bitsPerBlock;
        int wordsPerChunk = (int) Math.ceil(4096d / blocksPerWord); // there are 4096 blocks in a chunk stored in x words

        // We want to read the palette first so that we can translate what blocks are immediately.
        int chunkBlocksIndex = buffer.readerIndex();
        buffer.setIndex(chunkBlocksIndex + (wordsPerChunk * 4), buffer.writerIndex());

        var palette = readPalette(buffer);
        int endPaletteIndex = buffer.readerIndex(); // we jump to this index after reading the blocks

        // Go back and parse the blocks.
        buffer.setIndex(chunkBlocksIndex, buffer.writerIndex());
        var layer = new LDBLayer(palette);

        int pos = 0;
        for (int chunk = 0; chunk < wordsPerChunk; chunk++) {
            int word = buffer.readIntLE();  // This integer can store multiple minecraft blocks.
            for (int block = 0; block < blocksPerWord; block++) {
                if (pos >= 4096) {
                    break;
                }

                int paletteIndex = (word >> (pos % blocksPerWord) * bitsPerBlock) & ((1 << bitsPerBlock) - 1);
                layer.setBlockEntryAt(pos >> 8, pos & 15, (pos >> 4) & 15, palette.getEntry(paletteIndex));

                pos++;
            }
        }

        // Go back to the end of the palette to prepare for the next layer
        buffer.setIndex(endPaletteIndex, buffer.writerIndex());

        return layer;
    }

    public static void writeLayer(ByteBuf buffer, LDBLayer layer) throws IOException {
        layer.resize();

        int bitsPerBlock = Math.max((int) Math.ceil(Math.log(layer.getPalette().getEntries().size()) / Math.log(2)), 1);
        int blocksPerWord = 32 / bitsPerBlock;
        int wordsPerChunk = (int) Math.ceil(4096d / blocksPerWord);

        buffer.writeByte((bitsPerBlock << 1) | 1);

        int pos = 0;
        for (int chunk = 0; chunk < wordsPerChunk; chunk++) {
            int word = 0;
            for (int block = 0; block < blocksPerWord; block++) {
                if (pos >= 4096) {
                    break;
                }

                word |= layer.getPalette().getPaletteIndex(layer.getBlockEntryAt(pos >> 8, pos & 15, (pos >> 4) & 15)) << (bitsPerBlock * block);
                pos++;
            }
            buffer.writeIntLE(word);
        }

        writePalette(buffer, layer.getPalette());
    }

    public static LDBChunkData read2DChunkData(byte[] data) {
        ByteBuf buffer = Unpooled.copiedBuffer(data);

        try {
            // Parse height map
            var heightMap = new LDBChunkHeightMap();
            for (int i = 0; i < 256; i++) {
                int z = i >> 4;
                int x = i & 15;
                heightMap.setHighestBlockAt(x, z, buffer.readUnsignedShortLE());
            }

            // Parse biome map
            var biomeMap = new LDBChunkBiomeMap();

            byte[] biomeData = new byte[256];
            buffer.readBytes(biomeData);

            // Begin constructing 3D biomes...
            var subChunkBiomeMaps = new LDBSubChunkBiomeMap[25];
            for (int i = 0; i < 25; i++) {
                subChunkBiomeMaps[i] = new LDBSubChunkBiomeMap(new IntPalette());
            }

            // Assign every value in a column to its biome.
            for (int i = 0; i < 256; i++) {
                int z = i >> 4;
                int x = i & 15;

                for (int subChunkIndex = 0; subChunkIndex < 25; subChunkIndex++) {
                    for (int y = 0; y < 16; y++) {
                        subChunkBiomeMaps[subChunkIndex].setBiomeAt(x, y, z, biomeData[i]);
                    }
                }
            }

            // construct the biome map.
            for (int i = 0; i < 25; i++) {
                biomeMap.setSubChunk(i, subChunkBiomeMaps[i]);
            }

            return new LDBChunkData(heightMap, biomeMap);
        } finally {
            buffer.release();
        }
    }

    public static LDBChunkData read3DChunkData(byte[] data) throws IOException {
        ByteBuf buffer = Unpooled.wrappedBuffer(data);
        buffer.readerIndex(0);

        try {
            // Parse height map
            var heightMap = new LDBChunkHeightMap();
            for (int i = 0; i < 256; i++) {
                int z = i >> 4;
                int x = i & 15;
                heightMap.setHighestBlockAt(x, z, buffer.readUnsignedShortLE());
            }

            // Parse biome map
            var biomeMap = new LDBChunkBiomeMap();
            LDBSubChunkBiomeMap lastBiomeSubChunk = null;

            int subChunkIndex = 0;
            while (buffer.readableBytes() > 0) {
                int bitsPerBlock = buffer.readByte() >> 1;

                // if the bits is -1, that means that we should just copy the last biome map.
                if (bitsPerBlock == -1) {
                    if (lastBiomeSubChunk == null) {
                        throw new ChunkException("Cannot use last biome subchunk if none exists.");
                    }
                    biomeMap.setSubChunk(subChunkIndex++, lastBiomeSubChunk.clone());
                    continue;
                }

                // because the palette is written after the data, we keep a mental note of where the biome data is
                // so that we can construct the palette first.
                int biomeDataIndex = 0;
                int paletteLength = 1;  // for biomes, we assume by default that there is at least 1 biome present

                if (bitsPerBlock > 0) {
                    int blocksPerWord = 32 / bitsPerBlock;
                    int wordsPerChunk = (int) Math.ceil(4096d / blocksPerWord);

                    biomeDataIndex = buffer.readerIndex();

                    buffer.skipBytes(wordsPerChunk * 4);
                    paletteLength = buffer.readIntLE();
                }

                // Parse biome palette
                var palette = new IntPalette();
                for (int i = 0; i < paletteLength; i++) {
                    palette.addEntry(buffer.readIntLE());
                }

                int endOfPaletteIndex = buffer.readerIndex();

                // Begin constructing the biome map for this subchunk
                var subChunkBiomeMap = new LDBSubChunkBiomeMap(palette);
                if (bitsPerBlock > 0) {
                    // Move our index back to the biome data before the palette
                    buffer.readerIndex(biomeDataIndex);

                    int blocksPerWord = 32 / bitsPerBlock;
                    int wordsPerChunk = (int) Math.ceil(4096d / blocksPerWord);

                    int pos = 0;
                    for (int i = 0; i < wordsPerChunk; i++) {
                        int word = buffer.readIntLE();  // stores multiple biomes in 1 int
                        for (int block = 0; block < blocksPerWord; block++) {
                            if (pos >= 4096) {
                                break;
                            }

                            // Break apart the word into coordinates for each block's biome in the subchunk
                            int paletteIndex = (word >> (pos % blocksPerWord) * bitsPerBlock) & ((1 << bitsPerBlock) - 1);
                            subChunkBiomeMap.setBiomeAt(pos >> 8, pos & 15, (pos >> 4) & 15, palette.getEntry(paletteIndex));

                            pos++;
                        }
                    }
                }

                buffer.readerIndex(endOfPaletteIndex);

                // Add the biome subchunk to our biome map.
                lastBiomeSubChunk = subChunkBiomeMap;
                biomeMap.setSubChunk(subChunkIndex++, lastBiomeSubChunk);
            }

            return new LDBChunkData(heightMap, biomeMap);
        } finally {
            buffer.release();
        }
    }

    public static byte[] heightMapToBytes(LDBChunkHeightMap heightMap) {
        byte[] data = new byte[512];
        ByteBuf buffer = Unpooled.wrappedBuffer(data);
        buffer.writerIndex(0);

        try {
            for (int height : heightMap.array()) {
                buffer.writeShortLE(height);
            }

            return data;
        } finally {
            buffer.release();
        }
    }

    public static byte[] biomeMapToBytes(LDBChunkBiomeMap biomeMap) throws IOException {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            LDBSubChunkBiomeMap lastSubChunkBiomeMap = null;
            for (var subChunkBiomeMap : biomeMap.getSubChunks()) {
                if (subChunkBiomeMap.getPalette().getEntries().size() == 0) {
                    throw new IOException("biome sub chunk has no biomes present");
                }

                var bitsPerBlock = (int) Math.ceil(Math.log(subChunkBiomeMap.getPalette().getEntries().size()) / Math.log(2));
                var blocksPerWord = 0;
                var wordsPerChunk = 0;

                if (subChunkBiomeMap.equals(lastSubChunkBiomeMap)) {
                    buffer.writeByte(-1);
                    continue;
                }

                if (bitsPerBlock > 0) {
                    blocksPerWord = 32 / bitsPerBlock;
                    wordsPerChunk = (int) Math.ceil(4096d / blocksPerWord);
                }

                buffer.writeByte((bitsPerBlock << 1) | 1);

                var pos = 0;
                for (var i = 0; i < wordsPerChunk; i++) {
                    var word = 0;
                    for (var block = 0; block < blocksPerWord; block++) {
                        if (pos >= 4096) {
                            break;
                        }

                        word |= subChunkBiomeMap.getPalette().getPaletteIndex(subChunkBiomeMap.getBiomeAt(pos >> 8, (pos >> 4) & 15, pos & 15)) << (bitsPerBlock * block);
                        pos++;
                    }
                    buffer.writeIntLE(word);
                }

                if (bitsPerBlock > 0) {
                    buffer.writeIntLE(subChunkBiomeMap.getPalette().getEntries().size());
                }
                for (int i = 0, len = subChunkBiomeMap.getPalette().getEntries().size(); i < len; i++) {
                    buffer.writeIntLE(subChunkBiomeMap.getPalette().getEntry(i));
                }

                lastSubChunkBiomeMap = subChunkBiomeMap;
            }

            var data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);

            return data;
        } finally {
            buffer.release();
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
        System.out.println("Start level data.");
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
            fileStream.write(outputStream.getBuffer());
            System.out.println(outputStream.getBuffer().length);
            fileStream.flush();
            System.out.println("Finished level data");
        }
    }
}
